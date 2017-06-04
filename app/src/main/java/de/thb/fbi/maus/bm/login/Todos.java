package de.thb.fbi.maus.bm.login;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import de.thb.fbi.maus.bm.login.accessor.CursorAdapterTodoItemListAccessor;
import de.thb.fbi.maus.bm.login.accessor.TodoItemListAccessor;
import de.thb.fbi.maus.bm.login.model.TodoItem;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

public class Todos extends AppCompatActivity {
    public static final String ARG_ITEM_OBJECT = "itemObject";

    public static final int RESPONSE_ITEM_EDITED = 1;
    public static final int RESPONSE_ITEM_DELETED = 2;
    public static final int RESPONSE_NOCHANGE = -1;

    public static final int REQUEST_ITEM_DETAILS = 1;
    public static final int REQUEST_ITEM_CREATION = 2;

    private CursorAdapterTodoItemListAccessor accessor;
    private ListView listView;
    private final String logger = Todos.class.getName();


 @Override
 protected void onCreate(Bundle savedInstanceState) {
     super.onCreate(savedInstanceState);
     setContentView(R.layout.activity_todos);

     final String logger = Todos.class.getName();

     //Get resources from layout
     final Resources res = getResources();
     listView = (ListView) findViewById(R.id.list_view);
     final Button newButton = (Button) findViewById(R.id.newTodo);

     accessor = new CursorAdapterTodoItemListAccessor();
     accessor.setActivity(this);

     final ListAdapter listAdapter = accessor.getAdapter();

     listView.setAdapter(listAdapter);
     listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
         @Override
         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
             Log.i(logger, "onItemClick: Position is " + position + ", id is " + id);
             TodoItem item = accessor.getSelectedItem(position, (int)id);

             processItemSelection(item);
         }
     });

     newButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             processNewItemRequest();
         }
     });
 }

    public void processNewItemRequest() {
    Intent intent = new Intent(Todos.this, TodoDetail.class);

    startActivityForResult(intent, REQUEST_ITEM_CREATION);
}

    public void processItemSelection(TodoItem item) {
        Intent intent = new Intent(Todos.this, TodoDetail.class);

        intent.putExtra(ARG_ITEM_OBJECT, item);

        startActivityForResult(intent, REQUEST_ITEM_DETAILS );
}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        TodoItem item = data != null ? (TodoItem) data.getSerializableExtra(ARG_ITEM_OBJECT) : null;

        Log.i(logger, "onActivityResult() called: Checking result...");
        if(requestCode == REQUEST_ITEM_DETAILS) {
            if(resultCode == RESPONSE_ITEM_EDITED) {
                Log.i(logger, "existing item saved, updating db.");
                this.accessor.updateItem(item);
            } else if (resultCode == RESPONSE_ITEM_DELETED) {
                this.accessor.deleteItem(item);
            }
        } else if(requestCode == REQUEST_ITEM_CREATION && resultCode == RESPONSE_ITEM_EDITED) {
            this.accessor.addItem(item);
        }
    }

    @Override
    protected void onDestroy() {
        Log.i(logger, "onDestroy(): closing accessor");
        super.onDestroy();
        this.accessor.close();
    }

    public CursorAdapterTodoItemListAccessor getAccessor() {
        return accessor;
    }
}
