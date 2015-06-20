package shef.mt.tools.jrouge;

import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import shef.mt.features.util.Doc;
import shef.mt.features.util.Sentence;
import shef.mt.tools.ResourceProcessor;

public class ROUGEProcessor extends ResourceProcessor {

    private final ArrayList<RougeN> rouges;
    private int sentenceCounter;

    public ROUGEProcessor(String sourceFile, String targetFile) {
        // Initialize counter:
        this.sentenceCounter = 0;

        //Initialize ROUGE array:
        rouges = new ArrayList<>();
        try {
            BufferedReader sbr = new BufferedReader(new FileReader(sourceFile));
            BufferedReader tbr = new BufferedReader(new FileReader(targetFile));

            while (sbr.ready()) {
                String srcsent = sbr.readLine().trim();
                String trgsent = tbr.readLine().trim();

                ArrayList<String> srclist = new ArrayList<>();
                ArrayList<String> trglist = new ArrayList<>();
                srclist.add(srcsent);
                trglist.add(trgsent);

                RougeSummaryModel srcmodel = new RougeSummaryModel(null);
                RougeSummaryModel trgmodel = new RougeSummaryModel(null);

                srcmodel.setSentences(srclist);
                trgmodel.setSentences(trglist);

                RougeN rouge = new RougeN(srcmodel, trgmodel, Integer.MAX_VALUE, Integer.MAX_VALUE, 2, (float) 0.5);

                this.rouges.add(rouge);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ROUGEProcessor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ROUGEProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void processNextSentence(Sentence source) {
        RougeN rouge = this.rouges.get(this.sentenceCounter);

        this.sentenceCounter++;

        source.setValue("rouge-n", rouge);
    }

    @Override
    public void processNextDocument(Doc source) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
