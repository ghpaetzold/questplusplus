/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shef.mt.util;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import shef.mt.features.impl.Feature;

/**
 *
 * @author GustavoH
 */
public class ExtractFeatureResourceDependencies {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        //Get feature packages:
        File f = new File("./src/shef/mt/features/impl");
        File[] folders = f.listFiles();
        for (File fo : folders) {

            //Get package name:
            String folderName = fo.getName();
            if (!folderName.contains(".java")) {
                System.out.println("Feature package: " + folderName);
                File fpackage = fo.getAbsoluteFile();

                //Get features in feature package:
                File[] features = fpackage.listFiles();
                HashSet<String> allresources = new HashSet<>();
                HashMap<String, HashSet<String>> resourceToFile = new HashMap<>();
                for (File feature : features) {

                    //Obtain feature name:
                    String featureName = feature.getName();
                    if (featureName.contains(".java")) {

                        //Get feature's required resources:
                        featureName = featureName.substring(0, featureName.length() - 5);
                        Class c = Class.forName("shef.mt.features.impl." + folderName + "." + featureName);
                        Feature instance = (Feature) c.newInstance();
                        HashSet<String> resources = instance.getResources();

                        //Add files to map:
                        for(String resource: resources){
                            if(!resourceToFile.containsKey(resource)){
                                resourceToFile.put(resource, new HashSet<String>());
                            }
                            resourceToFile.get(resource).add(featureName);
                        }
                        
                        //Add required resources to package's required set:
                        allresources.addAll(resources);
                    }
                }
                //Print result:
                for (String resource : allresources) {
                    System.out.println("\tResource: " + resource);
                    for(String feature: resourceToFile.get(resource)){
                        System.out.println("\t\tFile: " + feature);
                    }
                }
                System.out.println("");
            }
        }
    }

}
