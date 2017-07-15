package de.thb.fbi.maus.bm.login.model;

/**
 * @author Benedikt M.
 */
public class ContactRelation {
    private long contactId;
    private long todoId;

    public ContactRelation(long contactId, long todoId) {
        this.contactId = contactId;
        this.todoId = todoId;
    }

    public long getContactId() {
        return contactId;
    }

    public void setContactId(long contactId) {
        this.contactId = contactId;
    }

    public long getTodoId() {
        return todoId;
    }

    public void setTodoId(long todoId) {
        this.todoId = todoId;
    }
}
