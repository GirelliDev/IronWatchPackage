package com.girellidev.ironwatchadmin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CompanyAdapter(
    private val onCompanyClick: (Company) -> Unit
) : RecyclerView.Adapter<CompanyAdapter.CompanyViewHolder>() {

    private val companies = mutableListOf<Company>()

    fun setCompanies(newCompanies: List<Company>) {
        companies.clear()
        companies.addAll(newCompanies)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompanyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_company, parent, false)
        return CompanyViewHolder(view)
    }

    override fun onBindViewHolder(holder: CompanyViewHolder, position: Int) {
        holder.bind(companies[position], onCompanyClick)
    }

    override fun getItemCount(): Int = companies.size

    class CompanyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val viewStatus: View = itemView.findViewById(R.id.viewStatus)
        private val txtCompanyName: TextView = itemView.findViewById(R.id.txtCompanyName)
        private val txtCompanyStatus: TextView = itemView.findViewById(R.id.txtCompanyStatus)
        private val txtStatusBadge: TextView = itemView.findViewById(R.id.txtStatusBadge)

        fun bind(company: Company, onCompanyClick: (Company) -> Unit) {
            val isOnline = company.isActive == 1

            txtCompanyName.text = company.nome

            if (isOnline) {
                viewStatus.setBackgroundResource(R.drawable.status_online_circle)
                txtCompanyStatus.text = "Sistema ativo e conectado"
                txtStatusBadge.text = "ONLINE"
                txtStatusBadge.setTextColor(0xFF00E676.toInt())
            } else {
                viewStatus.setBackgroundResource(R.drawable.status_offline_circle)
                txtCompanyStatus.text = "Sistema inativo ou desconectado"
                txtStatusBadge.text = "OFFLINE"
                txtStatusBadge.setTextColor(0xFFE6001B.toInt())
            }

            itemView.setOnClickListener {
                onCompanyClick(company)
            }
        }
    }
}