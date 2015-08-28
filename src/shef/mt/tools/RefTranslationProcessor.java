
package shef.mt.tools;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import shef.mt.features.util.Doc;
import shef.mt.features.util.Sentence;

/**
 * Adds pseudo-reference information to the sentences processed.
 * @author GustavoH
 */
public class RefTranslationProcessor extends ResourceProcessor {
   
    private BufferedReader reader;

    public RefTranslationProcessor(String path) {
        try {
            reader = new BufferedReader(new FileReader(path));
        } catch (FileNotFoundException ex) {
            System.out.println("ERROR: Failed to open reference translation file: " + path);
            reader = null;
            Logger.getLogger(RefTranslationProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void processNextSentence(Sentence target) {
        try {
            //Read data from reader:
            String[] data = reader.readLine().trim().split(" ");
            
            //Create hash of alignments target->source:
            HashMap<String, Integer> occurrences = new HashMap<>();
            for(String item: data){
                //Add word occurrence to map:
                if(occurrences.get(item)!=null){
                    occurrences.put(item, occurrences.get(item)+1);
                }else{
                    occurrences.put(item, 1);
                }
            }
            
            //Add resource to sentence:
            target.setValue("reftranslation", occurrences);
        } catch (IOException ex) {
            System.out.println("ERROR: Failed to read reference translation for sentence: " + target.getText());
            target.setValue("reftranslations", null);
            Logger.getLogger(RefTranslationProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void processNextDocument(Doc source) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
