import algorithm.GreedyScheduler;
import algorithm.ParallelOptimalScheduler;
import algorithm.Scheduler;
import algorithm.SequentialOptimalScheduler;
import domain.DomainHandler;
import domain.TaskNode;
import io.CommandLineException;
import io.InputHandler;
import io.OutputHandler;
import javafx.Controller;
import javafx.InfoTracker;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Map;

public class Main extends Application {

    static InfoTracker _infoTracker;
    static InputHandler _inputHandler = null;

    public static void main(String[] args) {

        try {
            _inputHandler = new InputHandler(args);
        } catch (CommandLineException e) {
            e.printStackTrace();
        }

        _infoTracker = new InfoTracker(_inputHandler.getFileName(), _inputHandler.getProcessors(), _inputHandler.getCores(), _inputHandler.getGraph());
        if (_inputHandler.produceGUI()) {
            launch(args);
        } else {
            executeAlgorithm();
        }

        System.exit(0);
    }

    /**
     * This method is called to start executing the required algorithms to produce the optimal schedule.
     */
    public static void executeAlgorithm() {

        // Find an OK schedule using the greedy algorithm
        GreedyScheduler greedyScheduler = _inputHandler.produceGUI() ? new GreedyScheduler(_inputHandler.getGraph(), _inputHandler.getProcessors(), _infoTracker) :
                new GreedyScheduler(_inputHandler.getGraph(), _inputHandler.getProcessors());
        greedyScheduler.executeAlgorithm();

        // Use this 'ok' schedule to bound to the optimal scheduler
        Scheduler optimalScheduler;

        if (_infoTracker.getCores() == 1) {
            optimalScheduler = _inputHandler.produceGUI() ?
                    new SequentialOptimalScheduler(greedyScheduler.getTopologicallyOrderedTaskNodes(), _inputHandler.getProcessors(), _infoTracker) :
                    new SequentialOptimalScheduler(greedyScheduler.getTopologicallyOrderedTaskNodes(), _inputHandler.getProcessors());
        } else {
            optimalScheduler = _inputHandler.produceGUI() ?
                    new ParallelOptimalScheduler(greedyScheduler.getTopologicallyOrderedTaskNodes(), _inputHandler.getProcessors(), _inputHandler.getCores(), _infoTracker) :
                    new ParallelOptimalScheduler(greedyScheduler.getTopologicallyOrderedTaskNodes(), _inputHandler.getProcessors(), _inputHandler.getCores());
        }

        Map<TaskNode, Double> bottomLevels = DomainHandler.getBottomLevels(greedyScheduler.getTopologicallyOrderedTaskNodes());
        boolean moreOptimalFound = optimalScheduler.executeBranchAndBoundAlgorithm(greedyScheduler.getSolutionLength(), bottomLevels);

        _infoTracker.setIsFinished(true); // Stop the polling for info tracker

        // Create the output file with optimal solution
        OutputHandler outputHandler = new OutputHandler();
        if (moreOptimalFound) {
            outputHandler.createOutputFile(optimalScheduler.getSolution(), _infoTracker.getGraph());
        } else {
            outputHandler.createOutputFile(greedyScheduler.getSolution());
        }
    }


    @Override
    public void start(Stage primaryStage) throws IOException {

        // Set up for javafx GUI
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("main.fxml"));
        Controller controller = new Controller();
        loader.setController(controller);
        Parent root = loader.load();
        Scene scene = new Scene(root);
        controller.setInputHandler(_infoTracker);
        controller.init();

        // Create new thread to run the algorithm while javafx application runs
        new Thread(Main::executeAlgorithm).start();
        primaryStage.setTitle("Scheduler visualisation");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);

        primaryStage.setOnCloseRequest(event -> System.exit(0));
        primaryStage.show();
    }
}
