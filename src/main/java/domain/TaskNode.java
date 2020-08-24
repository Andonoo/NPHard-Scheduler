package domain;

import org.graphstream.graph.Edge;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class TaskNode {
    private final String _id;
    private final TaskNode[] _dependencies;
    private final Map<TaskNode, Double> _dependencyWeights;
    private final double _weight;
    private final List<TaskNode> _dependents;

    public TaskNode(String id, TaskNode[] dependencies, double weight, Map<TaskNode, Double> dependencyWeights) {
        _id = id;
        _dependencies = dependencies;
        _weight = weight;
        _dependencyWeights = dependencyWeights;
        _dependents = new ArrayList<TaskNode>();
    }

    /**
     * @param dependency
     * @return Returns the weight of the edge coming from the provided dependency. Returns -1 if it isn't a dependency.
     */
    public double getDependencyEdgeWeight(TaskNode dependency) {
        if (_dependencyWeights.containsKey(dependency)) {
            return _dependencyWeights.get(dependency);
        }
        return -1;
    }

    /**
     * Adds a dependent to this TaskNode
     * @param dependent
     */
    public void addDependent(TaskNode dependent) {
        _dependents.add(dependent);
    }

    @Override
    public boolean equals(Object o) {
        TaskNode otherTask;
        try {
            otherTask = (TaskNode) o;
        } catch (ClassCastException e) {
            return false;
        }
        if (otherTask.getId().equals(_id)) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return _id.hashCode();
    }

    public double getWeight() {
        return _weight;
    }

    public String getId() {
        return _id;
    }

    public List<TaskNode> getDependents() {
        return _dependents;
    }

    public TaskNode[] getDependencies() {
        return _dependencies;
    }
}
