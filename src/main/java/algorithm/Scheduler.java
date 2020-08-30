package algorithm;

import domain.PartialSchedule;
import domain.TaskNode;

import java.util.Map;

/**
 * Interface to which the two schedulers (ParallelOptimalScheduler and SequentialOptimalScheduler) will implement.
 * They will inherit the methods described below.
 */
public interface Scheduler {

    /**
     * Gets the schedule which was computed by the branch and bound algorithm. Will return null if no schedule was found
     * with a shorter length than the provided initialBoundValue
     * @param initialBoundValue
     * @return Returns true if a schedule was found that is shorter than the provided bound value
     */
    boolean executeBranchAndBoundAlgorithm(double initialBoundValue, Map<TaskNode, Double> bottomLevelValues);

    /**
     * Gets the schedule which was computed by the branch and bound algorithm. Will return null if no schedule was found
     * with a shorter length than the provided initialBoundValue
     * @return Returns the most optimal completed schedule
     */
    PartialSchedule getSolution();
}
