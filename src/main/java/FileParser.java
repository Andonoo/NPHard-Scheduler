import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.stream.file.FileSink;
import org.graphstream.stream.file.FileSinkDOT;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceDOT;

import java.io.IOException;
import java.util.List;

public class FileParser {
    private String _fileName;
    private Graph _graph;

    public FileParser(String fileName) {
        _fileName = fileName;
        System.out.println(_fileName);
        _graph = new DefaultGraph("Input Graph");
        FileSource fs = new FileSourceDOT();
        try {
            fs.addSink(_graph);
            fs.readAll(_fileName);
        } catch( IOException e) {
            e.printStackTrace();
        } finally {
            fs.removeSink(_graph);
        }
        System.out.println(_graph.getNodeCount());
        System.out.println("Displaying data...");
        for(Node n:_graph) {
            System.out.println(n.getId());
            n.addAttribute("ui.label", n.getId());
            System.out.println("Node id: " + n.getId() + " Weight: " + n.getAttribute("Weight"));
        }
        for(Edge e:_graph.getEdgeSet()) {
            System.out.println(e.getId());
            System.out.println("Edge id: " + e.getId() + " Weight: " + e.getAttribute("Weight"));
        }
    }

    public Graph getGraph() {
        return _graph;
    }

    public void createOutputFile(List<Node>[] schedule, String outputName) {
//        for (List<Node> processor: schedule) {
//            for (Node)
//        }
        FileSink file = new FileSinkDOT(true);
        try {
            file.writeAll(_graph, outputName);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
