package com.example.training.payment;

/**
 * Data transfer object for payment authorization.
 */
public class PaymentAuthorizationDTOEx {
    private String transactionId;
    private Integer paymentId;
    private boolean result;

    public PaymentAuthorizationDTOEx() {
        // Default constructor
    }

    /**
     * Gets the transaction ID.
     *
     * @return the transaction ID
     */
    public String getTransactionId() {
        return transactionId;
    }

    /**
     * Sets the transaction ID.
     *
     * @param transactionId the transaction ID
     */
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    /**
     * Gets the payment ID.
     *
     * @return the payment ID
     */
    public Integer getPaymentId() {
        return paymentId;
    }

    /**
     * Sets the payment ID.
     *
     * @param paymentId the payment ID
     */
    public void setPaymentId(Integer paymentId) {
        this.paymentId = paymentId;
    }

    /**
     * Gets the result.
     *
     * @return the result
     */
    public boolean getResult() {
        return result;
    }

    /**
     * Sets the result.
     *
     * @param result the result
     */
    public void setResult(boolean result) {
        this.result = result;
    }
}