package com.rjx.gogu02.domain;

/**
 * Created by renjiaxing on 15/7/14.
 */
public class Poll {

    private String id;
    private String topic;
    private String votenum;
    private String created_at;
    private String updated_at;

    public Poll(String id, String topic,String votenum, String created_at, String updated_at) {
        this.id = id;
        this.topic = topic;
        this.votenum = votenum;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getVotenum() {
        return votenum;
    }

    public void setVotenum(String votenum) {
        this.votenum = votenum;
    }


}
