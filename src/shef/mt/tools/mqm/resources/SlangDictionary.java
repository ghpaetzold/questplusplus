package shef.mt.tools.mqm.resources;

import shef.mt.tools.Resource;


import java.io.*;
import java.util.HashSet;
import java.util.Set;

/**
 * monolingual slang dict
 * The current english resource was downloaded from http://www.noslang.com/dictionary/
 * More resources for different languages should follow.
 *
 * @author <a href="mailto:mail.jie.jiang@gmail.com">Jie Jiang</a>
 * @date 9/11/13
 */
public class SlangDictionary extends Resource{

    private Set<String> slangDict = new HashSet<String>();

    public SlangDictionary() {

    }
    //TODO, currently an web copied text format is used, one line with dict entry followed by slash, one line with explanation, needs better format
    public void load(String filepath) {
        //read file line by line and put into slang dict
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(filepath)));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                strLine = strLine.trim();
                if (strLine.split("-").length>1) {
                    //String entry = strLine.substring(0, strLine.length()-1).trim();
                    String entry = strLine.split("-")[0].trim();
                    //System.out.println(entry);
                    slangDict.add(entry);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static void main(String args[]) {
        SlangDictionary dict = new SlangDictionary();
        dict.load(args[0]);
    }

    public boolean isSlang(String word) {
        assert word != null;
        return slangDict.contains(word);
    }
}
