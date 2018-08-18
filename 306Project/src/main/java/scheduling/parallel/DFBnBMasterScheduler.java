package scheduling.parallel;

import exceptions.NotSchedulableException;
import exceptions.NotDeschedulableException;
import graph.TaskEdge;
import graph.TaskGraph;
import graph.TaskNode;
import scheduling.GreedyScheduler;
import scheduling.Processor;
import scheduling.Schedule;
import scheduling.Scheduler;
import view.VisualisationController;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by Ray on 28/07/2018.
 * Written by Kevin & Ray.
 */
public class DFBnBMasterScheduler implements  Scheduler {

	private VisualisationController scheduleListener;

	private List<Schedule> partialSchedules = new ArrayList<>();
	private int numCores;
	private int processors;

	private TaskGraph graph;
	private int upperBound;
	private int depth;
	private long numPaths;
	private long branchesPruned;

	// Index of the children of the schedule.
	private List<Integer> nodeIndices;
	private List<Integer> processorIndices;

	private Schedule optimalSchedule;
	private Schedule schedule;
	private List<TaskNode> schedulableNodes;

	private List<TaskNode> initialNodes;

    /**
     * Constructor which generates an initial empty schedule based on the processors and the graph
     * @param graph
     * @param processors
     * @param numCores
     */
	public DFBnBMasterScheduler(TaskGraph graph, int processors, int numCores) {
		this.graph = graph;
		this.processors = processors;
		this.numCores = numCores;
		schedule = new Schedule(processors, graph);
	}

    /**
     * Creates the optimal schedule through thread and parallelisation which involves assigning threads with the layers
     * to each of the schedules
     * @return optimal schedule
     */
	public Schedule createSchedule() {


		Schedule greedySchedule = new GreedyScheduler(graph, processors).createSchedule();
		Schedule defaultGreedy = (Schedule) deepClone(greedySchedule); //Default greedy schedule to use if no better schedule found in slave

		//intializing the upperbound
		upperBound = greedySchedule.getBound();

		//iterating through the tree by checking if there were any more schedulable tasks
		while (greedySchedule.getScheduledNodes().size() > 0) {
			greedySchedule.removeLastScheduledTask();
		}

		initialisePartialSchedules();

		//Thread pool assigned with the number of cores
        ExecutorService executor = Executors.newFixedThreadPool(numCores);

       // System.out.println("Running on " + numCores + " threads with " + partialSchedules.size() + " slave schedulers.");

        List<Schedule> locallyOptimalSchedules = new ArrayList<>();

        //generating the number of threads associative to the number of cores
        for (Schedule schedule : partialSchedules) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    DFBnBSlaveScheduler scheduler = new DFBnBSlaveScheduler(schedule.getGraph(), processors, schedule, upperBound);
					Schedule s = scheduler.createSchedule();

					//If bound found by thread is null, use default greedy
					if (s == null) {
						locallyOptimalSchedules.add(defaultGreedy);
					}
					else {
						locallyOptimalSchedules.add(s);
					}

                }
            });
        }

        executor.shutdown();
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
     * Gets the list of the partial schedules
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
				}
				catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
			}

			//moving to the next index value
			schedule = partialSchedules.get(scheduleIndex);
			scheduleIndex++;

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

	/**
	 * This method makes a "deep clone" of any object it is given.
	 */
	private static Object deepClone(Object object) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bais);
			return ois.readObject();
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
