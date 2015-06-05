/*
 *  Copyright (c) 2013 Ergun Bicici.
 *  All rights reserved.
 * 
 */

/**
 * @file F1Measurer.java An implementation of the F1 measure for machine translation evaluation.
 * @author Ergun Bicici. 
 * 
 * Citations:
 * 
 * Ergun Bicici and Deniz Yuret. 2011b. RegMT system for machine translation, system combination, and
 * evaluation. In Proceedings of the Sixth Workshop on Statistical Machine Translation, pages 323–329,
 * Edinburgh, Scotland, July. Association for Computational Linguistics.
 * 
 * @InProceedings{BiciciYuret:RegMT:WMT11,
 * author = {Ergun Bi\c{c}ici and Deniz Yuret},
 * title = {{RegMT} System for Machine Translation, System Combination, and Evaluation},
 * booktitle = {Proceedings of the {S}ixth {W}orkshop on {S}tatistical {M}achine {T}ranslation},
 * month = {July},
 * year = {2011},
 * address = {Edinburgh, Scotland},
 * publisher = {Association for Computational Linguistics},
 * pages = {323--329},
 * url = {http://www.aclweb.org/anthology/W11-2137},
 * keywords = "Machine Translation, Machine Learning",
 * }
 * 
 * Ergun Bicici. 2011. The Regression Model of Machine Translation. Ph.D. thesis, Koc University. 
 * Supervisor: Deniz Yuret.
 * 
 * @phdthesis{BiciciThesis,
 * author = {Ergun Bi\c{c}ici},
 * note = {Supervisor: Deniz Yuret},
 * title = {The Regression Model of Machine Translation},
 * year = {2011},
 * school = {Ko\c{c} University},
 * keywords = "Machine Translation, Machine Learning, Natural Language Processing, Artificial Intelligence",
 * }
 * 
 */

package shef.mt.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * An implementation of the F1 measure for machine translation evaluation.
 */
public class F1Measurer {
	
    /** We'll consider up to 5-grams in F1 */
    private static final int MAX_NGRAM = 5;
    
    /**
     * Constructor.
     */
    public F1Measurer() {

    }
    
    public HashMap<String,Integer> getFeatures(String [] parts, int n) {
        int xlen = parts.length;
        HashMap<String, Integer> xfeatures = new HashMap<String, Integer>();
        String key = "";
        int plen = 0;
        for (int p=1; p < n+1; p++) {
            for (int i=0; i < xlen - p + 1; i++) {
            	plen = 0;
            	for (int j=i; j < i+p; j++)
            		if (j < parts.length) {
            			key = key.concat(parts[j] + " ");
            			plen += 1;
            		}
            	key = key.trim();
                if (xfeatures.containsKey(key))
                    xfeatures.put(key, xfeatures.get(key) + plen);
                else
                    xfeatures.put(key, plen);
                key = "";
            }
        }
        return xfeatures;
    }
    
    public int [] getFeaturesLimited(HashMap<String,Integer> newfeatures, HashMap<String,Integer> featuresMap, String [] features) {
    	int [] xfeatures = new int [features.length];
        for (Map.Entry<String, Integer> e: newfeatures.entrySet()) {
            if (featuresMap.containsKey(e.getKey()))
                xfeatures[featuresMap.get(e.getKey())] += e.getValue();
        }
        return xfeatures;
    }
    
    public int [] boundVectors (int [] input) {
    	int [] boundedVec = new int [input.length];
    	for (int i=0; i < input.length; i++) {
    		if (input[i] >= 1)
    			boundedVec[i] = 1;
    		else if (input[i] < 0)
    			boundedVec[i] = 0;
    	}
    	return boundedVec;
    }

    public double f1Rate(int [] refVec, int [] transVec, int reflength) {
    	assert refVec.length == transVec.length;
        double [] yhatnew = new double[refVec.length];
        double numTP = 0.0;
        double numFP = 0.0;
        double numFN = 0.0;
        double transValue = 0.0;
        for (int i=0; i< refVec.length; i++) {
        	transValue = (double) transVec[i];
            if (refVec[i] > 0) {
                if (transVec[i] >= 0.5) {
                    numTP += transValue;
                    yhatnew[i] = transValue;
                }
                else {
                    numFN += transValue;
                    yhatnew[i] = 0;
                }
            }
            else {
                if (transVec[i] >= 0.5) {
                    numFP += transValue;
                    yhatnew[i] = transValue;
                }
                else {
                    yhatnew[i] = 0;
                }
            }
        }
        numFN = reflength - numTP;
        double numTPFP = numTP + numFP;
        double numTPFN = numTP + numFN;
        double valuep = 0.0;
        // Precision:
        if (numTPFP > 0)
            valuep = numTP / numTPFP;
        double valuer = 0.0;
        // Recall:
        if (numTPFN > 0)
            valuer = numTP / numTPFN;
        // F1:
        double valuef1 = 0.0;
        if ((valuep + valuer) > 0)
            valuef1 = (2 * valuep * valuer / (valuep + valuer));
        return valuef1;
    }
    
    // Ergun
    public double calcSentenceF1(String refSent, String transSent, int N) {
    	String [] transTokens;
        String [] refTokens;
        transTokens = transSent.split("\\s+");
        refTokens = refSent.split("\\s+");
        HashMap<String,Integer> refFeatures = getFeatures(refTokens, N);
        HashMap<String,Integer> transFeatures = getFeatures(transTokens, N);
        String [] transKeys = transFeatures.keySet().toArray(new String[transFeatures.size()]);
        int [] transValues = new int [transKeys.length];
        HashMap<String,Integer> transKeysIndices = new HashMap<String,Integer>();
        for (int i=0; i< transKeys.length; i++) {
        	transKeysIndices.put(transKeys[i], i);
        	transValues[i] = transFeatures.get(transKeys[i]);
        }
        int [] newfeatures = getFeaturesLimited(refFeatures, transKeysIndices, transKeys);
        int [] refVec = boundVectors(newfeatures);
        if (transKeys.length == 0)
        	return 0.0;
        else {
        	int [] transVec = boundVectors(transValues);
        	return f1Rate(refVec, transVec, refFeatures.size());
        }
    }
    
    public double calcSentenceF1(String refSent, String transSent) {
    	String [] transTokens;
        String [] refTokens;
        transTokens = transSent.split("\\s+");
        refTokens = refSent.split("\\s+");
        HashMap<String,Integer> refFeatures = getFeatures(refTokens, MAX_NGRAM);
        HashMap<String,Integer> transFeatures = getFeatures(transTokens, MAX_NGRAM);
        String [] transKeys = transFeatures.keySet().toArray(new String[transFeatures.size()]);
        int [] transValues = new int [transKeys.length];
        HashMap<String,Integer> transKeysIndices = new HashMap<String,Integer>();
        for (int i=0; i< transKeys.length; i++) {
        	transKeysIndices.put(transKeys[i], i);
        	transValues[i] = transFeatures.get(transKeys[i]);
        }
        int [] newfeatures = getFeaturesLimited(refFeatures, transKeysIndices, transKeys);
        int [] refVec = boundVectors(newfeatures);
        if (transKeys.length == 0)
        	return 0.0;
        else {
        	int [] transVec = boundVectors(transValues);
        	return f1Rate(refVec, transVec, refFeatures.size());
        }
    }
    
    public double calcSentenceF1(String [] refTokens, String transSent, int N) {
    	String [] transTokens;
        transTokens = transSent.split("\\s+");
        HashMap<String,Integer> refFeatures = getFeatures(refTokens, N);
        HashMap<String,Integer> transFeatures = getFeatures(transTokens, N);
        String [] transKeys = transFeatures.keySet().toArray(new String[transFeatures.size()]);
        int [] transValues = new int [transKeys.length];
        HashMap<String,Integer> transKeysIndices = new HashMap<String,Integer>();
        for (int i=0; i< transKeys.length; i++) {
        	transKeysIndices.put(transKeys[i], i);
        	transValues[i] = transFeatures.get(transKeys[i]);
        }
        int [] newfeatures = getFeaturesLimited(refFeatures, transKeysIndices, transKeys);
        int [] refVec = boundVectors(newfeatures);
        if (transKeys.length == 0)
        	return 0.0;
        else {
        	int [] transVec = boundVectors(transValues);
        	return f1Rate(refVec, transVec, refFeatures.size());
        }
    }
    
    public double calcSentenceF1(String [] refTokens, String transSent) {
    	String [] transTokens;
        transTokens = transSent.split("\\s+");
        HashMap<String,Integer> refFeatures = getFeatures(refTokens, MAX_NGRAM);
        HashMap<String,Integer> transFeatures = getFeatures(transTokens, MAX_NGRAM);
        String [] transKeys = transFeatures.keySet().toArray(new String[transFeatures.size()]);
        int [] transValues = new int [transKeys.length];
        HashMap<String,Integer> transKeysIndices = new HashMap<String,Integer>();
        for (int i=0; i< transKeys.length; i++) {
        	transKeysIndices.put(transKeys[i], i);
        	transValues[i] = transFeatures.get(transKeys[i]);
        }
        int [] newfeatures = getFeaturesLimited(refFeatures, transKeysIndices, transKeys);
        int [] refVec = boundVectors(newfeatures);
        if (transKeys.length == 0)
        	return 0.0;
        else {
        	int [] transVec = boundVectors(transValues);
        	return f1Rate(refVec, transVec, refFeatures.size());
        }
    }
    
    public double calcSentenceF1(String [] refTokens, String [] transTokens, int N) {
        HashMap<String,Integer> refFeatures = getFeatures(refTokens, N);
        HashMap<String,Integer> transFeatures = getFeatures(transTokens, N);
        String [] transKeys = transFeatures.keySet().toArray(new String[transFeatures.size()]);
        int [] transValues = new int [transKeys.length];
        HashMap<String,Integer> transKeysIndices = new HashMap<String,Integer>();
        for (int i=0; i< transKeys.length; i++) {
        	transKeysIndices.put(transKeys[i], i);
        	transValues[i] = transFeatures.get(transKeys[i]);
        }
        int [] newfeatures = getFeaturesLimited(refFeatures, transKeysIndices, transKeys);
        int [] refVec = boundVectors(newfeatures);
        if (transKeys.length == 0)
        	return 0.0;
        else {
        	int [] transVec = boundVectors(transValues);
        	return f1Rate(refVec, transVec, refFeatures.size());
        }
    }

    public double calcSentenceF1(String [] refTokens, String [] transTokens) {
        HashMap<String,Integer> refFeatures = getFeatures(refTokens, MAX_NGRAM);
        HashMap<String,Integer> transFeatures = getFeatures(transTokens, MAX_NGRAM);
        String [] transKeys = transFeatures.keySet().toArray(new String[transFeatures.size()]);
        int [] transValues = new int [transKeys.length];
        HashMap<String,Integer> transKeysIndices = new HashMap<String,Integer>();
        for (int i=0; i< transKeys.length; i++) {
        	transKeysIndices.put(transKeys[i], i);
        	transValues[i] = transFeatures.get(transKeys[i]);
        }
        int [] newfeatures = getFeaturesLimited(refFeatures, transKeysIndices, transKeys);
        int [] refVec = boundVectors(newfeatures);
        if (transKeys.length == 0)
        	return 0.0;
        else {
        	int [] transVec = boundVectors(transValues);
        	return f1Rate(refVec, transVec, refFeatures.size());
        }
    }
    
    public static void main(String[] args) {
    	
        BufferedReader reffile = null;
        BufferedReader transfile = null;
        int linecount = 0;
        
        // parameters check
        if (args.length != 2) {
            System.err.println("Input parameters: reference_file trans_file");
            System.exit(1);
        }
        
        // initialization, opening the files
        F1Measurer f1m = new F1Measurer();
        
        try {
            reffile = new BufferedReader(new FileReader(args[0]));
            transfile = new BufferedReader(new FileReader(args[1]));
        }
        catch(FileNotFoundException e) {
            System.err.println(e.getMessage());
            System.exit(2);
        }
        
        ArrayList<Double> allf1s = new ArrayList<Double>();
        double sumf1 = 0.0;
        double currf1 = 0.0;
        // read sentence by sentence
        while (true) {

            String transLine = null, refLine = null;
            String [] transTokens;
            String [] refTokens;
            
            try {
                refLine = reffile.readLine();
                transLine = transfile.readLine();
            }
            catch (IOException ex) {
                System.err.println(ex.getMessage());
                System.exit(1);
            }
            
            // test for EOF
            if (transLine == null && refLine == null){
                break;
            }
            if (transLine == null || refLine == null){
                System.err.println("The files are of different lengths.");
                System.exit(1);
            }
            
            // split to tokens by whitespace
            transLine.trim(); refLine.trim();
            transTokens = transLine.split("\\s+");
            refTokens = refLine.split("\\s+");
            
            currf1 = f1m.calcSentenceF1(refTokens, transTokens);
            sumf1 += currf1;
            allf1s.add(currf1);
            if (linecount % 1000 == 0) {
                System.err.print(".");
            }
            System.out.println(String.valueOf(linecount) + " " + String.valueOf(currf1));
            linecount++;
        }
        
        // print the result
        System.err.println("Total:" + linecount + " sentences.");
        System.out.println("Average F1 score: " + sumf1 / allf1s.size());
    }

}

