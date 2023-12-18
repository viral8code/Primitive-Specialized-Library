final class TreeMultiLong{
	private Node root;
	private long size;
	private int uniqueSize;
	private int hash;

	public TreeMultiLong(){
		size = 0;
		uniqueSize = 0;
		root = null;
		hash = 0;
	}

	static final private class Node{
		long value;
		long count, size;
		int height;
		Node left, right, parent;

		public Node(final Node p,final long v,final long c){
			value = v;
			parent = p;
			count = c;
			height = 1;
			size = c;
		}
	}

	public void add(final long x){
		if(root==null){
			root = new Node(null,x,1);
			++uniqueSize;
		}
		else{
			Node par;
			Node now = root;
			boolean bool = true;
			do{
				par = now;
				if(x<now.value){
					now = now.left;
				}
				else if(x>now.value){
					now = now.right;
				}
				else{
					bool = false;
					++now.count;
					break;
				}
			}
			while(now!=null);
			if(bool){
				++uniqueSize;
				if(x<par.value){
					par.left = new Node(par,x,1);
				}
				else{
					par.right = new Node(par,x,1);
				}
			}
			fix(par);
		}
		++size;
		hash ^= (int)x;
	}

	public void add(final long x,final long sum){
		if(root==null){
			root = new Node(null,x,sum);
			++uniqueSize;
		}
		else{
			Node par;
			Node now = root;
			boolean bool = true;
			do{
				par = now;
				if(x<now.value){
					now = now.left;
				}
				else if(x>now.value){
					now = now.right;
				}
				else{
					bool = false;
					now.count += sum;
					fix(now);
					break;
				}
			}
			while(now!=null);
			if(bool){
				++uniqueSize;
				if(x<par.value){
					par.left = new Node(par,x,sum);
				}
				else{
					par.right = new Node(par,x,sum);
				}
				fix(par);
			}
		}
		size += sum;
		if(sum%2==1){
			hash ^= (int)x;
		}
	}

	public long get(long index){
		if(root==null||size<=index){
			throw new NullPointerException();
		}
		Node now = root;
		while(true){
			assert now!=null;
			final long ls = now.left!=null?now.left.size:0;
			if(index<ls){
				now = now.left;
			}
			else if(ls+now.count<=index){
				index -= ls+now.count;
				now = now.right;
			}
			else{
				break;
			}
		}
		return now.value;
	}

	public boolean remove(final long x){
		final Node n = getNode(x);
		if(n==null){
			return false;
		}
		--size;
		hash ^= x;
		delete(n);
		return true;
	}

	public long remove(final long x,final long sum){
		final Node n = getNode(x);
		if(n==null){
			return 0;
		}
		final long ans = Math.min(sum,n.count);
		size -= ans;
		n.count -= ans-1;
		if(ans%2==1){
			hash ^= (int)x;
		}
		delete(n);
		return ans;
	}

	public long removeAll(final long x){
		final Node n = getNode(x);
		if(n==null){
			return 0;
		}
		size -= n.count;
		final long ans = n.count;
		if(n.count%2==1){
			hash ^= (int)x;
		}
		n.count = 0;
		delete(n);
		return ans;
	}

	private void delete(final Node node){
		if(node!=null){
			if(node.count>1){
				--node.count;
				fix(node);
				return;
			}
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
				--uniqueSize;
			}
			else{
				if(node.left!=null&&node.right!=null){
					final Node rep = getFirstNode(node.right);
					node.value = rep.value;
					node.count = rep.count;
					rep.count = 0;
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
					--uniqueSize;
				}
			}
		}
	}

	private Node getNode(final long x){
		Node now = root;
		while(now!=null){
			if(x<now.value){
				now = now.left;
			}
			else if(x>now.value){
				now = now.right;
			}
			else{
				break;
			}
		}
		return now;
	}

	public long first(){
		if(root==null){
			throw new NullPointerException();
		}
		return getFirstNode(root).value;
	}

	private Node getFirstNode(Node node){
		assert node!=null;
		Node par = null;
		while(node!=null){
			par = node;
			node = par.left;
		}
		return par;
	}

	public long last(){
		if(root==null){
			throw new NullPointerException();
		}
		return getLastNode(root).value;
	}

	private Node getLastNode(Node node){
		assert node!=null;
		Node par = null;
		while(node!=null){
			par = node;
			node = par.right;
		}
		return par;
	}

	public boolean contains(final long x){
		if(root==null){
			return false;
		}
		return getNode(x)!=null;
	}

	public long sum(final long x){
		if(root==null){
			return 0;
		}
		final Node node = getNode(x);
		return node!=null?node.count:0;
	}

	public long pollFirst(){
		if(root==null){
			throw new NullPointerException();
		}
		--size;
		final Node min = getFirstNode(root);
		hash ^= (int)min.value;
		final long ans = min.value;
		delete(min);
		return ans;
	}

	public long pollLast(){
		if(root==null){
			throw new NullPointerException();
		}
		--size;
		final Node max = getLastNode(root);
		hash ^= (int)max.value;
		final long ans = max.value;
		delete(max);
		return ans;
	}

	public long ceiling(final long x){
		return ceiling(root,x);
	}

	private long ceiling(Node node,final long x){
		Node ans = new Node(null,x-1,0);
		while(node!=null){
			if(x>node.value){
				node = node.right;
			}
			else if(x<node.value){
				ans = node;
				node = node.left;
			}
			else{
				return x;
			}
		}
		return ans.value;
	}

	public long higher(final long x){
		return higher(root,x);
	}

	private long higher(Node node,final long x){
		Node ans = new Node(null,x-1,0);
		while(node!=null){
			if(x>=node.value){
				node = node.right;
			}
			else{
				ans = node;
				node = node.left;
			}
		}
		return ans.value;
	}

	public long floor(final long x){
		return floor(root,x);
	}

	private long floor(Node node,final long x){
		Node ans = new Node(null,x+1,0);
		while(node!=null){
			if(x<node.value){
				node = node.left;
			}
			else if(x>node.value){
				ans = node;
				node = node.right;
			}
			else{
				return x;
			}
		}
		return ans.value;
	}

	public long lower(final long x){
		return lower(root,x);
	}

	private long lower(Node node,final long x){
		Node ans = new Node(null,x+1,0);
		while(node!=null){
			if(x<=node.value){
				node = node.left;
			}
			else{
				ans = node;
				node = node.right;
			}
		}
		return ans.value;
	}

	public int size(){
		return uniqueSize;
	}

	public long sumSize(){
		return size;
	}

	public long[][] toArray(){
		final long[][] list = new long[uniqueSize][2];
		if(root!=null){
			int index = 0;
			ArrayDeque<Node> deq = new ArrayDeque<>(root.height<<1);
			deq.add(root);
			while(!deq.isEmpty()){
				final Node now = deq.pollLast();
				if(index==0){
					if(now.left!=null){
						deq.add(now);
						deq.add(now.left);
					}
					else{
						list[index][0] = now.value;
						list[index++][1] = now.count;
						if(now.right!=null){
							deq.add(now.right);
						}
					}
				}
				else if(now.left!=null&&list[index-1][0]<now.left.value){
					deq.add(now);
					deq.add(now.left);
				}
				else{
					list[index][0] = now.value;
					list[index++][1] = now.count;
					if(now.right!=null){
						deq.add(now.right);
					}
				}
			}
		}
		return list;
	}

	public long[] toOneArray(){
		final long[] list = new long[(int)size];
		if(root!=null){
			int index = 0;
			final ArrayDeque<Node> deq = new ArrayDeque<>();
			deq.add(root);
			while(!deq.isEmpty()){
				final Node now = deq.pollLast();
				if(index==0){
					if(now.left!=null){
						deq.add(now);
						deq.add(now.left);
					}
					else{
						for(int i = 0;i<now.count;++i){
							list[index++] = now.value;
						}
						if(now.right!=null){
							deq.add(now.right);
						}
					}
				}
				else if(now.left!=null&&list[index-1]<now.left.value){
					deq.add(now);
					deq.add(now.left);
				}
				else{
					for(int i = 0;i<now.count;++i){
						list[index++] = now.value;
					}
					if(now.right!=null){
						deq.add(now.right);
					}
				}
			}
		}
		return list;
	}

	@Override public String toString(){
		final long[] list = toOneArray();
		return Arrays.toString(list);
	}

	@Override public boolean equals(final Object o){
		if(o instanceof final TreeMultiInt tree){
			if(size!=tree.size()){
				return false;
			}
			final long[][] array1 = toArray();
			final long[][] array2 = tree.toArray();
			for(int i = 0;i<size;++i){
				if(array1[i][0]!=array2[i][0]||array1[i][1]!=array2[i][1]){
					return false;
				}
			}
			return true;
		}
		return false;
	}

	@Override public int hashCode(){
		return hash;
	}

	/*
	 * 以下、平衡用メソッド
	 */
	private void fix(Node node){
		while(node!=null){
			final int lh = node.left==null?0:node.left.height;
			final int rh = node.right==null?0:node.right.height;
			if(lh-rh>1){
				assert node.left!=null;
				if(node.left.right!=null&&node.left.right.height==lh-1){
					rotateL(node.left);
					setStates(node.left);
				}
				rotateR(node);
			}
			else if(rh-lh>1){
				assert node.right!=null;
				if(node.right.left!=null&&node.right.left.height==rh-1){
					rotateR(node.right);
					setStates(node.right);
				}
				rotateL(node);
			}
			setStates(node);
			node = node.parent;
		}
	}

	private void rotateR(final Node node){
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
	}

	private void rotateL(final Node node){
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
	}

	private void setStates(final Node node){
		final int lh = node.left!=null?node.left.height:0;
		final int rh = node.right!=null?node.right.height:0;
		node.height = Math.max(lh,rh)+1;
		final long ls = node.left!=null?node.left.size:0;
		final long rs = node.right!=null?node.right.size:0;
		node.size = ls+rs+node.count;
	}
}
