class IntegerSet {
	private interface LinkableNode {
		public boolean add(int x);

		public boolean remove(int x);

		public boolean contains(int x);
	}

	private abstract class Node implements LinkableNode {
		Node next, prev;
		protected int value;
		int height;
	}

	private class LinkedNode extends Node {
		LinkableNode parent;
		LinkedNode son;

		private LinkedNode(int val) {
			value = val;
			height = 1;
			if (head == null) {
				head = tail = this;
			} else {
				tail.next = this;
				prev = tail;
				tail = this;
			}
			size++;
		}

		public boolean add(int x) {
			LinkedNode par = null;
			LinkedNode now = this;
			while (now != null) {
				par = now;
				if (now.value == x) {
					return false;
				}
				now = now.son;
			}
			par.son = new LinkedNode(x);
			par.son.parent = par;
			int h = 1;
			LinkableNode prev = par;
			while (prev instanceof LinkedNode p) {
				p.height = ++h;
				prev = p.parent;
			}
			return true;
		}

		private void add(LinkedNode n) {
			LinkedNode now = this;
			while (now.son != null) {
				LinkedNode next = now.son;
				now = next;
			}
			now.son = n;
			n.parent = now;
			int h = 1;
			LinkableNode prev = now;
			while (prev instanceof LinkedNode p) {
				p.height = ++h;
				prev = p.parent;
			}
		}

		public boolean remove(int x) {
			LinkedNode now = this;
			while (now != null) {
				if (now.value == x) {
					delete(now);
					return true;
				}
				now = now.son;
			}
			return false;
		}

		public boolean contains(int x) {
			LinkedNode now = this;
			while (now != null) {
				if (now.value == x) {
					return true;
				}
				now = now.son;
			}
			return false;
		}

		private void delete(LinkedNode node) {
			LinkableNode par = node.parent;
			LinkedNode son = node.son;
			if (par instanceof DummyNode p) {
				p.next = son;
			} else if (par instanceof LinkedNode p) {
				p.son = son;
			} else {
				throw new AssertionError("Unexpected Error : Bad type of node ");
			}
			if (son != null) {
				son.parent = par;
			} 
			Node prev = node.prev;
			Node next = node.next;
			if (prev == null) {
				head = head.next;
			} else {
				prev.next = next;
			}
			if (next == null) {
				tail = tail.prev;
			} else {
				next.prev = prev;
			}
			setNull(node);
			--size;
		}

		private static void setNull(LinkedNode node) {
			node.parent = null;
			node.son = null;
			node.prev = null;
			node.next = null;
		}
	}

	private class TreeNode extends Node {
		LinkableNode parent;
		TreeNode left, right;

		private TreeNode(int val) {
			value = val;
			height = 1;
			if (head == null) {
				head = tail = this;
			} else {
				tail.next = this;
				prev = tail;
				tail = this;
			}
			size++;
		}

		public boolean add(int x) {
			TreeNode par = null;
			TreeNode now = this;
			while (now != null) {
				par = now;
				if (x < now.value) {
					now = now.left;
				} else if (x > now.value) {
					now = now.right;
				} else {
					return false;
				}
			}
			assert par != null;
			if (x < par.value) {
				par.left = new TreeNode(x);
				par.left.parent = par;
			} else {
				par.right = new TreeNode(x);
				par.right.parent = par;
			}
			fix(par);
			return true;
		}

		public void add(TreeNode n) {
			TreeNode par = null;
			TreeNode now = this;
			while (now != null) {
				par = now;
				if (n.value < now.value) {
					now = now.left;
				} else {
					now = now.right;
				}
			}
			assert par != null;
			if (n.value < par.value) {
				par.left = n;
				n.parent = par;
			} else {
				par.right = n;
				n.parent = par;
			}
			fix(par);
		}

		public boolean remove(int x) {
			TreeNode now = this;
			while (now != null) {
				if (now.value == x) {
					delete(now);
					return true;
				}
				if (x < now.value) {
					now = now.left;
				} else {
					now = now.right;
				}
			}
			return false;
		}

		public boolean contains(int x) {
			TreeNode now = this;
			while (now != null) {
				if (now.value == x) {
					return true;
				}
				if (x < now.value) {
					now = now.left;
				} else {
					now = now.right;
				}
			}
			return false;
		}

		private void delete(TreeNode node) {
			if (node.left == null) {
				if (node.parent instanceof DummyNode p) {
					p.next = node.right;
					if (node.right != null) {
						node.right.parent = p;
					}
				} else if (node.parent instanceof TreeNode p) {
					if (p.left == node)
						p.left = node.right;
					else
						p.right = node.right;
					if (node.right != null) {
						node.right.parent = p;
					}
					fix(p);
				} else {
					throw new AssertionError("Unexpected Error : Bad type of node");
				}
				if (node.next != null)
					node.next.prev = node.prev;
				else
					tail = node.prev;
				if (node.prev != null)
					node.prev.next = node.next;
				else
					head = node.next;
				setNull(node);
				size--;
			} else if (node.right == null) {
				if (node.parent instanceof DummyNode p) {
					p.next = node.left;
					if (node.left != null) {
						node.left.parent = p;
					}
				} else if (node.parent instanceof TreeNode p) {
					if (p.left == node)
						p.left = node.left;
					else
						p.right = node.left;
					if (node.left != null) {
						node.left.parent = p;
					}
					fix(p);
				} else {
					throw new AssertionError("Unexpected Error : Bad type of node");
				}
				if (node.next != null)
					node.next.prev = node.prev;
				else
					tail = node.prev;
				if (node.prev != null)
					node.prev.next = node.next;
				else
					head = node.next;
				setNull(node);
				size--;
			} else {
				int sub = deleteMax(node.left);
				node.value = sub;
			}
		}

		private int deleteMax(TreeNode node) {
			assert node != null;
			TreeNode ans = node;
			while (ans.right != null)
				ans = ans.right;
			int max = ans.value;
			delete(ans);
			return max;
		}

		private static void fix(LinkableNode node) {
			while (node instanceof TreeNode n) {
				final int lh = n.left == null ? 0 : n.left.height;
				final int rh = n.right == null ? 0 : n.right.height;
				if (lh - rh > 1) {
					assert n.left != null;
					if (n.left.right != null && n.left.right.height == lh - 1) {
						rotateL(n.left);
					}
					rotateR(n);
				} else if (rh - lh > 1) {
					assert n.right != null;
					if (n.right.left != null && n.right.left.height == rh - 1) {
						rotateR(n.right);
					}
					rotateL(n);
				} else {
					setStates(n);
				}
				node = n.parent;
			}
		}

		private static void rotateR(final TreeNode node) {
			final TreeNode temp = node.left;
			node.left = temp.right;
			if (node.left != null) {
				node.left.parent = node;
			}
			temp.right = node;
			temp.parent = node.parent;
			if (node.parent instanceof DummyNode p) {
				p.next = temp;
			} else if (node.parent instanceof TreeNode p) {
				if (p.left == node) {
					p.left = temp;
				} else {
					p.right = temp;
				}
			}
			node.parent = temp;
			setStates(node);
		}

		private static void rotateL(final TreeNode node) {
			final TreeNode temp = node.right;
			node.right = temp.left;
			if (node.right != null) {
				node.right.parent = node;
			}
			temp.left = node;
			temp.parent = node.parent;
			if (node.parent instanceof DummyNode p) {
				p.next = temp;
			} else if (node.parent instanceof TreeNode p) {
				if (p.left == node) {
					p.left = temp;
				} else {
					p.right = temp;
				}
			}
			node.parent = temp;
			setStates(node);
		}

		private static void setStates(final TreeNode node) {
			final int lh = node.left != null ? node.left.height : 0;
			final int rh = node.right != null ? node.right.height : 0;
			node.height = Math.max(lh, rh) + 1;
		}

		private static void setNull(TreeNode node) {
			node.left = null;
			node.right = null;
			node.parent = null;
			node.prev = null;
			node.next = null;
		}
	}

	private class DummyNode implements LinkableNode {
		private static final int LOWER_BOUND = 16;
		Node next;

		public boolean add(int x) {
			if (next == null) {
				LinkedNode son = new LinkedNode(x);
				next = son;
				son.parent = this;
				return true;
			} else if (next instanceof LinkedNode ln) {
				boolean ans = ln.add(x);
				if (LOWER_BOUND <= ln.height) {
					treefy();
				}
				return ans;
			} else if (next instanceof TreeNode tn) {
				return tn.add(x);
			} else {
				throw new AssertionError("Unexpected Error : Bad type of node");
			}
		}

		private void add(Node n) {
			if (next == null) {
				LinkedNode son;
				if (n instanceof LinkedNode ln) {
					son = ln;
				} else if (n instanceof TreeNode tn) {
					son = new LinkedNode(tn.value);
				} else {
					throw new AssertionError("Unexpected Error : Bad type of node");
				}
				next = son;
				son.parent = this;
			} else if (next instanceof LinkedNode ln) {
				LinkedNode son;
				if (n instanceof LinkedNode node) {
					son = node;
				} else if (n instanceof TreeNode tn) {
					son = new LinkedNode(tn.value);
				} else {
					throw new AssertionError("Unexpected Error : Bad type of node");
				}
				ln.add(son);
				if (LOWER_BOUND <= ln.height) {
					treefy();
				}
			} else if (next instanceof TreeNode tn) {
				TreeNode son;
				if (n instanceof TreeNode node) {
					son = node;
				} else if (n instanceof LinkedNode ln) {
					son = new TreeNode(ln.value);
				} else {
					throw new AssertionError("Unexpected Error : Bad type of node");
				}
				tn.add(son);
			} else {
				throw new AssertionError("Unexpected Error : Bad type of node");
			}
		}

		public boolean remove(int x) {
			if (next == null) {
				return false;
			}
			return next.remove(x);
		}

		public boolean contains(int x) {
			if (next == null) {
				return false;
			}
			return next.contains(x);
		}

		private void treefy() {
			assert next instanceof LinkedNode;
			LinkedNode node = (LinkedNode) next;
			TreeNode newNode = new TreeNode(node.value);
			newNode.parent = this;
			next = newNode;
			node = node.son;
			while (node != null) {
				next.add(node.value);
				node = node.son;
			}
		}
	}

	private DummyNode[] table;
	private int size;
	private Node head, tail;
	private static final int BIG_TREE = 15;

	public IntegerSet() {
		this(21);
	}

	public IntegerSet(final int s) {
		if (s < 1) {
			throw new NegativeArraySizeException("hash table's size must be positive");
		}
		table = new DummyNode[s];
		size = 0;
	}

	public boolean add(final int x) {
		sizeCheck();
		final int index = hash(x);
		if (table[index] == null) {
			table[index] = new DummyNode();
		}
		final boolean ans = table[index].add(x);
		treeCheck(index);
		return ans;
	}

	private void add(final Node n) {
		final int index = hash(n.value);
		if (table[index] == null) {
			table[index] = new DummyNode();
		}
		table[index].add(n);
	}

	public boolean remove(final int x) {
		final int index = hash(x);
		if (table[index] == null) {
			return false;
		}
		return table[index].remove(x);
	}

	public boolean contains(final int x) {
		final int index = hash(x);
		if (table[index] == null) {
			return false;
		}
		return table[index].contains(x);
	}

	private void reHash() {
		final DummyNode[] oldTable = table;
		int nextSize = table.length << 2 | 1;
		table = new DummyNode[nextSize];
		Node node = head;
		while (node != null) {
			if (node instanceof LinkedNode n) {
				n.son = null;
			} else if (node instanceof TreeNode n) {
				n.left = null;
				n.right = null;
			} else {
				throw new AssertionError("Unexpected Error : Bad type of node");
			}
			add(node);
			node = node.next;
		}
	}

	private void sizeCheck() {
		if (table.length * 0.65 < size) {
			reHash();
		}
	}

	private void treeCheck(int index) {
		if (BIG_TREE <= table[index].next.height) {
			reHash();
		}
	}

	private int hash(final int val) {
		final int h = val % table.length;
		return h < 0 ? h + table.length : h;
	}

	public int size() {
		return size;
	}

	public int[] toArray() {
		final int[] array = new int[size];
		int index = 0;
		Node node = head;
		while (node != null) {
			array[index++] = node.value;
			node = node.next;
		}
		return array;
	}

	public String toString() {
		return java.util.Arrays.toString(toArray());
	}
}
