/**
 *
 */
package shef.mt.util;

import java.io.*;

/**
 * @author cat
 *
 */
public class GizaFilter {

    String input;
    String output;
    float threshold = 0.01f;

    public GizaFilter(String input, String output) {
        this.input = input;
        this.output = output;
    }

    public void run() {
        try {
            BufferedWriter bwOut = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output), "utf-8"));
            BufferedReader brInput = new BufferedReader(new InputStreamReader(new FileInputStream(input), "utf-8"));

            String line = brInput.readLine();
            while (line != null) {
                String[] comps = line.split(" ");
                String word = comps[0].toLowerCase();
                String trans = comps[1].toLowerCase();
                float prob = Float.parseFloat(comps[2]);
                if (prob >= threshold) {
                    bwOut.write(word + " " + trans + " " + prob + "\r\n");
                }
                line = brInput.readLine();
            }
            brInput.close();
            bwOut.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        (new GizaFilter(args[0], args[1])).run();
    }
}
