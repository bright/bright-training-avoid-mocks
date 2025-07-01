package com.example.training.config;

/**
 * Configuration for Redis connection parameters.
 * This allows for configuring Redis in tests using Testcontainers.
 */
public class RedisConfig {
    private static String redisHost = "localhost";
    private static int redisPort = 6379;
    private static int redisReadTimeout = 10000;

    public static String getRedisHost() {
        return redisHost;
    }

    public static void setRedisHost(String host) {
        redisHost = host;
    }

    public static int getRedisPort() {
        return redisPort;
    }

    public static void setRedisPort(int port) {
        redisPort = port;
    }

    public static int getRedisReadTimeout() {
        return redisReadTimeout;
    }

    public static void setRedisReadTimeout(int timeout) {
        redisReadTimeout = timeout;
    }
}
