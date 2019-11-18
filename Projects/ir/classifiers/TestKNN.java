package ir.classifiers;

import java.util.List;

public class TestKNN {
    /**
     * A driver method for testing the NaiveBayes classifier using
     * 10-fold cross validation.
     *
     * @param args a list of command-line arguments.  Specifying "-debug"
     *             will provide detailed output
     */
    public static void main(String args[]) throws Exception {
        String dirName = "/u/mooney/ir-code/corpora/yahoo-science/";
        String[] categories = {"bio", "chem", "phys"};
        System.out.println("Loading Examples from " + dirName + "...");
        List<Example> examples = new DirectoryExamplesConstructor(dirName, categories).getExamples();
        System.out.println("Initializing KNN classifier...");
        int K =  5;
        KNN knnC;
        boolean debug;
        // setting debug flag gives very detailed output, suitable for debugging
        if (args.length == 2 && args[0].equals("-K"))
            K = Integer.parseInt(args[1]);

        knnC = new KNN(categories, K);
        // Perform 10-fold cross validation to generate learning curve
        CVLearningCurve cvCurve = new CVLearningCurve(knnC, examples);
        cvCurve.run();
    }
}
