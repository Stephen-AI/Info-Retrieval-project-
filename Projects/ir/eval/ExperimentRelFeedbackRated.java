package ir.eval;

import ir.utilities.MoreString;
import ir.vsr.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 *  Simulate relevance feedback for the top N documents.
 */
public class ExperimentRelFeedbackRated extends ExperimentRated {
    /**
     * Perform relevance feedback on top N documents and reformulate query
     */
    public int N;
    /**
     * Binary relevance, either -1 or 1
     */
    public boolean binary;
    /**
     * Control experiment with no feedback
     */
    public boolean control;
    public ExperimentRelFeedbackRated(File corpusDir, File queryFile, File outFile, short docType,
                                      boolean stem, int N, boolean binary, boolean control) throws IOException {
        super(corpusDir, queryFile, outFile, docType, stem);
        this.N = N;
        this.binary = binary;
        this.control = control;
    }

    /**
     * Process the next query read from the query file reader and evaluate
     * results compared to known relevant docs also read from the query file.
     * This version computes NDCG results for each query, storing summed
     * results in NDCGvalues
     *
     * @return true if query successfully read, else false if no more queries
     * in query file
     */
    boolean processQuery(BufferedReader in) throws IOException {
        String query = in.readLine();   // get the query
        if (query == null) return false;  // return false if end of file
        System.out.println("\nQuery " + (rpResults.size() + 1) + ": " + query);

        // Process the query and get the ranked retrievals
        HashMapVector queryVector = new TextStringDocument(query, this.index.stem).hashMapVector();
        Retrieval[] retrievals = index.retrieve(queryVector);
        System.out.println("Returned " + retrievals.length + " documents.");

        // Get the correct retrievals
        ArrayList<String> correctRetrievals = new ArrayList<String>();
        getCorrectRatedRetrievals(in, correctRetrievals);

        //ADDED: for feedback on top N docs
        FeedbackRated fdback = new FeedbackRated(queryVector, retrievals, this.index);
        Set<String> topNdocs = new HashSet<>();

        //Save top N documents
        for (int i = 0; i < N; i++) {
            topNdocs.add(retrievals[i].docRef.file.getName());
        }
        //Feedback is allowed
        if (!control) {
            // ADDED: give feedback for top N docs
            for (int i = 1; i <= N; i++) {
                String docName = retrievals[i - 1].docRef.file.getName();
                if (correctRetrievals.contains(docName)) {
                    if (binary)
                        fdback.addFeedback(i, 1);
                    else
                        fdback.addFeedback(i, this.ratingsMap.get(docName));
                }
                else
                    fdback.addFeedback(i, -1);
            }
            System.out.println("Feedback:");
            System.out.println("Positive docs: " + fdback.goodDocRefs +
                    "\nNegative docs: " + fdback.badDocRefs);
            System.out.println("Executing New Expanded and Reweighted Query: ");

            //ADDED: Rerun retrieval
            queryVector = fdback.newQuery();
            retrievals = index.retrieve(queryVector);
        }
        // ADDED: filter out top N documents from retrieval and correctRetrievals
        Retrieval[] filteredRetrievals = new Retrieval[retrievals.length - N];
        ArrayList<String> filteredCorrectRetrievals = new ArrayList<>();
        int j = 0;
        for (Retrieval retrieval: retrievals) {
            if (!topNdocs.contains(retrieval.docRef.file.getName()))
                filteredRetrievals[j++] = retrieval;
        }
        for (String correct : correctRetrievals) {
            if (!topNdocs.contains(correct))
                filteredCorrectRetrievals.add(correct);
        }

        // Generate Recall/Precision points and save in rpResults
        rpResults.add(evalRetrievals(filteredRetrievals, filteredCorrectRetrievals));

        // Update the NDCG values for this query
        UpdateNDCG(filteredRetrievals, filteredCorrectRetrievals);

        // Read the blank line delimiter between queries in the query file
        String line = in.readLine();
        if (!(line == null || line.trim().equals(""))) {
            System.out.println("\nCould not find blank line after query, bad queryFile format");
            System.exit(1);
        }
        return true;
    }
    /**
     * Evaluate retrieval performance on a given query test corpus and
     * generate a recall/precision graph and table of NDCG results.
     * Command format: "Experiment [OPTION]* [DIR] [QUERIES] [OUTFILE]" where:
     * DIR is the name of the directory whose files should be indexed.
     * QUERIES is a file of queries paired with relevant docs
     * and continuous gold-standard relevance ratings (see queryFile).
     * OUTFILE is the name of the file to put the output. The plot
     * data for the recall precision curve is stored in this file and a
     * gnuplot file for the graph is the same name with a ".gplot" extension
     * and a NDCG result file is the same name with a ".ndcg" extension
     * OPTIONs can be
     * "-html" to specify HTML files whose HTML tags should be removed, and
     * "-stem" to specify tokens should be stemmed with Porter stemmer.
     */
    public static void main(String[] args) throws IOException {
        System.out.print("java ");
        for (String arg : args) {
            System.out.print(arg + " ");
        }
        System.out.println();
        // Parse the arguments into a directory name and optional flag
        String corpusDir = args[args.length - 4];
        String queryFile = args[args.length - 3];
        String outFile = args[args.length - 2];
        short docType = DocumentIterator.TYPE_TEXT;
        boolean stem = false, control = false, binary = false;
        int N = Integer.parseInt(args[args.length - 1]);
        for (int i = 0; i < args.length - 4; i++) {
            String flag = args[i];
            if (flag.equals("-html"))
                // Create HTMLFileDocuments to filter HTML tags
                docType = DocumentIterator.TYPE_HTML;
            else if (flag.equals("-stem"))
                // Stem tokens with Porter stemmer
                stem = true;
            else if (flag.equals("-binary")) {
                // use binary relevance feedback?
                binary = true;
            }
            else if (flag.equals("-control")) {
                // control experiment with no feedback
                control = true;
            }
            else {
                throw new IllegalArgumentException("Unknown flag: " + flag);
            }
        }
        ExperimentRelFeedbackRated exper = new ExperimentRelFeedbackRated(new File(corpusDir), new File(queryFile),
                new File(outFile), docType, stem, N, binary, control);
        // Generate a recall precision curve and NDCG results for this dataset
        // makeRpCurve must be first since it calculates the statistics for both
        exper.makeRpCurve();
        exper.makeNDCGtable();
    }

}
