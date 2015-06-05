package shef.mt.util;

/**
 *
 */
import java.util.*;
import java.io.*;

/**
 *
 * Transforms a giza translation file where entries represent indeces in a
 * vocabulary into a file where entries are represented by words<br>
 *
 * Usage: wlv.mt.util.GizaMerger <input file> <source vocabulary> <target
 * vocabulary> <output file> <probability threshold> <br>
 *
 * The last parameter, <probability threshold>, is optional. If it is used, only
 * those translation with the probability higher than the probability threshold
 * will be included in the output
 *
 *
 * @author Catalina Hallett
 *
 */
public class GizaMerger {

    private static HashMap<String, String> en = new HashMap<String, String>();
    private static HashMap<String, String> ar = new HashMap<String, String>();
    private static float thresh = 0;

    public static void run(String gizaFile, String arVcb, String enVcb, String out) {
        run(gizaFile, arVcb, enVcb, out, "0");

    }

    public static void run(String gizaFile, String arVcb, String enVcb, String out, String probThresh) {
        try {
            thresh = (new Float(probThresh)).floatValue();

            BufferedReader brGiza = new BufferedReader(new InputStreamReader(new FileInputStream(gizaFile), "utf-8"));
            BufferedReader brEn = new BufferedReader(new InputStreamReader(new FileInputStream(enVcb), "utf-8"));
            BufferedReader brAr = new BufferedReader(new InputStreamReader(new FileInputStream(arVcb), "utf-8"));
            BufferedWriter bwOut = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(out), "utf-8"));


            String line = brEn.readLine();
            String split[];
            while (line != null && !line.trim().isEmpty()) {
//				System.out.println(line);
                split = line.split("\\s+");
                en.put(split[0], split[1]);
                line = brEn.readLine();
            }
            brEn.close();

            line = brAr.readLine();
            while (line != null && !line.trim().isEmpty()) {
                split = line.split(" ");
                ar.put(split[0], split[1]);
                line = brAr.readLine();
            }
            brAr.close();



            String gizaLine = brGiza.readLine();
            String arWord;
            String enWord;
            float prob;
            while (gizaLine != null) {
                split = gizaLine.split(" ");
                prob = Float.parseFloat(split[2]);
                if (prob >= thresh) {
                    arWord = ar.get(split[0]);
                    enWord = en.get(split[1]);
                    bwOut.write(arWord + "\t" + enWord + "\t" + prob + "\r\n");
                }
                gizaLine = brGiza.readLine();
            }
            brGiza.close();
            bwOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (args.length < 4) {
            System.out.println("Not enough arguments! Required min 4");
            return;
        }
        if (args.length == 4) {
            GizaMerger.run(args[0], args[1], args[2], args[3]);
        } else {
            GizaMerger.run(args[0], args[1], args[2], args[3], args[4]);
        }
//		(new wlv.mt.util.GizaFilter(args[3], args[3]+".filtered")).run();
    }
}
