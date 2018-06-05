package com.stashevskiy.testtask;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import static com.stashevskiy.testtask.data.ElementContract.ElementEntry;


public class ElementCursorAdapter extends CursorAdapter {


    public ElementCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView textTextView = view.findViewById(R.id.text_element);
        TextView dateTextView = view.findViewById(R.id.date_and_time_element);


        int textColumnIndex = cursor.getColumnIndex(ElementEntry.COLUMN_ELEMENT_TEXT);
        int dateColumnIndex = cursor.getColumnIndex(ElementEntry.COLUMN_ELEMENT_DATE);
        int timeColumnIndex = cursor.getColumnIndex(ElementEntry.COLUMN_ELEMENT_TIME);


        String elementText = cursor.getString(textColumnIndex);
        String elementDate = cursor.getString(dateColumnIndex);
        //String elementTime = cursor.getString(timeColumnIndex);

        //String dateTime = elementDate + " " + elementTime;


        textTextView.setText(elementText);
        dateTextView.setText(elementDate);
        //timeTextView.setText(elementTime);
    }
}
