package de.thb.fbi.maus.bm.login.accessor;

import android.widget.ListAdapter;
import de.thb.fbi.maus.bm.login.model.TodoItem;

/**
 * Created by Bene on 04.05.2017.
 */
public interface TodoItemListAccessor {

    public void addItem(TodoItem item);
    public ListAdapter getAdapter();
    public void updateItem(TodoItem item);
    public void deleteItem(TodoItem item);
    public TodoItem getSelectedItem(int pos, int itemId);
    public void close();

}
