package ca.uwo.csd.cogeng.textdenoising;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * This class represents the biomedical verbs. The verbs reside in a ASCII text file.
 * The methods in this class reads verbs, and when a sentence is sent, returns the number of
 * biomedical verbs present in that sentence.
 * @author Rushdi Shams
 * @version 3.0 (Major optimization applied January 26 2015)
 *
 */

public class Verbs {
	
	//Instance variables
	private String verbFile = ""; // name of the verb file
	private Hashtable<String, String> verb; //Hash table to hold all the verbs in verb file
	private int count = 0; //number of verbs found in a given sentence
	
	//Constructor: takes the file containing verbs
	/**
	 * Constructor: takes the file containing verbs
	 * @param fileName is the name of the file containing biomedical verbs
	 */
	public Verbs (String fileName){		
		this.verbFile = fileName;
		FileReader verbFile = null;
		try {
			verbFile = new FileReader(this.verbFile);
		} catch (FileNotFoundException e) {
			System.out.println("Problem opening verb file");
			e.printStackTrace();
		}
		BufferedReader br = new BufferedReader(verbFile);
		String entries;
		verb = new Hashtable<String, String>();
		try {
			while ((entries = br.readLine()) != null){
				verb.put(entries.toLowerCase(),"");
			}
		} catch (IOException e) {
			System.out.println("Problem reading verb file");
			e.printStackTrace();
		}
		try {
			br.close();
		} catch (IOException e) {
			System.out.println("Problem closing buffered reader to read verb file");
			e.printStackTrace();
		}
	}//end method
	
	/**
	 * return number of verbs in a sentence (the sentence comes in as an arraylist of words)
	 * @param wordArray is the words of a sentence
	 * @return number of verbs in a sentence (the sentence comes in as an arraylist of words)
	 */
	public int verbCount(ArrayList <String> wordArray){
		this.count = 0;
		String temp = "";
		int size = wordArray.size();
		boolean found;
		for(int i = 0; i < size; i++){
			temp = wordArray.get(i).toLowerCase();
			found = verb.containsKey(temp);
			if(found){
				this.count++;
			}
		}//end loop
		return this.count;
	}//end method
}//public class Verbs