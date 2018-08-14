package scheduling;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by olive on 14/08/2018.
 */
public class ParallelThread extends Thread implements Serializable {

    private List<DFBnBScheduler> schedulers = new ArrayList<DFBnBScheduler>();
    private List<ParallelSchedule> parallelSchedules = new ArrayList<ParallelSchedule>();
    private int minDepth = 0;

    public void addScheduler(DFBnBScheduler scheduler, ParallelSchedule schedule) {
        schedulers.add(scheduler);
        parallelSchedules.add(schedule);

    }

    @Override
    public void run() {
        minDepth = parallelSchedules.get(0).getScheduledNodes().size();
        System.out.println("started");
        for (DFBnBScheduler scheduler : schedulers) {
            Schedule schedule = scheduler.createSchedule(minDepth);
            ParallelScheduler.setOptimalSchedule(schedule);
            System.out.println("finished this scheduler");
        }
        System.out.println("finished running thread");

        for (ParallelSchedule schedule : parallelSchedules) {
            schedule.setFinishedDFS();
        }
    }
}
