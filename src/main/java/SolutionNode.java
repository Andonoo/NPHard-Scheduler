public class SolutionNode {

    private String taskID;
    private int processor;
    private SolutionNode next;

    SolutionNode(String taskID, int processor, SolutionNode next) {
        this.taskID = taskID;
        this.processor = processor;
        this.next = next;
    }

    public void setProcessor(int nextProcessor) {
        processor = nextProcessor;
    }

    public void setNext(SolutionNode nextNode) {
        next = nextNode;
    }
}
