package algorithm;

import domain.PartialSchedule;
import domain.TaskNode;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

import java.util.*;

/**
 * Class implementing an optimal scheduling algorithm based on an exhaustive branch and bound search.
 */
public class SequentialOptimalScheduler {

    private final Collection<TaskNode>  _tasksToBeScheduled;
    private final List<TaskNode> _rootNodes;
    private final int _numProcessors;

    private PartialSchedule _solution;

    public SequentialOptimalScheduler(Node[] topologicalOrderedTasks, int numProcessors) {
        _numProcessors = numProcessors;
        _rootNodes = new ArrayList<TaskNode>();
        _tasksToBeScheduled = populateTaskNodes(topologicalOrderedTasks);
    }

    /**
     * Executes the branch and bound scheduling algorithm on the provided input graph.
     */
    public void executeBranchAndBoundAlgorithm(double initialBoundValue) {
        // Initializing the search tree with a partial schedule for each root node
        Stack<PartialSchedule> searchTree = new Stack<PartialSchedule>();
        for (TaskNode rootNode: _rootNodes) {
            List<TaskNode> canBeScheduled = new ArrayList<TaskNode>(_rootNodes);
            canBeScheduled.remove(rootNode);

            PartialSchedule rootSchedule = new PartialSchedule(_numProcessors, rootNode, canBeScheduled);
            searchTree.push(rootSchedule);
        }

        double boundValue = initialBoundValue;
        PartialSchedule currentBest = null;
        // While we have unexplored nodes, continue DFS with bound
        while (!searchTree.empty()) {
            PartialSchedule nodeToExplore = searchTree.pop();
            PartialSchedule[] foundChildren = nodeToExplore.createChildren();

            for (PartialSchedule child: foundChildren) {
                double childLength = child.getScheduleLength();
                // Check if we've found our new most optimal
                if (child.isComplete() && childLength <= boundValue) {
                    boundValue = childLength;
                    currentBest = child;
                }
                // Branch by pushing child into search tree or bound
                if (childLength < boundValue) {
                    searchTree.push(child);
                }
            }
        }

        _solution = currentBest;
        System.out.println("Schedule length: " +  _solution.getScheduleLength());
    }

    /**
     * Populates the TaskNode data structures which are used in the algorithm execution
     * @param topologicalOrder
     * @return
     */
    private Collection<TaskNode> populateTaskNodes(Node[] topologicalOrder) {
        Map<String, TaskNode> taskNodes = new HashMap<String, TaskNode>();

        for (Node task: topologicalOrder) {
            // Populating this TaskNodes set of dependencies, as well as the Map containing the dependency weights
            Collection<Edge> dependencies = task.getEnteringEdgeSet();
            TaskNode[] taskNodeDependencies = new TaskNode[dependencies.size()];
            Map<TaskNode, Double> dependencyWeights = new HashMap<TaskNode, Double>();
            int i = 0;
            for (Edge dependency: dependencies) {
                TaskNode dependencyTaskNode = taskNodes.get(dependency.getSourceNode().getId());
                // TODO - Work out what's going on with GraphStream parsing Weight into an Integer
                dependencyWeights.put(dependencyTaskNode, (double)((Integer)dependency.getAttribute("Weight")));
                taskNodeDependencies[i] = dependencyTaskNode;
                i++;
            }

            // Creating the TaskNode, and adding it to the set
            // TODO - Work out what's going on with GraphStream parsing Weight into an Integer
            TaskNode taskNode = new TaskNode(task.getId(), taskNodeDependencies, (double)((Integer)task.getAttribute("Weight")), dependencyWeights);
            taskNodes.put(task.getId(), taskNode);

            // Adding this TaskNode as a dependent where necessary
            for (Edge dependency: dependencies) {
                TaskNode dependencyTaskNode = taskNodes.get(dependency.getSourceNode().getId());
                dependencyTaskNode.addDependent(taskNode);
            }

            // Checking to see if this task is a root
            if (task.getInDegree() == 0) {
                _rootNodes.add(taskNode);
            }
        }

        return taskNodes.values();
    }
}
