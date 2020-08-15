import org.graphstream.graph.Graph;

import java.util.ArrayList;
import java.io.File;
import java.util.List;

import java.util.*;

import org.graphstream.graph.implementations.*;
import org.graphstream.graph.*;

public class Main {
    private static List<String> _options = new ArrayList<String>();
    private static String _filename;
    private static int _numProcessors;
    private static Graph _graph;

    public static void main(String[] args) {
        parseInput(args);
        executeAlgorithm(_graph, _numProcessors);
    }

    /*
    createGraph() will utilise the dot file inputted by the user and parse it to a usable object using GraphStream
     */
    public static void parseInput(String[] input) {
        _filename = input[0];
        _numProcessors = Integer.parseInt(input[1]);
        if (input.length > 2) {
            for (int i = 2; i < input.length; i++) {
                _options.add(input[i]);
            }
        }
        System.out.println("Number of processors: " + _numProcessors);
        GraphCreator graphCreator = new GraphCreator(new File(_filename)); // ./example-dot-files/example1.dot
        _graph = graphCreator.getGraph();
        if (_options.contains("-v")) {
            _graph.display();
        }
        graphCreator.displayGraphStats("Nodes");
        graphCreator.displayGraphStats("Edges");
    }

    /**
     * executeAlgorithm() will return a valid schedule
     */
    public static List<Node>[] executeAlgorithm(Graph g, int numProcessors) {
        Node[] nodes = sortTopologically(g);

        return scheduleByGreedy(nodes, numProcessors);
    }

    /**
     * scheduleByGreedy will schedule a task based on the greedy heuristic: Earliest Start Time.
     * This depends on the input nodes being on a valid topoligical order
     */
    public static List<Node>[] scheduleByGreedy(Node[] tasks, int numProcessors) {
        // Initializing a schedule for each processor
        List<Node>[] processorSchedules = new ArrayList[numProcessors];
        for (int i = 0; i < numProcessors; i++) {
            processorSchedules[i] = new ArrayList<Node>();
        }

        // Scheduling each task on earliest available processor heuristic
        List<Node> currentEarliestFreeProcessor;
        int earliestStartTime;
        for (Node task: tasks) {
            currentEarliestFreeProcessor = processorSchedules[0];
            earliestStartTime = startTime(processorSchedules[0], task);

            boolean foundFreeProcessor = false;
            int i = 0;
            // Checking each processor to determine earliest availability
            while(!foundFreeProcessor && i < processorSchedules.length) {
                List<Node> p = processorSchedules[i];
                i++;
                // Set to break after finding free processor
                if (p.size() == 0) {
                    foundFreeProcessor = true;
                }
                // Determine if this processor can schedule the task earlier
                int startTimeOfferedByP = startTime(p, task);
                if (startTimeOfferedByP < earliestStartTime) {
                    currentEarliestFreeProcessor = p;
                    earliestStartTime = startTimeOfferedByP;
                }
            }

            // Scheduling task to the earliest available processor
            task.addAttribute("Processor", currentEarliestFreeProcessor);
            task.addAttribute("startTime", earliestStartTime);
            task.addAttribute("endTime", earliestStartTime + (Integer)task.getAttribute("Weight"));
            currentEarliestFreeProcessor.add(task);
        }

        return processorSchedules;
    }

    /**
     * startTime() will return the earliest possible scheduling of task in a specified processor
     */
    public static int startTime(List<Node> processor, Node task) {
        Collection<Edge> dependencies = task.getEnteringEdgeSet();

        Integer currentProcFinishTime = processor.size() > 0 ? (Integer)processor.get(processor.size()-1).getAttribute("endTime") : 0;
        int earliestStartOnP = currentProcFinishTime == null ? 0 : currentProcFinishTime;

        for (Edge d: dependencies) {
            Node dependencyTask = d.getSourceNode();
            if (!dependencyTask.getAttribute("Processor").equals(processor)) {
                earliestStartOnP = Math.max(earliestStartOnP, (Integer)dependencyTask.getAttribute("endTime") + d.<Integer>getAttribute("Weight"));
            }
        }

        return earliestStartOnP;
    }

    /**
     * sortTopologically() will return a topological ordering of the vertices in Graph G using Kahn's algorithm
     */
    public static Node[] sortTopologically(Graph g) {
        Graph graphToDestruct = Graphs.clone(g);

        String[] topOrder = new String[graphToDestruct.getNodeSet().size()];
        int orderIndex = 0;

        // Initializing set containing nodes with no incoming edges
        Set<Node> noIncomingEdges = new HashSet<Node>();
        for (Node n: graphToDestruct.getNodeSet()) {
            if(n.getInDegree() == 0) {
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
            for (Edge e: n.getLeavingEdgeSet()) {
                if (e.getTargetNode().getInDegree() == 1) {
                    noIncomingEdges.add(e.getTargetNode());
                }
                removedEdges.add(e);
            }
            for (Edge e: removedEdges) {
                n.getGraph().removeEdge(e);
            }

            // Remove our node from the set of noIncomingEdges
            noIncomingEdges.remove(n);
        }

        // Converting string representation of topological order to references of the nodes from our intact graph
        Node[] topOrderedNodes = new Node[topOrder.length];
        int i = 0;
        for (String node: topOrder) {
            topOrderedNodes[i] = g.getNode(node);
            i++;
        }

        return topOrderedNodes;
    }

    /**
     * printOutput() will read the valid array of SolutionNodes and parse it into the dot output file
     */
    public void printOutput() {

    }
}
