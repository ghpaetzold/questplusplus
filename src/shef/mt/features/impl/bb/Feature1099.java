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
 * number of dependencies (max between source and target), with the order of the
 * constituents ignored
 *
 * @author Catalina Hallett
 *
 */
public class Feature1099 extends Feature {

    public Feature1099() {
        setIndex(1099);
        setDescription("number of dependencies with aligned constituents normalized by the total number of dependencies (max between source and target), with the order of the constituents ignored");
        this.addResource("stf");
        this.addResource("gizaAlign");
    }

    public void run(Sentence source, Sentence target) {
        //	System.out.println("FEATURE 86");
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

//	    System.out.println("matches="+matches);
        setValue((float) matches / Math.max(depNoSource, depNoTarget));
    }

    /**
     *
     * @param gov
     * @param dep
     * @param tdList
     * @return true if a matching dependency was found, false otherwise
     */
    public boolean findDependency(Pair pair, ArrayList<Pair> tdList) {
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
            if ((td.equalsKey(govMap) && td.equalsValue(depMap)) || (td.equalsKey(depMap) && td.equalsValue(govMap))) {
                return true;
            }
        }
        return false;
    }
}
