package scheduling;

import exceptions.NotDeschedulableException;
import exceptions.NotSchedulableException;
import exceptions.NotDeschedulableException;

public interface Scheduler {

	 Schedule createSchedule() throws NotSchedulableException, NotDeschedulableException, NotDeschedulableException;
}
