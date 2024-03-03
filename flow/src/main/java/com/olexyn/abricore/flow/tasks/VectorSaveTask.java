package com.olexyn.abricore.flow.tasks;

import com.olexyn.abricore.store.runtime.VectorService;
import com.olexyn.abricore.util.CtxAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;




@Component
public class VectorSaveTask extends CtxAware implements Task {



    public VectorSaveTask(ConfigurableApplicationContext ctx) {
        super(ctx);
    }


    @Override
    public void run() {
        bean(VectorService.class).save();
    }

}
