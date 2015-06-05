/**
 *
 */
package shef.mt.tools;

import shef.mt.features.util.Sentence;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 *
 * This class encapsulates word alignment information It registers the resource
 * "gizaAlign"
 *
 * @author Catalina Hallett
 *
 */
public class WordAlign {

    static HashMap<String, String> mappings;
    static BufferedReader br;

    public WordAlign(String file) {
//		System.out.println(file);
        mappings = new HashMap<String, String>();
        try {
            br = new BufferedReader(new BufferedReader(new FileReader(file)));

        } catch (IOException e) {
            e.printStackTrace();
        }
        ResourceManager.registerResource("gizaAlign");
    }

    public static void processNextSentence(Sentence source, Sentence target) {
        mappings.clear();
        try {
            String line = br.readLine();
            if (line == null) {
                br.close();
                return;
            }
            String[] pairs = line.split(" ");
            String[] matches;
            for (String pair : pairs) {
                matches = pair.split("-");
                //				System.out.print(source.getToken(new Integer(matches[0]))+"-"+target.getToken(new Integer(matches[1]))+" , ");
                mappings.put(source.getToken(new Integer(matches[0])), target.getToken(new Integer(matches[1])));

            }
//		System.out.println();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(br);
        }

    }

    public static String getMapping(String key) {
        return mappings.get(key);
    }
}
