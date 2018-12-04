package mx.edu.ittepic.dadm_u4_practica1_alejandragrande;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BaseDatos extends SQLiteOpenHelper {
    public BaseDatos(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE RECETA (ID INTEGER PRIMARY KEY, NOMBRE VARCHAR(200), INGREDIENTES VARCHAR (1000), PREPARACION VARCHAR (1000), OBSEVACIONES VARCHAR (500))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
