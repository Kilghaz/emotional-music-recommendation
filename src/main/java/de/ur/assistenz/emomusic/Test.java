package de.ur.assistenz.emomusic;

public class Test {

    public static void main(String[] args) {
        SettingsManager settings = SettingsManager.getInstance();
        try {
            settings.saveText("test", "12345");
            String test = settings.loadText("test");
            System.out.println(test);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

}
