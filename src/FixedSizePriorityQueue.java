public class FixedSizePriorityQueue<T extends Comparable<T>> {

	int maxSize;
	Comparable[] data;
	int size;
	
	public FixedSizePriorityQueue(int maxSize) {
		this.maxSize = maxSize;
		data = new Comparable[maxSize + 1];
	}
	
	public void add(T t) {
		data[size] = t;
		
		for(int i = size; i > 0; i--) {
			if(data[i].compareTo(data[i - 1]) < 0) {
				Comparable tmp = data[i];
				data[i] = data[i - 1];
				data[i - 1] = tmp;
			}
		}
		
		if(size < maxSize) {
			size++;
		}
	}

	public boolean contains(T t) {
		for(int i = 0; i < size; i++) {
			if(data[i].equals(t)) {
				return true;
			}
		}
		return false;
	}
	
}
