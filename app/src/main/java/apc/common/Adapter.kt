package apc.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

open class Adapter<T>(private val itemRes: Int = 0, callback: DiffUtil.ItemCallback<T> = Callback()) :
    ListAdapter<T, Holder>(callback) {
    override fun getItemViewType(position: Int) = itemRes
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        Holder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), viewType, parent, false))

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bindTo((getItem(position)))
    }
}

open class PairAdapter<K, V>(itemRes: Int = 0) : Adapter<Pair<K, V>>(itemRes, PairCallback())

class Holder(private val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bindTo(item: Any?) {
        binding.setVariable(BR.item, item)
        binding.executePendingBindings()
    }
}

open class Callback<T> : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T) = oldItem == newItem
    override fun areContentsTheSame(oldItem: T, newItem: T) = oldItem == newItem
}

class PairCallback<K, V> : Callback<Pair<K, V>>() {
    override fun areItemsTheSame(oldItem: Pair<K, V>, newItem: Pair<K, V>) = oldItem.first == newItem.first
}