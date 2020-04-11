package com.example.imageupload.data.remote;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.reactivex.rxjava3.core.Single;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface Api {

    String BASE_URL = "https://www.finnovationz.com/";

    //@FormUrlEncoded
    @Multipart
    @POST("dummy/upload.php/")
    Call<ResponseBody> postPhoto(@Part MultipartBody.Part file);
    //void postPhoto(@Field("To") String to, @Field("Body") String body);

}
