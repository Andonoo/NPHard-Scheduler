package se306.optimality;

import algorithm.GreedyScheduler;
import algorithm.ParallelOptimalScheduler;
import io.CommandLineException;
import io.InputHandler;
import org.graphstream.graph.implementations.AdjacencyListGraph;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static se306.optimality.OptimalityTestConstants.*;
import static se306.optimality.OptimalityTestConstants.GRAPH8_21NODES_4P_FINISH_TIME;

public class ParallelOptimalTest {

    private static final double DELTA = 1e-15;

    private Double findParallelOptimalFinishTime(AdjacencyListGraph graph, int processors, int cores) {
        GreedyScheduler greedyScheduler = new GreedyScheduler(graph, processors);
        greedyScheduler.executeAlgorithm();

        double scheduleFinishTime = greedyScheduler.getSolutionLength();

        ParallelOptimalScheduler optimalScheduler = new ParallelOptimalScheduler(greedyScheduler.getTopologicallyOrderedTaskNodes(), processors, cores);
        boolean moreOptimalFound = optimalScheduler.executeBranchAndBoundAlgorithm(greedyScheduler.getSolutionLength());

        if (moreOptimalFound){
            scheduleFinishTime = optimalScheduler.getSolution().getScheduleLength();
        }

        return scheduleFinishTime;
    }


    //region Tests for 21 nodes with 4 processors

    @Test
    public void test21Nodes4PGraph1() throws CommandLineException {
        InputHandler inputParser = new InputHandler(new String[]{GRAPH1_21NODES, "4", "-p","4"});

        Double optimalFinishTime = findParallelOptimalFinishTime(inputParser.getGraph(),inputParser.getProcessors(),inputParser.getCores());

        assertEquals(GRAPH1_21NODES_4P_FINISH_TIME,optimalFinishTime,DELTA);
    }

    @Test
    public void test21Nodes4PGraph2() throws CommandLineException {
        InputHandler inputParser = new InputHandler(new String[]{GRAPH2_21NODES, "4", "-p","4"});

        Double optimalFinishTime = findParallelOptimalFinishTime(inputParser.getGraph(),inputParser.getProcessors(),inputParser.getCores());

        assertEquals(GRAPH2_21NODES_4P_FINISH_TIME,optimalFinishTime,DELTA);
    }

    @Test
    public void test21Nodes4PGraph3() throws CommandLineException {
        InputHandler inputParser = new InputHandler(new String[]{GRAPH3_21NODES, "4", "-p","4"});

        Double optimalFinishTime = findParallelOptimalFinishTime(inputParser.getGraph(),inputParser.getProcessors(),inputParser.getCores());

        assertEquals(GRAPH3_21NODES_4P_FINISH_TIME,optimalFinishTime,DELTA);
    }

    @Test
    public void test21Nodes4PGraph4() throws CommandLineException {
        InputHandler inputParser = new InputHandler(new String[]{GRAPH4_21NODES, "4", "-p","4"});

        Double optimalFinishTime = findParallelOptimalFinishTime(inputParser.getGraph(),inputParser.getProcessors(),inputParser.getCores());

        assertEquals(GRAPH4_21NODES_4P_FINISH_TIME,optimalFinishTime,DELTA);
    }

    @Test
    public void test21Nodes4PGraph5() throws CommandLineException {
        InputHandler inputParser = new InputHandler(new String[]{GRAPH5_21NODES, "4", "-p","4"});

        Double optimalFinishTime = findParallelOptimalFinishTime(inputParser.getGraph(),inputParser.getProcessors(),inputParser.getCores());

        assertEquals(GRAPH5_21NODES_4P_FINISH_TIME,optimalFinishTime,DELTA);
    }

    @Test
    public void test21Nodes4PGraph6() throws CommandLineException {
        InputHandler inputParser = new InputHandler(new String[]{GRAPH6_21NODES, "4", "-p","4"});

        Double optimalFinishTime = findParallelOptimalFinishTime(inputParser.getGraph(),inputParser.getProcessors(),inputParser.getCores());

        assertEquals(GRAPH6_21NODES_4P_FINISH_TIME,optimalFinishTime,DELTA);
    }

    @Test
    public void test21Nodes4PGraph7() throws CommandLineException {
        InputHandler inputParser = new InputHandler(new String[]{GRAPH7_21NODES, "4", "-p","4"});

        Double optimalFinishTime = findParallelOptimalFinishTime(inputParser.getGraph(),inputParser.getProcessors(),inputParser.getCores());

        assertEquals(GRAPH7_21NODES_4P_FINISH_TIME,optimalFinishTime,DELTA);
    }

    @Test
    public void test21Nodes4PGraph8() throws CommandLineException {
        InputHandler inputParser = new InputHandler(new String[]{GRAPH8_21NODES, "4", "-p","4"});

        Double optimalFinishTime = findParallelOptimalFinishTime(inputParser.getGraph(),inputParser.getProcessors(),inputParser.getCores());

        assertEquals(GRAPH8_21NODES_4P_FINISH_TIME,optimalFinishTime,DELTA);
    }

    //endregion

    @Test
    public void testRandom() throws CommandLineException{
        InputHandler inputParser = new InputHandler(new String[]{TEST_GRAPH_INPUT, "2", "-p","4"});

        Double optimalFinishTime = findParallelOptimalFinishTime(inputParser.getGraph(),inputParser.getProcessors(),inputParser.getCores());

        assertEquals(TEST_GRAPH_FINISH_TIME,optimalFinishTime,DELTA);
    }
}
