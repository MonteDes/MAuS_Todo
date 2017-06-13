package de.thb.fbi.maus.bm.login.accessor;


import de.thb.fbi.maus.bm.login.model.TodoItem;

import javax.ws.rs.*;
import java.util.List;

/**
 * @author Benedikt M.
 */

@Path("/todoitems")
@Consumes({"application/json"})
@Produces({"application/json"})
public interface TodoItemCRUDAccessor {

    @GET
    public List<TodoItem> readAllItems();

    @POST
    public TodoItem createItem(TodoItem item);

    @PUT
    public TodoItem updateItem(TodoItem item);

    @DELETE
    @Path("/itemId")
    public boolean deleteItem(TodoItem item);
}
