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
import javafx.scene.chart.XYChart.Series;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    private ScheduleDisplayer<Number,String> scheduleDisplayer;

    public void setStatus(String status) {
        _status.setText(status);
    }

    public void setSearchesMade(String searchesMade) {
        _searchesMade.setText(searchesMade);
    }

    public void setCurrentBest(int currentBest) {
        _currentBest.setText(String.valueOf(currentBest));
    }

    public void setInputHandler(InfoTracker infoTracker) {
        _infoTracker = infoTracker;
    }

    public void init() {
        startTimer();
        _inputGraph.setText(_infoTracker.get_fileName());
        _processorsCount.setText(String.valueOf(_infoTracker.get_processors()));
        _cpuCount.setText(String.valueOf(_infoTracker.get_cores()));
        initialiseOutputGraph();
        File file = new File("graph.png");
        graphImage.setImage(new Image(file.toURI().toString()));
        startPolling();
    }

    private void initialiseOutputGraph() {

//        List<String> processors = new ArrayList<>();
//        for (int i = 0;i<_infoTracker.get_processors();i++){
//            processors.add("Processor " + (i + 1));
//        }
        String[] processors = new String[_infoTracker.get_processors()];
        for (int i = 0;i<_infoTracker.get_processors();i++){
            processors[i]= String.valueOf(i + 1);
        }

        // create x axis
        final NumberAxis durationAxis = new NumberAxis();
        durationAxis.setLabel("Duration");
//        durationAxis.setTickLabelFill(Color.rgb(254,89,21));
        durationAxis.setMinorTickCount(4);

        // create y axis
        final CategoryAxis processorAxis = new CategoryAxis();
        processorAxis.setLabel("Processor Number");
//        durationAxis.setTickLabelFill(Color.rgb(254,89,21));
//        processorAxis.setTickLabelGap(1);
//        processorAxis.setCategories(FXCollections.<String>observableArrayList(String.valueOf(processors)));
        processorAxis.setCategories(FXCollections.<String>observableArrayList(Arrays.asList(processors)));


        // create output graph
        scheduleDisplayer = new ScheduleDisplayer<Number, String>(durationAxis, processorAxis);
        scheduleDisplayer.setBlockHeight(200 / (_infoTracker.get_processors()));

        scheduleDisplayer.getStylesheets().add(getClass().getResource("/SchedulerDisplayer.css").toExternalForm());
        scheduleBox.getChildren().add(scheduleDisplayer);
    }

    private void startPolling() {
        Timeline poller = new Timeline(new KeyFrame(Duration.millis(50), event -> {
            if(_infoTracker.isFinished()){
                setStatus("DONE");
                stopTimer();
                if(pollingRanOnce) {
                    return;
                }
            }

            if(_infoTracker.get_currentBestHasChanged()){
                setCurrentBest(_infoTracker.get_currentBest());
                updateScheduleDisplayer(_infoTracker.get_scheduledToBeDisplayed());
                _infoTracker.set_currentBestHasChanged(false);
            }
            int numSearches = _infoTracker.getSearchesMade();
            setSearchesMade(numSearches > 1000 ? numSearches/1000 + "k" : String.valueOf(numSearches));

            pollingRanOnce = true;
        }));
        poller.setCycleCount(Animation.INDEFINITE);
        poller.play();
    }

    private void updateScheduleDisplayer(PartialSchedule scheduledToBeDisplayed) {

        // new array of series to write onto
        Series[] seriesArray = new Series[_infoTracker.get_processors()];

        // initializing series obj
        for (int i=0;i<_infoTracker.get_processors();i++){
            seriesArray[i]=new Series();
        }

        Map<TaskNode, PartialSchedule> schedulings = scheduledToBeDisplayed.getSchedulings();

        for (TaskNode task: schedulings.keySet()) {
            PartialSchedule nodeScheduling = schedulings.get(task);

            int startTime = (int)(nodeScheduling.getScheduledTaskEndTime() - task.getWeight());
            int processorNumber = nodeScheduling.getProcessorIndex();
            Data newData = new Data(startTime, String.valueOf(processorNumber + 1),
                    new ScheduleDisplayer.ExtraData(task, "task-style"));

            seriesArray[processorNumber].getData().add(newData);
        }
        //clear and rewrite series onto the chart
        scheduleDisplayer.getData().clear();
        for (Series series: seriesArray){
            scheduleDisplayer.getData().add(series);
        }
    }

    public void startTimer(){

        startTime=System.currentTimeMillis();
        timerHandler = new Timeline(new KeyFrame(Duration.seconds(0.05), new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                currentTime=System.currentTimeMillis();
                DecimalFormat df = new DecimalFormat("0.00");
                timeElapsed = df.format((currentTime-startTime)/1000);
                _timeElapsed.setText(timeElapsed);
            }
        }));
        timerHandler.setCycleCount(Timeline.INDEFINITE);
        timerHandler.play();
    }
    public void stopTimer(){
        timerHandler.stop();
    }
}