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
import de.thb.fbi.maus.bm.login.accessor.intent.IntentContactListAccessor;
import de.thb.fbi.maus.bm.login.model.Contact;

import java.util.ArrayList;

public class ContactList extends AppCompatActivity {
    private ArrayList<Contact> list;
    private ArrayList<Long> selectedContacts;
    private IntentContactListAccessor accessor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        final ListView contactList = (ListView) findViewById(R.id.contacts_listView);
        final Button saveButton = (Button) findViewById(R.id.contact_list_save_button);
        final Button returnButton = (Button) findViewById(R.id.contact_list_return_button);

        this.accessor = new IntentContactListAccessor();
        this.accessor.setActivity(this);

        if(accessor.hasItem()) {
            this.selectedContacts = accessor.readItem().getAssociatedContacts();
        }

        contactList.setAdapter(readContacts());
        contactList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                boolean check = false;
                for(long e : selectedContacts) {
                    if(e == list.get(position).getId()) {
                        check = true;
                        break;
                    }
                }
                if(!check) {
                    selectedContacts.add(list.get(position).getId());
                    ((TextView)contactList.getChildAt(position).findViewById(R.id.contact_element_text_View)).setTextColor(getColor(R.color.black));
                } else {
                    selectedContacts.remove(list.get(position).getId());
                    ((TextView)contactList.getChildAt(position).findViewById(R.id.contact_element_text_View)).setTextColor(getColor(R.color.lightGrey));
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processContactsSelected();
            }
        });
    }

    public ListAdapter readContacts() {
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.Data.DISPLAY_NAME);

        list = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            Contact contact = new Contact(cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts._ID)),
                    cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME)));

            list.add(contact);
            cursor.moveToNext();
        }
        cursor.close();
        final ListAdapter adapter = new ArrayAdapter<Contact>(this, R.layout.contact_layout, list){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                boolean isSelected = false;
                View v = convertView;
                if (v == null) {
                    LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = inflater.inflate(R.layout.contact_layout, parent, false);
                }

                TextView textView = (TextView) v.findViewById(R.id.contact_element_text_View);

                textView.setText(getItem(position).getName());

                for (long c : selectedContacts) {
                    if(c == getItem(position).getId()) {
                        isSelected = true;
                        break;
                    }
                }
                if(!isSelected)
                    textView.setTextColor(getColor(R.color.lightGrey));

                return v;
            }
        };

        return adapter;
    }

    public void processContactsSelected() {
        this.accessor.readItem().setAssociatedContacts(this.selectedContacts);
        this.accessor.writeItem();

        finish();
    }
}
