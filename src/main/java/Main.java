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

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));

        Scene scene = new Scene(root);

        primaryStage.setTitle("Scheduler visualisation");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        InputHandler inputParser = null;
        try {
            inputParser = new InputHandler(args);
        } catch (CommandLineException e) {
            e.printStackTrace();
        }

        if (inputParser.produceGUI()) {
            //javafx shazam
            launch(args);
            System.out.println("Thanks Francis");
        }

        AdjacencyListGraph g = inputParser.getGraph();
        GreedyScheduler greedyScheduler = new GreedyScheduler(g, inputParser.getProcessors());
        greedyScheduler.executeAlgorithm();

        if (inputParser.getCores() == 1) {
            System.out.println("Sequential");
            SequentialOptimalScheduler optimalScheduler = new SequentialOptimalScheduler(greedyScheduler.getTopologicalOrder(), inputParser.getProcessors());
            boolean moreOptimalFound = optimalScheduler.executeBranchAndBoundAlgorithm(greedyScheduler.getSolutionLength());

            OutputHandler outputHandler = new OutputHandler();
            if (moreOptimalFound) {
                outputHandler.createOutputFile(optimalScheduler.getSolution(), g);
            } else {
                outputHandler.createOutputFile(greedyScheduler.getSolution());
            }
        }
        else {
            System.out.println("Parallel");
            ParallelOptimalScheduler optimalScheduler = new ParallelOptimalScheduler(greedyScheduler.getTopologicalOrder(), inputParser.getProcessors());
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
