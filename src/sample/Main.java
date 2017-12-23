package sample;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;
import javafx.event.*;
import javafx.scene.input.*;
import javafx.stage.WindowEvent;


public class Main extends Application {
 MediaPlayer player;
 ObservableList<String> observableList;
    @Override
    public void start(Stage primaryStage) throws Exception{
        File directory;
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        GridPane gridLayout = new GridPane();
        gridLayout.setAlignment(Pos.CENTER);
        gridLayout.setHgap(10);
        gridLayout.setVgap(10);
        gridLayout.setPadding(new Insets(4,4,4,4));
        Scene panel = new Scene(gridLayout,260,200);
        Button play = new Button();
        Image icon = new Image(getClass().getResourceAsStream("Play24.gif"));
        ImageView iconView = new ImageView();
        iconView.setImage(icon);
        play.setGraphic(iconView);
        HBox hbButton =  new HBox(10);
        hbButton.setAlignment(Pos.BOTTOM_LEFT);
        hbButton.getChildren().add(play);
        gridLayout.add(hbButton,1,4);
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directory = directoryChooser.showDialog(primaryStage);
        observableList = FXCollections.observableList(makePlayList(directory));

        ListView<String> songs = new ListView<String>(observableList);
        gridLayout.add(songs,1,1);
        //  Media aSong; // = new Media(directory.toURI()+"/"+observableList.get(0));
       // MediaPlayer player; //= new MediaPlayer(aSong);
        //mediaPlayer.play();
        primaryStage.setScene(panel);
        primaryStage.show();
        songs.setOnMouseClicked(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
               if(event.getClickCount() == 2) {
                   String songFile = new String(songs.getSelectionModel().getSelectedItem()); //This gives me the song name as String
                   System.out.println(songFile);
                   Media aSong = new Media(directory.toURI().toString()+songFile ); // In side the () the two strings combine
                   player = new MediaPlayer(aSong);
                   //boolean playing = player.getStatus().equals(MediaPlayer.Status.PLAYING);
                   player.play();





               }

            }
        });



    }

    private List<String> makePlayList(File directory){
        List<String> list = Arrays.asList(directory.list(
                new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return name.endsWith(".mp3");
                    }
                }
        ));
        return list;
    }


    public static void main(String[] args) {
        launch(args);
    }
}
