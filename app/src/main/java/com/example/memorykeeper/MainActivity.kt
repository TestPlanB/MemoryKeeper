package com.example.memorykeeper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.memorykeepercore.Keeper

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Keeper.init(this.application)
    }
}