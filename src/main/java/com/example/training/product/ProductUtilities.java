package com.example.training.product;

import com.example.training.cache.RedisCacheHelper;
import com.example.training.common.CommonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Simplified ProductUtilities class based on the reference implementation.
 * Contains simplified versions of the main methods used by ProductController.
 */
public class ProductUtilities {

    private static final Logger log = LogManager.getLogger(ProductUtilities.class.getName());
    private final CommonUtil commonUtil = CommonUtil.getInstance();
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Resets the plans and addons cache for the specified brand(s).
     * 
     * @param brandId the brand ID (if null, resets for all brands)
     * @param setUpFee whether to include setup fees
     * @param itemCategories whether to include item categories
     * @return JSON response indicating success or failure
     */
    public String resetPlansAndAddonsCache(String brandId, boolean setUpFee, boolean itemCategories) throws Throwable {

        Map<String, Object> resMap = new HashMap<>();
        resMap.put("success", false);

        RedisCacheHelper redisCacheHelper = new RedisCacheHelper();

        Map<String, String> brands = redisCacheHelper.getDataByKey("plansAndAddons");

        if(brands == null) {
            brands = new HashMap<>();
        } else {
            brands = new HashMap<>(brands);
        }

        List<String> brandIds = null;

        if(brandId == null || brandId.isEmpty()) {
            brandIds = commonUtil.getListOfBrandIds();
        } else {
            brandIds = new ArrayList<>(Arrays.asList(brandId));
        }

        if(brandIds != null && !brandIds.isEmpty()) {

            log.info("Number of brandIds has to update : " + brandIds.size());
            Map<String, Object> brandDetails = null;

            for(String id : brandIds) {

                try {
                    log.info("Updating cache for the brand : " + id);
                    brandDetails = new HashMap<>();

                    // Simplified: just put some mock data for each brand
                    Map<String, Object> mockPlans = createMockPlansData(id);
                    Map<String, Object> mockAddons = createMockAddonsData(id);

                    brandDetails.put("plans", mockPlans);
                    brandDetails.put("additionalServices", mockAddons);

                    if(commonUtil.isProductBrand(id)) {
                        brandDetails.put("productPlan", createMockProductData(id));
                    }

                    // Simplified: just convert to JSON string instead of compressing
                    brands.put(id, mapper.writeValueAsString(brandDetails));
                    resMap.put(id, true);
                } catch(Throwable e) {
                    log.error("Error updating cache for brand " + id, e);
                    resMap.put(id, false);
                }
            }
        }

        if(setUpFee) {
            brands.put("setUpFee", mapper.writeValueAsString(createMockSetupFeeData()));
        }

        if(itemCategories) {
            brands.put("itemsCategory", mapper.writeValueAsString(createMockCategoriesData()));
        }

        redisCacheHelper.save("plansAndAddons", brands);
        resMap.put("success", true);

        return commonUtil.responseAsString(resMap);
    }

    /**
     * Gets static plans and addons for the specified brand.
     * 
     * @param brandId the brand ID
     * @param isNew whether to use new format
     * @param category the category filter (optional)
     * @param addKeyForFreeTrialPlan whether to add key for free trial plan
     * @return JSON response with plans and addons data
     */
    public String getStaticPlansAndAddonsv1(String brandId, boolean isNew, String category, boolean addKeyForFreeTrialPlan) {

        Map<String, Object> resMap = new HashMap<>();
        resMap.put("status", "failure");

        try {

            Map<String, Object> data = new HashMap<>();

            // Simplified: get data from cache or create mock data
            RedisCacheHelper redisCacheHelper = new RedisCacheHelper();
            String cachedData = redisCacheHelper.get("plansAndAddons", brandId);

            if(cachedData != null && !cachedData.isEmpty()) {
                log.info("Got the plans and addons from the cache server");
                // Parse cached data
                Map<String, Object> brandData = mapper.readValue(cachedData, Map.class);

                if(brandData.get("additionalServices") != null) {
                    data.put("addons", brandData.get("additionalServices"));
                }

                if(brandData.get("plans") != null) {
                    data.put("plans", brandData.get("plans"));
                }
            } else {
                // Create mock data if not in cache
                data.put("addons", createMockAddonsData(brandId));
                data.put("plans", createMockPlansData(brandId));
            }

            resMap.put("data", data);
            resMap.put("status", "success");
        } catch(Throwable e) {
            log.error("Error getting static plans and addons", e);
            resMap.put("status", "failure");
            resMap.put("exception", e.getMessage());
        }

        try {
            return mapper.writeValueAsString(resMap);
        } catch (JsonProcessingException e) {
            return "{\"status\":\"failure\",\"exception\":\""+e.getMessage()+"\"}";
        }
    }

    // Helper methods to create mock data

    private Map<String, Object> createMockPlansData(String brandId) {
        Map<String, Object> plans = new HashMap<>();
        List<Map<String, Object>> planList = new ArrayList<>();

        Map<String, Object> plan1 = new HashMap<>();
        plan1.put("id", 1);
        plan1.put("name", "Basic Plan - " + brandId);
        plan1.put("price", "9.99");
        plan1.put("description", "Basic plan for " + brandId);
        planList.add(plan1);

        Map<String, Object> plan2 = new HashMap<>();
        plan2.put("id", 2);
        plan2.put("name", "Premium Plan - " + brandId);
        plan2.put("price", "19.99");
        plan2.put("description", "Premium plan for " + brandId);
        planList.add(plan2);

        plans.put("plans", planList);
        return plans;
    }

    private Map<String, Object> createMockAddonsData(String brandId) {
        List<Map<String, Object>> addons = new ArrayList<>();

        Map<String, Object> addon1 = new HashMap<>();
        addon1.put("id", 101);
        addon1.put("name", "Extra Storage - " + brandId);
        addon1.put("price", "5.00");
        addon1.put("description", "Additional storage addon");
        addons.add(addon1);

        Map<String, Object> addon2 = new HashMap<>();
        addon2.put("id", 102);
        addon2.put("name", "Priority Support - " + brandId);
        addon2.put("price", "10.00");
        addon2.put("description", "Priority customer support");
        addons.add(addon2);

        Map<String, Object> result = new HashMap<>();
        result.put("addons", addons);
        return result;
    }

    private Map<String, Object> createMockProductData(String brandId) {
        Map<String, Object> product = new HashMap<>();
        product.put("id", 201);
        product.put("name", "Product Plan - " + brandId);
        product.put("price", "29.99");
        product.put("description", "Product-specific plan for " + brandId);
        return product;
    }

    private Map<String, Object> createMockSetupFeeData() {
        Map<String, Object> setupFee = new HashMap<>();
        setupFee.put("id", 301);
        setupFee.put("name", "Setup Fee");
        setupFee.put("price", "25.00");
        setupFee.put("description", "One-time setup fee");
        return setupFee;
    }

    private Map<String, Object> createMockCategoriesData() {
        List<Map<String, Object>> categories = new ArrayList<>();

        Map<String, Object> cat1 = new HashMap<>();
        cat1.put("id", 804);
        cat1.put("name", "Additional Services");
        cat1.put("description", "Additional service items");
        categories.add(cat1);

        Map<String, Object> cat2 = new HashMap<>();
        cat2.put("id", 801);
        cat2.put("name", "Setup Fees");
        cat2.put("description", "Setup fee items");
        categories.add(cat2);

        Map<String, Object> result = new HashMap<>();
        result.put("categories", categories);
        return result;
    }
}
