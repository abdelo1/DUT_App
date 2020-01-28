package com.example.blocnote.notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {

    @Headers({
          "Content-Type:application/json",
          "Authorization:key=AAAAdN7KhZs:APA91bGHYm-bflc02FzJg1gEw2ZHTu_p8uQORlk4mpqox8mfo6SH2A7IfKv4xT8GHNuKwt_YungGgx5sL_5AtVOHfEuLHew-k45UNwbbwzBok2iBCACIAH00MtijsClhJm7nf5N09ajr"

    })
    @POST("fcm/send")
    Call<Response> sendNotif(@Body Sender body);
}
