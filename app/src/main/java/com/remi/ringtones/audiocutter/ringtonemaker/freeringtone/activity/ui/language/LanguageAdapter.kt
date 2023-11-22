package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui.language

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.R
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.data.db.language.LanguageModel
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.callback.ICallBackItem
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.databinding.ItemLanguageBinding
import javax.inject.Inject

class LanguageAdapter @Inject constructor(): ListAdapter<LanguageModel, LanguageAdapter.LanguageHolder>(DiffCallback()) {

    private lateinit var context: Context
    private lateinit var callBack: ICallBackItem
    private var w = 0f

    fun newInstance(context: Context, callBackItem: ICallBackItem) {
        this.context = context
        this.callBack = callBackItem

        w = context.resources.displayMetrics.widthPixels / 100f
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageHolder {
        return LanguageHolder(ItemLanguageBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: LanguageHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    inner class LanguageHolder(private val binding: ItemLanguageBinding): RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.layoutParams.height = (15.556f * w).toInt()
        }

        fun onBind(lang: LanguageModel) {
            Glide.with(context)
                .asBitmap()
                .load("file:///android_asset/${lang.uri}/${lang.name.lowercase()}.png")
                .into(binding.ivFlag)

            binding.tvName.text = lang.name

            if (lang.isCheck) binding.ivChoose.setImageResource(R.drawable.ic_choose)
            else binding.ivChoose.setImageResource(R.drawable.ic_un_choose)

            binding.root.setOnClickListener {
                setCurrent(layoutPosition)
                callBack.callBack(lang, layoutPosition)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<LanguageModel>() {
        override fun areItemsTheSame(oldItem: LanguageModel, newItem: LanguageModel) = oldItem.name == newItem.name

        override fun areContentsTheSame(oldItem: LanguageModel, newItem: LanguageModel) = oldItem == newItem
    }

    fun setCurrent(position: Int) {
        for (pos in currentList.indices) currentList[pos].isCheck = pos == position

        changeNotify()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun changeNotify() {
        notifyDataSetChanged()
    }
}