package ir.classifiers;

import ir.utilities.MoreMath;
import ir.utilities.Weight;
import ir.vsr.HashMapVector;
import ir.vsr.InvertedIndex;

import java.util.*;

public class Rocchio extends Classifier {
    /**
     * Name of classifier
     */
    public String name = "Rocchio";

    /**
     * For idf weighting vectors
     */
    public InvertedIndex trainingIndex;

    /**
     * Prototypes for each category
     */
    private HashMapVector[] prototypes;

    /**
     * For breaking ties
     */
    private Random rand;

    /**
     * Subtract non similar documents from prototype
     */
    public boolean neg = false;

    public Rocchio(String []categories, boolean neg) {
        this.categories = categories;
        prototypes = new HashMapVector[categories.length];
        rand = new Random();
        this.neg = neg;
        this.name = String.format("Rocchio-%s", neg ? "neg" : "regular");
    }

    @Override
    public String getName() {
        return name;
    }

    public void idfWeight() {
        for (int category = 0; category < prototypes.length; category++) {
            for (Map.Entry<String, Weight> entry : prototypes[category].hashMap.entrySet()) {
                Weight currentWeight = entry.getValue();
                String token = entry.getKey();
                double idf = trainingIndex.tokenHash.get(token).idf;
                currentWeight.setValue(idf * currentWeight.getValue());
            }
        }
    }

    public void idfWeight(List<Example> examples) {
        HashMap<String, Double> idfVector = new HashMap<>();
        //Get counts
        for (Example example : examples) {
            for (Map.Entry<String, Weight> entry : example.hashVector.entrySet()) {
                String token = entry.getKey();
                if (idfVector.containsKey(token))
                    idfVector.put(token, idfVector.get(token) + 1);
                else
                    idfVector.put(token, 1.0);
            }
        }


        //Calculate idf
        for (Map.Entry<String, Double> idfKV : idfVector.entrySet()) {
            idfKV.setValue(MoreMath.log(examples.size() / idfKV.getValue(), 2));
        }

        for (int category = 0; category < prototypes.length; category++) {
            for (Map.Entry<String, Weight> entry : prototypes[category].hashMap.entrySet()) {
                Weight currentWeight = entry.getValue();
                String token = entry.getKey();
                double idf = idfVector.get(token);
                currentWeight.setValue(idf * currentWeight.getValue());
            }
        }
    }

    @Override
    public void train(List<Example> trainingExamples) {
//        trainingIndex = new InvertedIndex(trainingExamples);

        // Clean up and initialize
        for (int i = 0; i < prototypes.length; i++) {
//            if (prototypes[i] == null)
                prototypes[i] = new HashMapVector();
//            else
//                prototypes[i].clear();
        }

        //build prototypes
        for (Example example : trainingExamples) {
            int cat = example.getCategory();
            //tf-weighting
            prototypes[cat].addScaled(example.hashVector,
                    1.0 / example.hashVector.maxWeight());
            if (neg) {
                // subtract dissimilar vectors.
                for (int i = 0 ; i < prototypes.length; i++) {
                    if (i != cat)
                        prototypes[i].addScaled(example.hashVector,
                                -1.0 / example.hashVector.maxWeight());
                }
            }
        }

        //idf-weighting of prototypes
        idfWeight(trainingExamples);
    }

    public boolean emptyPrototypes() {
        for (int i = 0; i < prototypes.length; i++) {
            if (prototypes[i].size() == 0)
                return true;
        }
        return false;
    }
    @Override
    public boolean test(Example testExample) {
        double maxSim = Integer.MIN_VALUE;
        int maxSimCat = rand.nextInt(this.categories.length);

        if (!emptyPrototypes()) {
            for (int i = 0; i < prototypes.length; i++) {
                double coSim = prototypes[i].cosineTo(testExample.hashVector);
                maxSim = Math.max(maxSim, coSim);
                maxSimCat = maxSim == coSim ? i : maxSimCat;
            }
        }

        return maxSimCat == testExample.getCategory();
    }
}
