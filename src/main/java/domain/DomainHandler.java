package domain;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

import java.util.*;

public class DomainHandler {

    /**
     * Takes an array of topologically ordered GraphStream nodes, and converts them into a TaskNode representation
     * for use in the scheduling algorithms.
     * @param topologicalOrder
     * @return
     */
    public static List<TaskNode> populateTaskNodes(Node[] topologicalOrder) {
        Map<String, TaskNode> taskNodes = new LinkedHashMap<String, TaskNode>();

        for (Node task: topologicalOrder) {
            // Populating this TaskNodes set of dependencies, as well as the Map containing the dependency weights
            Collection<Edge> dependencies = task.getEnteringEdgeSet();
            TaskNode[] taskNodeDependencies = new TaskNode[dependencies.size()];
            Map<TaskNode, Double> dependencyWeights = new HashMap<TaskNode, Double>();
            int i = 0;
            for (Edge dependency: dependencies) {
                TaskNode dependencyTaskNode = taskNodes.get(dependency.getSourceNode().getId());
                dependencyWeights.put(dependencyTaskNode, (dependency.getAttribute("Weight")));
                taskNodeDependencies[i] = dependencyTaskNode;
                i++;
            }

            // Creating the TaskNode, and adding it to the set
            TaskNode taskNode = new TaskNode(task.getId(), taskNodeDependencies, (task.getAttribute("Weight")), dependencyWeights);
            taskNodes.put(task.getId(), taskNode);

            // Adding this TaskNode as a dependent where necessary
            for (Edge dependency: dependencies) {
                TaskNode dependencyTaskNode = taskNodes.get(dependency.getSourceNode().getId());
                dependencyTaskNode.addDependent(taskNode);
            }
        }

        return new ArrayList<TaskNode>(taskNodes.values());
    }

    /**
     * @param tasks
     * @return Returns the tasks which have no dependencies
     */
    public static List<TaskNode> findRootNodes(List<TaskNode> tasks) {
        List<TaskNode> rootNodes = new ArrayList<TaskNode>();

        for (TaskNode task: tasks) {
            if (task.getDependencies().length == 0) {
                rootNodes.add(task);
            }
        }

        return rootNodes;
    }

    /**
     * Determines the max bottom levels of each node in the input. These represent the longest path from a given
     * node to the a completed schedule state.
     * @param tasks
     * @return
     */
    public static Map<TaskNode, Double> getBottomLevels(List<TaskNode> tasks) {
        Map<TaskNode, Double> minBottomLevels = new HashMap<TaskNode, Double>();

        for (TaskNode task: tasks) {
            if (task.getDependencies().length == 0) {
                bottomLevelRecurse(task, minBottomLevels);
            }
        }

        return minBottomLevels;
    }

    /**
     * Recursive method used in the bottom levelling algorithm. This finds the bottom level for a single TaskNode based
     * on recursive calls.
     * @param task
     * @param bottomLevels
     * @return
     */
    private static double bottomLevelRecurse(TaskNode task, Map<TaskNode, Double> bottomLevels) {
        // Base case - if a node has no dependents, it has 0 bottom level (no nodes need to be scheduled after it)
        if (task.getDependents().size() == 0) {
            bottomLevels.put(task, 0.0);
            return 0.0;
        }

        // Finds the maximum path to an end node
        List<TaskNode> dependents = task.getDependents();
        double maxBottomLevel = 0;
        for (int i = 0; i < dependents.size(); i++) {
            maxBottomLevel = Math.max(maxBottomLevel, bottomLevelRecurse(dependents.get(i), bottomLevels) + dependents.get(i).getWeight());
        }

        bottomLevels.put(task, maxBottomLevel);
        return maxBottomLevel;
    }
}