package de.thb.fbi.maus.bm.login.accessor;

import android.widget.ListAdapter;
import de.thb.fbi.maus.bm.login.TodoDetail;
import de.thb.fbi.maus.bm.login.model.ContactRelation;
import de.thb.fbi.maus.bm.login.model.TodoItem;

/**
 * @author Bene
 */
public interface ContactRelationListAccessor {

    public void handleRelations(TodoItem item);
    public ContactRelation getSelectedRelation(int pos, int itemId);
    public void close();
}
