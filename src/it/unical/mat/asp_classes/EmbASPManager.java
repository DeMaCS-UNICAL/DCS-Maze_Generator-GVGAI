package it.unical.mat.asp_classes;

import it.unical.mat.debug.UnavCells;
import it.unical.mat.embasp.base.Handler;
import it.unical.mat.embasp.base.InputProgram;
import it.unical.mat.embasp.base.OptionDescriptor;
import it.unical.mat.embasp.languages.IllegalAnnotationException;
import it.unical.mat.embasp.languages.ObjectNotValidException;
import it.unical.mat.embasp.languages.asp.ASPInputProgram;
import it.unical.mat.embasp.languages.asp.ASPMapper;
import it.unical.mat.embasp.platforms.desktop.DesktopHandler;
import it.unical.mat.embasp.specializations.clingo.desktop.ClingoDesktopService;

public class EmbASPManager {

	private String solverPath = "lib/clingo";
    private Handler handler = new DesktopHandler(new ClingoDesktopService(solverPath));
    private InputProgram input = new ASPInputProgram();

    public EmbASPManager() {
    }

    public EmbASPManager(Handler handler, String solverPath, InputProgram input) {
        super();
        this.handler = handler;
        this.solverPath = solverPath;
        this.input = input;
    }

    public EmbASPManager(InputProgram input) {
        super();
        this.input = input;
    }

    public Handler getHandler() {
        return handler;
    }

    public InputProgram getInput() {
        return input;
    }

    public void initializeEmbASP() throws ObjectNotValidException, IllegalAnnotationException {
        ASPMapper.getInstance().registerClass(Cell.class);
        ASPMapper.getInstance().registerClass(NewDoor.class);
        ASPMapper.getInstance().registerClass(UnavCells.class);
        ASPMapper.getInstance().registerClass(Connected8.class);
        ASPMapper.getInstance().registerClass(Partition.class);
        ASPMapper.getInstance().registerClass(Assignment.class);
        ASPMapper.getInstance().registerClass(ObjectAssignment.class);
    }

    public void initializeEmbASP(int randomAnswersetNumber) throws ObjectNotValidException, IllegalAnnotationException {
        initializeEmbASP();
        // Initialize the options for the solver
        handler.addOption(new OptionDescriptor("-n" + randomAnswersetNumber));
//        handler.addOption(new OptionDescriptor(" --filter=cell/3,new_door/3,object_assignment6/6,connected8/8,assignment5/5,partition4/4 --printonlyoptimum"));
    }
}
