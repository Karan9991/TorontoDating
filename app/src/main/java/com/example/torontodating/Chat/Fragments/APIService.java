package com.example.torontodating.Chat.Fragments;

import com.example.torontodating.Chat.Notifications.MyResponse;
import com.example.torontodating.Chat.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAATh7WnDw:APA91bHh8z9AjM5rA-JVvE3vGYt1Opc5DteMM4nuAqAAfKsZzbSTWZNkcfaJwebVcoFb56OhCOA7yhod8u2iKoimrVYgBZMCyPBFiBhr3GdY_S_EAb6euz-l55N4hbPKH2TopZZ9ZOT3"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
