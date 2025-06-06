package com.example.training.common;

import java.util.Date;
import java.util.List;

/**
 * Represents an aging process for a user.
 * This class is used to track the status change of a user.
 */
public class AgingProcess {
    private String id;
    private String accountPin;
    private String userId;
    private String brandId;
    private String taskId;
    private String status;
    private String stage;
    private Date lastPaymentDate;
    private Date promiseDate;
    private Date processDate;
    private Date createdDate;
    private Date updatedDate;
    private String fromStatus;
    private String exception;
    private List<Object> assetIds;
    private List<Object> orderIds;

    public AgingProcess() {
        // Default constructor
    }

    public AgingProcess(String id, String accountPin, String userId, String brandId, String taskId, String status, String stage,
                        Date lastPaymentDate, Date promiseDate, Date processDate, Date createdDate, Date updatedDate,
                        String fromStatus, String exception, List<Object> assetIds, List<Object> orderIds) {
        this.id = id;
        this.accountPin = accountPin;
        this.userId = userId;
        this.brandId = brandId;
        this.taskId = taskId;
        this.status = status;
        this.stage = stage;
        this.lastPaymentDate = lastPaymentDate;
        this.promiseDate = promiseDate;
        this.processDate = processDate;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.fromStatus = fromStatus;
        this.exception = exception;
        this.assetIds = assetIds;
        this.orderIds = orderIds;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccountPin() {
        return accountPin;
    }

    public void setAccountPin(String accountPin) {
        this.accountPin = accountPin;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBrandId() {
        return brandId;
    }

    public void setBrandId(String brandId) {
        this.brandId = brandId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public Date getLastPaymentDate() {
        return lastPaymentDate;
    }

    public void setLastPaymentDate(Date lastPaymentDate) {
        this.lastPaymentDate = lastPaymentDate;
    }

    public Date getPromiseDate() {
        return promiseDate;
    }

    public void setPromiseDate(Date promiseDate) {
        this.promiseDate = promiseDate;
    }

    public Date getProcessDate() {
        return processDate;
    }

    public void setProcessDate(Date processDate) {
        this.processDate = processDate;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getFromStatus() {
        return fromStatus;
    }

    public void setFromStatus(String fromStatus) {
        this.fromStatus = fromStatus;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public List<Object> getAssetIds() {
        return assetIds;
    }

    public void setAssetIds(List<Object> assetIds) {
        this.assetIds = assetIds;
    }

    public List<Object> getOrderIds() {
        return orderIds;
    }

    public void setOrderIds(List<Object> orderIds) {
        this.orderIds = orderIds;
    }
}