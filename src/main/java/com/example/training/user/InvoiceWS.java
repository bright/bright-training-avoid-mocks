package com.example.training.user;

import java.util.Date;

/**
 * Web service representation of an invoice.
 */
public class InvoiceWS {
    private Integer id;
    private Integer userId;
    private Date createDatetime;

    public InvoiceWS() {
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
     * Gets the create datetime.
     *
     * @return the create datetime
     */
    public Date getCreateDatetime() {
        return createDatetime;
    }

    /**
     * Sets the create datetime.
     *
     * @param createDatetime the create datetime
     */
    public void setCreateDatetime(Date createDatetime) {
        this.createDatetime = createDatetime;
    }
}