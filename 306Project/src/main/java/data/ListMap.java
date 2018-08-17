package data;

import exceptions.ListException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by olive on 16/08/2018.
 */
public class ListMap<K, V> implements Serializable{

    private HashMap<K, V> map = new HashMap<>();
    private List<K> list = new ArrayList<>();

    /**
     * Adds a value to the data structure
     * @param key
     * @param value
     */
    public synchronized void add(K key, V value) {
        list.add(key);
        map.put(key, value);
        removeDuplicatesFromList();
    }

    /**
     * Removes the specified value
     * corresponding to the key
     * @param key
     */
    public synchronized void remove(K key) {
        list.remove(key);
        map.remove(key);
        removeDuplicatesFromList();

    }

    /**
     * Retrieves a value
     * @param key
     * @return
     */
    public synchronized V get(K key) {
        return map.get(key);
    }

    public synchronized K get(int index) {
        return list.get(index);
    }

    /**
     * Gets the List of keys
     * @return the list of keys
     */
    public synchronized List<K> getList() {
        removeDuplicatesFromList();
        return list;
    }

    /**
     * Sets the list of objects
     *
     * @param newList
     * @throws ListException
     */
    public void setList(List<K> newList) throws ListException {
        boolean subSetOfList = true;
        boolean subSetOfNewList = true;
        for (K k : newList) {
            if (!list.contains(k)) {
                subSetOfList = false;
                break;
            }
        }

        for (K k : list) {
            if (!newList.contains(k)) {
                subSetOfNewList = false;
                break;
            }
        }
        if (!subSetOfList || !subSetOfNewList) {
            throw new ListException();
        }

        list = newList;
    }

    /**
     * Returns a list of all the values
     * @return
     */
    public List<V> getListOfValues() {
        List<V> values = new ArrayList<>();
        removeDuplicatesFromList();

        for (K k : list) {
            values.add(this.map.get(k));
        }

        return values;
    }

    /**
     * Removes all the duplicate values in the list
     */
    private void removeDuplicatesFromList() {
        List<K> newList = new ArrayList<>();
        for (K key : list) {
            if (!newList.contains(key)) {
                newList.add(key);
            }
        }
        list = newList;
    }
}
