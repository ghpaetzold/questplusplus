package shef.mt.tools.mqm.core.fluency.inconsistency;

import shef.mt.features.util.Sentence;
import shef.mt.tools.ResourceProcessor;
import shef.mt.tools.mqm.Context;
import shef.mt.tools.mqm.GlobalProcessor;
import shef.mt.tools.mqm.resources.AbbreviationDictionary;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * @author <a href="mailto:mail.jie.jiang@gmail.com">Jie Jiang</a>
 * @date 9/12/13
 */
public class AbbreviationsProcessor extends ResourceProcessor implements GlobalProcessor {
    private AbbreviationDictionary abbreviationDictionary = null;

    private HashMap<String, String> position2abbrev = new LinkedHashMap<String, String>(); //TODO use Pair class to replace String position

    public AbbreviationsProcessor(AbbreviationDictionary abbreviationDictionary) {
        this.abbreviationDictionary = abbreviationDictionary;
    }

    @Override
    public void processNextSentence(Sentence sentence) {
        assert abbreviationDictionary != null;
        String strLine = sentence.getText();
        int abbrevConflicts = 0;
        Set<String> abbrevs = abbreviationDictionary.getAbbrevSet();
        for (String abbrev : abbrevs) {
            int pos = 0;
            for (String word : sentence.getTokens()) {
                 if (word.equals(abbrev)) {
                     String position = sentence.getIndex() + "-" + pos;
                     for (Map.Entry<String, String> entry : position2abbrev.entrySet()) {
                         String aPos = entry.getKey();
                         String aAbbrev = entry.getValue();
                         if (aAbbrev != abbrev) {  //not the same one
                             //find how close they are by meaning
                             Set<String> meaningSetA = new HashSet<String>(abbreviationDictionary.getMeaningSetOfAbbreviation(aAbbrev));
                             Set<String> meaningSetB = abbreviationDictionary.getMeaningSetOfAbbreviation(abbrev);
                             meaningSetA.retainAll(meaningSetB);
                             if (meaningSetA.size() > 0) {
                                 abbrevConflicts ++;
                             }
                         }
                     }
                 }
            }
        }
        sentence.setValue("abbrev_conflicts", abbrevConflicts); //number of conflicts
        if (position2abbrev.size() > 0) {
            sentence.setValue("abbrev_conflicts_divided_by_count", abbrevConflicts * 1.0 / position2abbrev.size());
        } else {
            sentence.setValue("abbrev_conflicts_divided_by_count", 0.0);
        }


    }

    @Override
    public void globalProcessing(Context context) {
        assert abbreviationDictionary != null;
        Set<String> abbrevs = abbreviationDictionary.getAbbrevSet();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(context.getTargetFilePath())));
            String strLine;
            int lineCount = 0;
            while ((strLine = br.readLine()) != null) {
                strLine = strLine.trim();
                for (String abbrev : abbrevs) {
                    int pos = 0;
                    for (String word : strLine.split("\\s+")) {
                        if (word.equals(abbrev)) {
                            String position = lineCount + "-" + pos;
                            position2abbrev.put(position, abbrev);
                        }
                        pos ++;
                    }
                }
                lineCount ++;
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
}
