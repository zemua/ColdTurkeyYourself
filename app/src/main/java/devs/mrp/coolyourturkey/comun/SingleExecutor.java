package devs.mrp.coolyourturkey.comun;

import java.util.concurrent.Executor;

public class SingleExecutor implements Executor {

    private static Thread thread;

    @Override
    public void execute(Runnable r) {
        if (!isExecuting()){
            thread = new Thread(r);
            thread.start();
        }
    }

    public boolean singleExecute(Runnable r){
        boolean lresp = !isExecuting();
        execute(r);
        return lresp;
    }

    public boolean isExecuting(){
        return (thread != null && thread.isAlive() && !thread.isInterrupted());
    }

    public void stop(){
        if (!isExecuting()){
            return;
        }
        thread.interrupt();
    }
}