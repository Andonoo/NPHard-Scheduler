package io;

import org.graphstream.graph.implementations.AdjacencyListGraph;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceDOT;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InputHandler {
    private final String _fileName;
    private AdjacencyListGraph _graph;
    private final int _numProcessors;
    private final List<String> _options = new ArrayList<String>();
    private String _outputFileName;

    public InputHandler(String[] input) {
        input = new String[]{"./example-dot-files/Outtree21.dot", "4"};
        _fileName = input[0];
        _numProcessors = Integer.parseInt(input[1]);
        if (input.length > 2) {
            for (int i = 2; i < input.length; i++) {
                _options.add(input[i]);
            }
        }
        setOutputFileName();
        createGraph();
    }

    private void createGraph() {
        _graph = new DefaultGraph(_outputFileName);
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

    public int getProcessors() {
        return _numProcessors;
    }

    private void setOutputFileName() {
        int outputIndex = _options.indexOf("-o");
        if (outputIndex != -1) {
            String desiredOutputName = _options.get(outputIndex + 1);
            _outputFileName = _fileName.substring(0, _fileName.lastIndexOf(File.separator) + 1) + desiredOutputName + "-output.dot";
        } else {
            _outputFileName = _fileName.substring(0, _fileName.indexOf(".dot")) + "-output.dot";
        }
    }

    public boolean produceGUI() {
        return _options.contains("-v");
    }
}
