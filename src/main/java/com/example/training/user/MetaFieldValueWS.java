package com.example.training.user;

/**
 * Web service representation of a meta field value.
 */
public class MetaFieldValueWS {
    private Integer id;
    private String fieldName;
    private String stringValue;
    private Integer groupId;

    public MetaFieldValueWS() {
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
     * Gets the field name.
     *
     * @return the field name
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * Sets the field name.
     *
     * @param fieldName the field name
     */
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * Gets the string value.
     *
     * @return the string value
     */
    public String getStringValue() {
        return stringValue;
    }

    /**
     * Sets the string value.
     *
     * @param stringValue the string value
     */
    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    /**
     * Gets the group ID.
     *
     * @return the group ID
     */
    public Integer getGroupId() {
        return groupId;
    }

    /**
     * Sets the group ID.
     *
     * @param groupId the group ID
     */
    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }
}