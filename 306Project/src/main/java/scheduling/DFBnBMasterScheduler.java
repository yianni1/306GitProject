package scheduling;

import graph.TaskGraph;
import graph.TaskNode;
import view.VisualisationController;


import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static scheduling.Utilities.deepClone;

/**
 * Master scheduler for parallelisation. Runs BFS to create subtrees and then runs slave schedulers on these subtrees
 * on separate threads.
 */
public class DFBnBMasterScheduler implements  Scheduler {

	//variables for visualisation
	private VisualisationController scheduleListener;

	//variables associated with the partial schedules and normal schedules
	private List<Schedule> partialSchedules = new ArrayList<>();
	private List<DFBnBSlaveScheduler> slaves = new ArrayList<>();
	private Schedule schedule;

	//variables associated with processors
	private int numCores;
	private int processors;

	//variables asssociated with graphs
	private TaskGraph graph;
	private int upperBound;
	private long numPaths;
	private long branchesPruned;

	/**
	 * Constructor which generates an initial empty schedule based on the processors , the graph and the number of cores
	 * it contains
	 * @param graph
	 * @param processors
	 * @param numCores
	 */
	public DFBnBMasterScheduler(TaskGraph graph, int processors, int numCores) {

		this.graph = graph;
		this.processors = processors;
		this.numCores = numCores;

		//creating new schedule with the number of processors and the Graph
		schedule = new Schedule(processors, graph);
		numPaths = 0;
		branchesPruned = 0;
	}

	/**
	 * Creates the optimal schedule through thread and parallelisation which involves assigning threads with the layers
	 * to each of the schedules
	 *
	 * @return optimal schedule
	 */
	public Schedule createSchedule() {


		Schedule greedySchedule = new GreedyScheduler(graph, processors).createSchedule();

		//Default Greedy schedule to be used in the slave if no better schedule can be found which results in the pruning
		//of the branch
		Schedule defaultGreedy = (Schedule) deepClone(greedySchedule);

		// upadate the schedule with the greedy schedule
		updateSchedule(defaultGreedy);

		//intializing the upperbound
		upperBound = greedySchedule.getBound();

		//iterating through the tree by checking if there were any more schedulable tasks
		while (greedySchedule.getScheduledNodes().size() > 0) {
			greedySchedule.removeLastScheduledTask();
		}

		initialisePartialSchedules();

		//Thread pool assigned with the number of cores
		ExecutorService executor = Executors.newFixedThreadPool(numCores);

		//this generates a list of all the optimal schedules which are found through the iterative search
        List<Schedule> locallyOptimalSchedules = new ArrayList<>();

		//generating the number of threads associative to the number of cores
		for (Schedule schedule : partialSchedules) {
			final DFBnBMasterScheduler master = this;

			//running on the executor thread
			executor.execute(new Runnable() {
				@Override
				public void run() {

					//generate a schedular to be inputted into the slave
					DFBnBSlaveScheduler scheduler = new DFBnBSlaveScheduler(schedule.getGraph(), processors, schedule, upperBound, master);

					//add the scheduler to the slave
					slaves.add(scheduler);

					//creates a schedule based on the schedular
					Schedule s = scheduler.createSchedule();

					//If bound found by thread is null through pruning then use default greedy
					if (s == null) {
						locallyOptimalSchedules.add(defaultGreedy);
					} else {
						locallyOptimalSchedules.add(s);
					}

				}
			});
		}

		executor.shutdown();

		//the thread then awaits the termination
		try {
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {

		}

		//finding the optimal bound
		int optimalBound = locallyOptimalSchedules.get(0).getBound();

		//deepcloning the value in the optimal schedule
		Schedule optimalSchedule = (Schedule) deepClone(locallyOptimalSchedules.get(0));

		//searching for bounds less than the optimal bound and replacing those bounds
		for (Schedule schedule : locallyOptimalSchedules) {

			//if bound is lower than the optimal bound , change optimal bound and deepclone the schedule
			if (schedule.getBound() < optimalBound) {
				optimalBound = schedule.getBound();
				optimalSchedule = (Schedule) deepClone(schedule);
			}


		}

		System.out.println("Global optimal solution with bound of " + optimalSchedule.getBound() + " found!");
		return optimalSchedule;

	}

	/**
	 * Sets the ScheduleListener for the GUI visulisation
	 * @param scheduleListener which listens for changes that occur in the alglorithim
	 */
	public void setScheduleListener(VisualisationController scheduleListener) {
		this.scheduleListener = scheduleListener;
	}


	/**
	 * The GUI is updated with a new optimal schedule every time it finds a better solution and it notifies the slaves
	 * of a better bound if it is necessary
	 * @param schedule
	 */
	public void updateSchedule(Schedule schedule) {

		//checking there is schedule listener present
		if (this.scheduleListener != null) {

			//checking the current bound is lower than the upperbound
			if (schedule.getBound() < upperBound) {

				//changes the lowerbound to that of the upperbound
				upperBound = schedule.getBound();

				//notifies the slaves of the updated Upperbound while they perform DFS
				for (DFBnBSlaveScheduler slave : slaves) {
					slave.updateUpperBound(upperBound);
				}

				//updates the schedule in the GUI
				scheduleListener.updateSchedule(schedule);


				try {
					//makes the thread sleep for 200msecs
					TimeUnit.MILLISECONDS.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * This method updates the amount of branches pruned in the GUI
	 * @param branchesPruned (The number of branhces pruned)
	 */
	public void updateBranchesPruned(long branchesPruned) {

		//adds the new amount of branches pruned to that of the previous value
		this.branchesPruned = this.branchesPruned + branchesPruned;

		//checking for there is a schedule listener object actually present
		if (this.scheduleListener != null) {
			scheduleListener.updateBranchesPruned(this.branchesPruned);
		}
	}

	/**
	 * Updates the numOfPaths Statistic in the GUI
	 * @param numPaths , statistic for the number of paths
	 */
	public void updateNumPaths(long numPaths) {

		//adds on the to the previous value of numofpaths
		this.numPaths = this.numPaths + numPaths;

		//check if the ScheduleListener object is present
		if (this.scheduleListener != null) {
			scheduleListener.updateNumPaths(this.numPaths);
		}

	}

	/**
	 * This method finishes the listening for the events of the schedulelistener
	 */
	public void finish() {

		//checking if hte schedule listener object is present
		if (this.scheduleListener != null) {
			for (DFBnBSlaveScheduler slave : slaves) {

				//check if the slave threads have completed there DFS iteration
				if (!slave.isFinished()) {
					return;
				}
			}

			//finished the schedulelistener object
			scheduleListener.finish();
		}
	}

	/**
	 * Gets the list of the partial schedules
	 *
	 * @return partialSchedules
	 */
	public List<Schedule> getPartialSchedules() {
		return partialSchedules;
	}


	/**
	 * initializes the partial schedules through iteratively searching for the size of the list of partial schedules
	 * to be greater than than that of the size of the number of cores
	 */
	public void initialisePartialSchedules() {

		int scheduleIndex = 0;

		//creates partial schedules if the number of partial schedules in the list is less than the number of cores
		while (partialSchedules.size() < numCores) {
			for (int i = 0; i < schedule.getProcessors().size(); i++) {
				try {
					createPartialSchedules(schedule, i);
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
			}

			//moving to the next index value
			schedule = partialSchedules.get(scheduleIndex);
			scheduleIndex++;

		}

		List<Schedule> removeSchedule = new ArrayList<>();

		//Code to remove duplicated initial nodes on partial schedules
		for (int i = 0; i < partialSchedules.size(); i++) {
			for (int j = i; j < partialSchedules.size(); j++)

				//checks if the partial schedules are the same
				if (equals(partialSchedules.get(i), partialSchedules.get(j))) {

					//adds that partial schedule to seperate schedule
					removeSchedule.add(partialSchedules.get(i));
				}
		}

		//removes the schedule from the partial schedule list
		for(Schedule s :removeSchedule) {
			partialSchedules.remove(s);
		}



		//Loop through partial schedules and pick the max layer depth
		int maxSize = 0;
		for (Schedule s : partialSchedules) {
			if ((s.getScheduledNodes().size()) > maxSize) {
				maxSize = s.getScheduledNodes().size();
			}
		}

		//Gets the partial schedules not on the max depth and stores in list
		List<Schedule> removeTheseSchedules = new ArrayList<>();
		for (Schedule s : partialSchedules) {
			if (s.getScheduledNodes().size() < maxSize) {
				removeTheseSchedules.add(s);
			}
		}

		//Remove the partial schedules from the tree
		for (Schedule s : removeTheseSchedules) {
			partialSchedules.remove(s);
		}



	}

	/**
	 * This method comapress the schedules to each other
	 * @param s1
	 * @param s2
	 * @return
	 */
	private boolean equals(Schedule s1, Schedule s2) {

		boolean same = false;

		//get the amount of processors for both of them
		for (Processor p : s1.getProcessors()) {
			for (Processor c : s2.getProcessors()) {

				//get the Id of both both the procceors and checks if the processors are not equal
				if (p.getID() != c.getID()) {

					//get the tasks that have been associated with each of the nodes
					for (TaskNode task : p.getTasks()) {
						for (TaskNode otherTask : c.getTasks()) {

							//check if names of the tasks and then the earleist startt
							if ((task.getName().equals(otherTask.getName())) && (task.getStartTime() == otherTask.getStartTime()) ) {
								same = true;
							}
							else {
								same = false;
								return false;
							}
						}
					}
				}

			}

		}

		return same;
	}



    /**
     * Creates partial schedules for the amount of schedulable nodes that are present in graph through deep cloning schedules
     * and storing the partial equivalents in a list of partial schedules
     * @param s
     * @param processorId
     * @throws CloneNotSupportedException
     */
	private void createPartialSchedules(Schedule s, int processorId) throws CloneNotSupportedException {

		List<TaskNode> nodes = s.getSchedulableNodes();

		//creating partial schedules for the amount of schedulable nodes
		for (int i = 0; i < s.getSchedulableNodes().size(); i++) {

			s.addTask(nodes.get(i), s.getProcessors().get(processorId), s.getEarliestSchedulableTime(nodes.get(i), s.getProcessors().get(processorId)));

			//deep clone the object and remove the last scheduled task
			Schedule partialSchedule = (Schedule) deepClone(s);
			s.removeLastScheduledTask();

			partialSchedules.add(partialSchedule);
		}

		//remove the value if the partial schedule already contains it
        if (partialSchedules.contains(s)) {
            partialSchedules.remove(s);
        }


	}

}
