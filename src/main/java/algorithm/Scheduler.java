package algorithm;

import domain.PartialSchedule;
import domain.TaskNode;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

import java.util.*;

public interface Scheduler {

    public boolean executeBranchAndBoundAlgorithm(double initialBoundValue, Map<TaskNode, Double> bottomLevels);
    public PartialSchedule getSolution();
}
