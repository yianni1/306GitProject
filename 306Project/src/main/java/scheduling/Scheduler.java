package scheduling;


import graph.TaskGraph;
import graph.TaskNode;

import java.util.List;

public abstract class 	Scheduler {

	protected List<TaskNode> schedulableNodes;
	protected Schedule schedule;

	public Scheduler(TaskGraph graph, int processors) {
		schedule = new Schedule(processors, graph);
		schedulableNodes = schedule.getSchedulableNodes();
	}

	public abstract Schedule createSchedule();
}




