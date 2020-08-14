import org.graphstream.graph.Graph;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;

public class GraphCreator {

    Graph _graph = new SingleGraph("Input Graph");

    public GraphCreator (File filename) {
        try {
            Scanner scanner = new Scanner(filename);
            scanner.nextLine(); // will we ever be given an empty .dot file? if so needs to change slightly - will throw exception
            while (scanner.hasNextLine()) {
                String inputLine = scanner.nextLine();
                if (inputLine.equals("}")) {
                    break;
                }
                addToGraph(inputLine);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void displayGraphStats(String type) {
        if (type.equals("Nodes")) {
            System.out.println("Showing weights for nodes....\n");
            for(Node n:_graph) {
                n.addAttribute("ui.label", n.getId());
                System.out.println("Node id: " + n.getId() + " Weight: " + n.getAttribute("Weight"));
            }
        } else if (type.equals("Edges")) {
            System.out.println("Showing weights for nodes....\n");
            for(Edge e:_graph.getEdgeSet()) {
                System.out.println("Edge id: " + e.getId() + " Weight: " + e.getAttribute("Weight"));
            }
        }
    }

    private void addToGraph(String inputLine) {

        //NOTE: format of .dot file consistent or no? ask Oliver
        String nonWeightInfo = inputLine.replaceAll("\\s", "").split("\\[")[0];
        int weight = Integer.parseInt(inputLine.replaceAll("\\D+",""));

        if (inputLine.contains(">")) {
            // handle edges
            //a −> b [Weight=1];
            //[a, b, weight];
            String[] nodeNames = nonWeightInfo.split("\\-\\>");
            _graph.addEdge(nodeNames[0] + nodeNames[1], nodeNames[0], nodeNames[1]);
            Edge edge = _graph.getEdge(nodeNames[0] + nodeNames[1]);
            edge.setAttribute("Weight", weight);
        } else {
            //a [Weight=2];
            //[a, weight]

            _graph.addNode(nonWeightInfo);
            Node node = _graph.getNode(nonWeightInfo);
            node.setAttribute("Weight", weight);
        }
    }
    public Graph getGraph() {
        return _graph;
    }
}
