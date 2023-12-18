class IntDeque{
	private final IntRingBuffer buff;
	public IntDeque(){
		buff = new IntRingBuffer();
	}
	public IntDeque(final int n){
		buff = new IntRingBuffer(n);
	}
	public int size(){
		return buff.size();
	}
	public void clear(){
		buff.clear();
	}
	public void addFirst(final int x){
		buff.addFirst(x);
	}
	public void addLast(final int x){
		buff.addLast(x);
	}
	public void add(final int x){
		addLast(x);
	}
	public void removeFirst(){
		buff.removeLast();
	}
	public void removeLast(){
		buff.removeFirst();
	}
	public void remove(){
		removeLast();
	}
	public int pollFirst(){
		return buff.pollFirst();
	}
	public int pollLast(){
		return buff.pollLast();
	}
	public int poll(){
		return pollFirst();
	}
	public int get(final int index){
		return buff.get(index);
	}
	public void set(final int index,final int x){
		buff.set(index,x);
	}
	public void swap(final int l,final int r){
		buff.swap(l,r);
	}
	public void sort(){
		final int size = size();
		if(size<16)
			insertionSort(0,size);
		else
			quickSort(size);
	}
	private void insertionSort(final int l,final int r){
		for(int i=l+1;i<r;i++){
			final int num = buff.get(i);
			int j = i;
			int temp;
			while(l<j&&(temp=buff.get(j-1))>num)
				buff.set(j--,temp);
			buff.set(j,num);
		}
	}
	private void mergeSort(final int l,final int r){
		final int[] stack = new int[r-l<<2];
		int tail = 0;
		stack[tail++] = l;
		stack[tail++] = r;
		stack[tail++] = 0;
		while(tail>0){
			final int query = stack[--tail];
			final int right = stack[--tail];
			final int left = stack[--tail];
			final int mid = left+right>>1;
			if(right-left<2)
				continue;
			else if(query==0){
				stack[tail++] = left;
				stack[tail++] = right;
				stack[tail++] = 1;
				stack[tail++] = left;
				stack[tail++] = mid;
				stack[tail++] = 0;
				stack[tail++] = mid;
				stack[tail++] = right;
				stack[tail++] = 0;
			}
			else{
				final int[] array1 = new int[mid-left];
				final int[] array2 = new int[right-mid];
				buff.arraycopy(left,array1,0,array1.length);
				buff.arraycopy(left+array1.length,array2,0,array2.length);
				int i = 0,j = 0,k = left;
				while(i<array1.length&&j<array2.length){
					if(array1[i]<array2[j])
						buff.set(k++,array1[i++]);
					else
						buff.set(k++,array2[j++]);
				}
				while(i<array1.length)
					buff.set(k++,array1[i++]);
				while(j<array2.length)
					buff.set(k++,array2[j++]);
			}
		}
	}
	private void quickSort(final int size){
		final int[] stack = new int[size+(size<<1)];
		int tail = 0;
		stack[tail++] = 0;
		stack[tail++] = size;
		stack[tail++] = 0;
		while(tail>0){
			final int depth = stack[--tail];
			final int r = stack[--tail];
			final int l = stack[--tail];
			if(r-l<2)
				continue;
			if(r-l<16){
				insertionSort(l,r);
				continue;
			}
			if(depth>10){
				mergeSort(l,r);
				continue;
			}
			final int mid = buff.get(r+l>>1);
			int i = l;
			int j = r-1;
			while(i<j){
				while(i<j&&buff.get(i)<=mid)
					++i;
				while(i<j&&buff.get(j)>mid)
					--j;
				if(i<j)
					buff.swap(i++,j--);
			}
			stack[tail++] = l;
			stack[tail++] = j+1;
			stack[tail++] = depth+1;
			stack[tail++] = i;
			stack[tail++] = r;
			stack[tail++] = depth+1;
		}
	}
	@Override
	public String toString(){
		return buff.toString();
	}
	public int[] toArray(){
		return buff.toArray();
	}
	public void arraycopy(final int fromIndex,final int[] array,final int from,final int length){
		buff.arraycopy(fromIndex,array,from,length);
	}
}
