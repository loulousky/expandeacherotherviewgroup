package com.example.liuhai.viewdraghelpertest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import com.liuhai.expandeacherotherviewgroup.ExpendEacherOtherViewGroup

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        //设置最小的展开高度
        findViewById<ExpendEacherOtherViewGroup>(R.id.expandView).MinHeight=500;

        findViewById<Button>(R.id.top_button).setOnClickListener {

            Toast.makeText(this,"上面的view",Toast.LENGTH_SHORT).show()

        }

        findViewById<Button>(R.id.bottom_button).setOnClickListener {

            Toast.makeText(this,"下面的view",Toast.LENGTH_SHORT).show()

        }
    }
}
