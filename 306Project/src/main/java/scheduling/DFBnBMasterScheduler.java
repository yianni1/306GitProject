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
public class DFBnBMasterScheduler implements Scheduler {

    private VisualisationController scheduleListener;

    private List<Schedule> partialSchedules = new ArrayList<>();
    private List<DFBnBSlaveScheduler> slaves = new ArrayList<>();
    private int numCores;
    private int processors;
    private TaskGraph graph;
    private int upperBound;
    private long numPaths;
    private long branchesPruned;
    private Schedule schedule;

    private Map<String, Boolean> combinations = new HashMap<>();


    /**
     * Constructor which generates an initial empty schedule based on the processors and the graph
     *
     * @param graph
     * @param processors
     * @param numCores
     */
    public DFBnBMasterScheduler(TaskGraph graph, int processors, int numCores) {
        this.graph = graph;
        this.processors = processors;
        this.numCores = numCores;
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
        Schedule defaultGreedy = (Schedule) deepClone(greedySchedule); //Default greedy schedule to use if no better schedule found in slave
        updateSchedule(defaultGreedy, true);

        //intializing the upperbound
        upperBound = greedySchedule.getBound();

        //iterating through the tree by checking if there were any more schedulable tasks
        while (greedySchedule.getScheduledNodes().size() > 0) {
            greedySchedule.removeLastScheduledTask();
        }

        // generate combinations for removal of duplicates in slaves
        List<TaskNode> schedulableNodes = greedySchedule.getSchedulableNodes();
        String[] namesArray = new String[(schedulableNodes.size())];

        for (int i=0; i<schedulableNodes.size(); i++) {
            namesArray[i]=schedulableNodes.get(i).getName();
        }

        for (int i = processors; i > 1; i--) {
            generateCombinations(namesArray, i, 0, new String[i]);
        }

        //initialise all the partial schedules
        initialisePartialSchedules();

        //Thread pool assigned with the number of cores
        ExecutorService executor = Executors.newFixedThreadPool(numCores);

        List<Schedule> locallyOptimalSchedules = new ArrayList<>();

        //generating the number of threads associative to the number of cores
        for (Schedule schedule : partialSchedules) {
            final DFBnBMasterScheduler master = this;
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    DFBnBSlaveScheduler scheduler = new DFBnBSlaveScheduler(schedule.getGraph(), processors, schedule, upperBound, master, combinations);
                    slaves.add(scheduler);
                    Schedule s = scheduler.createSchedule();

                    //If bound found by thread is null, use default greedy
                    if (s == null) {
                        locallyOptimalSchedules.add(defaultGreedy);
                    } else {
                        locallyOptimalSchedules.add(s);
                    }

                }
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
			e.printStackTrace();
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

        System.out.println("Optimal solution with bound of " + optimalSchedule.getBound() + " found!");
        return optimalSchedule;

    }

    public void setScheduleListener(VisualisationController scheduleListener) {
        this.scheduleListener = scheduleListener;
    }


    /**
     * Updates GUI with new optimal schedule and also notifies slaves of a better bound if necessary.
     *
     * @param schedule
     */
    public void updateSchedule(Schedule schedule, boolean forced) {
        if (this.scheduleListener != null) {
            if (schedule.getBound() < upperBound || forced) {
                upperBound = schedule.getBound();
                for (DFBnBSlaveScheduler slave : slaves) {
                    slave.updateUpperBound(upperBound);
                }
                scheduleListener.updateSchedule(schedule);
                try {
                    TimeUnit.MILLISECONDS.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void updateBranchesPruned(long branchesPruned) {
        this.branchesPruned = this.branchesPruned + branchesPruned;
        if (scheduleListener != null && (System.currentTimeMillis() % 40 == 0)) {
            scheduleListener.updateBranchesPruned(this.branchesPruned);
        }
    }

    public void updateNumPaths(long numPaths) {
        this.numPaths = this.numPaths + numPaths;
        if (scheduleListener != null) {
            scheduleListener.updateNumPaths(this.numPaths);
        }

    }

    public void finish() {
        if (this.scheduleListener != null) {
            for (DFBnBSlaveScheduler slave : slaves) {
                if (!slave.isFinished()) {
                    return;
                }
            }
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

        int processorNumber = schedule.getProcessors().size();
        boolean initialIteration = true;

        //creates partial schedules if the number of partial schedules in the list is less than the number of cores
        while (partialSchedules.size() < numCores) {

            //No loop needed for initial iteration to remove instances of duplicate schedules at start of tree
            if (initialIteration) {
                try {
                    createPartialSchedules(schedule, 0);
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }

            } else {

                //Used to avoid concurrent exception when removing parent schedule
                List<Schedule> schedulesToLoop = new ArrayList<>();
                schedulesToLoop = (List<Schedule>) deepClone(partialSchedules);
                try {
                    //Loop through other schedules avalible until core num is met
                    //If core num not met, repeat while loop
                    for (Schedule s : schedulesToLoop) {

                        //Creates new schedules for this iteration
                        for (int i = 0; i < processorNumber; i++) {
                            createPartialSchedules(s, i);
                        }




                        if (partialSchedules.size() >= numCores) {
                            break;
                        }
                    }
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }

                if (partialSchedules.size() >= numCores) {
                    break;
                }


            }


            //Next iteration not initial
            initialIteration = false;
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

        // Remove the 'parent' partial schedule.
        partialSchedules.remove(s);

    }

    public void generateCombinations(String[] arr, int len, int startPosition, String[] result){
        if (len == 0){
            combinations.put(Arrays.toString(result) + Arrays.toString(new int[result.length]), false);
            return;
        }
        for (int i = startPosition; i <= arr.length-len; i++){
            result[result.length - len] = arr[i];
            generateCombinations(arr, len-1, i+1, result);
        }
    }

}
