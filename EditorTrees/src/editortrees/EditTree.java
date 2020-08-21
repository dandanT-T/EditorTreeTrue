package editortrees;

import java.util.*;

import editortrees.Node.Code;
import editortrees.Node.Everything;

// A height-balanced binary tree with rank that could be the basis for a text editor.

public class EditTree {

	final static Node NULL_NODE = new Node();
	public Node root;
	public int totalRotation;

	/**
	 * MILESTONE 1 Construct an empty tree
	 */
	public EditTree() {
		this.root = NULL_NODE;
		this.totalRotation = 0;
	}

	/**
	 * MILESTONE 1 Construct a single-node tree whose element is ch
	 * 
	 * @param ch
	 */
	public EditTree(char ch) {
		this.root = new Node(ch);
		this.totalRotation = 0;
	}

	/**
	 * MILESTONE 2 Make this tree be a copy of e, with all new nodes, but the same
	 * shape and contents.
	 * 
	 * @param e
	 */
	public EditTree(EditTree e) {
		// building new tree from debug string runs in O(n) time

		if (e.root == NULL_NODE) {
			this.root = NULL_NODE;
			return;
		}
		// getting debugString cost O(n)
		String dbString = e.toDebugString();
		dbString = dbString.substring(1, dbString.length() - 1);
		Stack<Node> st = new Stack<Node>();
		if (dbString.length() <= 3) {
			root = new Node(dbString.charAt(0));
			root.balance = Code.SAME;
			return;
		}
		st.push(NULL_NODE);
		String[] dbArray = dbString.split(", "); // split and put everything in to a stack with tag (like a reversed
													// inorder iterator)

		for (int i = dbArray.length - 1; i >= 0; i--) {
			String currentString = dbArray[i];
			char data = currentString.charAt(0);
			char rank = currentString.charAt(1);
			char balance = currentString.charAt(2);
			if (rank == '0' && balance == '=') {
				Node node = new Node(data);
				node.rank = Integer.parseInt(rank + "");
				node.balance = Code.SAME;
				st.push(node);
			} else if (rank == '0') {
				Node popNode = st.pop();
				Node node = new Node(data);
				node.rank = Integer.parseInt(rank + "");
				node.right = popNode;
				node.balance = Code.RIGHT;
				st.push(node);
			} else if (rank != '0') {
				if (rank == '1' && balance == '/') {
					Node popNodeLeft = st.pop();
					Node node = new Node(data);
					node.rank = Integer.parseInt(rank + "");
					node.left = popNodeLeft;
					node.balance = Code.LEFT;
					st.push(node);
				} else {
					Node popNodeLeft = st.pop();
					Node popNodeRight = st.pop();
					if (popNodeRight == NULL_NODE) {
						st.push(NULL_NODE);
					}
					Node node = new Node(data);
					node.rank = Integer.parseInt(rank + "");
					node.left = popNodeLeft;
					node.right = popNodeRight;
					if (balance == '/')
						node.balance = Code.LEFT;
					else if (balance == '\\')
						node.balance = Code.RIGHT;
					else
						node.balance = Code.SAME;
					st.push(node);
				}
			}
		}
		// st. pop here must be the very last element in the stack
		root = st.pop();

	}

	/**
	 * MILESTONE 3 Create an EditTree whose toString is s. This can be done in O(N)
	 * time, where N is the size of the tree (note that repeatedly calling insert()
	 * would be O(N log N), so you need to find a more efficient way to do this.
	 * 
	 * @param s
	 */
	public EditTree(String s) {
		char[] sArray = s.toCharArray();
		if (s.length() == 1) {
			this.root = new Node(s.charAt(0));
			return;
		}
		Node[] nodeArray = new Node[s.length()];
		for (int i = 0; i < s.length(); i++) {
			Node node = new Node();
			node.element = sArray[i];
			nodeArray[i] = node;
		}
		this.root = this.makeTreeWithString(nodeArray, 0, s.length() - 1, true);
	}

	private Node makeTreeWithString(Node[] nodeArray, int start, int end, boolean isLeft) {
		if (end < start) {
			return NULL_NODE;
		}

		int currentIndex = (start + end) / 2;

		if ((start + end) % 2 == 1) {
			nodeArray[currentIndex].balance = Code.RIGHT;
		} else {
			nodeArray[currentIndex].balance = Code.SAME;
		}
		if (isLeft) {
			nodeArray[currentIndex].rank = currentIndex;

		} else {
			nodeArray[currentIndex].rank = currentIndex - start;
		}

		if (currentIndex == start) {
			nodeArray[currentIndex].left = NULL_NODE;

			nodeArray[currentIndex].right = nodeArray[currentIndex + 1];
			nodeArray[currentIndex].right.balance = Code.SAME;

			nodeArray[currentIndex].right.left = NULL_NODE;
			nodeArray[currentIndex].right.right = NULL_NODE;
			nodeArray[currentIndex].rank = 0;
			nodeArray[currentIndex].balance = Code.RIGHT;
			return nodeArray[currentIndex];
		}
		if (currentIndex == end) {
			nodeArray[currentIndex].right = NULL_NODE;
			nodeArray[currentIndex].left = nodeArray[currentIndex - 1];
			nodeArray[currentIndex].left.balance = Code.SAME;
			nodeArray[currentIndex].left.left = NULL_NODE;
			nodeArray[currentIndex].left.right = NULL_NODE;
			nodeArray[currentIndex].rank = 0;
			nodeArray[currentIndex].balance = Code.SAME;
			return nodeArray[currentIndex];
		}
		if (currentIndex == start + 1 || currentIndex == end - 1) {
			nodeArray[currentIndex].left = nodeArray[currentIndex - 1];
			nodeArray[currentIndex].right = nodeArray[currentIndex + 1];
			nodeArray[currentIndex].left.balance = Code.SAME;
			nodeArray[currentIndex].right.balance = Code.SAME;
			nodeArray[currentIndex].left.left = NULL_NODE;
			nodeArray[currentIndex].left.right = NULL_NODE;
			nodeArray[currentIndex].right.left = NULL_NODE;
			nodeArray[currentIndex].right.right = NULL_NODE;
			nodeArray[currentIndex].rank = 1;
			nodeArray[currentIndex].balance = Code.SAME;
			return nodeArray[currentIndex];

		}

		nodeArray[currentIndex].left = makeTreeWithString(nodeArray, start, currentIndex - 1, true);
		nodeArray[currentIndex].right = makeTreeWithString(nodeArray, currentIndex + 1, end, false);

		return nodeArray[currentIndex];

	}

	/**
	 * MILESTONE 1 returns the total number of rotations done in this tree since it
	 * was created. A double rotation counts as two.
	 *
	 * @return number of rotations since this tree was created.
	 */
	public int totalRotationCount() {
		return this.totalRotation; // replace by a real calculation.
	}

	/**
	 * MILESTONE 1 return the string produced by an inorder traversal of this tree
	 */
	@Override
	public String toString() {

		if (root == NULL_NODE) {
			return "";
		}
		StringBuilder stringBuilder = new StringBuilder();
		root.toString(stringBuilder);
		String ouString = stringBuilder.toString();
		return ouString;

	}

	/**
	 * MILESTONE 1 This one asks for more info from each node. You can write it like
	 * the arraylist-based toString() method from the BinarySearchTree assignment.
	 * However, the output isn't just the elements, but the elements, ranks, and
	 * balance codes. Former CSSE230 students recommended that this method, while
	 * making it harder to pass tests initially, saves them time later since it
	 * catches weird errors that occur when you don't update ranks and balance codes
	 * correctly. For the tree with root b and children a and c, it should return
	 * the string: [b1=, a0=, c0=] There are many more examples in the unit tests.
	 * 
	 * @return The string of elements, ranks, and balance codes, given in a
	 *         pre-order traversal of the tree.
	 */
	public String toDebugString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("[");

		if (this.root != NULL_NODE) {
			root.toDebugString(stringBuilder);
			stringBuilder.deleteCharAt(stringBuilder.length() - 1);
			stringBuilder.deleteCharAt(stringBuilder.length() - 1);
		}
		stringBuilder.append("]");
		String ouString = stringBuilder.toString();
		return ouString;
	}

	/**
	 * MILESTONE 1
	 * 
	 * @param ch character to add to the end of this tree.
	 */
	public void add(char ch) {
		// Notes:
		// 1. Please document chunks of code as you go. Why are you doing what
		// you are doing? Comments written after the code is finalized tend to
		// be useless, since they just say WHAT the code does, line by line,
		// rather than WHY the code was written like that. Six months from now,
		// it's the reasoning behind doing what you did that will be valuable to
		// you!
		// 2. Unit tests are cumulative, and many things are based on add(), so
		// make sure you get this one correct.
		int size = root.size();
		Everything e = root.add(ch, size);
		root = e.node;
		this.totalRotation += e.totalRotationCount;

	}

	/**
	 * MILESTONE 1
	 * 
	 * @param ch  character to add
	 * @param pos character added in this inorder position
	 * @throws IndexOutOfBoundsException if pos is negative or too large for this
	 *                                   tree
	 */
	public void add(char ch, int pos) throws IndexOutOfBoundsException {
		Everything e = root.add(ch, pos);
		root = e.node;
		int trc = e.totalRotationCount;

		this.totalRotation += trc;

	}

	/**
	 * MILESTONE 1
	 * 
	 * @param pos position in the tree
	 * @return the character at that position
	 * @throws IndexOutOfBoundsException
	 */
	public char get(int pos) throws IndexOutOfBoundsException {
		return root.get(pos);
	}

	/**
	 * MILESTONE 1
	 * 
	 * @return the height of this tree
	 */
	public int height() {
		return root.height(); // replace by a real calculation.
	}

	/**
	 * MILESTONE 2
	 * 
	 * @return the number of nodes in this tree, not counting the NULL_NODE if you
	 *         have one.
	 */
	public int size() {
		return root.size(); // replace by a real calculation.
	}

	/**
	 * MILESTONE 2
	 * 
	 * @param pos position of character to delete from this tree
	 * @return the character that is deleted
	 * @throws IndexOutOfBoundsException
	 */
	public char delete(int pos) throws IndexOutOfBoundsException {
		// Implementation requirement:
		// When deleting a node with two children, you normally replace the
		// node to be deleted with either its in-order successor or predecessor.
		// The tests assume assume that you will replace it with the
		// *successor*.

		int size = root.size();
		if (pos < 0 || pos >= size) {
			throw new IndexOutOfBoundsException("OOOOOOOOOOOut!");
		}
		Everything e = root.delete(pos);
		root = e.node;
		this.totalRotation += e.totalRotationCount;
		;
		return e.targetDeleteNodeData; // replace by a real calculation.
	}

	/**
	 * MILESTONE 3, EASY This method operates in O(length*log N), where N is the
	 * size of this tree.
	 * 
	 * @param pos    location of the beginning of the string to retrieve
	 * @param length length of the string to retrieve
	 * @return string of length that starts in position pos
	 * @throws IndexOutOfBoundsException unless both pos and pos+length-1 are
	 *                                   legitimate indexes within this tree.
	 */
	public String get(int pos, int length) throws IndexOutOfBoundsException {
		int treeSize = this.size();
		if (pos > treeSize - 1 || pos + length - 1 > treeSize - 1) {
			throw new IndexOutOfBoundsException();
		}
		StringBuilder sb = new StringBuilder();
		root.get(sb, pos, length);
		return sb.toString();
	}

	/**
	 * MILESTONE 3, MEDIUM - SEE THE PAPER REFERENCED IN THE SPEC FOR ALGORITHM!
	 * Append (in time proportional to the log of the size of the larger tree) the
	 * contents of the other tree to this one. Other should be made empty after this
	 * operation.
	 * 
	 * @param other
	 * @throws IllegalArgumentException if this == other
	 */
	public void concatenate(EditTree other) throws IllegalArgumentException {
//		special cases
		if (this.equals(other)) {
			throw new IllegalArgumentException();
		}
		if (other.root == NULL_NODE) {
			return;
		}
		if (this.root == NULL_NODE) {
			this.root = other.root;
			other.root = NULL_NODE;
		}
		if (other.root == NULL_NODE) {
			return;
		}

//		first comparing the height of two trees, always attach shorter tree to taller tree
		int thisHeight = this.height();
		int otherHeight = other.height();
//		if left tree (this) is taller than right tree (other)
		if (thisHeight > otherHeight) {
			Everything deletedEverything = other.deleteReturnsEverything(0);

			char deletedData = deletedEverything.targetDeleteNodeData;
			boolean myHeightDecreased = deletedEverything.myHeightDecreased;

//			set up qNode and its balance code
			Node qNode = new Node(deletedData);
			qNode.right = other.root;
			if (myHeightDecreased) {
				qNode.balance = Code.LEFT;
			} else {
				qNode.balance = Code.SAME;
			}
			Everything rightConcatenate = this.root.rightConcatenate(qNode, thisHeight, otherHeight, 0);
			this.root = rightConcatenate.node;
			other.root = NULL_NODE;

		} else {
//			if left tree (this) is shorter than right tree (other)
			Everything deletedEverything = this.deleteReturnsEverything(this.size() - 1);
			char deletedData = deletedEverything.targetDeleteNodeData;
			boolean myHeightDecreased = deletedEverything.myHeightDecreased;

//			set up qNode and its balance code
			Node qNode = new Node(deletedData);
			System.out.println("deleteddata: "+deletedData);
			qNode.left = this.root;
			if (myHeightDecreased) {
				qNode.balance = Code.RIGHT;
			} else {
				qNode.balance = Code.SAME;
			}
			qNode.rank = this.root.size();

//			char deletedData = other.delete(0);
			Everything leftConcatenate = other.root.leftConcatenate(qNode, thisHeight, otherHeight, 0);
			this.root = leftConcatenate.node;
			other.root = NULL_NODE;
		}

	}

	public Everything deleteReturnsEverything(int pos) throws IndexOutOfBoundsException {
		int size = root.size();
		if (pos < 0 || pos >= size) {
			throw new IndexOutOfBoundsException("OOOOOOOOOOOut!");
		}
		Everything e = root.delete(pos);
		root = e.node;
		this.totalRotation += e.totalRotationCount;
		return e; // replace by a real calculation.
	}

	/**
	 * MILESTONE 3: DIFFICULT This operation must be done in time proportional to
	 * the height of this tree.
	 * 
	 * @param pos where to split this tree
	 * @return a new tree containing all of the elements of this tree whose
	 *         positions are >= position. Their nodes are removed from this tree.
	 * @throws IndexOutOfBoundsException
	 */
	public EditTree split(int pos) throws IndexOutOfBoundsException {
//		this.root.rank = this.root.left.size();
		int size = root.size();
		if (pos < 0 || pos >= size) {
			throw new IndexOutOfBoundsException();
		}
		Everything e = this.root.split(pos);
		EditTree newTree = new EditTree();
		if(e.splitRightTree.isEmpty()) {
			newTree.root =NULL_NODE;
		}else {
			newTree.root = e.splitRightTree.pop();			
		}
		
		if(e.splitLeftTree.isEmpty()) {
			this.root =NULL_NODE;
		}else {
			this.root = e.splitLeftTree.pop();			
		}
		
//		EditTree t3 = new EditTree(newTree.toString());
		return newTree;
	}

	/**
	 * MILESTONE 3: JUST READ IT FOR USE OF SPLIT/CONCATENATE This method is
	 * provided for you, and should not need to be changed. If split() and
	 * concatenate() are O(log N) operations as required, delete should also be
	 * O(log N)
	 * 
	 * @param start  position of beginning of string to delete
	 * 
	 * @param length length of string to delete
	 * @return an EditTree containing the deleted string
	 * @throws IndexOutOfBoundsException unless both start and start+length-1 are in
	 *                                   range for this tree.
	 */
	public EditTree delete(int start, int length) throws IndexOutOfBoundsException {
		if (start < 0 || start + length >= this.size())
			throw new IndexOutOfBoundsException(
					(start < 0) ? "negative first argument to delete" : "delete range extends past end of string");
		EditTree t2 = this.split(start);
		EditTree t3 = t2.split(length);
		this.concatenate(t3);
		return t2;
	}

	/**
	 * MILESTONE 3 Don't worry if you can't do this one efficiently.
	 * 
	 * @param s the string to look for
	 * @return the position in this tree of the first occurrence of s; -1 if s does
	 *         not occur
	 */
	public int find(String s) {
		String outString = this.toString();

		return outString.indexOf(s);
	}

	/**
	 * MILESTONE 3
	 * 
	 * @param s   the string to search for
	 * @param pos the position in the tree to begin the search
	 * @return the position in this tree of the first occurrence of s that does not
	 *         occur before position pos; -1 if s does not occur
	 */
	public int find(String s, int pos) {
		String outString = this.toString();
		return outString.indexOf(s, pos);
	}

	/**
	 * @return The root of this tree.
	 */
	public Node getRoot() {
		return this.root;
	}

}
