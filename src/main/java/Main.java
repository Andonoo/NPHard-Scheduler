import algorithm.GreedyScheduler;
import algorithm.SequentialOptimalScheduler;
import io.InputHandler;
import io.OutputHandler;
import org.graphstream.graph.implementations.AdjacencyListGraph;

public class Main {

    public static void main(String[] args) {
        long startTime = System.nanoTime();

        InputHandler inputParser = new InputHandler(args);
        if (inputParser.produceGUI()) {
            //javafx shazam
            System.out.println("Thanks Francis");
        }

        AdjacencyListGraph g = inputParser.getGraph();
        GreedyScheduler greedyScheduler = new GreedyScheduler(g, inputParser.getProcessors());
        greedyScheduler.executeAlgorithm();

        SequentialOptimalScheduler optimalScheduler = new SequentialOptimalScheduler(greedyScheduler.getTopologicalOrder(), inputParser.getProcessors());
        optimalScheduler.executeBranchAndBoundAlgorithm(800);

        long endTime = System.nanoTime();
        System.out.println("Runtime: " + (endTime - startTime)/1000000000);
    }
}
