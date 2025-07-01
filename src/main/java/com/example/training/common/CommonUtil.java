package com.example.training.common;

import com.example.training.user.JbillingAPI;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for common operations.
 */
@Component
public class CommonUtil {
    private static final Map<String, String> applicationResources = new HashMap<>();
    private static final Map<String, JbillingAPI> apiCache = new HashMap<>();
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static CommonUtil instance;

    public static CommonUtil getInstance() {
        if (instance == null) {
            instance = new CommonUtil();
        }
        return instance;
    }

    public ObjectMapper getObjMapper() {
        return objectMapper;
    }

    static {
        // Initialize application resources
        applicationResources.put("user.teleport.active", "1701");
        applicationResources.put("user.teleport.canceled", "1702");
        applicationResources.put("user.teleport.nonpayment", "1703");
        applicationResources.put("user.teleport.activeunpaid", "1704");
        applicationResources.put("cancellationStep.teleport.Collection", "1705");

        applicationResources.put("user.setmore.active", "1801");
        applicationResources.put("user.setmore.canceled", "1802");
        applicationResources.put("user.setmore.nonpayment", "1803");
        applicationResources.put("user.setmore.activeunpaid", "1804");
        applicationResources.put("cancellationStep.setmore.Collection", "1805");

        applicationResources.put("user.inthechair.active", "1901");
        applicationResources.put("user.inthechair.canceled", "1902");
        applicationResources.put("user.inthechair.nonpayment", "1903");
        applicationResources.put("user.inthechair.activeunpaid", "1904");
        applicationResources.put("cancellationStep.inthechair.Collection", "1905");
    }

    /**
     * Gets a value from application resources.
     *
     * @param key the key to look up
     * @return the value associated with the key
     */
    public String getValueFromApplicationResource(String key) {
        return applicationResources.getOrDefault(key, "");
    }

    /**
     * Gets a value from application resources by mode.
     *
     * @param key the key to look up
     * @return the value associated with the key
     */
    public String getValueFromApplicationResourceByMode(String key) {
        return getValueFromApplicationResource(key);
    }

    /**
     * Gets a JbillingAPI instance for the given brand ID.
     *
     * @param brandId the brand ID
     * @return a JbillingAPI instance
     */
    public JbillingAPI getJbillingAPIByBrandId(String brandId) {
        if (!apiCache.containsKey(brandId)) {
            apiCache.put(brandId, new JbillingAPI() {});
        }
        return apiCache.get(brandId);
    }

    /**
     * Unmaps assets in FC.
     *
     * @param accountPin the account PIN
     * @param brandId the brand ID
     * @param param1 a boolean parameter
     * @param param2 another boolean parameter
     */
    public void unmapAssetsInFC(String accountPin, String brandId, boolean param1, boolean param2) {
        // Implementation omitted for brevity
    }

    /**
     * Gets a list of all brand IDs.
     *
     * @return list of brand IDs
     */
    public java.util.List<String> getListOfBrandIds() {
        // Simplified implementation - return some mock brand IDs
        return java.util.Arrays.asList("teleport", "setmore", "inthechair", "brand1", "brand2");
    }

    /**
     * Checks if the given brand ID is a product brand.
     *
     * @param brandId the brand ID to check
     * @return true if it's a product brand, false otherwise
     */
    public boolean isProductBrand(String brandId) {
        // Simplified implementation - consider certain brands as product brands
        return brandId != null && (brandId.equals("teleport") || brandId.equals("setmore"));
    }

    /**
     * Converts a response map to JSON string.
     *
     * @param resMap the response map
     * @return JSON string representation
     */
    public String responseAsString(java.util.Map<String, Object> resMap) {
        try {
            return objectMapper.writeValueAsString(resMap);
        } catch (Exception e) {
            return "{\"status\":\"failure\",\"message\":\"Error converting response to JSON\"}";
        }
    }
}
