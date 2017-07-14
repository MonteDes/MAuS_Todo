package de.thb.fbi.maus.bm.login.accessor.intent;

import android.content.Intent;
import de.thb.fbi.maus.bm.login.TodoDetail;
import de.thb.fbi.maus.bm.login.Todos;
import de.thb.fbi.maus.bm.login.accessor.AssociatedContactsAccessor;
import de.thb.fbi.maus.bm.login.accessor.TodoItemAccessor;
import de.thb.fbi.maus.bm.login.accessor.shared.AbstractActivityDataAccessor;
import de.thb.fbi.maus.bm.login.model.TodoItem;

import java.util.ArrayList;

/**
 * @author Benedikt M.
 */
public class IntentContactListAccessor extends AbstractActivityDataAccessor implements TodoItemAccessor{
    private TodoItem item;


    @Override
    public TodoItem readItem() {
        if(this.item == null)
            this.item = (TodoItem) getActivity().getIntent().getSerializableExtra(Todos.ARG_ITEM_OBJECT);
        return this.item;
    }

    @Override
    public void writeItem() {
        Intent returnIntent = new Intent();

        returnIntent.putExtra(Todos.ARG_ITEM_OBJECT, this.item);
        getActivity().setResult(TodoDetail.RESPONSE_CONTACTS_SELECTED, returnIntent);
    }

    @Override
    public boolean hasItem() {
        return readItem() != null;
    }

    //no functionality here
    @Deprecated
    @Override
    public void createItem() {

    }

    //no functionality here
    @Deprecated
    @Override
    public void deleteItem() {

    }
}
