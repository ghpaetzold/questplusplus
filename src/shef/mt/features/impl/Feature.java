package shef.mt.features.impl;

import shef.mt.tools.ResourceManager;
import shef.mt.features.util.Sentence;
import java.util.*;

/**
 * Feature is an abstract class which models a sentence feature. Typiclally, a
 * Feature consist of a value, a procedure for calculating the value and a set
 * of dependencies, i.e., resources that need to be available in order to be
 * able to compute the feature value. <br> Classes extending Feature will have
 * to provide their own method for computing the feature value by implementing
 *
 * Modified by José de Souza (desouza@fbk.eu)
 * - the value attribute was transformed into a HashMap<Integer, String>;
 * - the get*() and set*() methods were updated accordingly;
 * - each value in the hash corresponds to a different feature and the feature
 * counter is automatically incremented every time it is used. The counter starts
 * at 0. When a new value is added the counter is incremented and point to the
 * last inserted key.
 *
 * @see Feature.run
 */
public abstract class Feature {

    private Integer featureCounter = 0;
    private Map<Integer, String> values;
    private boolean computable;
    private String index;
    private String description;
    private HashSet<String> resources;

    public Feature() {
        this.values = new HashMap<Integer, String>();
        this.resources = new HashSet<String>();
    }

    /**
     * returns the value
     */
    public String getValue(Integer key) {
        return values.get(key);
    }

    /**
     * Sets a float feature value
     *
     * @param value the new value
     */
    public void setValue(float value) {
        this.featureCounter++;
        this.values.put(featureCounter, String.valueOf(value));
    }

    /**
     * Sets a String feature value
     *
     * @param value the new value
     */
    public void setValue(String value) {
        this.featureCounter++;
        this.values.put(featureCounter, value);
    }


    /*
     * returns a string representation of the Feature value
     */
    public String toString() {
        return this.values.toString();
    }

    /**
     * abstract method which should be overloaded in order to define the feature
     * value computation procedure
     *
     * @param source the source sentence
     * @param target the target sentence
     */
    public abstract void run(Sentence source, Sentence target);

    /**
     * check whether the Feature dependencies are registered and therefore the
     * feature value is computable returns true if the feature value is
     * computable and false otherwise
     */
    public boolean isComputable() {
        boolean result = true;
        if (getResources() == null || getResources().size() == 0) {
            return true;
        }
        Iterator<String> it = getResources().iterator();
        while (result && it.hasNext()) {
            result &= ResourceManager.isRegistered(it.next());
        }
        return result;
    }

    /**
     * sets the description
     *
     * @param d the new description
     */
    public void setDescription(String d) {
        description = d;
    }

    public String getDescription() {
        return description;
    }

    /**
     * returns the Feature index
     *
     * @return the index
     */
    public String getIndex() {
        return index;
    }

    /**
     *
     * @return the number of features returned by this feature class implementation.
     */
    public Integer getFeaturesNumber() {
        return this.featureCounter;
    }

    /**
     * sets the index
     *
     * @param index the new index as integer
     */
    public void setIndex(int index) {
        this.index = index + "";
    }

    /**
     * sets the index
     *
     * @param index the new index
     */
    public void setIndex(String index) {
        this.index = index;
    }

    /**
     * sets the resources (dependencies)
     *
     * @param resources a HashSet containing the resources this feature
     * computation depends on
     */
    public void setResources(HashSet<String> resources) {
        this.resources = resources;
    }

    public void addResource(String resource) {
        getResources().add(resource);
    }

    /**
     * Resets the internal state of the Feature object. The actual implementation
     * clears the values of the feature and reinitializes the feature counter.
     *
     * @author José de Souza (desouza@fbk.eu)
     */
    public void reset() {
        // if the number of items in the hash is too high,
        // consider using a new instance of HashMap instead of using clear()
        // José de Souza
        this.values.clear();
        this.featureCounter = 0;
    }

    /**
     * @return the resources
     */
    public HashSet<String> getResources() {
        return resources;
    }
}
