package shef.mt.features.impl.binqe;

import java.util.*;

import shef.mt.features.impl.Feature;
import shef.mt.features.util.Sentence;

public class BinQEFeature1001 extends Feature {

    public BinQEFeature1001() {
        setIndex(1001);
        setDescription("Number of words in common.");
    }

    public void run(Sentence source, Sentence target) {
        HashSet<String> hs = new HashSet<>();
        
        for(String token: source.getTokens()){
            hs.add(token);
        }
        
        for(String token: target.getTokens()){
            hs.add(token);
        }
        
        setValue(hs.size());
    }
}
