package com.hcp.hungchoipass;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


//Imports


public class Main2Activity extends SQLiteOpenHelper {

    private final static int DB_VERSION = 1; //


    private final static String DB_NAME = "Riding.db"; //Database Name


    private final static String INFO_TABLE = "bike_table";//table name
    private final static String ROW_ID = "rowId"; //row number

    private final static String CARDNUMBER = "cardnumber"; //column name 1

    private final static String CURRENT = "current"; //column name 2
    private final static String ELECTRICITY = "electricity";//column name 3
    private static final String FIELD_TITLE = "";
    private final static String CARDBALANCE="cardbalance";//column name 4
    private final static String GAIN="gain";//column name 5
    public Main2Activity(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        return;
    }


    public Cursor getCurrent(String cardnumber) {
        SQLiteDatabase db=this.getReadableDatabase();
        return  db.rawQuery("SELECT current FROM bike_table WHERE cardnumber="+cardnumber,null);
    }
    public Cursor getBalance(String cardnumber) {
        SQLiteDatabase db=this.getReadableDatabase();
        return  db.rawQuery("SELECT cardbalance FROM bike_table WHERE cardnumber="+cardnumber,null);
    }
    public void updateInfo( String cardnumber, String current) {
        SQLiteDatabase db =this.getReadableDatabase();
        String strSQL = "UPDATE bike_table SET current ="+current+" WHERE cardnumber = "+ cardnumber;
        db.execSQL(strSQL);
    }
    public void updateElectricity( String cardnumber, float electricity) {
        SQLiteDatabase db =this.getReadableDatabase();
        String strSQL = "UPDATE bike_table SET electricity ="+electricity+" WHERE cardnumber = "+ cardnumber;
        db.execSQL(strSQL);
    }
    public void updateBalance( String cardnumber, float balance) {
        SQLiteDatabase db =this.getReadableDatabase();
        String strSQL = "UPDATE bike_table SET current ="+balance+" WHERE cardnumber = "+ cardnumber;
        db.execSQL(strSQL);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {



        String createTable = "CREATE TABLE " + INFO_TABLE+ " ("
                + ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "

                + CARDNUMBER + " TEXT, "+ CARDBALANCE + " REAL, "+ GAIN + " REAL, "
                + CURRENT + " REAL, " + ELECTRICITY +" REAL);";

        db.execSQL(createTable);
        String createTable2 = "INSERT INTO bike_table VALUES(0,'96360885',0.00,0.00,0.00,0.00);";

        db.execSQL(createTable2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i2) {

    }


}