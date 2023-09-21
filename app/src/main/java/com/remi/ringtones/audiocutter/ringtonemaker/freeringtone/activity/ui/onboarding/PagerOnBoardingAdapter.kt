package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui.onboarding

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.data.db.TipsModel
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.databinding.ItemOnBoardingBinding
import javax.inject.Inject

class PagerOnBoardingAdapter @Inject constructor() :
    RecyclerView.Adapter<PagerOnBoardingAdapter.PagerHolder>() {

    private lateinit var context: Context
    private var type: Int = -1
    private lateinit var lstPage: MutableList<TipsModel>
    private var w = 0f

    fun newInstance(context: Context, type: Int) {
        this.context = context
        this.type = type

        w = context.resources.displayMetrics.widthPixels / 100f
    }

    fun setData(lstTip: MutableList<TipsModel>) {
        this.lstPage = lstTip

        notifyChange()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerHolder {
        return PagerHolder(
            ItemOnBoardingBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return if (lstPage.isNotEmpty()) lstPage.size else 0
    }

    override fun onBindViewHolder(holder: PagerHolder, position: Int) {
        holder.onBind(position)
    }

    inner class PagerHolder(private val binding: ItemOnBoardingBinding) : ViewHolder(binding.root) {

        init {

        }

        fun onBind(position: Int) {
            val item = lstPage[position]
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun notifyChange() {
        notifyDataSetChanged()
    }
}