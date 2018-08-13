package parallelisation;//####[1]####
//####[1]####
import pt.runtime.CurrentTask;//####[3]####
import pt.runtime.TaskID;//####[4]####
import pt.runtime.TaskIDGroup;//####[5]####
import scheduling.*;//####[6]####
import main.App;//####[7]####
import io.*;//####[8]####
import graph.*;//####[9]####
import java.io.File;//####[10]####
import java.util.*;//####[11]####
import scheduling.*;//####[13]####
//####[13]####
//-- ParaTask related imports//####[13]####
import pt.runtime.*;//####[13]####
import java.util.concurrent.ExecutionException;//####[13]####
import java.util.concurrent.locks.*;//####[13]####
import java.lang.reflect.*;//####[13]####
import pt.runtime.GuiThread;//####[13]####
import java.util.concurrent.BlockingQueue;//####[13]####
import java.util.ArrayList;//####[13]####
import java.util.List;//####[13]####
//####[13]####
public class ParallelTask {//####[16]####
    static{ParaTask.init();}//####[16]####
    /*  ParaTask helper method to access private/protected slots *///####[16]####
    public void __pt__accessPrivateSlot(Method m, Object instance, TaskID arg, Object interResult ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {//####[16]####
        if (m.getParameterTypes().length == 0)//####[16]####
            m.invoke(instance);//####[16]####
        else if ((m.getParameterTypes().length == 1))//####[16]####
            m.invoke(instance, arg);//####[16]####
        else //####[16]####
            m.invoke(instance, arg, interResult);//####[16]####
    }//####[16]####
//####[18]####
    private static int depth;//####[18]####
//####[19]####
    private static ArrayList<Integer> nodeIndex = new ArrayList<Integer>();//####[19]####
//####[21]####
    private static volatile Method __pt__buildSingleTask_TaskGraph_method = null;//####[21]####
    private synchronized static void __pt__buildSingleTask_TaskGraph_ensureMethodVarSet() {//####[21]####
        if (__pt__buildSingleTask_TaskGraph_method == null) {//####[21]####
            try {//####[21]####
                __pt__buildSingleTask_TaskGraph_method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__buildSingleTask", new Class[] {//####[21]####
                    TaskGraph.class//####[21]####
                });//####[21]####
            } catch (Exception e) {//####[21]####
                e.printStackTrace();//####[21]####
            }//####[21]####
        }//####[21]####
    }//####[21]####
    public static TaskID<Integer> buildSingleTask(TaskGraph graph) {//####[21]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[21]####
        return buildSingleTask(graph, new TaskInfo());//####[21]####
    }//####[21]####
    public static TaskID<Integer> buildSingleTask(TaskGraph graph, TaskInfo taskinfo) {//####[21]####
        // ensure Method variable is set//####[21]####
        if (__pt__buildSingleTask_TaskGraph_method == null) {//####[21]####
            __pt__buildSingleTask_TaskGraph_ensureMethodVarSet();//####[21]####
        }//####[21]####
        taskinfo.setParameters(graph);//####[21]####
        taskinfo.setMethod(__pt__buildSingleTask_TaskGraph_method);//####[21]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[21]####
    }//####[21]####
    public static TaskID<Integer> buildSingleTask(TaskID<TaskGraph> graph) {//####[21]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[21]####
        return buildSingleTask(graph, new TaskInfo());//####[21]####
    }//####[21]####
    public static TaskID<Integer> buildSingleTask(TaskID<TaskGraph> graph, TaskInfo taskinfo) {//####[21]####
        // ensure Method variable is set//####[21]####
        if (__pt__buildSingleTask_TaskGraph_method == null) {//####[21]####
            __pt__buildSingleTask_TaskGraph_ensureMethodVarSet();//####[21]####
        }//####[21]####
        taskinfo.setTaskIdArgIndexes(0);//####[21]####
        taskinfo.addDependsOn(graph);//####[21]####
        taskinfo.setParameters(graph);//####[21]####
        taskinfo.setMethod(__pt__buildSingleTask_TaskGraph_method);//####[21]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[21]####
    }//####[21]####
    public static TaskID<Integer> buildSingleTask(BlockingQueue<TaskGraph> graph) {//####[21]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[21]####
        return buildSingleTask(graph, new TaskInfo());//####[21]####
    }//####[21]####
    public static TaskID<Integer> buildSingleTask(BlockingQueue<TaskGraph> graph, TaskInfo taskinfo) {//####[21]####
        // ensure Method variable is set//####[21]####
        if (__pt__buildSingleTask_TaskGraph_method == null) {//####[21]####
            __pt__buildSingleTask_TaskGraph_ensureMethodVarSet();//####[21]####
        }//####[21]####
        taskinfo.setQueueArgIndexes(0);//####[21]####
        taskinfo.setIsPipeline(true);//####[21]####
        taskinfo.setParameters(graph);//####[21]####
        taskinfo.setMethod(__pt__buildSingleTask_TaskGraph_method);//####[21]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[21]####
    }//####[21]####
    public static int __pt__buildSingleTask(TaskGraph graph) {//####[21]####
        return build(graph);//####[22]####
    }//####[23]####
//####[23]####
//####[25]####
    public static void main(String[] args) {//####[25]####
        try {//####[26]####
            GraphLoader loader = new GraphLoader();//####[29]####
            TaskGraph graph = loader.load("src/main/resources/DotFiles/1.dot");//####[30]####
            depth = graph.getNodes().size() - 1;//####[32]####
            List<TaskGraph> graphs = new ArrayList<TaskGraph>();//####[34]####
            List<TaskNode> nodes = new ArrayList<TaskNode>();//####[36]####
            for (TaskNode node : graph.getNodes()) //####[38]####
            {//####[38]####
                if (node.getIncomingEdges().size() == 0) //####[39]####
                {//####[39]####
                    nodes.add(node);//####[40]####
                }//####[41]####
            }//####[42]####
            int currentNode = 0;//####[44]####
            while (nodes.size() > 0) //####[45]####
            {//####[45]####
                currentNode = 0;//####[46]####
                TaskNode initialNode = nodes.get(currentNode);//####[47]####
                TaskGraph newGraph = graph.clone();//####[48]####
                for (TaskEdge edge : initialNode.getOutgoingEdges()) //####[50]####
                {//####[50]####
                    newGraph.removeEdge(edge);//####[51]####
                }//####[52]####
                newGraph.removeNode(initialNode);//####[54]####
                nodes.remove(initialNode);//####[55]####
                graphs.add(newGraph);//####[59]####
            }//####[60]####
            List<TaskNode> path = null;//####[62]####
            for (TaskGraph tempGraph : graphs) //####[63]####
            {//####[63]####
                for (TaskNode node : tempGraph.getNodes()) //####[64]####
                {//####[64]####
                    if (node.getIncomingEdges().size() == 0) //####[65]####
                    {//####[65]####
                        path = dfs(node);//####[66]####
                    }//####[67]####
                }//####[68]####
                if (path != null) //####[71]####
                {//####[71]####
                    for (TaskNode node : tempGraph.getNodes()) //####[72]####
                    {//####[72]####
                        if (!path.contains(node)) //####[73]####
                        {//####[73]####
                            tempGraph.getNodes().remove(node);//####[74]####
                        }//####[75]####
                    }//####[76]####
                }//####[77]####
            }//####[79]####
            List<TaskID> ids = new ArrayList<TaskID>();//####[86]####
            for (TaskGraph tempGraph : graphs) //####[87]####
            {//####[87]####
                ids.add(buildSingleTask(tempGraph));//####[88]####
            }//####[89]####
            TaskIDGroup g = new TaskIDGroup(10);//####[92]####
            for (int i = 0; i < nodes.size(); i++) //####[94]####
            {//####[94]####
                g.add(ids.get(i));//####[95]####
            }//####[96]####
            System.out.println("** Going to wait for the tasks...");//####[98]####
            g.waitTillFinished();//####[100]####
            System.out.println("Finished");//####[101]####
        } catch (Exception e) {//####[103]####
            e.printStackTrace();//####[104]####
        }//####[105]####
    }//####[107]####
//####[109]####
    private static List<TaskNode> dfs(TaskNode node) {//####[109]####
        List<TaskNode> path = new ArrayList<TaskNode>();//####[111]####
        Stack<TaskNode> stack = new Stack<TaskNode>();//####[112]####
        stack.add(node);//####[113]####
        path.add(node);//####[114]####
        while (!stack.isEmpty()) //####[116]####
        {//####[117]####
            TaskNode element = stack.pop();//####[118]####
            for (TaskEdge edge : element.getOutgoingEdges()) //####[120]####
            {//####[120]####
                TaskNode child = edge.getEndNode();//####[121]####
                stack.add(child);//####[122]####
                path.add(child);//####[123]####
            }//####[125]####
        }//####[126]####
        return path;//####[128]####
    }//####[129]####
//####[132]####
    public static int build(TaskGraph graph) {//####[132]####
        DFBnBParrallel schedule = new DFBnBParrallel(graph, 2);//####[134]####
        Schedule solution = schedule.createSchedule(depth, nodeIndex);//####[135]####
        return solution.getBound();//####[139]####
    }//####[140]####
}//####[140]####
