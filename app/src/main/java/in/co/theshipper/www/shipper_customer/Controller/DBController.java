package in.co.theshipper.www.shipper_customer.Controller;

/**
 * Created by root on 8/5/16.
 */

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import in.co.theshipper.www.shipper_customer.Helper;

public class DBController  extends SQLiteOpenHelper {

    public static final String id =BaseColumns._ID;//for contacts database
    public static final String _ID = BaseColumns._ID;
    public static final String ID = BaseColumns._ID;
    public static final String TABLE_VIEW_CITY = "view_city";
    public static final String CITY_ID = "city_id";
    public static final String CITY_NAME = "city_name";
    public static final String UPDATE_DATE = "update_date";
    public static final String TABLE_VIEW_VEHICLE_TYPE = "view_vehicle_type";
    public static final String VEHICLETYPE_ID = "vehicletype_id";
    public static final String VEHICLE_NAME = "vehicle_name";
    public static final String IS_ACTIVE = "is_active";
    public static final String TABLE_VIEW_PRICING ="view_pricing";
    public static final String FROM_DISTANCE ="from_distance";
    public static final String TO_DISTANCE ="to_distance";
    public static final String PRICE_KM ="price_km";
    public static final String TABLE_VIEW_BASE_FARE = "view_base_fare";
    public static final String BASE_FARE = "base_fare";
    public static final String MAXIMUM_WEIGHT ="maximum_weight";
    public static final String FREEWAITING_TIME ="freewaiting_time";
    public static final String WAITING_CHARGE ="waiting_charge";
    public static final String NIGHT_HOLDING_CHARGE ="night_holding_charge";
    public static final String HARD_COPY_CHALLAN ="hard_copy_challan";
    public static final String DIMENSION="dimension";
    public static final String TRANSIT_CHARGE ="transit_charge";

    private static final String CREATE_TABLE_VIEW_CITY = "CREATE TABLE " + TABLE_VIEW_CITY
            + "(" + CITY_ID + " INTEGER PRIMARY KEY," + CITY_NAME + " TEXT,"
            + IS_ACTIVE + " INTEGER CHECK ( " + IS_ACTIVE +" IN (0,1) ),"
            + UPDATE_DATE + " TEXT" + ")";

    private static final String CREATE_TABLE_VIEW_VEHICLE_TYPE = "CREATE TABLE " + TABLE_VIEW_VEHICLE_TYPE
            + "(" + VEHICLETYPE_ID + " INTEGER PRIMARY KEY," + VEHICLE_NAME + " TEXT,"
            + IS_ACTIVE + " INTEGER CHECK ( " + IS_ACTIVE +" IN (0,1) ),"
            + UPDATE_DATE + " TEXT" + ")";

    private static final String CREATE_TABLE_VIEW_PRICING ="CREATE TABLE " +TABLE_VIEW_PRICING
            + "(" + _ID+ " INTEGER PRIMARY KEY AUTOINCREMENT," +VEHICLETYPE_ID + " INTEGER," + CITY_ID + " INTEGER," + VEHICLE_NAME + " TEXT,"
            + FROM_DISTANCE + " INTEGER," + TO_DISTANCE + " INTEGER," + PRICE_KM + " INTEGER,"
            + IS_ACTIVE + " INTEGER CHECK ( " + IS_ACTIVE +" IN (0,1) ),"
            + UPDATE_DATE + " TEXT" + ")";

    private static final String CREATE_TABLE_VIEW_BASE_FARE ="CREATE TABLE " + TABLE_VIEW_BASE_FARE
            + "(" + ID+ " INTEGER PRIMARY KEY AUTOINCREMENT," + VEHICLETYPE_ID + " INTEGER," + CITY_ID + " INTEGER," + VEHICLE_NAME + " TEXT,"
            + BASE_FARE + " REAL," + MAXIMUM_WEIGHT + " REAL," + FREEWAITING_TIME + " TEXT,"
            + WAITING_CHARGE + " INTEGER," + NIGHT_HOLDING_CHARGE + " INTEGER," + HARD_COPY_CHALLAN
            + " INTEGER," + DIMENSION + " TEXT," + TRANSIT_CHARGE + " INTEGER,"
            + IS_ACTIVE + " INTEGER CHECK ( " + IS_ACTIVE +" IN (0,1) ),"
            + UPDATE_DATE + " TEXT " + ")";

    private static final String CREATE_TABLE_CONTACTSDB= "CREATE TABLE contactsdb (" + id + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + " name TEXT, number TEXT )";

    private static final String DELETE = "DELETE FROM ";
    private static final String DELETE_VIEW_CITY = "DROP TABLE IF EXISTS " + TABLE_VIEW_CITY;
    private static final String DELETE_VIEW_VEHICLE_TYPE = "DROP TABLE IF EXISTS " + TABLE_VIEW_VEHICLE_TYPE;
    private static final String DELETE_VIEW_BASE_FARE = "DROP TABLE IF EXISTS " + TABLE_VIEW_BASE_FARE;
    private static final String DELETE_VIEW_PRICING = "DROP TABLE IF EXISTS " + TABLE_VIEW_PRICING;
    private static final String DELETE_CONTACTSDB = " DROP TABLE IF EXISTS contactsdb";

    public DBController(Context context) {
        super(context, "theShipper.db", null, 1);
    }
    //Creates Table
    @Override
    public void onCreate(SQLiteDatabase database) {

        try {

            database.execSQL(CREATE_TABLE_VIEW_BASE_FARE);
            database.execSQL(CREATE_TABLE_VIEW_CITY);
            database.execSQL(CREATE_TABLE_VIEW_PRICING);
            database.execSQL(CREATE_TABLE_VIEW_VEHICLE_TYPE);
            database.execSQL(CREATE_TABLE_CONTACTSDB);

        } catch (SQLException e) {

            e.printStackTrace();

        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int version_old, int current_version) {

        try {

            database.execSQL(DELETE_VIEW_BASE_FARE);
            database.execSQL(DELETE_VIEW_CITY);
            database.execSQL(DELETE_VIEW_PRICING);
            database.execSQL(DELETE_VIEW_VEHICLE_TYPE);
            database.execSQL(DELETE_CONTACTSDB);

        } catch (SQLException e) {

            e.printStackTrace();

        }

        onCreate(database);

    }


    public void deleteTable(int table_no) {

        try {

            SQLiteDatabase database = this.getWritableDatabase();

            if (table_no == 0) {

                database.execSQL(DELETE + TABLE_VIEW_BASE_FARE);

            } else if (table_no == 1) {

                database.execSQL(DELETE + TABLE_VIEW_CITY);

            } else if (table_no == 2) {

                database.execSQL(DELETE + TABLE_VIEW_PRICING);

            } else  {

                database.execSQL(DELETE + TABLE_VIEW_VEHICLE_TYPE);

            }

        } catch (SQLException e) {

            e.printStackTrace();

        }

    }

    public void deleteContacts(String number)
    {

        try {

            SQLiteDatabase database = this.getWritableDatabase();
            database.execSQL("DELETE FROM contactsdb WHERE number = '" + number + "'");

        } catch (SQLException e) {

            e.printStackTrace();

        }

    }

    /**
     * Inserts User into SQLite DB
     * @param queryValues
     */

    public void insert(HashMap<String, String> queryValues,int table_no) {

        try {

            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            if (table_no == 0) {

                values.put(VEHICLETYPE_ID, queryValues.get(VEHICLETYPE_ID));
                values.put(CITY_ID, queryValues.get(CITY_ID));
                values.put(VEHICLE_NAME, queryValues.get(VEHICLE_NAME));
                values.put(BASE_FARE, queryValues.get(BASE_FARE));
                values.put(MAXIMUM_WEIGHT, queryValues.get(MAXIMUM_WEIGHT));
                values.put(FREEWAITING_TIME, queryValues.get(FREEWAITING_TIME));
                values.put(WAITING_CHARGE, queryValues.get(WAITING_CHARGE));
                values.put(NIGHT_HOLDING_CHARGE, queryValues.get(NIGHT_HOLDING_CHARGE));
                values.put(HARD_COPY_CHALLAN, queryValues.get(HARD_COPY_CHALLAN));
                values.put(DIMENSION, queryValues.get(DIMENSION));
                values.put(TRANSIT_CHARGE, queryValues.get(TRANSIT_CHARGE));
                values.put(IS_ACTIVE, queryValues.get(IS_ACTIVE));
                values.put(UPDATE_DATE, queryValues.get(UPDATE_DATE));
                database.insert(TABLE_VIEW_BASE_FARE, null, values);

            }
            else if (table_no == 1) {

                values.put(CITY_ID, queryValues.get(CITY_ID));
                values.put(CITY_NAME, queryValues.get(CITY_NAME));
                values.put(IS_ACTIVE, queryValues.get(IS_ACTIVE));
                values.put(UPDATE_DATE, queryValues.get(UPDATE_DATE));
                database.insert(TABLE_VIEW_CITY, null, values);
            }
            else if (table_no == 2) {

                values.put(VEHICLETYPE_ID, queryValues.get(VEHICLETYPE_ID));
                values.put(CITY_ID, queryValues.get(CITY_ID));
                values.put(VEHICLE_NAME, queryValues.get(VEHICLE_NAME));
                values.put(FROM_DISTANCE, queryValues.get(FROM_DISTANCE));
                values.put(TO_DISTANCE, queryValues.get(TO_DISTANCE));
                values.put(PRICE_KM, queryValues.get(PRICE_KM));
                values.put(IS_ACTIVE, queryValues.get(IS_ACTIVE));
                values.put(UPDATE_DATE, queryValues.get(UPDATE_DATE));
                database.insert(TABLE_VIEW_PRICING, null, values);

            }
            else if(table_no == 3) {

                values.put(VEHICLETYPE_ID, queryValues.get(VEHICLETYPE_ID));
                values.put(VEHICLE_NAME, queryValues.get(VEHICLE_NAME));
                values.put(IS_ACTIVE, queryValues.get(IS_ACTIVE));
                values.put(UPDATE_DATE, queryValues.get(UPDATE_DATE));
                database.insert(TABLE_VIEW_VEHICLE_TYPE, null, values);

            }
            else {

                values.put("name",queryValues.get("name"));
                values.put("number", queryValues.get("number"));
                database.insert("contactsdb", null, values);
            }

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    /**
     * Get list of Users from SQLite DB as Array List
     * @return
     */
    public void getAll() throws ParseException {

        try {

            String selectQuery = "SELECT  base_fare, transit_charge  FROM view_base_fare";
            SQLiteDatabase database = this.getWritableDatabase();
            Cursor cursor = database.rawQuery(selectQuery, null);
            Helper.SystemPrintLn("DBCONTROLLER_ArrayList");

            if (cursor.moveToFirst()) {

                do {

                } while (cursor.moveToNext());

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

}

