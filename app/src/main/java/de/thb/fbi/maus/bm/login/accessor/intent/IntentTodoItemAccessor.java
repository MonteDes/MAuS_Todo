package de.thb.fbi.maus.bm.login.accessor.intent;

import android.content.Intent;
import de.thb.fbi.maus.bm.login.Todos;
import de.thb.fbi.maus.bm.login.accessor.TodoItemAccessor;
import de.thb.fbi.maus.bm.login.accessor.shared.AbstractActivityDataAccessor;
import de.thb.fbi.maus.bm.login.model.TodoItem;

/**
 * @author Benedikt Michaelis
 */
public class IntentTodoItemAccessor extends AbstractActivityDataAccessor implements TodoItemAccessor {
    private TodoItem item;

    @Override
    public TodoItem readItem() {
        if(this.item == null)
            this.item = (TodoItem) getActivity().getIntent().getSerializableExtra(Todos.ARG_ITEM_OBJECT);
        return this.item;
    }

    @Override
    public void writeitem() {
        Intent returnIntent = new Intent();

        returnIntent.putExtra(Todos.ARG_ITEM_OBJECT, this.item);
        getActivity().setResult(Todos.RESPONSE_ITEM_EDITED, returnIntent);
    }

    @Override
    public boolean hasItem() {
        return readItem() != null;
    }

    @Override
    public void createItem() {
        this.item = new TodoItem("", "", false, 0, false);
    }

    @Override
    public void deleteItem() {
        Intent returnIntent = new Intent();

        returnIntent.putExtra(Todos.ARG_ITEM_OBJECT, this.item);
        getActivity().setResult(Todos.RESPONSE_ITEM_DELETED, returnIntent);
    }

}
