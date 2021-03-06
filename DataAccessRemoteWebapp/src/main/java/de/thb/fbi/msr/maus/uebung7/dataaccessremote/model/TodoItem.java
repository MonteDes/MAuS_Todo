package de.thb.fbi.msr.maus.uebung7.dataaccessremote.model;

import com.sun.xml.internal.bind.v2.TODO;

import java.io.Serializable;

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

    public TodoItem(String name, String desciption) {
        idCount++;

        this.setName(name);
        this.setDesciption(desciption);
        this.setImportant(false);
        this.setId(idCount);
    }

    public TodoItem(String name, String desciption, long dueDate) {
        idCount++;

        this.setName(name);
        this.setDesciption(desciption);
        this.setImportant(false);
        this.setDueDate(dueDate);
        this.setDone(false);
        this.setId(idCount);
    }

    public TodoItem(String name, String desciption, boolean important, long dueDate) {
        idCount++;

        this.setName(name);
        this.setDesciption(desciption);
        this.setImportant(important);
        this.setDueDate(dueDate);
        this.setDone(false);
        this.setId(idCount);
    }

    public TodoItem(String name, String desciption, boolean important, long dueDate, boolean done) {
        idCount++;

        this.setName(name);
        this.setDesciption(desciption);
        this.setDone(done);
        this.setDueDate(dueDate);
        this.setImportant(important);
        this.setId(idCount);
    }

    public TodoItem() {
        idCount++;

        this.setName("Default Todo");
        this.setDesciption("This is a default Todo");
        this.setImportant(false);
        this.setId(idCount);
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

    public TodoItem updateFrom(TodoItem item) {
        this.setName(item.getName());
        this.setDesciption(item.getDesciption());
        this.setDueDate(item.getDueDate());
        this.setDone(item.isDone());
        this.setImportant(item.isImportant());

        return this;
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
