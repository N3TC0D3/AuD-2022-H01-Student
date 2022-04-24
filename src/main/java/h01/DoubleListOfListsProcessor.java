package h01;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;

import org.jetbrains.annotations.Nullable;

public class DoubleListOfListsProcessor {

    /**
     * Partitions a copy of {@code listOfLists} so that the sum of each sub-list does not exceed {@code limit}.
     * This implementation must not modify the input list.
     *
     * @param listOfLists a reference to the head of a list with arbitrary size
     * @param limit       the maximum value that may not be exceeded by the sum of any sub-list in the returned list
     * @return a partitioned list of lists
     * @throws RuntimeException if a single value exceeds {@code limit}
     */
    public static @Nullable ListItem<@Nullable ListItem<Double>> partitionListsAsCopyIteratively(
        @Nullable ListItem<@Nullable ListItem<Double>> listOfLists,
        double limit
    ) {
        var list = new ListItem<ListItem<Double>>();
        var listIterator = list;
        var iterator = listOfLists;
        var itNum = 0;
        while(iterator != null) {
        	double processedLimit = 0;
        	var innerIterator = iterator.key;
        	var innerItNum = 0;
        	var processedList = new ListItem<Double>();
        	var processedListIterator = processedList;
        	while(innerIterator != null) {
        		if(innerIterator.key > limit) {
        			throw new RuntimeException("element at ("+itNum+", "+innerItNum+") exceeds limit by "+(limit-innerIterator.key));
        		}
        		if(processedLimit+innerIterator.key > limit) {
        			listIterator.key = processedList;
        			listIterator.next = new ListItem<ListItem<Double>>();
        			listIterator = listIterator.next;
        			processedList = new ListItem<Double>();
        			processedListIterator = processedList;
        			processedLimit = 0;
        		}
        		processedListIterator.key = innerIterator.key;
        		processedLimit += innerIterator.key;
        		if(innerIterator.next != null && processedLimit+innerIterator.next.key <= limit) {
	        		processedListIterator.next = new ListItem<Double>();
	        		processedListIterator = processedListIterator.next;
        		}
        		innerIterator = innerIterator.next;
        		innerItNum++;
        	}
        	listIterator.key = processedList;
        	if(iterator.next != null) {
        		listIterator.next = new ListItem<ListItem<Double>>();
        	}
			listIterator = listIterator.next;
        	iterator = iterator.next;
        	itNum++;
        }
        iterator = list;
        return list;
    }

    /**
     * Partitions a copy of {@code listOfLists} so that the sum of each sub-list does not exceed {@code limit}.
     * This implementation must only use recursion and not modify the input list.
     *
     * @param listOfLists a reference to the head of a list with arbitrary size
     * @param limit       the maximum value that may not be exceeded by the sum of any sub-list in the returned list
     * @return a partitioned list of lists
     * @throws RuntimeException if a single value exceeds {@code limit}
     */
    public static @Nullable ListItem<@Nullable ListItem<Double>> partitionListsAsCopyRecursively(
        @Nullable ListItem<@Nullable ListItem<Double>> listOfLists,
        double limit
    ) {
    	if(listOfLists == null) {
    		return null;
    	}
        var list = new ListItem<ListItem<Double>>();
        var next = partitionSublistAsCopyRecursively(listOfLists.key, limit, limit);
        if(next.x == null) {
        	list.key = new ListItem<Double>();
        } else {
        	list.key = next.x;
        }
        if(next.y != null) {
        	//if there is still a rest of items from the key that have to be processed
        	var listCopy = new ListItem<ListItem<Double>>();
        	listCopy.key = next.y;
        	listCopy.next = listOfLists.next;
        	list.next = partitionListsAsCopyRecursively(listCopy, limit);
        } else {
        	list.next = partitionListsAsCopyRecursively(listOfLists.next, limit);
        }
        return list;
    }
    
    private static Tuple<ListItem<Double>,ListItem<Double>> partitionSublistAsCopyRecursively(ListItem<Double> listOfDouble, double originalLimit, double localLimit){
    	if(listOfDouble == null) return new Tuple<ListItem<Double>,ListItem<Double>>(null, null);
    	if(listOfDouble.key > originalLimit) {
    		throw new RuntimeException("element at (i, j) exceeds limit by "+(originalLimit-listOfDouble.key));
    	}
    	if(listOfDouble.key > localLimit) {
    		// if no item can be put in the current list, return the rest to be used for the next list
    		return new Tuple<ListItem<Double>,ListItem<Double>>(null, listOfDouble);
    	}
    	var list = new ListItem<Double>();
    	list.key = listOfDouble.key;
    	var subListResult = partitionSublistAsCopyRecursively(listOfDouble.next, originalLimit, localLimit-listOfDouble.key);
    	list.next = subListResult.x;
    	return new Tuple<ListItem<Double>,ListItem<Double>>(list,subListResult.y);
    }

    /**
     * Partitions a copy of {@code listOfLists} so that the sum of each sub-list does not exceed {@code limit}.
     * This implementation must not create new sub-lists.
     *
     * @param listOfLists a reference to the head of a list with arbitrary size
     * @param limit       the maximum value that may not be exceeded by the sum of any sub-list in the returned list
     * @return a partitioned list of lists
     * @throws RuntimeException if a single value exceeds {@code limit}
     */
    public static @Nullable ListItem<@Nullable ListItem<Double>> partitionListsInPlaceIteratively(
        @Nullable ListItem<@Nullable ListItem<Double>> listOfLists,
        double limit
    ) {
        var iterator = listOfLists;
        var tempNext = iterator.next;
        var itNum = 0;
        while(iterator != null) {
        	var innerIterator = iterator.key;
        	var innerItNum = 0;
        	var localLimit = innerIterator.key;
        	while(innerIterator != null) {
        		if(innerIterator.key > limit) {
        			throw new RuntimeException("element at ("+itNum+", "+innerItNum+") exceeds limit by "+(limit-innerIterator.key));
        		}
        		if(innerIterator.next != null) {
        			localLimit += innerIterator.next.key;
	        		if(localLimit > limit) {
	        			tempNext = iterator.next;
	        			iterator.next = new ListItem<ListItem<Double>>();
	        			iterator.next.key = innerIterator.next;
	        			innerIterator.next = null;
	        		}
        		}
        		innerIterator = innerIterator.next;
        		innerItNum++;
        	}
        	iterator = iterator.next;
        	if(iterator != null && iterator.next == null && tempNext != null) {
        		iterator.next = tempNext;
        		tempNext = null;
        	}
        	itNum++;
        }
        return listOfLists;
    }

    
    /**
     * Partitions a copy of {@code listOfLists} so that the sum of each sub-list does not exceed {@code limit}.
     * This implementation must only use recursion and not crete new sub-lists.
     *
     * @param listOfLists a reference to the head of a list with arbitrary size
     * @param limit       the maximum value that may not be exceeded by the sum of any sub-list in the returned list
     * @return a partitioned list of lists
     * @throws RuntimeException if a single value exceeds {@code limit}
     */
    public static @Nullable ListItem<@Nullable ListItem<Double>> partitionListsInPlaceRecursively(
        @Nullable ListItem<@Nullable ListItem<Double>> listOfLists,
        double limit
    ) {
    	if(listOfLists == null) {
    		return null;
    	}
        var next = partitionSublistInPlaceRecursively(listOfLists.key, limit, limit);
        listOfLists.key = next.x;
        if(next.y != null) {
        	//if there is still a rest of items from the key that have to be processed
        	var listCopy = new ListItem<ListItem<Double>>();
        	listCopy.key = next.y;
        	listCopy.next = listOfLists.next;
        	listOfLists.next = partitionListsInPlaceRecursively(listCopy, limit);
        } else {
        	listOfLists.next = partitionListsInPlaceRecursively(listOfLists.next, limit);
        }
        return listOfLists;
    }
    
    private static Tuple<ListItem<Double>,ListItem<Double>> partitionSublistInPlaceRecursively(ListItem<Double> listOfDouble, double originalLimit, double localLimit){
    	if(listOfDouble == null) return new Tuple<ListItem<Double>,ListItem<Double>>(null, null);
    	if(listOfDouble.key > originalLimit) {
    		throw new RuntimeException("element at (i, j) exceeds limit by "+(originalLimit-listOfDouble.key));
    	}
    	if(listOfDouble.key > localLimit) {
    		// if no item can be put in the current list, return the rest to be used for the next list
    		return new Tuple<ListItem<Double>,ListItem<Double>>(null, listOfDouble);
    	}
    	var subListResult = partitionSublistAsCopyRecursively(listOfDouble.next, originalLimit, localLimit-listOfDouble.key);
    	listOfDouble.next = subListResult.x;
    	return new Tuple<ListItem<Double>,ListItem<Double>>(listOfDouble,subListResult.y);
    }

    
    /**
     * Writes out {@code listOfLists} (well, the sub-lists) to {@code writer}.
     *
     * @param writer      the writer to write to
     * @param listOfLists the list of lists to write out
     */
    public static void write(Writer writer, @Nullable ListItem<@Nullable ListItem<Double>> listOfLists) {
    	try {
	        while(listOfLists != null) {
	        	var innerIterator = listOfLists.key;
	        	while(innerIterator != null){
	                writer.write(Double.toString(innerIterator.key));
	        		innerIterator = innerIterator.next;
	        		if(innerIterator != null) {
	        			writer.write('#');
	        		}
	        	}
	            listOfLists = listOfLists.next;
	            if(listOfLists != null) {
	            	writer.write('\n');
	            }
	        }
    	} catch(IOException e) {
    		e.printStackTrace();
    	}
    }

    /**
     * Reads a list of lists from {@code reader} and returns it.
     *
     * @param reader the reader to read from
     * @return a list of lists of double
     */
    public static @Nullable ListItem<@Nullable ListItem<Double>> read(BufferedReader reader) {
        var listOfLists = new ListItem<ListItem<Double>>();
        var listIterator = listOfLists;
        ListItem<Double> currentSubList = null;
        int currentChar;
        try {
			while((currentChar = reader.read()) != -1) {
				char c = (char) currentChar;
				String currentNumber = "";
				if(Character.isDigit(c) || c == '.') {
					currentNumber += c;
					while((currentChar = reader.read()) != -1 && currentChar != '#' && currentChar != '\n') {
						currentNumber += (char)currentChar;
					}
					double number = Double.valueOf(currentNumber);
					var item = new ListItem<Double>();
					item.key = number;
					if(listIterator.key == null) {
						listIterator.key = item;
						currentSubList = item;
					} else {
						currentSubList.next = item;
						currentSubList = currentSubList.next;
					}
				}
				if(currentChar == '\n') {
					listIterator.next = new ListItem<ListItem<Double>>();
					listIterator = listIterator.next;
					currentSubList = new ListItem<Double>();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
        return listOfLists;
    }
}
