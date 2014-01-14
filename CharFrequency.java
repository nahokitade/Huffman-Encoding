
/**
 * Class CharFrequency.java
 * Object to store character and frequency pair. 
 * @author nahokitade
 *
 */
public class CharFrequency{
	private Character myChar;
	private Integer myFreq;
	
	/**
	 * Constructor of CharFrequency object.
	 * @param character character to store
	 * @param frequency frequency to store
	 */
	public CharFrequency(Character character, Integer frequency){
		// assign instance variables
		myChar = character;
		myFreq = frequency;
	}
	
	/**
	 * getter method for the character
	 * @return my character
	 */
	public Character getChar(){
		return myChar;
	}
	
	/**
	 * getter method for the frequency
	 * @return my frequency
	 */
	public Integer getFreq(){
		return myFreq;
	}
	
	/**
	 * simple to string method.
	 */
	public String toString(){
		String string = myChar + ":" + Integer.toString(myFreq);
		return string;
	}
}