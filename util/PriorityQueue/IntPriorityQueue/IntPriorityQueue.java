class IntPriorityQueue{
	private int[] heap;
	private int size;
	private java.util.function.IntBinaryOperator comparator;
	public static final java.util.function.IntBinaryOperator reverseOrder =
		(a,b) -> Integer.compare(b,a);
	public IntPriorityQueue(){
		this(10,Integer::compare);
	}
	public IntPriorityQueue(final int size){
		this(size,Integer::compare);
	}
	public IntPriorityQueue(final java.util.function.IntBinaryOperator comparator){
		this(10,comparator);
	}
	public IntPriorityQueue(final int size,final java.util.function.IntBinaryOperator comparator){
		heap = new int[size+1];
		this.size = 0;
		this.comparator = comparator;
	}
	public IntPriorityQueue(final int[] defaultValues){
		this(defaultValues,Integer::compare);
	}
	public IntPriorityQueue(final int[] defaultValues,final java.util.function.IntBinaryOperator comparator){
		heap = new int[defaultValues.length+1];
		System.arraycopy(defaultValues,0,heap,1,defaultValues.length);
		this.size = heap.length;
		this.comparator = comparator;
		allHeapify();
	}
	public void add(final int value){
		if(++size==heap.length)
			grow();
		heap[size] = value;
		heapifyUp(size);
	}
	public int poll(){
		final int ans = heap[1];
		heap[1] = heap[size--];
		heapifyDown(1);
		return ans;
	}
	private void grow(){
		final int[] newHeap = new int[size<<1];
		System.arraycopy(heap,1,newHeap,1,size-1);
		heap = newHeap;
	}
	private void allHeapify(){
		for(int i=1;i<=size;i++){
			heapifyUp(i);
		}
	}
	private void heapifyUp(final int index){
		int now = index>>1;
		while(0<now){
			final int left = now<<1;
			final int right = left|1;
			if(right<=size){
				int pri = comparator.applyAsInt(heap[left],heap[right])<0?-1:1;
				if(comparator.applyAsInt(heap[now],pri==-1?heap[left]:heap[right])>0){
					if(pri==-1){
						swap(now,left);
					}
					else{
						swap(now,right);
					}
				}
				else{
					break;
				}
			}
			else{
				if(comparator.applyAsInt(heap[now],heap[left])>0){
					swap(now,left);
				}
				else{
					break;
				}
			}
			now >>= 1;
		}
	}
	private void heapifyDown(final int index){
		int now = index;
		while(now<<1<=size){
			final int left = now<<1;
			final int right = left|1;
			if(right<=size){
				int pri = comparator.applyAsInt(heap[left],heap[right])<0?-1:1;
				if(comparator.applyAsInt(heap[now],pri==-1?heap[left]:heap[right])>0){
					if(pri==-1){
						swap(now,left);
						now = left;
					}
					else{
						swap(now,right);
						now = right;
					}
				}
				else{
					break;
				}
			}
			else{
				if(comparator.applyAsInt(heap[now],heap[left])>0){
					swap(now,left);
					now = left;
				}
				else{
					break;
				}
			}
		}
	}
	private void swap(final int index1,final int index2){
		final int temp = heap[index1];
		heap[index1] = heap[index2];
		heap[index2] = temp;
	}
	public int size(){
		return size;
	}
}
