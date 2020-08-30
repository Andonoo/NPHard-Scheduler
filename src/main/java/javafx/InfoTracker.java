package javafx;

import domain.PartialSchedule;
import javafx.scene.chart.XYChart;
import org.graphstream.graph.implementations.AdjacencyListGraph;

/**
 * This class was created for the purposes of having a centralised place to keep track of all necessary data
 * while searching for the optimal solution. The javafx GUI application controller will poll InfoTracker to see
 * if any information it should currently be displaying has been updated - if so, the controller will re-render
 * the GUI to match the current state and information of the searches.
 */
public class InfoTracker {
    private int _currentBest = -1;
    private PartialSchedule _scheduledToBeDisplayed = null;
    private XYChart.Series[] _greedyData;
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

    public void setCurrentBest(int currentBest) {
        _currentBest = currentBest;
    }

    public boolean getCurrentBestHasChanged() {
        return _currentBestHasChanged;
    }

    public void setCurrentBestHasChanged(boolean currentBestHasChanged) {
        _currentBestHasChanged = currentBestHasChanged;
    }

    public PartialSchedule getScheduledToBeDisplayed() {
        return _scheduledToBeDisplayed;
    }

    public void setScheduledToBeDisplayed(PartialSchedule scheduledToBeDisplayed) {
        _scheduledToBeDisplayed = scheduledToBeDisplayed;
    }

    public int getSearchesMade() {
        return _searchesMade;
    }

    public void setSearchesMade(int searchesMade) {
        _searchesMade = searchesMade;
    }

    public boolean getIsFinished() {
        return _isFinished;
    }

    public void setIsFinished(boolean isFinished) {
        _isFinished = isFinished;
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

    public void setGreedyData(XYChart.Series[] seriesArray) {
        _greedyData = seriesArray;
    }

    public XYChart.Series[] getGreedyData() {
        return _greedyData;
    }
}
