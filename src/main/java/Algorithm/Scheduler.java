package Algorithm;

import IO.OutputHandler;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.AdjacencyListGraph;
import org.graphstream.graph.implementations.Graphs;

import java.util.*;

public class Scheduler {

    private List<Node>[] _processorSchedules;
    private final AdjacencyListGraph _graph;
    private final int _numProcessors;

    public Scheduler(AdjacencyListGraph graph, int processors) {
        _graph = graph;
        _numProcessors = processors;
    }

    /**
     * executeAlgorithm() will return a valid schedule
     */
    public void executeAlgorithm() {
        Node[] nodes = sortTopologically();
        scheduleByGreedy(nodes);
        prepForOutput();
    }

    private void prepForOutput() {
        for (Node n : _graph) {
            n.changeAttribute("Processor", Arrays.asList(_processorSchedules).indexOf(n.getAttribute("Processor")) + 1);
            n.removeAttribute("endTime");
            n.changeAttribute("Start", ((Double) n.getAttribute("Start")).intValue());
            n.changeAttribute("Weight", ((Double) n.getAttribute("Weight")).intValue());
        }

        for (Edge e : _graph.getEdgeSet()) {
            e.changeAttribute("Weight", ((Double) e.getAttribute("Weight")).intValue());
        }
        OutputHandler outputParser = new OutputHandler(_graph);
    }

    /**
     * scheduleByGreedy will schedule a task based on the greedy heuristic: Earliest Start Time.
     * This depends on the input nodes being on a valid topological order
     */
    public void scheduleByGreedy(Node[] tasks) {
        // Initializing a schedule for each processor
        _processorSchedules = new ArrayList[_numProcessors];
        for (int i = 0; i < _numProcessors; i++) {
            _processorSchedules[i] = new ArrayList<Node>();
        }

        // Scheduling each task on earliest available processor heuristic
        List<Node> currentEarliestFreeProcessor;
        double earliestStartTime;
        for (Node node : tasks) {
            Node task = _graph.getNode(node.getId());
            currentEarliestFreeProcessor = _processorSchedules[0];
            earliestStartTime = startTime(_processorSchedules[0], task);

            boolean foundFreeProcessor = false;
            int i = 0;
            // Checking each processor to determine earliest availability
            while (!foundFreeProcessor && i < _processorSchedules.length) {
                List<Node> p = _processorSchedules[i];
                i++;
                // Set to break after finding free processor
                if (p.size() == 0) {
                    foundFreeProcessor = true;
                }
                // Determine if this processor can schedule the task earlier
                double startTimeOfferedByP = startTime(p, task);
                if (startTimeOfferedByP < earliestStartTime) {
                    currentEarliestFreeProcessor = p;
                    earliestStartTime = startTimeOfferedByP;
                }
            }

            // Scheduling task to the earliest available processor
            task.addAttribute("Processor", currentEarliestFreeProcessor);
            task.addAttribute("Start", earliestStartTime);
            task.addAttribute("endTime", earliestStartTime + (Double) task.getAttribute("Weight"));
            currentEarliestFreeProcessor.add(task);
        }
    }

    /**
     * startTime() will return the earliest possible scheduling of task in a specified processor
     */
    public double startTime(List<Node> processor, Node task) {
        Collection<Edge> dependencies = task.getEnteringEdgeSet();

        // Determining the time at which this processor will be free
        Double currentProcFinishTime = processor.size() > 0 ? (Double) processor.get(processor.size() - 1).getAttribute("endTime") : 0;
        double earliestStartOnP = currentProcFinishTime == null ? 0 : currentProcFinishTime;

        /* For each dependency task, check whether this processor will have to wait for data to arrive - in which case the earliest
        potential scheduling on this processor may increase */
        for (Edge d : dependencies) {
            Node dependencyTask = d.getSourceNode();
            // Checking if dependency is scheduled on another processor
            if (!dependencyTask.getAttribute("Processor").equals(processor)) {
                earliestStartOnP = Math.max(earliestStartOnP, (Double)dependencyTask.getAttribute("endTime") + d.<Double>getAttribute("Weight"));
            }
        }

        return earliestStartOnP;
    }

    /**
     * sortTopologically() will return a topological ordering of the vertices in Graph G using Kahn's algorithm
     */
    public Node[] sortTopologically() {
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
}
