package com.example.training.user;

/**
 * Web service representation of payment information.
 */
public class PaymentInformationWS {
    private Integer id;
    private String paymentMethodType;
    private String processingOrder;
    private boolean deleted;

    public PaymentInformationWS() {
        // Default constructor
    }

    /**
     * Gets the ID.
     *
     * @return the ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets the ID.
     *
     * @param id the ID
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Gets the payment method type.
     *
     * @return the payment method type
     */
    public String getPaymentMethodType() {
        return paymentMethodType;
    }

    /**
     * Sets the payment method type.
     *
     * @param paymentMethodType the payment method type
     */
    public void setPaymentMethodType(String paymentMethodType) {
        this.paymentMethodType = paymentMethodType;
    }

    /**
     * Gets the processing order.
     *
     * @return the processing order
     */
    public String getProcessingOrder() {
        return processingOrder;
    }

    /**
     * Sets the processing order.
     *
     * @param processingOrder the processing order
     */
    public void setProcessingOrder(String processingOrder) {
        this.processingOrder = processingOrder;
    }

    /**
     * Checks if the payment information is deleted.
     *
     * @return true if deleted, false otherwise
     */
    public boolean isDeleted() {
        return deleted;
    }

    /**
     * Sets whether the payment information is deleted.
     *
     * @param deleted true if deleted, false otherwise
     */
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    /**
     * Gets the meta fields.
     *
     * @return the meta fields
     */
    public MetaFieldValueWS[] getMetaFields() {
        return new MetaFieldValueWS[0];
    }
}