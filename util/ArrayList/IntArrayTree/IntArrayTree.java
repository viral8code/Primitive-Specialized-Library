final class IntArrayTree{
	private static final java.util.concurrent.ThreadLocalRandom rm;
	static{
		rm = java.util.concurrent.ThreadLocalRandom.current();
	}
	private Node root;
	private int size;
	public final int illegalValue;

	public IntArrayTree(){
		this(-1);
	}

	public IntArrayTree(int illegalValue){
		size = 0;
		root = null;
		this.illegalValue = illegalValue;
	}

	final private class Node{
		private int value;
		private int size;
		private boolean reversed;
		private Node left, right, parent;
		public Node(final Node p,final int v){
			value = v;
			parent = p;
			size = 1;
			reversed = false;
		}
	}

	public void add(final int x){
		if(root==null)
			root = new Node(null,x);
		else{
			Node par = getLastNode(root);
			par.right = new Node(par,x);
			fix(par);
		}
		++size;
	}

	public int add(int index,final int x){
		Node now = root;
		while(true){
			assert now!=null;
			reverseCheck(now);
			final int ls = now.left!=null?now.left.size:0;
			if(index<ls){
				now = now.left;
			}
			else if(ls<index){
				now = now.right;
				index -= ls+1;
			}
			else{
				Node par = getFirstNode(now);
				par.left = new Node(par,x);
				fix(par);
				break;
			}
		}
		return x;
	}

	public int get(int index){
		if(index<0||size<=index){
			throw new IndexOutOfBoundsException();
		}
		return getNode(index).value;
	}

	private Node getNode(int index){
		Node now = root;
		while(true){
			assert now!=null;
			reverseCheck(now);
			final int ls = now.left!=null?now.left.size:0;
			if(index<ls){
				now = now.left;
			}
			else if(ls<index){
				now = now.right;
				index -= ls+1;
			}
			else{
				return now;
			}
		}
	}

	public boolean remove(final int index){
		final Node n = getNode(index);
		--size;
		delete(n);
		return true;
	}

	private void delete(final Node node){
		reverseCheck(node);
		if(node!=null){
			if(node.left==null&&node.right==null){
				if(node.parent!=null){
					if(node.parent.left==node){
						node.parent.left = null;
					}
					else{
						node.parent.right = null;
					}
					fix(node.parent);
				}
				else{
					root = null;
				}
				node.parent = null;
			}
			else{
				if(node.left!=null&&node.right!=null){
					final Node rep = getFirstNode(node.right);
					node.value = rep.value;
					delete(rep);
				}
				else{
					final Node rep = node.left!=null?node.left:node.right;
					rep.parent = node.parent;
					if(node.parent!=null){
						if(node.parent.left==node){
							node.parent.left = rep;
						}
						else{
							node.parent.right = rep;
						}
						fix(node.parent);
					}
					else{
						root = rep;
					}
					node.parent = null;
				}
			}
		}
	}

	public int getFirst(){
		if(root==null){
			return illegalValue;
		}
		return getFirstNode(root).value;
	}

	private Node getFirstNode(Node node){
		Node par = null;
		while(node!=null){
			reverseCheck(node);
			par = node;
			node = par.left;
		}
		return par;
	}

	public int getLast(){
		if(root==null){
			return illegalValue;
		}
		return getLastNode(root).value;
	}

	private Node getLastNode(Node node){
		Node par = null;
		while(node!=null){
			reverseCheck(node);
			par = node;
			node = par.right;
		}
		return par;
	}

	public int pollFirst(){
		if(root==null){
			return illegalValue;
		}
		--size;
		final Node min = getFirstNode(root);
		delete(min);
		return min.value;
	}

	public int pollLast(){
		if(root==null){
			return illegalValue;
		}
		--size;
		final Node max = getLastNode(root);
		delete(max);
		return max.value;
	}

	public void reverse(int l,int r){
		if(l>=r)
			return;
		Node subRoot = root;
		reverseCheck(root);
		int sumL = 0;
		while(sumL<l){
			reverseCheck(subRoot);
			final int ls = subRoot.left!=null?subRoot.left.size:0;
			if(sumL+ls>=l){
				rotateR(subRoot);
				subRoot = subRoot.parent;
			}
			else{
				sumL += ls+1;
				subRoot = subRoot.right;
			}
		}
		int sumR = size-1;
		while(r<sumR){
			reverseCheck(subRoot);
			final int rs = subRoot.right!=null?subRoot.right.size:0;
			if(sumR-rs<=r){
				rotateL(subRoot);
				subRoot = subRoot.parent;
			}
			else{
				sumR -= rs+1;
				subRoot = subRoot.left;
			}
		}
		if(subRoot!=null){
			subRoot.reversed ^= true;
			fix(subRoot);
		}
	}

	private void reverseCheck(Node node){
		if(node!=null&&node.reversed){
			Node temp = node.left;
			node.left = node.right;
			node.right = temp;
			node.reversed = false;
			if(node.left!=null)
				node.left.reversed ^= true;
			if(node.right!=null)
				node.right.reversed ^= true;
		}
	}

	public void clear(){
		root = null;
		size = 0;
	}

	public boolean isEmpty(){
		return size==0;
	}

	public int size(){
		return size;
	}

	public int[] toArray(){
		int[] ans = new int[size];
		toArray(root,ans,0);
		return ans;
	}

	private void toArray(Node now,int[] ans,int index){
		if(now==null)
			return;
		reverseCheck(now);
		toArray(now.left,ans,index);
		ans[index+(now.left!=null?now.left.size:0)] = now.value;
		toArray(now.right,ans,index+(now.left!=null?now.left.size:0)+1);
	}

	@Override
	public String toString(){
		return java.util.Arrays.toString(toArray());
	}

	@Override
	public boolean equals(final Object o){
		if(o instanceof final IntArrayTree tree){
			if(size!=tree.size()){
				return false;
			}
			final int[] ans1 = toArray();
			final int[] ans2 = tree.toArray();
			return java.util.Arrays.equals(ans1,ans2);
		}
		return false;
	}

	/*
	 * 以下、平衡用メソッド
	 */
	private void fix(Node node){
		while(node!=null&&node.parent!=null){
			if((rm.nextInt()&3)==0){
				if(node.parent.left==null&&node.parent.right!=null)
					rotateL(node.parent);
				else if(node.parent.left!=null&&node.parent.right==null)
					rotateR(node.parent);
				else{
					rotateBySize(node.parent);
				}
			}
			setStates(node);
			node = node.parent;
		}
		setStates(root);
	}

	private void rotateBySize(Node node){
		if(node.left.size*2<node.right.size)
			rotateL(node);
		else if(node.left.size>node.right.size*2)
			rotateR(node);
	}

	private void rotateR(final Node node){
		reverseCheck(node);
		reverseCheck(node.left);
		final Node temp = node.left;
		node.left = temp.right;
		if(node.left!=null){
			node.left.parent = node;
		}
		temp.right = node;
		temp.parent = node.parent;
		if(node.parent!=null){
			if(node.parent.left==node){
				node.parent.left = temp;
			}
			else{
				node.parent.right = temp;
			}
		}
		else{
			root = temp;
		}
		node.parent = temp;
		setStates(node);
		setStates(temp);
	}

	private void rotateL(final Node node){
		reverseCheck(node);
		reverseCheck(node.right);
		final Node temp = node.right;
		node.right = temp.left;
		if(node.right!=null){
			node.right.parent = node;
		}
		temp.left = node;
		temp.parent = node.parent;
		if(node.parent!=null){
			if(node.parent.left==node){
				node.parent.left = temp;
			}
			else{
				node.parent.right = temp;
			}
		}
		else{
			root = temp;
		}
		node.parent = temp;
		setStates(node);
		setStates(temp);
	}

	private void setStates(final Node node){
		final int ls = node.left!=null?node.left.size:0;
		final int rs = node.right!=null?node.right.size:0;
		node.size = ls+rs+1;
	}
}
