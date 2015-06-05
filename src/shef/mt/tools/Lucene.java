package shef.mt.tools;

import shef.mt.features.util.Sentence;
import shef.mt.util.Logger;
import shef.mt.util.Pair;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;




// IndexWriter
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory; 
import org.apache.lucene.util.Version;

// IndexReader
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.ScoreDoc;

import shef.mt.util.BleuMeasurer;
import shef.mt.util.F1Measurer;

/**
 * This class indexes a corpus and searches it for specific queries and returns the results together with a similarity score. 
 * 
 * @author Ergun Bicici
 */
public class Lucene extends Resource {
	
	private String indexDirectory;
	private String suffix;
	private String selectedpart;
	private boolean docPerLine;
	private boolean returnDistinct;
	private BleuMeasurer bm;
	private F1Measurer f1m;
	
	public Lucene() {
		super(null);
	}
	
	/**
	 * Intializes a Lucene index.
	 * 
	 * @param luceneIndexDirectory
	 * @param dataDirPath
	 * @param part: Either Source or Target
	 * @throws Exception
	 */
	public Lucene(String luceneIndexDirectory, String dataPath, boolean mydocPerLine, boolean myreturnDistinct, String part) throws Exception {
		super(null);
		indexDirectory = luceneIndexDirectory;
		Logger.log("Initiating Lucene index in: " + indexDirectory);
		Directory directory = FSDirectory.open(new File(indexDirectory));
		suffix = "";
		docPerLine = mydocPerLine;
		returnDistinct = myreturnDistinct;
		selectedpart = part;
		bm = new BleuMeasurer();
		f1m = new F1Measurer();
		if (!IndexReader.indexExists(directory)) {
			Logger.log("Indexing: " + dataPath + " as " + selectedpart);
			StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);
			IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_36, analyzer);
			IndexWriter indexWriter = new IndexWriter(directory, conf);
			
			int numIndex = index(indexWriter, new File(dataPath));
	
			if (docPerLine)
				System.out.println("Number of lines indexed: " + numIndex);
			else
				System.out.println("Number of files indexed: " + numIndex);
		}
		ResourceManager.registerResource("LuceneIndex"+selectedpart);
	}
	
	private int index(IndexWriter indexWriter, File dataDir) throws Exception {
		if (dataDir.isDirectory())
			indexDirectory(indexWriter, dataDir);
		else
			indexFile(indexWriter, dataDir);
		int numIndexed = indexWriter.maxDoc();
		indexWriter.close();
		return numIndexed;
	}
	
	private void indexDirectory(IndexWriter indexWriter, File dataDir) throws IOException {
		File[] files = dataDir.listFiles();
		for (int i = 0; i < files.length; i++) {
			File f = files[i];
			if (f.isDirectory()) {
				indexDirectory(indexWriter, f);
			}
			else {
				indexFile(indexWriter, f);
			}
		}
	}
	
	private void indexFile(IndexWriter indexWriter, File f) throws IOException {
		if (f.isHidden() || f.isDirectory() || !f.canRead() || !f.exists()) {
			return;
		}
		if (suffix!=null && !f.getName().endsWith(suffix)) {
			return;
		}
		System.out.println("Indexing: " + f.getCanonicalPath());

		if (! docPerLine) {
			Document doc = new Document();
			doc.add(new Field("contents", new FileReader(f)));        
			doc.add(new Field("filename", f.getCanonicalPath(), 
					Field.Store.YES, Field.Index.ANALYZED));
			indexWriter.addDocument(doc);
		}
		else {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "utf-8"));
			String line = br.readLine();
			int linenum = 0;
			while (line != null) {
				Document doc = new Document();
				doc.add(new Field("contents", line, Field.Store.YES, Field.Index.ANALYZED));
				doc.add(new Field("filename", String.valueOf(linenum), Field.Store.YES, Field.Index.ANALYZED));
				indexWriter.addDocument(doc);            	
				line = br.readLine();
				linenum += 1;
			}
		}
	}
	
	public List<Map.Entry<Float,String>> search(String queryStr, int maxHits, boolean distinct) throws Exception {
		Directory directory = FSDirectory.open(new File(indexDirectory));
		IndexReader ireader = IndexReader.open(directory);
		IndexSearcher isearcher = new IndexSearcher(ireader);
		// Score is calculated according to: http://lucene.apache.org/core/3_6_1/api/core/org/apache/lucene/search/Similarity.html
		QueryParser parser = new QueryParser(Version.LUCENE_36, "contents", new StandardAnalyzer(Version.LUCENE_36));
		Query query = parser.parse(QueryParser.escape(queryStr));
		TopDocs topDocs = isearcher.search(query, maxHits*10); // We search for more to allow distinct entries
		ScoreDoc[] hits = topDocs.scoreDocs;
		if (distinct) {
			ArrayList<ScoreDoc> distincthits = new ArrayList<ScoreDoc>();
			HashMap<String, Integer> found = new HashMap<String, Integer>();
			HashMap<ScoreDoc, Integer> foundDocs = new HashMap<ScoreDoc, Integer>();
			String content = "";
			boolean enoughFound = false;
			for (ScoreDoc hit: hits) {
				content = isearcher.doc(hit.doc).get("contents");
				if (found.containsKey(content))
					continue;
				else {
					found.put(content, 1);
					distincthits.add(hit);
					foundDocs.put(hit, 1);
				}
				if (distincthits.size() >= maxHits) {
					enoughFound = true;
					break;
				}
			}
			if (!enoughFound) {
				int index = 0;
				for (ScoreDoc hit: hits) {
					if (! foundDocs.containsKey(hit)) {
						distincthits.add(index, hit);
						index += 1;
					}
					if (distincthits.size() >= maxHits)
						break;
				}
			}
			assert (distincthits.size() == hits.length);
			ScoreDoc[] newhits = new ScoreDoc[Math.min(maxHits, hits.length)];
			for (int i=0; i<distincthits.size(); i++) {
				newhits[i] = distincthits.get(i);
			}
			hits = newhits;
		}
		// Iterate through the results
		List<java.util.Map.Entry<Float,String>> pairList= new java.util.ArrayList<>();
//		<float,String>[] results = new <float,String>[hits.length];
		String result = "";
		for (int i = 0; i < hits.length; i++) {
			int docId = hits[i].doc;
			Document hitdoc = isearcher.doc(docId);
			float score = hits[i].score;
			String str;
			if (docPerLine) {
				result = String.valueOf(score) + " ||| " + hitdoc.get("contents");
				str = hitdoc.get("contents");
			}
			else {
				result = String.valueOf(score) + " ||| " + hitdoc.get("filename");
				str = hitdoc.get("filename");
			}
			System.out.println(result);
			Map.Entry<Float,String> pair = new java.util.AbstractMap.SimpleEntry<>(score,str);
			pairList.add(pair);
//			results[i] = result;
		}
		
//		System.out.println("Found " + hits.length);
		isearcher.close();
		return pairList;
	}
	
    public void processNextSentence(Sentence sent) throws Exception {
    	// We evaluate the top 5 entries returned:
		List<Map.Entry<Float,String>> results = search(sent.getText(), 5, returnDistinct);
		for (int i=0; i < results.size(); i++) {
			sent.setValue("IRscore"+selectedpart+String.valueOf(i), results.get(i).getKey());
			if (docPerLine) {
				sent.setValue("BLEUscore"+selectedpart+String.valueOf(i), (float) bm.calcSentenceBLEU(sent.getTokens(), results.get(i).getValue()));
				sent.setValue("F1score"+selectedpart+String.valueOf(i), (float) f1m.calcSentenceF1(sent.getTokens(), results.get(i).getValue()));
			}
		}
    }
	
	public void print() {
		System.out.println("Index directory: " + indexDirectory);
		System.out.println("Part: " + selectedpart);
		System.out.println("Suffix: " + suffix);
	}

}
