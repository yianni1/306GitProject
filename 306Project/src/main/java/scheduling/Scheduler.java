package scheduling;

import exceptions.NotDeschedulableException;
import exceptions.NotSchedulableException;

public interface Scheduler {

	 Schedule createSchedule() throws NotSchedulableException, NotDeschedulableException;
}
