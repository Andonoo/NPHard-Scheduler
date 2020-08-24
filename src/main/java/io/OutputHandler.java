package io;

import domain.PartialSchedule;
import domain.TaskNode;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.AdjacencyListGraph;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.stream.file.FileSink;
import org.graphstream.stream.file.FileSinkDOT;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

public class OutputHandler {

    /**
     * Populates provided graph with the scheduling information from the schedule, before passing it off to be
     * parsed into an output file
     * @param schedule
     * @param graph
     */
    public void createOutputFile(PartialSchedule schedule, Graph graph) {
        Map<TaskNode, PartialSchedule> schedulings = schedule.getSchedulings();

        for (TaskNode task: schedulings.keySet()) {
            Node node = graph.getNode(task.getId());
            PartialSchedule nodeScheduling = schedulings.get(task);
            node.setAttribute("Processor", nodeScheduling.getProcessorIndex() + 1);
            node.setAttribute("Start", nodeScheduling.getScheduledTaskEndTime() - task.getWeight());
            node.setAttribute("Weight", task.getWeight());
        }

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
