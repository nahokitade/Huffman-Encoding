import java.io.*;
import java.util.*;
import javax.swing.JFileChooser;

/**
 * Class HuffmanEncoding.java
 * Program to run huffman encoding.
 * @author nahokitade
 */
public class HuffmanEncoding{
	
	
	/**
	 * creates and returns a frequency table represented as a map for each of the characters of the chosen file
	 * @param inputPathName Path name of the input file chosen to compress
	 * @return Map<Character, Integer> representing a frequency table for each of the characters of the file.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static Map<Character, Integer> generateFreqTable(String inputPathName) throws FileNotFoundException, IOException{
		// creates bufferedReader for the file chosen, and creates an empty frequency table.
		BufferedReader input =  new BufferedReader(new FileReader(inputPathName));
		Map<Character, Integer> freqTable = new HashMap<Character, Integer>();
		Character character;
		int intChar; 
		try{
			while((intChar = input.read()) != -1){ //loops until end of the file
				character = (char) intChar; // casts read character into a char. 
				//if character is contained as a key in the frequency table, just increment the frequency by 1.
				if (freqTable.containsKey(character)){ 
					Integer frequency = freqTable.get(character);
					frequency ++;
					freqTable.put(character, frequency);
				}
				//if character is not contained yet, put that character and the new frequency, 1.
				else{
					freqTable.put(character, 1);
				}
			}
			return freqTable;
		}
		finally{
			// close the file no matter what.
			input.close();
		}
	}
	
	/**
	 * Creates and returns a priority queue containing singleton trees for all the characters in the 
	 * frequency table.
	 * @param freqTable Frequency table of the characters of a file chosen.
	 * @return priority queue containing singleton trees for all the characters in the frequency table.
	 */
	private static PriorityQueue<BinaryTree<CharFrequency>> singletonTree(Map<Character, Integer> freqTable){
		// creates priority queue, a set containing all the characters in the frequency table, and a iterator
		//that iterates through said set.
		PriorityQueue<BinaryTree<CharFrequency>> PriorQueSingletonTree = 
				new PriorityQueue<BinaryTree<CharFrequency>>(1, new TreeComparator()); // compares tree with TreeComparator()
		Set<Character> characters = freqTable.keySet();
		Iterator<Character> characterIter = characters.iterator();
		// loop though all the characters in the frequency table.
		while (characterIter.hasNext()){
			Character currentCharacter = characterIter.next();
			// make the character and its frequency into a charFrequency object.
			CharFrequency currentCharFrequency = 
					new CharFrequency(currentCharacter, freqTable.get(currentCharacter));
			// make a singleton tree with that created CharFrequency object.
			BinaryTree<CharFrequency> singletonTree = new BinaryTree<CharFrequency>(currentCharFrequency);
			// add that singleton tree into the priority queue. 
			PriorQueSingletonTree.add(singletonTree);
		}
		return PriorQueSingletonTree;
	}
	
	/**
	 * Creates a tree that can be used to create the most efficient 0, 1 code for the file chosen.
	 * @param singletonTree priority queue containing singleton trees for all the characters in the frequency table.
	 * @return a code tree (binary tree) that can be used to create the most efficient 0, 1 code for the file chosen.
	 */
	private static BinaryTree<CharFrequency> codeTree(PriorityQueue<BinaryTree<CharFrequency>> singletonTree){
		// go through loop until there is only 1 singletonTree left in the priority queue.
		while (singletonTree.size() > 1){
			//pick out and store the two smallest singleton trees (smallest being the one with the smallest frequency)
			BinaryTree<CharFrequency> smallest1 = singletonTree.poll();
			Integer freq1 = smallest1.getValue().getFreq();
			BinaryTree<CharFrequency> smallest2 = singletonTree.poll();
			Integer freq2 = smallest2.getValue().getFreq();
			// get the frequency of those two trees, and add the frequencies to calculate the frequency of the root 
			// that combines these two trees together.  
			Integer addedFreq = freq1 + freq2;
			// make a new tree by combining the two trees with the appropriate root.
			BinaryTree<CharFrequency> newTree = new BinaryTree<CharFrequency>(new CharFrequency(null, addedFreq), 
					smallest1, smallest2);
			//Add this tree into the priority queue. 
			singletonTree.add(newTree);
		}
		// return the head of that priority queue because there is only one left anyway.
		return singletonTree.poll();
	}
	
	/**
	 * creates and returns a map containing the character and its appropriate/ most efficient code. 
	 * @param charCodePair stores the character and code pair. 
	 * @param codeTree the tree that can be used to create the most efficient 0, 1 code for the file chosen.
	 * @param pathSoFar the code written for the character so far.
	 */
	private static Map<Character, String> retrieveCode(Map<Character, String> charCodePair, BinaryTree<CharFrequency> codeTree, String pathSoFar){
		if (codeTree == null){ //happens at the boundary if the file chosen was empty.
			return null; //if the file is empty, there is no need for any codes, so return null.
		}
		if (codeTree.isLeaf()){ // base case. put in the character and code pair into the map if we hit the leaf. 
			charCodePair.put(codeTree.getValue().getChar(), pathSoFar);
		}
		else{
			// call the method recursively for the left after adding "0" to path so far.
			if(codeTree.hasLeft()){
				pathSoFar += "0";
				retrieveCode(charCodePair, codeTree.getLeft(), pathSoFar);
				//Every time it returns, we want to take off the last digit
					pathSoFar = pathSoFar.substring(0, pathSoFar.length() - 1);
			}
			// call the method recursively for the left after adding "1" to path so far.
			if(codeTree.hasRight()){
				pathSoFar += "1";
				retrieveCode(charCodePair, codeTree.getRight(), pathSoFar);
				//Every time it returns, we want to take off the last digit.
				pathSoFar = pathSoFar.substring(0, pathSoFar.length() - 1);
			}
		}
		return charCodePair;
	}
	
	/**
	 * Method to compress a file given all its needed parameters.
	 * @param codeMap map containing the appropriate character code pair for the file chosen.
	 * @param inputPathName the path of the input
	 * @param compressedPathName the path if the compressed output
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static void compressFile(Map<Character, String> codeMap, String inputPathName, String compressedPathName) throws FileNotFoundException, IOException{
		// read the create a bufferedreader and bufferedbitwriter for the input and output. 
		BufferedReader input =  new BufferedReader(new FileReader(inputPathName));
		BufferedBitWriter bitOutput = new BufferedBitWriter(compressedPathName);
		int intChar;
		try{
			// loop through until the end of the input. 
			while((intChar = input.read()) != -1){
				Character character = ((char) intChar); // cast to char from an int.
				String code = codeMap.get(character); // get that character's code from the codeMap.
				for (int i = 0; i < code.length(); i++){ // iterate through the code
					int codeBitInt = code.charAt(i) - '0'; // make each character of the code into an int
					bitOutput.writeBit(codeBitInt); //write that int (0 or 1s) out into a file.
				}
			}
		}
		//close the files no matter what.
		finally{
			input.close();
			bitOutput.close();
		}
	}
	
	/**
	 * Decompresses a compressed file given its codeTree.
	 * @param codeTree a code tree (binary tree) that can be used to create the most efficient 0, 1 code for the file chosen.
	 * @param compressedPathName path of the compressed file
	 * @param decompressedPathName path of the decompressed file
	 * @throws IOException
	 */
	private static void decompressFile(BinaryTree<CharFrequency> codeTree, String compressedPathName, String decompressedPathName) throws IOException{
		// creates the bufferedbitreader and bufferedwriter for the compressed and decompressed files.
		BufferedBitReader bitInput = new BufferedBitReader(compressedPathName);
		BufferedWriter output = new BufferedWriter(new FileWriter(decompressedPathName));
		// initialize the binary tree that will act an an iterator for the codeTree.
		BinaryTree<CharFrequency> codeTreeIter = codeTree;
		int nextBit;
		try{
			// loop until the end of the input file.
			while ((nextBit = bitInput.readBit()) != -1){
				// applies to a special case when the original file contained only 1 type of character.
				if (codeTree.size() == 1){
					//write that 1 type of character.
					output.write(codeTree.getValue().getChar());
					// avoids null pointer exception for the next if statement, "if (nextBit == 0)"
					codeTreeIter = codeTree;
				}
				else{ 
					//if we havent hit the leaf, iterate to the left if the next bit is 0, and iterate to the right
					// if the next bit is 1.
					if (nextBit == 0){
						codeTreeIter = codeTreeIter.getLeft();
					}
					else if (nextBit == 1){
						codeTreeIter = codeTreeIter.getRight();
					}
					// if it is not that special case, and the iterator hits a leaf, write out the character stored at
					// that node.
					if (codeTreeIter.isLeaf()){
						output.write(codeTreeIter.getValue().getChar());
						// make codeTreeIter point to the root of the codeTree again.
						codeTreeIter = codeTree;
					}
				}
			}
		}
		// close the files no matter what.
		finally{
			output.close();
			bitInput.close();
		}
	}
	
	/**
	 * Given code to get the file path.
	 * @return file path chosen.
	 */
	private static String getFilePath() {
	   //Create a file chooser
	   JFileChooser fc = new JFileChooser();
	    
	   int returnVal = fc.showOpenDialog(null);
	   if(returnVal == JFileChooser.APPROVE_OPTION)  {
	     File file = fc.getSelectedFile();
	     String pathName = file.getAbsolutePath();
	     return pathName;
	   }
	   else
	     return "";
	  }
	
	/**
	 * runs huffman encoding of a file using the private methods implemented in this class.
	 * @param inputPath the path of the input file to compress.
	 */
	public static void runHuffmanEncoding(String inputPath){
		try{
			BinaryTree<CharFrequency> codeTree; // variable that stores reference to the code tree.
			// make the frequency table
			Map<Character, Integer> freqTable = generateFreqTable(inputPath);
			// make the singleton tree priority queue using that frequency table
			PriorityQueue<BinaryTree<CharFrequency>> singletonTree = singletonTree(freqTable);
			// special case when there is only one type of character used.
			if (freqTable.size() == 1){
				// make the codeTree a singleton tree of that one character and its frequency.
				Set<Character> characters = freqTable.keySet();
				Iterator<Character> characterIter = characters.iterator();
				Character singleCharacter = characterIter.next();
				CharFrequency singleCharFrequency = 
						new CharFrequency(singleCharacter, freqTable.get(singleCharacter));
				codeTree = new BinaryTree<CharFrequency>(singleCharFrequency);
			}
			else{
				// if not that special case, make the code tree through the codeTree method using that priority
				// queue of the singleton trees.
				codeTree = codeTree(singletonTree);
			}
			String pathSoFar; 
			// if the file was empty, pathSoFar should be initialized to an empty string.
			if (codeTree == null){
				pathSoFar = "";
			}
			// if the file is that special case where there is only one character, the code for that character
			// will be "0"
			else if (codeTree.size() == 1){
				pathSoFar = "0";
			}
			//other than that, the pathSoFar starts as an empty string.
			else{
				pathSoFar = "";
			}
			// create new initial empty hashmap to pass into retrieveCode method.
			HashMap<Character, String> initCodeMap = new HashMap<Character, String>();
			// make the code map
			Map<Character, String> codeMap = retrieveCode(initCodeMap, codeTree, pathSoFar);
			//compressed path name is simply the input name with "_compressed" at the end.
			String compressedPathName = inputPath.substring(0, inputPath.length() - 4) + "_compressed";
			// compress the file
			compressFile(codeMap, inputPath, compressedPathName);
		  //decompressed path name is simply the input name with "_decompressed" at the end.
			String decompressedFileName = inputPath.substring(0, inputPath.length() - 4) + "_decompressed";
			//decompress the file
			decompressFile(codeTree, compressedPathName, decompressedFileName);
		}
		//catch the two exceptions when either of them are thrown by any of the methods called in the 
		//try block.
		catch (FileNotFoundException e) {
			//if we have a file not found exception, instruct user to pick another file.
			System.err.println("The file was not found. Please run the program again and with another file.");
		}
		catch (IOException e) {
			//there's something wrong with the input or output file.
			System.err.println("There was an error with the file.");
			// print out the stacktrace of the error.
			e.printStackTrace();
		}
	}
	
	/**
	 * main method that asks the user to pick a file, and runs huffman encoding on that file.
	 * @param args
	 */
	public static void main(String [] args){
		//ask the user to choose a file and run huffman encoding on that file.
		String inputPath = getFilePath();
		runHuffmanEncoding(inputPath);
	}
}