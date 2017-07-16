
    package de.thb.fbi.maus.bm.login.accessor;

import de.thb.fbi.maus.bm.login.model.TodoItem;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.client.core.executors.ApacheHttpClient4Executor;

import java.util.List;

    /**
     * @author Benedikt M
     *
     */
    public class CRUDAccessor implements TodoItemCRUDAccessor {



        private TodoItemCRUDAccessor client;
        private String url;

        public CRUDAccessor (String url) {
            this.url = url;
        }

        @Override
        public List<TodoItem> readAllItems() {
            return client.readAllItems();
        }

        @Override
        public TodoItem readItem(long itemId) {
            return client.readItem(itemId);
        }

        @Override
        public TodoItem createItem(TodoItem item) {
            return client.createItem(item);
        }

        @Override
        public TodoItem updateItem(TodoItem item) {
            return client.updateItem(item);
        }

        @Override
        public boolean deleteItem(long itemId) {
            return client.deleteItem(itemId);
        }

        public TodoItemCRUDAccessor init() {
            return this.client = ProxyFactory.create(TodoItemCRUDAccessor.class, url, new ApacheHttpClient4Executor());
         }

         public static String getBaseURL() {
            return "http://10.0.2.2:8080/DataAccessRemoteWebapp-1.0-SNAPSHOT/rest";
         }

         public TodoItemCRUDAccessor getClient() {
            return client;
        }
    }

