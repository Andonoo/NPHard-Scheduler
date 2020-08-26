package algorithm;

import domain.PartialSchedule;
import domain.TaskNode;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

import java.util.*;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/**
 * Class implementing an optimal scheduling algorithm based on an exhaustive branch and bound search.
 */
public class ParallelOptimalScheduler {

    private static final int THREAD_DEPTH = 10;
    private static final int NUM_RUNTIME_PROCESSORS = 4;

    private final List<TaskNode> _rootNodes;
    private final int _numProcessors;
    private PartialSchedule _solution = null;
    private double globalBound;

    public ParallelOptimalScheduler(Node[] topologicalOrderedTasks, int numProcessors) {
        _numProcessors = numProcessors;
        _rootNodes = new ArrayList<TaskNode>();
        populateTaskNodes(topologicalOrderedTasks);
    }

    /**
     * Executes the branch and bound scheduling algorithm on the provided input graph.
     * @param initialBoundValue Value used to bound search for schedules
     * @return Returns true if a schedule was found which is shorter than the provided bound value
     */
    public boolean executeBranchAndBoundAlgorithm(double initialBoundValue) {
        // Initializing the search tree with a partial schedule for each root node
        LinkedList<PartialSchedule> searchTree = new LinkedList<PartialSchedule>();
        globalBound = initialBoundValue;

        for (TaskNode rootNode: _rootNodes) {
            List<TaskNode> canBeScheduled = new ArrayList<TaskNode>(_rootNodes);
            canBeScheduled.remove(rootNode);

            PartialSchedule rootSchedule = new PartialSchedule(_numProcessors, rootNode, canBeScheduled);
            searchTree.push(rootSchedule);
        }

        SearchTask searchFork = new SearchTask(searchTree);
        ForkJoinPool workers = new ForkJoinPool(NUM_RUNTIME_PROCESSORS);
        workers.invoke(searchFork);

        if (_solution == null) {
            return false;
        }
        else {
            return true;
        }
    }

    /**
     * Gets the schedule which was computed by the branch and bound algorithm. Will return null if no schedule was found
     * with a shorter length than the provided initialBoundValue
     * @return
     */
    public PartialSchedule getSolution() {
        return _solution;
    }

    /**
     * Populates the TaskNode data structures which are used in the algorithm execution. Also finds the root nodes which
     * will be used to start the algorithm.
     * @param topologicalOrder
     * @return
     */
    private void populateTaskNodes(Node[] topologicalOrder) {
        Map<String, TaskNode> taskNodes = new HashMap<String, TaskNode>();

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
                _rootNodes.add(taskNode);
            }
        }
    }

    private class SearchTask extends RecursiveAction {

        LinkedList<PartialSchedule> searchTree;
        private double localBound = globalBound;

        private SearchTask(LinkedList<PartialSchedule> searchTree) {
            this.searchTree = searchTree;
        }

        @Override
        protected void compute() {
            // While we have unexplored nodes, continue DFS with bound
            while (!searchTree.isEmpty()) {

                PartialSchedule nodeToExplore = searchTree.pop();

                if (nodeToExplore.isComplete()) {
                    // Update the optimal schedule (shared by all tasks)
                    if (nodeToExplore.getScheduleLength() < localBound) {
                        localBound = nodeToExplore.getScheduleLength();
                        updateGlobal(nodeToExplore, localBound);
                    }
                    continue;
                }

                PartialSchedule[] foundChildren = nodeToExplore.createChildren();

                for (PartialSchedule child: foundChildren) {
                    double childLength = child.getScheduleLength();
                    // Branch by pushing child into search tree or bound
                    if (childLength < localBound) {
                        searchTree.push(child);
                    }
                }

                // If the task takes on too much, delegate some work to another task and queue it in thread
                if (searchTree.size() > THREAD_DEPTH) {
                    LinkedList<PartialSchedule> partitionList = new LinkedList<PartialSchedule>(Arrays.asList(searchTree.removeLast()));
                    invokeAll(new SearchTask(searchTree), new SearchTask(partitionList));
                }
            }
        }
    }

    private synchronized void updateGlobal(PartialSchedule localSchedule, double localBound) {
        // Double check in the case of asynchronicity
        if (localSchedule.getScheduleLength() < globalBound) {
            _solution = localSchedule;
            globalBound = localBound;
        }
    }
}
