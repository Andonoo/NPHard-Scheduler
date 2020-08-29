package javafx;

import domain.PartialSchedule;
import org.graphstream.graph.implementations.AdjacencyListGraph;

public class InfoTracker {
    private int _currentBest = -1;
    private PartialSchedule _scheduledToBeDisplayed;
    private int _searchesMade;
    private boolean _isFinished = false;
    private String _fileName;
    private final int _processors;
    private final int _cores;
    private boolean _currentBestHasChanged = false;
    private final AdjacencyListGraph _graph;


    public InfoTracker(String fileName, int processors, int cores, AdjacencyListGraph graph) {
        _fileName = fileName;
        _processors = processors;
        _cores = cores;
        _graph = graph;
    }

    public int getCurrentBest() {
        return _currentBest;
    }

    public void setCurrentBest(int _currentBest) {
        this._currentBest = _currentBest;
    }

    public boolean getCurrentBestHasChanged() {
        return _currentBestHasChanged;
    }

    public void setCurrentBestHasChanged(boolean currentBestHasChanged) {
        this._currentBestHasChanged = currentBestHasChanged;
    }

    public PartialSchedule getScheduledToBeDisplayed() {
        return _scheduledToBeDisplayed;
    }

    public void setScheduledToBeDisplayed(PartialSchedule _scheduledToBeDisplayed) {
        this._scheduledToBeDisplayed = _scheduledToBeDisplayed;
    }

    public int getSearchesMade() {
        return _searchesMade;
    }

    public void setSearchesMade(int _searchesMade) {
        this._searchesMade = _searchesMade;
    }

    public boolean getIsFinished() {
        return _isFinished;
    }

    public void setIsFinished(boolean _isFinished) {
        this._isFinished = _isFinished;
    }

    public String getFileName() {
        return _fileName;
    }

    public int getProcessors() {
        return _processors;
    }

    public int getCores() {
        return _cores;
    }

    public AdjacencyListGraph getGraph() {
        return _graph;
    }
}
