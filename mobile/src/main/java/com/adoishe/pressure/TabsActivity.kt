package com.adoishe.pressure

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.TextView
import com.adoishe.pressure.ui.main.SectionsPagerAdapter
import kotlinx.android.synthetic.main.fragment_tabs.*

class TabsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tabs)
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        fun sensorsList(){

            val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

            // val listSensor = sensorManager.getSensorList(Sensor.TYPE_ALL)
            val listSensor = sensorManager.getSensorList(Sensor.TYPE_ALL)

            val listSensorType: MutableList<String> = ArrayList()

            //        var arrayList: ArrayList<Any>? = null

            var textView: TextView? = null

            for (i in listSensor.indices) {
                listSensorType.add(listSensor[i].name)
            }

            //bm1383glv
            // создаем адаптер
            val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listSensorType)
            // устанавливаем для списка адаптер
            list_list.adapter = adapter;
        }
    }
}