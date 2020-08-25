package javafx;

import domain.PartialSchedule;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.text.DecimalFormat;


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


    private InfoTracker _infoTracker;
    private boolean pollingRanOnce = false;
    private double startTime;
    private Timeline timerHandler;
    private double currentTime;
    private String timeElapsed;

    public void setStatus(String status) {
        _status.setText(status);
    }

    public void setSearchesMade(int searchesMade) {
        _searchesMade.setText(String.valueOf(searchesMade));
    }

    public void setCurrentBest(int currentBest) {
        _currentBest.setText(String.valueOf(currentBest));
    }

    public void setInputGraph(String inputGraph) {
        _inputGraph.setText(inputGraph);
    }

    public void setProcessorsCount(int processorsCount) {
        _processorsCount.setText(String.valueOf(processorsCount));
    }

    public void setCpuCount(int cpuCount) {
        _cpuCount.setText(String.valueOf(cpuCount));
    }

    public void setInputHandler(InfoTracker infoTracker) {
        _infoTracker = infoTracker;
    }

    public void init() {
        startTimer();
        setInputGraph(_infoTracker.get_fileName());
        setProcessorsCount(_infoTracker.get_processors());
        setCpuCount(_infoTracker.get_cores());
//        startPolling();
    }

//    private void startPolling() {
//        Timeline poller = new Timeline(new KeyFrame(Duration.millis(50), event -> {
//            if(_infoTracker.isFinished()){
//                setStatus("Done");
////                stopTimer();
//
//                if(pollingRanOnce) {
//                    return;
//                }
//            }
//
//            if(_infoTracker.get_currentBest() != -1){
//                setCurrentBest(_infoTracker.get_currentBest());
//                setSchedule(_infoTracker.get_scheduledToBeDisplayed());
//            }
//            setSearchesMade(_infoTracker.getSearchesMade());
//
//            pollingRanOnce = true;
//        }));
//        poller.setCycleCount(Animation.INDEFINITE);
//        poller.play();
//    }

    private void setSchedule(PartialSchedule scheduledToBeDisplayed) {
        //display the schedule
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
//    public void stopTimer(){
//        timerHandler.stop();
//    }
}