package com.stashevskiy.testtask;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.stashevskiy.testtask.data.ElementContract.ElementEntry;
import com.stashevskiy.testtask.reminder.AlarmScheduler;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;

public class EditorActivity extends AppCompatActivity implements com.wdullaer.materialdatetimepicker.time.TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener,
        LoaderManager.LoaderCallbacks<Cursor> {


    private static final int EXISTING_ELEMENT_LOADER = 0;

    private Uri mCurrentElementUri;

    private EditText textEditText;

    private Toolbar mToolbar;

    private Calendar mCalendar;

    private int mYear, mMonth, mHour, mMinute, mDay;

    private String mTime;
    private String mDate;

    private TextView mDateText, mTimeText;


    private TextView dateTextview;


    private TextView timeTextView;

    private String mActive;


    private boolean mElementHasChanged = false;


    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mElementHasChanged = true;
            return false;
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_element);


        Intent intent = getIntent();
        mCurrentElementUri = intent.getData();


        if (mCurrentElementUri == null) {
            // "Добавление элемента"
            setTitle(getString(R.string.editor_activity_title_new_element));

            invalidateOptionsMenu();
        } else {
            // "Редактировать"
            setTitle(getString(R.string.editor_activity_title_edit_element));

            // Инициализация загрузчика
            getLoaderManager().initLoader(EXISTING_ELEMENT_LOADER, null, this);
        }


        mToolbar = findViewById(R.id.toolbar);
        mDateText = findViewById(R.id.set_date);
        mTimeText = findViewById(R.id.set_time);
        mActive = "true";


        // Находим View для каждого поля
        textEditText = findViewById(R.id.element_text);
        dateTextview = findViewById(R.id.element_date);
        timeTextView = findViewById(R.id.element_time);


        mCalendar = Calendar.getInstance();
        mHour = mCalendar.get(Calendar.HOUR_OF_DAY);
        mMinute = mCalendar.get(Calendar.MINUTE);
        mYear = mCalendar.get(Calendar.YEAR);
        mMonth = mCalendar.get(Calendar.MONTH) + 1;
        mDay = mCalendar.get(Calendar.DATE);

        mDate = mDay + "/" + mMonth + "/" + mYear;
        mTime = mHour + ":" + mMinute;

        mDateText.setText(mDate);
        mTimeText.setText(mTime);


        textEditText.setOnTouchListener(mTouchListener);
        dateTextview.setOnTouchListener(mTouchListener);
        textEditText.setOnTouchListener(mTouchListener);

    }

    // Метод для записи элемента из редактора в базу данных
    private void insertElement() {

        // Читаем данных из View для каждого поля
        String textString = textEditText.getText().toString().trim();
        String dateString = mDateText.getText().toString().trim();
        String timeString = mTimeText.getText().toString().trim();


        // Проверка на запись нового элемента
        if (mCurrentElementUri == null &&
                TextUtils.isEmpty(textString) && TextUtils.isEmpty(dateString) &&
                TextUtils.isEmpty(timeString)) {
            return;
        }

        // Создаем ContentValues
        ContentValues values = new ContentValues();
        values.put(ElementEntry.COLUMN_ELEMENT_TEXT, textString);
        values.put(ElementEntry.COLUMN_ELEMENT_DATE, dateString);
        values.put(ElementEntry.COLUMN_ELEMENT_TIME, timeString);
        values.put(ElementEntry.KEY_ACTIVE, mActive);

        // Настройка календаря для создания уведомления
        mCalendar.set(Calendar.MONTH, --mMonth);
        mCalendar.set(Calendar.YEAR, mYear);
        mCalendar.set(Calendar.DAY_OF_MONTH, mDay);
        mCalendar.set(Calendar.HOUR_OF_DAY, mHour);
        mCalendar.set(Calendar.MINUTE, mMinute);
        mCalendar.set(Calendar.SECOND, 0);

        long selectedTimestamp = mCalendar.getTimeInMillis();


        if (mCurrentElementUri == null) {
            // Новый элемент
            Uri newUri = getContentResolver().insert(ElementEntry.CONTENT_URI, values);

            // Всплывающие сообщения
            if (newUri == null) {
                Toast.makeText(this, "Ошибка сохранения элемента", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Элемент сохранен", Toast.LENGTH_SHORT).show();
            }
        } else {

            // Либо обновление элемента
            int rowsAffected = getContentResolver().update(mCurrentElementUri, values, null, null);

            // Всплывающие сообщения
            if (rowsAffected == 0) {
                Toast.makeText(this, "Ошибка обновления элемента",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Элемент обновлён", Toast.LENGTH_SHORT).show();
            }
        }

        // Создание нового уведомления
        if (mActive.equals("true")) {
            new AlarmScheduler().setAlarm(getApplicationContext(), selectedTimestamp, mCurrentElementUri);
        }



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // "Раздуваем" меню из res/menu/menu_editor.xml
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentElementUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Переключатель нажатия для пунктов меню
        switch (item.getItemId()) {

            // Сохранить изменения элемента
            case R.id.action_save:
                mActive = "true";
                // Сохраняем элемент в базу данных
                insertElement();
                // Выход с текущей Activity
                finish();
                return true;

            // Удалить элемент
            case R.id.action_delete:
                mActive = "false";
                // Диалог удаления
                showDeleteConfirmationDialog();

                return true;

            // Вернуться в родительскую Activity (ElementsActivity)
            case android.R.id.home:
                if (!mElementHasChanged) {
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Диалог при несохраненных изменениях
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        if (!mElementHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                ElementEntry._ID,
                ElementEntry.COLUMN_ELEMENT_TEXT,
                ElementEntry.COLUMN_ELEMENT_DATE,
                ElementEntry.COLUMN_ELEMENT_TIME};


        return new CursorLoader(this,
                mCurrentElementUri,
                projection,
                null,
                null,
                null);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor == null || cursor.getCount() < 1) {
            return;
        }


        if (cursor.moveToFirst()) {
            int textColumnIndex = cursor.getColumnIndex(ElementEntry.COLUMN_ELEMENT_TEXT);
            int dateColumnIndex = cursor.getColumnIndex(ElementEntry.COLUMN_ELEMENT_DATE);
            int timeColumnIndex = cursor.getColumnIndex(ElementEntry.COLUMN_ELEMENT_TIME);

            String text = cursor.getString(textColumnIndex);
            String date = cursor.getString(dateColumnIndex);
            String time = cursor.getString(timeColumnIndex);

            textEditText.setText(text);
            mDateText.setText(date);
            mTimeText.setText(time);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        textEditText.setText("");
        dateTextview.setText("");
        timeTextView.setText("");
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });


        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Prompt the user to confirm that they want to delete this pet.
     */
    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteElement();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void deleteElement() {
        if (mCurrentElementUri != null) {

            int rowsDeleted = getContentResolver().delete(mCurrentElementUri, null, null);

            if (rowsDeleted == 0) {
                Toast.makeText(this, "Ошибка удаления элемента",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Элемент удалён",
                        Toast.LENGTH_SHORT).show();
            }
        }

        new AlarmScheduler().cancelAlarm(getApplicationContext(),mCurrentElementUri);

        finish();
    }

    public void setTime(View v) {
        Calendar now = Calendar.getInstance();
        TimePickerDialog tpd = TimePickerDialog.newInstance(
                this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                false
        );
        tpd.setThemeDark(false);
        tpd.show(getFragmentManager(), "Timepickerdialog");
    }

    public void setDate(View v) {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        monthOfYear++;
        mDay = dayOfMonth;
        mMonth = monthOfYear;
        mYear = year;
        mDate = dayOfMonth + "/" + monthOfYear + "/" + year;
        mDateText.setText(mDate);
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {

        mHour = hourOfDay;
        mMinute = minute;
        if (minute < 10) {
            mTime = hourOfDay + ":" + "0" + minute;
        } else {
            mTime = hourOfDay + ":" + minute;
        }
        mTimeText.setText(mTime);
    }
}
