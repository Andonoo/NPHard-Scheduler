import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        System.out.println("hello world!");
    }

    /*
    parseInput() will utilise the dot file inputted by the user and parse it to a usable object using GraphStream
     */
    public void parseInput() {

    }

    /*
    executeAlgorithm() will return a valid schedule
     */
    public ArrayList<SolutionNode> executeAlgorithm(/* Graph G */) {

        /*
        Let D be an adjacency list where D{V} is the dependencies of V
        sortTopologically(G);
        For all v in V
            Add output of scheduleByGreedy(v) to ArrayList
         */

        return null;
    }

    /*
    sortTopologically() will return a topological ordering of the vertices in Graph G
     */
    public ArrayList<?> sortTopologically(/* Graph G */) {

        /*
        Let topOrder be an array of vertices/nodes
        While V is not empty
            Find a vertex v in V with no outgoing edges
            Pop v from V
            Add v to topological order
        Reverse topOrder
         */

        return null;
    }

    /*
    selectByGreedy will schedule a task based on the greedy heuristic: Earliest Start Time
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

    /*
    calculateStartTime() will return the earliest possible scheduling of task in a specified processor
     */
    public void calculateStartTime(/* Processor p, Node v */) {

        /*
        Let d = D(v)
        int startTime = p.getCurrentTime();
        For d' in d
            Find SolutionNode x that has d'
            If !p.hasTask(d')
                startTime = max{startTime, x.processor + w(d', v)
         */
    }

    /*
    printOutput() will read the valid array of SolutionNodes and parse it into the dot output file
     */
    public void printOutput() {

    }
}
