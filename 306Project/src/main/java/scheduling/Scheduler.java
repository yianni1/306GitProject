package scheduling;

import exceptions.NotSchedulableException;
import exceptions.NotScheduledException;

public interface Scheduler {

	 Schedule createSchedule() throws NotSchedulableException, NotScheduledException;
}
