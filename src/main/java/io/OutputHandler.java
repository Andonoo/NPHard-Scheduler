package io;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.AdjacencyListGraph;
import org.graphstream.stream.file.FileSink;
import org.graphstream.stream.file.FileSinkDOT;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class OutputHandler {

    public OutputHandler(AdjacencyListGraph graph) {
        createOutputFile(graph);
    }

    public void createOutputFile(Graph graph) {

        FileSink file = new FileSinkDOT(true);
        try {
            file.writeAll(graph, graph.getId());
        } catch (IOException e) {
            e.printStackTrace();
        }
        formatDotFile(graph.getId());
    }

    public void formatDotFile(String fileName) {

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
                    head.append(line, 0, index)
                            .append("\"")
                            .append(fileName, fileName.lastIndexOf(File.separator) + 1, fileName.lastIndexOf("."))
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
