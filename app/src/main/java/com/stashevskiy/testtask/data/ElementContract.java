package com.stashevskiy.testtask.data;


import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

public final class ElementContract {


    // Пустой конструктор
    private ElementContract(){

    }

    public static final String CONTENT_AUTHORITY = "com.stashevskiy.testtask";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_ELEMENTS = "elements";


    // Внутренний класс, который содержит необходимые константы для создания таблицы базы данных
    public static final class ElementEntry implements BaseColumns{

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ELEMENTS);

        public static final String CONTENT_LIST_TYPE =
                                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ELEMENTS;

        public static final String CONTENT_ITEM_TYPE =
                                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ELEMENTS;


        public final static String TABLE_NAME = "elements";

        public final static String _ID = BaseColumns._ID;

        public final static String COLUMN_ELEMENT_TEXT = "text";

        public final static String COLUMN_ELEMENT_DATE = "date";

        public final static String COLUMN_ELEMENT_TIME = "time";

        public static final String KEY_ACTIVE = "active";

    }

    public static String getColumnString(Cursor cursor, String columnText) {
        return cursor.getString( cursor.getColumnIndex(columnText) );
    }
}
