package de.thb.fbi.msr.maus.uebung7.dataaccessremote.model;


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
    @Path("/{itemId}")
    public boolean deleteItem(@PathParam("itemId") long itemId);
}