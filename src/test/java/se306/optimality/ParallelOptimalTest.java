package se306.optimality;

import algorithm.GreedyScheduler;
import algorithm.ParallelOptimalScheduler;
import io.CommandLineException;
import io.InputHandler;
import org.graphstream.graph.implementations.AdjacencyListGraph;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static se306.optimality.OptimalityTestConstants.*;

public class ParallelOptimalTest {

    private static final double DELTA = 1e-15;

    private Double findParallelOptimalFinishTime(AdjacencyListGraph graph, int processors, int cores) {
        GreedyScheduler greedyScheduler = new GreedyScheduler(graph, processors);
        greedyScheduler.executeAlgorithm();

        double scheduleFinishTime = greedyScheduler.getSolutionLength();

        ParallelOptimalScheduler optimalScheduler = new ParallelOptimalScheduler(greedyScheduler.getTopologicallyOrderedTaskNodes(), processors, cores);
        boolean moreOptimalFound = optimalScheduler.executeBranchAndBoundAlgorithm(greedyScheduler.getSolutionLength(), null);

        if (moreOptimalFound){
            scheduleFinishTime = optimalScheduler.getSolution().getScheduleLength();
        }

        return scheduleFinishTime;
    }


    //region Tests for 10 nodes with 4 processors

    @Test
    public void testParallel10Nodes4PGraph1() throws CommandLineException {
        InputHandler inputHandler = new InputHandler(new String[]{GRAPH1_10NODES, "4","-p","4"});

        Double optimalFinishTime = findParallelOptimalFinishTime(inputHandler.getGraph(),inputHandler.getProcessors(),inputHandler.getCores());

        assertEquals(GRAPH1_10NODES_4P_FINISH_TIME,optimalFinishTime,DELTA);
    }

    @Test
    public void testParallel10Nodes4PGraph2() throws CommandLineException {
        InputHandler inputHandler = new InputHandler(new String[]{GRAPH2_10NODES, "4","-p","4"});

        Double optimalFinishTime = findParallelOptimalFinishTime(inputHandler.getGraph(),inputHandler.getProcessors(),inputHandler.getCores());

        assertEquals(GRAPH2_10NODES_4P_FINISH_TIME,optimalFinishTime,DELTA);
    }

    @Test
    public void testParallel10Nodes4PGraph3() throws CommandLineException {
        InputHandler inputHandler = new InputHandler(new String[]{GRAPH3_10NODES, "4","-p","4"});

        Double optimalFinishTime = findParallelOptimalFinishTime(inputHandler.getGraph(),inputHandler.getProcessors(),inputHandler.getCores());

        assertEquals(GRAPH3_10NODES_4P_FINISH_TIME,optimalFinishTime,DELTA);
    }

    @Test
    public void testParallel10Nodes4PGraph4() throws CommandLineException {
        InputHandler inputHandler = new InputHandler(new String[]{GRAPH4_10NODES, "4","-p","4"});

        Double optimalFinishTime = findParallelOptimalFinishTime(inputHandler.getGraph(),inputHandler.getProcessors(),inputHandler.getCores());

        assertEquals(GRAPH4_10NODES_4P_FINISH_TIME,optimalFinishTime,DELTA);
    }

    @Test
    public void testParallel10Nodes4PGraph5() throws CommandLineException {
        InputHandler inputHandler = new InputHandler(new String[]{GRAPH5_10NODES, "4","-p","4"});

        Double optimalFinishTime = findParallelOptimalFinishTime(inputHandler.getGraph(),inputHandler.getProcessors(),inputHandler.getCores());

        assertEquals(GRAPH5_10NODES_4P_FINISH_TIME,optimalFinishTime,DELTA);
    }

    @Test
    public void testParallel10Nodes4PGraph6() throws CommandLineException {
        InputHandler inputHandler = new InputHandler(new String[]{GRAPH6_10NODES, "4","-p","4"});

        Double optimalFinishTime = findParallelOptimalFinishTime(inputHandler.getGraph(),inputHandler.getProcessors(),inputHandler.getCores());

        assertEquals(GRAPH6_10NODES_4P_FINISH_TIME,optimalFinishTime,DELTA);
    }

    @Test
    public void testParallel10Nodes4PGraph7() throws CommandLineException {
        InputHandler inputHandler = new InputHandler(new String[]{GRAPH7_10NODES, "4","-p","4"});

        Double optimalFinishTime = findParallelOptimalFinishTime(inputHandler.getGraph(),inputHandler.getProcessors(),inputHandler.getCores());

        assertEquals(GRAPH7_10NODES_4P_FINISH_TIME,optimalFinishTime,DELTA);
    }

    @Test
    public void testParallel10Nodes4PGraph8() throws CommandLineException {
        InputHandler inputHandler = new InputHandler(new String[]{GRAPH8_10NODES, "4","-p","4"});

        Double optimalFinishTime = findParallelOptimalFinishTime(inputHandler.getGraph(),inputHandler.getProcessors(),inputHandler.getCores());

        assertEquals(GRAPH8_10NODES_4P_FINISH_TIME,optimalFinishTime,DELTA);
    }

    @Test
    public void testParallel10Nodes4PGraph9() throws CommandLineException {
        InputHandler inputHandler = new InputHandler(new String[]{GRAPH9_10NODES, "4","-p","4"});

        Double optimalFinishTime = findParallelOptimalFinishTime(inputHandler.getGraph(),inputHandler.getProcessors(),inputHandler.getCores());

        assertEquals(GRAPH9_10NODES_4P_FINISH_TIME,optimalFinishTime,DELTA);
    }

    @Test
    public void testParallel10Nodes4PGraph10() throws CommandLineException {
        InputHandler inputHandler = new InputHandler(new String[]{GRAPH10_10NODES, "4","-p","4"});

        Double optimalFinishTime = findParallelOptimalFinishTime(inputHandler.getGraph(),inputHandler.getProcessors(),inputHandler.getCores());

        assertEquals(GRAPH10_10NODES_4P_FINISH_TIME,optimalFinishTime,DELTA);
    }

    @Test
    public void testParallel10Nodes4PGraph11() throws CommandLineException {
        InputHandler inputHandler = new InputHandler(new String[]{GRAPH11_10NODES, "4","-p","4"});

        Double optimalFinishTime = findParallelOptimalFinishTime(inputHandler.getGraph(),inputHandler.getProcessors(),inputHandler.getCores());

        assertEquals(GRAPH11_10NODES_4P_FINISH_TIME,optimalFinishTime,DELTA);
    }

    @Test
    public void testParallel10Nodes4PGraph12() throws CommandLineException {
        InputHandler inputHandler = new InputHandler(new String[]{GRAPH12_10NODES, "4","-p","4"});

        Double optimalFinishTime = findParallelOptimalFinishTime(inputHandler.getGraph(),inputHandler.getProcessors(),inputHandler.getCores());

        assertEquals(GRAPH12_10NODES_4P_FINISH_TIME,optimalFinishTime,DELTA);
    }

    @Test
    public void testParallel10Nodes4PGraph13() throws CommandLineException {
        InputHandler inputHandler = new InputHandler(new String[]{GRAPH13_10NODES, "4","-p","4"});

        Double optimalFinishTime = findParallelOptimalFinishTime(inputHandler.getGraph(),inputHandler.getProcessors(),inputHandler.getCores());

        assertEquals(GRAPH13_10NODES_4P_FINISH_TIME,optimalFinishTime,DELTA);
    }

    @Test
    public void testParallel10Nodes4PGraph14() throws CommandLineException {
        InputHandler inputHandler = new InputHandler(new String[]{GRAPH14_10NODES, "4","-p","4"});

        Double optimalFinishTime = findParallelOptimalFinishTime(inputHandler.getGraph(),inputHandler.getProcessors(),inputHandler.getCores());

        assertEquals(GRAPH14_10NODES_4P_FINISH_TIME,optimalFinishTime,DELTA);
    }

    //endregion

    //region Tests for 10 nodes with 8 processors

    @Test
    public void testParallel10Nodes8PGraph1() throws CommandLineException {
        InputHandler inputHandler = new InputHandler(new String[]{GRAPH1_10NODES, "8","-p","4"});

        Double optimalFinishTime = findParallelOptimalFinishTime(inputHandler.getGraph(),inputHandler.getProcessors(),inputHandler.getCores());

        assertEquals(GRAPH1_10NODES_8P_FINISH_TIME,optimalFinishTime,DELTA);
    }

    @Test
    public void testParallel10Nodes8PGraph2() throws CommandLineException {
        InputHandler inputHandler = new InputHandler(new String[]{GRAPH2_10NODES, "8","-p","4"});

        Double optimalFinishTime = findParallelOptimalFinishTime(inputHandler.getGraph(),inputHandler.getProcessors(),inputHandler.getCores());

        assertEquals(GRAPH2_10NODES_8P_FINISH_TIME,optimalFinishTime,DELTA);
    }

    @Test
    public void testParallel10Nodes8PGraph3() throws CommandLineException {
        InputHandler inputHandler = new InputHandler(new String[]{GRAPH3_10NODES, "8","-p","4"});

        Double optimalFinishTime = findParallelOptimalFinishTime(inputHandler.getGraph(),inputHandler.getProcessors(),inputHandler.getCores());

        assertEquals(GRAPH3_10NODES_8P_FINISH_TIME,optimalFinishTime,DELTA);
    }

    @Test
    public void testParallel10Nodes8PGraph4() throws CommandLineException {
        InputHandler inputHandler = new InputHandler(new String[]{GRAPH4_10NODES, "8","-p","4"});

        Double optimalFinishTime = findParallelOptimalFinishTime(inputHandler.getGraph(),inputHandler.getProcessors(),inputHandler.getCores());

        assertEquals(GRAPH4_10NODES_8P_FINISH_TIME,optimalFinishTime,DELTA);
    }

    @Test
    public void testParallel10Nodes8PGraph5() throws CommandLineException {
        InputHandler inputHandler = new InputHandler(new String[]{GRAPH5_10NODES, "8","-p","4"});

        Double optimalFinishTime = findParallelOptimalFinishTime(inputHandler.getGraph(),inputHandler.getProcessors(),inputHandler.getCores());

        assertEquals(GRAPH5_10NODES_8P_FINISH_TIME,optimalFinishTime,DELTA);
    }

    @Test
    public void testParallel10Nodes8PGraph6() throws CommandLineException {
        InputHandler inputHandler = new InputHandler(new String[]{GRAPH6_10NODES, "8","-p","4"});

        Double optimalFinishTime = findParallelOptimalFinishTime(inputHandler.getGraph(),inputHandler.getProcessors(),inputHandler.getCores());

        assertEquals(GRAPH6_10NODES_8P_FINISH_TIME,optimalFinishTime,DELTA);
    }

    @Test
    public void testParallel10Nodes8PGraph7() throws CommandLineException {
        InputHandler inputHandler = new InputHandler(new String[]{GRAPH7_10NODES, "8","-p","4"});

        Double optimalFinishTime = findParallelOptimalFinishTime(inputHandler.getGraph(),inputHandler.getProcessors(),inputHandler.getCores());

        assertEquals(GRAPH7_10NODES_8P_FINISH_TIME,optimalFinishTime,DELTA);
    }

    @Test
    public void testParallel10Nodes8PGraph8() throws CommandLineException {
        InputHandler inputHandler = new InputHandler(new String[]{GRAPH8_10NODES, "8","-p","4"});

        Double optimalFinishTime = findParallelOptimalFinishTime(inputHandler.getGraph(),inputHandler.getProcessors(),inputHandler.getCores());

        assertEquals(GRAPH8_10NODES_8P_FINISH_TIME,optimalFinishTime,DELTA);
    }

    @Test
    public void testParallel10Nodes8PGraph9() throws CommandLineException {
        InputHandler inputHandler = new InputHandler(new String[]{GRAPH9_10NODES, "8","-p","4"});

        Double optimalFinishTime = findParallelOptimalFinishTime(inputHandler.getGraph(),inputHandler.getProcessors(),inputHandler.getCores());

        assertEquals(GRAPH9_10NODES_8P_FINISH_TIME,optimalFinishTime,DELTA);
    }

    @Test
    public void testParallel10Nodes8PGraph10() throws CommandLineException {
        InputHandler inputHandler = new InputHandler(new String[]{GRAPH10_10NODES, "8","-p","4"});

        Double optimalFinishTime = findParallelOptimalFinishTime(inputHandler.getGraph(),inputHandler.getProcessors(),inputHandler.getCores());

        assertEquals(GRAPH10_10NODES_8P_FINISH_TIME,optimalFinishTime,DELTA);
    }

    @Test
    public void testParallel10Nodes8PGraph11() throws CommandLineException {
        InputHandler inputHandler = new InputHandler(new String[]{GRAPH11_10NODES, "8","-p","4"});

        Double optimalFinishTime = findParallelOptimalFinishTime(inputHandler.getGraph(),inputHandler.getProcessors(),inputHandler.getCores());

        assertEquals(GRAPH11_10NODES_8P_FINISH_TIME,optimalFinishTime,DELTA);
    }

    @Test
    public void testParallel10Nodes8PGraph12() throws CommandLineException {
        InputHandler inputHandler = new InputHandler(new String[]{GRAPH12_10NODES, "8","-p","4"});

        Double optimalFinishTime = findParallelOptimalFinishTime(inputHandler.getGraph(),inputHandler.getProcessors(),inputHandler.getCores());

        assertEquals(GRAPH12_10NODES_8P_FINISH_TIME,optimalFinishTime,DELTA);
    }

    @Test
    public void testParallel10Nodes8PGraph13() throws CommandLineException {
        InputHandler inputHandler = new InputHandler(new String[]{GRAPH13_10NODES, "8","-p","4"});

        Double optimalFinishTime = findParallelOptimalFinishTime(inputHandler.getGraph(),inputHandler.getProcessors(),inputHandler.getCores());

        assertEquals(GRAPH13_10NODES_8P_FINISH_TIME,optimalFinishTime,DELTA);
    }

    @Test
    public void testParallel10Nodes8PGraph14() throws CommandLineException {
        InputHandler inputHandler = new InputHandler(new String[]{GRAPH14_10NODES, "8","-p","4"});

        Double optimalFinishTime = findParallelOptimalFinishTime(inputHandler.getGraph(),inputHandler.getProcessors(),inputHandler.getCores());

        assertEquals(GRAPH14_10NODES_8P_FINISH_TIME,optimalFinishTime,DELTA);
    }
    //endregion

}
