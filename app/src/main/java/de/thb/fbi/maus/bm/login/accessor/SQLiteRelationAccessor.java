package de.thb.fbi.maus.bm.login.accessor;

import android.widget.ListAdapter;
import de.thb.fbi.maus.bm.login.accessor.shared.AbstractActivityDataAccessor;
import de.thb.fbi.maus.bm.login.model.ContactRelation;
import de.thb.fbi.maus.bm.login.model.TodoItem;

/**
 * @author Bene
 */
public class SQLiteRelationAccessor extends AbstractActivityDataAccessor implements ContactRelationListAccessor {
    private final String logger = SQLiteRelationAccessor.class.getName();

    SQLiteDBHelper dbHelper;

    public SQLiteRelationAccessor() {
        this.dbHelper = new SQLiteDBHelper(getActivity());
        this.dbHelper.prepareSQLiteDataBase();
    }

    public SQLiteRelationAccessor(SQLiteDBHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    @Override
    public void addRelation(ContactRelation relation) {
        this.dbHelper.addRelationToDB(relation);
    }

    @Override
    public ListAdapter getAdapter() {
        return null;
    }

    @Override
    public void deleteRelation(ContactRelation relation) {
        this.dbHelper.removeRelationFromDB(relation);
    }

    @Override
    public ContactRelation getSelectedRelation(int pos, int itemId) {
        return null;
    }

    public TodoItem makeItemContactReady() {

        return null;
    }

    @Override
    public void close() {
        this.dbHelper.close();
    }
}
