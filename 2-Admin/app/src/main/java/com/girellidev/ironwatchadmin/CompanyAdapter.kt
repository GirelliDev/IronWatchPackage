package com.girellidev.ironwatchadmin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class Company(val nome: String, val isActive: Int)

class CompanyAdapter : RecyclerView.Adapter<CompanyAdapter.CompanyViewHolder>() {

    private val companies = mutableListOf<Company>()

    fun setCompanies(list: List<Company>) {
        companies.clear()
        companies.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompanyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_company, parent, false)
        return CompanyViewHolder(view)
    }

    override fun onBindViewHolder(holder: CompanyViewHolder, position: Int) {
        val company = companies[position]
        holder.companyName.text = company.nome
        holder.statusDot.setBackgroundColor(if (company.isActive == 1) 0xFF00FF00.toInt() else 0xFFFF0000.toInt())
    }

    override fun getItemCount(): Int = companies.size

    class CompanyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val companyName: TextView = view.findViewById(R.id.companyName)
        val statusDot: View = view.findViewById(R.id.statusDot)
    }
}
