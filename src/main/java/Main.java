import algorithm.GreedyScheduler;
import algorithm.SequentialOptimalScheduler;
import domain.PartialSchedule;
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
        System.out.println("Greedy length: " + greedyScheduler.getSolutionLength());

        SequentialOptimalScheduler optimalScheduler = new SequentialOptimalScheduler(greedyScheduler.getTopologicalOrder(), inputParser.getProcessors());
        boolean moreOptimalFound = optimalScheduler.executeBranchAndBoundAlgorithm(greedyScheduler.getSolutionLength());

        OutputHandler outputHandler = new OutputHandler();
        if (moreOptimalFound) {
            outputHandler.createOutputFile(optimalScheduler.getSolution(), g);
            System.out.println("Schedule length: " + optimalScheduler.getSolution().getScheduleLength());
        } else {
            outputHandler.createOutputFile(greedyScheduler.getSolution());
            System.out.println("Schedule length: " + greedyScheduler.getSolutionLength());
        }

        long endTime = System.nanoTime();
        System.out.println("Time taken: " + (endTime-startTime)/1000000000);
    }
}
