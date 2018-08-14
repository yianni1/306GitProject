package scheduling;

import graph.TaskGraph;
import graph.TaskNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by olive on 14/08/2018.
 */
public class ParallelScheduler implements Scheduler {

//    private TaskGraph taskGraph;
    private List<ParallelSchedule> parallelSchedules = new ArrayList<ParallelSchedule>();
    private int coreNumber;
    private static Schedule optimalSchedule;
    private int processors;

    private List<TaskNode> schedulableNodes;
    private List<TaskNode> initialNodes;


    /**
     * Constructor takes in two fields
     * @param taskGraph
     * @param coreNumber
     */
    public ParallelScheduler(TaskGraph taskGraph, int coreNumber, int processors) {
//        this.taskGraph = taskGraph;
        this.coreNumber = coreNumber;
        this.processors = processors;

        //Setting an upper bound
        GreedyScheduler scheduler = new GreedyScheduler(taskGraph, processors);
        Schedule greedySchedule = scheduler.createSchedule();
        int upperBound = greedySchedule.getBound();
        while (greedySchedule.getScheduledNodes().size() > 0) {
            greedySchedule.removeLastScheduledTask();
        }

        ParallelSchedule schedule = new ParallelSchedule(processors, taskGraph, upperBound);
        parallelSchedules.add(schedule);


        //Initialising nodes
        schedulableNodes = schedule.getSchedulableNodes();
        initialNodes = new ArrayList<TaskNode>();
        for (TaskNode node : schedulableNodes) {
            initialNodes.add(node);
        }
    }



    public Schedule createSchedule() {
        runBFS(); //Rn bfs to get all the threads initialised

        System.out.println("finished bfs");
        for (ParallelSchedule schedule : parallelSchedules) {
            schedule.run();
        }

        boolean finished = false;
        while (!finished) {
            finished = true;
            for (ParallelSchedule schedule : parallelSchedules) {
                if (!schedule.getFinished()) {
                    finished = false;

                }
            }
        }
        System.out.println("done");
        return optimalSchedule;
    }

    /**
     * Runs a bfs to allocate a thread for each schedule
     */
    private void runBFS() {

        int numberOfSchedules = 0; //The number of schedules at the current depth
        List<ParallelSchedule> temporarySchedulers = new ArrayList<ParallelSchedule>(); //List to store temporarily the children of the schedules

        int numberOverMaxCoreNumber = 0; //The number of more schedules at a depth than core number
        //Cutting tree in half
        for (TaskNode node : schedulableNodes) {
            System.out.println("hi");
            numberOfSchedules++; //INcreasing the number of schedules
            ParallelSchedule schedule = (ParallelSchedule) DFBnBScheduler.deepClone(parallelSchedules.get(0));
            System.out.println("hi2");
            schedule.addTask(node, schedule.getProcessors().get(0), 0);
            System.out.println("hi3");
            if (!(numberOfSchedules <= coreNumber)) { //If there are now more threads than max cores
                schedule.setThread(temporarySchedulers.get(numberOverMaxCoreNumber)); //We give it the same thread
                numberOverMaxCoreNumber++;

            }
            temporarySchedulers.add(schedule);
            System.out.println("hi4");

        }

        //Updating the list of schedules to make memory easier
        clearLists(temporarySchedulers);

        while (numberOfSchedules < coreNumber) {
            numberOfSchedules = 0;
            numberOverMaxCoreNumber = 0;

            //Looping through all schedules at every level
            for (ParallelSchedule schedule : parallelSchedules) {
                for (int i = 0; i < schedule.getProcessors().size(); i++) {
                    for (TaskNode node : schedule.getSchedulableNodes()) {
                        TaskNode nodeClone = (TaskNode) DFBnBScheduler.deepClone(node);
                        numberOfSchedules++; //INcreasing the number of schedules
                        ParallelSchedule scheduleClone = (ParallelSchedule) DFBnBScheduler.deepClone(schedule);//Deep cloning old schedule
                        Processor processor = scheduleClone.getProcessors().get(i);

                        //Figuring out the earliest time to be scheduled
                        int earliestSchedulableTime = scheduleClone.getEarliestSchedulableTime(nodeClone, processor);
                        scheduleClone.addTask(nodeClone, processor, earliestSchedulableTime); //Adding time to new processor
                        if (!(numberOfSchedules <= coreNumber)) { //If there are now more threads than max cores
                            scheduleClone.setThread(temporarySchedulers.get(numberOverMaxCoreNumber)); //We give it the same thread
                            numberOverMaxCoreNumber++;
                        }
                        temporarySchedulers.add(schedule);
                    }
                }
            }

            //Updating the list of schedules to make memory easier
            clearLists(temporarySchedulers);

        }

    }


    private void clearLists(List<ParallelSchedule> list) {
        parallelSchedules.clear();
        for (ParallelSchedule schedule : list) {
            parallelSchedules.add(schedule);
        }
        list.clear();
    }


    /**
     * Takes in a schedule and checks if it is better than the
     * one we have. if it is, we accept it
     * @param schedule
     */
    public static synchronized void setOptimalSchedule(Schedule schedule) {
        if (optimalSchedule == null) {
            optimalSchedule = schedule;
        }
        if (optimalSchedule.getBound() > schedule.getBound()) {
            optimalSchedule = schedule;
        }
    }

}
