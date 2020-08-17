import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import java.util.*;

import org.graphstream.graph.implementations.*;
import org.graphstream.graph.*;

public class Main {
    private static List<String> _options = new ArrayList<String>();
    private static String _filename;
    private static int _numProcessors;
    private static AdjacencyListGraph _graph;

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
        FileParser parser = new FileParser(_filename);
        _graph = parser.getGraph();
        printOutput(parser);

//        GraphCreator graphCreator = new GraphCreator(new File(_filename)); // ./example-dot-files/example1.dot
//        _graph = graphCreator.getGraph();
        if (_options.contains("-v")) {
            System.out.println(_graph.getNodeCount());
            System.out.println("Displaying data...");
            for (Node n : _graph) {
                System.out.println(n.getId());
                n.addAttribute("ui.label", n.getId());
                System.out.println("Node id: " + n.getId() + " Weight: " + (n.getAttribute("Weight")));
            }
            for (Edge e : _graph.getEdgeSet()) {
                System.out.println(e.getId());
                System.out.println("Edge id: " + e.getId() + " Weight: " + (e.getAttribute("Weight")));
            }
            _graph.display();
        }
//        graphCreator.displayGraphStats("Nodes");
//        graphCreator.displayGraphStats("Edges");
    }

    /**
     * executeAlgorithm() will return a valid schedule
     */
    public static void executeAlgorithm(AdjacencyListGraph g, int numProcessors) {
        Node[] nodes = sortTopologically(g);
        scheduleByGreedy(nodes, numProcessors);
    }

    /**
     * scheduleByGreedy will schedule a task based on the greedy heuristic: Earliest Start Time.
     * This depends on the input nodes being on a valid topological order
     */
    public static void scheduleByGreedy(Node[] tasks, int numProcessors) {
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
    }

    /**
     * startTime() will return the earliest possible scheduling of task in a specified processor
     */
    public static int startTime(List<Node> processor, Node task) {
        Collection<Edge> dependencies = task.getEnteringEdgeSet();

        // Determining the time at which this processor will be free
        Integer currentProcFinishTime = processor.size() > 0 ? (Integer)processor.get(processor.size()-1).getAttribute("endTime") : 0;
        int earliestStartOnP = currentProcFinishTime == null ? 0 : currentProcFinishTime;

        /* For each dependency task, check whether this processor will have to wait for data to arrive - in which case the earliest
        potential scheduling on this processor may increase */
        for (Edge d: dependencies) {
            Node dependencyTask = d.getSourceNode();
            // Checking if dependency is scheduled on another processor
            if (!dependencyTask.getAttribute("Processor").equals(processor)) {
                earliestStartOnP = Math.max(earliestStartOnP, (Integer)dependencyTask.getAttribute("endTime") + d.<Integer>getAttribute("Weight"));
            }
        }

        return earliestStartOnP;
    }

    /**
     * sortTopologically() will return a topological ordering of the vertices in Graph G using Kahn's algorithm
     */
    public static Node[] sortTopologically(AdjacencyListGraph g) {
        AdjacencyListGraph graphToDestruct = (AdjacencyListGraph) Graphs.clone(g);

        String[] topOrder = new String[graphToDestruct.getNodeCount()];
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
            System.out.println(topOrderedNodes[i]);
            i++;
        }

        return topOrderedNodes;
    }

    /**
     * printOutput() will read the valid array of SolutionNodes and parse it into the dot output file
     */
    public static void printOutput(FileParser parser) {
        int fileEndingIndex = _filename.indexOf(".dot");
        String outputName = _filename.substring(0, fileEndingIndex) + "-output.dot";
        parser.createOutputFile(null, outputName, _graph);

        formatDotFile(outputName);
    }

    public static void formatDotFile(String fileName) {

        try {

            File f = new File(fileName);
            System.out.println(fileName);


            StringBuilder sb = new StringBuilder();
            StringBuilder head = new StringBuilder();

            Scanner scanner = new Scanner(f);

            String line;
            while (scanner.hasNextLine()) {
                line = scanner.nextLine();
                if (line.contains("{")) {
                    int index = line.indexOf("{");
                    head.append(line.substring(0, index))
                            .append("\"")
                            .append(fileName.substring(fileName.lastIndexOf("/")+1,fileName.lastIndexOf(".")+1))
                            .append("\" ")
                            .append(line.substring(index))
                            .append("\n");
                } else {
                    sb.append(line);
                    sb.append("\n");
                }
            }

            scanner.close();

            String data = head.append(sb.toString().replaceAll("\"", "")).toString();

            BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));

            bw.write(data);
            bw.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
