package shef.mt.features.impl.binqe;

import shef.mt.features.impl.bb.*;
import java.util.*;

import shef.mt.features.impl.Feature;
import shef.mt.features.util.Sentence;

public class BinQEFeature1003 extends Feature {

    public BinQEFeature1003() {
        setIndex(1003);
        setDescription("Number of words in common normalized by target sentence size.");
    }

    public void run(Sentence source, Sentence target) {
        HashSet<String> hs = new HashSet<>();
        
        for(String token: source.getTokens()){
            hs.add(token);
        }
        
        for(String token: target.getTokens()){
            hs.add(token);
        }
        
        setValue((float)hs.size()/(float)target.getNoTokens());
    }
}
