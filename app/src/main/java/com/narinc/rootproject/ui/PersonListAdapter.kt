package com.narinc.rootproject.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.narinc.rootproject.base.BaseAdapter
import com.narinc.rootproject.databinding.ItemPersonBinding
import com.narinc.rootproject.presenter.Person
import javax.inject.Inject

class PersonListAdapter @Inject constructor() : BaseAdapter<Person>() {

    private val diffCallback = object : DiffUtil.ItemCallback<Person>() {
        override fun areItemsTheSame(oldItem: Person, newItem: Person): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Person, newItem: Person): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    override val differ = AsyncListDiffer(this, diffCallback)

    override fun getViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            ItemPersonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemPersonViewHolder(binding)
    }

    inner class ItemPersonViewHolder(private val binding: ItemPersonBinding) :
        RecyclerView.ViewHolder(binding.root), Binder<Person> {
        override fun bind(item: Person) {
            binding.apply {
                tvPerson.text = String.format("%s (%d)", item.fullName, item.id)
            }
        }
    }
}
