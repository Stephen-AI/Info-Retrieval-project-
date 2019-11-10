package ir.webutils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class PRCompare {

    public static HashMap<String, Double> readPageRanks(File file) throws IOException {
        String line;
        BufferedReader in = new BufferedReader(new FileReader(file));
        HashMap<String, Double> pageRanks = new HashMap<>();
        while ((line = in.readLine()) != null) {
            StringTokenizer tokenizer = new StringTokenizer(line);
            String page = tokenizer.nextToken();
            String value = tokenizer.nextToken();
            pageRanks.put(page, Double.valueOf(value));
        }
        return pageRanks;
    }
    public static void main(String[] args) throws IOException {
        File prA = new File("crawled/page_ranks.txt");
        File prB = new File("/home/stephen/Downloads/page_ranksYF.txt");
        HashMap<String, Double> ranksA = readPageRanks(prA);
        HashMap<String, Double> ranksB = readPageRanks(prB);

        for (Map.Entry<String, Double> pageRank : ranksA.entrySet()){
            String pageName = pageRank.getKey();
            if (!ranksB.containsKey(pageName))
                System.out.printf("diff: %s doesn't contain page: %s\n", prB.getName(), pageName);
            else if (pageRank.getValue() != ranksB.get(pageName))
                System.out.printf("diff: For %s,  %s has value %.9f, while %s has value %.9f\n",
                        pageName, prA.getName(), pageRank.getValue(), prB.getName(), ranksB.get(pageName));
        }

    }
}
