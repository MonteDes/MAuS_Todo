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
    }

    public SQLiteRelationAccessor(SQLiteDBHelper dbHelper) {
        this.dbHelper = dbHelper;
        this.relations = new ArrayList<>();
        //readRelations();
    }

    @Override
    public void handleRelations(TodoItem item) {
        ArrayList<Long> itemContacts = item.getAssociatedContacts();
        long itemId = item.getId();



        //check for new relations
        for (long e: itemContacts) {
            boolean isOld = false;
            for(ContactRelation r : this.relations) {
                if(r.getTodoId() == itemId && r.getContactId() == e) {
                    isOld = true;
                    break;
                }
            }
            if(!isOld)
                this.dbHelper.addRelationToDB(new ContactRelation(e, itemId));
        }

        //check for deleted relations
        for(ContactRelation r : this.relations) {
            boolean isStillThere = false;
            for(long e : itemContacts) {
                if(r.getTodoId() == itemId && r.getContactId() == e) {
                    isStillThere = true;
                    break;
                }
            }
            if(!isStillThere)
                this.dbHelper.removeRelationFromDB(new ContactRelation(r.getContactId(), r.getTodoId()));
        }
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

    public void readRelations() {


        Cursor c = this.dbHelper.getRelationCursor();
        this.relations.clear();

        c.moveToFirst();
        while(!c.isAfterLast()) {
            this.relations.add(this.dbHelper.createRelationFromCursor(c));
            c.moveToNext();
        }
    }

    public void init() {
        this.dbHelper = new SQLiteDBHelper(getActivity());
        this.dbHelper.prepareSQLiteDataBase();
        this.relations = new ArrayList<>();
    }
    @Override
    public void close() {
        this.dbHelper.close();
    }
}
