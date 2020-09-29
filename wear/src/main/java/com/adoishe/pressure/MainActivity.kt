package com.adoishe.pressure

//import android.R

//import android.R
import DatabaseHelper
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.support.wearable.activity.WearableActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.common.internal.service.Common
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Boolean.FALSE
import java.lang.Boolean.TRUE
import java.util.*
import kotlin.math.round
import com.adoishe.adoishelib.Tools
import com.adoishe.adoishelib.PressureService

 class MainActivity(private var databaseHelper: DatabaseHelper? = null) : WearableActivity() {

     val z_baro_top     : Double     = 1050.0
     val z_baro_bottom  : Double     = 950.0
     var z_where        : Int        = 1
     var wh_temp_out    : Double     = 20.0

     val tools              = Tools()
     val pressureService    = PressureService()

     fun showStatus(message : String){

        val stattus = tools.getStatus(message, this)
         this.textView_log.text = stattus
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Enables Always-on
        setAmbientEnabled()
       // showStatus("")

        /*
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

                      // создаем адаптер
                      val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this,
                              android.R.layout.simple_list_item_1, listSensorType)
                      // устанавливаем для списка адаптер
                      listList.setAdapter(adapter);


        val defPressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)

        sensorManager.registerListener(this, defPressureSensor, SensorManager.SENSOR_DELAY_NORMAL);
*/
    }

    public fun act (View: View){

        val i  = Intent(this, pressureService::class.java)

        if (View.getId() == R.id.start) {
            startService(i)

           // showStatus("Started!")
        }

        else if (View.getId() == R.id.count) {

            databaseHelper  = DatabaseHelper(this)

            showStatus("Количество записей = " + databaseHelper!!.countPressuresList!!.toString())

        }
        else if(View.getId() == R.id.forecast) {

            showStatus(tools.getCurrentForecast(this , editTemperature.text.toString()))

        }
        else if (View.getId() == R.id.stop) {

            stopService(i)
            showStatus("Stopped!")

        }
        else if (View.getId() == R.id.truncate) {

            truncate(View)
            showStatus("Stopped!")

        }

        else {
            showStatus("Нипанятна!!!!")
        }
    }

    public fun truncate (View : View) {

        databaseHelper = DatabaseHelper(this)

        databaseHelper!!.truncatePressures()

        Toast.makeText(this, "truncatePressures Successfully!", Toast.LENGTH_SHORT).show()


    }
}