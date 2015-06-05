/**
 *
 */
package shef.mt.tools;

import shef.mt.features.util.Sentence;
import java.util.HashSet;
import java.util.ArrayList;
import shef.mt.util.Logger;

import java.io.*;

/**
 * This class is a processor of files containing Named Entity information
 *
 * @author Catalina Hallett
 *
 *
 */
public class NERProcessor {

    BufferedReader br;
    String input;
    private String person_str;
    private String org_str;
    private String location_str;
    private String PERSON = "person";
    private String LOCATION = "location";
    private String ORG = "organization";
    private String NER = "ner";

    /**
     *
     * @param input Path of the file containing the Named Entity information
     */
    public NERProcessor(String input, String[] values) {
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(input)));
            ResourceManager.registerResource("ner");
            Logger.log("Running named entity processor");
            PERSON = values[0];
            LOCATION = values[1];
            ORG = values[2];
            NER = values[3];
            person_str = values[4];
            location_str = values[5];
            org_str = values[6];
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public boolean isAvailable() {
        return br != null;
    }

    /**
     * Reads in the current sentence from the input file, extracts the relevant
     * named entities (PERSON, ORGANIZATION, LOCATION) and stores their count in
     * the Sentence object passed as parameter
     *
     * @param sent The sentence object which will be enhanced with NE counts as
     * a result of this function
     */
    public void processNextSentence(Sentence sent) {
        try {
            if (br == null) {
                return;
            }

            int pers = 0;
            int loc = 0;
            int org = 0;
            String ner;
            String line = br.readLine();
            if (line == null) {
                br.close();
                Logger.log("NER processor done!");
                return;
            }
            ArrayList<String> orgs = new ArrayList<String>();
            ArrayList<String> locs = new ArrayList<String>();
            ArrayList<String> persons = new ArrayList<String>();
            String[] values;
            while (!line.isEmpty()) {
                if (line.startsWith("#")) {
                    line = br.readLine();
                }
                values = line.split("\t");
                ner = values[1];
                if (ner.endsWith(org_str)) {
                    org++;
                    orgs.add(values[0].toLowerCase());
//					System.out.println(ner+" "+values[0]);
                } else if (ner.endsWith(location_str)) {
                    loc++;
                    locs.add(values[0].toLowerCase());
//					System.out.println(ner+" "+values[0]);
                } else if (ner.endsWith(person_str)) {
                    pers++;
                    persons.add(values[0].toLowerCase());
//					System.out.println(ner+" "+values[0]);
                }
                line = br.readLine();
            }
            sent.setValue(ORG, orgs);
            sent.setValue(LOCATION, locs);
            sent.setValue(PERSON, persons);
            sent.setValue(NER, pers + loc + org);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
    }
}
