package com.olexyn.abricore.util;

import org.junit.Assert;
import org.junit.Test;

import java.time.Instant;
import java.util.Map;

public class DataUtilTest {

    @Test
    public void resolveHrefParamsTest() {
        Map<String, String> paramMap = DataUtil.resolveHrefParams("https://trade.swissquote.ch/sqb_core/DispatchCtrl?commandName=quoteDetails&isin=CH0532219059&currency=CHF&stockExchange=672");
        Assert.assertEquals(paramMap.get("isin"), "CH0532219059");
        Assert.assertEquals(paramMap.get("commandName"), "quoteDetails");
    }

    @Test
    public void datesTest() {
        Instant from = DataUtil.getInstant(DataUtil.parseDateTime("13-08-2021 10:00:00"));
        Instant to = DataUtil.getInstant(DataUtil.parseDateTime("13-08-2021 14:00:00"));
    }
}
