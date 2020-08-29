package algorithm;

import domain.PartialSchedule;
import domain.TaskNode;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

import java.util.*;

public abstract class Scheduler {

    /**
     * Populates the TaskNode data structures which are used in the algorithm execution. Also finds the root nodes which
     * will be used to start the algorithm.
     * @param topologicalOrder
     * @return
     */
    List<TaskNode> populateTaskNodes(Node[] topologicalOrder) {
        Map<String, TaskNode> taskNodes = new HashMap<String, TaskNode>();
        List<TaskNode> rootNodes = new ArrayList<TaskNode>();

        for (Node task: topologicalOrder) {
            // Populating this TaskNodes set of dependencies, as well as the Map containing the dependency weights
            Collection<Edge> dependencies = task.getEnteringEdgeSet();
            TaskNode[] taskNodeDependencies = new TaskNode[dependencies.size()];
            Map<TaskNode, Double> dependencyWeights = new HashMap<TaskNode, Double>();
            int i = 0;
            for (Edge dependency: dependencies) {
                TaskNode dependencyTaskNode = taskNodes.get(dependency.getSourceNode().getId());
                dependencyWeights.put(dependencyTaskNode, (double)((Integer)dependency.getAttribute("Weight")));
                taskNodeDependencies[i] = dependencyTaskNode;
                i++;
            }

            // Creating the TaskNode, and adding it to the set
            TaskNode taskNode = new TaskNode(task.getId(), taskNodeDependencies, (double)((Integer)task.getAttribute("Weight")), dependencyWeights);
            taskNodes.put(task.getId(), taskNode);

            // Adding this TaskNode as a dependent where necessary
            for (Edge dependency: dependencies) {
                TaskNode dependencyTaskNode = taskNodes.get(dependency.getSourceNode().getId());
                dependencyTaskNode.addDependent(taskNode);
            }

            // Checking to see if this task is a root
            if (task.getInDegree() == 0) {
                rootNodes.add(taskNode);
            }
        }
        return rootNodes;
    }

    public abstract boolean executeBranchAndBoundAlgorithm(double initialBoundValue);

    public abstract PartialSchedule getSolution();
}
