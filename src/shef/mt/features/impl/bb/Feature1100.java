/**
 *
 */
package shef.mt.features.impl.bb;

import shef.mt.util.Pair;
import shef.mt.features.util.Sentence;
import shef.mt.features.impl.Feature;
import java.util.*;
import shef.mt.tools.Giza;
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
 * number of dependencies with well aligned constituents (Giza threshold 0.1)
 * normalized by the total number of dependencies (max between source and
 * target)
 *
 * @author Catalina Hallett
 *
 *
 */
public class Feature1100 extends Feature {

    private static float thresh = 0.1f;

    public Feature1100() {
        setIndex(1100);
        setDescription("number of dependencies with well aligned constituents (Giza threshold 0.1) normalized by the total number of dependencies (max between source and target)");
        this.addResource("stf");
        this.addResource("giza");
    }

    public void run(Sentence source, Sentence target) {
        //	System.out.println("FEATURE 87");
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

//	    System.out.println("matches="+matches+" out of "+depNoSource+" "+depNoTarget);
        setValue((float) matches / Math.max(depNoSource, depNoTarget));
    }

    /**
     *
     * @param pair
     * @param tdList
     * @return true if a matching dependency was found, false otherwise
     */
    public boolean findDependency(Pair pair, ArrayList<Pair> tdList) {
        if (tdList == null) {
            return false;
        }
        Iterator<Pair> it = tdList.iterator();
        Pair td;
//	    System.out.println("looking for matching dependency for "+pair);
        HashSet<String> govTrans = Giza.getTranslations(pair.getKey(), thresh);
        HashSet<String> depTrans = Giza.getTranslations(pair.getValue(), thresh);
//	    System.out.println(govTrans);
//	    System.out.println(depTrans);
        if (govTrans.isEmpty() || depTrans.isEmpty()) {
            return false;
        }

        while (it.hasNext()) {
            td = it.next();
            //		System.out.println(td);
            if (govTrans.contains(td.getKey()) && depTrans.contains(td.getValue())) {
//				System.out.println("found match: "+td);
                return true;
            }
        }
        return false;
    }
}
