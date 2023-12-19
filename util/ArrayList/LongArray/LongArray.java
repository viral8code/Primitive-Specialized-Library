class LongArray
	implements Cloneable,
		java.util.RandomAccess,
		java.util.Collection<Long>,
		java.io.Serializable{

	private long[] array;
	private int size,hash;
	public LongArray(){
		this(16);
	}
	public LongArray(int length){
		if(length<=0)
			throw new IllegalArgumentException("length must be positive");
		array = new long[length];
		size = 0;
		hash = 0;
	}
	public LongArray(long[] arr){
		array = new long[arr.length];
		System.arraycopy(arr,0,array,0,arr.length);
		size = array.length;
		hash = 0;
		for(long value:arr)
			hash ^= value;
	}
	private void grow(){
		long[] newArray = new long[array.length<<1];
		System.arraycopy(array,0,newArray,0,array.length);
		array = newArray;
	}
	public long add(long value){
		if(size==array.length)
			grow();
		hash ^= value;
		return array[size++] = value;
	}
	public long add(long value,int index){
		if(index<0||size<index)
			throw new ArrayIndexOutOfBoundsException("Index "+index+" out of bounds for length "+size);
		if(index==size)
			return add(value);
		if(size==array.length)
			grow();
		System.arraycopy(array,index,array,index+1,(size++)-index);
		hash ^= value;
		return array[index] = value;
	}
	public long get(int index){
		if(index<0||size<=index)
			throw new ArrayIndexOutOfBoundsException("Index "+index+" out of bounds for length "+size);
		return array[index];
	}
	public long set(int index,long value){
		if(index<0||size<=index)
			throw new ArrayIndexOutOfBoundsException("Index "+index+" out of bounds for length "+size);
		return array[index] = value;
	}
	public long removeLast(){
		if(size==0)
			throw new java.util.NoSuchElementException("array is empty");
		hash ^= array[--size];
		return array[size];
	}
	public long remove(int index){
		if(index<0||size<=index)
			throw new ArrayIndexOutOfBoundsException("Index "+index+" out of bounds for length "+size);
		long ans = array[index];
		System.arraycopy(array,index+1,array,index,size-index-1);
		--size;
		hash ^= ans;
		return ans;
	}
	public void extend(LongArray arr){
		if(arr.size()>0){
			long[] newArray = new long[size+arr.size()];
			System.arraycopy(array,0,newArray,0,size);
			System.arraycopy(arr.toArray(),0,newArray,size,arr.size());
			size += arr.size();
			hash ^= arr.hashCode();
		}
	}
	public void extend(long[] arr){
		if(arr.length>0){
			long[] newArray = new long[size+arr.length];
			System.arraycopy(array,0,newArray,0,size);
			System.arraycopy(arr,0,newArray,size,arr.length);
			size += arr.length;
			for(long num:arr)
				hash ^= num;
		}
	}
	public int size(){
		return size;
	}
	public long[] toLongArray(){
		return java.util.Arrays.copyOf(array,size);
	}
	public Long[] toArray(){
		Long[] array = new Long[size];
		for(int i=0;i<size;i++)
			array[i] = this.array[i];
		return array;
	}
	public LongArray copy(){
		return new LongArray(toLongArray());
	}
	public void sort(){
		java.util.Arrays.sort(array,0,size);
	}
	public void sort(java.util.function.LongBinaryOperator comparator){
		IntroSort.sort(array,0,size,comparator);
	}
	@Override
	public String toString(){
		StringBuilder str = new StringBuilder("[");
		if(size>0)
			str.append(array[0]);
		for(int i=1;i<size;i++){
			str.append(", ");
			str.append(array[i]);
		}
		str.append("]");
		return str.toString();
	}
	@Override
	public int hashCode(){
		return hash;
	}
	@Override
	public boolean equals(Object o){
		if(o instanceof LongArray){
			LongArray arr = (LongArray)o;
			if(size!=arr.size())
				return false;
			for(int i=0;i<size;i++)
				if(array[i]!=arr.get(i))
					return false;
			return true;
		}
		return false;
	}
	@Override
	public LongArray clone(){
		return copy();
	}
	@Override
	public void clear(){
		size = 0;
		hash = 0;
	}
	@Override
	public boolean retainAll(java.util.Collection<? extends Long> collection){
		long[] newArray = new long[size];
		int index = 0;
		for(java.util.Iterator<Long> iter = collection.iterator();iter.hasNext();){
			Long num = iter.next();
			if(contains(num))
				newArray[index++] = num;
		}
		array = newArray;
		boolean isChanged = size!=index;
		size = index;
		return isChanged;
	}
	@Override
	public boolean removeAll(java.util.Collection<? extends Long> collection){
		boolean isChanged = false;
		for(java.util.Iterator<Long> iter = collection.iterator();iter.hasNext();){
			Object num = iter.next();
			for(int i=0;i<size;i++){
				if(num.equals(array[i])){
					remove(i);
					isChanged = true;
					break;
				}
			}
		}
		return isChanged;
	}
	@Override
	public void addAll(java.util.Collection<? extends Long> collection){
		for(java.util.Iterator<Long> iter = collection.iterator();iter.hasNext();){
			Long num = iter.next();
			add((Long)num);
		}
		return isChanged;
	}
	@Override
	public boolean contains(Object o){
		if(o instanceof Long){
			long num = (Long)o;
			for(int i=0;i<size;i++)
				if(num==array[i])
					return true;
			return false;
		}
		return false;
	}
	static class IntroSort{
		public static void sort(long[] array,int l,int r,java.util.function.LongBinaryOperator comparator){
			if(array.length<10)
				insertSort(array,l,r,comparator);
			else{
				int limit = 1;
				while(r-l>1<<limit)
					limit++;
				quickSort(array,l,r,limit,comparator);
			}
		}
		private static void quickSort(long[] array,int l,int r,int limit,java.util.function.LongBinaryOperator comparator){
			int len = r-l;
			if(len<2)
				return;
			if(len<10){
				insertSort(array,l,r,comparator);
				return;
			}
			if(limit<0){
				heapSort(array,l,r,comparator);
				return;
			}
			long pivot = array[len/2+l];
			int i = l;
			int j = r-1;
			while(i<=j){
				while(comparator.applyAsLong(array[i],pivot)<0)
					i++;
				while(comparator.applyAsLong(array[j],pivot)>0)
					j--;
				if(i<=j){
					long temp = array[i];
					array[i] = array[j];
					array[j] = temp;
					i++;
					j--;
				}
			}
			quickSort(array,l,j+1,limit-1,comparator);
			quickSort(array,i,r,limit-1,comparator);
		}
		private static void insertSort(long[] array,int l,int r,java.util.function.LongBinaryOperator comparator){
			for(int i=l+1;i<r;i++){
				long num = array[i];
				int j = i-1;
				while(l<=j&&comparator.applyAsLong(num,array[j])<0)
					array[j+1] = array[j--];
				j++;
				array[j] = num;
			}
		}
		private static void heapSort(long[] array,int l,int r,java.util.function.LongBinaryOperator comparator){
			int len = r-l;
			for(int i=0;i<len;i++){
				int j = i;
				while(0<j&&comparator.applyAsLong(array[(j-1)/2+l],array[j+l])<0){
					long temp = array[(j-1)/2+l];
					array[(j-1)/2+l] = array[j+l];
					array[j+l] = temp;
					j = (j-1)/2;
				}
			}
			for(int i=len;i>0;i--){
				long temp1 = array[i-1+l];
				array[i-1+l] = array[l];
				array[l] = temp1;
				int j = 0;
				while(2*j+1<i-1&&comparator.applyAsLong(array[j+l],array[2*j+1+l])<0||
				      2*j+2<i-1&&comparator.applyAsLong(array[j+l],array[2*j+2+l])<0){
					if(2*j+2<i-1&&comparator.applyAsLong(array[2*j+2+l],array[2*j+1+l])>0){
						long temp2 = array[2*j+2+l];
						array[2*j+2+l] = array[j+l];
						array[j+l] = temp2;
						j++;
						j <<= 1;
					}
					else{
						long temp2 = array[2*j+1+l];
						array[2*j+1+l] = array[j+l];
						array[j+l] = temp2;
						j <<= 1;
						j++;
					}
				}
			}
		}
	}
}
