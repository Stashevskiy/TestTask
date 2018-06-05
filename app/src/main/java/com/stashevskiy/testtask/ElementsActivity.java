package com.stashevskiy.testtask;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.stashevskiy.testtask.data.ElementContract.ElementEntry;

public class ElementsActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>{



    private static final int ElEMENT_LOADER = 0;

    ElementCursorAdapter mCursorAdapter;

    Menu menu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elements);

        // Находим FAB
        FloatingActionButton fab = findViewById(R.id.fab);

        // Устанавливаем прослушиватель
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Создаем намерение
                Intent intent = new Intent(ElementsActivity.this, EditorActivity.class);

                // Запускаем его
                startActivity(intent);
            }
        });


        ListView elementListView = findViewById(R.id.list);

        View emptyView = findViewById(R.id.empty_view);
        elementListView.setEmptyView(emptyView);

        mCursorAdapter = new ElementCursorAdapter(this, null);
        elementListView.setAdapter(mCursorAdapter);


        elementListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Intent intent = new Intent(ElementsActivity.this, EditorActivity.class);

                Uri currentPetUri = ContentUris.withAppendedId(ElementEntry.CONTENT_URI, id);

                intent.setData(currentPetUri);

                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(ElEMENT_LOADER, null, this);

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        this.menu = menu;

        // "Раздуваем" меню из res/menu/menu_elements.xml
        getMenuInflater().inflate(R.menu.menu_elements, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        // Переключатель нажатия для пунктов меню
        switch (item.getItemId()) {
            case R.id.action_delete_all:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Очистить список");
                builder.setMessage("Вы уверены, что хотите удалить все ваши элементы?");
                builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //menu.findItem(R.id.action_delete_all).setVisible(false);
                        deleteAllElements();
                    }
                });

                builder.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.show();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteAllElements() {
        int rowsDeleted = getContentResolver().delete(ElementEntry.CONTENT_URI, null, null);
        Log.v("ElementsActivity", rowsDeleted + " строки удалены");
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                ElementEntry._ID,
                ElementEntry.COLUMN_ELEMENT_TEXT,
                ElementEntry.COLUMN_ELEMENT_DATE };


        return new CursorLoader(this,
                ElementEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}
