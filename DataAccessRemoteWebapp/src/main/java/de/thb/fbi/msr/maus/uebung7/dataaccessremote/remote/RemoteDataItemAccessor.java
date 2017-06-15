package de.thb.fbi.msr.maus.uebung7.dataaccessremote.remote;

import java.util.ArrayList;
import java.util.List;

import com.sun.xml.internal.bind.v2.TODO;
import de.thb.fbi.msr.maus.uebung7.dataaccessremote.model.TodoItem;
import de.thb.fbi.msr.maus.uebung7.dataaccessremote.model.TodoItemCRUDAccessor;
import org.apache.log4j.Logger;

public class RemoteDataItemAccessor implements TodoItemCRUDAccessor {

	protected static Logger logger = Logger
			.getLogger(RemoteDataItemAccessor.class);

	/**
	 * the list of data items, note that the list is *static* as for each client
	 * request a new instance of this class will be created!
	 */
	private static List<TodoItem> itemlist = new ArrayList<TodoItem>();

	/**
	 * we assign the ids here
	 */
	private static long idCount = 0;
	
	@Override
	public List<TodoItem> readAllItems() {
		logger.info("readAllItems(): " + itemlist);

		return itemlist;
	}

	@Override
	public TodoItem createItem(TodoItem item) {
		logger.info("createItem(): " + item);
		item.setId(idCount++);

		itemlist.add(item);
		return item;
	}

	@Override
	public boolean deleteItem(final long itemId) {
		logger.info("deleteItem(): " + itemId);

		boolean removed = itemlist.remove(new TodoItem() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 71193783355593985L;

			@Override
			public long getId() {
				return itemId;
			}
		});

		return removed;
	}

	@Override
	public TodoItem updateItem(TodoItem item) {
		logger.info("updateItem(): " + item);

		return itemlist.get(itemlist.indexOf(item)).updateFrom(item);
	}
}
