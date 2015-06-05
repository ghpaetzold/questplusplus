package shef.mt.tools;

import java.util.*;
import shef.mt.util.*;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

/**
 *
 * @author David Langlois
 */

/*
 * More details about triggers in Quality Estimation :
 * «"This sentence is wrong." Detecting errors in machine-translated sentences.», Raybaud, Sylvain; Langlois, David; Smaïli, Kamel, Machine Translation, (2011) vol. 25 num. 1 pp. 1-34 
 * "LORIA System for the WMT12 Quality Estimation Shared Task", Langlois D., Raybaud S., Smaïli K., Proceedings of the Seventh Workshop on Statistical Machine Translation. pp 114--119
 *
 */
public class Triggers extends Resource {

    int nb_triggers_max; //nb triggers max v --> w for each v. The nb_triggers_max are kept
    private HashMap<String, HashMap<String, Float>> triggers;
    int lengthMaxSide1;
    int lengthMaxSide2;
    String phraseSeparator;

    public Triggers(String triggersFilePath, String phraseSeparator) {
        super(null);
        triggers = new HashMap<String, HashMap<String, Float>>();
        lengthMaxSide1 = 1;
        lengthMaxSide2 = 1;
        nb_triggers_max = -1;
        this.phraseSeparator = phraseSeparator;
        Logger.log("initiating Triggers from file: " + triggersFilePath);

        if (loadTriggers(triggersFilePath) != -1) {
            shef.mt.tools.ResourceManager.registerResource("IntraLingualTriggers");
        }
    }

    public Triggers(String triggersFilePath, int nbm, String phraseSeparator) {
        super(null);
        triggers = new HashMap<String, HashMap<String, Float>>();
        lengthMaxSide1 = 1;
        lengthMaxSide2 = 1;
        nb_triggers_max = nbm;
        this.phraseSeparator = phraseSeparator;
        Logger.log("initiating Triggers from file: " + triggersFilePath);

        if (loadTriggers(triggersFilePath) != -1) {
            shef.mt.tools.ResourceManager.registerResource("IntraLingualTriggers");
        }
    }

    private int loadTriggers(String filePath) {
        long start = System.currentTimeMillis();
        Logger.log("Loading Triggers from " + filePath + "...");
        System.out.println("Loading Triggers from " + filePath + "...");
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "utf-8"));
            String line = br.readLine();
            while (line != null) {
                parseLine(line);
                line = br.readLine();
            }
            long elapsed = System.currentTimeMillis() - start;
            System.out.println("Triggers loaded in " + elapsed / 1000F + " sec");
            Logger.log("Triggers loaded in " + elapsed / 1000F + " sec");
        } catch (java.io.IOException e) {
            e.printStackTrace();
            return -1;
        }

        // normalization of Mutual Information values

        start = System.currentTimeMillis();
        Logger.log("Normalizing Mutual Information scores...");
        System.out.println("Normalizing Mutual Information scores...");

        Set<String> keyes = triggers.keySet();

        for (String i : keyes) {
            HashMap<String, Float> triggers_of_key = triggers.get(i);
            float som = 0;
            float min = Float.MAX_VALUE;
            Set<String> keyes2 = triggers_of_key.keySet();

            if (nb_triggers_max != -1) {
                String tab[] = new String[triggers_of_key.size()];
                int k = 0;

                // we keep only the nb_triggers_max triggers
                // I use a temp tab to retrieve the keyes with max score
                for (String j : keyes2) {
                    tab[k] = j;
                    k++;
                }
                //bubble sort :-(. Ok, ok, improve yourself if you are not happy
                // I am usre there exist a marvelous class for sorting... But I do not know sufficiently Java to find it.
                int l;
                for (k = 0; k < triggers_of_key.size(); k++) {
                    for (l = k + 1; l < triggers_of_key.size(); l++) {
                        int cmp = triggers_of_key.get(tab[k]).compareTo(triggers_of_key.get(tab[l]));
                        if (cmp < 0) {
                            String temp = tab[k];
                            tab[k] = tab[l];
                            tab[l] = temp;
                        }
                    }
                }

                // triggers with low cost are deleted
                for (k = nb_triggers_max; k < triggers_of_key.size(); k++) {
                    triggers_of_key.remove(tab[k]);
                }

                // set of keyes is updated
                keyes2 = triggers_of_key.keySet();
            }
            // finally : normalisation
            for (String j : keyes2) {
                if (triggers_of_key.get(j) < min) {
                    min = triggers_of_key.get(j);
                }
            }
            for (String j : keyes2) {
                som += triggers_of_key.get(j) + min;
            }
            for (String j : keyes2) {
                triggers_of_key.put(j, (triggers_of_key.get(j) + min) / som);
            }
        }

        long elapsed = System.currentTimeMillis() - start;
        System.out.println("Normalization done in " + elapsed / 1000F + " sec");
        Logger.log("Normalization done in " + elapsed / 1000F + " sec");

        return 0;
    }

    private void parseLine(String line) {
        
        //System.out.println(line);
        
        String[] tabline = line.split(" ");

        if (tabline[0].split(phraseSeparator).length > this.lengthMaxSide1) {
            this.lengthMaxSide1 = tabline[0].split(phraseSeparator).length;
        }

        if (tabline[1].split(phraseSeparator).length > this.lengthMaxSide2) {
            this.lengthMaxSide2 = tabline[1].split(phraseSeparator).length;
        }

        if (!(triggers.containsKey(tabline[0]))) {
            triggers.put(tabline[0], new HashMap<String, Float>());
        }
        triggers.get(tabline[0]).put(tabline[1], new Float(tabline[5]));
    }

    public void print(String s) {
        System.out.println("List of intra-lingual triggers from " + s + " are: ");
        HashMap<String, Float> tr = triggers.get(s);
        Iterator<String> it = tr.keySet().iterator();
        while (it.hasNext()) {
            // to improve : list pairs in sort order
            String n = it.next();
            System.out.print(s + " --> " + n + "\t" + tr.get(n) + "\n");

        }
    }

    public void print() {
        System.out.println("List of intra-lingual triggers are: ");
        Iterator<String> it = triggers.keySet().iterator();
        while (it.hasNext()) {
            // to improve : list pairs in sort order
            String s = it.next();
            HashMap<String, Float> tr = triggers.get(s);
            Iterator<String> it2 = tr.keySet().iterator();
            while (it2.hasNext()) {
                String n = it2.next();
                System.out.print(s + " --> " + n + "\t" + tr.get(n) + "\n");
            }
        }
    }

    /* Now monolingual triggers are built in the same way as bilingual triggers
     * mono is a special case of bilingual : source and target are same file
    public static int buildMonolingualTriggers(String filePath, String filePathOut, int nbMaxTriggers) {
    HashMap<String, Integer> vocabulary = new HashMap<String, Integer>();
    HashMap<String, HashMap<String, Integer>> tr = new HashMap<String, HashMap<String, Integer>>();
    float nblines;
    
    long start;
    
    try {
    start = System.currentTimeMillis();
    Logger.log("Building vocabulary from " + filePath + "...");
    System.out.println("Building vocabulary from " + filePath + "...");
    nblines = 0;
    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "utf-8"));
    String line = br.readLine();
    while (line != null) {
    nblines++;
    String[] tabline = line.split(" ");
    HashMap<String, Integer> vocsent = new HashMap<String, Integer>();
    for (int i = 0; i < tabline.length; i++) {
    if (!vocsent.containsKey(tabline[i])) {
    vocsent.put(tabline[i], 1);
    }
    }
    for (String s : vocsent.keySet()) {
    if (vocabulary.containsKey(s)) {
    vocabulary.put(s, vocabulary.get(s) + 1);
    } else {
    vocabulary.put(s, 1);
    }
    for (String s2 : vocsent.keySet()) {
    if (!s.equals(s2)) {
    if (!tr.containsKey(s)) {
    tr.put(s, new HashMap<String, Integer>());
    }
    if (tr.get(s).containsKey(s2)) {
    tr.get(s).put(s2, tr.get(s).get(s2) + 1);
    } else {
    tr.get(s).put(s2, 1);
    }
    }
    }
    }
    line = br.readLine();
    }
    long elapsed = System.currentTimeMillis() - start;
    System.out.println("Corpus loaded in " + elapsed / 1000F + " sec");
    Logger.log("Corpus loaded in " + elapsed / 1000F + " sec");
    
    start = System.currentTimeMillis();
    Logger.log("Computing mutual information...");
    System.out.println("Computing mutual information...");
    
    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(filePathOut)));
    Object tab[] = tr.keySet().toArray();
    Arrays.sort(tab);
    
    for (Object s : tab) {
    HashMap<String, Integer> t = tr.get((String) s);
    HashMap<String, Float> strAndIm = new HashMap<String, Float>();
    for (String s2 : t.keySet()) {
    float im = (float) Math.log10((t.get(s2) / nblines) / ((vocabulary.get((String) s) / nblines) * (vocabulary.get(s2) / nblines)));
    float pim = (float) (t.get((String) s2) / nblines) * im;
    strAndIm.put(s2, pim);
    }
    Object tab2[] = strAndIm.keySet().toArray();
    for (int k = 0; k < tab2.length; k++) {
    for (int l = k + 1; l < tab2.length; l++) {
    int cmp = strAndIm.get((String) tab2[k]).compareTo(strAndIm.get((String) tab2[l]));
    if (cmp < 0) {
    Object temp = tab2[k];
    tab2[k] = tab2[l];
    tab2[l] = temp;
    }
    }
    }
    
    if (nbMaxTriggers != -1) {
    for (int i = 0; i < tab2.length && i < nbMaxTriggers; i++) {
    String s2 = (String) tab2[i];
    double im = Math.log10((t.get((String) s2) / nblines) / ((vocabulary.get((String) s) / nblines) * (vocabulary.get((String) s2) / nblines)));
    double pim = (t.get((String) s2) / nblines) * im;
    out.println((String) s + " " + (String) s2 + " " + vocabulary.get((String) s) + " " + vocabulary.get((String) s2) + " " + t.get((String) s2) + " " + im + " " + pim);
    }
    } else {
    for (int i = 0; i < tab2.length; i++) {
    String s2 = (String) tab2[i];
    double im = Math.log10((t.get((String) s2) / nblines) / ((vocabulary.get((String) s) / nblines) * (vocabulary.get((String) s2) / nblines)));
    double pim = (t.get((String) s2) / nblines) * im;
    out.println((String) s + " " + (String) s2 + " " + vocabulary.get((String) s) + " " + vocabulary.get((String) s2) + " " + t.get((String) s2) + " " + im + " " + pim);
    }
    }
    
    }
    
    out.close();
    
    
    elapsed = System.currentTimeMillis() - start;
    System.out.println("Computed in " + elapsed / 1000F + " sec");
    Logger.log("Computed in " + elapsed / 1000F + " sec");
    
    return 0;
    } catch (java.io.IOException e) {
    e.printStackTrace();
    return -1;
    }
    
    }
     */
    public static int buildTriggers(String filePathSource, String filePathTarget, String filePathOut, int nbMaxTriggers) {
        // ONLY FOR WORD BASED TRIGGERS, NOT FOR PHRASE BASED.
        HashMap<String, Integer> vocabularySource = new HashMap<String, Integer>();
        HashMap<String, Integer> vocabularyTarget = new HashMap<String, Integer>();
        HashMap<String, HashMap<String, Integer>> tr = new HashMap<String, HashMap<String, Integer>>();
        float nblines;
        boolean mono = false;
        PrintWriter out;
        long elapsed;
        HashMap<String, Float> strAndPIm = new HashMap<String, Float>();
        HashMap<String, Integer> setWords = new HashMap<String, Integer>();
        ArrayList<String> kstab2list = new ArrayList<String>();
        ArrayList<String> kstablist = new ArrayList<String>();
        HashMap<String, Integer> vocSentSource = new HashMap<String, Integer>();
        HashMap<String, Integer> vocSentTarget = new HashMap<String, Integer>();
        String[] tabLineSource = null;
        String[] tabLineTarget = null;
 
        if (filePathSource.equals(filePathTarget)) {
            mono = true;
        }

        long start;

        int num = 1;

        try {
            start = System.currentTimeMillis();
            if (mono) {
                Logger.log("Building vocabulary from " + filePathSource + "...");
                System.out.println("Building vocabulary from " + filePathSource + "...");
            } else {
                Logger.log("Building vocabularies from " + filePathSource + " and " + filePathTarget + "...");
                System.out.println("Building vocabularies from " + filePathSource + " and " + filePathTarget + "...");

            }
            nblines = 0;
            BufferedReader brSource = new BufferedReader(new InputStreamReader(new FileInputStream(filePathSource), "utf-8"));
            BufferedReader brTarget = new BufferedReader(new InputStreamReader(new FileInputStream(filePathTarget), "utf-8"));
            String lineSource = brSource.readLine();
            String lineTarget = brTarget.readLine();
            while (lineSource != null) {
                nblines++;
                if (nblines % 1000 == 0) {
                    System.out.println("   " + nblines);
                }
                tabLineSource = lineSource.split(" ");
                tabLineTarget = lineTarget.split(" ");
                
                vocSentSource.clear();
                vocSentTarget.clear();
                for (int i = 0; i < tabLineSource.length; i++) {
                    if (!vocSentSource.containsKey(tabLineSource[i])) {
                        vocSentSource.put(tabLineSource[i], 1);
                    }
                }
                for (int i = 0; i < tabLineTarget.length; i++) {
                    if (!vocSentTarget.containsKey(tabLineTarget[i])) {
                        vocSentTarget.put(tabLineTarget[i], 1);
                    }
                }
                for (String s : vocSentSource.keySet()) {
                    if (vocabularySource.containsKey(s)) {
                        vocabularySource.put(s, vocabularySource.get(s) + 1);
                    } else {
                        vocabularySource.put(s, 1);
                    }
                }
                for (String s : vocSentTarget.keySet()) {
                    if (vocabularyTarget.containsKey(s)) {
                        vocabularyTarget.put(s, vocabularyTarget.get(s) + 1);
                    } else {
                        vocabularyTarget.put(s, 1);
                    }
                }
                for (String s : vocSentSource.keySet()) {
                    for (String s2 : vocSentTarget.keySet()) {
                        if (mono) {
                            if (!s.equals(s2)) { // trigger x --> x forbidden
                                if (!tr.containsKey(s)) {
                                    tr.put(s, new HashMap<String, Integer>());
                                }
                                if (tr.get(s).containsKey(s2)) {
                                    tr.get(s).put(s2, tr.get(s).get(s2) + 1);
                                } else {
                                    tr.get(s).put(s2, 1);
                                }
                            }
                        } else {
                            // same as mono but trigger x --> x allowed
                            if (!tr.containsKey(s)) {
                                tr.put(s, new HashMap<String, Integer>());
                            }
                            if (tr.get(s).containsKey(s2)) {
                                tr.get(s).put(s2, tr.get(s).get(s2) + 1);
                            } else {
                                tr.get(s).put(s2, 1);
                            }
                        }
                    }
                }

                if (nblines % 100000 == 0) {
                    elapsed = System.currentTimeMillis() - start;
                    System.out.println("(t0+" + elapsed / 1000F + " sec) : copy counts in " + filePathOut + "_" + num);
                    out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePathOut + "_" + num), "utf-8")));
                    Object kstab[] = tr.keySet().toArray();
                    kstablist.clear();
                    for (Object os : kstab) {
                        kstablist.add((String) os);
                    }
                    Collections.sort(kstablist);
                    for (String s : kstablist) {
                        Object kstab2[] = tr.get(s).keySet().toArray();
                        kstab2list.clear();
                        for (Object os2 : kstab2) {
                            kstab2list.add((String) os2);
                        }
                        Collections.sort(kstab2list);
                        for (String s2 : kstab2list) {
                            out.println(s + " " + s2 + " " + tr.get(s).get(s2));
                        }
                    }
                    out.close();
                    num++;
                    tr = new HashMap<String, HashMap<String, Integer>>();
                }

                lineSource = brSource.readLine();
                lineTarget = brTarget.readLine();
            }

            elapsed = System.currentTimeMillis() - start;
            System.out.println("(t0+" + elapsed / 1000F + " sec) : copy counts in " + filePathOut + "_" + num);
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePathOut + "_" + num), "utf-8")));
            Object kstab[] = tr.keySet().toArray();
            kstablist.clear();
            for (Object s : kstab) {
                kstablist.add((String) s);
            }
            Collections.sort(kstablist);
            for (String s : kstablist) {
                Object kstab2[] = tr.get(s).keySet().toArray();
                kstab2list.clear();
                for (Object s2 : kstab2) {
                    kstab2list.add((String) s2);
                }
                Collections.sort(kstab2list);
                for (String s2 : kstab2list) {
                    out.println(s + " " + s2 + " " + tr.get(s).get(s2));
                }
            }
            out.close();

//            unioning all temporary counts files

            String word1[] = new String[num + 1];
            String word2[] = new String[num + 1];
            Integer occ[] = new Integer[num + 1];
            BufferedReader in[] = new BufferedReader[num + 1];
            boolean eofin[] = new boolean[num + 1];
            for (int i = 1; i <= num; i++) {
                in[i] = new BufferedReader(new InputStreamReader(new FileInputStream(filePathOut + "_" + i), "utf-8"));
                String line = in[i].readLine();
                String words[] = line.split(" ");
                word1[i] = words[0];
                word2[i] = words[1];
                occ[i] = Integer.parseInt(words[2]);
                eofin[i] = false;
            }

            Logger.log("Now, unioning partial counts " + filePathOut + "_* into " + filePathOut + "_0");
            System.out.println("Now, unioning partial counts " + filePathOut + "_* into " + filePathOut + "_0");

            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePathOut + "_0"), "utf-8")));

            int nbeof = 0;
            while (nbeof != num) {
                int min = -1;
                for (int i = 1; i <= num; i++) {
                    if (!eofin[i]) {
                        if (min == -1) {
                            min = i;
                        } else if (word1[i].compareTo(word1[min]) < 0) {
                            min = i;
                        } else if (word1[i].compareTo(word1[min]) == 0) {
                            if (word2[i].compareTo(word2[min]) < 0) {
                                min = i;
                            }
                        }
                    }
                }
                int somocc = 0;
                for (int i = 1; i <= num; i++) {
                    if (!eofin[i] && word1[i].equals(word1[min]) && word2[i].equals(word2[min])) {
                        somocc += occ[i].intValue();
                    }
                }
                out.println(word1[min] + " " + word2[min] + " " + somocc);
                String currentword1 = word1[min];
                String currentword2 = word2[min];
                for (int i = 1; i <= num; i++) {
                    if (!eofin[i] && word1[i].equals(currentword1) && word2[i].equals(currentword2)) {
                        String line = in[i].readLine();
                        if (line == null) {
                            eofin[i] = true;
                            nbeof++;
                        } else {
                            String words[] = line.split(" ");
                            word1[i] = words[0];
                            word2[i] = words[1];
                            occ[i] = Integer.parseInt(words[2]);
                        }
                    }
                }
            }

            out.close();

            elapsed = System.currentTimeMillis() - start;
            System.out.println("Counts obtained in " + elapsed / 1000F + " sec");
            Logger.log("Counts obtained in " + elapsed / 1000F + " sec");

//            reading counts file for computing mutual information and selecting best triggers

            start = System.currentTimeMillis();
            Logger.log("Now, computing mutual information from words and (s,t) counts...");
            System.out.println("Now, computing mutual information from words and (s,t) counts...");

            BufferedReader in2 = new BufferedReader(new InputStreamReader(new FileInputStream(filePathOut + "_0"), "utf-8"));
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePathOut), "utf-8")));

            String line = in2.readLine();
            String words[] = line.split(" ");
            String w1 = words[0];
            String w2 = words[1];
            Integer occCouple = Integer.parseInt(words[2]);
            String currentWord = w1;
            setWords.clear();
            setWords.put(w2, occCouple);
            boolean ok = false;
            int nbWords = 1;

            while (!ok) {
                line = in2.readLine();
                if (line == null) {
                    ok = true;
                } else {
                    words = line.split(" ");
                    w1 = words[0];
                    w2 = words[1];
                    occCouple = Integer.parseInt(words[2]);
                    if (w1.equals(currentWord)) {
                        setWords.put(w2, occCouple);
                    } else {
                        //System.out.println(currentWord + " " + setWords.size());
                        nbWords++;
                        if (nbWords % 1000 == 0) {
                            System.out.println("   " + nbWords + " / " + vocabularySource.size());
                        }

                        //HashMap<String, Float> strAndIm = new HashMap<String, Float>();
                        strAndPIm.clear();
                        for (String s : setWords.keySet()) {
                            float im = (float) Math.log10((setWords.get(s) / nblines) / ((vocabularySource.get(currentWord) / nblines) * (vocabularyTarget.get(s) / nblines)));
                            float pim = (float) (setWords.get(s) / nblines) * im;
                            //strAndIm.put(s, im);
                            strAndPIm.put(s, pim);
                        }

                        Object tab[] = setWords.keySet().toArray();
                        if (nbMaxTriggers == -1) {
                            Triggers.mysort(0, tab, strAndPIm, tab.length);
                        } else {
                            Triggers.mysort(0, tab, strAndPIm, nbMaxTriggers);
                        }

                        if (nbMaxTriggers != -1) {
                            for (int i = 0; i < tab.length && i < nbMaxTriggers; i++) {
                                out.println(currentWord + " " + (String) tab[i] + " " + vocabularySource.get(currentWord) + " " + vocabularyTarget.get((String) tab[i]) + " " + setWords.get((String) tab[i]) + " " + strAndPIm.get((String) tab[i]));
                            }
                        } else {
                            for (int i = 0; i < tab.length; i++) {
                                out.println(currentWord + " " + (String) tab[i] + " " + vocabularySource.get(currentWord) + " " + vocabularyTarget.get((String) tab[i]) + " " + setWords.get((String) tab[i]) + " " + strAndPIm.get((String) tab[i]));
                            }
                        }

                        currentWord = w1;
                        setWords.clear();
                        setWords.put(w2, occCouple);
                    }

                }
            }

            // output for last currentWord
            //HashMap<String, Float> strAndIm = new HashMap<String, Float>();
            strAndPIm.clear();
            for (String s : setWords.keySet()) {
                float im = (float) Math.log10((setWords.get(s) / nblines) / ((vocabularySource.get(currentWord) / nblines) * (vocabularyTarget.get(s) / nblines)));
                float pim = (float) (setWords.get(s) / nblines) * im;
                //<strAndIm.put(s, im);
                strAndPIm.put(s, pim);
            }

            Object tab[] = setWords.keySet().toArray();
            if (nbMaxTriggers == -1) {
                Triggers.mysort(0, tab, strAndPIm, tab.length);
            } else {
                Triggers.mysort(0, tab, strAndPIm, nbMaxTriggers);
            }

            if (nbMaxTriggers != -1) {
                for (int i = 0; i < tab.length && i < nbMaxTriggers; i++) {
                    out.println(currentWord + " " + (String) tab[i] + " " + vocabularySource.get(currentWord) + " " + vocabularyTarget.get((String) tab[i]) + " " + setWords.get((String) tab[i]) + " " + strAndPIm.get((String) tab[i]));
                }
            } else {
                for (int i = 0; i < tab.length; i++) {
                    out.println(currentWord + " " + (String) tab[i] + " " + vocabularySource.get(currentWord) + " " + vocabularyTarget.get((String) tab[i]) + " " + setWords.get((String) tab[i]) + " " + strAndPIm.get((String) tab[i]));
                }
            }




            in2.close();
            out.close();

            //delete temporary files
            for (int i = 0; i <= num; i++) {
                java.io.File f = new java.io.File(filePathOut + "_" + i);
                f.delete();
            }

            elapsed = System.currentTimeMillis() - start;
            System.out.println("Computed in " + elapsed / 1000F + " sec");
            Logger.log("Computed in " + elapsed / 1000F + " sec");

            return 0;
        } catch (java.io.IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    static void mysort(int prof, Object tab[], HashMap<String, Float> strAndIm, int nbMaxTriggers) {
        int cmp;
        int nbLeft = 0;
        int nbRight = 0;
        Object left[] = null;
        Object right[] = null;
        Object temp;

        //for (int i = 0; i < prof * 3; i++) {
        //    System.out.print(" ");
        //}
        //System.out.println(tab.length);

        if (tab.length > 1) {
            int m = (tab.length - 1) / 2;
            temp = tab[m];
            for (int i = 0; i < tab.length; i++) {
                if (m != i) {
                    cmp = strAndIm.get((String) tab[i]).compareTo(strAndIm.get((String) tab[m]));
                    if (cmp > 0) {
                        nbLeft++;
                    } else {
                        nbRight++;
                    }
                }
            }
            if (nbLeft > 0) {
                left = new Object[nbLeft];
            }
            if (nbRight > 0) {
                right = new Object[nbRight];
            }
            nbLeft = 0;
            nbRight = 0;
            for (int i = 0; i < tab.length; i++) {
                if (i != m) {
                    cmp = strAndIm.get((String) tab[i]).compareTo(strAndIm.get((String) tab[m]));
                    if (cmp > 0) {
                        left[nbLeft] = tab[i];
                        nbLeft++;
                    } else {
                        right[nbRight] = tab[i];
                        nbRight++;
                    }
                }
            }
            if (nbLeft > 0) {
                mysort(prof + 1, left, strAndIm, nbMaxTriggers);
            }
            if (nbRight > 0 && nbLeft < nbMaxTriggers) {
                mysort(prof + 1, right, strAndIm, nbMaxTriggers);
            }
            for (int i = 0; i < nbLeft; i++) {
                tab[i] = left[i];
            }
            tab[nbLeft] = temp;
            for (int i = 0; i < nbRight; i++) {
                tab[nbLeft + 1 + i] = right[i];
            }
        }
    }

// Build triggers from input files
// Output triggers file.
    public static void main(String[] args) {
        String source = null;
        String target = null;
        String output = null;
        int i = 0;
        String nbmax = null;
        int nbm = -1;

        while (i < args.length) {
            if (args[i].equals("-s")) {
                if (i < args.length - 1) {
                    source = args[i + 1];
                } else {
                    System.err.println("Option '" + args[i] + "' without value");
                    System.exit(-1);
                }
                i += 2;
            } else if (args[i].equals("-t")) {
                if (i < args.length - 1) {
                    target = args[i + 1];
                } else {
                    System.err.println("Option '" + args[i] + "' without value");
                    System.exit(-1);
                }
                i += 2;
            } else if (args[i].equals("-o")) {
                if (i < args.length - 1) {
                    output = args[i + 1];
                } else {
                    System.err.println("Option '" + args[i] + "' without value");
                    System.exit(-1);
                }
                i += 2;
            } else if (args[i].equals("-m")) {
                if (i < args.length - 1) {
                    nbmax = args[i + 1];
                } else {
                    System.err.println("Option '" + args[i] + "' without value");
                    System.exit(-1);
                }
                i += 2;
            } else {
                System.err.println("Option '" + args[i] + "' unknown");
                System.exit(-1);
            }
        }

        if (nbmax != null) {
            try {
                nbm = Integer.parseInt(nbmax);
                if (nbm <= 0) {
                    System.err.println("Max number of triggers should be >0...");
                    System.exit(-1);
                }
            } catch (java.lang.NumberFormatException e) {
                System.err.println("Problem while reading '" + nbmax + "' as an integer");
                System.exit(-1);
            }
        } else {
            nbm = -1;
        }

        if (output == null) {
            System.err.println("Output file is not defined...");
            System.exit(-1);
        }

        if (source != null && target != null) //buildBilingualTriggers(source,target);
        {
            buildTriggers(source, target, output, nbm);
        } else if (source == null && target != null) {
            System.err.println("Target is defined but not source...");
            System.exit(-1);
        } else if (source == null && target == null) {
            System.err.println("At least source should be defined");
            System.exit(-1);
        } else {
            buildTriggers(source, source, output, nbm);
        }
    }

    public float getScore(String a, String b) {
        if (triggers.containsKey(a)) {
            if (triggers.get(a).containsKey(b)) {
                return triggers.get(a).get(b);
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }
}
