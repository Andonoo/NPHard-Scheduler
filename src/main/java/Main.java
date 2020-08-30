import algorithm.GreedyScheduler;
import algorithm.ParallelOptimalScheduler;
import algorithm.Scheduler;
import algorithm.SequentialOptimalScheduler;
import domain.TaskNode;
import io.CommandLineException;
import io.InputHandler;
import io.OutputHandler;
import javafx.Controller;
import javafx.InfoTracker;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import scala.collection.mutable.SynchronizedSet;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main extends Application {

    static InfoTracker _infoTracker;
    static InputHandler _inputHandler = null;

    public static void main(String[] args) {
        long startTime = System.nanoTime();

        try {
            _inputHandler = new InputHandler(args);
        } catch (CommandLineException e) {
            e.printStackTrace();
        }

        _infoTracker = new InfoTracker(_inputHandler.getFileName(), _inputHandler.getProcessors(), _inputHandler.getCores(), _inputHandler.getGraph());
        if (_inputHandler.produceGUI()) {
            launch(args);
        } else {
            executeAlgorithm();
        }

        long endTime = System.nanoTime();

        System.out.println("Time taken: " + (endTime-startTime)/1000000);
        System.exit(0);
    }

    public static void executeAlgorithm() {
        GreedyScheduler greedyScheduler = _inputHandler.produceGUI() ? new GreedyScheduler(_inputHandler.getGraph(), _inputHandler.getProcessors(), _infoTracker) :
                new GreedyScheduler(_inputHandler.getGraph(), _inputHandler.getProcessors());
        greedyScheduler.executeAlgorithm();

        Scheduler optimalScheduler;

        if (_infoTracker.getCores() == 1) {
            optimalScheduler = _inputHandler.produceGUI() ?
                    new SequentialOptimalScheduler(greedyScheduler.getTopologicallyOrderedTaskNodes(), _inputHandler.getProcessors(), _infoTracker) :
                    new SequentialOptimalScheduler(greedyScheduler.getTopologicallyOrderedTaskNodes(), _inputHandler.getProcessors());
        } else {
            optimalScheduler = _inputHandler.produceGUI() ?
                    new ParallelOptimalScheduler(greedyScheduler.getTopologicallyOrderedTaskNodes(), _inputHandler.getProcessors(), _inputHandler.getCores(), _infoTracker) :
                    new ParallelOptimalScheduler(greedyScheduler.getTopologicallyOrderedTaskNodes(), _inputHandler.getProcessors(), _inputHandler.getCores());
        }

        Map<TaskNode, Double> bottomLevels = getBottomLevels(greedyScheduler.getTopologicallyOrderedTaskNodes());

        boolean moreOptimalFound = optimalScheduler.executeBranchAndBoundAlgorithm(greedyScheduler.getSolutionLength(), bottomLevels);

        _infoTracker.setIsFinished(true);

        OutputHandler outputHandler = new OutputHandler();
        if (moreOptimalFound) {
            outputHandler.createOutputFile(optimalScheduler.getSolution(), _infoTracker.getGraph());
            System.out.println("Length: " + optimalScheduler.getSolution().getScheduleLength());
        } else {
            outputHandler.createOutputFile(greedyScheduler.getSolution());
            System.out.println("Length: " + greedyScheduler.getSolutionLength());
        }
    }

    private static Map<TaskNode, Double> getBottomLevels(List<TaskNode> tasks) {
        Map<TaskNode, Double> minBottomLevels = new HashMap<TaskNode, Double>();

        for (TaskNode task: tasks) {
            if (task.getDependencies().length == 0) {
                bottomLevelRecurse(task, minBottomLevels);
            }
        }

        return minBottomLevels;
    }

    private static double bottomLevelRecurse(TaskNode t, Map<TaskNode, Double> bottomLevels) {
        if (t.getDependents().size() == 0) {
            bottomLevels.put(t, 0.0);
            return 0.0;
        }

        List<TaskNode> dependents = t.getDependents();
        TaskNode firstDependent = dependents.get(0);
        double minBottomLevel = bottomLevelRecurse(firstDependent, bottomLevels) + firstDependent.getWeight() + firstDependent.getDependencyEdgeWeight(t);
        for (int i = 1; i < dependents.size(); i++) {
            minBottomLevel = Math.min(minBottomLevel, bottomLevelRecurse(dependents.get(i), bottomLevels) +
                    dependents.get(i).getWeight() + dependents.get(i).getDependencyEdgeWeight(t));
        }

        bottomLevels.put(t, minBottomLevel);
        return minBottomLevel;
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("main.fxml"));
        Controller controller = new Controller();
        loader.setController(controller);
        Parent root = loader.load();

        Scene scene = new Scene(root);
        controller.setInputHandler(_infoTracker);
        controller.init();

        new Thread(Main::executeAlgorithm).start();
        primaryStage.setTitle("Scheduler visualisation");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);

        primaryStage.setOnCloseRequest(event -> System.exit(0));
        primaryStage.show();
    }
}
