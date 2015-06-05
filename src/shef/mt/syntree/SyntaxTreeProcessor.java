package shef.mt.syntree;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class SyntaxTreeProcessor {

    String outPath;
    String[] files;

    public SyntaxTreeProcessor(String[] args) {
        outPath = args[args.length - 1];
        files = new String[args.length - 1];
        for (int i = 0; i < files.length; i++) {
            files[i] = args[i];
        }
    }

    public void run() {
        try {
            createOutputFolder();
            for (int i = 0; i < files.length; i++) {
                processFile(files[i], outPath);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void processFile(String input, String outputPath) throws java.io.IOException {
        File f = new File(input);
        String output = outputPath + File.separator + f.getName() + ".txt";
        System.out.println("Processing " + input + " into " + output);
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(input), "utf-8"));
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output), "utf-8"));

        int openBr = 0;
        int sentCount = 1;

        String tag = "";
        String token = "";
        StringBuffer text = new StringBuffer();
        StringBuffer outSent = new StringBuffer();
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
                    sentCount++;
                    text = new StringBuffer();
                    outSent = new StringBuffer();

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
        }

        br.close();
        out.flush();
        out.close();
    }

    public void createOutputFolder() throws java.io.IOException {
        File subdir = new File(outPath);
        if (!subdir.exists()) {
            subdir.mkdir();
        }
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        SyntaxTreeProcessor stp;
        System.out.println(args.length);
        if (args.length >= 2) {
            stp = new SyntaxTreeProcessor(args);
        } else {
            System.out.println("Wrong number of arguments.\r\nUsage: SyntaxTreeProcessor [file] [output_path] ");
            return;
        }

        stp.run();
    }
}
