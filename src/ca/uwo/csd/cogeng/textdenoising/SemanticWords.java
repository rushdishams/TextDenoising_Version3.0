package ca.uwo.csd.cogeng.textdenoising;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
/**
 * This class represents the words semantically connected to biomedical domain. The semantic words 
 * reside in a ASCII text file.
 * The methods in this class reads semantic words, and when a sentence is sent, returns the number of
 * semantic words present in that sentence.
 * @author Rushdi Shams
 * @version 3.0 (Major optimization applied January 26 2015)
 *
 */

public class SemanticWords {
	
	//Instance variables
	private String semWordFile = ""; // name of the semantic word file
	private Hashtable<String, String> semWord; //Hashtable to hold all the semantic words
	private int count = 0; //number of semantic words found in a given sentence
	
	//Constructor: takes the file containing semantic words
	/**
	 * Constructor: takes the file containing semantic words
	 * @param fileName is the name of the file containing semantic words
	 */
	public SemanticWords (String fileName){	
		this.semWordFile = fileName;
		FileReader semWordFile = null;
		try {
			semWordFile = new FileReader(this.semWordFile);
		} catch (FileNotFoundException e) {
			System.out.println("Problem opening semantic word file");
			e.printStackTrace();
		}
		BufferedReader br = new BufferedReader(semWordFile);
		String entries;
		semWord = new Hashtable<String, String>();
		try {
			while ((entries = br.readLine()) != null){
				semWord.put(entries.toLowerCase(),"");
			}
		} catch (IOException e) {
			System.out.println("Problem reading semantic word file");
			e.printStackTrace();
		}
		try {
			br.close();
		} catch (IOException e) {
			System.out.println("Problem closing buffered reader to read semantic word file");
			e.printStackTrace();
		}
	}//end method
	/**
	 * return number of semantic words in a sentence (the sentence comes in as an arraylist of words)
	 * @param wordArray is the words of a sentence
	 * @return number of semantic words in a sentence (the sentence comes in as an arraylist of words)
	 */
	public int semWordCount(ArrayList <String> wordArray){
		this.count = 0;
		String temp = "";
		int size = wordArray.size();
		boolean found;
		for(int i = 0; i < size; i++){
			temp = wordArray.get(i).toLowerCase();
			found = semWord.containsKey(temp);
			if(found){
				this.count++;
			}
		}//end loop
		return this.count;
	}//end method
}//public class SemanticWords