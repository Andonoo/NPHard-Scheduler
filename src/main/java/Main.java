import org.graphstream.graph.Graph;

import java.util.ArrayList;
import java.io.File;
import java.util.List;

import java.io.File;
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
    public static ArrayList<SolutionNode> executeAlgorithm(Graph g, int numProcessors) {
        /*
        Let D be an adjacency list where D{V} is the dependencies of V
        sortTopologically(G);
        For all v in V
            Add output of scheduleByGreedy(v) to ArrayList
         */

        Node[] nodes = sortTopologically(g);

        return null;
    }

    /**
     * sortTopologically() will return a topological ordering of the vertices in Graph G using Kahn's algorithm
     */
    public static Node[] sortTopologically(Graph g) {
        Graph graphToDestruct = Graphs.clone(g);

        Node[] topOrder = new Node[graphToDestruct.getNodeSet().size()];
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
            topOrder[orderIndex] = n;
            orderIndex++;

            /*
                Remove the edges leaving our selected node and update our set of noIncomingEdges
             */
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

        return topOrder;
    }

    /**
     * selectByGreedy will schedule a task based on the greedy heuristic: Earliest Start Time
     */
    public SolutionNode scheduleByGreedy(/* Node v */) {

        /*
        Let P = {Processor one, Processor two, Processor three ...}
        Processor earliest = one;
        For p in P
            If calculateStartTime(p, v) < calculateStartTime(earliest, v)
                earliest = p;
        return earliest.scheduleTask(v);
         */

        return null;


    }

    /**
     * calculateStartTime() will return the earliest possible scheduling of task in a specified processor
     */
    public void calculateStartTime(/* Processor p, Node v */) {

        /*
        Let d = D(v)
        int startTime = p.getCurrentTime();
        For d' in d
            Find SolutionNode x that has d'
            If !p.hasTask(d')
                startTime = max{startTime, x.processor.currentTime + w(d', v)}
         */
    }

    /**
     * printOutput() will read the valid array of SolutionNodes and parse it into the dot output file
     */
    public void printOutput() {

    }
}
