package scheduling;

import graph.TaskGraph;

import java.util.concurrent.TimeUnit;

/**
 * Slave version of DFbnBScheduler to support starting from a partial schedule and notifying master of updates.
 * Written by Kevin.
 */
public class DFBnBSlaveScheduler extends DFBnBScheduler {
    private DFBnBMasterScheduler master;
    private boolean done;

    public DFBnBSlaveScheduler(TaskGraph graph, int processors, Schedule schedule, int upperBound, DFBnBMasterScheduler master) {
        super(graph, processors);
        this.master = master;
        this.schedule = schedule;
        this.schedulableNodes = schedule.getSchedulableNodes();
        this.minDepth = schedule.getScheduledNodes().size();
        this.depth = minDepth;
        this.upperBound = upperBound;
        this.done = false;
    }

    /**
     * Called by master to notify schedule of a new upper bound found from another thread.
     * @param upperBound the new upper bound.
     */

    public void updateUpperBound(int upperBound) {
        if (upperBound < this.upperBound) {
            this.upperBound = upperBound;
        }
    }

    /**
     * Sends notification to master of a potential new optimal schedule.
     */
    @Override
    public void updateSchedule () {
        if (optimalSchedule != null) {
            master.updateSchedule(optimalSchedule);
        }
    }

    /**
     * Sends notification to master to update number of branches pruned.
     */
    @Override
    public void updateBranchesPruned () {
        master.updateBranchesPruned(branchesPruned);
    }

    /**
     * Sends notification to master to update number of paths.
     */
    @Override
    public void updateNumPaths () {
        master.updateNumPaths(numPaths);
    }

    /**
     * Sends notification to master to signal that this slave has finished running.
     */
    @Override
    public void finish () {
        this.done = true;
        master.finish();
    }

    public boolean isFinished() {
        return done;
    }


}
