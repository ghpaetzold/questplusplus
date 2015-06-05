/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package shef.mt.tools;
import shef.mt.features.util.*;

/**
 *
 * @author David Langlois
 */


//for dealing with Processor requiring source and target sentences to compute the score
public abstract class ResourceProcessorTwoSentences extends ResourceProcessor {
    public abstract void processNextParallelSentences(Sentence source,Sentence target);
}
