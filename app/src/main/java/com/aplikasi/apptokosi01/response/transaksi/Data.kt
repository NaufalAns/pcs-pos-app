package com.aplikasi.apptokosi01.response.transaksi

data class Data(
    val transaksi: List<Transaksi>,
    val pendapatan_kotor: Int,
    val pendapatan_bersih: Int,
)