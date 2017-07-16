package de.thb.fbi.maus.bm.login;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.*;
import de.thb.fbi.maus.bm.login.accessor.CRUDAccessor;
import de.thb.fbi.maus.bm.login.accessor.CursorAdapterTodoItemListAccessor;
import de.thb.fbi.maus.bm.login.accessor.SQLiteDBHelper;
import de.thb.fbi.maus.bm.login.accessor.SQLiteRelationAccessor;
import de.thb.fbi.maus.bm.login.accessor.intent.IntentContactListAccessor;
import de.thb.fbi.maus.bm.login.model.TodoItem;

import java.util.ArrayList;

import static de.thb.fbi.maus.bm.login.TodoDetail.MY_PERMISSION_REQUEST_CONTACTS;

public class Todos extends AppCompatActivity {
    public static final String ARG_ITEM_OBJECT = "itemObject";

    public static final int RESPONSE_ITEM_EDITED = 1;
    public static final int RESPONSE_ITEM_DELETED = 2;
    public static final int RESPONSE_NOCHANGE = -1;

    public static final int REQUEST_ITEM_DETAILS = 1;
    public static final int REQUEST_ITEM_CREATION = 2;

    public static boolean online = false;
    private TodoItem cItem;

    private CursorAdapterTodoItemListAccessor accessor;
    private SQLiteRelationAccessor relationAccessor;
    private CRUDAccessor webAccessor;

    private final String logger = Todos.class.getName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todos);

        final String logger = Todos.class.getName();

        //Get resources from layout
        final ListView listView = (ListView) findViewById(R.id.list_view);
        final FloatingActionButton newButton = (FloatingActionButton) findViewById(R.id.newTodo);
        final Spinner spinner = (Spinner) findViewById(R.id.spinner);
        final Button switchToContactsButton = (Button) findViewById(R.id.switch_Contacts_Button);

        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.sort_options, R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        accessor = new CursorAdapterTodoItemListAccessor();
        accessor.setActivity(this);

        relationAccessor = new SQLiteRelationAccessor();
        relationAccessor.setActivity(this);
        relationAccessor.init();

        if (online) {
            webAccessor = new CRUDAccessor(CRUDAccessor.getBaseURL());
            webAccessor.init();
            if (webAccessor.getClient() == null) {

            }
        }

        int permissionCheck = ContextCompat.checkSelfPermission(Todos.this, Manifest.permission.READ_CONTACTS);
        if(permissionCheck == PackageManager.PERMISSION_GRANTED) {

        } else if(permissionCheck == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(Todos.this, new String[]{Manifest.permission.READ_CONTACTS}, MY_PERMISSION_REQUEST_CONTACTS);
        }

        final ListAdapter listAdapter = accessor.getAdapter();

        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(logger, "onItemClick: Position is " + position + ", id is " + id);
                TodoItem item = accessor.getSelectedItem(position, (int) id);

                relationAccessor.readRelations();
                item = relationAccessor.makeItemContactReady(item);

                processItemSelection(item);
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO: 18.06.2017 How to reload the listView? 
                SQLiteDBHelper.ordering_method = position;
                Log.i(logger, "Position: " + position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processNewItemRequest();
            }
        });

        switchToContactsButton.setOnClickListener(new View.OnClickListener() {
                                                      @Override
                                                      public void onClick(View v) {
                                                          Intent intent = new Intent(Todos.this, Contacts.class);

                                                          startActivity(intent);
                                                      }
                                                  }
        );
    }

    private void processNewItemRequest() {
        Intent intent = new Intent(Todos.this, TodoDetail.class);

        startActivityForResult(intent, REQUEST_ITEM_CREATION);
    }

    private void processItemSelection(TodoItem item) {
        Intent intent = new Intent(Todos.this, TodoDetail.class);

        intent.putExtra(ARG_ITEM_OBJECT, item);

        startActivityForResult(intent, REQUEST_ITEM_DETAILS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        TodoItem item = data != null ? (TodoItem) data.getSerializableExtra(ARG_ITEM_OBJECT) : null;

        // TODO: 16.06.2017 - Implement result NO_CHANGE
        Log.i(logger, "onActivityResult() called: Checking result...");
        if (requestCode == REQUEST_ITEM_DETAILS) {
            if (resultCode == RESPONSE_ITEM_EDITED) {
                Log.i(logger, "existing item saved, updating db.");
                this.cItem = item;
                this.accessor.updateItem(item);
                this.relationAccessor.handleRelations(item);
            } else if (resultCode == RESPONSE_ITEM_DELETED) {
                item.setAssociatedContacts(new ArrayList<Long>());
                this.accessor.deleteItem(item);
                this.relationAccessor.handleRelations(item);
            }
        } else if (requestCode == REQUEST_ITEM_CREATION && resultCode == RESPONSE_ITEM_EDITED) {
            this.accessor.addItem(item);
            this.relationAccessor.handleRelations(item);
        }
    }

    @Override
    protected void onDestroy() {
        Log.i(logger, "onDestroy(): closing accessor");
        super.onDestroy();
        this.accessor.close();
        this.relationAccessor.close();
    }

    public CursorAdapterTodoItemListAccessor getAccessor() {
        return accessor;
    }
}
