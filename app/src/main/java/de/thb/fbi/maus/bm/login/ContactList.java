package de.thb.fbi.maus.bm.login;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;
import de.thb.fbi.maus.bm.login.model.Contact;

import java.util.ArrayList;

public class ContactList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);



    }

    public ListAdapter readContacts() {
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(ContactsContract.Data.CONTENT_URI, null, null, null, ContactsContract.Data.DISPLAY_NAME);

        ArrayList<Contact> list = new ArrayList<>();
        while (!cursor.isAfterLast()) {
            Contact contact = new Contact(cursor.getInt(cursor.getColumnIndex(ContactsContract.Data.CONTACT_ID)),
                    cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME)));

            list.add(contact);
            cursor.moveToNext();
        }

        final ListAdapter adapter = new ArrayAdapter<Contact>(this, R.layout.activity_contact_list, list){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    LayoutInflater inflater = getLayoutInflater();
                    convertView = inflater.inflate(R.layout.contact_layout, parent);
                }

                TextView textView = (TextView) convertView.findViewById(R.id.contact_element_text_View);

                textView.setText(getItem(position).getName());

                return convertView;
            }
        };

        return adapter;
    }
}
