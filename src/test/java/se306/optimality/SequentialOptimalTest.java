package se306.optimality;

import algorithm.GreedyScheduler;
import algorithm.SequentialOptimalScheduler;
import io.CommandLineException;
import io.InputHandler;
import io.OutputHandler;
import javafx.InfoTracker;
import org.graphstream.graph.implementations.AdjacencyListGraph;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static se306.optimality.OptimalityTestConstants.*;

public class SequentialOptimalTest {

    private static final double DELTA = 1e-15;

    public double findOptimalFinishTime(InputHandler inputHandler) {
        GreedyScheduler greedyScheduler = new GreedyScheduler(inputHandler.getGraph(), inputHandler.getProcessors());
        greedyScheduler.executeAlgorithm();

        double scheduleFinishTime = greedyScheduler.getSolutionLength();

        InfoTracker it = new InfoTracker(inputHandler.getFileName(), inputHandler.getProcessors(), inputHandler.getCores(), inputHandler.getGraph());

        SequentialOptimalScheduler optimalScheduler = new SequentialOptimalScheduler(greedyScheduler.getTopologicallyOrderedTaskNodes(), it);
        boolean moreOptimalFound = optimalScheduler.executeBranchAndBoundAlgorithm(greedyScheduler.getSolutionLength());

        if (moreOptimalFound) {
            scheduleFinishTime = optimalScheduler.getSolution().getScheduleLength();
        }

        return scheduleFinishTime;
    }

    //region Tests for 10 nodes with 2 processors
    @Test
    public void test10Nodes2PGraph1() throws CommandLineException {
        InputHandler inputHandler = new InputHandler(new String[]{GRAPH1_10NODES, "2"});

        Double optimalFinishTime = findOptimalFinishTime(inputHandler);

        assertEquals(GRAPH1_10NODES_2P_FINISH_TIME,optimalFinishTime,DELTA);
    }

    @Test
    public void test10Nodes2PGraph2() throws CommandLineException {
        InputHandler inputHandler = new InputHandler(new String[]{GRAPH2_10NODES, "2"});

        Double optimalFinishTime = findOptimalFinishTime(inputHandler);

        assertEquals(GRAPH2_10NODES_2P_FINISH_TIME,optimalFinishTime,DELTA);
    }

    @Test
    public void test10Nodes2PGraph3() throws CommandLineException {
        InputHandler inputHandler = new InputHandler(new String[]{GRAPH3_10NODES, "2"});

        Double optimalFinishTime = findOptimalFinishTime(inputHandler);

        assertEquals(GRAPH3_10NODES_2P_FINISH_TIME,optimalFinishTime,DELTA);
    }

    @Test
    public void test10Nodes2PGraph4() throws CommandLineException {
        InputHandler inputHandler = new InputHandler(new String[]{GRAPH4_10NODES, "2"});

        Double optimalFinishTime = findOptimalFinishTime(inputHandler);

        assertEquals(GRAPH4_10NODES_2P_FINISH_TIME,optimalFinishTime,DELTA);
    }

    @Test
    public void test10Nodes2PGraph5() throws CommandLineException {
        InputHandler inputHandler = new InputHandler(new String[]{GRAPH5_10NODES, "2"});

        Double optimalFinishTime = findOptimalFinishTime(inputHandler);

        assertEquals(GRAPH5_10NODES_2P_FINISH_TIME,optimalFinishTime,DELTA);
    }

    @Test
    public void test10Nodes2PGraph6() throws CommandLineException {
        InputHandler inputHandler = new InputHandler(new String[]{GRAPH6_10NODES, "2"});

        Double optimalFinishTime = findOptimalFinishTime(inputHandler);

        assertEquals(GRAPH6_10NODES_2P_FINISH_TIME,optimalFinishTime,DELTA);
    }

    @Test
    public void test10Nodes2PGraph7() throws CommandLineException {
        InputHandler inputHandler = new InputHandler(new String[]{GRAPH7_10NODES, "2"});

        Double optimalFinishTime = findOptimalFinishTime(inputHandler);

        assertEquals(GRAPH7_10NODES_2P_FINISH_TIME,optimalFinishTime,DELTA);
    }

    @Test
    public void test10Nodes2PGraph8() throws CommandLineException {
        InputHandler inputHandler = new InputHandler(new String[]{GRAPH8_10NODES, "2"});

        Double optimalFinishTime = findOptimalFinishTime(inputHandler);

        assertEquals(GRAPH8_10NODES_2P_FINISH_TIME,optimalFinishTime,DELTA);
    }

    @Test
    public void test10Nodes2PGraph9() throws CommandLineException {
        InputHandler inputHandler = new InputHandler(new String[]{GRAPH9_10NODES, "2"});

        Double optimalFinishTime = findOptimalFinishTime(inputHandler);

        assertEquals(GRAPH9_10NODES_2P_FINISH_TIME,optimalFinishTime,DELTA);
    }

    @Test
    public void test10Nodes2PGraph10() throws CommandLineException {
        InputHandler inputHandler = new InputHandler(new String[]{GRAPH10_10NODES, "2"});

        Double optimalFinishTime = findOptimalFinishTime(inputHandler);

        assertEquals(GRAPH10_10NODES_2P_FINISH_TIME,optimalFinishTime,DELTA);
    }

    @Test
    public void test10Nodes2PGraph11() throws CommandLineException {
        InputHandler inputHandler = new InputHandler(new String[]{GRAPH11_10NODES, "2"});

        Double optimalFinishTime = findOptimalFinishTime(inputHandler);

        assertEquals(GRAPH11_10NODES_2P_FINISH_TIME,optimalFinishTime,DELTA);
    }

    @Test
    public void test10Nodes2PGraph12() throws CommandLineException {
        InputHandler inputHandler = new InputHandler(new String[]{GRAPH12_10NODES, "2"});

        Double optimalFinishTime = findOptimalFinishTime(inputHandler);

        assertEquals(GRAPH12_10NODES_2P_FINISH_TIME,optimalFinishTime,DELTA);
    }

    @Test
    public void test10Nodes2PGraph13() throws CommandLineException {
        InputHandler inputHandler = new InputHandler(new String[]{GRAPH13_10NODES, "2"});

        Double optimalFinishTime = findOptimalFinishTime(inputHandler);

        assertEquals(GRAPH13_10NODES_2P_FINISH_TIME,optimalFinishTime,DELTA);
    }

    @Test
    public void test10Nodes2PGraph14() throws CommandLineException {
        InputHandler inputHandler = new InputHandler(new String[]{GRAPH14_10NODES, "2"});

        Double optimalFinishTime = findOptimalFinishTime(inputHandler);

        assertEquals(GRAPH14_10NODES_2P_FINISH_TIME,optimalFinishTime,DELTA);
    }

    @Test
    public void test10Nodes2PGraph15() throws CommandLineException {
        InputHandler inputHandler = new InputHandler(new String[]{GRAPH15_10NODES, "2"});

        Double optimalFinishTime = findOptimalFinishTime(inputHandler);

        assertEquals(GRAPH15_10NODES_2P_FINISH_TIME,optimalFinishTime,DELTA);
    }
    //endregion

    //region Tests for 10 nodes with 4 processors

    @Test
    public void test10Nodes4PGraph1() throws CommandLineException {
        InputHandler inputHandler = new InputHandler(new String[]{GRAPH1_10NODES, "4"});

        Double optimalFinishTime = findOptimalFinishTime(inputHandler);

        assertEquals(GRAPH1_10NODES_4P_FINISH_TIME,optimalFinishTime,DELTA);
    }

    @Test
    public void test10Nodes4PGraph2() throws CommandLineException {
        InputHandler inputHandler = new InputHandler(new String[]{GRAPH2_10NODES, "4"});

        Double optimalFinishTime = findOptimalFinishTime(inputHandler);

        assertEquals(GRAPH2_10NODES_4P_FINISH_TIME,optimalFinishTime,DELTA);
    }

    @Test
    public void test10Nodes4PGraph3() throws CommandLineException {
        InputHandler inputHandler = new InputHandler(new String[]{GRAPH3_10NODES, "4"});

        Double optimalFinishTime = findOptimalFinishTime(inputHandler);

        assertEquals(GRAPH3_10NODES_4P_FINISH_TIME,optimalFinishTime,DELTA);
    }

    @Test
    public void test10Nodes4PGraph4() throws CommandLineException {
        InputHandler inputHandler = new InputHandler(new String[]{GRAPH4_10NODES, "4"});

        Double optimalFinishTime = findOptimalFinishTime(inputHandler);

        assertEquals(GRAPH4_10NODES_4P_FINISH_TIME,optimalFinishTime,DELTA);
    }

    @Test
    public void test10Nodes4PGraph5() throws CommandLineException {
        InputHandler inputHandler = new InputHandler(new String[]{GRAPH5_10NODES, "4"});

        Double optimalFinishTime = findOptimalFinishTime(inputHandler);

        assertEquals(GRAPH5_10NODES_4P_FINISH_TIME,optimalFinishTime,DELTA);
    }

    @Test
    public void test10Nodes4PGraph6() throws CommandLineException {
        InputHandler inputHandler = new InputHandler(new String[]{GRAPH6_10NODES, "4"});

        Double optimalFinishTime = findOptimalFinishTime(inputHandler);

        assertEquals(GRAPH6_10NODES_4P_FINISH_TIME,optimalFinishTime,DELTA);
    }

    @Test
    public void test10Nodes4PGraph7() throws CommandLineException {
        InputHandler inputHandler = new InputHandler(new String[]{GRAPH7_10NODES, "4"});

        Double optimalFinishTime = findOptimalFinishTime(inputHandler);

        assertEquals(GRAPH7_10NODES_4P_FINISH_TIME,optimalFinishTime,DELTA);
    }

    @Test
    public void test10Nodes4PGraph8() throws CommandLineException {
        InputHandler inputHandler = new InputHandler(new String[]{GRAPH8_10NODES, "4"});

        Double optimalFinishTime = findOptimalFinishTime(inputHandler);

        assertEquals(GRAPH8_10NODES_4P_FINISH_TIME,optimalFinishTime,DELTA);
    }

    @Test
    public void test10Nodes4PGraph9() throws CommandLineException {
        InputHandler inputHandler = new InputHandler(new String[]{GRAPH9_10NODES, "4"});

        Double optimalFinishTime = findOptimalFinishTime(inputHandler);

        assertEquals(GRAPH9_10NODES_4P_FINISH_TIME,optimalFinishTime,DELTA);
    }

    @Test
    public void test10Nodes4PGraph10() throws CommandLineException {
        InputHandler inputHandler = new InputHandler(new String[]{GRAPH10_10NODES, "4"});

        Double optimalFinishTime = findOptimalFinishTime(inputHandler);

        assertEquals(GRAPH10_10NODES_4P_FINISH_TIME,optimalFinishTime,DELTA);
    }

    @Test
    public void test10Nodes4PGraph11() throws CommandLineException {
        InputHandler inputHandler = new InputHandler(new String[]{GRAPH11_10NODES, "4"});

        Double optimalFinishTime = findOptimalFinishTime(inputHandler);

        assertEquals(GRAPH11_10NODES_4P_FINISH_TIME,optimalFinishTime,DELTA);
    }

    @Test
    public void test10Nodes4PGraph12() throws CommandLineException {
        InputHandler inputHandler = new InputHandler(new String[]{GRAPH12_10NODES, "4"});

        Double optimalFinishTime = findOptimalFinishTime(inputHandler);

        assertEquals(GRAPH12_10NODES_4P_FINISH_TIME,optimalFinishTime,DELTA);
    }

    @Test
    public void test10Nodes4PGraph13() throws CommandLineException {
        InputHandler inputHandler = new InputHandler(new String[]{GRAPH13_10NODES, "4"});

        Double optimalFinishTime = findOptimalFinishTime(inputHandler);

        assertEquals(GRAPH13_10NODES_4P_FINISH_TIME,optimalFinishTime,DELTA);
    }

    @Test
    public void test10Nodes4PGraph14() throws CommandLineException {
        InputHandler inputHandler = new InputHandler(new String[]{GRAPH14_10NODES, "4"});

        Double optimalFinishTime = findOptimalFinishTime(inputHandler);

        assertEquals(GRAPH14_10NODES_4P_FINISH_TIME,optimalFinishTime,DELTA);
    }

    @Test
    public void test10Nodes4PGraph15() throws CommandLineException {
        InputHandler inputHandler = new InputHandler(new String[]{GRAPH15_10NODES, "4"});

        Double optimalFinishTime = findOptimalFinishTime(inputHandler);

        assertEquals(GRAPH15_10NODES_4P_FINISH_TIME,optimalFinishTime,DELTA);
    }

    //endregion
}
