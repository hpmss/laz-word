package me.hpms.lootworld.util;

public class ModifiedHashMap<K,V> {
	
	private K worldName;
	
	private V items;
	
	
	
	static class Entry<K,V> {
		final K key;
		V value;
		Entry<K,V> next;
		
		public Entry(K key,V value,Entry<K,V> next) {
			this.key = key;
			this.value = value;
			this.next = next;
		}
		
		public K getKey() {
			return key;
		}
		
		public V getValue() {
			return value;
		}
		
		public Entry<K,V> getNext() {
			return next;
		}
		
		
	}

}
