package de.thb.fbi.maus.bm.login;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import de.thb.fbi.maus.bm.login.accessor.SQLiteRelationAccessor;
import de.thb.fbi.maus.bm.login.accessor.SQLiteTodoAccessor;
import de.thb.fbi.maus.bm.login.model.Contact;
import de.thb.fbi.maus.bm.login.model.TodoItem;

import java.util.ArrayList;

import static de.thb.fbi.maus.bm.login.TodoDetail.MY_PERMISSION_REQUEST_CONTACTS;

public class Contacts extends AppCompatActivity {
    public static final String CONTACT_ID = "contactId";
    private ArrayList<Contact> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        final ListView listView = (ListView) findViewById(R.id.contact_list_view_inContacts);
        final Button switchToTodosButton = (Button) findViewById(R.id.switch_todoItem_Button);

        int permissionCheck = ContextCompat.checkSelfPermission(Contacts.this, Manifest.permission.READ_CONTACTS);
        if(permissionCheck == PackageManager.PERMISSION_GRANTED) {

        } else if(permissionCheck == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(Contacts.this, new String[]{Manifest.permission.READ_CONTACTS}, MY_PERMISSION_REQUEST_CONTACTS);
        }

        this.initContacts();

        SQLiteTodoAccessor todoAccessor = new SQLiteTodoAccessor();
        todoAccessor.setActivity(this);
        ArrayList<TodoItem> items = todoAccessor.getItemList();
        SQLiteRelationAccessor relationAccessor = new SQLiteRelationAccessor();
        relationAccessor.setActivity(this);
        relationAccessor.init();
        relationAccessor.readRelations();

        ArrayList<Contact> tempList = new ArrayList<>();

        for (Contact c : list) {
            boolean check = false;
            for(TodoItem i : items) {
                TodoItem nItem = relationAccessor.makeItemContactReady(i);
                ArrayList<Long>contactIds = nItem.getAssociatedContacts();
                for(long l : contactIds) {
                    if(l == c.getId()) {
                        check = true;
                        break;
                    }
                }
            }
            if(check) {
                tempList.add(c);
            }
        }

        this.list = tempList;

        final ArrayAdapter<Contact> arrayAdapter = new ArrayAdapter<Contact>(this, R.layout.contact_layout, this.list) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if(v == null) {
                    LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = inflater.inflate(R.layout.contact_layout, parent, false);
                }
                TextView contactName = (TextView) v.findViewById(R.id.contact_element_text_View);

                contactName.setText(list.get(position).getName());

                return v;
            }
        };

        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent rIntent = new Intent(Contacts.this, ContactTodos.class);

                rIntent.putExtra(Contacts.CONTACT_ID, list.get(position).getId());

                startActivity(rIntent);
            }
        });


        switchToTodosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Contacts.this, Todos.class);

                startActivity(intent);
            }
        });
    }

    public void initContacts() {
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.Data.DISPLAY_NAME);

        this.list = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            Contact contact = new Contact(cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts._ID)),
                    cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME)));

            this.list.add(contact);
            cursor.moveToNext();
        }
        cursor.close();
    }
}
