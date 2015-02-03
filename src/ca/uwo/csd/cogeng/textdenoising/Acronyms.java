package ca.uwo.csd.cogeng.textdenoising;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
/**
 * This class represents the biomedical acronyms. The acronyms reside in a ASCII text file.
 * The methods in this class reads acronyms, and when a sentence is sent, returns the number of
 * acronyms present in that sentence.
 * @author Rushdi Shams
 * @version 3.0 (Major optimization applied January 26 2015)
 *
 */

public class Acronyms {
	
	//Instance variables
	private String acronymFile = ""; // name of the verb file
	private int count = 0; //number of acronyms found in a given sentence
	private Hashtable<String, String> acronym;
	
	/**
	 * Constructor: takes the file containing verbs
	 * @param fileName is the name of the file that contains biomedical acronyms
	 */
	public Acronyms (String fileName){
		this.acronymFile = fileName;
		FileReader acronymFile = null;
		try {
			acronymFile = new FileReader(this.acronymFile);
		} catch (FileNotFoundException e) {
			System.out.println("Problem opening semantic word file");
			e.printStackTrace();
		}
		BufferedReader br = new BufferedReader(acronymFile);
		String entries;
		acronym = new Hashtable<String, String>();
		try {
			while ((entries = br.readLine()) != null){
				acronym.put(entries.toLowerCase(),"");
			}
		} catch (IOException e) {
			System.out.println("Problem reading acronym file");
			e.printStackTrace();
		}
		try {
			br.close();
		} catch (IOException e) {
			System.out.println("Problem closing buffered reader to read acronym file");
			e.printStackTrace();
		}
	}//end method
	/**
	 * return number of acronyms in a sentence (the sentence comes in as an arraylist of words)
	 * @param wordArray is the words of a sentence
	 * @return number of acronyms in a sentence (the sentence comes in as an arraylist of words)
	 */
	public int acronymCount(ArrayList <String> wordArray){
		this.count = 0;
		String temp = "";
		int size = wordArray.size();
		boolean found;
		for(int i = 0; i < size; i++){
			temp = wordArray.get(i).toLowerCase();
			found = acronym.containsKey(temp);
			if(found){
				this.count++;
			}
		}//end loop
		return this.count;
	}//end method
	
	public static void main(String[] args) throws IOException{
		Acronyms a = new Acronyms("H:/BioMedical Abbreviation.txt");
		//a.setAcronymFile();
		ArrayList<String> array = new ArrayList<String>();
		array.add("10th");
		//array.add("april");
		//array.add("does");
		//array.add("not");
		//array.add("show");
		//array.add("alternate");
		
		array.add("a junii");
		
		System.out.println(a.acronymCount(array));
		
	}

}//public class Acronyms