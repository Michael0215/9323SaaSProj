package com.example.comp9323_saasproj.bean;

/**
 * 发布的帖子的实体类
 * @author : autumn_leaf
 */
public class Commodity {

    //编号
    private String id;
    //标题
    private String title;
    //类别
    private String category;
    //联系方式
    private String phone;
    //描述
    private String description;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {this.title = title;}

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDescription() {
        return description;
    }
//    public String getDescription() {
//        return description;
//    }

    public void setDescription(String description) {
        this.description = description;
    }

}
