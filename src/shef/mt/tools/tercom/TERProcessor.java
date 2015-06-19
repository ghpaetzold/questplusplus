package shef.mt.tools.tercom;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import shef.mt.features.util.Doc;
import shef.mt.features.util.Sentence;
import shef.mt.tools.ResourceProcessor;

public class TERProcessor extends ResourceProcessor {

    private int sentenceCounter;
    private final LinkedHashMap hypsegs;
    private final LinkedHashMap refsegs;
    private final TERcost costfunc;

    public TERProcessor(String sourceFile, String targetFile) {
        // Initialize counter:
        this.sentenceCounter = 0;
        
        // Load inputs:
        this.hypsegs = this.load_segs(targetFile);
        this.refsegs = this.load_segs(sourceFile);

        // Set TER options:
        boolean normalized = true;
        boolean caseon = true;
        boolean nopunct = true;
        int beam_width = 20;
        String reflen_fn = "";
        String span_pfx = "";
        int shift_dist = 50;
        this.costfunc = new TERcost();
        this.costfunc._delete_cost = 1.0;
        this.costfunc._insert_cost = 1.0;
        this.costfunc._shift_cost = 1.0;
        this.costfunc._match_cost = 0.0;
        this.costfunc._substitute_cost = 1.0;
        TERcalc.setNormalize(normalized);
        TERcalc.setCase(caseon);
        TERcalc.setPunct(nopunct);
        TERcalc.setBeamWidth(beam_width);
        TERcalc.setShiftDist(shift_dist);
    }

    public TERalignment score_all_refs(String hyp,
            List refs,
            List reflens,
            List refids,
            String refspan,
            String hypspan,
            TERcost costfunc) {
        double totwords = 0;
        String ref;
        String refid = "";
        String bestref = "";
        String reflen = "";

        TERalignment bestresult = null;

        TERcalc.setRefLen(reflens);
        /* For each reference, compute the TER */
        for (int i = 0; i < refs.size(); ++i) {
            ref = (String) refs.get(i);
            if (!refids.isEmpty()) {
                refid = (String) refids.get(i);
            }

            TERalignment result = TERcalc.TER(hyp, ref, costfunc);

            if ((bestresult == null) || (bestresult.numEdits > result.numEdits)) {
                bestresult = result;
                if (!refids.isEmpty()) {
                    bestref = refid;
                }
            }

            totwords += result.numWords;
        }
        bestresult.numWords = ((double) totwords) / ((double) refs.size());
        if (!refids.isEmpty()) {
            bestresult.bestRef = bestref;
        }
        return bestresult;
    }

    public LinkedHashMap load_segs(String fn) {
        BufferedReader stream;
        LinkedHashMap segs = new LinkedHashMap();

        try {
            stream = new BufferedReader(new FileReader(fn));
        } catch (FileNotFoundException ioe) {
            System.out.println(ioe);
            return null;
        }

        try {
            String line;
            int index = -1;
            while ((line = stream.readLine()) != null) {
                index++;
                String text = line.trim();
                String id = "" + index;
                ArrayList al = new ArrayList();
                al.add(text);
                segs.put(id, al);
            }
        } catch (IOException ioe) {
            System.out.println(ioe);
            return null;
        }
        return segs;
    }

    @Override
    public void processNextSentence(Sentence source) {
        String id = "" + this.sentenceCounter;
        ArrayList hyps = (ArrayList) this.hypsegs.get(id);

        TERalignment result = score_all_refs((String) hyps.get(0),
                (ArrayList) this.refsegs.get(id), null, new ArrayList(1), "", "", this.costfunc);

        this.sentenceCounter++;
        
        source.setValue("teralignment", result);
    }

    @Override
    public void processNextDocument(Doc source) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
