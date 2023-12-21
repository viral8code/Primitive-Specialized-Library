class LongPriorityQueue{
	private long[] heap;
	private int size;
	private java.util.function.LongBinaryOperator comparator;
	public static final java.util.function.LongBinaryOperator reverseOrder =
		(a,b) -> Long.compare(b,a);
	public LongPriorityQueue(){
		this(10,Long::compare);
	}
	public LongPriorityQueue(final int size){
		this(size,Long::compare);
	}
	public LongPriorityQueue(final java.util.function.LongBinaryOperator comparator){
		this(10,comparator);
	}
	public LongPriorityQueue(final int size,final java.util.function.LongBinaryOperator comparator){
		heap = new long[size+1];
		this.size = 0;
		this.comparator = comparator;
	}
	public LongPriorityQueue(final long[] defaultValues){
		this(defaultValues,Long::compare);
	}
	public LongPriorityQueue(final long[] defaultValues,final java.util.function.LongBinaryOperator comparator){
		heap = new long[defaultValues.length+1];
		System.arraycopy(defaultValues,0,heap,1,defaultValues.length);
		this.size = heap.length;
		this.comparator = comparator;
		allHeapify();
	}
	public void add(final long value){
		if(++size==heap.length)
			grow();
		heap[size] = value;
		heapifyUp(size);
	}
	public long poll(){
		final long ans = heap[1];
		heap[1] = heap[size--];
		heapifyDown(1);
		return ans;
	}
	private void grow(){
		final long[] newHeap = new long[size<<1];
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
				int pri = comparator.applyAsLong(heap[left],heap[right])<0?-1:1;
				if(comparator.applyAsLong(heap[now],pri==-1?heap[left]:heap[right])>0){
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
				if(comparator.applyAsLong(heap[now],heap[left])>0){
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
				int pri = comparator.applyAsLong(heap[left],heap[right])<0?-1:1;
				if(comparator.applyAsLong(heap[now],pri==-1?heap[left]:heap[right])>0){
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
				if(comparator.applyAsLong(heap[now],heap[left])>0){
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
		final long temp = heap[index1];
		heap[index1] = heap[index2];
		heap[index2] = temp;
	}
	public int size(){
		return size;
	}
}
