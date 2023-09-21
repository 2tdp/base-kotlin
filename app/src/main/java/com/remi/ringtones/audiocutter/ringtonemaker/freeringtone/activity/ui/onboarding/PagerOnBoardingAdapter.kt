package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui.onboarding

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
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
            binding.ivItem.layoutParams.width = (w * 100f).toInt()
            if (type == 0) {
                binding.ctlTip.visibility = View.GONE
                binding.ctlOnBoarding.visibility = View.VISIBLE
            } else if (type == 1) {
                binding.ctlOnBoarding.visibility = View.GONE
                binding.ctlTip.visibility = View.VISIBLE
            }
        }

        fun onBind(position: Int) {
            val item = lstPage[position]

            if (item.imgPage != -1) binding.ivItem.setImageResource(item.imgPage)
            if (item.imgIcon != -1) binding.iv.setImageResource(item.imgIcon)
            binding.tvDes.text = item.str
            binding.tvDes2.text = item.str
            binding.tvTitle.text = item.strTitle
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun notifyChange() {
        notifyDataSetChanged()
    }
}