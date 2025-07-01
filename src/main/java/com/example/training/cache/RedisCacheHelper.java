package com.example.training.cache;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RedisCacheHelper that provides Redis cache implementation using JedisPool.
 * Based on the reference implementation from build/jBilling_hosted_api/src/main/java/com/cache/RedisCacheHelper.java
 * Uses JedisPool for actual Redis connectivity.
 */
public class RedisCacheHelper {

    private static final Logger _LOG = LogManager.getLogger(RedisCacheHelper.class.getName());

    // Redis configuration
    private static final String _REDIS_SERVER_URL = "localhost";
    private static final Integer redisPort = 6379;
    private static final Integer redisReadTimeout = 10000;

    private static JedisPool pool = new JedisPool(new JedisPoolConfig(), _REDIS_SERVER_URL, redisPort, redisReadTimeout);

    static {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(25);
        pool = new JedisPool(jedisPoolConfig, _REDIS_SERVER_URL, redisPort, redisReadTimeout);
    }

    /**
     * Gets all data for a given key (equivalent to Redis HGETALL).
     * 
     * @param key the cache key
     * @return a map of field-value pairs or null if not found
     */
    public Map<String, String> getDataByKey(String key) {
        Jedis jedis = null;
        Map<String, String> cacheMap = null;

        try {
            jedis = pool.getResource();

            _LOG.info("Cache Key => " + key);

            cacheMap = jedis.hgetAll(key);

            _LOG.info("jedis.hgetAll => " + cacheMap.size());

        } catch (Throwable e) {
            _LOG.log(Level.ERROR, e.getMessage(), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

        return cacheMap;
    }

    /**
     * Saves data to the cache (equivalent to Redis HMSET).
     * 
     * @param key the cache key
     * @param value the data to save (field-value pairs)
     */
    public void save(String key, Map<String, String> value) {
        Jedis jedis = null;

        int leaseErrorCount = 0;
        Boolean isProcessed = false;

        while ((leaseErrorCount >= 0 && leaseErrorCount <= 10 && !isProcessed)) {
            try {
                jedis = pool.getResource();

                jedis.hmset(key, value);
                jedis.expireAt(key, 2082736799000L);  // Setting expiry at 12/31/2035 11:59:59 PM

                isProcessed = true;

                if (leaseErrorCount > 0) {
                    _LOG.info("Processed successfully");
                }
            } catch (Throwable e) {
                _LOG.log(Level.ERROR, e.getMessage(), e);
                leaseErrorCount++;
                isProcessed = false;

                _LOG.info("leaseErrorCount " + leaseErrorCount);
            } finally {
                if (jedis != null) {
                    jedis.close();
                }
            }
        }
    }

    /**
     * Gets a specific field value from a hash key (equivalent to Redis HMGET).
     * 
     * @param nameSpace the cache key (hash name)
     * @param key the field within the hash
     * @return the cached value or null if not found
     */
    public String get(String nameSpace, String key) {
        Jedis jedis = null;

        String value = null;

        int leaseErrorCount = 0;
        Boolean isProcessed = false;

        while ((leaseErrorCount >= 0 && leaseErrorCount <= 10 && !isProcessed)) {
            try {
                jedis = pool.getResource();

                List<String> values = jedis.hmget(nameSpace, key);

                if (values != null && values.size() > 0) {
                    value = values.get(0);
                }

                isProcessed = true;

                if (leaseErrorCount > 0) {
                    _LOG.info("Processed successfully");
                }
            } catch (Throwable e) {
                _LOG.log(Level.ERROR, e.getMessage(), e);
                leaseErrorCount++;
                isProcessed = false;

                _LOG.info("leaseErrorCount " + leaseErrorCount);
            } finally {
                if (jedis != null) {
                    jedis.close();
                }
            }
        }

        return value;
    }

    /**
     * Clears all cache data (for testing purposes).
     */
    public void clearAll() {
        _LOG.info("Clearing all cache data");

        Jedis jedis = null;

        try {
            jedis = pool.getResource();
            jedis.flushAll();
        } catch (Throwable e) {
            _LOG.log(Level.ERROR, e.getMessage(), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * Gets cache server info.
     * 
     * @return map with cache server information
     */
    public Map<String, Object> getCacheServerInfo() {
        Map<String, Object> responseMap = new HashMap<>();

        Jedis jedis = null;

        try {
            jedis = pool.getResource();

            responseMap.put("success", true);
            responseMap.put("info", jedis.info());

        } catch (Throwable e) {
            _LOG.log(Level.ERROR, e.getMessage(), e);
            responseMap.put("success", false);
            responseMap.put("message", e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

        return responseMap;
    }

    /**
     * Flushes all cache.
     * 
     * @return map with operation result
     */
    public Map<String, Object> flushAllCache() {
        Map<String, Object> responseMap = new HashMap<>();

        Jedis jedis = null;

        try {
            jedis = pool.getResource();

            jedis.flushAll();
            responseMap.put("success", true);

        } catch (Throwable e) {
            _LOG.log(Level.ERROR, e.getMessage(), e);
            responseMap.put("success", false);
            responseMap.put("message", e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

        return responseMap;
    }

    /**
     * Clears cache by key.
     * 
     * @param key the key to clear
     * @return map with operation result
     */
    public Map<String, Object> clearCacheByKey(String key) {
        Map<String, Object> responseMap = new HashMap<>();

        Jedis jedis = null;

        try {
            _LOG.info("Removing cache for key : " + key);

            jedis = pool.getResource();

            long keysRemoved = jedis.del(key);

            _LOG.info("Keys removed : " + keysRemoved);

            responseMap.put("success", true);

        } catch (Throwable e) {
            _LOG.log(Level.ERROR, e.getMessage(), e);
            responseMap.put("success", false);
            responseMap.put("message", e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

        return responseMap;
    }
}
