package it.unical.mat.map_generator;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


import it.unical.mat.asp_classes.Assignment;
import it.unical.mat.asp_classes.Cell;
import it.unical.mat.asp_classes.CellManager;
import it.unical.mat.asp_classes.Connected8;
import it.unical.mat.asp_classes.EmbASPManager;
import it.unical.mat.asp_classes.NewDoor;
import it.unical.mat.asp_classes.ObjectAssignment;
import it.unical.mat.asp_classes.ObjectGenerator;
import it.unical.mat.asp_classes.Pair;
import it.unical.mat.asp_classes.Partition;
import it.unical.mat.asp_classes.RoomBuilder;
import it.unical.mat.debug.DebugPrinter;
import it.unical.mat.embasp.base.Handler;
import it.unical.mat.embasp.base.InputProgram;
import it.unical.mat.embasp.base.Output;
import it.unical.mat.embasp.languages.IllegalAnnotationException;
import it.unical.mat.embasp.languages.ObjectNotValidException;
import it.unical.mat.embasp.languages.asp.ASPInputProgram;
import it.unical.mat.embasp.languages.asp.AnswerSet;
import it.unical.mat.embasp.languages.asp.AnswerSets;
import it.unical.mat.util.Configuration;
import it.unical.mat.util.ConfigurationFile;
import it.unical.mat.util.FilePrinter;

public class MapGenerator {
    
    private static boolean IS_DEBUG_MODE;
    private static DebugPrinter DEBUGGER;
    private Lock debug_lock;
    /////////////////////// CLASS FIELDS //////////////////////////
    private final int maxColumns;
    private final int maxRows;
    private final int minDistanceWall;
    private final int randomAnswersetNumber;
    private int numEmptyPartitions;
    private int numPartitionsToBuild;
    private final CellManager matrixCells;
    private final String encodingFolder;
    private final Random randomGenerator;
    private final String[] orientation = { "vertical", "horizontal" };
    private List<Partition> partitions;
    private List<Connected8> connections;
    private FilePrinter levelPrinter;
    private Configuration configuration;
    private String game;
    ///////////////////////////////////////////////////////////////
    
    public MapGenerator( ConfigurationFile configurationFile ) {
    	configurationFile.getLock().lock();
    	this.configuration = new Configuration( configurationFile.getFile_path());
        this.maxRows = configuration.getRowSize();
        this.maxColumns = configuration.getColumnSize();
        this.minDistanceWall = configuration.getMinDistanceWall();
        this.randomAnswersetNumber = configuration.getRandomAnswersetNumber();
        this.numEmptyPartitions = 0;
        this.numPartitionsToBuild = 0;
        this.encodingFolder = configuration.getEncodingFolder();
        MapGenerator.IS_DEBUG_MODE = configuration.isDebugMode();
        if (configuration.getRandomSeed() >= 0)
            this.randomGenerator = new Random(configuration.getRandomSeed());
        else
            this.randomGenerator = new Random();
        this.game = configuration.getGame();
        configurationFile.getLock().unlock();
        
        this.matrixCells = new CellManager();
        this.partitions = new ArrayList<>();
        this.connections = new ArrayList<>();
        this.debug_lock = new ReentrantLock();
        this.levelPrinter = new FilePrinter("level_map/level_map");
        if (IS_DEBUG_MODE)
            MapGenerator.DEBUGGER = new DebugPrinter();
    }
    
    public MapGenerator(int width, int height, int min_distance_wall, int random_answerset_number,
            String encodingFolder, int randomSeed,ConfigurationFile configurationFile) {
        super();
        configurationFile.getLock().lock();
        this.maxColumns = width;
        this.maxRows = height;
        this.minDistanceWall = min_distance_wall;
        this.randomAnswersetNumber = random_answerset_number;
        this.encodingFolder = encodingFolder + File.separator;
        this.matrixCells = new CellManager();
        this.levelPrinter = new FilePrinter("level_map/level_map");
        if (randomSeed >= 0)
            this.randomGenerator = new Random(randomSeed);
        else
            this.randomGenerator = new Random();
        configurationFile.getLock().unlock();
        
        if (IS_DEBUG_MODE)
            MapGenerator.DEBUGGER = new DebugPrinter();
    }
    
    private NewDoor answerSetToCellMatrix(AnswerSets answers, Partition oldRoom)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {
        List<AnswerSet> answerSetsList = answers.getAnswersets();
        
        NewDoor door = null;
        
        if (answerSetsList.size() > 0) {
            int index = randomGenerator.nextInt(answerSetsList.size());
            AnswerSet a = answerSetsList.get(index);
            
            if (IS_DEBUG_MODE)
                DEBUGGER.printOnFile("\n\nOUTPUT PROGRAM: \n");
            
            for (Object obj : a.getAtoms()) {
                if (IS_DEBUG_MODE)
                    DEBUGGER.printOnFile(obj.toString());
                
                if (obj instanceof Cell)
                    matrixCells.addCell((Cell) obj);
                else if (obj instanceof NewDoor)
                    door = (NewDoor) obj;
            }
            
            if (IS_DEBUG_MODE)
                DEBUGGER.printOnFile("\n");
        }
        return door;
        
    }
    
    public void buildWalls( Partition partition ) throws Exception {
    	
    	 long imperative_part_start = 0;
    	 long imperative_part_end = 0;
    	 long declarative_part_start = 0;
    	 long declarative_part_end = 0;
    	
    	 if( IS_DEBUG_MODE ) {
    		 debug_lock.lock();
    		 imperative_part_start = System.currentTimeMillis();
    	 }
    	
    	 EmbASPManager controller = new EmbASPManager();
         controller.initializeEmbASP(randomAnswersetNumber);
         
         InputProgram input = controller.getInput();
         Handler handler = controller.getHandler();
         
         switch (partition.getType()) {
		 	case "\"hollow\"":
		 		input.addFilesPath(encodingFolder + "5-generate_room.asp");
		 	break;
		 	case "\"corridor\"":
		 		input.addFilesPath(encodingFolder + "5-generate_corridor.asp");
		 	break;
		 	case "\"empty\"":
		 		input.addFilesPath(encodingFolder + "5-generate_empty.asp");
		 	break;
		 	default:
			break;
		}
         
         handler.addProgram(input);
         
         InputProgram facts = new ASPInputProgram();
         
         if (IS_DEBUG_MODE) {
             DEBUGGER.printOnFile("INPUT PROGRAM: \n");
             DEBUGGER.printOnFile("min_row(" + partition.getMinRow() + ").\n");
             DEBUGGER.printOnFile("max_row(" + partition.getMaxRow() + ").\n");
             DEBUGGER.printOnFile("min_col(" + partition.getMinCol() + ").\n");
             DEBUGGER.printOnFile("max_col(" + partition.getMaxCol() + ").\n");
             DEBUGGER.printOnFile("col(" + partition.getMinCol() + ".." + partition.getMaxCol() + ").");
             DEBUGGER.printOnFile("row(" + partition.getMinRow() + ".." + partition.getMaxRow() + ").");
         }
         
         facts.addProgram("col(" + partition.getMinCol() + ".." + partition.getMaxCol() + ").");
         facts.addProgram("row(" + partition.getMinRow() + ".." + partition.getMaxRow() + ").");
         facts.addProgram("min_row(" + partition.getMinRow() + ").");
         facts.addProgram("max_row(" + partition.getMaxRow() + ").");
         facts.addProgram("min_col(" + partition.getMinCol() + ").");
         facts.addProgram("max_col(" + partition.getMaxCol() + ").");
         
         for (Pair door_coordinate : partition.getDoors()) {
        	 if(partition.getType().equals("\"empty\"")) {
        		 matrixCells.getCells().get(door_coordinate).setType("wall");
        	 }
        	 else {
        		 Cell door = matrixCells.getCells().get(door_coordinate);
        		 if (IS_DEBUG_MODE)
                     DEBUGGER.printOnFile("cell(" + door.getRow() + "," + door.getColumn() + ",\"" + door.getType() + "\").");       
        		 facts.addProgram("cell(" + door.getRow() + "," + door.getColumn() + ",\"" + door.getType() + "\").");
        	 }
         }
         handler.addProgram(facts);
         
         if( IS_DEBUG_MODE )
        	 declarative_part_start = System.currentTimeMillis();
         
         Output o = handler.startSync();
         AnswerSets answers = (AnswerSets) o;
         
         if( IS_DEBUG_MODE )
        	 declarative_part_end = System.currentTimeMillis();
         
         answerSetToCellMatrix(answers, null);
         
         if( IS_DEBUG_MODE ) {
         	
        	imperative_part_end = System.currentTimeMillis();
         	
        	printMatrixDebug();
            DEBUGGER.printOnFile("buildWalls( partition_size: " + partition.getSize() + " min_row: " + partition.getMinRow() + " min_col: " 
            				+ partition.getMinCol() + " max_row: " + partition.getMaxCol() + " max_col: " + partition.getMaxCol() + 
            				" type: " + partition.getType() +" )\n");
            computeTime(imperative_part_start, declarative_part_start, imperative_part_end, declarative_part_end);        
            debug_lock.unlock();
         }
    }
    
    public void generateFloor( ) throws ObjectNotValidException, IllegalAnnotationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException,NullPointerException {
    	try {
    		long startTime = System.currentTimeMillis();
    		List<RoomBuilder> buildersEmpty = new ArrayList<>();
    		List<RoomBuilder> buildersRoom = new ArrayList<>();
    		CyclicBarrier emptyBarrier = new CyclicBarrier(numEmptyPartitions+1);
    		CyclicBarrier toBuildBarrier = new CyclicBarrier(numPartitionsToBuild+1);
    		for( int i = 0; i < partitions.size() ; i++ ) {
    			RoomBuilder roomBuilder;
    			if( partitions.get(i).getType().equals("\"empty\"")) {
    				roomBuilder = new RoomBuilder(this, partitions.get(i),emptyBarrier);
    				buildersEmpty.add(roomBuilder);
    			}
    			else {
    				roomBuilder = new RoomBuilder(this, partitions.get(i),toBuildBarrier);
    				buildersRoom.add(roomBuilder);
    			}
    		}
    		
    		for ( RoomBuilder roomBuilder : buildersEmpty )
    			roomBuilder.start();
    		emptyBarrier.await();
    		
    		for ( RoomBuilder roomBuilder : buildersRoom )
    			roomBuilder.start();
    		toBuildBarrier.await();
    		
    		long stopTime = System.currentTimeMillis();
            long elapsedTime = stopTime - startTime;
            if( IS_DEBUG_MODE ) {
            	DEBUGGER.printOnFile("\n\n\n");
            	DEBUGGER.printOnFile(String.format("Elapsed Time Declarative Generate Floor : %d min, %d sec, %d ms\n\n\n",
                         TimeUnit.MILLISECONDS.toMinutes(elapsedTime), TimeUnit.MILLISECONDS.toSeconds(elapsedTime)
                                 - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(elapsedTime)), elapsedTime));
            }
    		
    	} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BrokenBarrierException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void generateMap() {
        try {
            long startTime = System.currentTimeMillis();
            setWallOnBorder();
            spacePartitioning(true, new Partition(1, 1, maxRows, maxColumns));
            generatePartitionGraph();
            partitioningTypeAssignment();
            generateFloor();
            partitionObjectTypeAssignment();
            generateRoomObject();
            printLevel();
            
            if (IS_DEBUG_MODE)
                printMatrixDebug();
            System.out.println(this.toString());
            
            long stopTime = System.currentTimeMillis();
            long elapsedTime = stopTime - startTime;
            System.out.println(String.format("Elapsed Time: %d min, %d sec",
                    TimeUnit.MILLISECONDS.toMinutes(elapsedTime), TimeUnit.MILLISECONDS.toSeconds(elapsedTime)
                            - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(elapsedTime))));
            if(IS_DEBUG_MODE)
            	DEBUGGER.printOnFile(String.format("Elapsed Time: %d min, %d sec \n",
                    TimeUnit.MILLISECONDS.toMinutes(elapsedTime), TimeUnit.MILLISECONDS.toSeconds(elapsedTime)
                            - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(elapsedTime))));
            
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }

	private void partitionObjectTypeAssignment() throws Exception {
		long imperative_part_start = System.currentTimeMillis();
    	
        EmbASPManager controller = new EmbASPManager();
        controller.initializeEmbASP(randomAnswersetNumber);
        
        InputProgram input = controller.getInput();
        Handler handler = controller.getHandler();
        
        input.addFilesPath(encodingFolder + "6-partition_object_type_assignment.asp");
        handler.addProgram(input);
        
        InputProgram facts = new ASPInputProgram();
        
        facts.addProgram("type(\"avatar\").");
        facts.addProgram("type(\"enemy\").");
        facts.addProgram("type(\"goal\").");
        facts.addProgram("type(\"key\").");
        
        if (IS_DEBUG_MODE) {
            DEBUGGER.printOnFile("INPUT PROGRAM: \n");
            DEBUGGER.printOnFile("type(\"avatar\").");
            DEBUGGER.printOnFile("type(\"goal\").");
            DEBUGGER.printOnFile("type(\"enemy\").");
            DEBUGGER.printOnFile("type(\"key\").");
        }
        
        for (Connected8 connected8 : connections) {
			Partition partition1 = new Partition(connected8.getMinRow1(),connected8.getMinCol1(), connected8.getMaxRow1(),connected8.getMaxCol1());
			Partition partition2 = new Partition(connected8.getMinRow2(),connected8.getMinCol2(), connected8.getMaxRow2(),connected8.getMaxCol2());
			facts.addProgram("connected(p(" + partition1.getMinRow() + "," + partition1.getMinCol() + "," + partition1.getMaxRow() + "," 
					+ partition1.getMaxCol()+ "),p(" + partition2.getMinRow() + "," + partition2.getMinCol() + "," + 
					partition2.getMaxRow() + "," + partition2.getMaxCol() +")).");
			if (IS_DEBUG_MODE)
				DEBUGGER.printOnFile("connected(p(" + partition1.getMinRow() + "," + partition1.getMinCol() + "," + partition1.getMaxRow() + "," 
							+ partition1.getMaxCol()+ "),p(" + partition2.getMinRow() + "," + partition2.getMinCol() + "," + 
							partition2.getMaxRow() + "," + partition2.getMaxCol() +")).");
        }
        
        for (Partition partition : partitions) {
        	facts.addProgram("assignment(p(" + partition.getMinRow() + "," + partition.getMinCol() + "," + partition.getMaxRow() + "," 
					+ partition.getMaxCol()+ ")" + "," + partition.getType() + ").");
        	if( IS_DEBUG_MODE )
        		DEBUGGER.printOnFile("assignment(p(" + partition.getMinRow() + "," + partition.getMinCol() + "," + partition.getMaxRow() + "," 
						+ partition.getMaxCol()+ ")" + "," + partition.getType() + ").");
        }
        
        if (IS_DEBUG_MODE)
            DEBUGGER.printOnFile("\n");
        
        handler.addProgram(facts);
        
        long declarative_part_start = System.currentTimeMillis();
        
        Output o = handler.startSync();
        AnswerSets answers = (AnswerSets) o;
       
        long declarative_part_end = System.currentTimeMillis();
        
        List<AnswerSet> answerSetsList = answers.getAnswersets();
        
        if (answerSetsList.size() > 0) {
            int index = randomGenerator.nextInt(answerSetsList.size());
            AnswerSet a = answerSetsList.get(index);
            	
            if (IS_DEBUG_MODE)
                DEBUGGER.printOnFile("\n\nOUTPUT PROGRAM: \n");
            
            for (Object obj : a.getAtoms()) {
            	if (IS_DEBUG_MODE)
                    DEBUGGER.printOnFile(obj.toString());
                if( obj instanceof ObjectAssignment) {
                	ObjectAssignment objectAssignment = (ObjectAssignment)obj;
                	
                	Partition partition = new Partition(objectAssignment.getMinRow(),objectAssignment.getMinCol(),objectAssignment.getMaxRow(),objectAssignment.getMaxCol());
                	int partition_index = partitions.indexOf(partition);
                	
                	partitions.get(partition_index).setType(objectAssignment.getType());
                }
            }
        }

        long imperative_part_end = System.currentTimeMillis();
        
        if( IS_DEBUG_MODE ) {
            DEBUGGER.printOnFile("\n\npartitionObjectTypeAssignment()\n");
            computeTime(imperative_part_start, declarative_part_start, imperative_part_end, declarative_part_end);        
        }
	}
	
	public void generateOBject(Partition partition) throws Exception {
		
		long imperative_part_start = 0;
		long imperative_part_end = 0;
		long declarative_part_start = 0;
   	 	long declarative_part_end = 0;
   	
   	 	if( IS_DEBUG_MODE ) {
   	 		debug_lock.lock();
   	 		imperative_part_start = System.currentTimeMillis();
   	 	}
   	
   	 	EmbASPManager controller = new EmbASPManager();
   	 	controller.initializeEmbASP(randomAnswersetNumber);
        
        InputProgram input = controller.getInput();
        Handler handler = controller.getHandler();
       
        handler.addProgram(input);
        
        InputProgram facts = new ASPInputProgram();
        
        input.addFilesPath(encodingFolder + "7-object_type_assignment.asp");
              
        switch (partition.getType()) {
		case "\"avatar\"":
			facts.addProgram("type(\"avatar\").");
		break;
		case "\"goal\"":
			if( game.equals("zelda"))
				facts.addProgram("type(\"goal\").");
			else 
				facts.addProgram("type(\"exit\").");
		break;
		case "\"key\"":
			if( game.equals("zelda"))
				facts.addProgram("type(\"key\").");
			else 
				return;
		break;
		case "\"enemy\"":
			if( game.equals("zelda"))
				facts.addProgram("type(\"enemy\").");
			else 
				facts.addProgram("type(\"trap\").");
		break;
		default:
			return;
		}
        
        for( int i = partition.getMinRow(); i < partition.getMaxRow(); i++ )
        	for( int j = partition.getMinCol(); j < partition.getMaxCol(); j++ ){
        		Cell cell = matrixCells.getCells().get(new Pair<Integer, Integer>(i, j));
        		if(matrixCells.getCells().containsKey(new Pair<Integer, Integer>(i, j)))
        			facts.addProgram("cell(" + i + "," + j + ",\"" + cell.getType()+ "\").");
        	}
        
        facts.addProgram("min_row(" + partition.getMinRow() + ").\n");
        facts.addProgram("max_row(" + partition.getMaxRow() + ").\n");
        facts.addProgram("min_col(" + partition.getMinCol() + ").\n");
        facts.addProgram("max_col(" + partition.getMaxCol() + ").\n");
        facts.addProgram("col(" + partition.getMinCol() + ".." + partition.getMaxCol() + ").");
        facts.addProgram("row(" + partition.getMinRow() + ".." + partition.getMaxRow() + ").");
        
        handler.addProgram(facts);
        
        if( IS_DEBUG_MODE )
       	 declarative_part_start = System.currentTimeMillis();
        
        Output o = handler.startSync();
        AnswerSets answers = (AnswerSets) o;
        
        if( IS_DEBUG_MODE )
       	 declarative_part_end = System.currentTimeMillis();
        answerSetToCellMatrix(answers, null);
        
        if( IS_DEBUG_MODE ) {
        	
       	imperative_part_end = System.currentTimeMillis();
        	
       	printMatrixDebug();
           DEBUGGER.printOnFile("generateOBject( partition_size: " + partition.getSize() + " min_row: " + partition.getMinRow() + " min_col: " 
           				+ partition.getMinCol() + " max_row: " + partition.getMaxCol() + " max_col: " + partition.getMaxCol() + 
           				" type: " + partition.getType() +" )\n");
           computeTime(imperative_part_start, declarative_part_start, imperative_part_end, declarative_part_end);        
           debug_lock.unlock();
        }
	}
	
	private void generateRoomObject() throws InterruptedException, BrokenBarrierException {
		CyclicBarrier objectToGenerateBarrier = new CyclicBarrier(numPartitionsToBuild+1);
		
		for( int i = 0; i < partitions.size() ; i++ ) {
			ObjectGenerator objectGenerator;
			if( !partitions.get(i).getType().equals("\"empty\"")) {
				objectGenerator = new ObjectGenerator(this, partitions.get(i),objectToGenerateBarrier);
				objectGenerator.start();
			}
		}
		
		objectToGenerateBarrier.await();		
	}

	private void generatePartitionGraph() throws Exception {
        
    	long imperative_part_start = System.currentTimeMillis();
    	
        EmbASPManager controller = new EmbASPManager();
        controller.initializeEmbASP(randomAnswersetNumber);
        
        InputProgram input = controller.getInput();
        Handler handler = controller.getHandler();
        
        input.addFilesPath(encodingFolder + "3-partition_graph_generator.asp");
        handler.addProgram(input);
        
        InputProgram facts = new ASPInputProgram();
        
        facts.addProgram("col(1.." + maxColumns + ").");
        facts.addProgram("row(1.." + maxRows + ").");
        
        if (IS_DEBUG_MODE)
            DEBUGGER.printOnFile("ALL CELLS: \n");
        
        for (Cell cell : matrixCells.getSetCells()) {
            if (IS_DEBUG_MODE)
                DEBUGGER.printOnFile(cell.toString());
            facts.addObjectInput(cell);
        }
        
        if (IS_DEBUG_MODE)
            DEBUGGER.printOnFile("\n");
        
        handler.addProgram(facts);
        
        long declarative_part_start = System.currentTimeMillis();
        
        Output o = handler.startSync();
        AnswerSets answers = (AnswerSets) o;
       
        long declarative_part_end = System.currentTimeMillis();
        
        List<AnswerSet> answerSetsList = answers.getAnswersets();
        
        if (answerSetsList.size() > 0) {
            int index = randomGenerator.nextInt(answerSetsList.size());
            AnswerSet a = answerSetsList.get(index);
            if (IS_DEBUG_MODE)
                DEBUGGER.printOnFile("\n\nOUTPUT PROGRAM: \n");
            
            for (Object obj : a.getAtoms()) {
            	if (IS_DEBUG_MODE)
                    DEBUGGER.printOnFile(obj.toString());
                if( obj instanceof Connected8) {
                	Connected8 connected8 = (Connected8)obj;
                	connections.add(connected8); 
                }
            }
            
            if (IS_DEBUG_MODE)
                DEBUGGER.printOnFile("\n");
        }

        long imperative_part_end = System.currentTimeMillis();
        
        if( IS_DEBUG_MODE ) {
            DEBUGGER.printOnFile("GeneratePartitionGraph()\n");
            computeTime(imperative_part_start, declarative_part_start, imperative_part_end, declarative_part_end);        
        }
    }
    
    private void partitioningTypeAssignment() throws Exception {
    	
    	long imperative_part_start = System.currentTimeMillis();
    	
    	EmbASPManager controller = new EmbASPManager();
        controller.initializeEmbASP(randomAnswersetNumber);
        
        InputProgram input = controller.getInput();
        Handler handler = controller.getHandler();
        
        input.addFilesPath(encodingFolder + "4-partition_type_assignment.asp");
        handler.addProgram(input);
        
        InputProgram facts = new ASPInputProgram();
        
        facts.addProgram("num_partitions("+partitions.size()+").");
        facts.addProgram("empty_percentage_range(10,20).");
        facts.addProgram("type(\"hollow\").");
        facts.addProgram("type(\"empty\").");
        facts.addProgram("type(\"corridor\").");
        
        int start_partition_index = randomGenerator.nextInt(partitions.size());
        Partition start_partition = partitions.get(start_partition_index);
        facts.addProgram("start_partition(p(" + start_partition.getMinRow() + "," + start_partition.getMinCol() + "," + 
        				 + start_partition.getMaxRow() + "," + start_partition.getMaxCol() +")).");
        
        if (IS_DEBUG_MODE) {
            DEBUGGER.printOnFile("INPUT PROGRAM: \n");
        	DEBUGGER.printOnFile("num_partitions("+partitions.size()+").");
            DEBUGGER.printOnFile("type(\"hollow\").");
            DEBUGGER.printOnFile("type(\"empty\").");
            DEBUGGER.printOnFile("type(\"corridor\").");
            DEBUGGER.printOnFile("empty_percentage_range(10,20).");
            DEBUGGER.printOnFile("start_partition(p(" + start_partition.getMinRow() + "," + start_partition.getMinCol() + "," + 
   				 + start_partition.getMaxRow() + "," + start_partition.getMaxCol() +")).");
        }
        
        for (Partition partition : partitions ) { 
        	facts.addProgram("partition(p(" + partition.getMinRow() +"," + partition.getMinCol() + "," + partition.getMaxRow() + "," + partition.getMaxCol() + ")).");
        	if (IS_DEBUG_MODE)
				DEBUGGER.printOnFile("partition(p(" + partition.getMinRow() +"," + partition.getMinCol() + "," + partition.getMaxRow() + "," + partition.getMaxCol() + ")).");
        }
        
        for (Connected8 connected8 : connections) {
			Partition partition1 = new Partition(connected8.getMinRow1(),connected8.getMinCol1(), connected8.getMaxRow1(),connected8.getMaxCol1());
			Partition partition2 = new Partition(connected8.getMinRow2(),connected8.getMinCol2(), connected8.getMaxRow2(),connected8.getMaxCol2());
			facts.addProgram("connected(p(" + partition1.getMinRow() + "," + partition1.getMinCol() + "," + partition1.getMaxRow() + "," 
							+ partition1.getMaxCol()+ "),p(" + partition2.getMinRow() + "," + partition2.getMinCol() + "," + 
							partition2.getMaxRow() + "," + partition2.getMaxCol() +")).");
			if (IS_DEBUG_MODE)
				DEBUGGER.printOnFile("connected(p(" + partition1.getMinRow() + "," + partition1.getMinCol() + "," + partition1.getMaxRow() + "," 
							+ partition1.getMaxCol()+ "),p(" + partition2.getMinRow() + "," + partition2.getMinCol() + "," + 
							partition2.getMaxRow() + "," + partition2.getMaxCol() +")).");
        }

        handler.addProgram(facts);
        
        long declarative_part_start = System.currentTimeMillis();
        
        Output o = handler.startSync();
        AnswerSets answers = (AnswerSets) o;
       
        long declarative_part_end = System.currentTimeMillis();
        
        List<AnswerSet> answerSetsList = answers.getAnswersets();
        
        if (answerSetsList.size() > 0) {
            int index = randomGenerator.nextInt(answerSetsList.size());
            AnswerSet a = answerSetsList.get(index);
        
            if (IS_DEBUG_MODE)
                DEBUGGER.printOnFile("\n\nOUTPUT PROGRAM: \n");
            
            for (Object obj : a.getAtoms()) {
            	if (IS_DEBUG_MODE)
                    DEBUGGER.printOnFile(obj.toString());
                if( obj instanceof Assignment) {
                	Assignment assignment = (Assignment)obj;
                	Partition partition = new Partition(assignment.getMinRow(),assignment.getMinCol(),assignment.getMaxRow(),assignment.getMaxCol());
                	int partition_index = partitions.indexOf(partition);
                	partitions.get(partition_index).setType(assignment.getType());
                	if( assignment.getType().equals("\"empty\""))
                		numEmptyPartitions++;
                	else
                		numPartitionsToBuild++;	
                }
            }
        }
        
        long imperative_part_end = System.currentTimeMillis();
        
        if( IS_DEBUG_MODE ) {
        	DEBUGGER.printOnFile("\n\n\n");
            DEBUGGER.printOnFile("partitioningTypeAssignment( num_partitions: " + partitions.size() + " )\n");
            computeTime(imperative_part_start, declarative_part_start, imperative_part_end, declarative_part_end);          
        }
     }
    
    private void printMatrixDebug() {
    	DEBUGGER.printOnFile("\n\n");
        for (int i = 1; i <= maxRows; i++) {
            for (int j = 1; j <= maxColumns; j++)
                if (matrixCells.getCells().containsKey(new Pair<Integer, Integer>(i, j)))
                    DEBUGGER.printOnFile(matrixCells.getCells().get(new Pair<Integer, Integer>(i, j)).getGVGAI() + " ");
                else
                    DEBUGGER.printOnFile("- ");
            DEBUGGER.printOnFile("\n");
        }
        
        DEBUGGER.printOnFile("\nMapGenerator [width=" + maxColumns + ", height=" + maxRows + ", min_distance_wall="
                + minDistanceWall + ", random_answerset_number=" + randomAnswersetNumber + "]\n\n\n\n");
    }
    
    private void printLevel() {
        for (int i = 1; i <= maxRows; i++) {
            for (int j = 1; j <= maxColumns; j++)
                if (matrixCells.getCells().containsKey(new Pair<Integer, Integer>(i, j))) {
                	Cell cell = matrixCells.getCells().get(new Pair<Integer, Integer>(i, j));
                	if(!cell.getType().equals("hdoor") && !cell.getType().equals("vdoor"))
                		levelPrinter.printOnFile(matrixCells.getCells().get(new Pair<Integer, Integer>(i, j)).getGVGAI());
                	else levelPrinter.printOnFile(".");
                }
                else
                    levelPrinter.printOnFile(".");
            levelPrinter.printOnFile("\n");
        }
    }
    
    private void setWallOnBorder() throws Exception {
        
    	long imperative_part_start = System.currentTimeMillis();
    	
        EmbASPManager controller = new EmbASPManager();
        controller.initializeEmbASP();
        
        InputProgram input = controller.getInput();
        Handler handler = controller.getHandler();
        
        input.addFilesPath(encodingFolder + "1-set_wall_on_border.asp");
        handler.addProgram(input);
        
        if (IS_DEBUG_MODE) {
            DEBUGGER.printOnFile("INPUT PROGRAM: \n");
            DEBUGGER.printOnFile("col(1.." + maxColumns + ").");
            DEBUGGER.printOnFile("row(1.." + maxRows + ").");
            DEBUGGER.printOnFile("max_col(" + maxColumns + ").");
            DEBUGGER.printOnFile("max_row(" + maxRows + ").");
        }
        
        InputProgram facts = new ASPInputProgram();
        facts.addProgram("col(1.." + maxColumns + ").");
        facts.addProgram("row(1.." + maxRows + ").");
        facts.addProgram("max_col(" + maxColumns + ").");
        facts.addProgram("max_row(" + maxRows + ").");
        
        handler.addProgram(facts);
        
        long declarative_part_start = System.currentTimeMillis();
        
        Output o = handler.startSync();
        
        AnswerSets answers = (AnswerSets) o;
        
        long declarative_part_end =  System.currentTimeMillis();
        
        answerSetToCellMatrix(answers, null);
        
        long imperative_part_end = System.currentTimeMillis();
        
        if (IS_DEBUG_MODE)
            printMatrixDebug();
        
        if (IS_DEBUG_MODE) { 
        	DEBUGGER.printOnFile("setWallOnBorder( col: 1.." + maxColumns + " row: 1.." + maxRows + " max_col: " + maxColumns 
            		+ " max_row: " + maxRows + " )\n");
            DEBUGGER.printOnFile("MapGenerator [wid1h=" + maxColumns + ", height=" + maxRows + ", min_distance_wall=" + minDistanceWall
       			+ ", random_answerset_number=" + randomAnswersetNumber + "]\n");
            computeTime(imperative_part_start, declarative_part_start, imperative_part_end, declarative_part_end);  
        }
    }
    
    public void spacePartitioning(boolean horizontal, Partition nextPartitioned) throws Exception {
    		
    	long imperative_part_start = System.currentTimeMillis();
    	
        if (nextPartitioned.getSize() < configuration.getMinRoomSize()) {
        	addPartition(nextPartitioned);
        	return;
        }
        
        double pruningPercentage = configuration.getPruningPercentage();
        double sameOrientationPercentage = configuration.getSameOrientationPercentage();
        
        double rNumber = randomGenerator.nextDouble();
//        if (rNumber < pruningPercentage * (1 - nextPartitioned.getSize() / mapSize)) {
//        	addPartition(nextPartitioned);
//        	return;
//        }
        
        if (rNumber < sameOrientationPercentage)
            horizontal = !horizontal;
        
        EmbASPManager controller = new EmbASPManager();
        controller.initializeEmbASP(randomAnswersetNumber);
        
        InputProgram input = controller.getInput();
        Handler handler = controller.getHandler();
        
        input.addFilesPath(encodingFolder + "2-space_partitioning.asp");
        // input.addFilesPath(encodingFolder
        // + (horizontal ? "2-space_partitioning_horizontal.asp" :
        // "2-space_partitioning_vertical.asp"));
        handler.addProgram(input);
        
        InputProgram facts = new ASPInputProgram();
        
        int orientationIndex = (horizontal) ? 1 : 0;
        
        if (IS_DEBUG_MODE) {
            DEBUGGER.printOnFile("INPUT PROGRAM: \n");
            DEBUGGER.printOnFile(nextPartitioned.toString() + "\n");
            DEBUGGER.printOnFile("row(" + nextPartitioned.getMinRow() + ".." + nextPartitioned.getMaxRow() + ").\n");
            DEBUGGER.printOnFile("col(" + nextPartitioned.getMinCol() + ".." + nextPartitioned.getMaxCol() + ").\n");
            DEBUGGER.printOnFile("max_row(" + nextPartitioned.getMaxRow() + ").\n");
            DEBUGGER.printOnFile("max_col(" + nextPartitioned.getMaxCol() + ").\n");
            DEBUGGER.printOnFile("min_row(" + nextPartitioned.getMinRow() + ").\n");
            DEBUGGER.printOnFile("min_col(" + nextPartitioned.getMinCol() + ").\n");
            DEBUGGER.printOnFile("min_distance_wall(" + minDistanceWall + ").\n");
            DEBUGGER.printOnFile("orientation(" + orientation[orientationIndex] + ").\n");
        }
        
        // System.out.println("NEXT ROOM: " + nextPartitioned.toString());
        // facts.addObjectsInput((Set)matrixCells.getSetCellsType("vdoor"));
        // facts.addObjectsInput((Set)matrixCells.getSetCellsType("hdoor"));
        facts.addProgram("row(" + nextPartitioned.getMinRow() + ".." + nextPartitioned.getMaxRow() + ").");
        facts.addProgram("col(" + nextPartitioned.getMinCol() + ".." + nextPartitioned.getMaxCol() + ").");
        facts.addProgram("max_row(" + nextPartitioned.getMaxRow() + ").");
        facts.addProgram("max_col(" + nextPartitioned.getMaxCol() + ").");
        facts.addProgram("min_row(" + nextPartitioned.getMinRow() + ").");
        facts.addProgram("min_col(" + nextPartitioned.getMinCol() + ").");
        facts.addProgram("min_distance_wall(" + minDistanceWall + ").");
        facts.addProgram("orientation(" + orientation[orientationIndex] + ").");
        
        for (Cell cell : matrixCells.getSetCells())
            if (cell.getRow() >= nextPartitioned.getMinRow() && cell.getRow() <= nextPartitioned.getMaxRow()
                    && cell.getColumn() >= nextPartitioned.getMinCol()
                    && cell.getColumn() <= nextPartitioned.getMaxCol()) {
                if (IS_DEBUG_MODE)
                    DEBUGGER.printOnFile(cell.toString());
                facts.addObjectInput(cell);
            }
        if (IS_DEBUG_MODE)
            DEBUGGER.printOnFile("\n");
        
        handler.addProgram(facts);
        
        long declarative_part_start = System.currentTimeMillis();
        
        Output o = handler.startSync();
        AnswerSets answers = (AnswerSets) o;
        
        long declarative_part_end = System.currentTimeMillis();
        
        NewDoor door = answerSetToCellMatrix(answers, nextPartitioned);
        
        long imperative_part_end = System.currentTimeMillis();
        
        if (IS_DEBUG_MODE) {
        	printMatrixDebug();
        	DEBUGGER.printOnFile("spacePartitioning( " + " min_row: " + nextPartitioned.getMinRow() + " min_col: " + nextPartitioned.getMinCol() 
            		+ " max_row: " + nextPartitioned.getMaxRow() + " max_col: " + nextPartitioned.getMaxCol() + " horizontal: " + horizontal + 
            		" nextPartitioned_size: " + nextPartitioned.getSize() + " )\n");
        	computeTime(imperative_part_start, declarative_part_start, imperative_part_end, declarative_part_end);
        }
        
        if (door != null) {
            if (door.getType().equals("hdoor")) {
                spacePartitioning(!horizontal, new Partition(nextPartitioned.getMinRow(), nextPartitioned.getMinCol(),
                        door.getRow(), nextPartitioned.getMaxCol()));
                spacePartitioning(!horizontal, new Partition(door.getRow(), nextPartitioned.getMinCol(),
                        nextPartitioned.getMaxRow(), nextPartitioned.getMaxCol()));
            } else if (door.getType().equals("vdoor")) {
                spacePartitioning(!horizontal, new Partition(nextPartitioned.getMinRow(), nextPartitioned.getMinCol(),
                        nextPartitioned.getMaxRow(), door.getColumn()));
                spacePartitioning(!horizontal, new Partition(nextPartitioned.getMinRow(), door.getColumn(),
                        nextPartitioned.getMaxRow(), nextPartitioned.getMaxCol()));
            }
       }
       else
    	   addPartition(nextPartitioned);
        
    }
    
    public void addPartition( Partition nextPartitioned) {
    	partitions.add(nextPartitioned);
    	Pair< Integer, Integer> door;
    	
    	for( int i = nextPartitioned.getMinRow(); i <= nextPartitioned.getMaxRow(); i++ ) {
    		
    		door = new Pair<Integer, Integer>(i, nextPartitioned.getMinCol());
    		if( matrixCells.getCells().get(door).getType().equals("vdoor") )
    			nextPartitioned.getDoors().add(door);
    		
    		door = new Pair<Integer,Integer>(i, nextPartitioned.getMaxCol());
    		if( matrixCells.getCells().get(door).getType().equals("vdoor"))
    			nextPartitioned.getDoors().add(door);
    	}
    	
    	for( int i = nextPartitioned.getMinCol(); i <= nextPartitioned.getMaxCol(); i++ ) {
    		
    		door = new Pair<Integer, Integer>(nextPartitioned.getMinRow(), i);
    		if( matrixCells.getCells().get(door).getType().equals("hdoor"))
    			nextPartitioned.getDoors().add(door);
    		
    		door = new Pair<Integer,Integer>(nextPartitioned.getMaxRow(), i);
    		if( matrixCells.getCells().get(door).getType().equals("hdoor"))
    			nextPartitioned.getDoors().add(door);
    	}
    }
    
    @Override
    public String toString() {
        for (int i = 1; i <= maxRows; i++) {
        	for (int j = 1; j <= maxColumns; j++)
        		if (matrixCells.getCells().containsKey(new Pair<Integer, Integer>(i, j)))
       				System.out.print(matrixCells.getCells().get(new Pair<Integer, Integer>(i, j)).getGVGAI() + " ");
       			else
       				System.out.print("- ");
       		System.out.println();
       	}
       
       	System.out.println("PARTITIONS NUMBER: " + partitions.size());
       
       	for( int i = 0; i < partitions.size(); i++ ) {
       		Partition partition = partitions.get(i);
       		System.out.println("Partition " + i + " " + partition.getMinRow() + " " + partition.getMaxRow() + " " 
       			+ partition.getMinCol() + " " + partition.getMaxCol());
       		for ( int j = 0; j < partition.getDoors().size(); j++ ) {
       			Pair<Integer, Integer> door_coordinate = partition.getDoors().get(j);
       			Cell door = matrixCells.getCells().get(door_coordinate);
       			System.out.println( j+1 + " DOOR: " + door.getRow() + " " + door.getColumn() + " " + door.getGVGAI() + " " + door.getType());
       		}	
       		System.out.println();
       	}
       	return "MapGenerator [width=" + maxColumns + ", height=" + maxRows + ", min_distance_wall=" + minDistanceWall
       			+ ", random_answerset_number=" + randomAnswersetNumber + "]\n";
    }
    
    public List<Partition> getPartitions() {
    	return partitions;
    }
    
    public void computeTime( long imperative_part_start, long declarative_part_start, long imperative_part_end, long declarative_part_end ){
    	long elapsed_time_imperative_part = (declarative_part_start - imperative_part_start) + (imperative_part_end - declarative_part_end);
        long elapsed_time_declarative_part = (declarative_part_end - declarative_part_start );
        long elapsed_time_function = (imperative_part_end - imperative_part_start );
        DEBUGGER.printOnFile(String.format("Elapsed Time Function: %d min, %d sec, %d ms\n",
                TimeUnit.MILLISECONDS.toMinutes(elapsed_time_function), TimeUnit.MILLISECONDS.toSeconds(elapsed_time_function)
                        - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(elapsed_time_function)) ,elapsed_time_function ));
        DEBUGGER.printOnFile(String.format("Elapsed Time Imperative Part: %d min, %d sec, %d ms\n",
                TimeUnit.MILLISECONDS.toMinutes(elapsed_time_imperative_part), TimeUnit.MILLISECONDS.toSeconds(elapsed_time_imperative_part)
                        - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(elapsed_time_imperative_part)) ,elapsed_time_imperative_part ));
        DEBUGGER.printOnFile(String.format("Elapsed Time Declarative Part: %d min, %d sec, %d ms\n\n\n",
                TimeUnit.MILLISECONDS.toMinutes(elapsed_time_declarative_part), TimeUnit.MILLISECONDS.toSeconds(elapsed_time_declarative_part)
                        - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(elapsed_time_declarative_part)), elapsed_time_declarative_part));
    }
}