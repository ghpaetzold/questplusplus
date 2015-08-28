
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
 * Adds word alignment information to input sentence pairs.
 * @author GustavoH
 */
public class AlignmentProcessor extends ResourceProcessor {
   
    private BufferedReader reader;

    public AlignmentProcessor(String path) {
        try {
            reader = new BufferedReader(new FileReader(path));
        } catch (FileNotFoundException ex) {
            reader = null;
            Logger.getLogger(AlignmentProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void processNextSentence(Sentence target) {
        try {
            //Read data from reader:
            String[] data = reader.readLine().trim().split(" ");
            
            //Create hash of alignments target->source:
            HashMap<Integer, Integer> alignments = new HashMap<>();
            for(String item: data){
                //Extract alignment data:
                String[] splitItem = item.split("-");
                int sourceIndex = Integer.parseInt(splitItem[0].trim());
                int targetIndex = Integer.parseInt(splitItem[1].trim());
                if(alignments.get(targetIndex)!=null){
                    System.out.println("Problem!");
                }
                //Add alignment to hash:
                alignments.put(targetIndex, sourceIndex);
            }
            
            //Add resource to sentence:
            target.setValue("alignments.file", alignments);
        } catch (IOException ex) {
            target.setValue("alignments.file", null);
            Logger.getLogger(AlignmentProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void processNextDocument(Doc source) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
