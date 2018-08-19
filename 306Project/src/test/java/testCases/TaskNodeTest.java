package testCases;

import exceptions.NotSchedulableException;
import exceptions.NotDeschedulableException;
import graph.TaskEdge;
import graph.TaskNode;
import org.junit.Test;
import org.junit.internal.runners.statements.Fail;
import scheduling.Processor;

import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertFalse;

public class TaskNodeTest {

    /**
     * Tests a simple graph with two parents and a child node.
     * The parents should be schedulable, the child should not.
     */
    @Test
    public void isSchedulableTest() {
        TaskNode p1 = new TaskNode(5, "p1");
        TaskNode p2 = new TaskNode(5, "p2");
        TaskNode c1 = new TaskNode(5, "c1");

        TaskEdge e1 = new TaskEdge(p1,c1,5);
        TaskEdge e2 = new TaskEdge(p2,c1,5);

        p1.addOutgoingEdge(e1);
        p2.addOutgoingEdge(e2);
        c1.addIncomingEdge(e1);
        c1.addIncomingEdge(e2);

        assertFalse(c1.isSchedulable());
        assertTrue(p1.isSchedulable());
        assertTrue(p2.isSchedulable());

    }

    /**
     * Tests a simple graph with four nodes.
     * Tests that a scheduled node is no longer schedulable, and that the child of a scheduled node is schedulable.
     */
    @Test
    public void isSchedulableTest2() throws NotSchedulableException {
        TaskNode parent1 = new TaskNode(5, "p1");
        TaskNode parent2 = new TaskNode(5, "p2");
        TaskNode child1 = new TaskNode(5, "c1");
        TaskNode child2 = new TaskNode(5, "c2");

        TaskEdge e1 = new TaskEdge(parent1,child2,5);
        TaskEdge e2 = new TaskEdge(parent2,child1,5);
        TaskEdge e3 = new TaskEdge(child1,child2,5);

        Processor p1 = new Processor(1);
        Processor p2 = new Processor(2);

        parent1.addOutgoingEdge(e1);
        child2.addIncomingEdge(e1);

        parent2.addOutgoingEdge(e2);
        child1.addIncomingEdge(e2);

        child1.addOutgoingEdge(e3);
        child2.addIncomingEdge(e3);

        parent1.schedule(0,p1);
        parent2.schedule(0,p2);

        assertTrue(child1.isSchedulable());
        assertFalse(child2.isSchedulable());
        assertFalse(parent1.isSchedulable());
        assertFalse(parent2.isSchedulable());

        // Try to add an unschedulable node. Check it correctly catches the exception.
        try {
            child2.schedule(5,p1);
        } catch (NotSchedulableException e) {
            // Expected
        }
        assertFalse(child2.isScheduled());

    }

    /**
     * Tests that the schedule method works correctly.
     */
    @Test
    public void scheduleMethodTest() throws NotSchedulableException {
        TaskNode parent1 = new TaskNode(5, "p1");
        TaskNode parent2 = new TaskNode(5, "p2");
        TaskNode child1 = new TaskNode(5, "c1");
        TaskNode child2 = new TaskNode(5, "c2");

        TaskEdge e1 = new TaskEdge(parent1,child2,5);
        TaskEdge e2 = new TaskEdge(parent2,child1,5);
        TaskEdge e3 = new TaskEdge(child1,child2,5);

        Processor p1 = new Processor(1);
        Processor p2 = new Processor(2);

        parent1.addOutgoingEdge(e1);
        child2.addIncomingEdge(e1);

        parent2.addOutgoingEdge(e2);
        child1.addIncomingEdge(e2);

        child1.addOutgoingEdge(e3);
        child2.addIncomingEdge(e3);

        parent1.schedule(0,p1);
        parent2.schedule(0,p2);

        assertTrue(parent1.isScheduled());
        assertTrue(parent1.isScheduled());
        assertFalse(child1.isScheduled());


    }

    /**
     * Tests that the schedule method works correctly.
     */
    @Test
    public void deScheduleMethodTest() throws NotSchedulableException, NotDeschedulableException {
        TaskNode parent1 = new TaskNode(5, "p1");
        TaskNode parent2 = new TaskNode(5, "p2");
        TaskNode child1 = new TaskNode(5, "c1");
        TaskNode child2 = new TaskNode(5, "c2");

        TaskEdge e1 = new TaskEdge(parent1,child2,5);
        TaskEdge e2 = new TaskEdge(parent2,child1,5);
        TaskEdge e3 = new TaskEdge(child1,child2,5);

        Processor p1 = new Processor(1);
        Processor p2 = new Processor(2);

        parent1.addOutgoingEdge(e1);
        child2.addIncomingEdge(e1);

        parent2.addOutgoingEdge(e2);
        child1.addIncomingEdge(e2);

        child1.addOutgoingEdge(e3);
        child2.addIncomingEdge(e3);

        parent1.schedule(0,p1);
        parent2.schedule(0,p2);
        child1.schedule(5, p1);

        assertTrue(parent1.isScheduled());
        assertTrue(parent1.isScheduled());
        assertTrue(child1.isScheduled());

        // Basic deschedule.
        child1.deschedule();
        parent2.deschedule();
        assertFalse(child1.isScheduled());
        assertFalse(parent2.isScheduled());

        // Attempt to deschedule an unscheduled task.
        try {
            child2.deschedule();
        } catch (NotDeschedulableException e) {
            // Expected
        }

        assertFalse(child2.isScheduled());

        parent2.schedule(0,p2);
        child1.schedule(5, p1);

        try{
            parent2.deschedule();
        } catch (NotDeschedulableException e) {
            // Expected
        }

        assertTrue(parent2.isScheduled());
    }

}
