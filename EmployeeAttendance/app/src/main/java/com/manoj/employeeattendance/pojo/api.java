package com.manoj.employeeattendance.pojo;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface api {
    String baseurl = "http://parxsys.com/accounting/";

    @GET("att_rprt_api.php?e76c37b493ea168cea60b8902072387caf297979")
    Call<List<employeelist>> getempList();


    @FormUrlEncoded
    @POST("att_rprt_api.php?e76c37b493ea168cea60b8902072387caf297979")
    Call<List<employeeDetails>> getempDetails(
            @Field("emp_id") String emp_id,
            @Field("from_dt") String from_dt,
            @Field("to_dt") String to_dt
    );



}
