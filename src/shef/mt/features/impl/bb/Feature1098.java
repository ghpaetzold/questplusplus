/**
 *
 */
package shef.mt.features.impl.bb;

import shef.mt.util.Pair;
import shef.mt.features.util.Sentence;
import shef.mt.features.impl.Feature;
import java.util.*;
import shef.mt.tools.WordAlign;
/**
 * @author Catalina Hallett
 *
 *
 * Dependency feature: number of matched dependencies divided by total number of
 * dependencies (max between source and target)
 */
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.process.*;

/**
 * number of dependencies with aligned constituents normalized by the total
 * number of dependencies (max between source and target)
 *
 * @author Catalina Hallett
 *
 *
 */
public class Feature1098 extends Feature {

    public Feature1098() {
        setIndex(1098);
        setDescription("number of dependencies with aligned constituents normalized by the total number of dependencies (max between source and target)");
        this.addResource("stf");
        this.addResource("gizaAlign");
    }

    public void run(Sentence source, Sentence target) {
//	    System.out.println("FEATURE 85");
        ArrayList<Pair> tdListSource = source.getDependencies();
        ArrayList<Pair> tdListTarget = target.getDependencies();

        if (tdListSource == null || tdListTarget == null) {
            setValue(0);
            return;
        }
        int depNoSource = tdListSource.size();
        int depNoTarget = tdListTarget.size();


        Iterator<Pair> itSource = tdListSource.iterator();
        Pair td;
        int matches = 0;
        while (itSource.hasNext()) {
            td = itSource.next();
            if (findDependency(td, tdListTarget)) {
                matches++;

            }
            //	System.out.println(td.gov().nodeString()+" -- "+td.dep().nodeString());
        }

        //    System.out.println("matches="+matches);
        setValue((float) matches / Math.max(depNoSource, depNoTarget));
    }

    /**
     * Checks whether a given pair of source language words in a dependency
     * relation have aligned correspondents in a list of target dependencies
     *
     * @param pair
     * @param tdList
     * @return true if a matching dependency was found, false otherwise
     */
    public boolean findDependency(Pair pair, ArrayList<Pair> tdList) {
        //	System.out.println("Finding matching dependency for: "+pair);
        if (tdList == null) {
            return false;
        }
        Iterator<Pair> it = tdList.iterator();
        Pair td;
        String govMap = WordAlign.getMapping(pair.getKey());
        String depMap = WordAlign.getMapping(pair.getValue());
        if (govMap == null || depMap == null) {
            return false;
        }

        while (it.hasNext()) {
            td = it.next();
            if (govMap.equals(td.getKey())
                    && depMap.equals(td.getValue())) {
                return true;
            }
        }
//		System.out.println("match NOT found");
        return false;
    }
}
