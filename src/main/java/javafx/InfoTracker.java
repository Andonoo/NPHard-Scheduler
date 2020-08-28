package javafx;

import domain.PartialSchedule;
import org.graphstream.graph.implementations.AdjacencyListGraph;

public class InfoTracker {
    int _currentBest = -1;
    PartialSchedule _scheduledToBeDisplayed;
    int searchesMade;
    boolean isFinished = false;
    String _fileName;
    int _processors;
    int _cores;
    boolean _currentBestHasChanged = false;
    AdjacencyListGraph _graph;



    public InfoTracker(String fileName, int processors, int cores, AdjacencyListGraph graph) {
        _fileName = fileName;
        _processors = processors;
        _cores = cores;
        _graph = graph;
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

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    public String get_fileName() {
        return _fileName;
    }

    public int get_processors() {
        return _processors;
    }

    public int get_cores() {
        return _cores;
    }

    public AdjacencyListGraph getGraph() {
        return _graph;
    }
}
