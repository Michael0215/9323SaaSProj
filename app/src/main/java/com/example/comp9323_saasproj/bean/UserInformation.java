package com.example.comp9323_saasproj.bean;

public class UserInformation {

    // The class of an user.
    // It is never used because we made use of another similar one in models package.
    // Still keep this to show our work.

    public String type;
    public String Id;
    public String Name;
    public String Faculty;
    public String MailAddress;
    public String Course;

    public UserInformation(){}

    public UserInformation(String Id,String Name,String Faculty,String MailAddress,String Course, String type){
        this.Id = Id;
        this.Name=Name;
        this.Faculty=Faculty;
        this.MailAddress = MailAddress;
        this.Course = Course;
        this.type = type;
    }

    public String getType(){
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
}
