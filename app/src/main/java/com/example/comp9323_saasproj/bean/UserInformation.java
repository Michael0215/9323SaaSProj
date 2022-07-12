package com.example.comp9323_saasproj.bean;

public class UserInformation {
    public String Id;
    public String Name;
    public String Faculty;
    public String MailAddress;
    public String Course;
    public UserInformation(){

    }
    public UserInformation(String Id,String Name,String Faculty,String MailAddress,String Course){
        this.Id = Id;
        this.Name=Name;
        this.Faculty=Faculty;
        this.MailAddress = MailAddress;
        this.Course = Course;

    }
   /* public String getId(){
        return Id;
    }
    public String getName (){
        return Name;
    }
    public String getFaculty (){
        return Faculty;
    }
    public String getMailAddress (){
        return MailAddress;
    }*/




}
