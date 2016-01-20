
package shef.mt.tools;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import shef.mt.features.util.AlignmentData;
import shef.mt.features.util.Doc;
import shef.mt.features.util.Sentence;

/**
 * Adds block alignment values to be used by BinQE features.
 * @author GustavoH
 */
public class BlockAlignmentProcessor extends ResourceProcessor {
   
    private BufferedReader reader;

    public BlockAlignmentProcessor(String path) {
        try {
            reader = new BufferedReader(new FileReader(path));
        } catch (Exception ex) {
            reader = null;
            System.out.println("ERROR: File for the -alignments parameter is missing.");
            Logger.getLogger(BlockAlignmentProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void processNextSentence(Sentence target) {
        try {
            //Read data from reader:
            String[] data = reader.readLine().trim().split(" ");
            
            //Create hash of alignments target->source:
            HashMap<Integer, HashSet<Integer>> alignments = new HashMap<>();
            HashSet<Integer> sourceAligned = new HashSet<>();
            HashSet<Integer> targetAligned = new HashSet<>();
            for(String item: data){
                //Extract alignment data:
                String[] splitItem = item.split("-");
                int sourceIndex = Integer.parseInt(splitItem[0].trim());
                int targetIndex = Integer.parseInt(splitItem[1].trim());
                if(alignments.get(targetIndex)==null){
                    HashSet<Integer> alignedTo = new HashSet<>();
                    alignments.put(targetIndex, alignedTo);
                }
                //Add alignment to hashes:
                alignments.get(targetIndex).add(sourceIndex);
                sourceAligned.add(sourceIndex);
                targetAligned.add(targetIndex);
            }
            
            //Create alignment data object:
            AlignmentData ad = new AlignmentData(alignments, sourceAligned, targetAligned, target);
            
            //Add resource to sentence:
            target.setValue("blockalignments", ad);
        } catch (IOException ex) {
            target.setValue("blockalignments", null);
            Logger.getLogger(BlockAlignmentProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void processNextDocument(Doc source) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
