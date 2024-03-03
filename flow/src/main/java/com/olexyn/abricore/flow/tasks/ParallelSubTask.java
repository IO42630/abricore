package com.olexyn.abricore.flow.tasks;

public class ParallelSubTask implements Runnable {


    private final Action action;


    public ParallelSubTask(Action action) {
        this.action = action;
    }

    @Override
    public void run() {


        action.run();

    }

}
