package de.thb.fbi.maus.bm.login.accessor;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import de.thb.fbi.maus.bm.login.R;
import de.thb.fbi.maus.bm.login.Todos;
import de.thb.fbi.maus.bm.login.accessor.shared.AbstractActivityDataAccessor;
import de.thb.fbi.maus.bm.login.model.Contact;
import de.thb.fbi.maus.bm.login.model.TodoItem;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;

/**
 * @author Benedikt M
 */
public class CursorAdapterTodoItemListAccessor extends AbstractActivityDataAccessor implements TodoItemListAccessor {
    private static final String logger = CursorAdapterTodoItemListAccessor.class.getName();

    private static final int LOADER_ID = 1;

    private SimpleCursorAdapter cursorAdapter;
    private LoaderManager.LoaderCallbacks<Cursor> loaderCallbacks;
    private SQLiteDBHelper dbHelper;


    private static abstract class SQLiteCursorLoader extends AsyncTaskLoader<Cursor> {
        private Cursor mCursor;

        protected abstract Cursor loadCursor();

        private SQLiteCursorLoader(Context context) {
            super(context);
        }

        @Override
        public Cursor loadInBackground() {
            final Cursor cursor = loadCursor();
            if (cursor != null)
                cursor.getCount();

            return cursor;
        }

        @Override
        public void onCanceled(Cursor data) {
            if (data != null && !data.isClosed())
                data.close();
        }

        @Override
        public void deliverResult(Cursor data) {
            Cursor oldCursor = mCursor;
            mCursor = data;

            if (isStarted())
                super.deliverResult(data);

            if (oldCursor != null && oldCursor != data && !oldCursor.isClosed())
                oldCursor.close();
        }

        @Override
        protected void onReset() {
            super.onReset();

            onStopLoading();
        }

        @Override
        protected void onStartLoading() {
            if (mCursor != null)
                deliverResult(mCursor);

            if (takeContentChanged() || mCursor == null)
                forceLoad();
        }

        @Override
        protected void onStopLoading() {
            cancelLoad();

            if (mCursor != null && !mCursor.isClosed())
                mCursor.close();
        }
    }

    private static class MyCursorLoader extends SQLiteCursorLoader {
        private SQLiteDBHelper dbHelper;

        private MyCursorLoader(Context context, SQLiteDBHelper dbHelper) {
            super(context);
            this.dbHelper = dbHelper;
        }

        @Override
        protected Cursor loadCursor() {
            return dbHelper.getCursor();
        }
    }

    private class MyLoaderCallbacks implements LoaderManager.LoaderCallbacks<Cursor> {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new MyCursorLoader(getActivity(), dbHelper);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (loader.getId() == LOADER_ID)
                cursorAdapter.swapCursor(data);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            cursorAdapter.swapCursor(null);
        }
    }

    @Override
    public void addItem(TodoItem item) {
        dbHelper.addItemToDB(item);
        getActivity().getLoaderManager().restartLoader(LOADER_ID, null, loaderCallbacks);
    }

    @Override
    public ListAdapter getAdapter() {
        this.dbHelper = new SQLiteDBHelper(getActivity());
        this.dbHelper.prepareSQLiteDataBase();

        LoaderManager loaderManager = getActivity().getLoaderManager();
        loaderCallbacks = new MyLoaderCallbacks();
        loaderManager.initLoader(LOADER_ID, null, loaderCallbacks);

        this.cursorAdapter = new SimpleCursorAdapter(getActivity(), R.layout.activity_todos, null,
                new String[]{SQLiteDBHelper.COL_NAME, SQLiteDBHelper.COL_IMPORTANT},
                new int[]{R.id.todo_name_mainView, R.id.important_button}) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                return inflater.inflate(R.layout.todo_layout, parent, false);
            }

            @Override
            public void bindView(View view, final Context context, final Cursor cursor) {
                GregorianCalendar calendar = new GregorianCalendar();
                TextView todoName = (TextView) view.findViewById(R.id.todo_name_mainView);
                CheckBox done = (CheckBox) view.findViewById(R.id.checkBox_done);
                Button importButt = (Button) view.findViewById(R.id.important_button);

                importButt.setTag(cursor.getPosition());
                done.setTag(cursor.getPosition());

                todoName.setText(String.format(context.getString(R.string.todo_nameANDduedate),
                        cursor.getString(cursor.getColumnIndex(SQLiteDBHelper.COL_NAME)),
                        DateFormat.getDateInstance(DateFormat.MEDIUM).format(cursor.getLong(cursor.getColumnIndex(SQLiteDBHelper.COL_DUEDATE)))));

                if (cursor.getLong(cursor.getColumnIndex(SQLiteDBHelper.COL_DUEDATE)) < calendar.getTimeInMillis())
                    todoName.setTextColor(context.getColor(R.color.redAttention));

                if (cursor.getInt(cursor.getColumnIndex(SQLiteDBHelper.COL_DONE)) != 0)
                    todoName.setTextColor(context.getColor(R.color.lightGrey));

                if (cursor.getInt(cursor.getColumnIndex(SQLiteDBHelper.COL_IMPORTANT)) == 0) {
                    importButt.setBackgroundResource(R.mipmap.favorite_false);
                } else {
                    importButt.setBackgroundResource(R.mipmap.favorite_true);
                }

                if (cursor.getInt(cursor.getColumnIndex(SQLiteDBHelper.COL_DONE)) == 0) {
                    done.setChecked(false);
                } else {
                    done.setChecked(true);
                }

                importButt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TodoItem item;
                        item = dbHelper.createItemFromCursor((Cursor) cursorAdapter.getItem((int) v.getTag()));
                        Log.i(logger, "Changing favorite Status of Item: " + (int) v.getTag());
                        if (!item.isImportant()) {

                            item.setImportant(true);
                            updateItem(item);

                            Log.i(logger, "Changed Status to: favorite");
                        } else {
                            item.setImportant(false);
                            updateItem(item);

                            Log.i(logger, "Changed Status to: not favorite");
                        }
                        notifyDataSetChanged();

                    }

                });

                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TodoItem item;
                        item = dbHelper.createItemFromCursor((Cursor) cursorAdapter.getItem((int) v.getTag()));
                        Log.i(logger, "Changing Done Status of Item: " + (int) v.getTag());
                        if (!item.isDone()) {

                            item.setDone(true);
                            updateItem(item);

                            Log.i(logger, "Changed Status to: done");
                        } else {
                            item.setDone(false);
                            updateItem(item);

                            Log.i(logger, "Changed Status to: not done");
                        }
                    }
                });
            }
        };

        return this.cursorAdapter;
    }

    @Override
    public void updateItem(TodoItem item) {
        dbHelper.updateItemInDB(item);
        getActivity().getLoaderManager().restartLoader(LOADER_ID, null, loaderCallbacks);
    }

    @Override
    public void deleteItem(TodoItem item) {
        dbHelper.removeItemFromDB(item);
        getActivity().getLoaderManager().restartLoader(LOADER_ID, null, loaderCallbacks);
    }

    @Override
    public TodoItem getSelectedItem(int pos, int itemId) {
        return dbHelper.createItemFromCursor((Cursor) this.cursorAdapter.getItem(pos));
    }

    @Override
    public void close() {
        dbHelper.close();
    }


    public ArrayAdapter<TodoItem> getArrayAdapter() {
        this.dbHelper = new SQLiteDBHelper(getActivity());
        this.dbHelper.prepareSQLiteDataBase();

        LoaderManager loaderManager = getActivity().getLoaderManager();
        loaderCallbacks = new MyLoaderCallbacks();
        loaderManager.initLoader(LOADER_ID, null, loaderCallbacks);

        this.cursorAdapter = new SimpleCursorAdapter(getActivity(), R.layout.activity_todos, null,
                new String[]{SQLiteDBHelper.COL_NAME, SQLiteDBHelper.COL_IMPORTANT},
                new int[]{R.id.todo_name_mainView, R.id.important_button});

        final ArrayList<TodoItem> list = new ArrayList<>();
        Cursor c = this.dbHelper.getCursor();

        c.moveToFirst();
        while(!c.isAfterLast()) {
            TodoItem item = dbHelper.createItemFromCursor(c);
            boolean check = false;

            for(TodoItem e: list) {

                if(item.getId() == e.getId()) {
                    check = true;
                    item.addContact(item.getAssociatedContacts().get(0));
                }

            }
            if(!check)
                list.add(item);
        }



        ArrayAdapter adapter = new ArrayAdapter<TodoItem>(getActivity(), R.id.list_view, list){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View retView = convertView;
                GregorianCalendar calendar = new GregorianCalendar();
                TextView todoName;
                CheckBox done;
                Button importButton;

                if (convertView == null) {
                    LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    retView = inflater.inflate(R.layout.todo_layout, parent, false);
                }

                todoName = (TextView) retView.findViewById(R.id.todo_name_mainView);
                done = (CheckBox) retView.findViewById(R.id.checkBox_done);
                importButton = (Button) retView.findViewById(R.id.important_button);

                todoName.setText(String.format(getContext().getString(R.string.todo_nameANDduedate),
                        list.get(position).getName(),
                        DateFormat.getDateInstance(DateFormat.MEDIUM).format(list.get(position).getDueDate())));

                if(list.get(position).getDueDate() < calendar.getTimeInMillis())
                    todoName.setTextColor(getActivity().getColor(R.color.redAttention));

                if(list.get(position).isDone()) {
                    todoName.setTextColor(getActivity().getColor(R.color.lightGrey));
                    done.setChecked(true);
                } else {
                    done.setChecked(false);
                }

                if(!list.get(position).isImportant()) {
                    importButton.setBackgroundResource(R.mipmap.favorite_false);
                } else {
                    importButton.setBackgroundResource(R.mipmap.favorite_true);
                }

                importButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                return  retView;
            }
        };

        adapter.setNotifyOnChange(true);
        return adapter;
    }
}
