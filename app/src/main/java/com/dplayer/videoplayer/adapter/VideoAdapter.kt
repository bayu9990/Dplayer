package com.dplayer.videoplayer.adapter

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dplayer.videoplayer.R
import com.dplayer.videoplayer.data.VideoModel
import com.dplayer.videoplayer.databinding.DetailsBinding
import com.dplayer.videoplayer.databinding.VideoItemBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class VideoAdapter(private var data: ArrayList<VideoModel>): RecyclerView.Adapter<VideoAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoAdapter.ViewHolder {
        val binding = VideoItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    fun filtered(data: ArrayList<VideoModel>){
        this.data = data
        notifyDataSetChanged()
    }

    private fun size(s:Long): String{
        val kilobyte = 1024L
        val megabyte = kilobyte * 1024
        val gigabyte = megabyte * 1024
        return when{
            s < kilobyte ->{
                "$s B"
            } s < megabyte ->{
                String.format("%.2f KB",(s.toDouble()/kilobyte))
            } s < gigabyte ->{
                String.format("%.2f MB",(s.toDouble()/megabyte))
            } s >= gigabyte ->{
                String.format("%.2f GB",(s.toDouble()/gigabyte))
            }
            else->{
                String.format("%.2f GB",(s.toDouble()/gigabyte))
            }
        }
    }

    private fun duration(dur:Long):String{
        val sec = dur/1000%60
        val min = dur/1000/60
        val hour = min/60

        return if (hour>0) String.format("%02d:%02d:%02d",hour,min,sec) else String.format("%02d:%02d",min,sec)
    }

    private fun details(context: Context,title:String,duration: String,size:String,location:String,date: String?){
        val dialog = BottomSheetDialog(context)
        val binding = DetailsBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)

        binding.title.text = title
        binding.size.text = size
        binding.duration.text = duration
        binding.location.text = location
        binding.date.text = date

        dialog.show()
    }

    override fun onBindViewHolder(holder: VideoAdapter.ViewHolder, position: Int) {
        val vid = data[position]
        holder.bind(vid)
        Glide
            .with(holder.itemView.context)
            .load(vid.thumb)
            .error(R.drawable.thumb)
            .into(holder.binding.pic)
        holder.binding.details.setOnClickListener {
            vid.title?.let { it1 ->
                details(holder.itemView.context,
                    it1,duration(vid.duration),size(vid.size),vid.path,vid.date)
            }
        }
    }

    override fun getItemCount(): Int {
       return data.size
    }

    inner class ViewHolder(val binding: VideoItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: VideoModel){
            binding.title.text = item.title

            binding.duration.text = duration(item.duration)
        }
    }
}