package data;

import exceptions.ListException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by olive on 16/08/2018.
 * This is the data structure to maintains the concurrence between the HashMap of nodes and the list of their
 * names in a seperate list so that no null values are obtained when the names point to the HashMap nodes
 * As well as other methods such as Add , Remove, get, getlist , setlist
 */
public class ListMap<K, V> implements Serializable{

    //Hashmap is used to store the nodes within the graph, with a key and value
    private HashMap<K, V> map = new HashMap<K, V>();

    //The list is used to store the names of the of the nodes so they can be accessed
    private List<K> list = new ArrayList<K>();

    /**
     * The Add method adds the key value to the list (the nodes name) and then adds the Node and the name
     * into the hash map this is done through concurrently so that we can avoid null pointer.
     * @param key
     * @param value
     */
    public void add(K key, V value) {

        //adds the Key value into a list (Node Name)
        list.add(key);

        //add the key and value into the map , (Node value and the Node name)
        map.put(key, value);
    }

    /**
     * The remove method involves the removal of the key value in both the
     * list and map concurrently as it avoid null values in both aspects
     * @param key
     */
    public void remove(K key) {
        //removes the key in the list
        list.remove(key);

        //removes the key in the map
        map.remove(key);
    }

    /**
     * The Get Method gets the value for the key value, therefore using the node name
     * you should be able to get the node out for usage
     * @param key
     * @return
     */
    public V get(K key) {
        //returns the node value(Value) based on the node name(Key)
        return map.get(key);
    }

    /**
     * getList returns the list of keys which is the node names
     * @return
     */
    public List<K> getList() {

        //returns a list of keys
        return list;
    }

    /**
     * Sets a list of objects which correspond to the Name of the nodes
     * @param newList
     * @throws ListException
     */
    public void setList(List<K> newList) throws ListException {
        //checking for subsets within the lists therefore we need
        //to check if the subset is available within the list
        boolean subSetOfList = true;
        boolean subSetOfNewList = true;

        //accounting for lists that are duplicates
        //checks the list if they are a subset of the list, if not then it
        //sets the new list to false
        for (K k : newList) {

            //check if the list contains the key value
            if (!list.contains(k)) {
                subSetOfList = false;
                break;
            }
        }

        //checking the new list contains of the key values which is a subset
        //so it doesn't add duplicate values within the list
        for (K k : list) {

            //if the new list doesn't contain the list value
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
        List<V> values = new ArrayList<V>();

        for (K k : list) {
            values.add(this.map.get(k));
        }

        return values;
    }
}
