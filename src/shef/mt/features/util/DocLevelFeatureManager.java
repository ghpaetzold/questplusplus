package shef.mt.features.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import shef.mt.features.impl.DocLevelFeature;
import shef.mt.features.impl.Feature;
import shef.mt.util.Logger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Pattern;


public class DocLevelFeatureManager extends FeatureManager{

    public DocLevelFeatureManager(String featureArgs, String featureFile) {
        super(featureArgs, featureFile);
    }

    public DocLevelFeatureManager(String featureArgs) {
        super(featureArgs);
    }

    public String runFeatures(Doc source, Doc target) {
        StringBuffer result = new StringBuffer();
        Set<String> fIndeces = features.keySet();

        ArrayList<String> featureIndeces = new ArrayList<String>(fIndeces);

        Collections.sort(featureIndeces);
//		System.out.println(featureIndeces.size()+" feature indeces: "+ featureIndeces);
        Iterator<String> it = featureIndeces.iterator();
        Feature f;
        while (it.hasNext()) {
            String index = it.next();
            f = features.get(index);
            //System.out.println(index);

            // Modified by José de Souza
            // every new sentence pair has new features
            // therefore, the feature object state must be reset
            f.reset();

            if (f.isComputable()) {
                DocLevelFeature f_doc=(DocLevelFeature) f;
                f_doc.run(source, target);
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
            String index = it.next();
            System.out.print(index + "\t");
        }
        System.out.println();
    }

}
