
package shef.mt.features.impl;

/**
 * Variation of the Feature class, which is suited for word-level features.
 * @author GustavoH
 */
public abstract class WordLevelFeature extends Feature{
    
    private String identifier;
    private String[] values;

    public WordLevelFeature() {
        super();
    }

    public void setValues(String[] values) {
        this.values = values;
    }
    
    public String[] getValues(){
        return this.values;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
}
