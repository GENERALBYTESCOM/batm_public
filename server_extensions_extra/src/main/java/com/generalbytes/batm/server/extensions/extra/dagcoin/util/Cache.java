package com.generalbytes.batm.server.extensions.extra.dagcoin.util;

/**
 * Interface for enabling cache
 * 
 * @author shubhrapahwa
 *
 */
public interface Cache {
	
	/**
	 * Adds a new cache object with given key and expiry time
	 * 
	 * @param key
	 * @param value
	 * @param periodInMillis
	 */
	void add(String key, Object value, long periodInMillis);
	 
	/**
	 * Removes the cached object given key
	 * 
	 * @param key
	 */
    void remove(String key);
 
    /**
     * Returns the cached object given the key
     * 
     * @param key
     * @return Object
     */
    Object get(String key);
 
    /**
     * Clears the cache
     * 
     */
    void clear();
 
    /**
     * Returns the size of the cache
     * 
     * @return size
     */
    long size();

}
