package testCases;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
        /*
		HashSet<TaskNode> nde = taskGraph.getNodes();
		for (TaskNode n : nde) {
			HashSet<TaskEdge> tEdges = n.getIncomingEdges();
			System.out.println("Node " + n.getName());
			for (TaskEdge e : tEdges) {
				System.out.println("Edge " + e.getWeight());
			}
		}*/
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }
}
