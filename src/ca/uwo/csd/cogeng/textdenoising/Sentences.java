package ca.uwo.csd.cogeng.textdenoising;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import fr.unice.nlp.genia.Genia;

/**
 * One of the major classes in the project that represents a sentence. All the features (stylistic) associated
 * with the sentence are represented as instance variables. The methods are for setting some features, 
 * calculating some features, and getting some feature values.
 * @author Rushdi Shams
 * @version 3.0 (Major optimization applied January 20 2015
 *
 */

public class Sentences {

	//---------------------------------------------------------------------------------------------
	//Instance Variables
	//---------------------------------------------------------------------------------------------	
	static StopWords stopList = new StopWords("languageresources/stopword.txt");
	static SemanticWords semWordObj = new SemanticWords("languageresources/umlsrelations.txt");
	static Verbs verbObj = new Verbs("languageresources/biomedicalverbs.txt");
	static Acronyms acronymObj = new Acronyms ("languageresources/biomedicalabbreviations.txt");
	//---------------------------------------------------------------------------------------------
	private String sentence = "", sentenceWSW = ""; //sentence
	final private int sentenceCount = 1; // number of sentence (fixed to 1)
	private ArrayList <String> wordList = new ArrayList <String> ();// content words + stop words
	private ArrayList<String> wordListWSW = new ArrayList <String> ();//content words
	private int wordSyllables = 0, wordSyllablesWSW = 0;// syllables in words
	private int totalSyllables = 0, totalSyllablesWSW = 0;// total syllables
	private int wordCount = 0; // #content+function words
	private int totalContentWords = 0;//#content words in the sentence
	private double contentWordPercent = 0.0;//%of content words in the sentence
	private int totalFunctionWords = 0;//#function words in the sentence
	private double functionWordPercent = 0.0;//%of function words in the sentence
	private int noOfComplexWords = 0, noOfComplexWordsWSW = 0; // #complex words
	private double complexWordsPercent = 0.0, complexWordsPercentWSW = 0.0; // %of complex words
	private int noOfMonosyllabicWords = 0, noOfMonosyllabicWordsWSW = 0;// #monosyllabic words
	private double monosyllabicWordsPercent = 0.0, monosyllabicWordsPercentWSW = 0.0; //%of monosyllabic
	private int namedEntityCount = 0, namedEntityCountWSW = 0;// #named entity 
	private double namedEntityPercent = 0.0, namedEntityPercentWSW = 0.0;// %of named entities
	private int aNum = 0;// #alpha numeric
	private int acronym = 0, acronymWSW = 0;// #of acronyms
	private double acronymPercent = 0.0, acronymPercentWSW = 0.0;// %of acronyms
	private int verbs = 0, verbsWSW = 0;// #of verb
	private double verbsPercent = 0.0, verbsPercentWSW = 0.0;// %of acronyms
	private int semanticWords = 0, semanticWordsWSW = 0;// #of semantic words
	private double semanticWordsPercent = 0.0, semanticWordsPercentWSW = 0.0;// %of semantic
	private int totalCharacters = 0, totalCharactersWSW = 0;//#characters
	private int notWhiteSpace = 0, notWhiteSpaceWSW = 0;//#of characters excluding white spaces
	private double charPerWord = 0.0, charPerWordWSW = 0.0;//character per word
	private boolean specialCharacter; //true if contains special character
	//Some word related variables (#)
	private int alpha = 0, numeric = 0, lowerCase = 0, upperCase = 0, alphaNum = 0, longWord = 0;
	private int alphaWSW = 0, numericWSW = 0, lowerCaseWSW = 0, upperCaseWSW = 0, alphaNumWSW = 0, longWordWSW = 0;
	//Some word related variables (%)
	private double alphaPercent, numericPercent, lowerCasePercent, upperCasePercent, alphaNumPercent, longWordPercent;
	private double alphaPercentWSW, numericPercentWSW, lowerCasePercentWSW, upperCasePercentWSW, alphaNumPercentWSW, longWordPercentWSW;
	private Set<String> uniqueWords, uniqueWordsWSW; //To hold only the unique words in the sentence
	private int wordUnique = 0, wordUniqueWSW = 0;// #unique words
	private double wordUniquePercent = 0.0, wordUniquePercentWSW = 0.0;//%of unique word 
	private int wordRepeated = 0, wordRepeatedWSW = 0;//#of repeated word 
	private double wordRepeatedPercent = 0.0, wordRepeatedPercentWSW = 0.0;//%of repeated words
	private boolean longSentence, shortSentence;//true if long or short sentence
	//Text complexity measure variables
	private double fogIndex, fogIndexWSW, forcast, forcastWSW, smogIndex, smogIndexWSW, 
	fkri, fkriWSW, flesch, fleschWSW, monoFogIndex, monoFogIndexWSW,invFogIndex, invFogIndexWSW;
	//POS related variables
	double conjunction = 0, number = 0, determiner = 0, preposition = 0, adjective = 0, noun = 0, pronoun = 0;
	double adverb = 0, verb = 0, interjection = 0, foreign = 0, list = 0, possessive = 0, particle = 0, symbol = 0;
	private int label;//sentence label 0 if negative, 1 if positive
	//locale of word iterator
	Locale currentLocale = new Locale ("en","US");
	BreakIterator wordIterator = BreakIterator.getWordInstance(currentLocale);	
	private final int longSentenceThreshold = 30;
	private final int shortSentenceThreshold = 20;
	private double wordLength = 0.0, wordLengthWSW = 0.0; //word length feature
	//---------------------------------------------------------------------------------------------
	//map for syllable count
	protected Map<String, String> syllableCountMap	=
			new HashMap<String, String>();


	//pattern to find syllables
	protected static final Pattern[] SubtractSyllables =
			new Pattern[]
					{
		Pattern.compile( "cial" ) ,
		Pattern.compile( "tia" ) ,
		Pattern.compile( "cius" ) ,
		Pattern.compile( "cious" ) ,
		Pattern.compile( "giu" ) ,	// belgium!
		Pattern.compile( "ion" ) ,
		Pattern.compile( "iou" )	,
		Pattern.compile( "sia$" ) ,
		Pattern.compile( ".ely$" )	// absolutely! (but not ely!)
					};// protected static final Pattern[] SubtractSyllables =new Pattern[]

	//pattern for syllables
	protected static final Pattern[] AddSyllables =
			new Pattern[]
					{
		Pattern.compile( "ia" ),
		Pattern.compile( "riet" ),
		Pattern.compile( "dien" ),
		Pattern.compile( "iu" ),
		Pattern.compile( "io" ),
		Pattern.compile( "ii" ),
		Pattern.compile( "[aeiouym]bl$" ) ,		// -Vble, plus -mble
		Pattern.compile( "[aeiou]{3}" ) ,		// agreeable
		Pattern.compile( "^mc" ) ,
		Pattern.compile( "ism$" ) ,				// -isms
		Pattern.compile( "([^aeiouy])\1l$" ) ,	// middle twiddle battle bottle, etc.
		Pattern.compile( "[^l]lien" ) ,			// alien, salient [1]
		Pattern.compile( "^coa[dglx]." ) , 		// [2]
		Pattern.compile( "[^gq]ua[^auieo]" ) ,	// i think this fixes more than it breaks
		Pattern.compile( "dnt$" )				// couldn't
					};//protected static final Pattern[] AddSyllables =new Pattern[]

	//---------------------------------------------------------------------------------------------
	//Methods
	//---------------------------------------------------------------------------------------------
	/**
	 * Constructor
	 * @param s is the sentence
	 */
	public Sentences(String s){
		this.sentence = s;
	}
	//-----------------------------------------------------------------------------------------
	//Setter Methods
	//-----------------------------------------------------------------------------------------
	/**
	 * Sets the label of the sentence. 
	 * @param label is either 0 for negative sentence or 1 for positive sentence
	 */
	public void setLabel(int label){
		this.label = label;
	}
	/**
	 * Sets features like total word
	 * @param sentence is the sentence
	 */
	//split the words of a sentence and take them to an arraylist
	public void setWord(String sentence){
		if(sentence != null){
			wordIterator.setText(sentence); // Word iterator is set on the sentence i
			int start = wordIterator.first(); // Start of word
			int end = wordIterator.next(); // Start of next word
			while (end != BreakIterator.DONE){
				String word = sentence.substring(start,end); // Word i of sentence i
				/*We take one sentence at a time and then read each word in it on the condition
				 that the first character of the word is either letter or digit.*/
				if (Character.isLetterOrDigit(word.charAt(0))) {
					this.wordCount ++; // Counting words 
					this.wordList.add(word);
				}//if (Character.isLetterOrDigit(word.charAt(0)))
				start = end; // Pointing to next word
				end = wordIterator.next(); // Pointing to the next of next word
			}//while (end!=BreakIterator.DONE)
		}//end if sentence is not null
		else{
			System.out.println("Message from setWord Method: Sentence is null");
		}
	}//end of method
	/**
	 * Method to set function and content word features
	 */
	public void setFunctionAndContentWords(){
		//setting up features related to # and % of function/content words
		//StopWords stopList = new StopWords("languageresources/stopword.txt");
		stopList.dealStopWord(this.wordList); //at this point, the # of stop words and the sentence without stop word is generated
		this.totalFunctionWords = stopList.getStopWordCount(); //setting the feature here.
		this.wordListWSW = stopList.getNoStopWordArray();
		for (String s : wordListWSW){
			this.sentenceWSW += s + " ";
		}
		this.sentenceWSW.trim();
		this.totalContentWords = this.wordListWSW.size();
		this.functionWordPercent = (double) this.totalFunctionWords / this.wordCount;
		this.contentWordPercent = (double) this.totalContentWords / this.wordCount;
		//...set up done.
	}// end method
	/**
	 * Method to set semantic words feature
	 */
	public void setSemanticWords(){
		//setting up features related to # and % of semantic words
		//SemanticWords semWordObj = new SemanticWords("languageresources/umlsrelations.txt");
		this.semanticWords = semWordObj.semWordCount(this.wordList);
		this.semanticWordsPercent = (double)this.semanticWords / this.wordCount;
		this.semanticWordsWSW = semWordObj.semWordCount(this.wordListWSW);
		this.semanticWordsPercentWSW = (double)this.semanticWordsWSW / this.totalContentWords;
		//... set up done.
	}//end method
	/**
	 * Method to set Biomedical Verb feature
	 */
	public void setBiomedicalVerbWords(){
		//setting up features related to # and % of biomedical verbs
		//Verbs verbObj = new Verbs("languageresources/biomedicalverbs.txt");
		this.verbs = verbObj.verbCount(this.wordList);
		this.verbsPercent = (double)this.verbs / this.wordCount;
		this.verbsWSW = verbObj.verbCount(this.wordListWSW);
		this.verbsPercentWSW = (double)this.verbsWSW / this.totalContentWords;
		//... set up done.
	}//end method
	/**
	 * Method to set Biomedical Acronym features
	 */
	public void setBiomedicalAcronymWords(){
		//setting up features related to # and % of biomedical acronyms
		//Acronyms acronymObj = new Acronyms ("languageresources/biomedicalabbreviations.txt");
		this.acronym = acronymObj.acronymCount(this.wordList);
		this.acronymPercent = (double)this.acronym / this.wordCount;
		this.acronymWSW = acronymObj.acronymCount(this.wordListWSW);
		this.acronymPercentWSW = (double)this.acronymWSW / this.totalContentWords;
		//... set up done.
	}//end method
	/**
	 * Sets total characters of the sentence with and without function words
	 */
	public void setTotalCharacters(){
		this.totalCharacters = this.sentence.length();
		this.totalCharactersWSW = this.sentenceWSW.length();
	}
	/**
	 * to count syllable of a word and total syllables of a sentence.
	 * Also to count number of complex and monosyllabic words in a sentence
	 */
	public void setSyllables(){
		String[] wordListArray;

		//Converting the array to string array
		//For the entire sentence

		wordListArray = new String[this.wordList.size()];
		if(wordListArray.length > 0){
			wordListArray = this.wordList.toArray(wordListArray);	
			for (String word: wordListArray){
				this.wordSyllables = countSyllables(word);
				this.totalSyllables += this.wordSyllables;
				//counting number of complexe words in a sentence
				if (this.wordSyllables >= 2)
					this.noOfComplexWords ++;
				//counting number of monosyllabic words in a sentence
				else
					this.noOfMonosyllabicWords ++;
			}//end for
		}//end if wordListArray is not empty
		else{
			System.out.println("Message from setSyllables Method: Sentence is null");
		}

		//For the content words
		wordListArray = new String[this.wordListWSW.size()];
		if(wordListArray.length > 0){
			wordListArray = this.wordListWSW.toArray(wordListArray);
			for (String word: wordListArray){
				this.wordSyllablesWSW = countSyllables(word);
				this.totalSyllablesWSW = this.totalSyllablesWSW + this.wordSyllablesWSW;
				//counting number of complexe words in a sentence
				if (this.wordSyllablesWSW >= 2)
					this.noOfComplexWordsWSW ++;
				//counting number of monosyllabic words in a sentence
				else
					this.noOfMonosyllabicWordsWSW ++;
			}//end for
		}//if wordListArray is not empty
		else{
			System.out.println("Message from setSyllables Method: The array of content words is null");
		}	
	}//end method
	/**
	 * Method to set features to deal with #of characters excluding spaces
	 */
	public void setCharacterWithoutSpace(){
		String noWhiteSpace = StringUtils.deleteWhitespace(this.sentence);
		String noWhiteSpaceWSW = StringUtils.deleteWhitespace(this.sentenceWSW);
		//setting total character except white spaces feature
		if(noWhiteSpace.length() > 0)
			this.notWhiteSpace = noWhiteSpace.length();
		else
			System.out.println("Message from setCharacterWithoutSpace Method: Is your sentence just a word?");
		if(noWhiteSpaceWSW.length() > 0)
			this.notWhiteSpaceWSW = noWhiteSpaceWSW.length();
		else
			System.out.println("Message from setCharacterWithoutSpace Method: Just one content word and no space?");
	}//end method
	/**
	 * Method to set features to deal with characters per word
	 */
	public void setCharPerWord(){
		if(this.wordCount > 0)
			this.charPerWord = (double)this.totalCharacters / this.wordCount;
		if(this.totalContentWords > 0)
			this.charPerWordWSW = (double)this.totalCharactersWSW / this.totalContentWords;
	}//end method
	/**
	 * Method to set features to deal whether the sentence contains special characters or not
	 */
	public void setSpecialCharacter(){
		Pattern pattern = Pattern.compile("[^a-z0-9. ]", Pattern.CASE_INSENSITIVE);
		if(this.sentence.length() > 0){
			Matcher patternMatcher = pattern.matcher(this.sentence);
			this.specialCharacter = patternMatcher.find();
		}//end if
	}//end method
	/**
	 * counting named entities in the sentence
	 * @param genObj is the Genia object
	 */
	//counting named entities in the sentence
	public void setNamedEntity(Genia genObj){
		//preparing input to Genia NER
		String [] geniaInput = new String [2];
		String namedEntity;
		geniaInput [0] = "";
		//for entire sentence
		if(this.sentence != null){
			geniaInput [1] = this.sentence;
			namedEntity = genObj.process(geniaInput);
			String[] strArr = namedEntity.split("\n");
			for (String temp : strArr){
				if (!temp.equalsIgnoreCase("O"))
					this.namedEntityCount ++;
			}//for (String temp : strArr)	
		}//end if sentence is not null
		//for content words
		if(this.sentenceWSW != null){
			geniaInput [1] = this.sentenceWSW;
			namedEntity = genObj.process(geniaInput);
			String[] strArr = namedEntity.split("\n");
			for (String temp : strArr){
				if (!temp.equalsIgnoreCase("O"))
					this.namedEntityCountWSW ++;
			}//for (String temp : strArr)
		}//end if sentence is not null
	}//public void setNamedEntity()
	/**
	 * Method to set word level features
	 */
	public void setWordLevelFeatures(){
		Scanner scanner;
		if(this.wordList.size() > 0){
			for (String w : this.wordList){
				w = w.trim();
				scanner = new Scanner (w);
				if (StringUtils.isAlpha(w)){alpha ++;}
				if (StringUtils.isNumeric(w) || scanner.hasNextDouble()){numeric ++;}
				if (StringUtils.isAllLowerCase(w)){lowerCase ++;}
				if (StringUtils.isAllUpperCase(w)){upperCase++;}
				if (StringUtils.isAlphanumeric(w)){
					if (w.equals("PROT")){}
					else{alphaNum++;}
				}//end outer if
				if (w.length() > 6){longWord ++;}
			}//end for
			if(this.wordCount > 0){
				alphaPercent = (double)alpha / this.wordCount;
				numericPercent = (double)numeric / this.wordCount;
				lowerCasePercent = (double)lowerCase / this.wordCount;
				upperCasePercent = (double)upperCase / this.wordCount;
				alphaNumPercent = (double)alphaNum / this.wordCount;
				longWordPercent = (double)longWord / this.wordCount;
			}//end if
		}//end if the word list is not empty

		if(this.wordListWSW.size() > 0){
			for (String w : this.wordListWSW){
				w = w.trim();
				scanner = new Scanner (w);
				if (StringUtils.isAlpha(w)){alphaWSW ++;}
				if (StringUtils.isNumeric(w) || scanner.hasNextDouble()){numericWSW ++;}
				if (StringUtils.isAllLowerCase(w)){lowerCaseWSW ++;}
				if (StringUtils.isAllUpperCase(w)){upperCaseWSW++;}
				if (StringUtils.isAlphanumeric(w)){
					if (w.equals("PROT")){}
					else{alphaNumWSW++;}
				}//end outer if
				if (w.length() > 6){longWordWSW ++;}
			}//end for loop
			if(this.totalContentWords > 0){
				alphaPercentWSW = (double)alphaWSW / this.totalContentWords;
				numericPercentWSW = (double)numericWSW / this.totalContentWords;
				lowerCasePercentWSW = (double)lowerCaseWSW / this.totalContentWords;
				upperCasePercentWSW = (double)upperCaseWSW / this.totalContentWords;
				alphaNumPercentWSW = (double)alphaNumWSW / this.totalContentWords;
				longWordPercentWSW = (double)longWordWSW / this.totalContentWords;
			}//end if
		}//end if the conten word list is not empty
	}//end method
	/**
	 * Method to set unique word feature
	 */
	public void setUniqueWordsFeatures(){
		uniqueWords = new HashSet<String>(this.wordList);
		this.wordUnique = uniqueWords.size();
		if(this.wordCount > 0)
			this.wordUniquePercent = (double)wordUnique / this.wordCount;

		uniqueWordsWSW = new HashSet<String>(this.wordListWSW);
		this.wordUniqueWSW = uniqueWordsWSW.size();
		if(this.totalContentWords > 0)
			this.wordUniquePercentWSW = (double)wordUniqueWSW / this.totalContentWords;
	}//end method
	/**
	 * Method to set repeated word feature
	 */
	public void setRepeatedWordsFeatures(){
		this.wordRepeated = this.wordCount - wordUnique;
		if(this.wordCount > 0)
			this.wordRepeatedPercent = (double)this.wordRepeated / this.wordCount;

		this.wordRepeatedWSW = this.totalContentWords - wordUniqueWSW;
		if(this.totalContentWords > 0)
			this.wordRepeatedPercentWSW = (double)this.wordRepeatedWSW / this.totalContentWords; 
	}//end method
	/**
	 * Method to set long and short features
	 */
	public void setLongAndShortSentenceFeatures(){
		if (this.wordCount > this.longSentenceThreshold)
			longSentence = true;
		if (this.wordCount < this.shortSentenceThreshold)
			shortSentence = true;
	}//end method
	/**
	 * Method to set complexity measure features
	 */
	public void setComplexityMeasureFeatures(){
		//Fog index
		this.fogIndex = measureFogIndex(this.wordCount, this.sentenceCount, this.noOfComplexWords);
		this.fogIndexWSW = measureFogIndex(this.totalContentWords, this.sentenceCount, this.noOfComplexWordsWSW);

		//FORCAST
		this.forcast = measureForcastIndex(this.wordCount, this.noOfMonosyllabicWords);
		this.forcastWSW = measureForcastIndex(this.totalContentWords, this.noOfMonosyllabicWordsWSW);

		//Smog index
		this.smogIndex = measureSI(this.sentenceCount, this.noOfComplexWords);
		this.smogIndexWSW = measureSI(this.sentenceCount, this.noOfComplexWordsWSW);

		//FKRI
		this.fkri = measureFleschKincaid(this.wordCount, this.sentenceCount, this.totalSyllables);
		this.fkriWSW = measureFleschKincaid(this.totalContentWords, this.sentenceCount, this.totalSyllablesWSW);

		//Flesch-Kincaid index
		this.flesch = measureFlesch(this.wordCount, this.sentenceCount, this.totalSyllables);
		this.fleschWSW = measureFlesch(this.totalContentWords, this.sentenceCount, this.totalSyllablesWSW);

		//Monosyllabic Fog index
		this.monoFogIndex = measureFogIndex(this.wordCount, this.sentenceCount, this.noOfMonosyllabicWords);
		this.monoFogIndexWSW = measureFogIndex(this.totalContentWords, this.sentenceCount, this.noOfMonosyllabicWordsWSW);

		//Inverse Fog index
		if(this.fogIndex > 0.0)
			this.invFogIndex = 1.0 / this.fogIndex;
		if(this.fogIndexWSW > 0.0)
			this.invFogIndexWSW = 1.0 / this.fogIndexWSW;
	}//end method
	/**
	 * Method to set POS tag features
	 * @param tagger is the Stanford Maximum entropy tagger
	 */
	public void setPOSFeatures(MaxentTagger tagger){
		if(this.sentence != null){
			String tagged = tagger.tagString(this.sentence.trim());
			String[] taggedArray = tagged.split(" ");
			for (String t : taggedArray){
				t = t.trim();
				if (StringUtils.endsWith(t, "CC") || StringUtils.endsWith(t, "IN")){conjunction ++;}
				if (StringUtils.endsWith(t, "CD")){number ++;}
				if(StringUtils.endsWith(t, "DT") || StringUtils.endsWith(t, "PDT") 
						|| StringUtils.endsWith(t, "WDT")){determiner ++;}
				if(StringUtils.endsWith(t, "IN") || StringUtils.endsWith(t, "TO")){preposition ++;}
				if(StringUtils.endsWith(t, "JJ") || StringUtils.endsWith(t, "JJR") 
						|| StringUtils.endsWith(t, "JJS")){adjective ++;}
				if(StringUtils.endsWith(t, "NN") || StringUtils.endsWith(t, "NNS") 
						|| StringUtils.endsWith(t, "NNP") || StringUtils.endsWith(t, "NNPS")){noun ++;}
				if(StringUtils.endsWith(t, "PRP") || StringUtils.endsWith(t, "PRP$") || StringUtils.endsWith(t, "WP") 
						|| StringUtils.endsWith(t, "WP$") || StringUtils.endsWith(t, "EX")){pronoun ++;}
				if(StringUtils.endsWith(t, "RB") || StringUtils.endsWith(t, "RBR") 
						|| StringUtils.endsWith(t, "RBS") || StringUtils.endsWith(t, "WRB")){adverb ++;}
				if(StringUtils.endsWith(t, "VB") || StringUtils.endsWith(t, "VBD") 
						|| StringUtils.endsWith(t, "VBG") || StringUtils.endsWith(t, "VBN") 
						|| StringUtils.endsWith(t, "VBP") || StringUtils.endsWith(t, "VBZ") 
						|| StringUtils.endsWith(t, "MD")){verb ++;}
				if(StringUtils.endsWith(t, "UH")){interjection ++;}
				if(StringUtils.endsWith(t, "FW")){foreign ++;}
				if(StringUtils.endsWith(t, "LS")){list ++;}
				if(StringUtils.endsWith(t, "POS")){possessive ++;}
				if(StringUtils.endsWith(t, "RP")){particle ++;}
				if(StringUtils.endsWith(t, "SYM")){symbol ++;}
			}//for (String t : taggedArray)

			if(this.wordCount > 0){
				conjunction = (double)conjunction / this.wordCount;
				number = (double)number / this.wordCount;
				determiner = (double)determiner / this.wordCount;
				preposition = (double)preposition / this.wordCount;
				adjective = (double)adjective / this.wordCount;
				noun = (double)noun / this.wordCount;
				pronoun = (double)pronoun / this.wordCount;
				adverb = (double)adverb / this.wordCount;
				verb = (double)verb / this.wordCount;
				interjection = (double)interjection / this.wordCount;
				foreign = (double)foreign / this.wordCount;
				list = (double)list / this.wordCount;
				possessive = (double)possessive / this.wordCount;
				particle = (double)particle / this.wordCount;
				symbol = (double)symbol / this.wordCount;
			}//if # of word is not 0
		}//end if the sentence is not null
	}//end method
	//-----------------------------------------------------------------------------------------
	//Getter Methods
	//-----------------------------------------------------------------------------------------
	/**
	 * Returns the sentence
	 * @return the sentence in String format
	 */
	public String getSentence(){
		return this.sentence;
	}
	/**
	 * Return the sentence without stop word
	 * @return the sentence with only content words
	 */
	public String getSentenceWSW(){
		return this.sentenceWSW;
	}
	/**
	 * Returns the label of the sentence
	 * @return 0 if the sentence is negative, 1 otherwise
	 */
	public int getLabel(){
		return this.label;
	}
	/**
	 * Returns total characters in a sentence
	 * @return number of characters in the sentence
	 */
	public int getTotalCharacters() {
		return totalCharacters;
	}
	/**
	 * Returns total characters in the content words
	 * @return number of characters in the content words
	 */
	public int getTotalCharactersWSW() {
		return totalCharactersWSW;
	}
	/**
	 * Returns total number of content words in the sentence
	 * @return total number of content words in the sentence
	 */
	public int getNumberOfContentWord(){
		return this.wordListWSW.size();
	}
	/**
	 * return the list of words in a sentence
	 * @return the list of words in a sentence
	 */
	public ArrayList <String> getWord(){
		return this.wordList;
	}
	/**
	 * Method to return total words in a sentence
	 * @return # of words in a sentence
	 */
	public int getWordCount(){
		return this.wordCount;
	}
	/**
	 * return total number of syllables in a sentence
	 * @return total number of syllables in a sentence
	 */
	public int getTotalSyllables(){
		return this.totalSyllables;
	}
	/**
	 * returns total number of complex words in a sentence
	 * @return total number of complex words in a sentence
	 */
	public int getComplexWordNumber(){
		return this.noOfComplexWords;
	}
	/**
	 * returns total number of monosyllabic words in a sentence
	 * @return total number of monosyllabic words in a sentence
	 */
	public int getMonosyllabicWordNumber(){
		return this.noOfMonosyllabicWords;
	}
	/**
	 *return fog index of the sentence 
	 * @return fog index of the sentence
	 */
	public double getFogIndex(){
		return this.fogIndex;
	}
	/**
	 * returns # of named entities in the sentence
	 * @return # of named entities in the sentence
	 */
	public int getNE(){
		return this.namedEntityCount;
	}
	/**
	 * returns # of named entities in the content words
	 * @return # of named entities in the content words
	 */
	public int getNEWSW(){
		return this.namedEntityCountWSW;
	}
	/**
	 * returns the % of complex words in the sentence
	 * @return % of complex words in the sentence
	 */
	public double getComplexWordPercent(){
		this.complexWordsPercent = (double) this.noOfComplexWords / this.wordCount;
		return this.complexWordsPercent;
	}
	/**
	 * returns the % of complex words in the content words
	 * @return % of complex words in the content words
	 */
	public double getComplexWordWSW(){
		this.complexWordsPercentWSW = (double) this.noOfComplexWordsWSW / this.totalContentWords;
		return this.complexWordsPercentWSW;
	}
	/**
	 * returns the % of simple words in the sentence
	 * @return % of simple words in the sentence
	 */
	public double getMonosyllabicWordPercent(){
		this.monosyllabicWordsPercent = (double) this.noOfMonosyllabicWords / this.wordCount;
		return this.monosyllabicWordsPercent;
	}
	/**
	 * returns the % of simple words in the content words
	 * @return % of simple words in the content words
	 */
	public double getMonosyllabicWordPercentWSW(){
		this.monosyllabicWordsPercentWSW = (double) this.noOfMonosyllabicWordsWSW / this.totalContentWords;
		return this.monosyllabicWordsPercentWSW;
	}
	/**
	 * returns the % of NEs in the sentence
	 * @return % of NEs in the sentence
	 */
	public double getNamedEntityPercent(){
		this.namedEntityPercent = (double) this.namedEntityCount / this.wordCount;
		return this.namedEntityPercent;
	}
	/**
	 * returns the % of NEs in the content words
	 * @return % of NEs in the content words
	 */
	public double getNamedEntityPersentWSW(){
		this.namedEntityPercentWSW = (double) this.namedEntityCountWSW / this.totalContentWords;
		return this.namedEntityPercentWSW;
	}
	/**
	 * returns # of alphanumeric words in the sentence
	 * @return # of alphanumeric words in the sentence
	 */
	public double getAlphaNum (){
		return this.alphaNum; 
	}
	/**
	 * returns % of verbs in the sentence
	 * @return % of verbs in the sentence
	 */
	public double getVerbPercent(){
		return this.verbsPercent;
	}
	/**
	 * returns % of verbs in the content words
	 * @return % of verbs in the content words
	 */
	public double getVerbPercentWSW(){
		return this.verbsPercentWSW;
	}
	/**
	 * returns # of acronyms in the sentence
	 * @return # of acronyms in the sentence
	 */
	public double getAcronym(){
		return this.acronym;
	}
	/**
	 * returns % of acronyms in the sentence
	 * @return % of acronyms in the sentence
	 */
	public double getAcronymPercent(){
		return this.acronymPercent;
	}
	/**
	 * returns # of acronyms in the content words
	 * @return # of acronyms in the content words
	 */
	public double getAcronymWSW(){
		return this.acronymWSW;
	}
	/**
	 * returns % of acronyms in the content words
	 * @return % of acronyms in the content words
	 */
	public double getAcronymPercentWSW(){
		return this.acronymPercentWSW;
	}
	/**
	 * returns # of semantic words in the sentence
	 * @return # of semantic words in the sentence
	 */
	public double getSemWord(){
		return this.semanticWords;
	}
	/**
	 * returns % of semantic words in the sentence
	 * @return % of semantic words in the sentence
	 */
	public double getSemWordPercent(){
		return this.semanticWordsPercent;
	}

	public double getSemWordWSW(){
		return this.semanticWordsWSW;
	}

	public double getSemWordPercentWSW(){
		return this.semanticWordsPercentWSW;
	}

	public double getFogIndexWSW() {
		return fogIndexWSW;
	}

	public double getInvFogIndex() {
		return invFogIndex;
	}

	public double getInvFogIndexWSW() {
		return invFogIndexWSW;
	}

	public double getForcast() {
		return forcast;
	}

	public double getForcastWSW() {
		return forcastWSW;
	}

	public double getFkri() {
		return fkri;
	}

	public double getFkriWSW() {
		return fkriWSW;
	}

	public double getFlesch() {
		return flesch;
	}

	public double getFleschWSW() {
		return fleschWSW;
	}

	public double getMonoFogIndex() {
		return monoFogIndex;
	}

	public double getMonoFogIndexWSW() {
		return monoFogIndexWSW;
	}

	public double getSmogIndex() {
		return smogIndex;
	}

	public double getSmogIndexWSW() {
		return smogIndexWSW;
	}

	public int getNoOfComplexWordsWSW() {
		return noOfComplexWordsWSW;
	}

	public int getNoOfMonosyllabicWordsWSW() {
		return noOfMonosyllabicWordsWSW;
	}

	public double getFunctionWordPercent() {
		return functionWordPercent;
	}

	public int getTotalContentWords() {
		return totalContentWords;
	}

	public int getTotalFunctionWords() {
		return totalFunctionWords;
	}

	public double getComplexWordsPercent() {
		return complexWordsPercent;
	}

	public int getaNum() {
		return aNum;
	}

	public double getCharPerWord() {
		return charPerWord;
	}

	public double getCharPerWordWSW() {
		return charPerWordWSW;
	}

	public double getNumeric() {
		return numeric;
	}

	public double getAlphaWSW() {
		return alphaWSW;
	}

	public double getNumericWSW() {
		return numericWSW;
	}

	public double getAlphaNumWSW() {
		return alphaNumWSW;
	}

	public double getAlphaPercent() {
		return alphaPercent;
	}

	public double getNumericPercent() {
		return numericPercent;
	}

	public double getAlphaNumPercent() {
		return alphaNumPercent;
	}

	public double getAlphaPercentWSW() {
		return alphaPercentWSW;
	}

	public double getNumericPercentWSW() {
		return numericPercentWSW;
	}

	public double getAlphaNumPercentWSW() {
		return alphaNumPercentWSW;
	}

	public double getConjunction() {
		return conjunction;
	}

	public double getNumber() {
		return number;
	}

	public double getDeterminer() {
		return determiner;
	}

	public double getPreposition() {
		return preposition;
	}

	public double getNoun() {
		return noun;
	}

	public double getPronoun() {
		return pronoun;
	}

	public double getVerb() {
		return verb;
	}

	public double getInterjection() {
		return interjection;
	}

	public double getForeign() {
		return foreign;
	}

	public double getList() {
		return list;
	}

	public double getPossessive() {
		return possessive;
	}

	public double getParticle() {
		return particle;
	}

	public double getSymbol() {
		return symbol;
	}

	public double getAlpha() {
		return alpha;
	}

	public double getAdjective() {
		return adjective;
	}

	public double getAdverb() {
		return adverb;
	}

	public double getLowerCase() {
		return lowerCase;
	}

	public double getLowerCaseWSW() {
		return lowerCaseWSW;
	}

	public double getLowerCasePercent() {
		return lowerCasePercent;
	}

	public double getLowerCasePercentWSW() {
		return lowerCasePercentWSW;
	}

	public double getUpperCase() {
		return upperCase;
	}

	public double getUpperCaseWSW() {
		return upperCaseWSW;
	}

	public double getUpperCasePercent() {
		return upperCasePercent;
	}

	public double getUpperCasePercentWSW() {
		return upperCasePercentWSW;
	}

	public double getLongWord() {
		return longWord;
	}

	public double getLongWordWSW() {
		return longWordWSW;
	}

	public double getLongWordPercent() {
		return longWordPercent;
	}

	public double getLongWordPercentWSW() {
		return longWordPercentWSW;
	}

	public int getWordUnique() {
		return wordUnique;
	}

	public int getWordUniqueWSW() {
		return wordUniqueWSW;
	}

	public double getWordUniquePercent() {
		return wordUniquePercent;
	}

	public double getWordUniquePercentWSW() {
		return wordUniquePercentWSW;
	}

	public double getWordRepeated() {
		return wordRepeated;
	}

	public double getWordRepeatedWSW() {
		return wordRepeatedWSW;
	}

	public double getWordRepeatedPercent() {
		return wordRepeatedPercent;
	}

	public double getWordRepeatedPercentWSW() {
		return wordRepeatedPercentWSW;
	}

	public double getNotWhiteSpace() {
		return notWhiteSpace;
	}

	public double getNotWhiteSpaceWSW() {
		return notWhiteSpaceWSW;
	}

	public boolean isSpecialCharacter() {
		return specialCharacter;
	}

	public int getTotalSyllablesWSW() {
		return totalSyllablesWSW;
	}

	public boolean isLongSentence() {
		return longSentence;
	}

	public boolean isShortSentence() {
		return shortSentence;
	}

	public double getContentWordPercent() {
		return contentWordPercent;
	}

	public double getVerbs() {
		return verbs;
	}

	public double getVerbsWSW() {
		return verbsWSW;
	}
	public double getWordLength(){
		this.wordLength = (double)this.totalSyllables / this.wordCount;
		return this.wordLength;
	}

	public double getWordLengthWSW() {
		this.wordLengthWSW = (double)this.totalSyllablesWSW / this.totalContentWords;
		return wordLengthWSW;
	}


	//---------------------------------------------------------------------------------------------
	//Syllables
	//---------------------------------------------------------------------------------------------

	//count syllables of a word
	public static int countSyllables( String word ){

		int result = 0;
		//	Null or empty word?
		//	Syllable count is zero.
		if ( ( word == null ) || ( word.length() == 0 ) )
		{
			return result;
		}//if ( ( word == null ) || ( word.length() == 0 ) )
		//	If word is in the dictionary,
		//	return the syllable count from the
		//	dictionary.

		String lcWord	= word.toLowerCase();

		//	If word is not in the dictionary,
		//	use vowel group counting to get
		//	the estimated syllable count.

		//	Remove embedded apostrophes and
		//	terminal e.

		lcWord	= lcWord.replaceAll( "'" , "" ).replaceAll( "e$" , "" );

		//	Split word into vowel groups.

		String[] vowelGroups	= lcWord.split( "[^aeiouy]+" );

		//	Handle special cases.

		//	Subtract from syllable count
		//	for these patterns.

		for ( Pattern p : SubtractSyllables )
		{
			Matcher m	= p.matcher( lcWord );

			if ( m.find() )
			{
				result--;
			}//if ( m.find() )
		}//for ( Pattern p : SubtractSyllables )
		//	Add to syllable count for these patterns.

		for ( Pattern p : AddSyllables )
		{
			Matcher m	= p.matcher( lcWord );

			if ( m.find() )
			{
				result++;
			}//if ( m.find() )
		}//for ( Pattern p : AddSyllables )

		if ( lcWord.length() == 1 )
		{
			result++;
		}//if ( lcWord.length() == 1 )
		//	Count vowel groupings.

		if	(	( vowelGroups.length > 0 ) &&
				( vowelGroups[ 0 ].length() == 0 )
				)
		{
			result	+= vowelGroups.length - 1;
		}//if	(	( vowelGroups.length > 0 ) && ( vowelGroups[ 0 ].length() == 0 ))
		else
		{
			result	+= vowelGroups.length;
		}//else

		//	Return syllable count of
		//	at least one.

		return Math.max( result , 1 );
	}//public static int countSyllables( String word )
	//--------------------------------------------------------------------------------
	///////////////////////////////////////////
	//Methods to measure various reading index//
	////////////////////////////////////////////
	/**
	 * Static method to calculate the fog index of the sentence
	 * @param wordCount # of words in the sentence
	 * @param sentenceCount # of sentences passed (constant, 1)
	 * @param complexwordCount # of complex words in the sentence 
	 * @return fog index
	 */
	public static double measureFogIndex(int wordCount,int sentenceCount,int complexwordCount){
		double fi = 0.0;
		double normalizeWord = (double) wordCount / sentenceCount;
		if(wordCount > 0){
			double normalizeComplex = (double) 100 * (double)complexwordCount/wordCount;
			fi= (double) (0.4 * (normalizeWord + normalizeComplex));
		}//end if
		return fi;
	}//end method
	/**
	 * Static method to calculage FORCAST index
	 * @param wordcount # of words in the sentence
	 * @param monosyllable # of monosyllabic words in the sentence
	 * @return FORCAST index of the sentence
	 */
	public static double measureForcastIndex(int wordcount,int monosyllable){
		double forcast = 0.0;
		if(wordcount > 0){
			double n = ((double)monosyllable / wordcount) * 150;
			double parameter1 = n / 10;
			forcast = 20 - parameter1;
		}//end if
		return forcast;
	}//end method
	/**
	 * Static method to calculate smog index
	 * @param sentencecount # of sentences passed (constant, 1)
	 * @param complexwordcount # of complex words in the sentence
	 * @return SMOG index of the sentence
	 */
	public static double measureSI(int sentencecount,int complexwordcount){
		double parameter1 = (double)complexwordcount / sentencecount;
		double parameter2 = (double)Math.sqrt((double)30 * parameter1);
		double smog = 1.043 * parameter2 + 3.1291;
		if(smog < 0.0)
			smog=0.0;
		return smog;
	}//end method
	/**
	 * Static method to calculate the FKRI index of a sentence
	 * @param wordcount # words in a sentence
	 * @param sentencecount # of sentences passed (constant, 1)
	 * @param totalsyllable # of syllables in the sentence
	 * @return FKRI index of the sentence
	 */
	public static double measureFleschKincaid(int wordcount,int sentencecount,int totalsyllable){
		double fk = 0.0;
		double parameter1 = (double)wordcount / sentencecount;
		if(wordcount > 0){
			double parameter2 = (double)totalsyllable / wordcount;
			fk = 0.39 * parameter1 + 11.8* parameter2 - 15.59;
		}//end if
		if(fk < -3.40)// The lowest possible value of Flesch-Kincaid Index
			fk = 0.0;
		return fk;
	}//end method
	/**
	 * Static method to calculate Flesch index of a sentence
	 * @param wordcount # words in a sentence
	 * @param sentencecount # of sentences passed (constant, 1)
	 * @param totalsyllable # of syllables in a sentence
	 * @return
	 */
	public static double measureFlesch(int wordcount,int sentencecount,int totalsyllable){
		double fres = 0.0;
		double parameter1 =(double)wordcount / sentencecount;
		if(wordcount > 0){
			double parameter2 = (double)totalsyllable / wordcount;
			fres = 206.835 - 1.015 * parameter1 - 84.6 * parameter2;
		}//end if
		if(fres<0.0) // Flesch Index is never negative
			fres = 0.0;
		return fres;
	}//end method

}//public class Sentences