package com.example.training.product;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * Simplified ProductController based on the reference implementation.
 * Contains the two main methods: resetPlansAndAddonsCache and getStaticPlansAndAddonsV2
 */
@RestController
@RequestMapping("/product")
public class ProductController {

    private static final Logger log = LogManager.getLogger(ProductController.class.getName());
    private static final ProductUtilities productUtilities = new ProductUtilities();

    /**
     * Resets the plans and addons cache for the specified brand.
     * 
     * @param brandId the brand ID (optional)
     * @param setUpFee whether to include setup fees
     * @param category whether to include categories
     * @return JSON response indicating success or failure
     */
    @RequestMapping(value="/resetPlansAndAddonsCache", method = RequestMethod.PUT)
    public String resetPlansAndAddonsCache(@RequestParam(required=false) String brandId, 
                                         @RequestParam boolean setUpFee, 
                                         @RequestParam boolean category) throws Throwable {
        log.info("resetPlansAndAddonsCache, brandId : " + brandId + ", setUpFee : " + setUpFee + ", category : " + category);
        return productUtilities.resetPlansAndAddonsCache(brandId, setUpFee, category);
    }

    /**
     * Gets static plans and addons for the specified brand (version 2).
     * 
     * @param brandId the brand ID
     * @param category the category filter (optional)
     * @param addKeyForFreeTrialPlan whether to add key for free trial plan
     * @return JSON response with plans and addons data
     */
    @GetMapping(value = "/v2/getStaticPlansAndAddons/{brandId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String getStaticPlansAndAddonsV2(@PathVariable String brandId, 
                                          @RequestParam(required=false) String category,
                                          @RequestParam(required = false) boolean addKeyForFreeTrialPlan) {
        log.info("get plans and addons v2, brandId : " + brandId);
        return productUtilities.getStaticPlansAndAddonsv1(brandId, true, category, addKeyForFreeTrialPlan);
    }
}
