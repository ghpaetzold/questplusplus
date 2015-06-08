/**
 *
 */
package shef.mt.features.impl.doclevel;

import shef.mt.features.util.Doc;
import shef.mt.util.Pair;
import shef.mt.features.util.Sentence;
import shef.mt.features.impl.DocLevelFeature;
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
public class DocLevelFeature1098 extends DocLevelFeature {

    public DocLevelFeature1098() {
        setIndex(1098);
        setDescription("number of dependencies with aligned constituents normalized by the total number of dependencies (max between source and target)");
        this.addResource("postags");
        this.addResource("giza");

    }

    public void run(Sentence source, Sentence target) {
//	    System.out.println("FEATURE 85");

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

    @Override
    public void run(Doc source, Doc target) {
        
        int matches = 0;
        int depNoSource=0;
        int depNoTarget=0;
        for (int i=0;i<source.getSentences().size();i++){
            ArrayList<Pair> tdListSource = source.getSentence(i).getDependencies();
            ArrayList<Pair> tdListTarget = target.getSentence(i).getDependencies();

            if (tdListSource == null || tdListTarget == null) {
                setValue(0);
                return;
            }
            depNoSource += tdListSource.size();
            depNoTarget += tdListTarget.size();


            Iterator<Pair> itSource = tdListSource.iterator();
            Pair td;
            
            while (itSource.hasNext()) {
                td = itSource.next();
                if (findDependency(td, tdListTarget)) {
                    matches++;

                }
                //	System.out.println(td.gov().nodeString()+" -- "+td.dep().nodeString());
            }

            //    System.out.println("matches="+matches);
            
        }
        setValue((float) matches / Math.max(depNoSource, depNoTarget));
    }
}
