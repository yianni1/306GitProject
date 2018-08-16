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
//        if (list.size() != map.size()) {
//            System.out.println(key + " " + value);
//            System.out.println(list.size() + " " + map.size());
//            throw new ListException();
//        }
//        if (!list.contains(key)) {
            list.add(key);
            map.put(key, value);
//        }
//        if (list.size() != map.size()) {
//            System.out.println(key + " " + value);
//            System.out.println(list.size() + " " + map.size());
//            String list = "";
//            for (K listValue : this.list) {
//                list += " " + listValue.toString();
//            }
//            System.out.println(list);
//            throw new ListException();
//        }
    }

    /**
     * Removes the specified value
     * corresponding to the key
     * @param key
     */
    public synchronized void remove(K key) {
        list.remove(key);
        map.remove(key);
//        if (key.equals("b")) {
//            System.out.println("removing b");
//        }
//        if (list.size() != map.size()) {
//            System.out.println("something went wrong");
//            System.out.println(key);
//            throw new ListException();
//        }
        List<K> newList = new ArrayList<>();
        for (K k : list) {
            if (!k.equals(key)) {
                newList.add(k);
            }
        }
        list = newList;
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
