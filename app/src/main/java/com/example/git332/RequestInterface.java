package com.example.git332;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RequestInterface {

    @GET("/img/fatchimg.json")
    Call<JSONResponse> getJSON();
}
