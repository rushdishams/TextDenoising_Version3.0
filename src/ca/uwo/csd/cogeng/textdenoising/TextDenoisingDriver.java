package ca.uwo.csd.cogeng.textdenoising;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.io.FileUtils;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import fr.unice.nlp.genia.Genia;

/**
 * Driver class for running text denoising software tool.
 * This class has a main() method to read a labeled dataset, extract stylometric features, and 
 * build a 2-stage classifier involving KNN and NB. Finally, the method displays the classification
 * results as output. 
 * @author Rushdi Shams
 * @version 3.0 February 03 2015
 *
 */

public class TextDenoisingDriver {

	//Instance variable

	static Genia genObj = new Genia();// Object to access Genia NER system
	static MaxentTagger tagger = new MaxentTagger("models/wsj-0-18-bidirectional-nodistsim.tagger");

	public static void main(String[] args) throws IOException {
		//Initializing Genia NER system
		genObj.init();

		File dataFile = new File("data/aimed-annotated.txt"); 
		String dataContent = FileUtils.readFileToString(dataFile);
		String[] data = dataContent.split("\n");

		//Takes output file from user
		System.out.println("Output File: ");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String arffFile = br.readLine();

		br.close();

		//to hold the number sentences in a file
		int totalSentences = data.length;

		System.err.println("-- Total Sentences: " + totalSentences + " --");
		StringBuilder outputString = new StringBuilder();

		//for every Sentence --
		Sentences[] s = new Sentences[totalSentences]; //sentence object..
		for (int i = 0; i < totalSentences; i++){
			System.out.println("Processing sentence # " + (i+1));		
			if(data[i].startsWith("N")){ //if the sentence is negative...
				s[i] = new Sentences(data[i].substring(3).trim()); //take only the text part leaving the label behind
				s[i].setLabel(0);; //labels set to 0(negative)
			}//end if
			else if(data[i].startsWith("Y")){//if the sentence is positive...
				s[i] = new Sentences(data[i].substring(4).trim());//take only the text part leaving the label behind
				s[i].setLabel(1);;//labels set to 1(positive)
			}//end else if
			else{//if the sentence does not start with Y or N, then there is a problem in formatting...
				System.out.println("Data instances should start either with Yes or No (The sentence labels)");
				break;//we are not proceeding any further.
			}//end else

			//System.out.println(s[i].getSentence());

			//setting the words of a sentence
			s[i].setWord(s[i].getSentence());
			//setting fucntion and content words
			s[i].setFunctionAndContentWords();
			//setting biomedical verb
			s[i].setBiomedicalVerbWords();
			//setting biomedical acronym
			s[i].setBiomedicalAcronymWords();
			//setting semantic words
			s[i].setSemanticWords();
			//setting number of complex and monosyllabic words features in a sentence
			s[i].setSyllables();
			//setting named entity feature
			s[i].setNamedEntity(genObj);
			//setting total character feature
			s[i].setTotalCharacters();
			//Sentence without white space
			s[i].setCharacterWithoutSpace();
			//setting character per word feature
			s[i].setCharPerWord();
			// setting special character setting
			s[i].setSpecialCharacter();
			//setting word level features
			s[i].setWordLevelFeatures();
			//setting unique word feature
			s[i].setUniqueWordsFeatures();
			//setting repeated word feature
			s[i].setRepeatedWordsFeatures();
			//setting long and short sentence feature
			s[i].setLongAndShortSentenceFeatures();
			//setting complexity measurement features
			s[i].setComplexityMeasureFeatures();
			//Part of Speech related features
			s[i].setPOSFeatures(tagger);
			String label = "?";

			//setting label of the sentence
			if (s[i].getLabel() == 1)
				label = "positive";
			else
				label = "negative";
			outputString.append(
							s[i].getFogIndex()+ "," +
							s[i].getFogIndexWSW() + "," +
							s[i].getMonoFogIndex() + "," +
							s[i].getMonoFogIndexWSW() + "," +
							s[i].getInvFogIndex() + "," +
							s[i].getInvFogIndex() + "," +
							s[i].getForcast() + "," +
							s[i].getForcastWSW() + "," +
							s[i].getSmogIndex() + "," +
							s[i].getSmogIndexWSW() + "," +
							s[i].getFkri() + "," +
							s[i].getFkriWSW()+ "," +
							s[i].getFlesch() + "," +
							s[i].getFleschWSW() + "," +
							s[i].getComplexWordNumber() + "," +
							s[i].getComplexWordPercent() + "," +
							s[i].getNoOfComplexWordsWSW() + "," +
							s[i].getComplexWordWSW() + "," +
							s[i].getMonosyllabicWordNumber() + "," +
							s[i].getMonosyllabicWordPercent() + "," +
							s[i].getNoOfMonosyllabicWordsWSW() + "," +
							s[i].getMonosyllabicWordPercentWSW() + "," +
							s[i].getWordLength() + "," +
							s[i].getWordLengthWSW() + "," +
							s[i].getTotalSyllables() + "," +
							s[i].getTotalSyllablesWSW() + "," +
							s[i].isLongSentence() + "," +
							s[i].isShortSentence() + "," +

							s[i].getWordCount() + "," +
							s[i].getTotalFunctionWords() + "," +
							s[i].getFunctionWordPercent() + "," +
							s[i].getTotalContentWords() + "," +
							s[i].getContentWordPercent() + "," +
							(double)s[i].getNE() + "," +
							s[i].getNamedEntityPercent() + "," +
							s[i].getNEWSW() + "," +
							s[i].getNamedEntityPersentWSW() + "," +
							s[i].getAlphaNum() + "," +
							s[i].getAlphaNumPercent() + "," +
							s[i].getAlphaNumWSW() + "," +
							s[i].getAlphaNumPercentWSW() + "," +
							s[i].getVerbs() + "," +
							s[i].getVerbPercent() + "," +
							s[i].getVerbsWSW() + "," +
							s[i].getVerbPercentWSW() + "," +
							s[i].getAcronym() + "," +
							s[i].getAcronymPercent() + "," +
							s[i].getAcronymWSW() + "," +
							s[i].getAcronymPercentWSW() + "," +
							s[i].getSemWord() + "," +
							s[i].getSemWordPercent() + "," +
							s[i].getSemWordWSW() + "," +
							s[i].getSemWordPercentWSW() + "," +
							s[i].getAlpha() + "," +
							s[i].getAlphaPercent() + "," +
							s[i].getAlphaWSW() + "," +
							s[i].getAlphaPercentWSW() + "," +
							s[i].getNumeric() + "," +
							s[i].getNumericPercent() + "," +
							s[i].getNumericWSW() + "," +
							s[i].getNumericPercentWSW() + "," +
							s[i].getLowerCase() + "," +
							s[i].getLowerCasePercent() + "," +
							s[i].getLowerCaseWSW() + "," +
							s[i].getLowerCasePercentWSW() + "," +
							s[i].getUpperCase() + "," +
							s[i].getUpperCasePercent() + "," +
							s[i].getUpperCaseWSW() + "," +
							s[i].getUpperCasePercentWSW() + "," +
							s[i].getLongWord() + "," +
							s[i].getLongWordPercent() + "," +
							s[i].getLongWordWSW() + "," +
							s[i].getLongWordPercentWSW() + "," +
							s[i].getWordUnique() + "," +
							s[i].getWordUniquePercent() + "," +
							s[i].getWordUniqueWSW() + "," +
							s[i].getWordUniquePercentWSW() + "," +
							s[i].getWordRepeated() + "," +
							s[i].getWordRepeatedPercent() + "," +
							s[i].getWordRepeatedWSW() + "," +
							s[i].getWordRepeatedPercentWSW() + "," +
							s[i].getConjunction() + "," +
							s[i].getNumber() + "," +
							s[i].getDeterminer() + "," +
							s[i].getPreposition() + "," +
							s[i].getAdjective() + "," +
							s[i].getNoun() + "," +
							s[i].getPronoun() + "," +
							s[i].getAdverb() + "," +
							s[i].getVerb() + "," +
							s[i].getInterjection() + "," +
							s[i].getForeign() + "," +
							s[i].getList() + "," +
							s[i].getPossessive() + "," +
							s[i].getParticle() + "," +
							s[i].getSymbol() + "," +
		
							s[i].getTotalCharacters() + "," +
							s[i].getTotalCharactersWSW() + "," +
							s[i].getCharPerWord() + "," +
							s[i].getCharPerWordWSW() + "," +
							s[i].getNotWhiteSpace() + "," +
							s[i].getNotWhiteSpaceWSW() + "," +
							s[i].isSpecialCharacter() + "," +
		
							label + 
							"\n"
					);
		}//end looping the sentences

		//setting output file path
		FileWriter arffFileWriter = new FileWriter("out/" + arffFile);
		BufferedWriter bw = new BufferedWriter(arffFileWriter);
		//writing the first two sections of arff file
		bw.write(		"@relation stylometry-lll" + "\n\n" +
						"@attribute fog_index numeric" + "\n" +
						"@attribute fog_index_WSW numeric" + "\n" +
						"@attribute monosyllabic_fog_index numeric" + "\n" +
						"@attribute monosyllabic_fog_index_WSW numeric" + "\n" +
						"@attribute inverse_fog_index numeric" + "\n" +
						"@attribute inverse_fog_index_WSW numeric" + "\n" +
						"@attribute forcast numeric" + "\n" +
						"@attribute forcast_WSW numeric" + "\n" +
						"@attribute smog_index numeric" + "\n" +
						"@attribute smog_index_WSW numeric" + "\n" +
						"@attribute fkri numeric" + "\n" +
						"@attribute fkri_WSW numeric" + "\n" +
						"@attribute flesch numeric" + "\n" +
						"@attribute flesch_WSW numeric" + "\n" +
						"@attribute complex_word numeric" + "\n" +
						"@attribute complex_word_percent numeric" + "\n" +
						"@attribute complex_word_WSW numeric" + "\n" +
						"@attribute complex_word_WSW_percent numeric" + "\n" +
						"@attribute monosyllabic_word numeric" + "\n" +
						"@attribute monosyllabic_word_percent numeric" + "\n" +
						"@attribute monosyllabic_word_WSW numeric" + "\n" +
						"@attribute monosyllabic_word_WSW_percent numeric" + "\n" +
						"@attribute word_length numeric" + "\n" +
						"@attribute word_length_WSW numeric" + "\n" +
						"@attribute syllable_count numeric" + "\n" +
						"@attribute syllables_count_WSW numeric" + "\n" +
						"@attribute long_sentence {true, false}" + "\n" +
						"@attribute short_sentence {true, false}" + "\n" +

						"@attribute word_count numeric" + "\n" +
						"@attribute function_word numeric" + "\n" +
						"@attribute function_word_percent numeric" + "\n" +
						"@attribute content_word numeric" + "\n" +
						"@attribute content_word_percent numeric" + "\n" +
						"@attribute named_entity numeric" + "\n" +
						"@attribute named_entity_percent numeric" + "\n" +
						"@attribute named_entity_WSW numeric" + "\n" +
						"@attribute named_entity_WSW_percent numeric" + "\n" +
						"@attribute alpha_numeric numeric" + "\n" +
						"@attribute alpha_numeric_percent numeric" + "\n" +
						"@attribute alpha_numeric_WSW numeric" + "\n" +
						"@attribute alpha_numeric_WSW_percent numeric" + "\n" +
						"@attribute verb numeric" + "\n" +
						"@attribute verb_percent numeric" + "\n" +
						"@attribute verb_WSW numeric" + "\n" +
						"@attribute verb_WSW_percent numeric" + "\n" +
						"@attribute acronym numeric" + "\n" +
						"@attribute acronym_percent numeric" + "\n" +
						"@attribute acronym_WSW numeric" + "\n" +
						"@attribute acronym_WSW_percent numeric" + "\n" +
						"@attribute semantic_word numeric" + "\n" +
						"@attribute semantic_word_percent numeric" + "\n" +
						"@attribute semantic_word_WSW numeric" + "\n" +
						"@attribute semantic_word_WSW_percent numeric" + "\n" +
						"@attribute alphabet_words numeric" + "\n" +
						"@attribute alphabet_words_percent numeric" + "\n" +
						"@attribute alphabet_words_WSW numeric" + "\n" +
						"@attribute alphabet_words_WSW_percent numeric" + "\n" +
						"@attribute numeric_words numeric" + "\n" +
						"@attribute numeric_words_percent numeric" + "\n" +
						"@attribute numeric_words_WSW numeric" + "\n" +
						"@attribute numeric_words_WSW_percent numeric" + "\n" +
						"@attribute lowercase_words numeric" + "\n" +
						"@attribute lowercase_words_percent numeric" + "\n" +
						"@attribute lowercase_words_WSW numeric" + "\n" +
						"@attribute lowercase_words_WSW_percent numeric" + "\n" +
						"@attribute uppercase_words numeric" + "\n" +
						"@attribute uppercase_words_percent numeric" + "\n" +
						"@attribute uppercase_words_WSW numeric" + "\n" +
						"@attribute uppercase_words_WSW_percent numeric" + "\n" +
						"@attribute long_word numeric" + "\n" +
						"@attribute long_word_percent numeric" + "\n" +
						"@attribute long_word_WSW numeric" + "\n" +
						"@attribute long_word_WSW_percent numeric" + "\n" +
						"@attribute unique_word numeric" + "\n" +
						"@attribute unique_word_percent numeric" + "\n" +
						"@attribute unique_word_WSW numeric" + "\n" +
						"@attribute unique_word_WSW_percent numeric" + "\n" +
						"@attribute repeated_word numeric" + "\n" +
						"@attribute repeated_word_percent numeric" + "\n" +
						"@attribute repeated_word_WSW numeric" + "\n" +
						"@attribute repeated_word_WSW_percent numeric" + "\n" +
						"@attribute pos_conjunction numeric" + "\n" +
						"@attribute pos_number numeric" + "\n" +
						"@attribute pos_determiner numeric" + "\n" +
						"@attribute pos_preposition numeric" + "\n" +
						"@attribute pos_adjective numeric" + "\n" +
						"@attribute pos_noun numeric" + "\n" +
						"@attribute pos_pronoun numeric" + "\n" +
						"@attribute pos_adverb numeric" + "\n" +
						"@attribute pos_verb numeric" + "\n" +
						"@attribute pos_interjection numeric" + "\n" +
						"@attribute pos_foreign numeric" + "\n" +
						"@attribute pos_list numeric" + "\n" +
						"@attribute pos_possessive numeric" + "\n" +
						"@attribute pos_particle numeric" + "\n" +
						"@attribute pos_symbol numeric" + "\n" +

						"@attribute character_count numeric" + "\n" +
						"@attribute character_count_WSW numeric" + "\n" +
						"@attribute character_per_word numeric" + "\n" +
						"@attribute character_per_word_WSW numeric" + "\n" +
						"@attribute character_without_space numeric" + "\n" +
						"@attribute character_without_space_WSW numeric" + "\n" +
						"@attribute special_char {true, false}" + "\n" +

						"@attribute class {positive, negative}" + "\n\n" +

						"@data" + "\n" +
						outputString
				);

		bw.close(); //close buffered writer
		System.out.println("DONE!");
		Classification classification = new Classification("out/" + arffFile);
		classification.setParameters();
		classification.classify();
		System.out.println(classification.toString());
	}//end main method
}//class ends