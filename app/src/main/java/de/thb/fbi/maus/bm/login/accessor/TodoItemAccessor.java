package de.thb.fbi.maus.bm.login.accessor;

import de.thb.fbi.maus.bm.login.model.TodoItem;

/**
 * @author Benedikt M.
 *
 */
public interface TodoItemAccessor {

    public TodoItem readItem();
    public void writeItem();
    public boolean hasItem();
    public void createItem();
    public void deleteItem();
}
