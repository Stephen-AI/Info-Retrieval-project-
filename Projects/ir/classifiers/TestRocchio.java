package ir.classifiers;

import java.util.List;

public class TestRocchio {
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
        System.out.println("Initializing Rocchio classifier...");
        Rocchio rocchio;
        boolean debug, neg = false;
        // setting debug flag gives very detailed output, suitable for debugging
        if (args.length == 1 && args[0].equals("-neg"))
            neg = true;

        rocchio = new Rocchio(categories, neg);
        // Perform 10-fold cross validation to generate learning curve
        CVLearningCurve cvCurve = new CVLearningCurve(rocchio, examples);
        cvCurve.run();
    }
}
