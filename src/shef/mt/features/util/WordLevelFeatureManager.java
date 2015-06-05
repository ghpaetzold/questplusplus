package shef.mt.features.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import shef.mt.features.impl.WordLevelFeature;
import shef.mt.util.Logger;

public class WordLevelFeatureManager extends FeatureManager {

    public WordLevelFeatureManager(String featureArgs, String featureFile) {
        super(featureArgs, featureFile);
    }

    public WordLevelFeatureManager(String featureArgs) {
        super(featureArgs);
    }

    @Override
    public String runFeatures(Sentence source, Sentence target) {
        //Initialize result:
        String result = "";

        //Get features:
        Set<String> fIndeces = features.keySet();
        ArrayList<String> featureIndeces = new ArrayList<String>(fIndeces);
        Collections.sort(featureIndeces);
        Iterator<String> it = featureIndeces.iterator();
        WordLevelFeature f;

        //Create vector for output:
        String[] output = new String[target.getNoTokens()];
        for (int i = 0; i < output.length; i++) {
            output[i] = "";
        }
        while (it.hasNext()) {

            //Get next feature:
            String index = it.next();
            f = (WordLevelFeature) features.get(index);
            f.reset();

            //Calculate value for each target word in translation:
            if (f.isComputable()) {
                try {
                    f.run(source, target);
                    String[] values = f.getValues();
                    for (int i = 0; i < values.length; i++) {
                        output[i] += values[i] + '\t';
                    }
                } catch (Exception e) {
                    System.out.println("ERROR: Failed to run feature " + f.getIndex() + ". Feature omitted.");
                }

            } else {
                Logger.log("Feature " + f.getIndex() + " cannot run because some of its dependencies are missing.");
                System.out.println("Feature " + f.getIndex() + " cannot run because some of its dependencies are missing.");
                features.remove(index);
            }
        }

        //Create output:
        for (int i = 0; i < output.length; i++) {
            result += output[i] + '\n';
        }

        //Return output:
        return result;
    }

}
