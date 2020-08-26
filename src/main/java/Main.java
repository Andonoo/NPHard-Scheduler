import javafx.Controller;
import algorithm.GreedyScheduler;
import algorithm.ParallelOptimalScheduler;
import algorithm.SequentialOptimalScheduler;
import io.CommandLineException;
import io.InputHandler;
import io.OutputHandler;
import javafx.InfoTracker;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application{

    static InfoTracker _infoTracker;

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("main.fxml"));
        Controller controller = new Controller();
        loader.setController(controller);
        Parent root = loader.load();

        Scene scene = new Scene(root);
        controller.setInputHandler(_infoTracker);
        controller.init();

        new Thread(Main::executeAlgorithm).start();
        primaryStage.setTitle("Scheduler visualisation");
        primaryStage.setScene(scene);
        
        primaryStage.setOnCloseRequest(event -> System.exit(0));
        primaryStage.show();
    }

    public static void main(String[] args) {
        InputHandler inputHandler = null;
        try {
            inputHandler = new InputHandler(args);
        } catch (CommandLineException e) {
            e.printStackTrace();
        }

        _infoTracker = new InfoTracker(inputHandler.getFileName(), inputHandler.getProcessors(), inputHandler.getCores(), inputHandler.getGraph());
        if (inputHandler.produceGUI()) {
            launch(args);
        } else {
            executeAlgorithm();
        }
        System.exit(0);
    }

    public static void executeAlgorithm() {
        System.out.println("Start executing algorithm");
        GreedyScheduler greedyScheduler = new GreedyScheduler(_infoTracker.getGraph(), _infoTracker.get_processors());
        greedyScheduler.executeAlgorithm();

        if (_infoTracker.get_cores() == 1) {
            System.out.println("Sequential");
            SequentialOptimalScheduler optimalScheduler = new SequentialOptimalScheduler(greedyScheduler.getTopologicalOrder(), _infoTracker);
            boolean moreOptimalFound = optimalScheduler.executeBranchAndBoundAlgorithm(greedyScheduler.getSolutionLength());
            _infoTracker.setFinished(true);
            OutputHandler outputHandler = new OutputHandler();
            if (moreOptimalFound) {
                outputHandler.createOutputFile(optimalScheduler.getSolution(), _infoTracker.getGraph());
            } else {
                outputHandler.createOutputFile(greedyScheduler.getSolution());
            }

        } else {
            System.out.println("Parallel");
            ParallelOptimalScheduler optimalScheduler = new ParallelOptimalScheduler(greedyScheduler.getTopologicalOrder(), _infoTracker.get_processors());
            boolean moreOptimalFound = optimalScheduler.executeBranchAndBoundAlgorithm(greedyScheduler.getSolutionLength());
            _infoTracker.setFinished(true);
            OutputHandler outputHandler = new OutputHandler();
            if (moreOptimalFound) {
                outputHandler.createOutputFile(optimalScheduler.getSolution(), _infoTracker.getGraph());
            } else {
                outputHandler.createOutputFile(greedyScheduler.getSolution());
            }
        }
    }
}
