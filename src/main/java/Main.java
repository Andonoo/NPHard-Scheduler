import algorithm.GreedyScheduler;
import algorithm.ParallelOptimalScheduler;
import algorithm.SequentialOptimalScheduler;
import io.CommandLineException;
import io.InputHandler;
import io.OutputHandler;
import org.graphstream.graph.implementations.AdjacencyListGraph;

public class Main {

    public static void main(String[] args) {
        long startTime = System.nanoTime();
        InputHandler inputParser = null;
        try {
            inputParser = new InputHandler(args);
        } catch (CommandLineException e) {
            e.printStackTrace();
        }

        if (inputParser.produceGUI()) {
            //javafx shazam
            System.out.println("Thanks Francis");
        }

        AdjacencyListGraph g = inputParser.getGraph();
        GreedyScheduler greedyScheduler = new GreedyScheduler(g, inputParser.getProcessors());
        greedyScheduler.executeAlgorithm();
        System.out.println("Greedy length: " + greedyScheduler.getSolutionLength());

        if (inputParser.getCores() == 1) {
            System.out.println("Sequential");
            SequentialOptimalScheduler optimalScheduler = new SequentialOptimalScheduler(greedyScheduler.getTopologicalOrder(), inputParser.getProcessors());
            boolean moreOptimalFound = optimalScheduler.executeBranchAndBoundAlgorithm(greedyScheduler.getSolutionLength());

            OutputHandler outputHandler = new OutputHandler();
            if (moreOptimalFound) {
                outputHandler.createOutputFile(optimalScheduler.getSolution(), g);
                System.out.println("Length: " + optimalScheduler.getSolution().getScheduleLength());
            } else {
                outputHandler.createOutputFile(greedyScheduler.getSolution());
                System.out.println("Length: " + optimalScheduler.getSolution().getScheduleLength());
            }
        }
        else {
            System.out.println("Parallel");
            ParallelOptimalScheduler optimalScheduler = new ParallelOptimalScheduler(greedyScheduler.getTopologicalOrder(), inputParser.getProcessors());
            boolean moreOptimalFound = optimalScheduler.executeBranchAndBoundAlgorithm(greedyScheduler.getSolutionLength());

            OutputHandler outputHandler = new OutputHandler();
            if (moreOptimalFound) {
                outputHandler.createOutputFile(optimalScheduler.getSolution(), g);
                System.out.println("Length: " + optimalScheduler.getSolution().getScheduleLength());
            } else {
                outputHandler.createOutputFile(greedyScheduler.getSolution());
                System.out.println("Length: " + greedyScheduler.getSolutionLength());
            }
        }

        long endTime = System.nanoTime();
        System.out.println("Time taken (ms): " + (endTime-startTime)/1000000);
    }
}
