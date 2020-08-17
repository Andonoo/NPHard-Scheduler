import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.AdjacencyListGraph;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.stream.file.FileSink;
import org.graphstream.stream.file.FileSinkDOT;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceDOT;

import java.io.IOException;

public class FileParser {
    private String _fileName;
    private AdjacencyListGraph _graph;

    public FileParser(String fileName) {
        _fileName = fileName;
        _graph = new DefaultGraph("Input Graph");
        FileSource fs = new FileSourceDOT();
        try {
            fs.addSink(_graph);
            fs.readAll(_fileName);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            fs.removeSink(_graph);
        }
    }

    public AdjacencyListGraph getGraph() {
        return _graph;
    }

    public void createOutputFile(String outputName, Graph graph) {

        for (Node n : graph) {
            n.changeAttribute("Weight", ((Double) n.getAttribute("Weight")).intValue());
        }
        for (Edge e : graph.getEdgeSet()) {
            e.changeAttribute("Weight", ((Double) e.getAttribute("Weight")).intValue());
        }
        FileSink file = new FileSinkDOT(true);
        try {
            file.writeAll(graph, outputName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
