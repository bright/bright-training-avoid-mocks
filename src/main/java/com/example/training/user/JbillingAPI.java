package com.example.training.user;

import java.util.Date;

/**
 * Interface for interacting with the Jbilling API.
 */
public interface JbillingAPI {
    /**
     * Gets the user's subscriptions.
     *
     * @param userId the user ID
     * @return an array of OrderWS objects
     */
    default OrderWS[] getUserSubscriptions(Integer userId) {
        return new OrderWS[0];
    }

    /**
     * Gets a user by customer meta field.
     *
     * @param value the meta field value
     * @param metaFieldName the meta field name
     * @return a UserWS object
     */
    default UserWS getUserByCustomerMetaField(String value, String metaFieldName) {
        return new UserWS();
    }

    /**
     * Gets orders by period.
     *
     * @param userId the user ID
     * @param period the period
     * @return an array of order IDs
     */
    default Integer[] getOrderByPeriod(Integer userId, Integer period) {
        return new Integer[0];
    }

    /**
     * Creates an invoice.
     *
     * @param userId the user ID
     * @param isRecurring whether the invoice is recurring
     * @return an array of invoice IDs
     */
    default Integer[] createInvoice(Integer userId, boolean isRecurring) {
        return new Integer[0];
    }

    /**
     * Notifies an invoice by email.
     *
     * @param invoiceId the invoice ID
     */
    default void notifyInvoiceByEmail(Integer invoiceId) {
        // Implementation omitted for brevity
    }

    /**
     * Gets the latest invoice.
     *
     * @param userId the user ID
     * @return an InvoiceWS object
     */
    default InvoiceWS getLatestInvoice(Integer userId) {
        return new InvoiceWS();
    }
}