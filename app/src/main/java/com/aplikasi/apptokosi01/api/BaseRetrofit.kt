package com.aplikasi.apptokosi01.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class BaseRetrofit {
    val endpoint : ApiEndPoint
        get(){
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY

            val client = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build()

            val retrofit = Retrofit.Builder()
//                .baseUrl("http://pcs.donnipra.com/index.php/api_pcs/")
//                .baseUrl("http://10.0.2.2/amikom/ci-pcs/index.php/api_pcs/")
                .baseUrl("http://192.168.233.1/amikom/ci-pcs/index.php/api_pcs/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(ApiEndPoint::class.java)
        }
}