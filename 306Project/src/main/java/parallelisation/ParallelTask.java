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
//####[10]####
//-- ParaTask related imports//####[10]####
import pt.runtime.*;//####[10]####
import java.util.concurrent.ExecutionException;//####[10]####
import java.util.concurrent.locks.*;//####[10]####
import java.lang.reflect.*;//####[10]####
import pt.runtime.GuiThread;//####[10]####
import java.util.concurrent.BlockingQueue;//####[10]####
import java.util.ArrayList;//####[10]####
import java.util.List;//####[10]####
//####[10]####
public class ParallelTask {//####[13]####
    static{ParaTask.init();}//####[13]####
    /*  ParaTask helper method to access private/protected slots *///####[13]####
    public void __pt__accessPrivateSlot(Method m, Object instance, TaskID arg, Object interResult ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {//####[13]####
        if (m.getParameterTypes().length == 0)//####[13]####
            m.invoke(instance);//####[13]####
        else if ((m.getParameterTypes().length == 1))//####[13]####
            m.invoke(instance, arg);//####[13]####
        else //####[13]####
            m.invoke(instance, arg, interResult);//####[13]####
    }//####[13]####
//####[15]####
    private static volatile Method __pt__buildSingleTask__method = null;//####[15]####
    private synchronized static void __pt__buildSingleTask__ensureMethodVarSet() {//####[15]####
        if (__pt__buildSingleTask__method == null) {//####[15]####
            try {//####[15]####
                __pt__buildSingleTask__method = ParaTaskHelper.getDeclaredMethod(new ParaTaskHelper.ClassGetter().getCurrentClass(), "__pt__buildSingleTask", new Class[] {//####[15]####
                    //####[15]####
                });//####[15]####
            } catch (Exception e) {//####[15]####
                e.printStackTrace();//####[15]####
            }//####[15]####
        }//####[15]####
    }//####[15]####
    public static TaskID<Integer> buildSingleTask() {//####[15]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[15]####
        return buildSingleTask(new TaskInfo());//####[15]####
    }//####[15]####
    public static TaskID<Integer> buildSingleTask(TaskInfo taskinfo) {//####[15]####
        // ensure Method variable is set//####[15]####
        if (__pt__buildSingleTask__method == null) {//####[15]####
            __pt__buildSingleTask__ensureMethodVarSet();//####[15]####
        }//####[15]####
        taskinfo.setParameters();//####[15]####
        taskinfo.setMethod(__pt__buildSingleTask__method);//####[15]####
        taskinfo.setInteractive(true);//####[15]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[15]####
    }//####[15]####
    public static int __pt__buildSingleTask() {//####[15]####
        return build();//####[16]####
    }//####[17]####
//####[17]####
//####[19]####
    public static void main(String[] args) {//####[19]####
        try {//####[20]####
            buildSingleTask();//####[23]####
            List<TaskID> ids = new ArrayList<TaskID>();//####[26]####
            for (int i = 0; i < 1000; i++) //####[27]####
            {//####[27]####
                ids.add(buildSingleTask());//####[28]####
            }//####[29]####
            TaskIDGroup g = new TaskIDGroup(10);//####[32]####
            for (int i = 0; i < 1000; i++) //####[34]####
            {//####[34]####
                g.add(ids.get(i));//####[35]####
                System.out.println(ids.get(i).getProgress());//####[36]####
            }//####[37]####
            System.out.println("** Going to wait for the tasks...");//####[41]####
            g.waitTillFinished();//####[43]####
            System.out.println("Finished");//####[44]####
        } catch (Exception e) {//####[46]####
            e.printStackTrace();//####[47]####
        }//####[48]####
    }//####[50]####
//####[53]####
    public static int build() {//####[53]####
        GraphLoader loader = new GraphLoader();//####[55]####
        TaskGraph graph = loader.load("src/main/resources/DotFiles/Nodes_10_Random.dot");//####[56]####
        Scheduler schedule = new DFBnBScheduler(graph, 2);//####[58]####
        Schedule solution = schedule.createSchedule();//####[59]####
        return solution.getBound();//####[63]####
    }//####[64]####
}//####[64]####
