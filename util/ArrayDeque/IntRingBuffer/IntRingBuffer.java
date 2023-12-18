class IntRingBuffer{
	private int[] buff;
	private int head,tail;
	public IntRingBuffer(){
		this(16);
	}
	public IntRingBuffer(final int n){
		buff = new int[n];
		head = tail = 0;
	}
	private int next(final int index){
		final int next = index+1;
		return next==buff.length?0:next;
	}
	private int previous(final int index){
		final int previous = index-1;
		return previous==-1?buff.length-1:previous;
	}
	private int getIndex(final int index){
		final int size = size();
		if(index>=size)
			throw new IndexOutOfBoundsException("Index "+index+" out of bounds for length "+size);
		final int id = head+index;
		return buff.length<=id?id-buff.length:id;
	}
	private void grow(){
		int[] newBuff = new int[buff.length<<1];
		arraycopy(0,newBuff,0,size());
		buff = newBuff;
	}
	public int size(){
		final int size = tail-head;
		return size<0?size+buff.length:size;
	}
	public void addFirst(final int x){
		if(previous(head)==tail)
			grow();
		head = previous(head);
		buff[head] = x;
	}
	public void addLast(final int x){
		if(next(tail)==head)
			grow();
		buff[tail] = x;
		tail = next(tail);
	}
	public void removeFirst(){
		if(head==tail)
			throw new java.util.NoSuchElementException("Buffer is empty");
		head = next(head);
	}
	public void removeLast(){
		if(head==tail)
			throw new java.util.NoSuchElementException("Buffer is empty");
		tail = previous(tail);
	}
	public int pollFirst(){
		if(head==tail)
			throw new java.util.NoSuchElementException("Buffer is empty");
		final int ans = buff[head];
		head = next(head);
		return ans;
	}
	public int pollLast(){
		if(head==tail)
			throw new java.util.NoSuchElementException("Buffer is empty");
		tail = previous(tail);
		return buff[tail];
	}
	public int get(final int index){
		final int id = getIndex(index);
		return buff[id];
	}
	public void set(final int index,final int x){
		final int id = getIndex(index);
		buff[id] = x;
	}
	public void swap(final int index1,final int index2){
		final int id1 = getIndex(index1);
		final int id2 = getIndex(index2);
		final int num = buff[id1];
		buff[id1] = buff[id2];
		buff[id2] = num;
	}
	public void arraycopy(final int fromIndex,final int[] array,final int from,final int length){
		if(fromIndex+length>size())
			throw new IndexOutOfBoundsException("last source index "+(fromIndex+length)+" out of bounds for int["+size()+"]");
		final int h = getIndex(fromIndex);
		if(h+length<buff.length)
			System.arraycopy(buff,h,array,from,length);
		else{
			final int back = buff.length-h;
			System.arraycopy(buff,h,array,from,back);
			System.arraycopy(buff,0,array,from+back,length-back);
		}
	}
	public int[] toArray(){
		int[] array = new int[size()];
		arraycopy(0,array,0,size());
		return array;
	}
	public void clear(){
		head = tail = 0;
	}
	@Override
	public String toString(){
		int[] array = new int[size()];
		arraycopy(0,array,0,size());
		return java.util.Arrays.toString(array);
	}
}
