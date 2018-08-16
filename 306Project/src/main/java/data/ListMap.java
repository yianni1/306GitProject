package data;

import exceptions.ListException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by olive on 16/08/2018.
 */
public class ListMap<K, V>{

    private HashMap<K, V> map = new HashMap<>();
    private List<K> list = new ArrayList<>();

    /**
     * Adds a value to the data structure
     * @param key
     * @param value
     */
    public void add(K key, V value) {
        list.add(key);
        map.put(key, value);
    }

    /**
     * Removes the specified value
     * corresponding to the key
     * @param key
     */
    public void remove(K key) {
        list.remove(key);
        map.remove(key);
    }

    /**
     * Retrieves a value
     * @param key
     * @return
     */
    public V get(K key) {
        return map.get(key);
    }

    /**
     * Gets the List of keys
     * @return the list of keys
     */
    public List<K> getList() {
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

    public List<V> getListOfValues() {
        List<V> values = new ArrayList<>();

        for (K k : list) {
            values.add(this.map.get(k));
        }

        return values;
    }
}
