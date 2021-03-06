package algorithm;

import domain.DomainHandler;
import domain.PartialSchedule;
import domain.TaskNode;
import javafx.InfoTracker;

import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/**
 * Class implementing an optimal scheduling algorithm based on an exhaustive branch and bound search.
 */
public class ParallelOptimalScheduler implements Scheduler {

    private static final int THREAD_DEPTH = 10;
    private static final int FREE_MEMORY_LIMIT = 100000000;

    private final int _numCores;
    private final List<TaskNode> _rootNodes;
    private final int _numProcessors;
    private PartialSchedule _solution = null;
    private final List<TaskNode> _topologicalOrderedTasks;
    private Map<TaskNode, Double> _bottomLevels;
    private double _globalBound;
    private Set<String> _syncExploredScheduleIds;
    private InfoTracker _infoTracker = null;
    private volatile int _searchesMade = 0;

    /**
     * Constructor for which to create the parallel scheduler
     *
     * @param topologicallyOrderedTaskNodes
     * @param numProcessors
     * @param numCores
     */
    public ParallelOptimalScheduler(List<TaskNode> topologicallyOrderedTaskNodes, int numProcessors, int numCores) {
        _numCores = numCores;
        _numProcessors = numProcessors;
        _topologicalOrderedTasks = topologicallyOrderedTaskNodes;
        _rootNodes = DomainHandler.findRootNodes(_topologicalOrderedTasks);
    }

    /**
     * Constructor for which an info tracker is implemented to the class (to concurrently update the GUI)
     *
     * @param topologicallyOrderedTaskNodes
     * @param numProcessors
     * @param numCores
     * @param infoTracker
     */
    public ParallelOptimalScheduler(List<TaskNode> topologicallyOrderedTaskNodes, int numProcessors, int numCores, InfoTracker infoTracker) {
        _numCores = numCores;
        _numProcessors = numProcessors;
        _topologicalOrderedTasks = topologicallyOrderedTaskNodes;
        _rootNodes = DomainHandler.findRootNodes(_topologicalOrderedTasks);
        _infoTracker = infoTracker;
    }

    /**
     * Executes the branch and bound scheduling algorithm on the provided input graph.
     *
     * @param initialBoundValue Value used to bound search for schedules
     * @return Returns true if a schedule was found which is shorter than the provided bound value
     */
    public boolean executeBranchAndBoundAlgorithm(double initialBoundValue, Map<TaskNode, Double> bottomLevels) {
        _bottomLevels = bottomLevels;

        // Initializing the search tree with a partial schedule for each root node
        LinkedList<PartialSchedule> searchTree = new LinkedList<PartialSchedule>();
        _globalBound = initialBoundValue;

        // Instantiating a synchronized set to track explored PartialSchedules
        _syncExploredScheduleIds = Collections.synchronizedSet(new HashSet<String>());

        for (TaskNode rootNode : _rootNodes) {
            List<TaskNode> canBeScheduled = new ArrayList<TaskNode>(_rootNodes);
            canBeScheduled.remove(rootNode);

            PartialSchedule rootSchedule = new PartialSchedule(_numProcessors, rootNode, canBeScheduled, bottomLevels);
            searchTree.push(rootSchedule);
        }

        // Declare task to be run concurrently on a pool of worker threads
        BranchAndBoundTask task = new BranchAndBoundTask(searchTree);
        ForkJoinPool workers = new ForkJoinPool(_numCores);
        workers.invoke(task);

        return _solution != null;
    }

    /**
     * Gets the schedule which was computed by the branch and bound algorithm. Will return null if no schedule was found
     * with a shorter length than the provided initialBoundValue
     *
     * @return
     */
    public PartialSchedule getSolution() {
        return _solution;
    }

    /**
     * A nested inner class which computes the branch and bound algorithm. It is called recursively and concurrently.
     */
    private class BranchAndBoundTask extends RecursiveAction {

        LinkedList<PartialSchedule> searchTree;
        private double localBound = _globalBound;

        private BranchAndBoundTask(LinkedList<PartialSchedule> searchTree) {
            this.searchTree = searchTree;
        }

        /**
         * Main computation that will be recursively called. It continuously performs a DFS search on a list of partial schedules,
         * thereby adding and removing to the list. When the list gets too long, a new task/thread is invoked.
         */
        @Override
        protected void compute() {

            // While we have unexplored nodes, continue DFS with bound
            while (!searchTree.isEmpty()) {

                PartialSchedule nodeToExplore = searchTree.pop();
                PartialSchedule[] foundChildren = nodeToExplore.createChildren(_bottomLevels);

                // Ensure that localBound is always updated in the while loop to lower search space.
                localBound = Math.min(localBound, _globalBound);
                for (PartialSchedule child : foundChildren) {
                    double childLength = child.getScheduleLength();

                    if (_infoTracker != null) {
                        updateSearchCount();
                    }

                    if (child.isComplete()) {
                        // Update the solution if this child is more optimal
                        if (child.getScheduleLength() < localBound) {
                            localBound = child.getScheduleLength();
                            updateGlobal(child, localBound);
                        }
                        // Go to next child
                        continue;
                    }

                    // Branch by pushing child into search tree or bound
                    if (childLength < localBound && child.getEstimatedFinish() < localBound && !_syncExploredScheduleIds.contains(child.getScheduleId())) {
                        searchTree.addFirst(child);

                        // As storing the explored schedules may result in overflow, we must detect and avoid this
                        if (Runtime.getRuntime().freeMemory() > FREE_MEMORY_LIMIT) {
                            _syncExploredScheduleIds.add(child.getScheduleId());
                        } else {
                            _syncExploredScheduleIds.clear();
                        }
                    }
                }

                // If the task takes on too much computation, delegate some work to another task and queue it in the pool of worker threads
                if (searchTree.size() > THREAD_DEPTH) {
                    LinkedList<PartialSchedule> partitionList = new LinkedList<PartialSchedule>(Arrays.asList(searchTree.removeLast()));
                    invokeAll(new BranchAndBoundTask(searchTree), new BranchAndBoundTask(partitionList));
                }
            }
        }
    }

    /**
     * Updates the global shared solution synchronously
     *
     * @param localSchedule
     * @param localBound
     * @return
     */
    private synchronized void updateGlobal(PartialSchedule localSchedule, double localBound) {
        // Double check in the case of asynchronicity
        if (localSchedule.getScheduleLength() < _globalBound) {
            _solution = localSchedule;
            _globalBound = localBound;
            // Update GUI to show current best schedule
            if (_infoTracker != null) {
                _infoTracker.setCurrentBestHasChanged(true);
                _infoTracker.setCurrentBest((int) localSchedule.getScheduleLength());
                _infoTracker.setScheduledToBeDisplayed(localSchedule);
            }
        }
    }

    /**
     * Updates the search count synchronously for the GUI to visualise
     */
    private synchronized void updateSearchCount() {
        _searchesMade++;
        _infoTracker.setSearchesMade(_searchesMade);
    }
}
