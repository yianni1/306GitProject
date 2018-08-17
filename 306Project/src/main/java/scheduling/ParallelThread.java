package scheduling;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by olive on 14/08/2018.
 */
public class ParallelThread extends Thread implements Serializable {

    private List<ParallelDFS> schedulers = new ArrayList<ParallelDFS>();
    private List<ParallelSchedule> parallelSchedules = new ArrayList<ParallelSchedule>();
    private int minDepth = 0;


    public ParallelThread() {
        setDaemon(true);
    }

    public void addScheduler(ParallelDFS scheduler, ParallelSchedule schedule, int minDepth) {
        schedulers.add(scheduler);
        parallelSchedules.add(schedule);
        this.minDepth = minDepth;
    }

    @Override
    public void run() {
        for (ParallelDFS scheduler : schedulers) {
            Schedule schedule = scheduler.createSchedule(minDepth);
            ParallelScheduler.setOptimalSchedule(schedule);
        }

        for (ParallelSchedule schedule : parallelSchedules) {
            schedule.setFinishedDFS(this);
        }
    }
}
