package ir.webutils;

import ir.utilities.MoreMath;
import ir.utilities.MoreString;

import java.util.*;

public class PageRankSpider extends SiteSpider {
    Graph graph = new Graph();

    /**
     * ADDED:
     * For every incoming link into LINK, add an edge between it and LINK
     * @param parentMap The
     * @param link
     */
    public void addAllEdges(HashMap<String, LinkedList<String>> parentMap, Link link) {
        if (parentMap.containsKey(link.toString())) {
            LinkedList<String> stack = parentMap.get(link.toString());
            while (!stack.isEmpty()) {
                String parent = stack.removeLast();
                graph.addEdge(parent, link.toString());
            }
        }
        else {
            graph.addNode(link.toString());
        }

    }

    /**
     * Performs the crawl.  Should be called after
     * <code>processArgs</code> has been called.  Assumes that
     * starting url has been set.  <p> This implementation iterates
     * through a list of links to visit.  For each link a check is
     * performed using {@link #visited visited} to make sure the link
     * has not already been visited.  If it has not, the link is added
     * to <code>visited</code>, and the page is retrieved.  If access
     * to the page has been disallowed by a robots.txt file or a
     * robots META tag, or if there is some other problem retrieving
     * the page, then the page is skipped.  If the page is downloaded
     * successfully {@link #indexPage indexPage} and {@link
     * #getNewLinks getNewLinks} are called if allowed.
     * <code>go</code> terminates when there are no more links to visit
     * or <code>count &gt;= maxCount</code>
     */
    @Override
    public void doCrawl() {
        if (linksToVisit.size() == 0) {
            System.err.println("Exiting: No pages to visit.");
            System.exit(0);
        }
        visited = new HashSet<Link>();
        //ADDED: Stores the list of links pointing to a page
        HashMap<String, LinkedList<String>> parentMap = new HashMap<>();
        HashMap<String, String> linkToFileName = new HashMap<>();
        while (linksToVisit.size() > 0 && count < maxCount) {
            // Pause if in slow mode
            if (slow) {
                synchronized (this) {
                    try {
                        wait(1000);
                    }
                    catch (InterruptedException e) {
                    }
                }
            }
            // Take the top link off the queue
            Link link = linksToVisit.remove(0);
            System.out.println("Trying: " + link);
            // Skip if already visited this page
            if (!visited.add(link)) {
                System.out.println("Already visited");
                if (graph.getExistingNode(link.toString()) != null)
                    addAllEdges(parentMap, link);
                continue;
            }
            if (!linkToHTMLPage(link)) {
                System.out.println("Not HTML Page");
                continue;
            }
            HTMLPage currentPage = null;
            // Use the page retriever to get the page
            try {
                currentPage = retriever.getHTMLPage(link);
            }
            catch (PathDisallowedException e) {
                System.out.println(e);
                continue;
            }
            if (currentPage.empty()) {
                System.out.println("No Page Found");
                continue;
            }
            Node curNode = null;

            if (currentPage.indexAllowed()) {
                count++;
                System.out.println("Indexing" + "(" + count + "): " + link);
                indexPage(currentPage);
                // ADDED: create a node for this graph and add an edge between all incoming links
                addAllEdges(parentMap, link);
                curNode = graph.getExistingNode(link.toString());
                String docName = "P" + MoreString.padWithZeros(count,
                        (int) Math.floor(MoreMath.log(maxCount, 10)) + 1);
                linkToFileName.put(link.toString(), docName);
                curNode.name = docName;
                // End of ADDED code
            }


            List<Link> newLinks = getNewLinks(currentPage);
            if (count <= maxCount) {
                //ADDED: add new links to the graph
                for (Link newLink : newLinks) {
                    Node outNode = null;
                    if (!parentMap.containsKey(newLink.toString()))
                        parentMap.put(newLink.toString(), new LinkedList<>());
                    outNode = graph.getExistingNode(newLink.toString());
                    // Outgoing link has not been visited yet
                    if (outNode == null)
                        parentMap.get(newLink.toString()).add(link.toString());
                    // Not self referencing
                    else {
                        graph.addEdge(link.toString(), newLink.toString());
                    }
                }
                // End of ADDED code

//                 System.out.println("Adding the following links" + newLinks);
                // Add new links to end of queue
                linksToVisit.addAll(newLinks);
            }

        }
        graph.saveGraphToFile();
//        changeNodeNames(linkToFileName);
    }

    public void changeNodeNames(HashMap<String, String> linkToFileName) {
        Node [] nodes = graph.nodeArray();
        for (Node node : nodes)
            node.name = linkToFileName.get(node.name);
    }
}
