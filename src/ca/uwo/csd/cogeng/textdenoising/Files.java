package ca.uwo.csd.cogeng.textdenoising;
import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

/**
 * Class to represent a file.
 * @author Rushdi Shams
 * @version 3.0 (Major optimization applied January 20 2015
 *
 */

public class Files {
	//-----------------------------------------------------------------------
	//Instance variable
	//-----------------------------------------------------------------------
	private String fileContent = ""; //the document contents
	private String fileName = ""; // the document name
	//-----------------------------------------------------------------------
	//Setter methods
	//-----------------------------------------------------------------------
	/**
	 * Method to set the file names
	 * @param file is the name of the file
	 */
	public void setFile(String file){
		this.fileName = file;
	}
	/**
	 * Setting file contents to fileContent
	 * @param fileName is the name of the file
	 * @param dirName directory name
	 */
	public void setContent(String fileName, String dirName){
		String fullPath = dirName + "/" + fileName;
		File file = new File(fullPath); 
		try {
			this.fileContent = FileUtils.readFileToString(file);
		} catch (IOException e) {
			System.out.println("Error from setContent method in Files class: File not found!");
			e.printStackTrace();
		}
	}//end method
	//-----------------------------------------------------------------------
	//Getter methods
	//-----------------------------------------------------------------------
	/**
	 * Method to return fileName
	 * @return file name
	 */
	public String getFile(){	
		return this.fileName;
	}
	/**
	 * Getting file contents
	 * @return data in the file
	 */
	public String getContent(){
		return this.fileContent;
	}
}//end class 