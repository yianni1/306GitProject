package scheduling;

import graph.TaskGraph;
import javafx.concurrent.Task;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by olive on 14/08/2018.
 */
public class ParallelSchedule extends Schedule implements Serializable {

    private ParallelThread thread;
    private int upperBound;
    private boolean finishedDFS = false;
    private TaskGraph graph;
    private int minDepth;

    /**
     * Constructor initliases graph, and thread.
     * This constructor is to be called
     * @param processorNumber
     * @param graph
     */
    public ParallelSchedule(int processorNumber, TaskGraph graph, int upperBound) {
        super(processorNumber, graph);
        this.upperBound = upperBound;
        setThread(0);
        this.graph = graph;
    }

//    public ParallelSchedule(int processorNumber, TaskGraph graph, ParallelThread thread, int upperBound) {
//        super(processorNumber, graph);
//        this.thread = thread;
//        ParallelDFS dfs = new ParallelDFS(this, upperBound);
//        thread.addScheduler(dfs);
//    }
//
    public void setThread(ParallelSchedule schedule, int minDepth) {
        this.thread = schedule.thread;
        ParallelDFS dfs = new ParallelDFS(this, upperBound, graph, minDepth);
        thread.addScheduler(dfs, this, minDepth);
    }

    public void setThread(int minDepth) {
        thread = new ParallelThread();
        ParallelDFS dfs = new ParallelDFS(this, upperBound, graph, minDepth);
        thread.addScheduler(dfs, this, minDepth);
        this.minDepth = minDepth;
    }



    public void run() {
        if (!thread.isAlive()) {
            thread.start();
        }
    }

    public ParallelThread getThread() {
        return thread;
    }

    void setFinishedDFS(ParallelThread thread) {
        finishedDFS = true;
//        thread.interrupt();
    }

    public boolean getFinished() {
        return finishedDFS;
    }
}
