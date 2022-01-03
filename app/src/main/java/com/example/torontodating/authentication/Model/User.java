package com.example.torontodating.authentication.Model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class User {

    public String name;
    public String mobile;
    public String email;
    public String password;
    public String address;
    public String userType;
    public String imageURL;
    public String status;
    public String id;
    public String username;
    public String search;
    public String age;
    public String gender;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String name, String age, String email, String password, String imageURL) {
        this.name = name;
        this.age = age;
        this.email = email;
        this.password = password;
        this.imageURL = imageURL;
    }

    public User(String name, String age) {
        this.name = name;
        this.age = age;
    }

    public User(String name, String age, String imageURL) {
        this.name = name;
        this.age = age;
        this.imageURL = imageURL;
    }

    public User(String name, String mobile, String email, String password, String address, String userType) {
        this.name = name;
        this.mobile = mobile;
        this.email = email;
        this.password = password;
        this.address = address;
        this.userType = userType;
    }

    public User(String name, String email, String password, String imageURL, String status, String id, String username, String search, String age) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.imageURL = imageURL;
        this.status = status;
        this.id = id;
        this.username = username;
        this.search = search;
        this.age = age;
    }

    //    public User(String name, String mobile, String email, String password, String address, String userType, String imageURL, String status, String id) {
//        this.name = name;
//        this.mobile = mobile;
//        this.email = email;
//        this.password = password;
//        this.address = address;
//        this.userType = userType;
//        this.imageURL = imageURL;
//        this.status = status;
//        this.id = id;
//    }
    public User(String name, String mobile, String email, String password, String address, String userType, String imageURL, String status, String id, String username, String search) {
        this.name = name;
        this.mobile = mobile;
        this.email = email;
        this.password = password;
        this.address = address;
        this.userType = userType;
        this.imageURL = imageURL;
        this.status = status;
        this.id = id;
        this.username = email;
        this.search = search;
    }

    public User(String name, String email, String password, String imageURL, String status, String id, String username, String search, String age, String gender) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.imageURL = imageURL;
        this.status = status;
        this.id = id;
        this.username = username;
        this.search = search;
        this.age = age;
        this.gender = gender;
    }

    public String getUsername() {
        return email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("name", name);
        result.put("age", age);
        result.put("imageURL", imageURL);
        result.put("username", username);
        result.put("email", email);
        result.put("status", "offline");
        result.put("search", email);
        result.put("password", password);
        result.put("gender", gender);
        return result;
    }
}
