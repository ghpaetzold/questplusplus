package shef.mt.features.util;

import shef.mt.features.util.Sentence;
import shef.mt.util.Pair;
import java.util.*;


public class Paragraph {
	private Sentence[] sentences;
	private int index;

	public Paragraph(Sentence[] s, int index) {
		this.sentences=s;
		this.index = index;
    	}

	public int getIndex() {
        	return index;
    	}

	public Sentence[] getSentences(){
		return this.sentences;
	}

	public Sentence getSentence(int index){
		return this.sentences[index];
	} 
	
	public void setSentence(Sentence s, int index){
		this.sentences[index]=s;
	}

}
