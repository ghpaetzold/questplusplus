package shef.mt.features.wce;
import java.util.*;
import java.io.*;
import java.lang.Exception;
public class CountWordOccurences
{
    //This class is for calculating the average number of occurences of words 
    //in a sentence
    public String sentence;
    public CountWordOccurences(String sentence)
    {
        this.sentence= sentence;
    }
    public float calculate()
    {
        ArrayList<String> wordList = new ArrayList<String>();
        int count=0;
        int sum = 0;
        int index= sentence.indexOf(" ");
        while (index >=0)
        {
           String word = sentence.substring(0,index);
           wordList.add(word);
           sentence = sentence.substring(index+1);
	   index= sentence.indexOf(" ");
           count++;
        }
        wordList.add(sentence);
        //if (stopWordList.contains(sentence.trim())) count++;  
        List<String> unique = new LinkedList<String>(wordList);
        for (String key : unique) 
        {
                sum += Collections.frequency(wordList, key);
        }
        HashSet set = new HashSet(wordList);
        return (float)sum/(count+1);
        
    }

public static void main(String[] file) throws Exception
{
    CountWordOccurences wo = new CountWordOccurences("I love you and you love me too .");
    System.out.println("The number is: "+wo.calculate());
       
}






}





