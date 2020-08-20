import Algorithm.Scheduler;
import IO.InputHandler;

public class Main {

    public static void main(String[] args) {
        InputHandler inputParser = new InputHandler(args);
        if (inputParser.produceGUI()) {
            //javafx shazam
            System.out.println("Thanks Francis");
        }
        Scheduler scheduler = new Scheduler(inputParser.getGraph(), inputParser.getProcessors());
        scheduler.executeAlgorithm();
    }
}
