/**
 *
 */
package shef.mt.features.impl.bb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.StringTokenizer;

import shef.mt.features.impl.Feature;
import shef.mt.features.util.Sentence;
import shef.mt.tools.Giza;
import shef.mt.tools.Giza2;
//import wlv.mt.features.wce.Numerical;
import java.lang.*;
import java.io.*;
import java.util.*;




/**
 * Number of stopwords between in target sentence
 *
 * @author Luong Ngoc Quang
 *
 *
 */
public class Feature5009 extends Feature {

    //final static Float probThresh = 0.10f;

    public Feature5009() {
        setIndex(5009);
        setDescription("List of all POS in the target sentence");
        //HashSet res = new HashSet<String>();
        //res.add("giza.path");
        //setResources(res);
    }

    /* (non-Javadoc)
     * @see wlv.mt.features.util.Feature#run(wlv.mt.features.util.Sentence, wlv.mt.features.util.Sentence)
     */
    @Override
    public void run(Sentence source, Sentence target) {
        
        // TODO Auto-generated method stub
        String text = target.getText();
        //String t = "\"Peace\" is \" nice \"";
        try
        {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("config/config_en-es.properties")));
        String postaggerpath;
        while ((postaggerpath=br.readLine())!=null)
        {
            if (postaggerpath.startsWith("sourcetarget.postagger.scripts")) break;
        }
        br.close();
        int index = postaggerpath.indexOf("=");
        postaggerpath= postaggerpath.substring(index+1).trim();
        System.out.println(postaggerpath);
        Runtime r;
        r = Runtime.getRuntime();
        
        
        
        ProcessBuilder pb = new ProcessBuilder("./src/wlv/mt/features/wce/postagger.sh",postaggerpath,text);//"The world is not enough for all of us .");
        //Map<String, String> env = pb.environment();
        //env.put("VAR1", "myValue");
 //env.remove("OTHERVAR");
 //env.put("VAR2", env.get("VAR1") + "suffix");
 //pb.directory(new File("myDir"));
        Process p = pb.start();
        
        
        
        
        
        
        
         //p=Runtime.getRuntime().exec(execution5);
        // p=Runtime.getRuntime().exec(execution);
        p.waitFor(); 
        
        
        
        
        BufferedReader reader=new BufferedReader(new InputStreamReader(p.getInputStream())); 
        String line=reader.readLine(); 
        while(line!=null) 
        { 
            System.out.println(line); 
            if (line != null) setValue(line);
            line=reader.readLine(); 
            
            //System.out.println(line);
        
        }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        //float i=0.07f;

        //setValue(i);
       
} 

//} 
//catch(IOException e1) {} 
//catch(InterruptedException e2) {} 

//System.out.println("Done"); 
        
        
        
        
    //}
    public static void main(String[] args)
    {
        Feature5009 f = new Feature5009();
        //System.out.println("fhajfdas");
        f.run(null,null);
    }
    
}

