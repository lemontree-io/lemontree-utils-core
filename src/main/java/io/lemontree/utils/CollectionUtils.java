package io.lemontree.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class CollectionUtils {

	/**
	 * Removes multiple items from a list of objects.
	 * <br><b>Example:</b>
	 * <br>stringList = ["a", "b" , "c", "d", "e"]
	 * <br>removeList = [2, 4]
	 * <br> <code>removeMultipleIndexes(stringList, removeList)</code> returns ["a", "b", "d"]
	 * @param list The list where items are supposed to be removed.
	 * @param removeIds A list containing the index values to be removed.
	 */
	public static void removeMultipleIndexes(List<?> list,
			final List<Integer> removeIds) {
		java.util.Collections.sort(removeIds, new Comparator<Integer>() {
			@Override
			public int compare(Integer i1, Integer i2) {
				return i2.compareTo(i1);
			}

		});
		for (Integer idx : removeIds) {
			list.remove(idx.intValue());
		}
	}

	/**
	 * Checks whether all objects in a collection are equal.
	 * <br><b>Example:</b>
	 * <br><code>checkForEqualityOnAllValues(["a", "a", "a"])</code> returns true
	 * <br><code>checkForEqualityOnAllValues(["a", "a", "z"])</code> returns false
	 * @param values A collection of objects to be checked on equality.
	 * @return true - If all items are equal. <br>false - if at least two objects in the collections are not equal
	 */
	public static boolean checkForEqualityOnAllValues(Collection<?> values) {
		Object o = values.iterator().next();
		for (Object oCompare : values) {
			if (!o.equals(oCompare)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Removes list of items from another list and returns a new list with the
	 * result. Useful to create a new list without a subset. The function
	 * doesn't effect the original lists.
	 * 
	 * @param items
	 * @param oneInstanceItems
	 * @return
	 */
	public static List<?> removeItems(List<?> items, List<?> removings) {
		List<?> sublist = copyList(items);
		sublist.removeAll(removings);
		return sublist;
	}

	/**
	 * Creates a new list and copies the original (<b>caution:</b> not cloned!) values into the
	 * new list.
	 * @param items List of items to be copied into a new list.
	 * @return new java.util.List containing objects from input List
	 */
	public static <T> List<T> copyList(List<T> items) {
		List<T> copy = new ArrayList<T>();
		for (T o : items) {
			copy.add(o);
		}
		return copy;
	}

	/**
	 * Checks whether the items in list are contained in the same order in another list.
	 * <br><b>Example 1:</b>
	 * <br>ListA = ["a", "b", "c", "d", "e"]
	 * <br>ListB = ["b", "c", "d"]
	 * <br><code>isSublist(ListB, ListA)</code>returns true;
	 * <br>
	 * <br><b>Example 2:</b>
	 * <br>ListA = ["a", "b", "c", "d", "e"]
	 * <br>ListB = ["c", "b", "d"]
	 * <br><code>isSublist(ListB, ListA)</code>returns false;
	 * @param sublist
	 * @param parent
	 * @return
	 */
	public static boolean isSublist(final List<?> sublist, final List<?> parent) {
		return indexOfSublist(sublist, parent)>-1;
	}

	/**
	 * Determines the list index value of the beginning of a sublist.
	 * <br><b>Example:</b>
	 * <br>listA = ["a", "b", "c", "d", "e"]
	 * <br>listB = ["b", "c", "d"]
	 * <br><code>indexOfSublist(listB, listA)</code>returns 1;
	 * @param sublist The sublist to be detected in a parent /  containing list.
	 * @param parent The containing List of the sublist.
	 * @return index of the first occurrence of the sublist within the parent / containing list.
	 * <br>returns -1 if sublist is not contained in parent.
	 */
	public static int indexOfSublist(List<?> sublist,
			List<?> parent) {
		
		boolean isSublist = false;
		Object o = sublist.get(0);
		List<?> lookUp = copyList(parent);
		int startIndex = 0;
		int tempIndex = -1;
		while((tempIndex=lookUp.indexOf(o))>-1 && tempIndex+sublist.size()<=lookUp.size() && !isSublist){
			if(sublist.size()==1){
				isSublist = true;
			}
			for(int i=1;i<sublist.size();i++){
				if(!sublist.get(i).equals(lookUp.get(tempIndex+i))){
					break;
				}else if(i==sublist.size()-1){
					isSublist = true;
				}
			}
			lookUp = lookUp.subList(tempIndex+1, lookUp.size());
			startIndex += tempIndex;
		}
		if(!isSublist){
			startIndex = -1;
		}
		return startIndex;
	}
	
	/**
	 * Removes a range of items from a list.
	 * <br><b>Example:</b>
	 * <br>list = ["a","b","c","d"]
	 * <br><code>removeIndexRange(list, 1, 2)</code> returns ["a", "d"]
	 * @param list List where item index range is to be removed.
	 * @param startIndex Item position to start removal.
	 * @param endIndex Item position to end removal.
	 * @return Shortened list without items in index range <code>startIndex ... endIndex</code>
	 */
	public static List<?> removeIndexRange(List<?> list,
			int startIndex, int endIndex) {
		List<?> workList = copyList(list);
		List sublist = new ArrayList();
		sublist.addAll(workList.subList(0, startIndex));
		sublist.addAll(workList.subList(endIndex+1, workList.size()));
		return sublist;
	}

	/**
	 * Counts the number of occurrences of objects equal to <code>obj</code> within list <code>list</code>.
	 * @param obj
	 * @param list
	 * @return number of occurrences of objects equal to <code>obj</code> within list <code>list</code>
	 */
	public static int count(Object obj, List<?> list) {
		int count = 0;
		for(Object listObj:list){
			if(obj.equals(listObj)){
				count++;
			}
		}
		return count;
	}

	/**
	 * Checks whether all items in <code>listA</code> are equal to an item in <code>listB</code>
	 * and the size of <code>listA</code> equals the size of <code>listB</code>
	 * <br><b>Example 1:</b>
	 * <br>listA = ["a", "b", "c"]
	 * <br>listB = ["a", "b", "c"]
	 * <br>equalsAll(listA, listB) returns true
	 * <br>
	 * <br><b>Example 2:</b>
	 * <br>listA = ["a", "b", "c"]
	 * <br>listB = ["a", "b"]
	 * <br>equalsAll(listA, listB) returns false
	 * <br>
	 * <br><b>Example 3:</b>
	 * <br>listA = ["a", "b", "c"]
	 * <br>listB = ["c", "b", "a"]
	 * <br>equalsAll(listA, listB) returns false
	 * @param listA
	 * @param listB
	 * @return <b>true</b> if the size of listA equals the size of list listB and each item in listA equals the item at the same index position in listB
	 * <br><b>false</b> if the size of listA does not equal the size of list listB or if not each item in listA equals the item at the same index position in listB
	 */
	public static boolean equalsAll(List<?> listA, List<?> listB) {
		if(listA.size()==listB.size()){
			for(int i=0;i<listA.size();i++){
				if(!listA.get(i).equals(listB.get(i))){
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * Selects all items in <code>lookupItems</code> that are not contained in list <code>mustNotEqualItems</code>
	 * <br><b>Example:</b>
	 * <br>lookupList = ["a", "b", "c", "d"]
	 * <br>mustNotEqualList = ["b", "c"]
	 * <br><code>getItemsNotIn(lookupList, mustNotEqualList)</code> returns ["a", "d"]
	 * @param lookupItems
	 * @param mustNotEqualItems
	 * @return New list containing all items from <code>lookupItems</code> that are not contained in <code>mustNotEqualItems</code>
	 */
	public static List<?> getItemsNotIn(List<?> lookupItems,
			List<?> mustNotEqualItems) {
		
		List<Object> filtered = new ArrayList<Object>();
		for(Object lookup:lookupItems){
			if(!mustNotEqualItems.contains(lookup)){
				filtered.add(lookup);
			}
		}
		return filtered;
	}

	/**
	 * Creates a new list and adds the given input item into the list.
	 * @param toBeWrapped Object to be put in a list.
	 * @return List containing the Object provided as input param.
	 */
	public static <T> List<T> getWrappedInList(
			T toBeWrapped) {
		List<T> wrapperList = new ArrayList<T>();
		wrapperList.add(toBeWrapped);
		return wrapperList;
	}
	
	/**
	 * Creates new list and copies items provided in param <code>itemsList</code> into the new <code>java.util.List</code> instance
	 * @param itemsList List of items to be packed into new <code>java.util.List</code> instance
	 * @return new <code>java.util.List</code> instance containing the same items provided in param <code>itemsList</code>
	 */
	public static <T> List<T> getItemsAddedToNewList(
			List<T> itemsList) {
		List<T> newList = new ArrayList<T>();
		newList.addAll(itemsList);
		return newList;
	}

	/**
	 * Sorts a collection of lists by their size ascending.
	 * @param lists A list containing multiple lists to be sorted by size
	 */
	public static <T> void sortBySize(List<List<T>> lists) {
		Collections.sort(lists, new BySizeComparator());
	}
	
	private static class BySizeComparator implements Comparator<List>{
		@Override
		public int compare(List o1, List o2) {
			return Integer.compare(o1.size(), o2.size());
		}
	}

	/**
	 * Inserts a value at a specified position in a list.
	 * @param list
	 * @param insertion
	 * @param pos The position where the insertion item is inserted (inserting before and right shifting old items >=pos). 
	 * <br>I.e. pos=0 inserts a new item at position 0. The previous position 0 is then 1;
	 */
	public static <T> List<T> insertAtPosition(List<T> list,
			int pos, T ... insertion) {

		
		List<T> newList = new ArrayList<T>();
		
		List<T> pre;
		if(pos == 0){			
			pre = Collections.EMPTY_LIST;
		}else{
			pre = list.subList(0, pos);			
		}
		List<T> post = list.subList(pos, list.size());
		
		newList.addAll(pre);
		newList.addAll(createListFromIterable(insertion));
		newList.addAll(post);
		
		return newList;
	}
	
	/**
	 * Puts the values of an array object and puts them into a <code>java.util.List</code>
	 * @param iterable Array object to be converted to a <code>java.util.List</code>
	 * @return List containing the same items contained in param <code>iterable</code>
	 */
	public static <T> List<T> createListFromIterable(T[] iterable){
		List<T> out = new ArrayList<T>();
		for(T item:iterable){
			out.add(item);
		}
		return out;
	}
	
	/**
	 * Puts the values of a <code>java.util.Collection</code> object and puts them into a <code>java.util.List</code>
	 * @param collection Collection object to be converted to a <code>java.util.List</code>
	 * @return List containing the same items contained in param <code>collection</code>
	 */
	public static <T> List<T> createListFromCollection(Collection<T> collection){
		List<T> out = new ArrayList<T>();
		for(T item:collection){
			out.add(item);
		}
		return out;
	}

	public static void print(List<?> values) {
		for(Object o:values){
			System.out.println(o);
		}
	}
	public static void print(Map<? , ?> values) {
		for(Entry<? , ?> entry:values.entrySet()){
			System.out.println(entry.getKey()+":"+entry.getValue());
		}
	}

	public static <T> void addAllIfNotContains(List<T> all, List<T> toBeAdded ) {
		for(T o : toBeAdded){
			if(!all.contains(o)){
				all.add(o);
			}
		}
	}

	public static <T> List<T> getLast(List<T> shows, int itemsLength) {
		if(shows.size() <= itemsLength){
			return copyList(shows);
		}
		
		List<T> last = new ArrayList<T>(itemsLength);
		int start = last.size()-itemsLength;
		for(int j=start ; j < itemsLength ; j++){
			last.add(shows.get(itemsLength));
		}
		return last;
	}

	public static <T> T getLastItem(List<T> items) {
		int size = items.size();
		if(size > 0){
			int lastIndex = size - 1;
			return items.get(lastIndex);
		}
		return null;
	}
}
