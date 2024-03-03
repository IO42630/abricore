package com.olexyn.abricore.flow.jobs.util.time;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;


@Scope("prototype")
@Component
public class TimeHelper extends ProtoTimeHelper {

    protected TimeHelper(ConfigurableApplicationContext ctx) {
        super(ctx);
    }


    @Override
    protected Instant getTodaysOpen() {
        return getAsset().getExchange()
            .getOpen(LocalDate.now().getDayOfWeek())
            .atDate(LocalDate.now())
            .toInstant(getZoneOffset());
    }

    @Override
    protected Instant getTodaysClose() {
        return getAsset().getExchange()
            .getClose(LocalDate.now().getDayOfWeek())
            .atDate(LocalDate.now())
            .toInstant(getZoneOffset());
    }

    @Override
    protected Instant now() {
        return Instant.now();
    }
}
