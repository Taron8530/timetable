package com.example.timetable

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory



class ApiClient {
    companion object{
        private val URL = "https://open.neis.go.kr/hub/"
        private var retrofit : Retrofit? = null
        fun getRetrofit() : Retrofit{
            if(retrofit == null){
                var gson : Gson =
                    GsonBuilder()
                        .setLenient()
                        .create();
                retrofit = Retrofit.Builder()
                    .baseUrl(URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
            }
            return retrofit!!
        }
    }
}