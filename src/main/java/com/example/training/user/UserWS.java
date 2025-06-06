package com.example.training.user;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Web service representation of a user.
 */
public class UserWS {
    private Integer userId;
    private Integer id;
    private BigDecimal owingBalance = BigDecimal.ZERO;
    private CustomerNoteWS[] customerNotes;
    private List<PaymentInformationWS> paymentInstruments = new ArrayList<>();

    public UserWS() {
        // Default constructor
    }

    /**
     * Gets the user ID.
     *
     * @return the user ID
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * Sets the user ID.
     *
     * @param userId the user ID
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
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
     * Gets the owing balance as a decimal.
     *
     * @return the owing balance
     */
    public BigDecimal getOwingBalanceAsDecimal() {
        return owingBalance;
    }

    /**
     * Sets the owing balance.
     *
     * @param owingBalance the owing balance
     */
    public void setOwingBalance(BigDecimal owingBalance) {
        this.owingBalance = owingBalance;
    }

    /**
     * Gets the customer notes.
     *
     * @return the customer notes
     */
    public CustomerNoteWS[] getCustomerNotes() {
        return customerNotes;
    }

    /**
     * Sets the customer notes.
     *
     * @param customerNotes the customer notes
     */
    public void setCustomerNotes(CustomerNoteWS[] customerNotes) {
        this.customerNotes = customerNotes;
    }

    /**
     * Gets the payment instruments.
     *
     * @return the payment instruments
     */
    public List<PaymentInformationWS> getPaymentInstruments() {
        return paymentInstruments;
    }

    /**
     * Sets the payment instruments.
     *
     * @param paymentInstruments the payment instruments
     */
    public void setPaymentInstruments(List<PaymentInformationWS> paymentInstruments) {
        this.paymentInstruments = paymentInstruments;
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