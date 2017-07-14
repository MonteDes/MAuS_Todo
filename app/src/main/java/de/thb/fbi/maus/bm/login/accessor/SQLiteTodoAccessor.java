package de.thb.fbi.maus.bm.login.accessor;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import de.thb.fbi.maus.bm.login.R;
import de.thb.fbi.maus.bm.login.accessor.shared.AbstractActivityDataAccessor;
import de.thb.fbi.maus.bm.login.model.TodoItem;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;

/**
 * @author Bene
 */
public class SQLiteTodoAccessor extends AbstractActivityDataAccessor implements TodoItemListAccessor {
    private static final String logger = SQLiteTodoAccessor.class.getName();

    ArrayList<TodoItem> itemList = new ArrayList<>();

    ArrayAdapter<TodoItem> adapter;

    private SQLiteDBHelper dbHelper;

    @Override
    public void addItem(TodoItem item) {
        this.dbHelper.addItemToDB(item);
    }

    @Override
    public ListAdapter getAdapter() {
        dbHelper = new SQLiteDBHelper(getActivity());
        dbHelper.prepareSQLiteDataBase();

        readItemsFromDB();

        ArrayAdapter<TodoItem> arrayAdapter = new ArrayAdapter<TodoItem>(getActivity(), R.layout.todo_layout){
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
                        itemList.get(position).getName(),
                        DateFormat.getDateInstance(DateFormat.MEDIUM).format(itemList.get(position).getDueDate())));

                if(itemList.get(position).getDueDate() < calendar.getTimeInMillis())
                    todoName.setTextColor(getActivity().getColor(R.color.redAttention));

                if(itemList.get(position).isDone()) {
                    todoName.setTextColor(getActivity().getColor(R.color.lightGrey));
                    done.setChecked(true);
                } else {
                    done.setChecked(false);
                }

                if(!itemList.get(position).isImportant()) {
                    importButton.setBackgroundResource(R.mipmap.favorite_false);
                } else {
                    importButton.setBackgroundResource(R.mipmap.favorite_true);
                }

                importButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });



                return retView;
            }
        };
    return arrayAdapter;
    }

    @Override
    public void updateItem(TodoItem item) {
        this.dbHelper.updateItemInDB(item);
    }

    @Override
    public void deleteItem(TodoItem item) {
        this.dbHelper.removeItemFromDB(item);
    }

    @Override
    public TodoItem getSelectedItem(int pos, int itemId) {
        return this.itemList.get(pos);
    }

    private void readItemsFromDB() {
        Cursor c = dbHelper.getCursor();

        c.moveToFirst();
        while(!c.isAfterLast()) {
            this.itemList.add(dbHelper.createItemFromCursor(c));
            c.moveToNext();
        }
    }

    @Override
    public void close() {
        dbHelper.close();
    }
}
