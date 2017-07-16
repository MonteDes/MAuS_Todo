package de.thb.fbi.maus.bm.login;

import android.Manifest;
import android.content.*;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import de.thb.fbi.maus.bm.login.accessor.CursorAdapterTodoItemListAccessor;
import de.thb.fbi.maus.bm.login.accessor.SQLiteRelationAccessor;
import de.thb.fbi.maus.bm.login.accessor.SQLiteTodoAccessor;
import de.thb.fbi.maus.bm.login.model.Contact;
import de.thb.fbi.maus.bm.login.model.TodoItem;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;

public class ContactTodos extends AppCompatActivity {
    private long contactId;
    private ArrayList<TodoItem> items;
    private final int MY_PERMISSIONS_REQUEST_SEND_SMS = 199;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_todos);

        final Button backButton = (Button) findViewById(R.id.contact_todos_backButton);
        final ListView listView = (ListView) findViewById(R.id.contact_todos_listView);
        contactId = (long) this.getIntent().getSerializableExtra(Contacts.CONTACT_ID);

        SQLiteTodoAccessor accessor = new SQLiteTodoAccessor();
        accessor.setActivity(this);
        SQLiteRelationAccessor relationAccessor = new SQLiteRelationAccessor();
        relationAccessor.setActivity(this);
        relationAccessor.init();
        relationAccessor.readRelations();

        this.items = accessor.getItemList();

        for (TodoItem e : items) {
            boolean itemIsRelated = false;
            e = relationAccessor.makeItemContactReady(e);
            ArrayList<Long> contacts = e.getAssociatedContacts();

            for(long l : contacts) {
                if(l == contactId)
                    itemIsRelated = true;
            }
        if(!itemIsRelated)
            items.remove(e);
        }

        ArrayAdapter<TodoItem> arrayAdapter = new ArrayAdapter<TodoItem>(this, R.layout.todo_layout, this.items) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View retView = convertView;
                GregorianCalendar calendar = new GregorianCalendar();
                TextView todoName;
                CheckBox done;
                Button importButton;


                if (convertView == null) {
                    LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    retView = inflater.inflate(R.layout.todo_layout, parent, false);
                }

                todoName = (TextView) retView.findViewById(R.id.todo_name_mainView);
                done = (CheckBox) retView.findViewById(R.id.checkBox_done);
                importButton = (Button) retView.findViewById(R.id.important_button);

                todoName.setText(String.format(getContext().getString(R.string.todo_nameANDduedate),
                        items.get(position).getName(),
                        DateFormat.getDateInstance(DateFormat.MEDIUM).format(items.get(position).getDueDate())));

                if(items.get(position).getDueDate() < calendar.getTimeInMillis())
                    todoName.setTextColor(parent.getContext().getColor(R.color.redAttention));

                if(items.get(position).isDone()) {
                    todoName.setTextColor(parent.getContext().getColor(R.color.lightGrey));
                    done.setChecked(true);
                } else {
                    done.setChecked(false);
                }

                if(!items.get(position).isImportant()) {
                    importButton.setBackgroundResource(R.mipmap.favorite_false);
                } else {
                    importButton.setBackgroundResource(R.mipmap.favorite_true);
                }

                importButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                importButton.setClickable(false);
                done.setClickable(false);

                return retView;
            }
        };
        arrayAdapter.setNotifyOnChange(true);
        arrayAdapter.notifyDataSetChanged();

        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ContactTodos.this, TodoDetail.class);

                intent.putExtra(Todos.ARG_ITEM_OBJECT, items.get(position));

                Toast.makeText(ContactTodos.this, "View Mode. Changes will NOT be saved.", Toast.LENGTH_LONG).show();
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final CharSequence [] options = new CharSequence[]{"Email", "SMS"};

                AlertDialog.Builder dialog = new AlertDialog.Builder(ContactTodos.this);

                dialog.setTitle("Send Email or SMS");
                dialog.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(options[which] == "Email") {
                            String adress = getEmail();
                            if(adress.equals("NO_EMAIL_ATTACHED")) {
                                Toast.makeText(ContactTodos.this, "This contact has no Email.", Toast.LENGTH_SHORT).show();
                            } else {
                                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                                emailIntent.setType("message/rfc822");
                                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{adress});
                                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "TEST JO");
                                emailIntent.putExtra(Intent.EXTRA_TEXT, "hey you suck");
                                //emailIntent.setData(Uri.parse("mailto:" + adress));

                                try {
                                    startActivity(Intent.createChooser(emailIntent, "Send Email..."));
                                } catch (ActivityNotFoundException ae) {
                                    Toast.makeText(ContactTodos.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            String adress = getPhoneNumber();
                            if(adress.equals("NO_PHONE_ATTACHED")) {
                                Toast.makeText(ContactTodos.this, "This contact has no Phone.", Toast.LENGTH_SHORT).show();
                            } else {
                                if (ContextCompat.checkSelfPermission(ContactTodos.this,
                                        Manifest.permission.SEND_SMS)
                                        != PackageManager.PERMISSION_GRANTED) {
                                    if (ActivityCompat.shouldShowRequestPermissionRationale(ContactTodos.this,
                                            Manifest.permission.SEND_SMS)) {
                                    } else {
                                        ActivityCompat.requestPermissions(ContactTodos.this,
                                                new String[]{Manifest.permission.SEND_SMS},
                                                MY_PERMISSIONS_REQUEST_SEND_SMS);
                                    }
                                } else {
                                    Intent phoneIntent = new Intent(Intent.ACTION_VIEW);
                                    phoneIntent.setType("vnd.android-dir/mms-sms");
                                    phoneIntent.setData(Uri.parse("sms:" + getPhoneNumber()));
                                    phoneIntent.putExtra("sms_body", items.get(position).getName() +
                                            "\n\nDescription:\n\t" + items.get(position).getDesciption());
                                    try {
                                        startActivity(Intent.createChooser(phoneIntent, "Send SMS..."));
                                    } catch (ActivityNotFoundException ae) {
                                        Toast.makeText(ContactTodos.this, "No SMS client installed.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                            }


                        }
                    });

                dialog.show();

                return true;
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private String getEmail() {
        String ret;
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(
                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                new String[]{contactId + ""},
                null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            ret = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
        } else {
            ret = "NO_EMAIL_ATTACHED";
        }
        cursor.close();
        return ret;
    }
    private String getPhoneNumber() {
        String ret;
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email._ID + " = ?", new String[]{contactId + ""}, null);
        cursor.moveToFirst();
        if(cursor.getCount() > 0) {
            ret = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA));
        } else {
            ret = "NO_PHONE_ATTACHED";
        }
        cursor.close();
        return ret;
    }
}
