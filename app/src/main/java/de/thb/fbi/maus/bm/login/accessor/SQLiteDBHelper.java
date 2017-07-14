package de.thb.fbi.maus.bm.login.accessor;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MergeCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;
import de.thb.fbi.maus.bm.login.model.ContactRelation;
import de.thb.fbi.maus.bm.login.model.TodoItem;

import java.util.Locale;

/**
 * @author Benedikt M.
 */
public class SQLiteDBHelper {

    private final Activity mActivity;
    public static int ordering_method = 1;

    private static final String logger = SQLiteDBHelper.class.getName();

    private static final String DBNAME = "todoItems.db";
    private static final int INITIAL_DBVERSION = 0;
    private static final String TABNAME_TODOITEMS = "todoItems";
    private static final String TABNAME_REL_CONTACT = "contactRelation";

    protected static final String COL_ID = "_id";
    protected static final String COL_IMPORTANT = "important";
    protected static final String COL_NAME = "name";
    protected static final String COL_DESCRIPTION = "description";
    protected static final String COL_DUEDATE = "duedate";
    protected static final String COL_DONE = "done";
    protected static final String COL_REL_CONTACT = "_cid";
    protected static final String COL_REL_TODO = "_tid";

    private static final String TODO_TABLE_CREATION_QUERY = "CREATE TABLE " + TABNAME_TODOITEMS + " (" +
            COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            COL_IMPORTANT + " TINYINT,\n" +
            COL_NAME + " TEXT,\n" +
            COL_DESCRIPTION + " TEXT,\n" +
            COL_DUEDATE + " LONG,\n" +
            COL_DONE + " TINYINT" +
            ");";
    private static final String REL_TABLE_CREATION_QUERY = "CREATE TABLE " + TABNAME_REL_CONTACT + " (" +
            COL_REL_TODO + "INTEGER PRIMARY KEY,\n" +
            COL_REL_CONTACT + "INTEGER PRIMARY KEY,\n" +
            "PRIMARY KEY(" + COL_REL_TODO + "," + COL_REL_CONTACT + ")" +
            "FOREIGN KEY (" + COL_REL_TODO + ") REFERENCES " + TABNAME_TODOITEMS + "(" + COL_ID + ")" +
            ");";
    private final String WHERE_IDENTIFY_ITEM = COL_ID + "=?";


    SQLiteDatabase db;

    public SQLiteDBHelper(Activity activity) {
        super();
        this.mActivity = activity;
    }

    protected void prepareSQLiteDataBase() {
        this.db = mActivity.openOrCreateDatabase(DBNAME, SQLiteDatabase.CREATE_IF_NECESSARY, null);

        if(this.db.getVersion() == INITIAL_DBVERSION) {
            Log.i(logger, "DB Created. Creating tables...");
            db.setLocale(Locale.getDefault());
            db.setVersion(INITIAL_DBVERSION + 1);
            db.execSQL(TODO_TABLE_CREATION_QUERY);
            db.execSQL(REL_TABLE_CREATION_QUERY);
            Log.i(logger, "Tables created");
        } else {
            Log.i(logger, "DB already exists.");
        }
    }

    private Activity getActivity() {
        return mActivity;
    }

    public static ContentValues createDBTodoItem(TodoItem item) {
        ContentValues insertItem = new ContentValues();
        insertItem.put(COL_NAME, item.getName());
        insertItem.put(COL_IMPORTANT, item.isImportant());
        insertItem.put(COL_DESCRIPTION, item.getDesciption());
        insertItem.put(COL_DUEDATE, item.getDueDate());
        insertItem.put(COL_DONE, item.isDone());

        return insertItem;
    }

    public static ContentValues createDBContactRelation(ContactRelation relation) {
        ContentValues insertItem = new ContentValues();
        insertItem.put(COL_REL_CONTACT, relation.getContactId());
        insertItem.put(COL_REL_TODO, relation.getTodoId());

        return insertItem;
    }

    public Cursor getCursor(){
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(TABNAME_TODOITEMS);

        String[] asColumnsToReturn =  {COL_ID, COL_IMPORTANT, COL_NAME, COL_DESCRIPTION, COL_DUEDATE, COL_DONE};
        String ordering;
        if(ordering_method == 0) {
            ordering = COL_DONE + " ASC, " + COL_IMPORTANT + " DESC, " + COL_DUEDATE + " ASC";
        } else {
            ordering = COL_DONE + " ASC, " + COL_DUEDATE + " ASC, " + COL_IMPORTANT + " DESC";
        }

        Cursor cursor = queryBuilder.query(this.db, asColumnsToReturn, null, null, null, null, ordering);

        return cursor;
    }

    public TodoItem createItemFromCursor(Cursor c) {
        int bool;
        TodoItem item = new TodoItem();

        item.setId(c.getLong(c.getColumnIndex(COL_ID)));
        item.setName(c.getString(c.getColumnIndex(COL_NAME)));
        item.setDesciption(c.getString(c.getColumnIndex(COL_DESCRIPTION)));
        item.setDueDate(c.getLong(c.getColumnIndex(COL_DUEDATE)));

        bool = c.getInt(c.getColumnIndex(COL_IMPORTANT));
        item.setImportant(bool != 0);
        bool = c.getInt(c.getColumnIndex(COL_DONE));
        item.setDone(bool != 0);



        return item;
    }

    public ContactRelation createRelationFromCursor(Cursor c) {
        return new ContactRelation(c.getLong(c.getColumnIndex(COL_REL_CONTACT)),
                c.getLong(c.getColumnIndex(COL_REL_TODO)));
    }

    public void addItemToDB(TodoItem item) {
        ContentValues insertItem = SQLiteDBHelper.createDBTodoItem(item);
        long itemId = this.db.insert(TABNAME_TODOITEMS, null, insertItem);

        item.setId(itemId);
    }

    public void removeItemFromDB(TodoItem item) {
        Log.i(logger, "Removing item from DB. (Item: " + item + ")");
        this.db.delete(TABNAME_TODOITEMS, WHERE_IDENTIFY_ITEM, new String[] {String.valueOf(item.getId())});
        Log.i(logger, "Item removed from DB.");
    }

    public void updateItemInDB(TodoItem item) {
        Log.i(logger, "Updating item with id: " + item.getId());
        this.db.update(TABNAME_TODOITEMS, createDBTodoItem(item), WHERE_IDENTIFY_ITEM, new String [] {String.valueOf(item.getId())});
        Log.i(logger, "Updated item in database");
    }

    public void addRelationToDB(ContactRelation relation) {
        Log.i(logger, "Adding Contact Relation to DB");
        ContentValues insertRelation = SQLiteDBHelper.createDBContactRelation(relation);
        this.db.insert(TABNAME_REL_CONTACT, null, insertRelation);
        Log.i(logger, "Added Relation to DB");
    }

    public void removeRelationFromDB(ContactRelation relation) {
        Log.i(logger, "Deleting Relation (CID: " + relation.getContactId() + "\tItem_ID: " + relation.getTodoId() + ") in DB");
        this.db.delete(TABNAME_REL_CONTACT, WHERE_IDENTIFY_ITEM,
                new String[] {String.valueOf(relation.getContactId()), String.valueOf(relation.getTodoId())});
        Log.i(logger, "Deleted Relation from DB");
    }

    public void updateRelationInDB(ContactRelation relation) {
        Log.i(logger, "Updating Relation (CID: " + relation.getContactId() + "\tItem_ID: " + relation.getTodoId() + ") in DB");
        this.db.update(TABNAME_REL_CONTACT, createDBContactRelation(relation), WHERE_IDENTIFY_ITEM,
                new String[] {String.valueOf(relation.getContactId()), String.valueOf(relation.getTodoId())});
        Log.i(logger, "Updated Relation in DB");
    }
    public void close() {
        this.db.close();
        Log.i(logger, "DB has been closed");
    }
}
