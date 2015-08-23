package de.ur.assistenz.emomusic;

import de.hijacksoft.oosql.DerbyAdapter;
import de.hijacksoft.oosql.Where;
import de.ur.assistenz.emomusic.sql.Setting;

import java.util.List;

public class SettingsManager {

    private static final String TABLE_SETTINGS = "settings";
    private static final String COLUMN_VALUE = "value";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_TYPE = "type";

    private static final String TYPE_NUMBER = "Number";
    private static final String TYPE_TEXT = "Text";
    private static final String TYPE_BOOLEAN = "Boolean";

    private static SettingsManager instance = null;

    private DerbyAdapter derby = null;

    private SettingsManager() {
        instance = this;
        createDatabaseConnection();
        initializeDatabase();
    }

    public static synchronized SettingsManager getInstance() {
        return instance == null ? new SettingsManager() : instance;
    }

    private void createDatabaseConnection() {
        this.derby = DatabaseAdapterProvider.getInstance().getAdapter();
    }

    private void save(Setting setting) {
        if(!doesSettingExist(setting.getName())) {
            this.derby.insert(setting);
        }
        else {
            this.derby.update(setting);
        }
    }

    private Setting load(String name) {
        List<Setting> settings = derby.select(Setting.class, Where.equals(COLUMN_NAME, name));
        if(settings != null && settings.size() > 0){
            return settings.get(0);
        }
        else return null;
    }

    private boolean doesSettingExist(String name){
        return load(name) != null;
    }

    private void initializeDatabase() {
        if(!derby.doesTableExist(TABLE_SETTINGS)) {
            derby.createTable(Setting.class);
        }
    }

    public String loadText(String name) {
        Setting setting = load(name);
        if(setting == null) return null;
        return setting.getValue();
    }

    public Double loadNumber(String name) {
        Setting setting = load(name);
        if(setting == null) return null;
        return Double.parseDouble(setting.getValue());
    }

    public Boolean loadBoolean(String name) {
        Setting setting = load(name);
        if(setting == null) return null;
        return Boolean.parseBoolean(setting.getValue());
    }

    public void saveText(String name, String value) {
        save(new Setting(name, value, TYPE_TEXT));
    }

    public void saveNumber(String name, Double value) {
        save(new Setting(name, value.toString(), TYPE_NUMBER));
    }

    public void saveBoolean(String name, Boolean value) {
        save(new Setting(name, value.toString(), TYPE_BOOLEAN));
    }

}
