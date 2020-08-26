package domain;

import javafx.concurrent.Task;

import java.util.*;

/**
 * Used to represent a partial solution to the scheduling problem, as a node in the branch and bound search tree. This can
 * also be used to represent a complete solution. This class contains state pertaining to both the abstraction of
 * the schedule itself, as well as the scheduling which led to the creation of this schedule (the addition of one
 * task to a processor).
 */
public class PartialSchedule implements Comparable<PartialSchedule> {

    // State relating to the schedule
    private TaskNode[][] _processorSchedules;
    private double[] _processorEndTimes;
    private final Set<TaskNode> _canBeScheduled;
    private final double _scheduleLength;
    private int _firstAvailableProcessor;
    private final double _estimatedFinish;

    // State relating to the scheduling, which created this PartialSchedule
    private final Map<TaskNode, PartialSchedule> _schedulings; // Maps the scheduling of a task to the PartialOrder in which it was added
    private final double _scheduledTaskEndTime;
    private final int _processorUsedIndex;

    /**
     * Initializes a PartialSchedule consisting of only one node, scheduled on the first processor. This
     * represents a root in the search tree.
     * @param numProcessors
     * @param scheduledNode
     */
    public PartialSchedule(int numProcessors, TaskNode scheduledNode, List<TaskNode> otherRoots, List<TaskNode> allTasks) {
        _processorSchedules = new TaskNode[numProcessors][];
        _processorSchedules[0] = new TaskNode[] {scheduledNode};
        for (int i = 1; i < numProcessors; i++) {
            _processorSchedules[i] = new TaskNode[0];
        }

        _processorEndTimes = new double[numProcessors];
        _processorEndTimes[0] = scheduledNode.getWeight();
        _processorUsedIndex = 0;
        _firstAvailableProcessor = Math.min(_processorSchedules.length-1, 1);

        _schedulings = new HashMap<TaskNode, PartialSchedule>();
        _schedulings.put(scheduledNode, this);

        _canBeScheduled = new HashSet<TaskNode>(otherRoots);
        _canBeScheduled.addAll(getNewlySchedulable(scheduledNode));

        _scheduledTaskEndTime = scheduledNode.getWeight();
        _scheduleLength = scheduledNode.getWeight();

        _estimatedFinish = estimateFinish(allTasks);
    }

    /**
     * Creates a new child PartialSchedule, by taking the processor schedules of its parent and scheduling the new
     * task on the specified processor. Used to create a PartialSchedules child schedules.
     * @param parentProcessorSchedules
     * @param scheduledProcessorIndex
     * @param scheduledTask task to be scheduled
     */
    private PartialSchedule(Map<TaskNode, PartialSchedule> schedulings, TaskNode[][] parentProcessorSchedules, Set<TaskNode> canBeScheduled,
                           int scheduledProcessorIndex, TaskNode scheduledTask, int firstAvailableProcessor, double[] processorEndTimes, List<TaskNode> allTasks) {
        generateProcessorSchedules(parentProcessorSchedules, scheduledTask, scheduledProcessorIndex);

        _processorUsedIndex = scheduledProcessorIndex;
        _firstAvailableProcessor = firstAvailableProcessor;

        _schedulings = new HashMap<TaskNode, PartialSchedule>(schedulings);
        _schedulings.put(scheduledTask, this);

        _canBeScheduled = new HashSet<TaskNode>(canBeScheduled);
        _canBeScheduled.remove(scheduledTask);
        _canBeScheduled.addAll(getNewlySchedulable(scheduledTask));

        _scheduledTaskEndTime = computeScheduledEndTime(scheduledTask);

        _processorEndTimes = Arrays.copyOf(processorEndTimes, processorEndTimes.length);
        _processorEndTimes[scheduledProcessorIndex] = _scheduledTaskEndTime;
        _scheduleLength = computeScheduleLength();

        _estimatedFinish = estimateFinish(allTasks);
    }

    /**
     * Generates this PartialSchedules processor schedulings. This is done by copying it's parents processor schedules, with
     * the addition of the newly scheduled task on its specified processor.
     * @param parentProcessorSchedules
     * @param scheduledTask
     * @param scheduledProcessorIndex
     */
    private void generateProcessorSchedules(TaskNode[][] parentProcessorSchedules, TaskNode scheduledTask, int scheduledProcessorIndex) {
        _processorSchedules = new TaskNode[parentProcessorSchedules.length][];

        for (int i = 0; i < parentProcessorSchedules.length; i++) {
            if (i == scheduledProcessorIndex) {
                TaskNode[] parentProcessorSchedule = parentProcessorSchedules[scheduledProcessorIndex];
                /* We've found the processor which we need to schedule the task on. We copy the processors schedule from
                   the parent PartialSchedule, and concat our scheduled task to the end. */
                if (parentProcessorSchedule == null || parentProcessorSchedule.length == 0) {
                    _processorSchedules[scheduledProcessorIndex] = new TaskNode[] {scheduledTask};
                } else {
                    _processorSchedules[scheduledProcessorIndex] = new TaskNode[parentProcessorSchedule.length + 1];
                    _processorSchedules[scheduledProcessorIndex] = Arrays.copyOf(parentProcessorSchedule, parentProcessorSchedule.length + 1);
                    _processorSchedules[scheduledProcessorIndex][parentProcessorSchedule.length] = scheduledTask;
                }
            } else {
                _processorSchedules[i] = parentProcessorSchedules[i];
            }
        }
    }

    /**
     * Creates every potential child of this PartialSchedule, by scheduling every available task on every
     * available processor. This represents the exploration of this node in the search tree.
     * @return child schedules
     */
    public PartialSchedule[] createChildren(List<TaskNode> allTasks) {
        PartialSchedule[] children = new PartialSchedule[_canBeScheduled.size() * (_firstAvailableProcessor +1)];

        int i = 0;
        Set<TaskNode> scheduled = new HashSet<TaskNode>();
        // Initializing a child for every task to processor combination
        for (TaskNode task: _canBeScheduled) {
            scheduled.add(task);
            for (int processorIndex = 0; processorIndex <= _firstAvailableProcessor; processorIndex++) {
                int firstAvailableProcessor = Math.min(processorIndex + 1, _processorSchedules.length-1);
                children[i] = new PartialSchedule(_schedulings, _processorSchedules, _canBeScheduled, processorIndex, task, firstAvailableProcessor, _processorEndTimes, allTasks);
                i++;
            }
        }

        return children;
    }

    /**
     * Computes the time at which the task scheduled via the creation of this PartialSchedule will end
     * @param scheduledTask
     * @return End time of the task scheduled
     */
    private double computeScheduledEndTime(TaskNode scheduledTask) {
        double scheduledEndTime;
        // Initializing scheduledEndTime as best case scenario - when it can be scheduled as soon as its processor is availible
        TaskNode scheduledBeforeCurrent;
        if (_processorSchedules[_processorUsedIndex].length > 1) {
            scheduledBeforeCurrent = _processorSchedules[_processorUsedIndex][_processorSchedules[_processorUsedIndex].length-2];
            scheduledEndTime = _schedulings.get(scheduledBeforeCurrent).getScheduledTaskEndTime() + scheduledTask.getWeight();
        } else {
            scheduledEndTime = scheduledTask.getWeight();
        }

        TaskNode[] dependencies = scheduledTask.getDependencies();
        // Iterating over the dependencies of this task, determining if they will delay the scheduling
        for (TaskNode dependency: dependencies) {
            PartialSchedule dependencyScheduling = _schedulings.get(dependency);

            // If the dependency is on this processor we can ignore it
            if (_schedulings.get(dependency).getProcessorUsedIndex() != _processorUsedIndex) {
                double bestEndTimeForDependency = dependencyScheduling.getScheduledTaskEndTime()
                        + scheduledTask.getDependencyEdgeWeight(dependency)
                        + scheduledTask.getWeight();
                scheduledEndTime = Math.max(bestEndTimeForDependency, scheduledEndTime);
            }
        }

        return scheduledEndTime;
    }

    /**
     * Computes the total length of this schedule i.e. the time taken to execute the tasks based on this scheduling.
     * @return total length of the schedule
     */
    private double computeScheduleLength() {
        double length = _scheduledTaskEndTime;

        // Iterating over each processor, to check its end time
        for (TaskNode[] processor: _processorSchedules) {
            if (processor.length > 0) {
                double processorEndTime = _schedulings.get(processor[processor.length - 1])._scheduledTaskEndTime;
                if (processorEndTime > length) {
                    length = processorEndTime;
                }
            }
        }

        return length;
    }

    /**
     * Takes the full input graph, the scheduled task, and determines if any more tasks are schedulable (in degree = 0).
     * If those tasks are schedulable, we add them to our schedulable set.
     * @param scheduledTask task scheduled to create this child
     */
    private Set<TaskNode> getNewlySchedulable(TaskNode scheduledTask) {
        List<TaskNode> dependentsOfScheduled = scheduledTask.getDependents();
        Set<TaskNode> newlySchedulableTasks = new HashSet<TaskNode>();

        // Checking each all potential additions to schedulable (have incoming edge from scheduled task)
        for (TaskNode dependent: dependentsOfScheduled) {
            TaskNode[] dependencies = dependent.getDependencies();
            boolean schedulable = true;
            // If this PartialSchedule has scheduled all of its dependencies, it is schedulable
            for (TaskNode dependency: dependencies) {
                if (!_schedulings.keySet().contains(dependency)) {
                    schedulable = false;
                    break;
                }
            }
            if (schedulable) {
                newlySchedulableTasks.add(dependent);
            }
        }

        return newlySchedulableTasks;
    }

    private double estimateFinish(List<TaskNode> topologicalOrderedTasks) {
        double[] processorEndTimes = Arrays.copyOf(_processorEndTimes, _processorEndTimes.length) ;

        List<TaskNode> remainingTasks = new ArrayList<TaskNode>();
        Set<TaskNode> scheduledTasks = _schedulings.keySet();
        for (TaskNode task: topologicalOrderedTasks) {
            if (!scheduledTasks.contains(task)) {
                remainingTasks.add(task);
            }
        }
        Collections.sort(remainingTasks);

        for (TaskNode task: remainingTasks) {
            Arrays.sort(processorEndTimes);
            processorEndTimes[0] = processorEndTimes[0] + task.getWeight();
        }

        Arrays.sort(processorEndTimes);
        return processorEndTimes[processorEndTimes.length-1];
    }

    /**
     * @return Returns the end time of the task which was scheduled with the creation of this node (PartialSchedule)
     */
    public double getScheduledTaskEndTime() {
        return _scheduledTaskEndTime;
    }

    /**
     * @return Returns the processor which was used for the scheduling which is represented by the creation of this
     * PartialSchedule
     */
    private int getProcessorUsedIndex() {
        return _processorUsedIndex;
    }

    /**
     * @return Returns the total length of this partial schedule i.e. the end time of the whole schedule
     */
    public double getScheduleLength() {
        return _scheduleLength;
    }

    /**
     * @return Returns true if this PartialSchedule has scheduled all tasks in the input graph.
     */
    public boolean isComplete() {
        if (_canBeScheduled.size() == 0) {
            return true;
        }
        return false;
    }

    public double getEstimatedFinish() {
        return _estimatedFinish;
    }

    public double[] getProcessorEndTimes() {
        return _processorEndTimes;
    }

    public int getProcessorIndex() {
        return _processorUsedIndex;
    }

    public Map<TaskNode, PartialSchedule> getSchedulings() {
        return _schedulings;
    }

    @Override
    public int compareTo(PartialSchedule o) {
        return Double.compare(_scheduleLength, o._scheduleLength);
    }
}
