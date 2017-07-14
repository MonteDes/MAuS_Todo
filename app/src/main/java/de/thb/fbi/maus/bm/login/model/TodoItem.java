package de.thb.fbi.maus.bm.login.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Benedikt M.
 */
public class TodoItem implements Serializable {

    private static final long serialVersionID = 0;

    private static int idCount = 0;

    private long id;
    private String name;
    private String desciption;
    private boolean done = false;
    private boolean important = false;
    private long dueDate;
    private ArrayList<Integer> associatedContacts;

    public TodoItem(String name, String description) {
        idCount++;

        this.setName(name);
        this.setDesciption(description);
        this.setImportant(false);
        this.setId(idCount);
        associatedContacts = new ArrayList<>();
    }

    public TodoItem(String name, String description, long dueDate) {
        idCount++;

        this.setName(name);
        this.setDesciption(description);
        this.setImportant(false);
        this.setDueDate(dueDate);
        this.setDone(false);
        this.setId(idCount);
        associatedContacts = new ArrayList<>();
    }

    public TodoItem(String name, String description, boolean important, long dueDate) {
        idCount++;

        this.setName(name);
        this.setDesciption(description);
        this.setImportant(important);
        this.setDueDate(dueDate);
        this.setDone(false);
        this.setId(idCount);
        associatedContacts = new ArrayList<>();
    }

    public TodoItem(String name, String description, boolean important, long dueDate, boolean done) {
        idCount++;

        this.setName(name);
        this.setDesciption(description);
        this.setDone(done);
        this.setDueDate(dueDate);
        this.setImportant(important);
        this.setId(idCount);
        associatedContacts = new ArrayList<>();
    }

    public TodoItem(String name, String description, boolean important, long dueDate, boolean done, ArrayList<Integer> associatedContacts){
        this(name, description, important, dueDate, done);
        this.associatedContacts = associatedContacts;
    }

    public TodoItem() {
        idCount++;

        this.setName("Default Todo");
        this.setDesciption("This is a default Todo");
        this.setImportant(false);
        this.setId(idCount);
        associatedContacts = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesciption() {
        return desciption;
    }

    public void setDesciption(String desciption) {
        this.desciption = desciption;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public boolean isImportant() {
        return important;
    }

    public void setImportant(boolean important) {
        this.important = important;
    }

    public long getDueDate() {
        return dueDate;
    }

    public void setDueDate(long dueDate) {
        this.dueDate = dueDate;
    }

    public ArrayList<Integer> getAssociatedContacts() {
        return associatedContacts;
    }

    public void setAssociatedContacts(ArrayList<Integer> associatedContacts) {
        this.associatedContacts = associatedContacts;
    }

    public void addContact(int c) {
        this.getAssociatedContacts().add(c);
    }

    @Override
    public String toString() {
        return "TodoItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", desciption='" + desciption + '\'' +
                ", done=" + done +
                ", important=" + important +
                ", dueDate=" + dueDate +
                '}';
    }
}
