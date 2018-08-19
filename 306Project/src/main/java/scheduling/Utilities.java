package scheduling;

import graph.TaskNode;
import javafx.concurrent.Task;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.util.*;

public class Utilities {

    /**
     * This method makes a "deep clone" of any object it is given.
     */
    public static Object deepClone(Object object) {
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

    /**
     * Returns true if these two schedules are the same. Generally only used for testing because of it's time cost.
     *
     * @param s1 First schedule to be compared
     * @param s2 Second schedule to be compared
     * @return true if s1 and s2 are the same
     */
    private boolean equals(Schedule s1, Schedule s2) {

        boolean same = false;
        for (Processor p : s1.getProcessors()) {
            for (Processor c : s2.getProcessors()) {
                if (p.getID() == c.getID()) {

                    for (TaskNode task : p.getTasks()) {
                        for (TaskNode otherTask : c.getTasks()) {
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

}
