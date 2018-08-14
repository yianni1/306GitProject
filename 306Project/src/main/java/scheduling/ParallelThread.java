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

    public void addScheduler(DFBnBScheduler scheduler, ParallelSchedule schedule) {
        schedulers.add(scheduler);
        parallelSchedules.add(schedule);
    }

    @Override
    public void run() {
        for (DFBnBScheduler scheduler : schedulers) {
            Schedule schedule = scheduler.createSchedule();
            ParallelScheduler.setOptimalSchedule(schedule);
        }

        for (ParallelSchedule schedule : parallelSchedules) {
            schedule.setFinishedDFS();
        }
    }
}
