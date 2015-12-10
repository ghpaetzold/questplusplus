package shef.mt.features.util;

import shef.mt.features.util.Sentence;
import shef.mt.features.util.Paragraph;
import shef.mt.util.Pair;
import java.util.*;


public class Doc {
	private Paragraph[] paragraphs;
        private ArrayList<Sentence> sentences = new ArrayList<>();
	private int index;
        private HashMap<String, Object> values;
        private String document;

	public Doc(Paragraph[] p, int index) {
		this.paragraphs=p;
		this.index = index;
                this.values = new HashMap<String, Object>();
    	}
        public Doc(ArrayList<Sentence> s, int index) {
		this.sentences=s;
		this.index = index;
                this.values = new HashMap<String, Object>();
    	}
        public void setValue(String key, Object value) {
            values.put(key, value);
        }

	public int getIndex() {
        	return index;
    	}

	public Paragraph[] getParagraphs(){
		return this.paragraphs;
	}

	public Paragraph getParagraph(int index){
		return this.paragraphs[index];
	}
        
        public ArrayList<Sentence> getSentences(){
		return this.sentences;
	}

	public Sentence getSentence(int index){
		return sentences.get(index);
	}

	public void setParagraph(Paragraph p, int index){
		this.paragraphs[index]=p;
	}
	public Object getValue(String key) {
           Object value = values.get(key);
           if (value == null) {
             return Float.NaN;
           }
           return values.get(key);
        }
        
        public String getText() {
            document="";
            for (int i=0; i<sentences.size();i++){
                document=document+sentences.get(i).getText()+"\n";
            }
            System.out.println(document);
            return document;
        }
}
