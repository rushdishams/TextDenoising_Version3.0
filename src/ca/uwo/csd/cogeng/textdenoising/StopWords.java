package ca.uwo.csd.cogeng.textdenoising;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * This class represents the stop words/function words. The stop words reside in a ASCII text file.
 * The methods in this class reads stop words, and when a sentence is sent, returns the number of
 * stop words present in that sentence. One method also returns the sentence by excluding the stop words from it.
 * @author Rushdi Shams
 * @version 3.0 (Major optimization applied January 26 2015)
 *
 */

public class StopWords {
	//Instance variables
	private String stopWordFile = ""; // name of the stopword file
	private Hashtable<String, String> stopWord; //Hashtable to hold the stop words
	private int count = 0; //number of stopwords found in a given sentence
	private ArrayList <String> noStopWordArray;
	
	/**
	 * Constructor: takes the file containing stopwords
	 * @param fileName is the name of the stop words file
	 */
	public StopWords (String fileName){
		this.stopWordFile = fileName;
		FileReader stopWordFile = null;
		try {
			stopWordFile = new FileReader(this.stopWordFile);
		} catch (FileNotFoundException e) {
			System.out.println("Problem opening stop word file");
			e.printStackTrace();
		}
		BufferedReader br = new BufferedReader(stopWordFile);
		String entries;
		stopWord = new Hashtable<String, String>();
		try {
			while ((entries = br.readLine()) != null){
				stopWord.put(entries.toLowerCase(),"");
			}
		} catch (IOException e) {
			System.out.println("Problem reading stop word file");
			e.printStackTrace();
		}
		try {
			br.close();
		} catch (IOException e) {
			System.out.println("Problem closing buffered reader to read stop word file");
			e.printStackTrace();
		}
	}//end method
	/**
	 * Method to count number of stopwords in a sentence (the sentence comes in as an arraylist of words)
	 * @param wordArray is the words in a sentence
	 */
	public void dealStopWord(ArrayList <String> wordArray){
		this.count = 0;
		String temp = "";
		int size = wordArray.size();
		noStopWordArray = new ArrayList <String> ();
		boolean found;
		for(int i = 0; i < size; i++){
			temp = wordArray.get(i).toLowerCase();
			found = stopWord.containsKey(temp);
			if(found){
				this.count++;
			}
			else{
				this.noStopWordArray.add(wordArray.get(i));
			}
		}//end for
	}//end method
	/**
	 * Method to return only the content words of a sentence
	 * @return the content words of a sentence
	 */
	public ArrayList <String> getNoStopWordArray(){
		return this.noStopWordArray;
	}//end method
	/**
	 * Method to return # of function words in a sentence
	 * @return # of function words in a sentence
	 */
	public int getStopWordCount(){
		return this.count;
	}//end method
	
	public static void main(String[] args) throws IOException{
		StopWords a = new StopWords("languageresources/stopword.txt");
	//	a.setStopWordFile();
		ArrayList<String> array = new ArrayList<String>();
		array.add("Thyroid");
		array.add("is");
		array.add("a");
		array.add("Simple");
		array.add("test");
		//array.add("alternate");
		
		array.add("a");
		
		a.dealStopWord(array);
		System.out.println(a.getStopWordCount());
		ArrayList<String> noStopWord = new ArrayList<String>();
		noStopWord = a.getNoStopWordArray();
		System.out.println(noStopWord.size());
		for (String s : noStopWord)
			System.out.println(s);
		
	}

}//public class StopWords