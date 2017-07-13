package de.thb.fbi.maus.bm.login;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import de.thb.fbi.maus.bm.login.model.Contact;

import java.util.ArrayList;

public class ContactList extends AppCompatActivity {
    ArrayList<Contact> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        ListView contactList = (ListView) findViewById(R.id.contacts_listView);

        contactList.setAdapter(readContacts());
        contactList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

    }

    public ListAdapter readContacts() {
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(ContactsContract.RawContacts.CONTENT_URI, null, null, null, ContactsContract.Data.DISPLAY_NAME);

        list = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Contact contact = new Contact(cursor.getInt(cursor.getColumnIndex(ContactsContract.Data.CONTACT_ID)),
                    cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME)));

            list.add(contact);
            cursor.moveToNext();
        }

        final ListAdapter adapter = new ArrayAdapter<Contact>(this, R.layout.contact_layout, list){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = inflater.inflate(R.layout.contact_layout, parent, false);
                }

                TextView textView = (TextView) v.findViewById(R.id.contact_element_text_View);

                textView.setText(getItem(position).getName());

                return v;
            }
        };

        return adapter;
    }
}
