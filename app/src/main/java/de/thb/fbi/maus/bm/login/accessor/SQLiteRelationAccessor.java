package de.thb.fbi.maus.bm.login.accessor;

import android.database.Cursor;
import android.widget.ListAdapter;
import de.thb.fbi.maus.bm.login.accessor.shared.AbstractActivityDataAccessor;
import de.thb.fbi.maus.bm.login.model.ContactRelation;
import de.thb.fbi.maus.bm.login.model.TodoItem;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bene
 */
public class SQLiteRelationAccessor extends AbstractActivityDataAccessor implements ContactRelationListAccessor {
    private final String logger = SQLiteRelationAccessor.class.getName();

    private SQLiteDBHelper dbHelper;
    private List<ContactRelation>relations;

    public SQLiteRelationAccessor() {
        this.dbHelper = new SQLiteDBHelper(getActivity());
        this.dbHelper.prepareSQLiteDataBase();
        this.relations = new ArrayList<>();
    }

    public SQLiteRelationAccessor(SQLiteDBHelper dbHelper) {
        this.dbHelper = dbHelper;
        this.relations = new ArrayList<>();
    }

    @Override
    public void addRelation(ContactRelation relation) {
        this.dbHelper.addRelationToDB(relation);
    }

    //not needed
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

    public TodoItem makeItemContactReady(TodoItem item) {
        TodoItem retItem = item;

        for(ContactRelation r: this.relations) {
            if(r.getTodoId() == item.getId())
                item.addAssociatedContact(r.getContactId());
        }
        return retItem;
    }

    private void readRelations() {
        Cursor c = this.dbHelper.getRelationCursor();

        c.moveToFirst();
        while(!c.isAfterLast()) {
            this.relations.add(this.dbHelper.createRelationFromCursor(c));
        }
    }

    @Override
    public void close() {
        this.dbHelper.close();
    }
}
