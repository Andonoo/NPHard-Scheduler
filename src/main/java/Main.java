import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.Graphs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.*;

import org.graphstream.graph.implementations.*;

public class Main {
    private static List<String> _options = new ArrayList<String>();
    private static String _filename;
    private static int _numProcessors;
    private static AdjacencyListGraph _graph;
    private static FileParser _parser;
    private static List<Node>[] _processorSchedules;

    public static void main(String[] args) {
        parseInput(args);
        executeAlgorithm(_graph, _numProcessors);
        printOutput(_parser);
        displayOutput();
        printTopologicalOrders(_graph);
    }

    private static void displayOutput() {
        if (_options.contains("-v")) {
            for (Node n : _graph) {
                n.addAttribute("ui.label", n.getId());
            }
            _graph.display();
        }
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
        _parser = new FileParser(_filename);
        _graph = _parser.getGraph();
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
        _processorSchedules = new ArrayList[numProcessors];
        for (int i = 0; i < numProcessors; i++) {
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
    public static double startTime(List<Node> processor, Node task) {
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
                earliestStartOnP = Math.max(earliestStartOnP, (Double) dependencyTask.getAttribute("endTime") + d.<Double>getAttribute("Weight"));
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
            topOrderedNodes[i] = g.getNode(node);
            i++;
        }

        return topOrderedNodes;
    }

    /**
     * printOutput() will read the valid array of SolutionNodes and parse it into the dot output file
     */
    public static void printOutput(FileParser parser) {

        int fileEndingIndex = _filename.indexOf(".dot");
        String outputName;
        int outputIndex = _options.indexOf("-o");
        if (outputIndex != -1) {
            String desiredOutputName = _options.get(outputIndex + 1);
            outputName = _filename.substring(0, _filename.lastIndexOf(File.separator) + 1) + desiredOutputName + "-output.dot";

        } else {
            outputName = _filename.substring(0, fileEndingIndex) + "-output.dot";
        }

        for (Node n : _graph) {
            n.changeAttribute("Processor", Arrays.asList(_processorSchedules).indexOf(n.getAttribute("Processor")) + 1);
            n.removeAttribute("endTime");
            n.changeAttribute("Start", ((Double) n.getAttribute("Start")).intValue());
        }
        parser.createOutputFile(outputName, _graph);

        formatDotFile(outputName);
    }

    public static void formatDotFile(String fileName) {

        try {

            File f = new File(fileName);
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
                            .append(fileName.substring(fileName.lastIndexOf(File.separator) + 1, fileName.lastIndexOf(".")))
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

    public static void printTopologicalOrders(AdjacencyListGraph g) {
        AdjacencyListGraph graphToDestruct = (AdjacencyListGraph) Graphs.clone(g);

        Stack<Node> nodeStack = new Stack<Node>();
        Map<Node, Boolean> discovered = new HashMap();

        for (Node node: graphToDestruct.getNodeSet()) {
            discovered.put(node, false);
        }

        getTopologicalOrders(graphToDestruct, nodeStack, discovered, graphToDestruct.getNodeCount());
    }

    public static void getTopologicalOrders(AdjacencyListGraph g, Stack<Node> nodeStack, Map<Node, Boolean> discovered, int totalNodes) {

        // Iterate through each node in the graph
        for (Node node: g.getNodeSet()) {

            //System.out.println(nodeStack + " : " + node);
            //System.out.println(node.getInDegree() + " : " + !discovered.get(node));

            // Find next node in topological order
            if (node.getInDegree() == 0 && !discovered.get(node)) {

                //System.out.println(node + " has been added to stack\n");
                // Add this node to the stack and mark it as discovered

                nodeStack.add(node);
                discovered.replace(node, true);

                // Find and remove outgoing edges from this node from the graph
                Collection<Edge> removedEdges = new HashSet<Edge>();
                for (Edge edge: node.getLeavingEdgeSet()) {
                    //System.out.println(edge + " will be deleted");
                    removedEdges.add(edge);
                }
                for (Edge edge: removedEdges) {
                    g.removeEdge(edge);
                }

               // Return the stack if all the nodes have been stacked - it is a topological order
                // else call this method on the graph without the node and it's edges
                if (nodeStack.size() == totalNodes) {
                    System.out.println(nodeStack);
                }
                else {
                    //System.out.println("Recurse here\n\n");
                    getTopologicalOrders(g, nodeStack, discovered, totalNodes);
                }

                // Backtrack and add this node and all it's edges back to the graph
               //g.addNode(node.getId());

               for (Edge edge: removedEdges) {
                   //System.out.println(edge + " will be re-added");
                   g.addEdge(edge.getId(), (Node) edge.getSourceNode(), edge.getTargetNode(), true);
               }

               // Backtrack and remove this node from the stack
               nodeStack.pop();
               discovered.replace(node, false);


            }

        }

    }
}
