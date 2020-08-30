package algorithm;

import domain.DomainHandler;
import domain.TaskNode;
import javafx.InfoTracker;
import javafx.ScheduleDisplayer;
import javafx.scene.chart.XYChart;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.AdjacencyListGraph;
import org.graphstream.graph.implementations.Graphs;

import java.util.*;

/**
 * Takes an input graph and schedules the tasks on n processors based on a greedy selection heuristic. Note that
 * this implementation is dependent on GraphStream for it's data structures and is not memory efficient.
 */
public class GreedyScheduler {

    private List<TaskNode>[] _processorSchedules;
    private final AdjacencyListGraph _graph;
    private final int _numProcessors;
    private double _solutionLength;
    private List<TaskNode> _topologicalOrderedTasks;
    private final InfoTracker _infoTracker;
    private final XYChart.Series[] _seriesArray;

    public GreedyScheduler(AdjacencyListGraph graph, int processors) {
        _graph = graph;
        _numProcessors = processors;
        _solutionLength = 0;
        _infoTracker = null;
        _seriesArray = null;
    }

    public GreedyScheduler(AdjacencyListGraph graph, int processors, InfoTracker infoTracker) {
        _graph = graph;
        _numProcessors = processors;
        _solutionLength = 0;
        _infoTracker = infoTracker;
        _seriesArray = new XYChart.Series[_numProcessors];
    }

    /**
     * executeAlgorithm() will return a valid schedule
     */
    public void executeAlgorithm() {
        Node[] topologicalOrderedNodes = getTopologicalOrdering(_graph);
        _topologicalOrderedTasks = DomainHandler.populateTaskNodes(topologicalOrderedNodes);
        scheduleByGreedy(_topologicalOrderedTasks);
    }

    /**
     * scheduleByGreedy will schedule a task based on the greedy heuristic: Earliest Start Time.
     * This depends on the input nodes being on a valid topological order
     */
    private void scheduleByGreedy(List<TaskNode> tasks) {
        // Initializing a schedule for each processor
        _processorSchedules = new List[_numProcessors];
        for (int i = 0; i < _numProcessors; i++) {
            _processorSchedules[i] = new ArrayList<TaskNode>();
        }

        /* Initializing Task end times and schedulings. This data is not held within the TaskNode
         class as it is designed to be as minimal as possible (for efficiency) */
        Map<TaskNode, Double> endTimes = new HashMap<TaskNode, Double>();
        Map<TaskNode, List<TaskNode>> taskSchedulings = new LinkedHashMap<TaskNode, List<TaskNode>>();

        // Scheduling each task on earliest available processor heuristic
        List<TaskNode> currentEarliestFreeProcessor;
        double earliestStartTime;
        for (TaskNode task : tasks) {
            currentEarliestFreeProcessor = _processorSchedules[0];
            earliestStartTime = startTime(taskSchedulings, endTimes, _processorSchedules[0], task);

            boolean foundFreeProcessor = false;
            int i = 0;
            // Checking each processor to determine earliest availability
            while (!foundFreeProcessor && i < _processorSchedules.length) {
                List<TaskNode> p = _processorSchedules[i];
                i++;
                // Set to break after finding free processor
                if (p.size() == 0) {
                    foundFreeProcessor = true;
                }
                // Determine if this processor can schedule the task earlier
                double startTimeOfferedByP = startTime(taskSchedulings, endTimes, p, task);
                if (startTimeOfferedByP < earliestStartTime) {
                    currentEarliestFreeProcessor = p;
                    earliestStartTime = startTimeOfferedByP;
                }
            }

            // Scheduling task to the earliest available processor
            taskSchedulings.put(task, currentEarliestFreeProcessor);
            endTimes.put(task, earliestStartTime + task.getWeight());

            if (earliestStartTime + task.getWeight() > _solutionLength) {
                _solutionLength = earliestStartTime + task.getWeight();
            }
            currentEarliestFreeProcessor.add(task);
        }
        prepGraphForOutput(taskSchedulings, endTimes);
    }

    /**
     * startTime() will return the earliest possible scheduling of task in a specified processor
     */
    private double startTime(Map<TaskNode, List<TaskNode>> taskSchedulings, Map<TaskNode, Double> endTimes, List<TaskNode> processor, TaskNode task) {
        TaskNode[] dependencies = task.getDependencies();

        // Determining the time at which this processor will be free
        double earliestStartOnP = processor.size() > 0 ? endTimes.get(processor.get(processor.size() - 1)) : 0;

        /* For each dependency task, check whether this processor will have to wait for data to arrive - in which case the earliest
        potential scheduling on this processor may increase */
        for (TaskNode d : dependencies) {
            // Checking if dependency is scheduled on another processor
            if (!taskSchedulings.get(d).equals(processor)) {
                earliestStartOnP = Math.max(earliestStartOnP, endTimes.get(d) + task.getDependencyEdgeWeight(d));
            }
        }

        return earliestStartOnP;
    }

    /**
     * sortTopologically() will return a topological ordering of the vertices in Graph G using Kahn's algorithm
     */
    private Node[] getTopologicalOrdering(Graph inputGraph) {
        AdjacencyListGraph graphToDestruct = (AdjacencyListGraph) Graphs.clone(_graph);

        String[] topOrder = new String[graphToDestruct.getNodeCount()];
        int orderIndex = 0;

        // Initializing set containing nodes with no incoming edges
        Set<Node> noIncomingEdges = new HashSet<Node>();
        for (Node n : graphToDestruct.getNodeSet()) {
            if (n.getInDegree() == 0) {
                noIncomingEdges.add(n);
            }
        }

        // While we still have nodes with no incoming edges, remove one and add to the topological order
        while (!noIncomingEdges.isEmpty()) {
            Node n = noIncomingEdges.iterator().next();
            topOrder[orderIndex] = n.getId();
            orderIndex++;

            // Remove the edges leaving our selected node and update our set of noIncomingEdges
            Collection<Edge> removedEdges = new HashSet<Edge>();
            for (Edge e : n.getLeavingEdgeSet()) {
                if (e.getTargetNode().getInDegree() == 1) {
                    noIncomingEdges.add(e.getTargetNode());
                }
                removedEdges.add(e);
            }
            for (Edge e : removedEdges) {
                n.getGraph().removeEdge(e);
            }

            // Remove our node from the set of noIncomingEdges
            noIncomingEdges.remove(n);
        }

        // Converting string representation of topological order to references of the nodes from our intact graph
        Node[] topOrderedNodes = new Node[topOrder.length];
        int i = 0;
        for (String node : topOrder) {
            topOrderedNodes[i] = _graph.getNode(node);
            i++;
        }

        return topOrderedNodes;
    }

    /**
     * Preps the IO graph with necessary attributes to meet the correct output format.
     */
    private void prepGraphForOutput(Map<TaskNode, List<TaskNode>> taskSchedulings, Map<TaskNode, Double> endTimes) {
        if (_seriesArray != null) {
            // Initializing series obj
            for (int i = 0; i < _numProcessors; i++) {
                _seriesArray[i] = new XYChart.Series();
            }
        }

        for (TaskNode task : taskSchedulings.keySet()) {
            Node node = _graph.getNode(task.getId());

            int processorIndex = 0;
            for (int i = 0; i < _processorSchedules.length; i++) {
                if (_processorSchedules[i].contains(task)) {
                    processorIndex = i;
                }
            }
            double startTime = endTimes.get(task) - task.getWeight();
            node.setAttribute("Processor", processorIndex + 1);
            node.setAttribute("Start", startTime);
            node.setAttribute("Weight", task.getWeight());

            if (_infoTracker != null && _seriesArray != null) {
                XYChart.Data newData = new XYChart.Data(startTime, String.valueOf(processorIndex + 1),
                        new ScheduleDisplayer.ExtraData(task, "task"));

                _seriesArray[processorIndex].getData().add(newData);
            }
        }
        if (_infoTracker != null) {
            _infoTracker.setCurrentBestHasChanged(true);
            _infoTracker.setGreedyData(_seriesArray);
            _infoTracker.setCurrentBest((int) _solutionLength);
        }
    }

    /**
     * @return Returns the input tasks, ordered topologically.
     */
    public List<TaskNode> getTopologicallyOrderedTaskNodes() {
        return _topologicalOrderedTasks;
    }

    /**
     * @return Returns the graph representation of a valid scheduling. Each node is annotated with it's processor, start time and
     * end time.
     */
    public AdjacencyListGraph getSolution() {
        return _graph;
    }

    /**
     * @return Returns length of computed solution, or 0 if the solution has not been computed.
     */
    public double getSolutionLength() {
        return _solutionLength;
    }
}