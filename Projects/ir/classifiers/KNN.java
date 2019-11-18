package ir.classifiers;

import ir.vsr.FileDocument;
import ir.vsr.InvertedIndex;
import ir.vsr.Retrieval;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class KNN extends Classifier {

    /**
     * Name of classifier
     */
    public String name;

    public int K;

    /**
     * For breaking ties
     */
    public Random rand;

    /**
     * Store mapping from a file name to the Example training data
     */
    public HashMap<String, Example> fileToExample;

    /**
     * For storing the tf-idf weighted vectors of training data
     */
    protected InvertedIndex trainingIndex = null;

    public KNN(String[] categories, int k) {
        this.categories = categories;
        this.K = k;
        rand = new Random();
        this.name = String.format("KNN-%d", k);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void train(List<Example> trainingExamples) {
        // Create mapping for efficient testing
        fileToExample = new HashMap<>();
        for (Example example : trainingExamples)
            fileToExample.put(example.name, example);
        trainingIndex = new InvertedIndex(trainingExamples);
    }

    @Override
    public boolean test(Example testExample) {
        Retrieval [] retrievals = trainingIndex.retrieve(testExample.hashVector);
        ArrayList<Integer> potentialCats = new ArrayList<>();
        int[] categoryCount = new int[categories.length];
        int maxCat = 0, maxCatValue = Integer.MIN_VALUE, winner = rand.nextInt(categories.length);

        //Count categories in K nearest neighbor
        for (int i = 0; i < K && i < retrievals.length; i++) {
            int cat = fileToExample.get(retrievals[i].docRef.toString()).getCategory();
            categoryCount[cat]++;
            maxCatValue = Math.max(maxCatValue, categoryCount[cat]);
            maxCat = maxCatValue == categoryCount[cat] ? cat : maxCat;
        }

        if (retrievals.length > 0) {
            //Look for ties
            for (int i = 0; i < categoryCount.length; i++) {
                if (categoryCount[i] == maxCatValue)
                    potentialCats.add(i);
            }
            // Break ties
            winner = potentialCats.get(rand.nextInt(potentialCats.size()));
        }

        return winner == testExample.getCategory();
    }
}
