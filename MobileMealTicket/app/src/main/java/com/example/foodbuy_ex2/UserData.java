package com.example.foodbuy_ex2;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;


@IgnoreExtraProperties
public class UserData {

    public String userId;
    public String userNickName;
    public int ticket=0;

    public UserData() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }


    public UserData(String userId, String userNickName) {
        this.userId = userId;
        this.userNickName = userNickName;
    }

   public Map<String, Object> toMap() {
       HashMap<String, Object> result = new HashMap<>();
       result.put("userId", userId);
       result.put("userNickName", userNickName);
       result.put("ticket", ticket);

       return result;
   }


   public String getUserId(){
        return userId;
   }

    public String getUserNickName(){
        return userNickName;
    }

    public int getUserticket(){
        return ticket;
    }

}


