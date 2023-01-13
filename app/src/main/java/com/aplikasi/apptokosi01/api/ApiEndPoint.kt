package com.aplikasi.apptokosi01.api

import android.content.ClipData.Item
import com.aplikasi.apptokosi01.response.itemTransaksi.ItemTransaksiResponsePost
import com.aplikasi.apptokosi01.response.login.LoginResponse
import com.aplikasi.apptokosi01.response.produk.ProdukResponse
import com.aplikasi.apptokosi01.response.transaksi.TransaksiResponse
import com.aplikasi.apptokosi01.response.transaksi.TransaksiResponsePost
import retrofit2.Call
import retrofit2.http.*

interface ApiEndPoint {
    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email :String,
        @Field("password") password : String
    ):Call<LoginResponse>

    @GET("produk")
    fun getProduk(
        @Header("Authorization") token: String
    ): Call<ProdukResponse>

    @FormUrlEncoded
    @POST("transaksi")
    fun postTransaksi(
        @Header("Authorization") token : String,
        @Field("admin_id") admin_id : Int,
        @Field("total") total : Int
    ) : Call <TransaksiResponsePost>

    @FormUrlEncoded
    @POST("item_transaksi")
    fun postItemTransaksi(
        @Header("Authorization") token : String,
        @Field("transaksi_id") transaksi_id : Int,
        @Field("produk_id") produk_id : Int,
        @Field("qty") qty : Int,
        @Field("harga_saat_transaksi") harga_saat_transaksi : Int
    ) : Call <ItemTransaksiResponsePost>
}