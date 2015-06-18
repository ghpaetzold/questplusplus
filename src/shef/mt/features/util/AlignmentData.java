package shef.mt.features.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class AlignmentData {

    private final HashMap<Integer, HashSet<Integer>> alignments;
    private final HashSet<Integer> sourceAligned;
    private final HashSet<Integer> targetAligned;
    private final HashSet<Integer[]> alignedBlocks;
    private final HashSet<Integer> targetUnaligned;
    private final HashSet<Integer[]> unalignedBlocks;
    private final Sentence target;

    public AlignmentData(HashMap<Integer, HashSet<Integer>> alignments, HashSet<Integer> sourceAligned, HashSet<Integer> targetAligned, Sentence target) {
        this.alignments = alignments;
        this.sourceAligned = sourceAligned;
        this.targetAligned = targetAligned;
        this.target = target;
        this.alignedBlocks = this.getBlockAlignments();
        this.targetUnaligned = this.getUnalignedWords();
        this.unalignedBlocks = this.getBlockUnalignments();
    }

    private HashSet<Integer[]> getBlockAlignments() {
        //Create resulting set:
        HashSet<Integer[]> result = new HashSet<>();

        //Sort indexes:
        ArrayList<Integer> list = new ArrayList<>();
        Iterator<Integer> iter = this.targetAligned.iterator();
        while (iter.hasNext()) {
            Integer index = iter.next();
            list.add(index);
        }
        Collections.sort(list);

        //Identify blocks:
        Integer first = list.get(0);
        Integer last = first - 1;
        for (int i = 0; i < list.size(); i++) {
            Integer curr = list.get(i);
            if (last == curr - 1) {
                last = curr;
            } else {
                if (last - first > 0) {
                    Integer[] block = new Integer[2];
                    block[0] = first;
                    block[1] = last;
                    result.add(block);
                }
                first = curr;
                last = curr;
            }
        }
        if (last - first > 0) {
            Integer[] block = new Integer[2];
            block[0] = first;
            block[1] = last;
            result.add(block);
        }

        //Return blocks found:
        return result;
    }

    private HashSet<Integer[]> getBlockUnalignments() {
        //Create resulting set:
        HashSet<Integer[]> result = new HashSet<>();

        //Sort indexes:
        ArrayList<Integer> list = new ArrayList<>();
        Iterator<Integer> iter = this.targetUnaligned.iterator();
        while (iter.hasNext()) {
            Integer index = iter.next();
            list.add(index);
        }
        Collections.sort(list);

        if (list.isEmpty()) {
            return result;
        }

        //Identify blocks:
        Integer first = list.get(0);
        Integer last = first - 1;
        for (Integer curr : list) {
            if (last == curr - 1) {
                last = curr;
            } else {
                if (last - first > 0) {
                    Integer[] block = new Integer[2];
                    block[0] = first;
                    block[1] = last;
                    result.add(block);
                }
                first = curr;
                last = curr;
            }
        }
        if (last - first > 0) {
            Integer[] block = new Integer[2];
            block[0] = first;
            block[1] = last;
            result.add(block);
        }
        
        //Return blocks found:
        return result;
    }

    private HashSet<Integer> getUnalignedWords() {
        //Create resulting set:
        HashSet<Integer> result = new HashSet<>();

        //Get target tokens:
        String[] tokens = this.target.getTokens();

        //Get unaligned words:
        for (int i = 0; i < this.target.getNoTokens(); i++) {
            Integer index = i;
            if (!this.targetAligned.contains(index)) {
                result.add(index);
            }
        }

        //Return unaligned words:
        return result;
    }

    /**
     * @return the alignments
     */
    public HashMap<Integer, HashSet<Integer>> getAlignments() {
        return alignments;
    }

    /**
     * @return the sourceAligned
     */
    public HashSet<Integer> getSourceAligned() {
        return sourceAligned;
    }

    /**
     * @return the targetAligned
     */
    public HashSet<Integer> getTargetAligned() {
        return targetAligned;
    }

    /**
     * @return the alignedBlocks
     */
    public HashSet<Integer[]> getAlignedBlocks() {
        return alignedBlocks;
    }

    /**
     * @return the targetUnaligned
     */
    public HashSet<Integer> getTargetUnaligned() {
        return targetUnaligned;
    }

    /**
     * @return the unalignedBlocks
     */
    public HashSet<Integer[]> getUnalignedBlocks() {
        return unalignedBlocks;
    }
}
