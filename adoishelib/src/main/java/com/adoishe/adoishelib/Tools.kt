package com.adoishe.adoishelib

import DatabaseHelper
import android.app.Activity
import android.app.Service
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.Toast
//import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import com.adoishe.adoishelib.PressureService
import kotlinx.coroutines.*
import java.lang.Boolean
import java.text.SimpleDateFormat
import java.util.*
import kotlin.*
import kotlin.concurrent.timer
import kotlin.math.round




//class PressureService : SensorEventListener, Service() {
class PressureService : SensorEventListener, Service() {

 private lateinit var sensorManager     : SensorManager
 private lateinit var defPressureSensor : Sensor

     var press                  : Double = 0.0
     var previousPeriodMs       = System.currentTimeMillis()

    //В одном часе 3 600 000 миллисекунд
    val oneHourMS           : Long = 3600000
    var currentPeriodMillis = System.currentTimeMillis()
    var timerTime           = (oneHourMS / 60) * 10
 //   var tools               = Tools()
    var diffMs              : Long = 0L
    var itsTimeToRecord     : kotlin.Boolean = false
    var timeReg: Long       = System.currentTimeMillis()
    var timeUnreg: Long     = System.currentTimeMillis()
    var timeBetween: Long   = 0L
//    var timerReg = Timer()
//    var timerUnReg = Timer()


    ////////////////////////////////////////////////
//     fun  regTask  (): () -> Unit  =  {
//
//        timeReg             = System.currentTimeMillis()
//        timeBetween        = (timeReg - timeUnreg)
//
//        Log.d("_______________", "TimerTask registerListenertimeBetween = " + (timeBetween/1000).toString())
//
//        previousPeriodMs   = currentPeriodMillis
//        regged             = sensorManager.registerListener(
//            this@PressureService,
//            defPressureSensor,
//            SensorManager.SENSOR_DELAY_UI)
//    }


    //////////////////////////////////////////
//    fun unregTask (): () -> Unit  =  {
//
//            timeUnreg      = System.currentTimeMillis()
//            timeBetween    = (timeUnreg - timeReg)
//
//            Log.d("^^^^^^^^^^^^^", "TimerTask unregisterListener timeBetween = " + (timeBetween/1000).toString())
//            sensorManager.unregisterListener(this@PressureService, defPressureSensor)
//
//        }
    //////////////////////////////////////////////
//        val timerReg = timer("reg", false , 5 , oneHourMS,  )
   /*
        {

                timeReg             = System.currentTimeMillis()
                timeBetween        = (timeReg - timeUnreg)

                Log.d("_______________", "TimerTask registerListenertimeBetween = " + (timeBetween/1000).toString())

                previousPeriodMs   = currentPeriodMillis
                regged             = sensorManager.registerListener(
                    this@PressureService,
                    defPressureSensor,
                        SensorManager.SENSOR_DELAY_UI)
        }

    */

//    val timerUnReg = timer("unreg", false , 5 , oneHourMS, this::unregTask)
    /*
        {

            timeUnreg      = System.currentTimeMillis()
            timeBetween    = (timeUnreg - timeReg)

            Log.d("^^^^^^^^^^^^^", "TimerTask unregisterListener timeBetween = " + (timeBetween/1000).toString())
            sensorManager.unregisterListener(this@PressureService, defPressureSensor)

        }

     */

    //   ("Reg", false, 1000 , (oneHourMS/ 60) , )//Timer()
                var regged              : kotlin.Boolean    = false
    private     var databaseHelper      : DatabaseHelper?   = null
                var sensorInitialized   : kotlin.Boolean    = false

    val job                 = SupervisorJob()
    var jobPressure: Job?   = null
    private val scope               = CoroutineScope(Dispatchers.Default + job)
    private var isPaused    = false

//    lateinit    var mainHandler         : Handler

//    private val regPressTask = object : Runnable {
//        override fun run() {
//            regTask()
//
//            unregTask()
//
////            mainHandler.postDelayed(this, 1000)
//        }
//    }
//    private val unregTask = object : Runnable {
//        override fun run() {
//            unregTask()
//            mainHandler.postDelayed(this, 1000)
//        }
//    }

  override fun onBind(intent: Intent): IBinder? {
     // Log.d("PressureService", "onBind")
     throw UnsupportedOperationException("Not yet implemented")
    }

  override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
       //  Log.d("PressureService", "onAccuracyChanged")
   }

  override fun onSensorChanged(event: SensorEvent) {

      val pressure= event.values[0].toDouble()
      databaseHelper      = DatabaseHelper(this)
      // Получаем атмосферное давление в миллибарах
      databaseHelper!!.addPressure(pressure)

      Log.d("PressureService", "`SENSOR_SERVICE` $pressure!")
      isPaused = true
 }

 override  fun onCreate() {

   try {
     sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

     Log.d("PressureService", "`SENSOR_SERVICE` Successfully!")
     Toast.makeText(this, "SENSOR_SERVICE Successfully!", Toast.LENGTH_SHORT).show()

     if (sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE) != null) {

         defPressureSensor      = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)
         this.sensorInitialized = true
     }
     else{
       // defPressureSensor = sensorManager.
       Log.d("PressureService", "нет барометра в девайсе!")
       Toast.makeText(this, "нет барометра в девайсе!", Toast.LENGTH_SHORT).show()
     }

     Toast.makeText(this, "sensorManager Successfully!", Toast.LENGTH_SHORT).show()

     Log.d("PressureService", "sensorManager Successfully!")

//       mainHandler = Handler(Looper.getMainLooper())

   }catch (e: Throwable)
   {
     Log.d("PressureService", "onCreate failed!")
     Toast.makeText(this, "onCreate failed!", Toast.LENGTH_SHORT).show()
   }
 }

//  fun onResume() {
////    super.onResume()
////    sensorManager.registerListener(this, defPressureSensor, SensorManager.SENSOR_DELAY_UI)
//
//      Log.d("PressureService" , "Начал слушать Successfully!")
//
//    isPaused = false
//
//
//
////    Toast.makeText(this, "Начал слушать Successfully!", Toast.LENGTH_SHORT).show()
////      mainHandler.post(regPressTask)
////      mainHandler.post(unregTask)
// }
//
    private fun stopJobPressure()  = runBlocking {

        jobPressure?.cancelAndJoin()
    }
//
//  fun onPause() {
////    super.onPause()
////    sensorManager.unregisterListener(this, defPressureSensor)
//
//      isPaused = true
//
////      mainHandler.removeCallbacks(regPressTask)
////      mainHandler.removeCallbacks(unregTask)
//      Log.d("PressureService" , "пауза слушать Successfully!")
////      Toast.makeText(this, "пауза слушать Successfully!", Toast.LENGTH_SHORT).show()
// }

 override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

//     var tools = Tools()
//     var diffMs: Long
//     var itsTimeToRecord: kotlin.Boolean
//     var timeReg: Long = System.currentTimeMillis()
//     var timeUnreg: Long = System.currentTimeMillis()
//     var timeBetween: Long



      if (sensorInitialized){
             try {

                 val startTime       = System.currentTimeMillis()
                 jobPressure         = scope.launch{

                     var nextPrintTime   = startTime
                     var i               = 0
//                     val pressureIntent  = Intent(baseContext, pressureService::class.java)
                     val timerTime       = (PressureService().oneHourMS / 60) * 1

                     while (isActive) { // cancellable computation loop
                         // print a message twice a second

                         var itsTimeToMeasure = (System.currentTimeMillis() >= nextPrintTime)

                         when(isPaused){

                             true ->{

                                 isPaused = !(itsTimeToMeasure)

                                 if (regged){

                                 Log.d("PressureService" , "job: I'm pausing pressure...")

                                     sensorManager.unregisterListener(this@PressureService, defPressureSensor)

                                     regged = false




                                 }


                             }

                             false ->{

                                 if (itsTimeToMeasure) {

                                     when(regged){

                                         true -> {

                                             Log.d("PressureService" , "job: I'm getting pressure ${i++} ...")

                                         }
                                         false -> {

                                             regged = sensorManager.registerListener(
                                                                            this@PressureService,
                                                                                   defPressureSensor,
                                                                                   SensorManager.SENSOR_DELAY_UI
                                                                                     )

                                             Log.d("PressureService" , "job: I'm reggiing pressure  ...")

                                         }
                                     }

                                     nextPrintTime += (timerTime / 2)

                                 }
                             }
                         }
                     }

                     Log.d("PressureService" , "job: I'm stoping pressure...")
                     sensorManager.unregisterListener(this@PressureService, defPressureSensor)
                 }







                 return START_STICKY

             } catch (e: Throwable) {

                 Toast.makeText(this, "onStartCommand failed!", Toast.LENGTH_SHORT).show()
                 return START_NOT_STICKY

             }
         }else{
              Toast.makeText(this, "onStartCommand failed!", Toast.LENGTH_SHORT).show()
              return START_NOT_STICKY
          }
 }

 override fun onDestroy() {


     stopJobPressure()

     sensorManager.unregisterListener(this@PressureService, defPressureSensor)

     Log.d("PressureService", "onDestroy")
 }
}

public  class Tools
 {
    val pressureService    = PressureService()



    fun getStatus(message : String, activity : Activity) : String{

         var pressureString                         : String
         var lastPeriodString                       : String
        // var common                                 = Common()
         val builder                                = StringBuilder()
         var databaseHelper: DatabaseHelper? = null
         databaseHelper                             = DatabaseHelper(activity)
         // нужна одна последняя запись
         databaseHelper!!.limit                     = 1
         // массив с одной струкрутруой - последнее измеренное давление
         var arrContentValues   = databaseHelper!!.lastPressuresList

         if (arrContentValues.count()> 0) {

          var lastPeriod  = arrContentValues[0].getAsLong("period")
          // lastPeriodString       = getDateStringFromMilliseconds(lastPeriod)
          lastPeriodString       = getDateStringFromMilliseconds(lastPeriod)
          pressureString        = arrContentValues[0].getAsString("pressure")

     }
     else {
          lastPeriodString   = "нетути"
          pressureString     = " ваще!"
     }

     builder.append(message)
      .append("\n")
      .append("last @ ")
      .append(lastPeriodString)
      .append("\n")
      .append(pressureString)

     return builder.toString()
    }

    fun getDateStringFromMilliseconds (mSeconds : Long) : String {

     var date: String?  = null
     val formatter      = SimpleDateFormat("yyyy-MM-dd HH:mm:ss") // modify format
     val dateFromPeriod = Date(mSeconds)
     date               = formatter.format(dateFromPeriod)

     return date.toString()
    }

    fun getPressure(hourCount : Long, activity : Activity) :Double {

      var databaseHelper: DatabaseHelper? = null

      if (hourCount == 0L)
      {

        var pressure                : Double
        databaseHelper              = DatabaseHelper(activity)
        // нужна одна последняя запись
        databaseHelper!!.limit      = 1
        // массив с одной струкрутруой - последнее измеренное давление
        var arrContentValues    = databaseHelper!!.lastPressuresList

        if (arrContentValues.count()> 0)
        {

          var lastPeriodString = arrContentValues[0].getAsString("period")
          pressure = arrContentValues[0].getAsDouble("pressure")
        }
        else
        {
          pressure = 0.0
        }

        return  pressure

      } else
      {

        databaseHelper                             = DatabaseHelper(activity)
        /*
        // нужно две последних записи
        databaseHelper!!.limit                     = 2
        // массив с одной струкрутруой - последнее измеренное давление
        var arrContentValues= databaseHelper!!.lastPressuresList

         */
        // нужна одна последняя запись
        databaseHelper!!.limit                         = 1
        // массив с одной струкрутруой - последнее измеренное давление
        val arrContentValues0    = databaseHelper!!.lastPressuresList
        val arrContentValues1    = databaseHelper!!.getPressureByHourPast(-1)

        //     if (arrContentValues.count()> 1){

        //var diff = arrContentValues[1].getAsLong("period") - arrContentValues[0].getAsLong("period")
        val diff = arrContentValues0[0].getAsLong("period") - arrContentValues1[0].getAsLong("period") /3600000 // hours

        //Log.d("getPressure 2 rec" , diff.toString() + " часов")
        //Toast.makeText(activity, "diff.toString() = " + diff.toString(), Toast.LENGTH_SHORT).show()

        // var lastPeriodString = arrContentValues[1].getAsString("period")
        val pressure                     = arrContentValues0[0].getAsDouble("pressure")
        /*       }
               else {
                   pressure = 0.0
               }
                 */
        return  pressure
      }

    }

    fun getCurrentForecast(activity : Activity, temperature : String) : String {

        val calendar = Calendar.getInstance()
        val pressureTrend         : Int
        val pressureTrendText    : String
        val forecast               : String
        val contentValues          = ContentValues()
        val windDirTextUk       : Array<String> = activity.resources.getStringArray(R.array.wind_dir_text_uk)
        val absPressure           : Double = getPressure(0, activity)
        val absPressure1h        : Double = getPressure(-1, activity)

        if ( absPressure > absPressure1h + 0.25)
        {
            pressureTrend = 1;
            pressureTrendText = "Рост";
        }
        else if ( absPressure1h > absPressure + 0.25)
        {
            pressureTrend = 2;
            pressureTrendText = "Падение";
        }
        else
        {
            pressureTrend = 0;
            pressureTrendText = "Не меняется";
        }

//        val date    : Date = Date()
        contentValues.put("z_hpa"          , absPressure)
        contentValues.put("z_month"        , calendar.get(Calendar.MONTH))//date.getMonth())
        // todo Где-то взять направление ветра
        contentValues.put("z_wind"         , windDirTextUk[16])
        contentValues.put("z_trend"        , pressureTrend)
        contentValues.put("z_where"        ,   1)
        contentValues.put("z_baro_top"     , 1050)
        contentValues.put("z_baro_bottom"  , 950)
        // todo Где-то взять текущую температуру


        var temp : String = if(temperature == "") "0" else temperature

        contentValues.put("wh_temp_out"    , Integer.parseInt(temp)) // editTemperature.text.toString()

        forecast = forecast(contentValues, activity)

        return forecast
    }

    fun forecast(values: ContentValues, activity : Activity) : String{
       //Zambretti
       // https://ab-log.ru/smart-house/weather-station/forecast

       var zOutput                : String  = ""
       var zOption                : Int
       val zBaroBottom    = values.getAsDouble("z_baro_bottom")
       val zBaroTop       = values.getAsDouble("z_baro_top")
       val zRange         = zBaroTop - zBaroBottom
       val zConstant      = round(zRange / 22)
       val zSeason        = ( values.getAsInteger("z_month" ) >= 4 ) && ( values.getAsInteger("z_month" ) <= 9 )
       val zWind           = values.getAsString("z_wind")
       val zWhere            = values.getAsInteger("z_where")
       var zHpa           = values.getAsDouble("z_hpa")
       val zTrend            = values.getAsInteger("z_trend")

       if (zWhere == 1) {// North hemisphere
         if (zWind === "N")       zHpa += 6 / 100 * zRange
         else if (zWind === "NNE")     zHpa += 5 / 100 * zRange
         else if (zWind === "NE")      zHpa += 5 / 100 * zRange
         else if (zWind === "ENE")     zHpa += 2 / 100 * zRange
         else if (zWind === "E")       zHpa -= 0.5 / 100 * zRange
         else if (zWind === "ESE")     zHpa -= 2 / 100 * zRange
         else if (zWind === "SE")      zHpa -= 5 / 100 * zRange
         else if (zWind === "SSE")     zHpa -= 8.5 / 100 * zRange
         else if (zWind === "S")       zHpa -= 12 / 100 * zRange
         else if (zWind === "SSW")     zHpa -= 10 / 100 * zRange
         else if (zWind === "SW")      zHpa -= 6 / 100 * zRange
         else if (zWind === "WSW")     zHpa -= 4.5 / 100 * zRange
         else if (zWind === "W")       zHpa -= 3 / 100 * zRange
         else if (zWind === "WNW")     zHpa -= 0.5 / 100 * zRange
         else if (zWind === "NW")      zHpa += 1.5 / 100 * zRange
         else if (zWind === "NNW")     zHpa += 3 / 100 * zRange

         if (zSeason == Boolean.TRUE) {     // if Summer
           if (zTrend == 1)   zHpa += 7 / 100 * zRange  // rising
           else  if (zTrend == 2)   zHpa -= 7 / 100 * zRange //    falling
         }
       } else {// south hemisphere
         if (zWind === "S")   zHpa += 6 / 100 * zRange
         else if (zWind === "SSW")  zHpa += 5 / 100 * zRange
         else if (zWind === "SW")   zHpa += 5 / 100 * zRange
         else if (zWind === "WSW")  zHpa += 2 / 100 * zRange
         else if (zWind === "W")    zHpa -= 0.5 / 100 * zRange
         else if (zWind === "WNW")  zHpa -= 2 / 100 * zRange
         else if (zWind === "NW")   zHpa -= 5 / 100 * zRange
         else if (zWind === "NNW")  zHpa -= 8.5 / 100 * zRange
         else if (zWind === "N")    zHpa -= 12 / 100 * zRange
         else if (zWind === "NNE")  zHpa -= 10 / 100 * zRange
         else if (zWind === "NE")   zHpa -= 6 / 100 * zRange
         else if (zWind === "ENE")  zHpa -= 4.5 / 100 * zRange
         else if (zWind === "E")    zHpa -= 3 / 100 * zRange
         else if (zWind === "ESE")  zHpa -= 0.5 / 100 * zRange
         else if (zWind === "SE")   zHpa += 1.5 / 100 * zRange
         else if (zWind === "SSE")  zHpa += 3 / 100 * zRange

         if (zSeason == Boolean.FALSE) {    // if Winter
           if (zTrend == 1)  zHpa += 7 / 100 * zRange // rising
           else  if (zTrend == 2)  zHpa -= 7 / 100 * zRange // falling
         }
       }

       if (zHpa == zBaroTop)  zHpa = zBaroTop - 1

       zOption = Math.floor((zHpa - zBaroBottom) /zConstant).toInt()

       if (zOption < 0) {
         zOption = 0
         zOutput = "Exceptional Weather, "
       }
       if (zOption > 21) {
         zOption = 21
         zOutput = "Exceptional Weather, "
       }

        val rise_options    : IntArray = activity.getResources().getIntArray(R.array.rise_options)
        val fall_options    : IntArray = activity.getResources().getIntArray(R.array.fall_options)
        val steady_options  : IntArray = activity.getResources().getIntArray(R.array.steady_options)
        val z_forecast      : Array<String> = activity.getResources().getStringArray(R.array.z_forecast)

              if (zTrend == 1) zOutput = zOption.toString() + " " + z_forecast[rise_options[zOption]]
       else   if (zTrend == 2) zOutput = zOption.toString() + " " + z_forecast[fall_options[zOption]]
       else                    zOutput = zOption.toString() + " " + z_forecast[steady_options[zOption]]

       return zOutput
    }
 }