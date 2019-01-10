package com.mapsa.core.commits;

import java.io.Serializable;

public abstract class CommitResponse implements Serializable {
    private String commitId;
    private String commitFollowerId;
    private String creationTime;

    public CommitResponse(){
        
    }
    public String getCommitId(){
        return this.commitId;
    }

    public void setCommitId (String cuId){
        this.commitId=cuId;
    }

     public String getCommitFollowerId(){
        return this.commitFollowerId;
     }
}
