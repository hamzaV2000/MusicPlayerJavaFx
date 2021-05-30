package sample.DataModel;

import javafx.beans.property.SimpleStringProperty;

public class Song {
    private String Name;
    private String Path;
    public Song(String Name,String Path){
        this.Name=(Name);
        this.Path=(Path);
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPath() {
        return Path;
    }

    public void setPath(String path) {
        Path = path;
    }
}
