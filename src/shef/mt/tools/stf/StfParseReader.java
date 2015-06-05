/**
 *
 */
package shef.mt.tools.stf;

import shef.mt.features.util.Sentence;
import java.io.*;
import java.util.*;

import shef.mt.tools.ResourceManager;

/**
 * @author Catalina Hallett
 *
 * Reads in a file containing Stanford parser dependencies in the format
 * produced by
 * @see StfDepParserWrapper
 */
public class StfParseReader {

    private static String[] PHRASE_TAGS = {"S", "SBAR", "SQ", "SBARQ", "FRAG", "NP",
        "VP", "PP", "ADVP", "ADJP", "PRT", "WHNP", "WHADVP", "WHPP", "CONJP", "PRN",
        "NAC", "UCP", "LST", "X", "INTJ", "SINV", "NX", "QP", "RRC",
        "UCP", "WHADJP", "NP-TMP"};
    BufferedReader br;
    private TreeSet<String> phraseTags;

    public StfParseReader(String input) {
        try {
            br = new BufferedReader(new FileReader(input));
            phraseTags = new TreeSet<String>();
            for (String tag : PHRASE_TAGS) {
                phraseTags.add(tag);
            }
            ResourceManager.registerResource("stf");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isAvailable() {
        return br != null;
    }

    /**
     * Retrieves the parse tree and the dependencies corresponding to the
     * following sentence in the input and processes it It retrieves a series of
     * values from the parsed input and sets them as values of the Sentence
     * parameter
     *
     * @param sent he current sentence
     */
    public void processNextSentence(Sentence sent) {
        try {
            if (br == null) {
                return;
            }
            //read lines until we reach the beginning of the parse tree: starts with (ROOT
            String line = br.readLine();
            while (line != null && !line.trim().startsWith("(ROOT")) {
                line = br.readLine();
            }

            //skip the parse tree
            StringBuffer parse = new StringBuffer();
            while (line != null && !line.trim().isEmpty()) {
                parse.append(line);
                line = br.readLine();
            }
            processParse(parse.toString(), sent);

            if (line == null) {
                br.close();
                return;
            }

            //now we've reached the list of dependencies
            //format is dep(token1-number1,token2-number2)

            line = br.readLine();

            String lineTrim = line.trim();
            String[] tokens;
            shef.mt.util.Pair p = null;
            while (!lineTrim.isEmpty() && line != null) {
                tokens = lineTrim.substring(4, lineTrim.length() - 1).split(",");
                if (tokens.length == 2) {
                    p = new shef.mt.util.Pair(tokens[0].substring(0, tokens[0].lastIndexOf("-")), tokens[1].substring(0, tokens[1].lastIndexOf("-")));
                    sent.addDependency(p);
                } else {
                    System.out.println("CANT PROCESS THIS LINE: " + lineTrim);
                }


                line = br.readLine();
                lineTrim = line.trim();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Processes the parse tree and sets the phrase count for each type of
     * phrase as values of the sentence parameter
     *
     * @param line the parse tree
     * @param sent the current sentence
     */
    public void processParse(String line, Sentence sent) {
        if (line == null) {
            return;
        }
        if (line.isEmpty()) {
            return;
        }


        char[] chars = line.toCharArray();
        int depth = 0;
        int nps = 0;
        int vps = 0;
        int adjps = 0;
        int advps = 0;
        int conjps = 0;
        int pps = 0;


        int openBr = 0;
        String tag = "";
        boolean isTag = false;
        int tagCount = 0;
        for (char ch : chars) {
            if (ch == '(') {
                openBr++;
                if (openBr > depth) {
                    depth = openBr;
                }
                tag = "";
                isTag = true;
            } else if (ch == ')') {
                openBr--;
            } else if (ch == ' ') {
                if (isTag) {
                    //found end of tag
                    isTag = false;
                    if (phraseTags.contains(tag)) {
                        tagCount++;
                    }
                    if (tag.equals("NP")) {
                        nps++;
                    } else if (tag.equals("ADJP")) {
                        adjps++;
                    } else if (tag.equals("ADVP")) {
                        advps++;
                    } else if (tag.equals("PP")) {
                        pps++;
                    } else if (tag.equals("CONJP")) {
                        conjps++;
                    } else if (tag.equals("VP")) {
                        vps++;
                    }

                }
            } else if (isTag) {
                tag += ch;
            }

        }



        sent.setValue("phrase_tags", tagCount);
        sent.setValue("PP", pps);
        sent.setValue("ADJP", adjps);
        sent.setValue("ADVP", advps);
        sent.setValue("NP", nps);
        sent.setValue("VP", vps);
        sent.setValue("CONJP", conjps);
        sent.setValue("depth", depth);

    }

    public void close() {
        try {
            if (br != null) {
                br.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        StfParseReader test = new StfParseReader(args[0]);
    }
}
