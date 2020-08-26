package io;

import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.AdjacencyListGraph;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.stream.file.FileSinkImages;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceDOT;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InputHandler {
    private String _fileName;
    private AdjacencyListGraph _graph;
    private int _numProcessors;
    private int _numCores;
    private final List<String> _options = new ArrayList<String>();
    private String _outputFileName;

    public InputHandler(String[] input ) throws CommandLineException {

        // Checks that all compulsory arguments are inputted
        if (input.length < 2) {
            throw new CommandLineException("Please enter a valid input file and number of processors");
        }

        // Checks that the input file is valid
        if (!input[0].contains(".dot")){
            throw new CommandLineException("Please enter a valid input file (.dot file)");
        } else {
            _fileName = input[0];
        }

        if(!new File(_fileName).isFile()) {
            throw new CommandLineException("Please enter a valid input file that exists");
        }

        // Checks that the number of processors is actually a number
        try {
            _numProcessors = Integer.parseInt(input[1]);
        } catch (Exception e){
            throw new CommandLineException("Please enter an integer for the number of processors to be used");
        }

        // Check that number of processors is at least 1
        if (_numProcessors < 1) {
            throw new CommandLineException("Please enter a valid number for the number of processors (at least 1)");
        }

        if (input.length > 2) {
            for (int i = 2; i < input.length; i++) {
                _options.add(input[i]);
            }
        }

        setOutputFileName();

        _numCores = setCores();

        // Overwrites the output file
        if(new File(_outputFileName).isFile()) {
            System.out.println("WARNING: OUTPUT FILE ALREADY EXISTS. FILE WILL BE OVERWRITTEN.");
        }

        createGraph();
    }

    private void createGraph() {
        _graph = new DefaultGraph(_outputFileName);

        FileSource fs = new FileSourceDOT();
        FileSinkImages pic = new FileSinkImages(FileSinkImages.OutputType.PNG, FileSinkImages.Resolutions.VGA);
        pic.setResolution(500,500);
        try {
            fs.addSink(_graph);
            fs.readAll(_fileName);
            _graph.addAttribute("ui.stylesheet", styleSheet);
            for (Node n: _graph) {
                n.addAttribute("ui.style", "size: 15px; fill-color: #D22; text-alignment: center; text-style: bold; text-size: 25px;");
                n.addAttribute("ui.label", n.getId() );

            }
            pic.setLayoutPolicy(FileSinkImages.LayoutPolicy.COMPUTED_FULLY_AT_NEW_IMAGE);
            pic.setAutofit(true);
            pic.writeAll(_graph, "graph.png");
            for (Node n: _graph) {
                n.removeAttribute("ui.label");
                n.removeAttribute("ui.style");
            }
            _graph.removeAttribute("ui.stylesheet");

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

    public int getCores() { return _numCores; }

    public int setCores() throws CommandLineException {
        int coresIndex = _options.indexOf("-p");

        if (coresIndex != -1) {
            try {
                if (_options.get(coresIndex + 1).startsWith("-")) {
                    // e.g. -p -v or -p -o, what about -p -
                    return 1;
                }
            }
            catch (Exception e) {
                // -p is the last argument
                return 1;
            }

            if (isNumeric(_options.get(coresIndex + 1))) {
                int number = Integer.parseInt(_options.get(coresIndex + 1));
                if (number >= 1 && number <= 4) {
                    // valid number
                    return number;
                }
                throw new CommandLineException("Please enter an integer between 1 to 4 for the number of cores to be used");
            }
            else {
                throw new CommandLineException("Please enter a valid integer for the number of cores to be used");
            }
        }
        else {
            return 1;
        }
    }

    public boolean isNumeric(String input) {
        if (input == null) {
            return false;
        }
        try {
            int numCores = Integer.parseInt(input);
        }
        catch (NumberFormatException e) {
            return false;
        }
        return true;
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

    public String getFileName() {
        String displayedName = _fileName.substring(_fileName.lastIndexOf(File.separator) + 1);
        return displayedName;
    }

    protected String styleSheet =
            "edge { size: 2px; shape: cubic-curve; arrow-size: 15px, 8px; fill-color: white;}" +
                    "graph { padding: 45px; fill-color: #EF8354; }";
}
