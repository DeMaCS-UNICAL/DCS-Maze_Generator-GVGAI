package it.unical.mat.map_generator.gui;
	
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.Random;

import it.unical.mat.map_generator.MapGenerator;
import it.unical.mat.util.ConfigurationFile;
import it.unical.mat.util.FilePrinter;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import tools.Utils;
import tracks.ArcadeMachine;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

public class MapGeneratorGui extends Application {

	@FXML 
	ChoiceBox<Integer> columnSize = new ChoiceBox<>();
	
	@FXML 
	ChoiceBox<Integer> rowSize = new ChoiceBox<>();
	
	@FXML 
	ChoiceBox<Integer> randomAsNum = new ChoiceBox<>();
	
	@FXML
	TextField minRoomSize = new TextField();
	
	@FXML
	TextField minDistanceWall = new TextField();
	
	@FXML
	TextField randomSeed = new TextField();
	
	@FXML
	ImageView labyrinth = new ImageView();
	
	@FXML 
	ImageView zelda = new ImageView();
	
	@FXML 
	ImageView start = new ImageView();
	
	@FXML
	Button debug = new Button();
	
	@FXML
	Text error = new Text();
	
	@FXML
	Text loading = new Text();
	
	@FXML
	Slider pruningPercentage = new Slider();
	
	@FXML
	Slider sameOrientationPercentage = new Slider();
	
	@FXML
	ProgressIndicator loadingInd = new ProgressIndicator();
	
	private boolean lab = false;
	private boolean zel = false;
	private boolean deb = false;
	private boolean sta = false;
	private Timeline timeline = null;
	private String encoding_folder = null;
	private String pruning_percentage = "0.0";
	private String same_orientation_percentage = "0.0";
	
	ObservableList<Integer> columnRowNumber = FXCollections.observableArrayList();
	
	ObservableList<Integer> randomAsNumber = FXCollections.observableArrayList();
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws IOException {
		Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("ControlPane.fxml"));
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Level game generator with ASP");
		scene.getStylesheets().add(getClass().getClassLoader().getResource("ControlPane.css").toExternalForm());
		scene.getRoot().requestFocus();
		primaryStage.setResizable(false);
		primaryStage.centerOnScreen();
		primaryStage.show();
	}
	
	@FXML
	public void labyrinthClicked() throws IOException
	{
		lab = true;
		zel = false;
		zeldaExited();
	}
	
	@FXML
	public void labyrinthMoved() throws IOException
	{
		ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness(0);
        labyrinth.setEffect(colorAdjust);
	}
	
	@FXML
	public void labyrinthExited()
	{
		if( !lab)
		{
			ColorAdjust colorAdjust = new ColorAdjust();
			colorAdjust.setBrightness(-0.7);
			labyrinth.setEffect(colorAdjust);
		}
	}
	
	@FXML
	public void zeldaClicked() throws IOException
	{
		lab = false;
		zel = true;
		labyrinthExited();
	}
	
	@FXML
	public void zeldaMoved() throws IOException
	{
		ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness(0);
        zelda.setEffect(colorAdjust);
	}
	
	@FXML
	public void zeldaExited() throws IOException
	{
		if( !zel)
		{
			ColorAdjust colorAdjust = new ColorAdjust();
			colorAdjust.setBrightness(-0.7);
			zelda.setEffect(colorAdjust);
		}
	}
	
	@FXML
	public void startClicked() throws IOException
	{
		try {
			
			ConfigurationFile configurationFile = new ConfigurationFile();
			configurationFile.getLock().lock();
			
			FilePrinter configPrinter = new FilePrinter("configuration/config.properties");
			
			int column_size = columnSize.getValue();
			int row_size = rowSize.getValue();
			int random_as_number = randomAsNum.getValue();
			Integer min_room_size = Integer.parseInt(minRoomSize.getText());
			Integer min_distance_wall = Integer.parseInt(minDistanceWall.getText());
			Integer random_seed = Integer.parseInt(randomSeed.getText());
			
			configPrinter.printOnFile("rowSize=" + row_size+"\n");
			configPrinter.printOnFile("columnSize=" + column_size +"\n");
			configPrinter.printOnFile("minRoomSize="+min_room_size+"\n");
			configPrinter.printOnFile("minDistanceWall="+min_distance_wall+"\n");
			configPrinter.printOnFile("randomSeed="+random_seed+"\n");
			configPrinter.printOnFile("randomAnswersetNumber="+random_as_number+"\n");
			configPrinter.printOnFile("sameOrientationPercentage=" + same_orientation_percentage + "\n");
			configPrinter.printOnFile("pruningPecentage=" + pruning_percentage + "\n");
			configPrinter.printOnFile("encodingFolder=" + encoding_folder + "\n");
			configPrinter.printOnFile("debug="+deb+"\n");
			
			if( zel )
				configPrinter.printOnFile("game=zelda");
			else
				configPrinter.printOnFile("game=labyrinth");
			
			configurationFile.getLock().unlock();
			
			File file = new File(encoding_folder);
			
			for( int i = 0; i < file.listFiles().length; i++ )
				if( ! file.listFiles()[i].toString().matches("\\w:\\\\.*\\.asp"))
					throw new Exception();
					
			if( !zel && !lab || encoding_folder == null )
				throw new Exception();

			loadingInd.setVisible(true);
			loading.setVisible(true);
			error.setVisible(false);
			
			MapGenerator mapGenerator = new MapGenerator(configurationFile);
			mapGenerator.generateMap();

			//Load available games
			String spGamesCollection =  "examples/all_games_sp.csv";
			String[][] games = Utils.readGames(spGamesCollection);

			//Game settings
			boolean visuals = true;
			int seed = new Random().nextInt();

			// Game and level to play
			int gameIdx = 0;
			
			gameIdx = (lab)? 54 : 100;
			
			String gameName = games[gameIdx][1];
			String game = games[gameIdx][0];
			String level1 = "level_map/level_map";

			String recordActionsFile = null;				
			
			ArcadeMachine.playOneGame(game, level1, recordActionsFile, seed);
			
		} catch (Exception e) {
			
			loadingInd.setVisible(false);
			loading.setVisible(false);
			error.setVisible(true);
			
			timeline = new Timeline(new KeyFrame(Duration.seconds(0.08), evt -> error.setVisible(false)),
	                new KeyFrame(Duration.seconds( 0.1), evt -> error.setVisible(true)));
			timeline.setCycleCount(Animation.INDEFINITE);
			timeline.play();
		}
	}
	
	@FXML
	public void startMoved() throws IOException
	{
		ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness(0);
        start.setEffect(colorAdjust);
	}
	
	@FXML
	public void startExited() throws IOException
	{
		ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness(-0.7);
        start.setEffect(colorAdjust);
	}
	
	@FXML
	public void debugClicked() throws IOException
	{
		deb = !deb;
		
		ColorAdjust colorAdjust = new ColorAdjust();
	
		if( deb )
			colorAdjust.setBrightness(0);
		else 
			colorAdjust.setBrightness(-0.7);
		
		debug.setEffect(colorAdjust);
	}
	
	@FXML
	public void searchClicked()
	{
		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle("Search Encodings Folder");
		Window stage = null;
		File selectedDirectory = chooser.showDialog(stage);
		encoding_folder = selectedDirectory.toPath().toString();
		encoding_folder = encoding_folder.replace(' ','_');
		encoding_folder = encoding_folder.replace("\\","\\\\");
	}
	
	@FXML
	public void pruningReleased()
	{
		pruning_percentage = new DecimalFormat("#.##").format(pruningPercentage.getValue()/100).replace(",", ".");
	}
	
	@FXML
	public void orientationReleased()
	{
		same_orientation_percentage = new DecimalFormat("#.##").format(sameOrientationPercentage.getValue()/1000).replaceAll(",", ".");
	}
	
	@FXML
	public void initialize()
	{	
		columnRowNumber.addAll(20,50,100,150);
		
		randomAsNumber.addAll(1,5,10,15);
		
		columnSize.setItems(columnRowNumber);
		rowSize.setItems(columnRowNumber);
		
		randomAsNum.setItems(randomAsNumber);
		
		ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness(-0.7);
        
        labyrinth.setEffect(colorAdjust);
        zelda.setEffect(colorAdjust);
        debug.setEffect(colorAdjust);
        start.setEffect(colorAdjust);
        
        loadingInd.setVisible(false);
		loading.setVisible(false);
		error.setVisible(false);
	}
}
