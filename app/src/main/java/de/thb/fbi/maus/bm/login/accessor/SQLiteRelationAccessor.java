package de.thb.fbi.maus.bm.login.accessor;

import android.widget.ListAdapter;
import de.thb.fbi.maus.bm.login.accessor.shared.AbstractActivityDataAccessor;
import de.thb.fbi.maus.bm.login.model.ContactRelation;

/**
 * @author Bene
 */
public class SQLiteRelationAccessor extends AbstractActivityDataAccessor implements ContactRelationListAccessor {
    @Override
    public void addRelation(ContactRelation relation) {

    }

    @Override
    public ListAdapter getAdapter() {
        return null;
    }

    @Override
    public void deleteRelation(ContactRelation relation) {

    }

    @Override
    public ContactRelation getSelectedRelation(int pos, int itemId) {
        return null;
    }

    @Override
    public void close() {

    }
}
