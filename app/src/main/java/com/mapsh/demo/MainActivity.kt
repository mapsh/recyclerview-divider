package com.mapsh.demo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView.HORIZONTAL
import com.mapsh.recyclerview.divider.RecyclerViewDivider
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }


    fun init() {
        recyclerView.layoutManager = GridLayoutManager(this,4)
        recyclerView.adapter = Adapter(mutableListOf("1", "2", "3", "1", "2", "3", "1", "2", "3", "1", "2", "3", "1"))
        RecyclerViewDivider.with(this)
                .size(1)
                .build()
                .addTo(recyclerView)

    }
}
