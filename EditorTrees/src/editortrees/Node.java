package editortrees;

import java.util.Stack;

// A node in a height-balanced binary tree with rank.
// Except for the NULL_NODE (if you choose to use one), one node cannot
// belong to two different trees.

public class Node {

	enum Code {
		SAME, LEFT, RIGHT;
		// Used in the displayer and debug string
		public String toString() {
			switch (this) {
			case LEFT:
				return "/";
			case SAME:
				return "=";
			case RIGHT:
				return "\\";
			default:
				throw new IllegalStateException();
			}
		}
	}

	/////////////////////////////////
	// Combined class for the everything: rank, balance code and ...
	public class Everything {
		Node node;
		int rank;
		Code balance;
		boolean myHeightIncreased;
		boolean myHeightDecreased;
		int totalRotationCount;
		char targetDeleteNodeData;
//		public boolean myHeightIncreases;
		Node realDeletedNode;
		public boolean foundTheNode;
		public Stack<Node> splitRightTree;
		public Stack<Node> splitLeftTree;

		Everything() {
			this.node = new Node();
			this.realDeletedNode = new Node();
			this.rank = 0;
			this.balance = Code.SAME;
			this.totalRotationCount = 0;
			this.myHeightIncreased = false;
			this.myHeightDecreased = false;
			this.targetDeleteNodeData = 0;
			this.foundTheNode = false;
			this.splitRightTree = new Stack<Node>();
			this.splitLeftTree = new Stack<Node>();
		}
	}

	/////////////////////////////////
	// Constructors of Node

	public Node() {
		this.element = 0;
		this.left = EditTree.NULL_NODE;
		this.right = EditTree.NULL_NODE;
	}

	public Node(char ch) {
		this.element = ch;
		this.left = EditTree.NULL_NODE;
		this.right = EditTree.NULL_NODE;
	}

	// The fields would normally be private, but for the purposes of this class,
	// we want to be able to test the results of the algorithms in addition to the
	// "publicly visible" effects

	char element;
	Node left, right; // subtrees
	int rank; // inorder position of this node within its own subtree.
	Code balance;
	// Node parent; // You may want this field.
	// Feel free to add other fields that you find useful

	// You will probably want to add several other methods

	// For the following methods, you should fill in the details so that they work
	// correctly
	public int height() {
		if (this == EditTree.NULL_NODE) {
			return -1;
		}

		if (this.balance == Code.LEFT) {
			return this.left.height() + 1;
		} else {
			return this.right.height() + 1;
		}
	}

	public int size() {
		if (this == EditTree.NULL_NODE) {
			return 0;
		}

		return this.rank + this.right.size() + 1;
	}

	public Everything add(char ch, int pos) {
		Everything e = new Everything();
		int currentRotations = 0;
		// always add when pos == 0
		if (this == EditTree.NULL_NODE) {
			if (pos != 0) {
				throw new IndexOutOfBoundsException("@@@@@@");
			} else {
				e.myHeightIncreased = true;
				e.node = new Node(ch);
				e.node.balance = Code.SAME;
				e.balance = e.node.balance;
				e.totalRotationCount = 0;
				return e;

			}
		}

		if (pos <= this.rank) {
			// going left and catch it using everything class
			Everything eOnLeft = this.left.add(ch, pos);
			this.rank++;// if a node go to current's left current's rank increases one
			this.left = eOnLeft.node;

			boolean myLeftHeightIncreased = eOnLeft.myHeightIncreased;
			Code myLeftBalanceCode = eOnLeft.balance;
			currentRotations = eOnLeft.totalRotationCount;

			if (myLeftHeightIncreased) {
				// current's left increases check balance code to determine which form of
				// rotation
				if (this.balance == Code.RIGHT) {
					this.balance = Code.SAME;
					e.node = this;
				} else if (this.balance == Code.SAME) {
					this.balance = Code.LEFT;
					e.myHeightIncreased = true;
					e.node = this;
					e.balance = e.node.balance;
				} else {
					// this is the case to rotate,check left child's balance code to determine SR
					// or DR
					if (myLeftBalanceCode == Code.LEFT) {
						e.totalRotationCount = currentRotations + 1;
						e.node = this.singleRightRotation();
						e.balance = e.node.balance;
//						return e;
					} else if (myLeftBalanceCode == Code.RIGHT) {
						// balance of left child in addition here should only be right
						e.totalRotationCount = currentRotations + 2;
						e.node = this.doubleRightRotation();
						e.balance = e.node.balance;
//						return e;
					} else {
						e.totalRotationCount = currentRotations;
					}
//					return e;
				}
			} else {
				e.node = this;
				e.totalRotationCount = currentRotations;
			}

		} else {
			// going right
			Everything eOnRight = this.right.add(ch, pos - rank - 1);
			this.right = eOnRight.node;

			boolean myRightHeightIncreased = eOnRight.myHeightIncreased;
			Code myRightBalanceCode = eOnRight.balance;
			currentRotations = eOnRight.totalRotationCount;
			if (myRightHeightIncreased) {
				// current's left increases check balance code to determine which form of
				// rotation
				if (this.balance == Code.LEFT) {
					this.balance = Code.SAME;
					e.node = this;
					e.balance = this.balance;
				} else if (this.balance == Code.SAME) {
					this.balance = Code.RIGHT;
					e.myHeightIncreased = true;
					e.node = this;
					e.balance = this.balance;
				} else {
					// this is the case to rotate,, check left child's balance code to determine SR
					// or DR
					if (myRightBalanceCode == Code.RIGHT) {
						e.totalRotationCount = currentRotations + 1;
						e.node = this.singleLeftRotation();
						e.balance = e.node.balance;
					} else if (myRightBalanceCode == Code.LEFT) {
						e.totalRotationCount = currentRotations + 2;
						// balance of left child in addition here should only be right
						e.node = this.doubleLeftRotation();
						e.balance = e.node.balance;
					} else {
						e.totalRotationCount = currentRotations;
					}
				}
			} else {

				e.node = this;
				e.totalRotationCount = currentRotations;

			}
		}
		return e;

	}

	private Node singleRightRotation() {
		// TODO Auto-generated method stub
		Node A = this;
		Node B = A.left;
		Node BR = B.right;

		// rotate
		B.right = A;
		A.left = BR;

		// Changing balance code and rank
		if (B.balance != Code.SAME) {
			A.balance = Code.SAME;
			B.balance = Code.SAME;
		}else {
			A.balance = Code.LEFT;
			B.balance = Code.RIGHT;
		}

		// change of rank 1: for addition only (maybe)
		A.rank -= (B.rank + 1);

		return B;
	}

	private Node singleLeftRotation() {
		Node A = this;
		Node B = A.right;
		Node BL = B.left;

		// rotate
		A.right = BL;
		B.left = A;

		// Changing balance code and rank
		A.balance = Code.SAME;
		B.balance = Code.SAME;

		// change of rank 1: for addition only (maybe)
		B.rank += (A.rank + 1);

		return B;
	}

	private Node doubleRightRotation() {
		// construct the tree
		Node A = this;
		Node B = A.left;
		Node C = B.right;
//		Node BL = B.left;
		Node CL = C.left;
		Node CR = C.right;
//		Node AR = A.right;

		// start rotation
		C.left = B;
		C.right = A;
		B.right = CL;
		A.left = CR;

//		change balance code
		if (CL == EditTree.NULL_NODE && CR == EditTree.NULL_NODE) {
			A.balance = Code.SAME;
			B.balance = Code.SAME;
		} else {
			if (C.balance == Code.LEFT) {
				A.balance = Code.RIGHT;
				B.balance = Code.SAME;

			} else {
				A.balance = Code.SAME;
				B.balance = Code.LEFT;

			}

		}
		C.balance = Code.SAME;

//		change ranks
		A.rank = A.rank - B.rank - 2 - C.rank;
		C.rank = C.rank + B.rank + 1;

		return C;
	}

	private Node doubleLeftRotation() {
//		construct the tree
		Node A = this;
		Node B = A.right;
		Node C = B.left;
//		Node BR = B.right;
		Node CL = C.left;
		Node CR = C.right;
//		Node AL = this.left;

//		start rotation
		C.left = A;
		C.right = B;
		B.left = CR;
		A.right = CL;

		if (CL == EditTree.NULL_NODE && CR == EditTree.NULL_NODE) {
			A.balance = Code.SAME;
			B.balance = Code.SAME;
		} else {
			if (C.balance == Code.LEFT) {
				A.balance = Code.SAME;
				B.balance = Code.RIGHT;

			} else {
				A.balance = Code.LEFT;
				B.balance = Code.SAME;

			}

		}
		C.balance = Code.SAME;

//		change ranks
		B.rank = B.rank - C.rank - 1;
		C.rank = C.rank + A.rank + 1;

//		return the new root
		return C;

	}

	public void toString(StringBuilder stringBuilder) {
		if (this == EditTree.NULL_NODE) {

			return;
		}

		this.left.toString(stringBuilder);
		stringBuilder.append(this.element);

		this.right.toString(stringBuilder);
		return;

	}

	public void toDebugString(StringBuilder stringBuilder) {
		if (this == EditTree.NULL_NODE) {
			return;
		}

		stringBuilder.append(this.element + "" + this.rank + "" + this.balance + ", ");

		this.left.toDebugString(stringBuilder);
		this.right.toDebugString(stringBuilder);
		return;

	}

	public char get(int pos) throws IndexOutOfBoundsException {
		if (this == EditTree.NULL_NODE) {
			throw new IndexOutOfBoundsException("!!");
		}
		if (pos < this.rank) {
			return this.left.get(pos);
		} else if (pos > this.rank) {
			return this.right.get(pos - this.rank - 1);
		} else {
			return this.element;
		}
	}

	public Everything delete(int pos) {
		Everything e = new Everything();
		if (pos < this.rank) {
			Everything leftDelete = this.left.delete(pos);
			boolean myLeftChildHasDecreasedHeight = leftDelete.myHeightDecreased;
			int currentRotationCount = leftDelete.totalRotationCount;
			e.targetDeleteNodeData = leftDelete.targetDeleteNodeData;
			this.left = leftDelete.node;
			this.rank--; // decrease rank if node on left is deleted
			e.node = this;

			// changing balance code and judge rotate or not
			if (myLeftChildHasDecreasedHeight) {
				if (this.balance == Code.SAME) {
					this.balance = Code.RIGHT;
				} else if (this.balance == Code.LEFT) {
					e.myHeightDecreased = true;
					this.balance = Code.SAME;

				} else {
					if (this.right.balance == Code.LEFT) {
						e.node = this.doubleLeftRotation();
						currentRotationCount += 2;
						e.myHeightDecreased = true;
					} else {
						e.node = this.singleLeftRotation();
						currentRotationCount += 1;
						e.myHeightDecreased = true;
					}
				}
			}
			e.totalRotationCount = currentRotationCount;
		} else if (pos > this.rank) {
			Everything rightDelete = this.right.delete(pos - this.rank - 1);
			boolean myRightChildHasDecreasedHeight = rightDelete.myHeightDecreased;
			int currentRotationCount = rightDelete.totalRotationCount;
			e.targetDeleteNodeData = rightDelete.targetDeleteNodeData;
			this.right = rightDelete.node;
			e.node = this;
			
			if(myRightChildHasDecreasedHeight) {
				if(this.balance == Code.SAME) {
					this.balance = Code.LEFT;
				}else if(this.balance == Code.RIGHT) {
					e.myHeightDecreased = true;
					this.balance = Code.SAME;
				}else {
					if(this.left.balance==Code.RIGHT) {
						e.node = this.doubleRightRotation();
						currentRotationCount+=2;
						e.myHeightDecreased=true;
					}else {
						e.node = this.singleRightRotation();
						currentRotationCount+=1;
						e.myHeightDecreased = (e.node.balance==Code.SAME);
					}
				}
			}else {
				//do nothing
			}
			
			
			e.totalRotationCount = currentRotationCount;
		}else {
			// pos == this.rank found the node to delete
			e = this.deleteTheLeafNodeHandleRotationAndGetTheTrueDeletedNodesData();
			e.targetDeleteNodeData = this.element;
		}
		return e;
	}

	private Everything deleteTheLeafNodeHandleRotationAndGetTheTrueDeletedNodesData() {
		Everything e = new Everything();
//		Node A = this;
		Node AL = this.left;
		Node AR = this.right;
		
		if(AL == EditTree.NULL_NODE && AR == EditTree.NULL_NODE) {
			e.node = EditTree.NULL_NODE;
			e.myHeightDecreased = true;
			
		}else if(AR == EditTree.NULL_NODE){
			Everything leftDeleteLeaf = this.left.findAndRemoveMaxAndRotationDownThere();
			boolean myLeftChildHasDecreasedHeight = leftDeleteLeaf.myHeightDecreased;
			int currentRotationCount = leftDeleteLeaf.totalRotationCount;
			e.targetDeleteNodeData = leftDeleteLeaf.targetDeleteNodeData;
			this.left = leftDeleteLeaf.node;
			this.rank--;
			this.element = leftDeleteLeaf.realDeletedNode.element;
			e.node = this;
			
			if(myLeftChildHasDecreasedHeight) {
				if (this.balance == Code.SAME) {
					this.balance = Code.RIGHT;
				} else if (this.balance == Code.LEFT) {
					e.myHeightDecreased = true;
					this.balance = Code.SAME;
//					e.node = this;
				} else {
					if (this.right.balance == Code.LEFT) {
						e.node = this.doubleLeftRotation();
						currentRotationCount += 2;
						e.myHeightDecreased = true;
					} else {
						e.node = this.singleLeftRotation();
						currentRotationCount += 1;
						if (e.node.balance != Code.SAME) {
							e.myHeightDecreased = false;
						} else {

							e.myHeightDecreased = true;
						}
					}
				}
			}else {
//				do nothing
			}
			e.totalRotationCount = currentRotationCount;
		}else {
			if(AL == EditTree.NULL_NODE) {
				e.node = this.right;
				e.myHeightDecreased = true;
			}else {
				Everything rightDeleteLeaf = AR.findAndRemoveMinAndRotationDownThere();
				boolean myRightChildHasDecreasedHeight = rightDeleteLeaf.myHeightDecreased;
				int currentRotationCount = rightDeleteLeaf.totalRotationCount;
				e.targetDeleteNodeData = rightDeleteLeaf.targetDeleteNodeData;
				this.right = rightDeleteLeaf.node;
				this.element = rightDeleteLeaf.realDeletedNode.element;	
				
				e.node = this;
				
				if (myRightChildHasDecreasedHeight) {
					if (this.balance == Code.SAME) {
						this.balance = Code.LEFT;
//						e.node = this;
					} else if (this.balance == Code.RIGHT) {
						e.myHeightDecreased = true;
						this.balance = Code.SAME;
//						e.node = this;
					} else {
						if (this.left.balance == Code.RIGHT) {
							e.node = this.doubleRightRotation();
							currentRotationCount += 2;
							e.myHeightDecreased = true;
						} else {
							e.node = this.singleRightRotation();
							currentRotationCount += 1;
							e.myHeightDecreased = true;
						}
					}
				}
				e.totalRotationCount = currentRotationCount;
			}
			
		}
		
		return e;
	}

	private Everything findAndRemoveMaxAndRotationDownThere() {
		Everything everything = new Everything();
		if (this.right == EditTree.NULL_NODE) {
			if(this.left!=EditTree.NULL_NODE) {
				everything.node = this.left;
			}else {
				everything.node = EditTree.NULL_NODE;				
			}
			everything.realDeletedNode = this;
			everything.myHeightDecreased = true;
		} else {
			Everything rightEverything = this.right.findAndRemoveMaxAndRotationDownThere();
			this.right = rightEverything.node;
			everything.node = this;
			everything.realDeletedNode = rightEverything.realDeletedNode;
			int currentRotationCount = rightEverything.totalRotationCount;
			boolean myRightChildHasDecreasedHeight = rightEverything.myHeightDecreased;
			if(myRightChildHasDecreasedHeight) {
				if (this.balance == Code.SAME) {
					this.balance = Code.LEFT;
				} else if (this.balance == Code.RIGHT) {
					everything.myHeightDecreased = true;
					this.balance = Code.SAME;
//					e.node = this;
				} else {
					if (this.left.balance == Code.RIGHT) {
						everything.node = this.doubleRightRotation();
						currentRotationCount += 2;
						everything.myHeightDecreased = true;
					} else {
						everything.node = this.singleRightRotation();
						currentRotationCount += 1;
						if (everything.node.balance != Code.SAME) {
							everything.myHeightDecreased = false;
						} else {

							everything.myHeightDecreased = true;
						}
					}
				}
			}else {
//				do nothing here
			}
			everything.totalRotationCount = currentRotationCount;
			
		}
		return everything;

	}

	private Everything findAndRemoveMinAndRotationDownThere() {
		Everything everything = new Everything();
		if (this.left == EditTree.NULL_NODE) {
			if(this.right!=EditTree.NULL_NODE) {
				everything.node = this.right;
			}else {
				everything.node = EditTree.NULL_NODE;				
			}
			everything.realDeletedNode = this;
			everything.myHeightDecreased = true;
		} else {
			Everything leftEverything = this.left.findAndRemoveMinAndRotationDownThere();
			this.rank--;
			this.left = leftEverything.node;
			everything.node = this;
			everything.realDeletedNode = leftEverything.realDeletedNode;
			int currentRotationCount = leftEverything.totalRotationCount;
			boolean myLeftChildHasDecreasedHeight = leftEverything.myHeightDecreased;
			if(myLeftChildHasDecreasedHeight) {
				if (this.balance == Code.SAME) {
					this.balance = Code.RIGHT;
				} else if (this.balance == Code.LEFT) {
					everything.myHeightDecreased = true;
					this.balance = Code.SAME;
//					e.node = this;
				} else {
					if (this.right.balance == Code.LEFT) {
						everything.node = this.doubleLeftRotation();
						currentRotationCount += 2;
						everything.myHeightDecreased = true;
					} else {
						everything.node = this.singleLeftRotation();
						currentRotationCount += 1;
						if (everything.node.balance != Code.SAME) {
							everything.myHeightDecreased = false;
						} else {

							everything.myHeightDecreased = true;
						}
					}
				}
			}else {
//				do nothing here
			}
			everything.totalRotationCount = currentRotationCount;
			
		}
		return everything;
	}

	public Everything get(StringBuilder sb, int pos, int length) {
		Everything e = new Everything();
		if (this == EditTree.NULL_NODE) {
			throw new IndexOutOfBoundsException("!!");
		}
		if (pos < this.rank) {
			Everything everythingOnLeft = this.left.get(sb, pos, length);
			if (everythingOnLeft.foundTheNode) {
				e.foundTheNode = true;
				if (sb.length() < length)
					sb.append(this.element);
				this.right.putYourselfToStringBuilder(sb, length);
				;
			}

		} else if (pos > this.rank) {
			Everything everythingOnRight = this.right.get(sb, pos - this.rank - 1, length);
			e.foundTheNode = everythingOnRight.foundTheNode;
		} else {
			e.foundTheNode = true;

			sb.append(this.element);
			this.right.putYourselfToStringBuilder(sb, length);

		}
		e.node = this;
		return e;
	}

	public void putYourselfToStringBuilder(StringBuilder sb, int length) {
		if (sb.length() >= length || this == EditTree.NULL_NODE) {
			return;
		}
		this.left.putYourselfToStringBuilder(sb, length);
		if (sb.length() < length)
			sb.append(this.element);
		this.right.putYourselfToStringBuilder(sb, length);
	}

	public Everything rightConcatenate(Node qNode, int thisHeight, int otherHeight, int depth) {
		Everything e = new Everything();
		if ((thisHeight - otherHeight - depth) == -1) {
			// pNode has no parent
			qNode.left = this;
			qNode.rank = this.size();
			e.node = qNode;
			return e;
		}

		if ((thisHeight - otherHeight - depth) == 0) {
			// pNode
			qNode.left = this;
			qNode.rank = this.size();
			e.node = qNode;
			return e;

		}

		if ((thisHeight - otherHeight - depth) == 1) {
			// pNode's parent
			Node originalPParentRight = this.right;
			this.right = qNode;
			qNode.left = originalPParentRight;
			qNode.rank = originalPParentRight.size();

			if (this.balance == Code.LEFT) {
				this.balance = Code.SAME;
			} else if (this.balance == Code.SAME) {
				this.balance = Code.RIGHT;
			} else {
				if (qNode.balance == Code.RIGHT) {
					e.node = this.doubleLeftRotation();
				} else {

					e.node = this.singleLeftRotation();
				}
				return e;
			}
			e.node = this;
			e.myHeightIncreased = true;
			return e;

		}

		Everything eRight = this.right.rightConcatenate(qNode, thisHeight, otherHeight, depth + 1);
		this.right = eRight.node;
		if (eRight.myHeightIncreased) {
			if (this.balance == Code.LEFT) {
				this.balance = Code.SAME;
			} else if (this.balance == Code.SAME) {
				this.balance = Code.RIGHT;
			} else {
				if (this.right.balance == Code.RIGHT) {
					e.node = this.doubleLeftRotation();
				} else {
					e.node = this.singleLeftRotation();
				}
				return e;
			}

		}
		e.node = this;

		return e;
	}

	public Everything leftConcatenate(Node qNode, int thisHeight, int otherHeight, int depth) {
		Everything e = new Everything();
		if ((otherHeight - thisHeight - depth) + 1 == 0) {// pNode has no parent
			qNode.right = this;
			e.node = qNode;
			return e;
		}

		if ((otherHeight - thisHeight - depth) == 0) {// pNode
			qNode.right = this;
			e.node = qNode;
			return e;

		}

		if ((otherHeight - thisHeight - depth) == 1) {// pNode's parent
			Node originalPParentRight = this.left;
			this.left = qNode;
			qNode.right = originalPParentRight;
			this.rank = qNode.size();
			if (this.balance == Code.RIGHT) {
				this.balance = Code.SAME;
			} else if (this.balance == Code.SAME) {
				this.balance = Code.LEFT;
			} else {
				if (qNode.balance == Code.LEFT) {
					e.node = this.doubleRightRotation();
				} else {
					e.node = this.singleRightRotation();
				}
				return e;
			}
			e.node = this;

			e.myHeightIncreased = true;
			return e;

		}

		Everything eLeft = this.left.leftConcatenate(qNode, thisHeight, otherHeight, depth + 1);
		if (eLeft.myHeightIncreased) {
			if (this.balance == Code.RIGHT) {
				this.balance = Code.SAME;
			} else if (this.balance == Code.SAME) {
				this.balance = Code.LEFT;
			} else {
				if (this.right.balance == Code.LEFT) {
					e.node = this.doubleRightRotation();
				} else {
					e.node = this.singleRightRotation();
				}
				return e;
			}

		}
		e.node = this;

		return e;
	}
	
	
	public Everything split(int pos) {
		Everything e = new Everything();
		if(pos==this.rank) {
			e.splitLeftTree.push(this.left);
			this.left = EditTree.NULL_NODE;
			e.splitRightTree.push(this);
		}else if (pos < this.rank) {
			
			Everything everythingFromLeft = this.left.split(pos); 
			if(everythingFromLeft.splitRightTree.isEmpty()) {
				everythingFromLeft.splitRightTree.push(this);
				e.splitRightTree = everythingFromLeft.splitRightTree;
				e.splitLeftTree = everythingFromLeft.splitLeftTree;
				
			}else {
				Node newTreeNode = everythingFromLeft.splitRightTree.pop();
//				System.out.println("size: "+everythingFromLeft.splitRightTree.size());
				this.left = newTreeNode;
				everythingFromLeft.splitRightTree.push(this);
				e.splitRightTree = everythingFromLeft.splitRightTree;
				e.splitLeftTree = everythingFromLeft.splitLeftTree;
			}
			return e;
			
		} else if (pos > this.rank) {
			Everything everythingFromRight = this.right.split(pos-this.rank-1); 
			if(everythingFromRight.splitLeftTree.isEmpty()) {
				everythingFromRight.splitLeftTree.push(this);
				e.splitLeftTree = everythingFromRight.splitLeftTree;
				e.splitRightTree = everythingFromRight.splitRightTree;
			}else {
				Node oldTreeNode = everythingFromRight.splitLeftTree.pop();
				this.right = oldTreeNode;
				everythingFromRight.splitLeftTree.push(this);
				e.splitLeftTree = everythingFromRight.splitLeftTree;
				e.splitRightTree = everythingFromRight.splitRightTree;
			}
			return e;
		}
		return e;
	}

}