package sample.DataModel;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DataHandler {
     private static DataHandler instance = new DataHandler();
    private ObservableList<Song> Songs;
    private String musicList="MusicList.txt";

    private DataHandler() {
        this.Songs = FXCollections.observableArrayList();
    }
    public static DataHandler getInstance(){
        return instance;
    }
    public ObservableList<Song> getSongs() {
        return Songs;
    }
    public boolean addSong(Song song){
       return Songs.add(song);
    }


     public boolean addSongs(List<Song> song){
        return Songs.addAll(song);
     }

    public boolean removeSong(Song song){
        return Songs.remove(song);
    }
    public void loadSongs() throws IOException {
        Path path= Paths.get(musicList);
        BufferedReader br= Files.newBufferedReader(path);
        try{

            String input;
            while((input=br.readLine())!=null)
            {
                String[] songItems=input.split("\t");

                String sname=songItems[0];
                String spath=songItems[1];
                Song song=new Song(sname,spath);
               // System.out.println(sname+" "+spath);
                Songs.add(song);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if(br!=null)
                br.close();
        }
    }
    public void storeSongs() throws IOException {
        Path path= Paths.get(musicList);
        BufferedWriter br= Files.newBufferedWriter(path);
        try{
                Iterator<Song> iterator=Songs.iterator();
                while(iterator.hasNext()){
                    Song song=iterator.next();
                    br.write(String.format("%s\t%s\t",song.getName(),song.getPath()));
                    br.newLine();
                }

        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if(br!=null)
                br.close();
        }
    }
}
