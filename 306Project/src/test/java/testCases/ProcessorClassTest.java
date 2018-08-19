package testCases;

import exceptions.NotSchedulableException;
import exceptions.NotDeschedulableException;
import graph.TaskNode;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import scheduling.Processor;
import exceptions.TaskException;

/**
 * Created by olive on 7/08/2018.
 */
public class ProcessorClassTest {

    private Processor processor;

    @Before
    public void setUp() {
        processor = new Processor(0);
    }

    @Test
    /**
     * Tests that adding a task actually adds the task, and that the bound is correct
     *
     */
    public void testAddTask() throws NotSchedulableException {
        TaskNode node1 = new TaskNode(12, "nani");
        TaskNode node2 = new TaskNode(3002, "he");
        processor.addTask(node1, 0);
        processor.addTask(node2, 12);
        assertTrue(processor.getTasks().contains(node1) && processor.getTasks().size() == 2
        && processor.getTasks().contains(node2));
        assertTrue(processor.getBound() == 3014);
    }

    @Test
    /**
     * Tests that processor throws an exception when the
     * starttime is less than the bound
     */
    public void testExceptionHandlingInAddTask() throws NotSchedulableException {
        try {
            TaskNode node1 = new TaskNode(12, "nani");
            TaskNode node2 = new TaskNode(3002, "he");
            processor.addTask(node1, 0);
            processor.addTask(node2, 1);
            fail();
        }
        catch (TaskException tskex) {

        }
    }

    @Test
    public void testRemoveTask() throws NotSchedulableException, NotDeschedulableException {

        //Adding and removing one task
        TaskNode node1 = new TaskNode(12, "nani");
        TaskNode node2 = new TaskNode(3002, "he");
        processor.addTask(node1, 0);
        processor.removeTask(node1);
        assertTrue(processor.getTasks().size() == 0
        && processor.getBound() == 0);

        //Adding a second task
        processor.addTask(node1, 0);
        processor.addTask(node2, 12);
        processor.removeTask(node2);
        assertTrue(processor.getTasks().size() == 1 &&
        processor.getTasks().contains(node1) &&
        processor.getBound() == 12);

    }

    @Test
    public void testAddTaskWithGap() throws NotSchedulableException {
        TaskNode node1 = new TaskNode(12, "nani");
        TaskNode node2 = new TaskNode(3002, "he");
        processor.addTask(node1, 0);
        processor.addTask(node2, 100);
        assertTrue(processor.getBound() == 3102);
    }
}
