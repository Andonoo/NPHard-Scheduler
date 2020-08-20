package Algorithm;

public class SolutionNode {

    private String _taskID;
    private int _processor;
    private SolutionNode _next;

    SolutionNode(String taskID, int processor, SolutionNode next) {
        _taskID = taskID;
        _processor = processor;
        _next = next;
    }

    public void setProcessor(int nextProcessor) {
        _processor = nextProcessor;
    }

    public void setNext(SolutionNode nextNode) {
        _next = nextNode;
    }
}
