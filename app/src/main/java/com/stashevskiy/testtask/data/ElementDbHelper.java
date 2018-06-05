package com.stashevskiy.testtask.data;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.stashevskiy.testtask.data.ElementContract.*;

public class ElementDbHelper extends SQLiteOpenHelper {

    // Имя базы данных
    private static final String DATABASE_NAME = "myDatabase.db";

    // Версия базы данных
    private static final int DATABASE_VERSION = 1;

    // Конструктор
    public ElementDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    // Метод создания базы данных
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // Создаем строку, содержащую инструкцию SQL, чтобы создать таблицу для элементов
        String SQL_CREATE_ELEMENTS_TABLE =  "CREATE TABLE " + ElementEntry.TABLE_NAME + " ("
                + ElementEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ElementEntry.COLUMN_ELEMENT_TEXT + " TEXT NOT NULL, "
                + ElementEntry.COLUMN_ELEMENT_DATE + " TEXT NOT NULL, "
                + ElementEntry.KEY_ACTIVE + " TEXT, "
                + ElementEntry.COLUMN_ELEMENT_TIME + " TEXT NOT NULL);";

        // Выполнить SQL инструкцию
        sqLiteDatabase.execSQL(SQL_CREATE_ELEMENTS_TABLE);

    }

    // Метод при обновлении базы данных при изменении её версии
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
