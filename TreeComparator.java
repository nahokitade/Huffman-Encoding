import java.util.Comparator;

/**
 * Class TreeComparator.java
 * class that is used to compare the trees in HuffmanEncoding.java.
 * @author nahokitade
 *
 */
public class TreeComparator implements Comparator<BinaryTree<CharFrequency>>{

	/**
	 * compares the trees based on the frequency stored in their CharFrequency object data. 
	 */
	public int compare(BinaryTree<CharFrequency> tree1, BinaryTree<CharFrequency> tree2) {
		// compare the frequencies to return the appropriate signed numbers.
		if (tree1.getValue().getFreq() < tree2.getValue().getFreq()){
			return -1;
		}
		else if (tree1.getValue().getFreq() > tree2.getValue().getFreq()){
			return 1;
		}
		// if the frequency is the same, return 0.
		else{
			return 0;
		}
	}
	
}