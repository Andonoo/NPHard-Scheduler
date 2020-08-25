import algorithm.GreedyScheduler;
import algorithm.ParallelOptimalScheduler;
import algorithm.SequentialOptimalScheduler;
import io.CommandLineException;
import io.InputHandler;
import io.OutputHandler;
import org.graphstream.graph.implementations.AdjacencyListGraph;

public class Main {

    public static void main(String[] args) {
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


        long starts = System.nanoTime();

        SequentialOptimalScheduler seq = new SequentialOptimalScheduler(greedyScheduler.getTopologicalOrder(), inputParser.getProcessors());
        // ParallelOptimalScheduler optimalScheduler = new ParallelOptimalScheduler(greedyScheduler.getTopologicalOrder(), inputParser.getProcessors());

        long ends = System.nanoTime();
        double durations = (double) (ends-starts)/1000000000;

        long start = System.nanoTime();

        // SequentialOptimalScheduler optimalScheduler = new SequentialOptimalScheduler(greedyScheduler.getTopologicalOrder(), inputParser.getProcessors());
        ParallelOptimalScheduler optimalScheduler = new ParallelOptimalScheduler(greedyScheduler.getTopologicalOrder(), inputParser.getProcessors());
        boolean moreOptimalFound = optimalScheduler.executeBranchAndBoundAlgorithm(greedyScheduler.getSolutionLength());

        long end = System.nanoTime();
        double duration = (double) (end-start)/1000000000;

        System.out.println("Duration: " + durations + " seconds!!!");
        System.out.println("Duration: " + duration + " seconds!!!" + " with bound of : " + greedyScheduler.getSolutionLength() + " or " + optimalScheduler.getSolution().getScheduleLength());

        OutputHandler outputHandler = new OutputHandler();
        if (moreOptimalFound) {
            outputHandler.createOutputFile(optimalScheduler.getSolution(), g);
        } else {
            outputHandler.createOutputFile(greedyScheduler.getSolution());
        }
    }
}
