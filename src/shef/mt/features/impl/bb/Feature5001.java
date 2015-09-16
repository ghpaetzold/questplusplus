/**
 *
 */
package shef.mt.features.impl.bb;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import shef.mt.features.impl.Feature;
import shef.mt.features.util.Sentence;
import shef.mt.features.util.StopWord;



/**
 * Number of stopwords between in target sentence
 *
 * @author Luong Ngoc Quang
 *
 *
 */
public class Feature5001 extends Feature {

    //final static Float probThresh = 0.10f;

    public Feature5001() {
        setIndex(5001);
        setDescription("Number of stopwords in the source sentence");
        this.addResource("giza.path");
    }

    /* (non-Javadoc)
     * @see wlv.mt.features.util.Feature#run(wlv.mt.features.util.Sentence, wlv.mt.features.util.Sentence)
     */
    @Override
    public void run(Sentence source, Sentence target) {
        // TODO Auto-generated method stub
        String text = source.getText();
        String stopwordPath="";
        //Get the path of stopword list for Spanish
        try
        {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("config/config_en-es.properties")));
        String path;
        while ((path=br.readLine())!=null)
        {
            if (path.startsWith("source.stopwordlist")) break;
        }
        br.close();
        int index = path.indexOf("=");
        stopwordPath= path.substring(index+1).trim();
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
        
        System.out.println(stopwordPath);
        
        ///Users/luongngocquang/Documents/resources/spanishstopwords.txt
        StopWord swcalculator = new StopWord(stopwordPath,text);
        int result = swcalculator.calculate();

        setValue(result);
    }
}

