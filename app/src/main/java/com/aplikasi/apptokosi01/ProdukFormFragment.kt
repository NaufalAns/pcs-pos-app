package com.aplikasi.apptokosi01

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.aplikasi.apptokosi01.api.BaseRetrofit
import com.aplikasi.apptokosi01.response.login.LoginResponse
import com.aplikasi.apptokosi01.response.produk.Produk
import com.aplikasi.apptokosi01.response.produk.ProdukResponsePost
import com.aplikasi.apptokosi01.utils.SessionManager
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.log

class ProdukFormFragment : Fragment() {
    private val api by lazy { BaseRetrofit().endpoint}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_produk_form, container, false)

        val btnProses = view.findViewById<Button>(R.id.btnProses)

        val txtFormNama = view.findViewById<TextView>(R.id.txtFormNama)
        val txtFormHarga = view.findViewById<TextView>(R.id.txtFormHarga)
        val txtFormStok = view.findViewById<TextView>(R.id.txtFormStok)

        val status = arguments?.getString("status")
        val produk = arguments?.getParcelable<Produk>("produk")

        if (status=="edit") {
            txtFormNama.setText(produk?.nama.toString())
            txtFormHarga.setText(produk?.harga.toString())
            txtFormStok.setText(produk?.stok.toString())
        }


        btnProses.setOnClickListener {
            val txtNama = view.findViewById<TextInputEditText>(R.id.txtFormNama)
            val txtHarga = view.findViewById<TextInputEditText>(R.id.txtFormHarga)
            val txtStok = view.findViewById<TextInputEditText>(R.id.txtFormStok)

            if (txtNama.text.isNullOrEmpty() && txtHarga.text.isNullOrEmpty() && txtStok.text.isNullOrEmpty()) {
                Toast.makeText(activity?.applicationContext,"Harap isi semua data!",Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val token = LoginActivity.sessionManager.getString("TOKEN")
            val adminId = LoginActivity.sessionManager.getString("ADMIN_ID")

            if (status=="edit") {
                api.putProduk(token.toString(), produk?.id.toString().toInt(), adminId.toString().toInt(), txtNama.text.toString(), txtHarga.text.toString().toInt(), txtStok.text.toString().toInt()).enqueue(object :
                    Callback<ProdukResponsePost> {
                    override fun onResponse(
                        call: Call<ProdukResponsePost>,
                        response: Response<ProdukResponsePost>
                    ) {
                        Log.d("ProdukPost",response.toString())
                        Toast.makeText(activity?.applicationContext,"Data ${response.body()!!.data.produk.nama} berhasil diubah!",Toast.LENGTH_LONG).show()

                        findNavController().navigate(R.id.produkFragment)
                    }

                    override fun onFailure(call: Call<ProdukResponsePost>, t: Throwable) {
                        Log.e("ProdukPostError",t.toString())
                    }
                })
            } else {
                api.postProduk(token.toString(), adminId.toString().toInt(), txtNama.text.toString(), txtHarga.text.toString().toInt(), txtStok.text.toString().toInt(), 0).enqueue(object :
                    Callback<ProdukResponsePost> {
                    override fun onResponse(
                        call: Call<ProdukResponsePost>,
                        response: Response<ProdukResponsePost>
                    ) {
                        Log.d("ProdukPost",response.toString())
                        Toast.makeText(activity?.applicationContext,"Data berhasil ditambah!",Toast.LENGTH_LONG).show()

                        findNavController().navigate(R.id.produkFragment)
                    }

                    override fun onFailure(call: Call<ProdukResponsePost>, t: Throwable) {
                        Log.e("ProdukPostError",t.toString())
                    }
                })
            }
        }

        return view
    }

}