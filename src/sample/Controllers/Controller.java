package sample.Controllers;


import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import javafx.animation.*;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.effect.Reflection;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import javafx.util.Duration;
import sample.DataModel.DataHandler;
import sample.DataModel.Song;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class Controller {
    @FXML
    VBox mainWindow;
    @FXML
    JFXButton playbtn;
    @FXML
    JFXButton pausebtn;
    @FXML
    JFXButton stopbtn;
    @FXML
    JFXButton addbtn;
    @FXML
    Slider songSlider;
    @FXML
    Slider volumeSlider;
    @FXML
    JFXListView<Song> SongListView;
    @FXML
    StackPane discStackPane;
    @FXML
    Label songNamelbl;
    @FXML
    Label timeLabel;
    @FXML
    ContextMenu contextMenu;
    MediaPlayer player;
    ImageView disc=new ImageView(new Image(new FileInputStream("src\\sample\\resources\\vinyl.png")));
    RotateTransition rotateTransition=new RotateTransition();
    ScaleTransition scaleTransition=new ScaleTransition();
    TranslateTransition translateTransition=new TranslateTransition();
    ParallelTransition parallelTransition=new ParallelTransition(scaleTransition,translateTransition,rotateTransition);
    boolean isPlaying=false,isPaused=false,isDragged=false;
    Duration wherePaused=new Duration(0);

    public Controller() throws FileNotFoundException {
    }

    public void AddMusic(ActionEvent actionEvent) {
        FileChooser fileChooser=new FileChooser();
        FileChooser.ExtensionFilter extensionFilter=new FileChooser.ExtensionFilter(" Music (*.mp3)","*.mp3");
        fileChooser.getExtensionFilters().add(extensionFilter);
        File file=fileChooser.showOpenDialog(mainWindow.getScene().getWindow());
        if(file!=null) {
            String name=file.getName().substring(0,file.getName().indexOf('.'));
            String path=file.getPath();
            DataHandler.getInstance().addSong(new Song(name,path));
        }
    }


    public void stopSong(ActionEvent actionEvent) {
        if(player!=null)
            player.stop();
        isPlaying=false;
        isPaused=false;
        parallelTransition.pause();
    }

    public void playSong() {

        String song=(new File(SongListView.getSelectionModel().getSelectedItem().getPath())).toURI().toString();
        if(isPlaying)
        if(!new File(SongListView.getSelectionModel().getSelectedItem().getPath()).toURI().toString().equals(player.getMedia().getSource()))
        {
            player.stop();
            isPlaying=false;
            isPaused=false;
            wherePaused=new Duration(0);
        }
        Media media=new Media(song);
        songNamelbl.setText(SongListView.getSelectionModel().getSelectedItem().getName());
         if(!isPlaying){

             player=new MediaPlayer(media);



             if(isPaused)
             {
                 System.out.println(songSlider.getValue());
                 player.seek(Duration.seconds(songSlider.getValue()));
                 System.out.println(wherePaused.toSeconds());
                 System.out.println(player.getCurrentTime()+"");
                 isDragged=false;
                 isPlaying=true;
                 isPaused=false;
             }

         }

        isPlaying=true;
        isPaused=false;
        player.setOnEndOfMedia(() -> {
            SongListView.getSelectionModel().selectNext();
            songSlider.setValue(0);
            songNamelbl.setText(SongListView.getSelectionModel().getSelectedItem().getName());
            isPlaying=false;
            isPaused=false;
            parallelTransition.pause();

        });
        player.currentTimeProperty().addListener((observableValue, duration, t1) -> {
            songSlider.setMax(player.getTotalDuration().toSeconds());
            songSlider.setValue(t1.toSeconds());
            String s=((t1.toMinutes()/60+"").substring(0,2)+""+(t1.toSeconds()/60+"").substring(0,2)+""+(t1.toSeconds()%60+"").substring(0,2));
            timeLabel.setText(s.replace("."," : "));
        });
        songSlider.setOnDragDetected((EventHandler<Event>) event -> {
            player.pause();
            isPaused=true;
            isPlaying=false;
            isDragged=true;
        });

        songSlider.setOnMouseReleased((EventHandler<Event>) event -> {
            if(isDragged){ player.seek(Duration.seconds(songSlider.getValue()));player.play();isDragged=false;isPlaying=true;isPaused=false; } });
        player.setOnEndOfMedia(() -> nextSong(new ActionEvent()));
        rotateTransition.setDuration(Duration.millis(3000));rotateTransition.setByAngle(720);rotateTransition.setNode(disc);rotateTransition.setCycleCount(Animation.INDEFINITE);

        translateTransition.setByY(140);translateTransition.setFromY(0);translateTransition.setAutoReverse(true);
        translateTransition.setCycleCount(Animation.INDEFINITE);translateTransition.setNode(songNamelbl);translateTransition.setDuration(Duration.millis(2000));

        parallelTransition.play();

        player.setAudioSpectrumNumBands(1);
        player.setAudioSpectrumInterval(0.05);
        player.volumeProperty().bindBidirectional(volumeSlider.valueProperty());player.play();
    }

    public void pauseSong(ActionEvent actionEvent) {
        if(isPlaying){
                player.pause();
                isDragged=true;
        }
       parallelTransition.pause();
    }

    public void initialize() throws FileNotFoundException {
        contextMenu=new ContextMenu();
        MenuItem addSongs=new MenuItem("Add Multiple");
        contextMenu.getItems().add(addSongs);
        addSongs.setOnAction(actionEvent -> {
            FileChooser fileChooser=new FileChooser();
            FileChooser.ExtensionFilter extensionFilter=new FileChooser.ExtensionFilter(" Music (*.mp3)","*.mp3");
            fileChooser.getExtensionFilters().add(extensionFilter);
            List<File> file=fileChooser.showOpenMultipleDialog(mainWindow.getScene().getWindow());
            List<Song> songs=new ArrayList<>();
            for(File s:file)
            {
                String name=s.getName().substring(0,s.getName().indexOf('.'));
                String path=s.getPath();
                songs.add(new Song(name,path));
            }
            DataHandler.getInstance().addSongs(songs);
        });
        volumeSlider.setMax(1);
        volumeSlider.setValue(1);
        songNamelbl.setEffect(new Reflection());
        addbtn.setContextMenu(contextMenu);
        disc.setFitHeight(100);
        disc.setFitWidth(100);
        discStackPane.getChildren().add(disc);
        playbtn.setGraphic(new ImageView(new Image(new FileInputStream("src\\sample\\resources\\play.png"))));

        ObservableList list= DataHandler.getInstance().getSongs();
        SongListView.setItems(list);

        CellFactoryinit();//modify list cells

        SongListView.getSelectionModel().selectFirst();
    }



    private void CellFactoryinit(){
        ContextMenu ScontextMenu=new ContextMenu();
        MenuItem deleteItem=new MenuItem("delete");
        deleteItem.setOnAction(actionEvent -> DataHandler.getInstance().removeSong(SongListView.getSelectionModel().getSelectedItem()));
        ScontextMenu.getItems().add(deleteItem);

        SongListView.setCellFactory(new Callback<ListView<Song>, ListCell<Song>>() {
            @Override
            public ListCell<Song> call(ListView<Song> songListView) {
                ListCell<Song> cell = new ListCell<Song>() {
                    @Override
                    protected void updateItem(Song song, boolean empty) {
                        super.updateItem(song, empty);
                        if (empty)
                            setText(null);
                        else {
                            setText(song.getName());
                        }
                        setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, new Insets(2, 2, 2, 2))));

                        setPrefSize(0, 30);
                        setOnMouseClicked(mouseEvent -> {
                            if(mouseEvent.getClickCount()>=2)
                              playSong();
                        });
                    }
                };
                cell.emptyProperty().addListener((observableValue, aBoolean, t1) -> {
                    if(t1)
                        cell.setContextMenu(null);
                    else
                        cell.setContextMenu(ScontextMenu);
                });
                return cell;
            }
        });
    }

    public void nextSong(ActionEvent actionEvent) {
        if(SongListView.getSelectionModel().getSelectedIndex()==SongListView.getItems().size()-1)
            SongListView.getSelectionModel().selectFirst();
        else
          SongListView.getSelectionModel().selectNext();

        playSong();
    }

    public void previousSong(ActionEvent actionEvent) {
        if(SongListView.getSelectionModel().getSelectedIndex()==0)
            SongListView.getSelectionModel().selectLast();
        else
        SongListView.getSelectionModel().selectPrevious();
        playSong();
    }
}
