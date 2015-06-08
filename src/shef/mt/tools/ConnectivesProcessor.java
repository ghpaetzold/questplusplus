/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shef.mt.tools;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import shef.mt.features.util.Doc;
import shef.mt.features.util.FeatureManager;
import shef.mt.features.util.Sentence;
import shef.mt.util.PropertiesManager;

/**
 *
 * @author carol
 */
public class ConnectivesProcessor extends ResourceProcessor {
    private HashMap<String, Integer> allConnectivesTarget;
    private HashMap<String, Integer> allConnectivesSource;
    @Override
    public void processNextSentence(Sentence source) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    
    public void initialize(PropertiesManager propertiesManager,
			FeatureManager featureManager) {
        String filenameAddConnTarget = propertiesManager.getProperty("additives.connectives.target");
        String filenameTemConnTarget = propertiesManager.getProperty("temporal.connectives.target");
        String filenameLogConnTarget = propertiesManager.getProperty("logical.connectives.target");
        String filenameCauConnTarget = propertiesManager.getProperty("causal.connectives.target");
        String filenameAddConnSource = propertiesManager.getProperty("additives.connectives.source");
        String filenameTemConnSource = propertiesManager.getProperty("temporal.connectives.source");
        String filenameLogConnSource = propertiesManager.getProperty("logical.connectives.source");
        String filenameCauConnSource = propertiesManager.getProperty("causal.connectives.source");
        
	FileInputStream fstream;
		
        allConnectivesTarget = new HashMap<String,Integer>(); 
        allConnectivesSource = new HashMap<String,Integer>(); 
                
		
		try {
                    fstream = new FileInputStream(filenameAddConnTarget);


                    BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

                    String strLine;

                    //Read File Line By Line
                    while ((strLine = br.readLine()) != null)   {
                            String[] tok = strLine.split("\t");
                            if (!allConnectivesTarget.containsKey(tok[0])){
                                allConnectivesTarget.put(tok[0], Integer.parseInt(tok[1]));
                            }
                            br.readLine();
                    }        
                    //Close the input stream
                    br.close();
                    
                    
		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }

    @Override
    public void processNextDocument(Doc source) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
