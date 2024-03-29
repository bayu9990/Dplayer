package com.dplayer.videoplayer

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.storage.StorageManager
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dplayer.videoplayer.adapter.VideoAdapter
import com.dplayer.videoplayer.data.VideoModel
import com.dplayer.videoplayer.databinding.ActivityBrowseBinding
import com.dplayer.videoplayer.databinding.SortBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.io.File
import java.security.Permissions
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class BrowseActivity : AppCompatActivity() {

    private lateinit var binding:ActivityBrowseBinding
    private val videoList: ArrayList<VideoModel> = ArrayList()
    private lateinit var recy: RecyclerView
    private var check:Int = 0
    private lateinit var adapter: VideoAdapter

    private fun sort(){

        val dialog = BottomSheetDialog(this)
        val binding = SortBinding.inflate(LayoutInflater.from(this))
        dialog.setContentView(binding.root)

        if(check != 0) binding.root.findViewById<RadioButton>(check).isChecked = true else binding.aZ.isChecked = true

        binding.sortCheckbox.setOnCheckedChangeListener{_,checkId->
            when(checkId){
                R.id.a_z ->{
                    getAllVideo("${MediaStore.Video.Media.TITLE} ASC")
                    recy.adapter?.notifyDataSetChanged()
                }
                R.id.z_a->{
                    getAllVideo("${MediaStore.Video.Media.TITLE} DESC")
                    recy.adapter?.notifyDataSetChanged()
                }
                R.id.newest->{
                    getAllVideo("${MediaStore.Video.Media.DATE_TAKEN} DESC")
                    recy.adapter?.notifyDataSetChanged()
                }
                R.id.oldest->{
                    getAllVideo("${MediaStore.Video.Media.DATE_TAKEN} ASC")
                    recy.adapter?.notifyDataSetChanged()
                }
                else->{
                    getAllVideo("${MediaStore.Video.Media.TITLE} ASC")
                    recy.adapter?.notifyDataSetChanged()
                }
            }
            check = checkId
        }
        dialog.show()
    }

    private fun getAllVideo(sort:String?="${MediaStore.Video.Media.TITLE} ASC"){
        videoList.clear()
        val videoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val details = arrayOf(
            MediaStore.Video.Media.TITLE,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DATE_ADDED,
            MediaStore.Video.Media.DATE_TAKEN
        )

        val cursor = contentResolver.query(videoUri,details,null,null,sort)

        if (cursor != null){
            while (cursor.moveToNext()){
                val title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE))
                val data = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA))
                val duration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION))
                val size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE))
                val dateAdded = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED))
                val dateTaken = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_TAKEN))

                val file = File(data)
                val thumb = Uri.fromFile(file)

                val millis: Long = if (dateTaken > 0) dateTaken else dateAdded * 1000

                val dateFormat = SimpleDateFormat("dd MMMM yyyy HH:mm", Locale.getDefault())
                val vids = VideoModel(title, duration, data, thumb, size, dateFormat.format(Date(millis)))
                videoList.add(vids)

            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 8876){
            if (grantResults.all { it != PackageManager.PERMISSION_GRANTED }){
                Toast.makeText(applicationContext,"Please Allow Permission via Settings Before Using This Apps",Toast.LENGTH_LONG).show()
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBrowseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ){
            requestPermissions(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.READ_EXTERNAL_STORAGE),8876)
        }

        recy = binding.videoList

        getAllVideo()

        adapter = VideoAdapter(videoList)
        recy.adapter = adapter
        recy.layoutManager = LinearLayoutManager(applicationContext,LinearLayoutManager.VERTICAL,false)

        binding.sort.setOnClickListener {
            sort()
        }

        binding.search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s != null){
                    val searched =  ArrayList<VideoModel>()
                    for (i in videoList){
                        if (i.title?.lowercase(Locale.ROOT)?.contains(s.toString().lowercase(Locale.ROOT)) == true){
                            searched.add(i)
                        }
                    }
                    if (searched.isNotEmpty()){
                        recy.visibility = View.VISIBLE
                        binding.notF.visibility = View.GONE
                        adapter.filtered(searched)
                        adapter.notifyDataSetChanged()
                    }else{
                        recy.visibility = View.GONE
                        binding.notF.visibility = View.VISIBLE
                    }
                }

            }

        })

        binding.searchIcons.setOnClickListener {
            val v = if (binding.search.visibility == View.GONE) View.VISIBLE else View.GONE
            if (v != View.VISIBLE){
                val view:View? = this.currentFocus
                if (view != null){
                    binding.search.text = Editable.Factory.getInstance().newEditable("")
                    val input = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    input.hideSoftInputFromWindow(view.windowToken,0)
                }
            }
            binding.search.visibility = v

        }

    }
}