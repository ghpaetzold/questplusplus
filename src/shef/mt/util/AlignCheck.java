package shef.mt.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class AlignCheck {

    String enPath;
    String arPath;
    String outPath;
    BufferedReader mapFileReader;

    public AlignCheck(String enPath, String arPath, String mapFile, String outPath) {
        this.enPath = enPath;
        this.arPath = arPath;

        this.outPath = outPath;
        try {
            mapFileReader = new BufferedReader(new InputStreamReader(new FileInputStream(mapFile), "utf-8"));
            mapFileReader.readLine();
            mapFileReader.readLine();
            mapFileReader.readLine();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createOutputFolder() throws java.io.IOException {
        File subdir = new File(outPath);
        if (!subdir.exists()) {
            subdir.mkdir();
        }
        subdir = new File(outPath + File.separator + "english");
        if (!subdir.exists()) {
            subdir.mkdir();
        }

        subdir = new File(outPath + File.separator + "arabic");
        if (!subdir.exists()) {
            subdir.mkdir();
        }
        subdir = new File(outPath + File.separator + "aligned");
        if (!subdir.exists()) {
            subdir.mkdir();
        }


    }

    public boolean processNextMapping(int index) {
        try {
            String line = mapFileReader.readLine();

            if (line == null) {
                return false;
            }
            if (line.trim().isEmpty()) {
                return true;
            }
//		System.out.println(line);
            String[] map = line.split("\\|");
            String enText = map[0].trim();
            String arText = map[1].trim();
            String enTree = enText + ".ann.mrg";
            arText = arText.substring(0, arText.length() - 9) + "." + arText.substring(arText.length() - 8);
            String arTree = arText.substring(0, arText.length() - 4) + ".tree";
            String outAr = index + "_ar_" + arTree;
            String outEn = index + "_en_" + enTree;
            String alignedPath = outPath + File.separator + "aligned" + File.separator;
//		String outBoth = index+"_both.txt";
//		BufferedWriter outBothBr = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outPath+File.separator+outBoth), "utf-8"));
            processFile(enPath + File.separator + enTree, alignedPath + outEn, outPath + File.separator + "english" + File.separator + outEn);
            processFile(arPath + File.separator + arTree, alignedPath + outAr, outPath + File.separator + "arabic" + File.separator + outAr);
            System.out.println();
//		outBothBr.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public void run() {
        try {
            createOutputFolder();
            int i = 1;
            while (processNextMapping(i)) {
                i++;
            }
//		for (int i=1;i<2;i++)
//			processNextMapping(i);
            mapFileReader.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        /*		File f = new File(folderPath);
         if (f.isDirectory()) {
         File[] files = f.listFiles(new ExtensionFilter(ext));
         for (File file:files)
         processFile(file);
         */
    }

    public void processFile(String input, String output, String fullOutput) throws java.io.IOException {
        System.out.println("Processing " + input + " into " + output);
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(input), "utf-8"));
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output), "utf-8"));
        BufferedWriter fullOut = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fullOutput), "utf-8"));

        int openBr = 0;
        int sentCount = 1;

        String tag = "";
        String token = "";
        StringBuffer text = new StringBuffer("#" + sentCount + "#\t");
        StringBuffer outSent = new StringBuffer("#" + sentCount + "#\t");
        boolean isTag = false;
        int ich = br.read();
        char ch;
        while (ich != -1) {
            ch = (char) ich;
            if (!Character.isWhitespace(ch)) {
                text.append(ch);
            } else {
                text.append(" ");
            }
            if (ch == '(') {
                openBr++;
                tag = "";
                isTag = true;
            } else if (ch == ')') {
                openBr--;
                //			System.out.println("FINAL TAG:"+tag);
//				System.out.println("token=\""+token+"\"");
                if (token.equals("-RRB-")) {
                    token = ") ";
                } else if (token.equals("-LRB-")) {
                    token = "( ";
                } else if (tag.trim().equals("-NONE-")) {
                    token = "";
                } else if (!token.isEmpty()) {
                    token = token.trim() + " ";
                }

                outSent.append(token);
                token = "";
                if (openBr == 0) {
                    String s = outSent.toString();
                    out.write(s);
                    out.newLine();
                    fullOut.write(text.toString());
                    fullOut.newLine();
                    //				System.out.println(s);
                    sentCount++;
                    text = new StringBuffer("#" + sentCount + "#\t");
                    outSent = new StringBuffer("#" + sentCount + "#\t");

                }
            } else if (ch == ' ') {
                token = "";
                isTag = false;
            } else {
                if (isTag) {
                    tag += ch;//System.out.println("tag="+tag);
                } else if (!Character.isWhitespace(ch)) {
                    token += ch;
                }
            }
            ich = br.read();
//			System.out.println(ch);
        }

        br.close();
        out.flush();
        out.close();
        fullOut.close();
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        AlignCheck ac;
        if (args.length == 4) {
            ac = new AlignCheck(args[0], args[1], args[2], args[3]);
        } else if (args.length == 1) {
            ac = new AlignCheck("/home/ldc_data/english-arabic_treebank/eng_ara_tbk/data/pennTB-style-trees", "/home/ldc_data/arabic_treebank/data/treebank/without-vowel", "/home/ldc_data/english-arabic_treebank/eng_ara_tbk/docs/afp-filname.map", args[0]);
        } else {
            System.out.println("Wrong number of arguments.\r\nUsage: AlignCheck [english_path] [arabic_path] [mapping_file] [output_path]\r\n or \r\n AlignCheck [output_path] \r\n for running with default input paths ");
            return;
        }

        ac.run();
    }
}
