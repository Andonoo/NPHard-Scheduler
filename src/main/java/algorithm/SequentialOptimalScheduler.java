package algorithm;

import domain.DomainHandler;
import domain.PartialSchedule;
import domain.TaskNode;
import javafx.InfoTracker;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

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

    public SequentialOptimalScheduler(List<TaskNode> topologicalOrderedTasks, InfoTracker infoTracker) {
        _numProcessors = infoTracker.getProcessors();
        _infoTracker = infoTracker;
        _topologicalOrderedTasks = topologicalOrderedTasks;
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

                // Increment searches made to update GUI
                _infoTracker.setSearchesMade(_infoTracker.getSearchesMade() + 1);

                // Check if we've found our new most optimal
                if (child.isComplete() && childLength < boundValue) {
                    if (childLength == 501) {
                        System.out.println("Good");
                    }
                    boundValue = childLength;
                    currentBest = child;
                    _infoTracker.setCurrentBestHasChanged(true);
                    _infoTracker.setCurrentBest((int) childLength);
                    _infoTracker.setScheduledToBeDisplayed(child);
                }
                // Branch by pushing child into search tree or bound
                if (!exploredScheduleIds.contains(child.getScheduledId()) && childLength < boundValue && child.getEstimatedFinish() < boundValue) {
                    searchTree.push(child);
                    exploredScheduleIds.add(child.getScheduledId());
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
}