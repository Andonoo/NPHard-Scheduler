import algorithm.GreedyScheduler;
import algorithm.ParallelOptimalScheduler;
import algorithm.SequentialOptimalScheduler;
import io.CommandLineException;
import io.InputHandler;
import io.OutputHandler;
import org.graphstream.graph.implementations.AdjacencyListGraph;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application{

    static InputHandler _inputHandler;

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));

        Scene scene = new Scene(root);

        new Thread(Main::executeAlgorithm).start();
        primaryStage.setTitle("Scheduler visualisation");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        try {
            _inputHandler = new InputHandler(args);
        } catch (CommandLineException e) {
            e.printStackTrace();
        }

        if (_inputHandler.produceGUI()) {
            launch(args);
        } else {
            executeAlgorithm();
        }
    }

    public static void executeAlgorithm() {
        AdjacencyListGraph g = _inputHandler.getGraph();
        GreedyScheduler greedyScheduler = new GreedyScheduler(g, _inputHandler.getProcessors());
        greedyScheduler.executeAlgorithm();

        if (_inputHandler.getCores() == 1) {
            System.out.println("Sequential");
            SequentialOptimalScheduler optimalScheduler = new SequentialOptimalScheduler(greedyScheduler.getTopologicalOrder(), _inputHandler.getProcessors());
            boolean moreOptimalFound = optimalScheduler.executeBranchAndBoundAlgorithm(greedyScheduler.getSolutionLength());

            OutputHandler outputHandler = new OutputHandler();
            if (moreOptimalFound) {
                outputHandler.createOutputFile(optimalScheduler.getSolution(), g);
            } else {
                outputHandler.createOutputFile(greedyScheduler.getSolution());
            }
        } else {
            System.out.println("Parallel");
            ParallelOptimalScheduler optimalScheduler = new ParallelOptimalScheduler(greedyScheduler.getTopologicalOrder(), _inputHandler.getProcessors());
            boolean moreOptimalFound = optimalScheduler.executeBranchAndBoundAlgorithm(greedyScheduler.getSolutionLength());

            OutputHandler outputHandler = new OutputHandler();
            if (moreOptimalFound) {
                outputHandler.createOutputFile(optimalScheduler.getSolution(), g);
            } else {
                outputHandler.createOutputFile(greedyScheduler.getSolution());
            }
        }
    }
}
