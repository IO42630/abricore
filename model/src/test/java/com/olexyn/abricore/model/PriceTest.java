package com.olexyn.abricore.model;

import com.olexyn.abricore.model.snapshots.Price;
import com.olexyn.abricore.util.ANum;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PriceTest {

    @Test
    public void priceTest() {
        Price oldPrice = new Price();
        oldPrice.setTraded(new ANum(3));
        Price newPrice = new Price();
        newPrice.setTraded(new ANum(4));

        oldPrice.mergeFrom(newPrice);

        assertEquals(oldPrice, newPrice);


    }
}
