package de.ur.assistenz.emomusic.test;

import de.ur.assistenz.emomusic.classifier.FleissKappaCalculator;
import junit.framework.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class FleissKappaCalculatorTest {

    @Test
    public void testCalculate() throws Exception {
        FleissKappaCalculator kappaCalculator = new FleissKappaCalculator();
        Assert.assertEquals(1.0, kappaCalculator.calculate(new int[]{0, 0, 0, 3}));
        Assert.assertEquals(1.0 / 3.0, kappaCalculator.calculate(new int[]{0, 0, 1, 2}));
        Assert.assertEquals(0.0, kappaCalculator.calculate(new int[]{0, 1, 1, 1}));

        List<Object> ratings = new ArrayList<>();
        ratings.add("happy_amazed");
        ratings.add("happy_amazed");
        ratings.add("happy_amazed");
        Assert.assertEquals(1.0, kappaCalculator.calculate(ratings, 4));

        ratings = new ArrayList<>();
        ratings.add("happy_amazed");
        ratings.add("happy_amazed");
        ratings.add("sad_lonely");
        Assert.assertEquals(1.0 / 3.0, kappaCalculator.calculate(ratings, 4));

        ratings = new ArrayList<>();
        ratings.add("happy_amazed");
        ratings.add("calm_relaxing");
        ratings.add("sad_lonely");
        Assert.assertEquals(0.0, kappaCalculator.calculate(ratings, 4));
    }

}