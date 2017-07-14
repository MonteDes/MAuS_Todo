package de.thb.fbi.maus.bm.login.model;

/**
 * @author Benedikt M.
 */
public class Contact {
    private long id;
    private String name;

    public Contact(int id, String name) {
        this.id = id;
        this.name = name;
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
}
