package ir.vsr;

import ir.utilities.UserInput;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.StringTokenizer;

public class PageRankInvertedIndex extends InvertedIndex {
    double weight;
    HashMap<String, Double> pageRanks;
    public PageRankInvertedIndex(File dirFile, short docType, boolean stem, boolean feedback, double weight,
                                 HashMap<String, Double> pageRanks) {
        super(dirFile, docType, stem, feedback);
        this.weight = weight;
        this.pageRanks = pageRanks;
    }

    /**
     * Enter an interactive user-query loop, accepting queries and showing the retrieved
     * documents in ranked order.
     */
    public void processQueries() {

        System.out.println("Now able to process queries. When done, enter an empty query to exit.");
        // Loop indefinitely answering queries
        do {
            // Get a query from the console
            String query = UserInput.prompt("\nEnter query:  ");
            // If query is empty then exit the interactive loop
            if (query.equals(""))
                break;
            // Get the ranked retrievals for this query string and present them
            HashMapVector queryVector = (new TextStringDocument(query, stem)).hashMapVector();
            Retrieval[] retrievals = retrieve(queryVector);

            // ADDED: Refine the retrievals to take account for page rank
            for (Retrieval retrieval : retrievals){
                retrieval.score += (weight * pageRanks.get(retrieval.docRef.toString()));
            }
            Arrays.sort(retrievals);
            //End of ADDED

            presentRetrievals(queryVector, retrievals);
        }
        while (true);
    }

    /**
     * Index the documents in dirFile.
     */
    protected void indexDocuments() {
        System.out.println("Hello");
        if (!tokenHash.isEmpty() || !docRefs.isEmpty()) {
            // Currently can only index one set of documents when an index is created
            throw new IllegalStateException("Cannot indexDocuments more than once in the same InvertedIndex");
        }
        //ADDED: filter out page_ranks.txt
        // Get an iterator for the documents
        DocumentIterator docIter = new DocumentIterator(dirFile, docType, stem,
                ((file, name) -> {
            return  name.toLowerCase().endsWith(".html");
        }));
        System.out.println("Indexing documents in " + dirFile);
        // Loop, processing each of the documents

        while (docIter.hasMoreDocuments()) {
            FileDocument doc = docIter.nextDocument();
            // Create a document vector for this document
            System.out.print(doc.file.getName() + ",");
            HashMapVector vector = doc.hashMapVector();
            indexDocument(doc, vector);
        }
        // Now that all documents have been processed, we can calculate the IDF weights for
        // all tokens and the resulting lengths of all weighted document vectors.
        computeIDFandDocumentLengths();
        System.out.println("\nIndexed " + docRefs.size() + " documents with " + size() + " unique terms.");
    }

    /**
     * ADDED:
     * Read the page ranks from the page_ranks.txt file created by PageRankSiteSpider
     * @return key value pair of document name to page rank
     * @throws IOException
     */
    public static HashMap<String, Double> readPageRanks(String dirName) throws IOException {
        String line;
        HashMap<String, Double> pageRanks = new HashMap<>();
        File pageRankFile = new File(dirName, "page_ranks.txt");
        BufferedReader in = new BufferedReader(new FileReader(pageRankFile));
        String docName = null;
        double rank = 0.0;
        int i = 0;
        while ((line = in.readLine()) != null) {
            StringTokenizer tokenizer = new StringTokenizer(line);
            while (tokenizer.hasMoreTokens()) {
                if (i % 2 == 0) {
                    docName = tokenizer.nextToken();
                }
                else {
                    rank = Double.parseDouble(tokenizer.nextToken());
                }
                i++;
            }
            pageRanks.put(docName, rank);
        }
        return pageRanks;
    }
    /**
     * Index a directory of files and then interactively accept retrieval queries.
     * Command format: "InvertedIndex [OPTION]* [DIR]" where DIR is the name of
     * the directory whose files should be indexed, and OPTIONs can be
     * "-html" to specify HTML files whose HTML tags should be removed.
     * "-stem" to specify tokens should be stemmed with Porter stemmer.
     * "-feedback" to allow relevance feedback from the user.
     */
    public static void main(String[] args) {
        // Parse the arguments into a directory name and optional flag

        String dirName = args[args.length - 1];
        short docType = DocumentIterator.TYPE_HTML;
        boolean stem = false, feedback = false;
        double weight = 0.0;
        for (int i = 0; i < args.length - 1; i++) {
            String flag = args[i];
            if (flag.equals("-weight"))
                weight = Double.parseDouble(args[++i]);
            else {
                throw new IllegalArgumentException("Unknown flag: "+ flag);
            }
        }

        // Create an inverted index for the files in the given directory.
        InvertedIndex index = null;
        try {
            index = new PageRankInvertedIndex(new File(dirName), docType, stem, feedback, weight, readPageRanks(dirName));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        // index.print();
        // Interactively process queries to this index.
        index.processQueries();
    }
}
