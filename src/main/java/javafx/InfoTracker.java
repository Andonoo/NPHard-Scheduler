package javafx;

import domain.PartialSchedule;

public class InfoTracker {
    int _currentBest = -1;
    PartialSchedule _scheduledToBeDisplayed;
    int searchesMade;
    long completeSchedules;
    boolean isFinished = false;
    String _fileName;
    int _processors;
    int _cores;
    boolean _currentBestHasChanged = false;



    public InfoTracker(String fileName, int processors, int cores) {
        _fileName = fileName;
        _processors = processors;
        _cores = cores;
    }

    public int get_currentBest() {
        return _currentBest;
    }

    public void set_currentBest(int _currentBest) {
        this._currentBest = _currentBest;
    }

    public boolean get_currentBestHasChanged() {
        return _currentBestHasChanged;
    }

    public void set_currentBestHasChanged(boolean currentBestHasChanged) {
        this._currentBestHasChanged = currentBestHasChanged;
    }

    public PartialSchedule get_scheduledToBeDisplayed() {
        return _scheduledToBeDisplayed;
    }

    public void set_scheduledToBeDisplayed(PartialSchedule _scheduledToBeDisplayed) {
        this._scheduledToBeDisplayed = _scheduledToBeDisplayed;
    }

    public int getSearchesMade() {
        return searchesMade;
    }

    public void setSearchesMade(int searchesMade) {
        this.searchesMade = searchesMade;
    }

    public long getCompleteSchedules() {
        return completeSchedules;
    }

    public void setCompleteSchedules(long completeSchedules) {
        this.completeSchedules = completeSchedules;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    public String get_fileName() {
        return _fileName;
    }

    public void set_fileName(String _fileName) {
        this._fileName = _fileName;
    }

    public int get_processors() {
        return _processors;
    }

    public void set_processors(int _processors) {
        this._processors = _processors;
    }

    public int get_cores() {
        return _cores;
    }

    public void set_cores(int _cores) {
        this._cores = _cores;
    }
}
