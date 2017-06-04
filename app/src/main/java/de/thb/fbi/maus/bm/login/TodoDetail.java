package de.thb.fbi.maus.bm.login;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import de.thb.fbi.maus.bm.login.accessor.TodoItemAccessor;
import de.thb.fbi.maus.bm.login.accessor.intent.IntentTodoItemAccessor;

public class TodoDetail extends AppCompatActivity {

    private IntentTodoItemAccessor accessor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_detail);

         accessor = new IntentTodoItemAccessor();
         accessor.setActivity(this);
        final EditText nameEdit = (EditText) findViewById(R.id.detailsName);
        final EditText descEdit = (EditText) findViewById(R.id.detailsDescription);
        final Button saveButton = (Button) findViewById(R.id.buttonSave);
        final Button deleteButton = (Button) findViewById(R.id.buttonDelete);

        if(accessor.hasItem()){
            nameEdit.setText(accessor.readItem().getName());
            descEdit.setText(accessor.readItem().getDesciption());

        } else {
            accessor.createItem();
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processItemSave(accessor, nameEdit, descEdit);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processItemDelete();
            }
        });
    }

    protected void processItemSave(TodoItemAccessor accessor, EditText name, EditText desc) {
        accessor.readItem().setName(name.getText().toString());
        accessor.readItem().setDesciption(desc.getText().toString());

        accessor.writeitem();

        finish();
    }

    private void processItemDelete() {
        accessor.deleteItem();

        finish();
    }
}
