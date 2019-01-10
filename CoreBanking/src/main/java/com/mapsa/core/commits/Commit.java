package com.mapsa.core.commits;

import java.io.Serializable;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.time.Instant;

public abstract class Commit implements Serializable {
    private BigInteger number;
    private String CUID;
    private String transactionId;
    private String requestId;
    private String sourceSignature;
    private String validationSignature;
    private String commitFollowerId;
    private String creationTime;

    public Commit() {
        this.creationTime = String.valueOf(Instant.now().toEpochMilli());
    }

    public BigInteger getNumber() {
        return number;
    }

    public void setNumber(BigInteger number) {
        this.number = number;
    }

    public String getCUID() {
        return CUID;
    }

    public void setCUID(String CUID) {
        this.CUID = CUID;
    }

    public String getSourceSignature() {
        return sourceSignature;
    }

    public void setSourceSignature(String sourceSignature) {
        this.sourceSignature = sourceSignature;
    }

    public String getValidationSignature() {
        return validationSignature;
    }

    public String getCommitFollowerId() {
        return commitFollowerId;
    }
}
