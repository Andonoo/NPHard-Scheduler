package javafx;

import domain.PartialSchedule;
import domain.TaskNode;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.File;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Map;


public class Controller {

    @FXML
    private Text _status;

    @FXML
    private Text _timeElapsed;

    @FXML
    private Text _searchesMade;

    @FXML
    private Text _currentBest;

    @FXML
    private Label _inputGraph;

    @FXML
    private Text _processorsCount;

    @FXML
    private Text _cpuCount;

    @FXML
    private ImageView graphImage;

    @FXML
    private VBox scheduleBox;

    private InfoTracker _infoTracker;
    private boolean pollingRanOnce = false;
    private double startTime;
    private Timeline timerHandler;
    private double currentTime;
    private String timeElapsed;
    private ScheduleDisplayer<Number, String> scheduleDisplayer;

    public void setStatus(String status) {
        _status.setText(status);
    }

    /**
     * This function is called from polling and updates the number of searches made. Once the number of searches
     * reaches a million, it will shorten it down to 3sf using 'M'.
     *
     * @param numSearches the number of searches made
     */
    public void setSearchesMade(double numSearches) {
        String searchesMade;
        if (numSearches > 1000000) {
            DecimalFormat df = new DecimalFormat("0.00");
            searchesMade = df.format(numSearches / 1000000) + "M";
        } else {
            searchesMade = String.valueOf((int) numSearches);
        }
        _searchesMade.setText(searchesMade);
    }

    public void setCurrentBest(int currentBest) {
        _currentBest.setText(String.valueOf(currentBest));
    }

    public void setInputHandler(InfoTracker infoTracker) {
        _infoTracker = infoTracker;
    }

    /**
     * Initialise all the GUI. Starts the timer as soon as it opens.
     * Sets input file name, processor count, CPU count, input graph, and the current best output graph.
     * Once all that is set, polling will start to continuously update the GUI
     */
    public void init() {
        startTimer();
        _inputGraph.setText(_infoTracker.getFileName());
        _processorsCount.setText(String.valueOf(_infoTracker.getProcessors()));
        _cpuCount.setText(String.valueOf(_infoTracker.getCores()));
        initialiseOutputGraph();
        File file = new File("graph.png");
        graphImage.setImage(new Image(file.toURI().toString()));
        startPolling();
    }

    /**
     * Function to initialise the schedule visualisation. It will set the y axis based on the number
     * of processors the user has chosen and initialise the x axis as the duration.
     */
    private void initialiseOutputGraph() {

        String[] processors = new String[_infoTracker.getProcessors()];
        for (int i = 0; i < _infoTracker.getProcessors(); i++) {
            processors[i] = String.valueOf(i + 1);
        }

        // Create x axis
        final NumberAxis durationAxis = new NumberAxis();
        durationAxis.setLabel("Duration");
        durationAxis.setMinorTickCount(4);

        // Create y axis
        final CategoryAxis processorAxis = new CategoryAxis();
        processorAxis.setLabel("Processor Number");
        processorAxis.setCategories(FXCollections.observableArrayList(Arrays.asList(processors)));

        // Create output graph
        scheduleDisplayer = new ScheduleDisplayer<Number, String>(durationAxis, processorAxis);
        scheduleDisplayer.setBlockHeight(200 / (_infoTracker.getProcessors()));
        scheduleDisplayer.getStylesheets().add(getClass().getResource("/SchedulerDisplayer.css").toExternalForm());
        scheduleBox.getChildren().add(scheduleDisplayer);
    }

    /**
     * Every 50 milliseconds, the GUI will be updated based on the algorithm state.
     * Only update the Gantt Chart if there is a new current best is found. This is to help performance.
     * If no partial schedule is found, set the schedule to the schedule found by the greedy algorithm. This prevents
     * the GUI from ever being empty.
     * Once the algorithm is done, set the status to DONE.
     */
    private void startPolling() {
        Timeline poller = new Timeline(new KeyFrame(Duration.millis(50), event -> {
            if (_infoTracker.getIsFinished()) {
                setStatus("DONE");
                stopTimer();
                if (pollingRanOnce) {
                    return;
                }
            }

            if (_infoTracker.getCurrentBestHasChanged()) {
                setCurrentBest(_infoTracker.getCurrentBest());
                XYChart.Series[] greedyData = _infoTracker.getGreedyData();
                PartialSchedule partialSchedule = _infoTracker.getScheduledToBeDisplayed();

                if (partialSchedule == null) {
                    updateScheduleDisplayer(greedyData);
                } else {
                    parsePartialSchedule(partialSchedule);
                }
                _infoTracker.setCurrentBestHasChanged(false);
            }
            int numSearches = _infoTracker.getSearchesMade();
            setSearchesMade(numSearches);

            pollingRanOnce = true;
        }));
        poller.setCycleCount(Animation.INDEFINITE);
        poller.play();
    }

    private void updateScheduleDisplayer(Series[] scheduleData) {
        // Clear and rewrite series onto the chart
        scheduleDisplayer.getData().clear();
        for (Series series : scheduleData) {
            scheduleDisplayer.getData().add(series);
        }
    }

    /**
     * Parses a partial schedule so that it can be visualised by ScheduleDisplayer. This will allow tasks to be
     * seen as blocks (based on time scheduled).
     *
     * @param scheduledToBeDisplayed the new schedule that should be visualised as the current best
     */
    private void parsePartialSchedule(PartialSchedule scheduledToBeDisplayed) {
        // New array of series to write onto
        Series[] seriesArray = new Series[_infoTracker.getProcessors()];

        // Initializing series obj
        for (int i = 0; i < _infoTracker.getProcessors(); i++) {
            seriesArray[i] = new Series();
        }

        Map<TaskNode, PartialSchedule> schedulings = scheduledToBeDisplayed.getSchedulings();

        for (TaskNode task : schedulings.keySet()) {
            PartialSchedule nodeScheduling = schedulings.get(task);

            int startTime = (int) (nodeScheduling.getScheduledTaskEndTime() - task.getWeight());
            int processorNumber = nodeScheduling.getProcessorIndex();
            Data newData = new Data(startTime, String.valueOf(processorNumber + 1),
                    new ScheduleDisplayer.ExtraData(task, "task"));

            seriesArray[processorNumber].getData().add(newData);
        }
        updateScheduleDisplayer(seriesArray);
    }

    /**
     * Timer to show the elapsed time. This is called as soon as the algorithm starts and is updated
     * every 0.05 seconds.
     */
    public void startTimer() {
        startTime = System.currentTimeMillis();
        timerHandler = new Timeline(new KeyFrame(Duration.seconds(0.05), new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                currentTime = System.currentTimeMillis();
                DateFormat timeFormat = new SimpleDateFormat("mm:ss:SSS");
                timeElapsed = timeFormat.format((currentTime - startTime));
                _timeElapsed.setText(timeElapsed);
            }
        }));
        timerHandler.setCycleCount(Timeline.INDEFINITE);
        timerHandler.play();
    }

    public void stopTimer() {
        timerHandler.stop();
    }
}