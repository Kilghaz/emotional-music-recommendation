package de.ur.assistenz.emomusic;

import de.hijacksoft.oosql.DerbyAdapter;

public class DatabaseAdapterProvider {

    private static final String DATABASE_LOCATION = ".database";

    private static DatabaseAdapterProvider instance = null;
    private DerbyAdapter derby;

    private DatabaseAdapterProvider(){
        derby = new DerbyAdapter(DATABASE_LOCATION);
        instance = this;
    }

    public static synchronized DatabaseAdapterProvider getInstance(){
        return instance == null ? new DatabaseAdapterProvider() : instance;
    }

    public DerbyAdapter getAdapter() {
        return this.derby;
    }

}
