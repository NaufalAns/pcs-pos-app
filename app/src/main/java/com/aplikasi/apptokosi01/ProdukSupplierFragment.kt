package com.aplikasi.apptokosi01

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aplikasi.apptokosi01.adapter.ProdukSupplierAdapter
import com.aplikasi.apptokosi01.api.BaseRetrofit
import com.aplikasi.apptokosi01.response.cart.Cart
import com.aplikasi.apptokosi01.response.itemTransaksi.ItemTransaksiResponsePost
import com.aplikasi.apptokosi01.response.produk.ProdukResponse
import com.aplikasi.apptokosi01.response.transaksi.TransaksiResponsePost
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.*


class ProdukSupplierFragment : Fragment() {

    private val api by lazy { BaseRetrofit().endpoint }
    private lateinit var my_cart : ArrayList<Cart>
    private lateinit var total_bayar : String
    private lateinit var builder: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_produk_supplier, container, false)

        val btnBayar = view.findViewById<Button>(R.id.btnBayar)

        getProduk(view)

        btnBayar.setOnClickListener {
            dialogPembelian(view)
        }

        return view
    }

    fun getProduk(view:View){
        val token = LoginActivity.sessionManager.getString("TOKEN")

        api.getProdukSupplier(token.toString()).enqueue(object : Callback<ProdukResponse> {
            override fun onResponse(
                call: Call<ProdukResponse>,
                response: Response<ProdukResponse>
            ) {
                Log.d("ProdukData",response.body().toString())

                val rv = view.findViewById(R.id.rv_produk_supplier) as RecyclerView

                rv.setHasFixedSize(true)
                rv.layoutManager = LinearLayoutManager(activity)
                val rvAdapter = ProdukSupplierAdapter(response.body()!!.data.produk)
                rv.adapter = rvAdapter

                rvAdapter.callbackInterface = object : CallbackInterface{
                    override fun passResultCallback(total: String, cart: ArrayList<Cart>) {
                        val txtTotalBayar = activity?.findViewById<TextView>(R.id.txtTotalBayar)

                        val localeID = Locale("in", "ID")
                        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
                        txtTotalBayar?.setText(numberFormat.format(total.toDouble()).toString())

                        total_bayar = total
                        my_cart = cart

                        Log.d("MyCart",cart.toString())
                    }
                }
            }

            override fun onFailure(call: Call<ProdukResponse>, t: Throwable) {
                Log.e("ProdukError",t.toString())
            }
        })
    }

    fun dialogPembelian(view: View) {
        AlertDialog.Builder(view.context)
            .setTitle("Konfirmasi Pembelian!")
            .setMessage("Apakah Anda yakin ingin membeli produk dari supplier?")
            .setCancelable(true)
            .setPositiveButton("Ya") {
                dialogInterface, it -> bayar(view)
            }
            .setNegativeButton("Tidak") {
                dialogInterface, it -> dialogInterface.cancel()
            }
            .show()
    }

    fun bayar(view: View) {
        val token = LoginActivity.sessionManager.getString("TOKEN")
        val adminId = LoginActivity.sessionManager.getString("ADMIN_ID")

        api.postTransaksi(token.toString(), adminId.toString().toInt(), total_bayar.toInt(), "pembelian").enqueue(object : Callback<TransaksiResponsePost>{
            override fun onResponse(
                call: Call<TransaksiResponsePost>,
                response: Response<TransaksiResponsePost>
            ) {
                if (response.isSuccessful) {
                    val id_transaksi = response.body()!!.data.transaksi.id
                    Log.e("id_transaksi", id_transaksi)

                    for(item in my_cart!!){
                        api.postItemTransaksiSupplier(token.toString(), adminId.toString().toInt(), id_transaksi.toInt(),item.id,item.qty,item.harga).enqueue(object : Callback<ItemTransaksiResponsePost>{
                            override fun onResponse(
                                call: Call<ItemTransaksiResponsePost>,
                                response: Response<ItemTransaksiResponsePost>
                            ) {

                            }

                            override fun onFailure(
                                call: Call<ItemTransaksiResponsePost>,
                                t: Throwable
                            ) {
                                Log.e("Error", t.toString())
                            }

                        })
                    }

                    Toast.makeText(view.context, "Pembelian barang dari supplier barhasil!", Toast.LENGTH_LONG).show()

                    findNavController().navigate(R.id.produkFragment)
                } else {
                    if (response.code() == 400) {
                        Toast.makeText(view.context, "Pendapatan tidak mencukupi!", Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<TransaksiResponsePost>, t: Throwable) {
                Log.e("Error", t.toString())
            }
        })
    }
}