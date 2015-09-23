package de.ur.assistenz.emomusic.classifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FleissKappaCalculator<T> {

    public double calculate(List<T> ratings, int categories) {
        HashMap<Object, Integer> ratingCounts = new HashMap<>();
        for(Object rating : ratings) {
            ratingCounts.put(rating, 0);
        }
        for(Object rating : ratings) {
            int count = ratingCounts.get(rating);
            ratingCounts.put(rating, count + 1);
        }
        List<Integer> ratingList = new ArrayList<>();
        for(Object rating : ratingCounts.keySet()) {
            ratingList.add(ratingCounts.get(rating));
        }
        int[] ratingArray = new int[categories];
        for(int i = 0; i < categories; i++) {
            if(i < ratingList.size()) {
                ratingArray[i] = ratingList.get(i);
            }
            else {
                ratingArray[i] = 0;
            }
        }
        return calculate(ratingArray);
    }

    public double calculate(int[] ratings) {
        double rater = calculateRater(ratings);
        double kappa = 1.0 / (rater * (rater - 1.0));
        double quadSum = 0;
        for(int rating : ratings) {
            quadSum += Math.pow(rating, 2);
        }
        quadSum -= rater;
        return kappa * quadSum;
    }

    private int calculateRater(int[] ratings) {
        int count = 0;
        for(int rating : ratings) {
            count += rating;
        }
        return count;
    }

}
