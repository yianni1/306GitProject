package scheduling;

import graph.TaskGraph;

public class DFBnBSlaveScheduler extends DFBnBScheduler {
    public DFBnBSlaveScheduler(TaskGraph graph, int processors, Schedule schedule, int upperBound) {
        super(graph, processors);
        this.schedule = schedule;
        this.schedulableNodes = schedule.getSchedulableNodes();
        this.minDepth = schedule.getScheduledNodes().size();
        this.depth = minDepth;
        this.upperBound = upperBound;
    }
}
