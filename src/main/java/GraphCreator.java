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

    public void addToGraph(String inputLine) {



        System.out.println(inputLine);
        if (inputLine.contains("->")) {
            // handle edges
        }
    }
}
