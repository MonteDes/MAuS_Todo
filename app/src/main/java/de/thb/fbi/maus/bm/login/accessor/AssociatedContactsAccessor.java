package de.thb.fbi.maus.bm.login.accessor;

import java.util.ArrayList;

/**
 * @author Benedikt M.
 */
public interface AssociatedContactsAccessor {

    public ArrayList<Long> readContacts();
    public void writeContacts();
    public boolean hasContacts();
    public void createContacts();
    public void deleteContacts();
}
