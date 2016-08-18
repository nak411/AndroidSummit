package org.androidsummit.eventapp.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO expand to use LRU cache and add functionality to clear from memory
 * <p/>
 * Created= on 8/18/16.
 */
public class MemCache {

    private static MemCache mInstance;

    //TODO replace with LRU structure
    private static Map<String, Object> mCache;

    private MemCache(){
        mCache = new HashMap<>();
    }


    public static MemCache getInstance() {
        if (mInstance == null) {
            mInstance = new MemCache();
        }
        return mInstance ;
    }


    public Object getObject(String key) {
        return mCache.get(key);
    }

    public void putObject(String key, Object o) {
        mCache.put(key, o);
    }
}
