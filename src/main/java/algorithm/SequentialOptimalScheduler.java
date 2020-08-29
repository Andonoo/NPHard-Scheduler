package algorithm;

import domain.DomainHandler;
import domain.PartialSchedule;
import domain.TaskNode;
import javafx.InfoTracker;

import java.util.*;

/**
 * Class implementing an optimal scheduling algorithm based on an exhaustive branch and bound search.
 */
public class SequentialOptimalScheduler implements Scheduler {

    private List<TaskNode> _rootNodes;
    private final int _numProcessors;
    private PartialSchedule _solution;
    private final InfoTracker _infoTracker;
    private final List<TaskNode> _topologicalOrderedTasks;

    public SequentialOptimalScheduler(List<TaskNode> topologicallyOrderedTaskNodes, int numProcessors, InfoTracker infoTracker) {
        _numProcessors = numProcessors;
        _infoTracker = infoTracker;
        _topologicalOrderedTasks = topologicallyOrderedTaskNodes;
        _rootNodes = DomainHandler.findRootNodes(_topologicalOrderedTasks);
    }

    public SequentialOptimalScheduler(List<TaskNode> topologicallyOrderedTaskNodes, int numProcessors) {
        _numProcessors = numProcessors;
        _infoTracker = null;
        _topologicalOrderedTasks = topologicallyOrderedTaskNodes;
        _rootNodes = DomainHandler.findRootNodes(_topologicalOrderedTasks);
    }

    /**
     * Executes the branch and bound scheduling algorithm on the provided input graph.
     *
     * @param initialBoundValue Value used to bound search for schedules
     * @return Returns true if a schedule was found which is shorter than the provided bound value
     */
    public boolean executeBranchAndBoundAlgorithm(double initialBoundValue) {
        // Initializing the search tree with a partial schedule for each root node
        Stack<PartialSchedule> searchTree = new Stack<PartialSchedule>();
        for (TaskNode rootNode : _rootNodes) {
            List<TaskNode> canBeScheduled = new ArrayList<TaskNode>(_rootNodes);
            canBeScheduled.remove(rootNode);

            PartialSchedule rootSchedule = new PartialSchedule(_numProcessors, rootNode, canBeScheduled, _topologicalOrderedTasks);
            searchTree.push(rootSchedule);
        }

        // We maintain a set of PartialSchedule hashes, so we can detect and avoid duplicates
        Set<String> exploredScheduleIds = new HashSet<String>();

        double boundValue = initialBoundValue;
        PartialSchedule currentBest = null;
        // While we have unexplored nodes, continue DFS with bound
        while (!searchTree.empty()) {
            PartialSchedule nodeToExplore = searchTree.pop();
            PartialSchedule[] foundChildren = nodeToExplore.createChildren(_topologicalOrderedTasks);

            for (PartialSchedule child : foundChildren) {
                double childLength = child.getScheduleLength();

                if (_infoTracker != null) {
                    // Increment searches made to update GUI
                    _infoTracker.setSearchesMade(_infoTracker.getSearchesMade() + 1);
                }

                if (childLength == initialBoundValue && child.isComplete()){
                    System.out.println("yeet");
                }
                // If the greedy schedule is the optimal solution, assign currentBest to it
                if (child.isComplete() && childLength == initialBoundValue) {
                    boundValue = childLength;
                    System.out.println("Justin our lord and saviour");
                    currentBest = child;
                    if(_infoTracker != null) {
                        updateGUI(child.getScheduleLength(), child);
                    }
                }

                // Check if we've found our new most optimal
                if (child.isComplete() && childLength < boundValue) {
                    boundValue = childLength;
                    currentBest = child;
                    if (_infoTracker != null) {
                        updateGUI(child.getScheduleLength(), child);
                    }
                }
                // Branch by pushing child into search tree or bound
                if (!exploredScheduleIds.contains(child.getScheduleId()) && childLength < boundValue && child.getEstimatedFinish() < boundValue) {
                    searchTree.push(child);
                    exploredScheduleIds.add(child.getScheduleId());
                }
            }
        }

        _solution = currentBest;
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

    private void updateGUI(double scheduleLength, PartialSchedule child) {
        _infoTracker.setCurrentBestHasChanged(true);
        _infoTracker.setCurrentBest((int) scheduleLength);
        _infoTracker.setScheduledToBeDisplayed(child);
    }
}