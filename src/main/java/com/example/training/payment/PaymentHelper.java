package com.example.training.payment;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper class for payment operations.
 */
@Component
public class PaymentHelper {
    /**
     * Gets the latest successful or entered payment from cache.
     *
     * @param accountPin the account PIN
     * @param type the payment type
     * @return a map containing payment details
     */
    public Map<String, Object> getLatestSuccessOrEnteredPaymentFromCache(String accountPin, String type) {
        // Implementation omitted for brevity
        Map<String, Object> payment = new HashMap<>();
        payment.put("paymentDate", System.currentTimeMillis());
        return payment;
    }
}