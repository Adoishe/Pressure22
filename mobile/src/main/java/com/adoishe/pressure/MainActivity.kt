package com.adoishe.pressure
import DatabaseHelper
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import com.adoishe.adoishelib.Tools
import com.adoishe.adoishelib.PressureService
import java.util.Timer
import kotlin.concurrent.schedule

class MainActivity (private var databaseHelper: DatabaseHelper? = null): AppCompatActivity() {

    val tools              = Tools()
    val pressureService    = PressureService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    fun showStatus(message : String){

        val stattus = tools.getStatus(message, this)
        this.textView_log.text = stattus
    }
    public fun act (View: View){

        val i  = Intent(this, pressureService::class.java)

        if (View.getId() == R.id.start) {
            Log.d("act", "start")
            startService(i)
            Log.d("act", "after start")


           //  showStatus("Started!")
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
            showStatus("truncate!")

        }
        else if (View.getId() == R.id.startTimer)
        {
//            Timer("SettingUp", false).schedule(500) {
//                readPressureAtOnce()
//            }
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
