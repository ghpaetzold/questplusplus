package shef.mt.features.util;

import java.util.Comparator;

import shef.mt.features.impl.Feature;

/**
 * A Comparator for Feature objects. Features are compared according to their
 * index
 *
 * @author Catalina Hallett
 *
 */
public class FeatureComparator implements Comparator<Feature> {

    @Override
    public int compare(Feature arg0, Feature arg1) {
        // TODO Auto-generated method stub
        return arg0.getIndex().compareTo(arg1.getIndex());
    }
}
