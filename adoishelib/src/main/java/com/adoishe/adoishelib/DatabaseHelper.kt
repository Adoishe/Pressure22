import android.content.ContentValues
import android.content.Context
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.widget.Toast
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import kotlin.collections.ArrayList


class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    var limit :Int = 10
        get() = field
    set(value)
    {
        field = value
    }

    val lastPressuresList: ArrayList<ContentValues>
        get() {
            val lim                 = this.limit
            //val pressuresArrayList      = ArrayList<Any>()
            var pressure                = 0.0
            val selectQuery             = "SELECT  * FROM $TABLE_PRESSURES order by period desc LIMIT $lim"
            val db       = this.readableDatabase
            val c               = db.rawQuery(selectQuery, null)

            var arrContentValues = ArrayList<ContentValues>()
            var contentVal : ContentValues

            if (c.moveToFirst()) {
                do {
                    // новый структура ага
                    contentVal = ContentValues()
                    // запись в формат структуры
                    DatabaseUtils.cursorRowToContentValues(c, contentVal);
                    // структру в массив структуру
                    arrContentValues.add(contentVal);
/*
                    pressure = c.getDouble(c.getColumnIndex(KEY_PRESSURE))

                    pressuresArrayList.add(pressure)

 */

                } while (c.moveToNext())

                //Log.d("array", arrContentValues.toString())
            }
            c.close()
            db.close()
            Log.d("lastPressuresList", arrContentValues.toString())
            return arrContentValues
        }

    val countPressuresList: Int
        get() {
            val lim                 = this.limit
            var count                   = 0
            var pressure                = 0.0
            val db       = this.readableDatabase
            val c               = db.rawQuery(COUNT_TABLE_PRESSURES, null)

            if (c.moveToFirst()) {
                do {
                    count = c.getInt(c.getColumnIndex("COUNT"))
                } while (c.moveToNext())

                Log.d("countPressuresList", count.toString())
            }
            c.close()
            db.close()
            return count
        }

    init {

      //  Log.d("table", CREATE_TABLE_PRESSURES)
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE_PRESSURES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS '$TABLE_PRESSURES'")
        onCreate(db)
    }

    fun truncatePressures() {

        val db= this.writableDatabase

        db.execSQL(DELETE_TABLE_PRESSURES)
        db.execSQL(VACUUM_TABLE_PRESSURES)

        Log.d("truncatePressures", "")
    }
    fun getPressureByHourPast(countHour : Long) : ArrayList<ContentValues> {
/*
        // Текущая дата
        var currentDate = LocalDateTime.now()
        //  дата минут часы
         var dateBack =   currentDate.plusHours(countHour)
        // формат даты для скуля
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        // дата для скуля
        val date = Date.from( dateBack.atZone( ZoneId.systemDefault()).toInstant());
        // дата строкой для скуля
        var strdateBack = dateFormat.format(date)


 */
        val selectQuery =
            //,'localtime'
            "SELECT * FROM $TABLE_PRESSURES where datetime($KEY_PERIOD / 1000, 'unixepoch','localtime') BETWEEN datetime('now','$countHour minutes') and datetime('now') order by period desc LIMIT 1;"
            //"SELECT * FROM $TABLE_PRESSURES  order by period desc LIMIT 2;"
        val db   = this.readableDatabase
        val c           = db.rawQuery(selectQuery, null)
        var arrContentValues    = ArrayList<ContentValues>()
        // новый структура ага
        var contentVal          : ContentValues = ContentValues()

        if (c.moveToFirst())
            do {

               // pressure = c.getDouble(c.getColumnIndex(KEY_PRESSURE))


                // запись в формат структуры
                DatabaseUtils.cursorRowToContentValues(c, contentVal);
                // структру в массив структуру


            } while (c.moveToNext())
        else{
            contentVal.put("period", System.currentTimeMillis())
            contentVal.put("pressure", 0)
        }
        arrContentValues.add(contentVal);
        c.close()
        db.close()
        Log.d("getPressureByHourPast", arrContentValues.toString())
        return arrContentValues
        //return pressure
    }
    fun getDateStringFromMilliseconds (mSeconds : Long) : String {

        var date: String?  = null
        val formatter      = SimpleDateFormat("yyyy-MM-dd HH:mm:ss") // modify format
        var dateFromPeriod = Date(mSeconds)
        date               = formatter.format(dateFromPeriod)

        return date.toString()
    }

    fun addPressure(pressure: Double): Long {

        val db  = this.writableDatabase
        val values              = ContentValues()
        val period = System.currentTimeMillis()

        values.put(KEY_PRESSURE , pressure)
        values.put(KEY_PERIOD   , period)

        Log.d("addPressure", pressure.toString() + " @ "+ this.getDateStringFromMilliseconds(period))
        //Toast.makeText(this, pressure.toString() + " @ "+ this.getDateStringFromMilliseconds(period), Toast.LENGTH_SHORT).show()

        return db.insert(TABLE_PRESSURES, null, values)

    }

    companion object {

                var DATABASE_NAME       = "pressure_database"
        private val DATABASE_VERSION    = 2
        private val TABLE_PRESSURES     = "pressures"
        private val KEY_ID              = "id"
        private val KEY_PRESSURE        = "pressure"
        private val KEY_PERIOD          = "period"

        private val CREATE_TABLE_PRESSURES = ("CREATE TABLE "
                                                + TABLE_PRESSURES
                                                + "("
                                                + KEY_ID
                                                + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                                                + KEY_PRESSURE
                                                + " REAL,"
                                                + KEY_PERIOD
                                                + " LONG "
                                                + ");"
                                            )
        private val DELETE_TABLE_PRESSURES = ("DELETE FROM "
                + TABLE_PRESSURES
                + ";"
                )
        private val VACUUM_TABLE_PRESSURES = ("VACUUM "
                + ";"
                )
        private val COUNT_TABLE_PRESSURES = ("SELECT COUNT(*)  as COUNT FROM "
                + TABLE_PRESSURES
                + ";"
                )
    }
}