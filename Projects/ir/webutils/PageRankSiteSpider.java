package ir.webutils;

import ir.utilities.MoreMath;
import ir.utilities.MoreString;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class PageRankSiteSpider extends PageRankSpider {
    /**
     * To represent the web as a graph structure for link analysis
     */
    public HashMap<String, Double> pageRanks = new HashMap<>();
    public final int MAX_ITERATIONS = 50;
    public final double ALPHA = 0.15;

    public void writePageRanks() throws IOException {
        File file = new File(this.saveDir, "page_ranks.txt");
        PrintWriter out = new PrintWriter(new FileWriter(file));
        for (Map.Entry<String, Double> rankedDoc : pageRanks.entrySet()) {
            out.println(rankedDoc.getKey() + ".html " + rankedDoc.getValue());
        }
        out.close();
    }

    /**
     * ADDED:
     * calculate R'(p) for one iteration
     * @param p A node p representing a page
     * @param Ep The constant to trickle in on each iteration
     * @param prevScore The cached scores from the previous iteration.
     * @return R'(p)
     */
    private double rpRankSource(Node p, double Ep, HashMap<String, Double> prevScore) {
        double rankP = 0.0;
        for (Node q : p.getEdgesIn()) {
            double rankQ = prevScore.get(q.name);
            rankP +=  rankQ / q.edgesOut.size();
        }
        rankP *= (1 - ALPHA);
        return rankP + Ep;

    }
    /**
     * ADDED:
     * Calculate the page rank based on the graph structure gotten from crawling the web
     */
    public void doPageRank() {
        Node[] nodes = graph.nodeArray();
        double Ep = ALPHA / nodes.length;
        double initalScore = 1.0 / nodes.length, invC;
        HashMap<String, Double> prevScore = new HashMap<>();
        //Initialize node values
        for (Node node : nodes) {
            pageRanks.put(node.name, initalScore);
            prevScore.put(node.name, initalScore);
        }

        for (int i = 0; i < MAX_ITERATIONS; i++) {
            nodes = graph.nodeArray();
            invC = 0;
            // Main calculation
            for (Node p : nodes) {
                double rankP = rpRankSource(p, Ep, prevScore);
                pageRanks.put(p.name, rankP);
                invC += rankP;
            }

            // Normalize
            for (Node p : nodes) {
                pageRanks.put(p.name, pageRanks.get(p.name) / invC);
                prevScore.put(p.name, pageRanks.get(p.name));
            }
        }

        // Save Page ranks to a file
        try {
            writePageRanks();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Checks command line arguments and performs the crawl.  <p> This
     * implementation calls <code>processArgs</code>,
     * <code>doCrawl</code>, and then finally <code>doPageRank</code>.
     *
     * @param args Command line arguments.
     */
    @Override
    public void go(String[] args) {
        processArgs(args);
        doCrawl();
        doPageRank();
    }

    public static void main(String args[]) {
        new PageRankSiteSpider().go(args);
    }

}
