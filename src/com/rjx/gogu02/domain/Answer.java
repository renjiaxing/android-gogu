package com.rjx.gogu02.domain;

/**
 * Created by renjiaxing on 15/7/14.
 */
public class Answer {

    private String id;
    private String content;
    private String created_at;
    private String updated_at;
    private String choose;
    private String percentage;

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }

    public String getChoose() {
        return choose;
    }

    public void setChoose(String choose) {
        this.choose = choose;
    }

    public Answer(String id, String content, String created_at, String updated_at,String choose,
                  String percentage) {
        this.id = id;
        this.content = content;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.choose=choose;
        this.percentage=percentage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
}
