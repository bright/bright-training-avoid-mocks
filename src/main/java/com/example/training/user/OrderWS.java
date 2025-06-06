package com.example.training.user;

/**
 * Web service representation of an order.
 */
public class OrderWS {
    private Integer id;
    private Integer userId;
    private String statusStr;

    public OrderWS() {
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
     * Gets the status string.
     *
     * @return the status string
     */
    public String getStatusStr() {
        return statusStr;
    }

    /**
     * Sets the status string.
     *
     * @param statusStr the status string
     */
    public void setStatusStr(String statusStr) {
        this.statusStr = statusStr;
    }
}