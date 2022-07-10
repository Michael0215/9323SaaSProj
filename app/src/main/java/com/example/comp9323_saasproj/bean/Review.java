package com.example.comp9323_saasproj.bean;

/**
 * 评论实体类
 * @author autumn_leaf
 */
public class Review {

    private String postID ;//商品项编号
    private String phone;//用户账号
    private String currentTime;//当前时间
    private String content;//评论内容


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }
}



