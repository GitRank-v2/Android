package com.dragonguard.android.ui.menu.org.search

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dragonguard.android.data.model.org.OrganizationNamesModelItem
import com.dragonguard.android.databinding.RepositoryListBinding
import com.dragonguard.android.ui.menu.org.auth.AuthOrgActivity

class SearchOrganizationAdapter(
    private val context: Context,
) : ListAdapter<OrganizationNamesModelItem, SearchOrganizationAdapter.ViewHolder>(differ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            RepositoryListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }


    //리사이클러 뷰의 요소들을 넣어줌
    inner class ViewHolder(private val binding: RepositoryListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        //클릭리스너 구현
        fun bind(data: OrganizationNamesModelItem) {
            binding.repoName.text = data.name
            binding.repoName.setOnClickListener {
                val authOrg = context as SearchOrganizationActivity
                val intentW = Intent(context, AuthOrgActivity::class.java)
                intentW.putExtra("orgName", data.name)
                intentW.putExtra("orgId", data.id)
                intentW.putExtra("endPoint", data.email_endpoint)
                authOrg.setResult(0, intentW)
                authOrg.finish()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val differ = object : DiffUtil.ItemCallback<OrganizationNamesModelItem>() {
            override fun areItemsTheSame(
                oldItem: OrganizationNamesModelItem,
                newItem: OrganizationNamesModelItem
            ) = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: OrganizationNamesModelItem,
                newItem: OrganizationNamesModelItem
            ) = oldItem == newItem
        }
    }
}