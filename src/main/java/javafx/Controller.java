package javafx;

import domain.PartialSchedule;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.File;
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

    @FXML
    private ImageView graphImage;

    private InfoTracker _infoTracker;
    private boolean pollingRanOnce = false;
    private double startTime;
    private Timeline timerHandler;
    private double currentTime;
    private String timeElapsed;

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
        File file = new File("graph.png");
        graphImage.setImage(new Image(file.toURI().toString()));
        startPolling();
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
                setSchedule(_infoTracker.get_scheduledToBeDisplayed());
                _infoTracker.set_currentBestHasChanged(false);
            }
            int numSearches = _infoTracker.getSearchesMade();
            setSearchesMade(numSearches > 1000 ? numSearches/1000 + "k" : String.valueOf(numSearches));

            pollingRanOnce = true;
        }));
        poller.setCycleCount(Animation.INDEFINITE);
        poller.play();
    }

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
    public void stopTimer(){
        timerHandler.stop();
    }
}