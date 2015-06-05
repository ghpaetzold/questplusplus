/**
 *
 */
package shef.mt.syntree;

import java.io.*;
import java.util.*;

import javax.swing.RowFilter.Entry;

/**
 * @author Catalina Hallett This class is used for processing syntactic treess
 * included in the GALE Phase 4 Arabic Parallel Aligned Treebank Part 1 Version
 * 1.1 (LDC2009E82) dataset Syntactic trees are in the penn treebank format and
 * contain indeces to tokens. This class produces similar trees but replaces
 * indeces with the referenced tokens * Usage: wlv.mt.syntree.TreeComplete
 * [treeFileFolder] [tokenFileFolder] [outputFolder]
 */
public class TreeComplete {

    static String TKN = ".tkn";
    static String COMP = ".penn";
    static HashSet<String> deleteTags = new HashSet<String>();
    static HashMap<String, String> replaceTags = new HashMap<String, String>();
    File treeFileFolder;
    File tokenFileFolder;
    File outFolder;
    HashMap<Integer, String> tokens;

    /**
     * Reads in a folder of files containing syntactic trees and produces a
     * similar file where the indeces are replaced with tokens
     *
     * @param treeFileFolder the folder containing syntactic trees
     * @param tokenFileFolder the folder containing the tokenised input
     * @param outFolder the output folder where the new complete trees will be
     * stored
     */
    public TreeComplete(String treeFileFolder, String tokenFileFolder, String outFolder) {
        try {
            this.treeFileFolder = new File(treeFileFolder);
            this.tokenFileFolder = new File(tokenFileFolder);
            this.outFolder = new File(outFolder);
            tokens = new HashMap<Integer, String>();

            deleteTags.add("NML");
            deleteTags.add("-NONE-");

            replaceTags.put("PP-", "PP");
            replaceTags.put("NP-", "NP");
            replaceTags.put("NP-SBJ", "NP");
            replaceTags.put("NP-SBJ-", "NP");
            replaceTags.put("ADVP-", "ADVP");
            replaceTags.put("ADJP-", "ADJP");
            replaceTags.put("WHPP-", "WHPP");
            replaceTags.put("WHADVP-", "WHADVP");
            replaceTags.put("WHNP-", "WHNP");
            replaceTags.put("NAC-", "NAC");
            replaceTags.put("SBAR-", "SBAR");
            replaceTags.put("S-", "S");
            replaceTags.put("HYPH", "-");


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {

        try {
            createOutputFolder();
            File[] treeFiles = treeFileFolder.listFiles();
            String inTreeFile;
            String inTokenFile;
            String outFile;
            for (int i = 0; i < treeFiles.length; i++) {
                //		System.out.println(treeFiles[i].getName());
                String fileName = treeFiles[i].getName().substring(0, treeFiles[i].getName().lastIndexOf(".tree"));
                inTreeFile = treeFiles[i].getPath();
                inTokenFile = tokenFileFolder + File.separator + fileName + TKN;
                outFile = outFolder + File.separator + fileName + COMP;
                processFile(inTreeFile, inTokenFile, outFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void processFile(String inTreeFile, String inTokenFile, String outFile) throws IOException {
//		extractTokens(inTokenFile);
        BufferedReader brTree = new BufferedReader(new FileReader(inTreeFile));
        BufferedWriter bw = new BufferedWriter(new FileWriter(outFile));

        BufferedReader brToken = new BufferedReader(new FileReader(inTokenFile));
        String tokLine = brToken.readLine();
        String tWord = "";
        String treeLine = brTree.readLine();
        StringBuffer allSent = new StringBuffer();
        boolean newSent = true;
        while (tokLine != null && treeLine != null) {
            if (treeLine.trim().isEmpty() || tokLine.trim().isEmpty()) {
                tokLine = brToken.readLine();
                treeLine = brTree.readLine();
                continue;
            }
//			System.out.println(tokLine);
//			System.out.println(treeLine);
            extractTokens(tokLine);

            int openBr = 0;
            int sentCount = 1;

            String tag = "ROOT";
            String token = "";
            StringBuffer text = new StringBuffer();
            StringBuffer outLine = new StringBuffer("");
            boolean isTag = false;
            boolean isToken = false;
            //indicates whether the last tag has been deleted (e.g., it was "-NONE-")
            boolean deleted = false;
            //remove the beginning and end brackets; we will replace them with "(ROOT...)"
            char[] chars = treeLine.trim().toCharArray();
            //iterate through the characters in the line
            for (char ch : chars) {
                if (ch == '(') {
                    openBr++;
                    tag = "";
                    token = "";
                    isTag = true;
                    if (newSent) {
                        outLine = new StringBuffer();
                        newSent = false;
                    }
//					if (openBr>1)
                    outLine.append(ch);
                    deleted = false;

                } else if (ch == ')') {
                    openBr--;
                    //			if (token.equals("-RRB-"))
                    //				token=") ";
                    //			else if (token.equals("-LRB-"))
                    //				token="( ";
				/*	if (tag.trim().equals("-NONE-")){
                     token="";
                     removeLastTag(outLine);
                     deleted = true;
                     }
                     else*/ if (!token.isEmpty()) {
                        try {
                            tWord = token;
                            try {
                                tWord = tokens.get(new Integer(token.trim()));
                                if (tag.equals("-RRB-")) {
                                    tWord = "-RRB-";
                                } else if (tag.equals("-LRB-")) {
                                    tWord = "-LRB-";
                                }
                            } catch (java.lang.NumberFormatException e) {
                                e.printStackTrace();
                                tWord = tokens.get(new Integer(16));
                            }

                            if (tWord == null) {
                                System.out.println("null token " + token + "\t" + inTreeFile + "\t" + treeLine);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println(inTreeFile + "\t" + treeLine);
                        }
                    }
                    if (!deleted && isToken) {
                        outLine.append(tWord);
                        isToken = false;
                    }
                    //				if (openBr>0 && !deleted && !isToken)
                    if (!deleted && !isToken) {
                        outLine.append(ch);
                    }
                    token = "";
                    if (openBr == 0) {
                        String s = outLine.toString().trim();
                        //String sSimple =s.substring(1,s.length()-1);
                        //outLine = new StringBuffer(sSimple);
                        //				System.out.println(sentCount+" "+s);
                        allSent.append(outLine);
                        newSent = true;
                        sentCount++;
                    }
                } else if (ch == ' ') {
                    if (isTag) {
                        String replace = findReplacement(tag);
                        outLine.append(replace);
                        token = "";
                        isTag = false;
                        isToken = true;
                    }

                    outLine.append(ch);
                } else {
                    if (isTag) {
                        tag += ch;//System.out.println("tag="+tag);
                    } else if (!Character.isWhitespace(ch)) {
                        token += ch;
                    }
                }
//				ich = brTree.read();
            }
            bw.write("(ROOT ");
            bw.write(outLine.toString());
            bw.write(" )");
            bw.newLine();
            treeLine = brTree.readLine();
            tokLine = brToken.readLine();
        }
        brTree.close();
        brToken.close();
        bw.close();
    }

    /**
     * removes the last sequence "(TAG" from the specified StringBuffer
     *
     * @param outLine the sequence to be removed
     */
    public void removeLastTag(StringBuffer outLine) {
        int index = outLine.lastIndexOf("(");
        outLine = outLine.delete(index, outLine.length());
    }

    /**
     * Checks whether the tag has to be replaced in order to make it compliant
     * with the Stanford parser output
     *
     * @param tag
     * @return the replacement tag if tag needs to be replaced, or tag if no
     * changes are necessary
     */
    public String findReplacement(String tag) {
        String result = tag;
        Iterator<Map.Entry<String, String>> it = replaceTags.entrySet().iterator();
        String key;
        Map.Entry<String, String> entry;
        while (it.hasNext()) {
            entry = it.next();
            key = entry.getKey();
            if (tag.startsWith(key)) {
                return entry.getValue();
            }
        }
        return result;
    }

    /**
     * Extracts the tokens from a line in a tokenised file and stores them in a
     * HashMap in the format [tokenIndex,Token>]<br> The tokenised file is part
     * of the GALE Phase 4 Arabic Parallel Aligned Treebank Part 1 Version 1.1
     * (LDC2009E82) For Arabic, the token is the unvocalized Buckwalter form of
     * the word
     *
     * @param tokLine the input tokenised line
     * @throws IOException
     */
    public void extractTokens(String tokLine) throws IOException {
        tokens.clear();
        String index;
        String word;
        //	System.out.println(tokLine);
        if (tokLine.trim().isEmpty()) {
            return;
        }
        String[] splitWords = tokLine.trim().split(" ");
        if (splitWords.length == 0) {
            return;
        }
        for (String tok : splitWords) {
            //		System.out.println(tok);
            String[] split = tok.split(";");
            index = split[0];
            if (split.length < 4) {
                System.out.println(tokLine);

                word = "";
            } else if (split.length <= 7) {
                word = split[3];
            } else {
                word = split[6];
            }
            tokens.put(new Integer(index), word);
        }
    }

    /**
     * creates the output folder if it does not already exist
     *
     * @throws java.io.IOException
     */
    public void createOutputFolder() throws java.io.IOException {
        if (!outFolder.exists()) {
            outFolder.mkdir();
            System.out.println("created " + outFolder.getPath());
        }
    }

    public static void main(String[] args) {
        TreeComplete tc = new TreeComplete(args[0], args[1], args[2]);
        tc.run();
    }
}
