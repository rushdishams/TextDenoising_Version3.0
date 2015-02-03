package ca.uwo.csd.cogeng.textdenoising;
/**
 * Class to represent a directory
 * @author Rushdi Shams
 * @version 3.0 (Major optimization applied January 20 2015
 *
 */

public class Directory {
	//-----------------------------------------------------------------------
	//Instance variable
	//-----------------------------------------------------------------------
	private String directoryPath = "";

	/**
	 * Constructor sets the directory path
	 * @param path is the directory path
	 */
	public Directory(String path){
		this.directoryPath = path;
	}
	//-----------------------------------------------------------------------
	//Setter methods
	//-----------------------------------------------------------------------
	/**
	 * method to return the directory path
	 * @return the directory path
	 */
	public String getPath(){
		return this.directoryPath;
	}
}//end class