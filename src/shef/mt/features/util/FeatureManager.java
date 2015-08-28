package shef.mt.features.util;

import shef.mt.util.Logger;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.regex.*;
import java.util.*;

import shef.mt.features.impl.Feature;
import shef.mt.tools.ResourceManager;

/**
 * The FeatureManager controls the loading, instantiation and running of the
 * features. <br> It parses the user-supplied list of feature indeces and uses a
 * FeatureLoader to instantiate the selected features<br> It also provides a
 * method for running the features over a given pair of source-target Sentences
 *
 * @author Catalina Hallett
 *
 */
public class FeatureManager {

    static HashMap<String, Feature> features;
    private static HashSet<String> featureList;
    private static FeatureLoader featureLoader;
    private static String featureConfig;

    /**
     * instantiates the FeatureManager from a list of feature indeces given as a
     * String and a feature configuration file
     *
     * @param featureArgs
     * @param featureFile
     */
    public FeatureManager(String featureArgs, String featureFile) {
        //parsing the feature list that is supplied as a parameter and populate featureList with the feature indeces
        featureConfig = featureFile;
        parseFeatureArgs(featureArgs);
        registerFeatures();

    }

    public FeatureManager(String featureFile) {
        featureConfig = featureFile;
    }

    public void setFeatureList(String featureArgs) {
        if (!featureArgs.equals("all")) {
            parseFeatureArgs(featureArgs);
        }
//		else
//			loadAllFeatures();
        registerFeatures();
    }

    public void loadAllFeatures() {
        featureLoader = new FeatureLoader(featureConfig);

    }
    
    public boolean hasFeature(String index) {
    	return features.containsKey(index);
    }

    /**
     * constructs a list of available features by matching features listed in
     * the feature configuration file with those requested by the application
     */
    public void registerFeatures() {
        featureLoader = new FeatureLoader(featureConfig);
        if (features == null) {
            features = new HashMap<String, Feature>();
        }
        if (featureList == null || featureList.size() == 0) //all features
        {
            features.putAll(featureLoader.getAllFeatures());
//			System.out.println(features);
            return;
        }
        Iterator<String> it = featureList.iterator();
        while (it.hasNext()) {
            String index = it.next();
            if (index.length() == 1) {
                index = "0" + index;
            }
            Feature f = featureLoader.getFeature(index);
            if (f != null) {
                features.put(index, f);
            }
            // else throw an exception feature argument not found
        }

//		System.out.println(features.keySet());
    }

    /**
     * removes a Feature from the features list
     *
     * @param index
     */
    public void deregisterFeature(String index) {
        features.remove(index);
    }

    public void parseFeatureArgs(String featureArgs) {
        String plus;
        String minus = "";
        featureArgs = featureArgs.trim();
        int separator = featureArgs.indexOf("/");
        if (separator == -1) {
            plus = featureArgs;
        } else {
            plus = featureArgs.substring(0, separator).trim();
            minus = featureArgs.substring(separator + 1).trim();
        }
        
        try {
            featureList = parseArgs(plus);
            HashSet minusArgs = parseArgs(minus);
            featureList.removeAll(minusArgs);
            printFeatures();
        } catch (FeatureArgumentException e) {
            e.printStackTrace();
        }
    }

    public HashSet<String> parseArgs(String args) throws FeatureArgumentException {
        System.out.println(args);
        HashSet<String> al = new HashSet<String>();
        Pattern p = Pattern.compile("[\\[,\\]]");
        String[] result = p.split(args);
//		 System.out.println("arguments:");
        int index;
        String first;
        String second;
        int firstInt;
        int secondInt;
        for (String s : result) {
            String trimmed = s.trim();
            if (trimmed.contains("-")) {
                index = trimmed.indexOf('-');
                first = trimmed.substring(0, index).trim();
                second = trimmed.substring(index + 1).trim();

                firstInt = Integer.parseInt(first);
                secondInt = Integer.parseInt(second);
                if (firstInt > secondInt) {
                    throw new FeatureArgumentException("not a valid interval");
                } else {
                    for (int i = firstInt; i <= secondInt; i++) {
                        al.add(String.valueOf(i));
                    }
                }
            } else {
                al.add(trimmed);
            }
        }

        return al;
    }

    public void printFeatures() {
        Iterator<String> it = featureList.iterator();
        while (it.hasNext()) {
            System.out.println(it.next());
        }
    }
    /*
    Returns all the resources required by the features stored.
    */
    public HashSet<String> getRequiredResources(){
        //Initialize final set of resources:
        HashSet<String> result = new HashSet<>();
        
        //Get features' indeces:
        Set<String> fIndeces = features.keySet();
        ArrayList<String> featureIndeces = new ArrayList<>(fIndeces);
        Collections.sort(featureIndeces);
        
        //Add each feature's required resources to resulting set:
        Iterator<String> it = featureIndeces.iterator();
        Feature f;
        while (it.hasNext()) {
            String index = it.next();
            f = features.get(index);
            
            //Get feature's required resources:
            HashSet<String> requiredResources = f.getResources();
            
            //Add it to resulting required resources:
            result.addAll(requiredResources);
        }
        
        //Return resulting set of resources:
        return result;
    }

    //HACK
    public String runFeatures(Sentence source, Sentence target) {
        StringBuffer result = new StringBuffer();
        Set<String> fIndeces = features.keySet();

        ArrayList<String> featureIndeces = new ArrayList<String>(fIndeces);

        Collections.sort(featureIndeces);
//		System.out.println(featureIndeces.size()+" feature indeces: "+ featureIndeces);
        Iterator<String> it = featureIndeces.iterator();
        Feature f;
        while (it.hasNext()) {
            String index = it.next();
            //System.out.println(index);
            f = features.get(index);
//			System.out.println(index);

            // Modified by José de Souza
            // every new sentence pair has new features
            // therefore, the feature object state must be reset
            f.reset();

            if (f.isComputable()) {
                f.run(source, target);
                Integer featsNumber = f.getFeaturesNumber();
                for (int i = 1; i <= featsNumber; i++) {
                    result.append(f.getValue(i) + "\t");
            }

            } else {
                Logger.log("Feature " + f.getIndex() + " cannot run because some of its dependencies are missing.");
                System.out.println("Feature " + f.getIndex() + " cannot run because some of its dependencies are missing.");
                features.remove(index);
//				System.out.println(features.size());
            }
        }
//		System.out.println("Result:");
//		System.out.println(result.toString());
//		System.out.println("");
        return result.toString();
    }

    public void printFeatureIndeces() {
        Set<String> fIndeces = features.keySet();
        ArrayList<String> featureIndeces = new ArrayList<String>(fIndeces);

        Collections.sort(featureIndeces);
        
        Iterator<String> it = featureIndeces.iterator();
        while (it.hasNext()) {
            System.out.print(it.next() + "\t");
        }
        System.out.println();
    }
}
