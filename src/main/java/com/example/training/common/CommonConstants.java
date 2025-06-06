package com.example.training.common;

import java.util.Arrays;
import java.util.List;

/**
 * Constants used throughout the application.
 */
public class CommonConstants {
    // Brand IDs
    public static final String TELEPORT_BRANDID = "teleport";
    public static final String SETMORE_BRANDID = "setmore";
    public static final String IN_THE_CHAIR_BRANDID = "inthechair";

    // Product brands
    public static final List<String> saasProductBrands = Arrays.asList(
            TELEPORT_BRANDID,
            SETMORE_BRANDID,
            IN_THE_CHAIR_BRANDID
    );

    // Action types
    public static final String ACTION_TYPE_DOWNGRADE = "downgrade";

    // Platform types
    public static final String PLATFORM_WEB = "web";

    // Free plan
    public static final String FREE = "Free";

    // Private constructor to prevent instantiation
    private CommonConstants() {
        throw new AssertionError("CommonConstants class should not be instantiated");
    }
}