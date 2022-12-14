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
import kotlinx.coroutines.*
//import kotlinx.coroutines.DefaultExecutor.isActive
import kotlinx.coroutines.NonCancellable.isActive
import java.lang.System.currentTimeMillis


class MainActivity(private var databaseHelper: DatabaseHelper? = null) : WearableActivity() {

     val z_baro_top     : Double     = 1050.0
     val z_baro_bottom  : Double     = 950.0
     var z_where        : Int        = 1
     var wh_temp_out    : Double     = 20.0

     val tools              = Tools()
     private val pressureService    = PressureService()
    private    var pressureIntent: Intent? = null
//private var pressureIntent = Intent(baseContext, pressureService::class.java)

//    val job = SupervisorJob()
//    var jobPressure: Job? = null

    //    val scope = CoroutineScope(Dispatchers.Default + job)
//    var getDataCoroutine =
    override fun onDestroy() {
        Log.d("PressureService" , "onDestroy")

        super.onDestroy()
    }

    private fun showStatus(message : String){

         this.textView_log.text = tools.getStatus(message, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Enables Always-on
        setAmbientEnabled()
       // showStatus("")
        pressureIntent = Intent(baseContext, pressureService::class.java)
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
                      // ?????????????? ??????????????

                      // ?????????????? ??????????????
                      val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this,
                              android.R.layout.simple_list_item_1, listSensorType)
                      // ?????????????????????????? ?????? ???????????? ??????????????
                      listList.setAdapter(adapter);


        val defPressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)

        sensorManager.registerListener(this, defPressureSensor, SensorManager.SENSOR_DELAY_NORMAL);
*/
    }

    override fun onStart() {
        super.onStart()

        Log.d("PressureService" , "onStart")
    }

    override fun onRestart() {

        Log.d("PressureService" , "onRestart")
        super.onRestart()
    }

//    suspend fun doWork(): Deferred<Unit> = coroutineScope {     // (1)
//        async {  }
//    }
//
//    fun getData() = scope.launch {                       // (2)
//        try {
//            doWork().await()
//        } catch (e: Exception) {  }
//    }

//    private fun getPressure(minutes : Int) : Deferred<Unit> = scope.async{
//
//        val uiInfo  = Runnable {showStatus(minutes.toString())}
//
//        runOnUiThread(uiInfo)
//
//    }
//        fun stopJobPressure()  = runBlocking {
//            press jobPressure?.cancelAndJoin()
//        }

    fun act (View: View){

//        val i  = Intent(this, pressureService::class.java)

        when (View.id) {

            R.id.start -> {

                //            startService(i)
                //            getPressure(5).start()


                var componentName = baseContext.startService(pressureIntent)

//                val startTime       = currentTimeMillis()
//                jobPressure         = scope.launch{
//
//                    var nextPrintTime   = startTime
//                    var i               = 0
//                    val pressureIntent  = Intent(baseContext, pressureService::class.java)
//                    val timerTime       = (PressureService().oneHourMS / 60) * 1
//
//                    while (isActive) { // cancellable computation loop
//                        // print a message twice a second
//                        if (System.currentTimeMillis() >= nextPrintTime) {
//
//                            showStatus("job: I'm getting pressure ${i++} ...")
//
//                            baseContext.startService(pressureIntent)
//
//    //                                delay(1L)
//
//                            baseContext.stopService(pressureIntent)
//
//                            nextPrintTime += (timerTime / 2)
//                        }
//                    }
//                }

                showStatus("Started!")
            }
            R.id.count -> {

                databaseHelper  = DatabaseHelper(this)

                showStatus("???????????????????? ?????????????? = " + databaseHelper!!.countPressuresList!!.toString())

            }
            R.id.forecast -> {

                showStatus(tools.getCurrentForecast(this , editTemperature.text.toString()))

            }
            R.id.stop -> {

                stopService(pressureIntent)
//                stopJobPressure()
                showStatus("Stopped!")

            }
            R.id.truncate -> {

                truncate(View)
                showStatus("truncate!")

            }
            else -> {
                showStatus("??????????????????!!!!")
            }
        }
    }


    public fun truncate (View : View) {

        databaseHelper = DatabaseHelper(this)

        databaseHelper!!.truncatePressures()

        Toast.makeText(this, "truncatePressures Successfully!", Toast.LENGTH_SHORT).show()


    }
}