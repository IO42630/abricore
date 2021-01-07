package com.olexyn.abricore.fingers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.function.Predicate;

public class ObservableQuote {

    private static final BigDecimal zero = new BigDecimal(0);

    Stack<BigDecimal> observedValues = new Stack<>();

    List<Predicate<BigDecimal>> conditions = new ArrayList<>();

    public void addCondition(Predicate<BigDecimal> predicate) {
        conditions.add(predicate);
    }

    public void notifyX() {
        for (Predicate<BigDecimal> predicate : conditions) {
            BigDecimal value = observedValues.pop();
            if (predicate.test(value)) {
                System.out.println("do notify about " + value);
            }
        }
    }

    public void push(BigDecimal item) {
        observedValues.push(item);
        notifyX();
    }

    public static void main(String... args) {
        ObservableQuote observableQuote = new ObservableQuote();

        //observableQuote.addCondition(x -> x > zero);

        //observableQuote.push(2d);
        //observableQuote.push(0d);
        //observableQuote.push(4d);
    }

    /**
     * get Moving Average
     */
    public Double calculateMA(Integer days) {
        BigDecimal sum = new BigDecimal(0);
        if (observedValues.size() > days) {
            for (; days > 0; days--) {
                sum = sum.add(observedValues.pop());
            }
            return sum.divide(new BigDecimal(days), RoundingMode.HALF_EVEN).doubleValue();
        }
        return null;
    }


}
