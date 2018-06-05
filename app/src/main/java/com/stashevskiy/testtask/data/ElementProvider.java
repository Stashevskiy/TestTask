package com.stashevskiy.testtask.data;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.stashevskiy.testtask.data.ElementContract.ElementEntry;

public class ElementProvider extends ContentProvider {


    // Индентификатор для таблицы элементов
    private static final int ELEMENTS = 100;

    // Индентификатор для каждого отдельного элемента
    private static final int ELEMENT_ID = 101;

    // UriMatcher
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);


    // Статический инициализатор
    static {
        // Добавляем URI для доступа к многострочным строка таблицы элементов
        sUriMatcher.addURI(ElementContract.CONTENT_AUTHORITY, ElementContract.PATH_ELEMENTS, ELEMENTS);

        // Добавляем URI для доступа к каждому отдельному элементу
        sUriMatcher.addURI(ElementContract.CONTENT_AUTHORITY,
                ElementContract.PATH_ELEMENTS + "/#", ELEMENT_ID);
    }


    // ElementDbHelper
    private ElementDbHelper dbHelper;


    @Override
    public boolean onCreate() {
        // Создаем экземпляр ElementDbHelper
        dbHelper = new ElementDbHelper(getContext());
        return true;
    }


    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        // Устанавливаем режим для чтения базы данных
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        // Cursor будет содержать результат запроса
        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case ELEMENTS:
                // Запрашиваем данные для всей таблицы
                cursor = database.query(ElementEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case ELEMENT_ID:
                // Запрашиваем данные для конкретного элемента
                selection = ElementEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                cursor = database.query(ElementEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Не удается запросить неизвестный URI " + uri);
        }


        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Возвращаем курсор
        return cursor;
    }

    // Метод добавления новых данных
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ELEMENTS:
                return insertElement(uri, contentValues);
            default:
                throw new IllegalArgumentException("Добавление не поддерживается для " + uri);
        }
    }

    private Uri insertElement(Uri uri, ContentValues values) {

        // Проверка текста
        String text = values.getAsString(ElementEntry.COLUMN_ELEMENT_TEXT);
        if (text == null) {
            throw new IllegalArgumentException("Элемент требует текст");
        }

        // Проверка даты
        String date = values.getAsString(ElementEntry.COLUMN_ELEMENT_DATE);
        if (date == null) {
            throw new IllegalArgumentException("Элемент требует дату");
        }

        // Проверка времени
        String time = values.getAsString(ElementEntry.COLUMN_ELEMENT_TIME);
        if (time == null) {
            throw new IllegalArgumentException("Элемент требует время");
        }


        // Устанавливаем режим для чтения базы данных
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        // Новый элемент
        long id = database.insert(ElementEntry.TABLE_NAME, null, values);

        // Если -1, то добавление провалено
        if (id == -1) {
            return null;
        }

        // Сохраняем изменение данных
        getContext().getContentResolver().notifyChange(uri, null);

        // Возвращаем новый URI
        return ContentUris.withAppendedId(uri, id);
    }


    // Метод для обновления данных
    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ELEMENTS:
                return updateElement(uri, contentValues, selection, selectionArgs);
            case ELEMENT_ID:
                selection = ElementEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateElement(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Обновления не поддерживается для " + uri);
        }
    }

    private int updateElement(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        // Проверяем текста
        if (values.containsKey(ElementEntry.COLUMN_ELEMENT_TEXT)) {
            String text = values.getAsString(ElementEntry.COLUMN_ELEMENT_TEXT);
            if (text == null) {
                throw new IllegalArgumentException("Элемент требует текст");
            }
        }

        // Проверка даты
        if (values.containsKey(ElementEntry.COLUMN_ELEMENT_DATE)) {
            String date = values.getAsString(ElementEntry.COLUMN_ELEMENT_DATE);
            if (date == null) {
                throw new IllegalArgumentException("Элемент требует дату");
            }
        }

        // Проверка времени
        if (values.containsKey(ElementEntry.COLUMN_ELEMENT_TIME)) {
            String date = values.getAsString(ElementEntry.COLUMN_ELEMENT_TIME);
            if (date == null) {
                throw new IllegalArgumentException("Элемент требует время");
            }
        }


        // Не обновляем базу данных, если нет значений
        if (values.size() == 0) {
            return 0;
        }

        // Получаем базу данных, пригодную для записи, для обновления данных
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        // Количество обновленных строк
        int rowsUpdated = database.update(ElementEntry.TABLE_NAME, values, selection, selectionArgs);

        // Сохраняем изменения
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Возвращаем количество обновленных строк
        return rowsUpdated;
    }


    // Метод для удаления данных
    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        // Режим чтения базы данных
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        // Отслеживаем количество удаленных строк
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ELEMENTS:
                // Удаляем все строки
                rowsDeleted = database.delete(ElementEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case ELEMENT_ID:
                // Удаляем конкретную строку
                selection = ElementEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(ElementEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Удаление не поддерживается для " + uri);
        }

        // Сохраняем изменения
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Возвращаем количество удаленных строк
        return rowsDeleted;
    }


    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ELEMENTS:
                return ElementEntry.CONTENT_LIST_TYPE;
            case ELEMENT_ID:
                return ElementEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Неизвестный URI " + uri + " с " + match);
        }
    }

}