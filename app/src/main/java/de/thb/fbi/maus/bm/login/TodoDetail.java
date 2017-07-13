package de.thb.fbi.maus.bm.login;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import de.thb.fbi.maus.bm.login.accessor.TodoItemAccessor;
import de.thb.fbi.maus.bm.login.accessor.intent.IntentTodoItemAccessor;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class TodoDetail extends AppCompatActivity {

    private IntentTodoItemAccessor accessor;
    private boolean imp;
    private long dueDate;
    private int gYear, gMonth, gDay, gHour, gMinute;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Calendar c = Calendar.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_detail);

         accessor = new IntentTodoItemAccessor();
         accessor.setActivity(this);

         //get resources
        final EditText nameEdit = (EditText) findViewById(R.id.detailsName);
        final Switch doneSwitch = (Switch) findViewById(R.id.switch_Done);

        final TextView dueDateEdit = (TextView) findViewById(R.id.detailsDueDate);
        final Button importButton = (Button) findViewById(R.id.details_button_favorite);

        final EditText descEdit = (EditText) findViewById(R.id.detailsDescription);
        final ImageButton imageButton = (ImageButton) findViewById(R.id.shareContacts_imageButton);

        //initialize TimePicker and save picked values
        final TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                gHour = hourOfDay;
                gMinute = minute;
                GregorianCalendar calendar = new GregorianCalendar(gYear,gMonth, gDay, gHour, gMinute);
                dueDate = calendar.getTimeInMillis();

                updateTime();
            }

        },c.get(Calendar.HOUR), c.get(Calendar.MINUTE), false );

        final DatePickerDialog datePickerDialog = new DatePickerDialog(TodoDetail.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                gYear = year;
                gMonth = month;
                gDay = dayOfMonth;

                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.RESULT_HIDDEN);
                timePickerDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                /*timePickerDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {

                    }
                });*/

                timePickerDialog.show();
            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

        final Button saveButton = (Button) findViewById(R.id.buttonSave);
        final Button deleteButton = (Button) findViewById(R.id.buttonDelete);


        //initialize existing or new item
        if(accessor.hasItem()){
            nameEdit.setText(accessor.readItem().getName());
            descEdit.setText(accessor.readItem().getDesciption());
            dueDate = accessor.readItem().getDueDate();
            updateTime();

            doneSwitch.setChecked(accessor.readItem().isDone());

            if(accessor.readItem().isImportant()) {
                importButton.setBackgroundResource(R.mipmap.favorite_true);
                imp = true;
            } else {
                importButton.setBackgroundResource(R.mipmap.favorite_false);
                imp = false;
            }

        } else {
            accessor.createItem();
        }





        // set trigger to open up the DatePicker
        dueDateEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.RESULT_HIDDEN);

                datePickerDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                /*datePickerDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {

                    }
                });*/

                datePickerDialog.show();



            }
        });




        // save item and return to list acitivty
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processItemSave(accessor, nameEdit, descEdit, dueDate, doneSwitch, importButton);
            }
        });
        // delete item and return to list activity
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(TodoDetail.this)
                        .setMessage(R.string.delete_message)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                processItemDelete();
                            }
                        })
                        .setNegativeButton(R.string.no, null)
                        .show();
            }
        });

        importButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!imp) {
                    importButton.setBackgroundResource(R.mipmap.favorite_true);
                    imp = true;
                } else {
                    importButton.setBackgroundResource(R.mipmap.favorite_false);
                    imp = false;
                }
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TodoDetail.this, ContactList.class);

                startActivity(intent);
            }
        });
    }



    // save item and return to list activity
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        processItemSave(accessor, (EditText) findViewById(R.id.detailsName), (EditText)findViewById(R.id.detailsDescription), dueDate,
                (Switch)findViewById(R.id.switch_Done), (Button)findViewById(R.id.details_button_favorite));
    }

    private void processItemSave(TodoItemAccessor accessor, EditText name, EditText desc, long dueDate, Switch done, Button important) {

        accessor.readItem().setName(name.getText().toString());
        accessor.readItem().setDesciption(desc.getText().toString());
        accessor.readItem().setDueDate(dueDate);
        if(done.isChecked()) {
            accessor.readItem().setDone(true);
        } else {
            accessor.readItem().setDone(false);
        }
        if(imp){
            accessor.readItem().setImportant(true);
        } else {
            accessor.readItem().setImportant(false);
        }


        accessor.writeItem();

        finish();
    }

    private void processItemDelete() {
        accessor.deleteItem();

        finish();
    }

    private void updateTime() {
        Date d = new Date(dueDate);
        String time = new SimpleDateFormat("MM/dd/yyyy - hh:mm", Locale.US).format(d) + " " +(d.getHours() < 12? "AM" : "PM");
        ((TextView)findViewById(R.id.detailsDueDate)).setText(time);
    }


}
