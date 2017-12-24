package sample;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.prefs.Preferences;
import javafx.event.*;
import javafx.scene.input.*;
public class Main extends Application {
    private MediaPlayer player;
    private boolean playing;
    private long randomNum;
    private  Thread game;
    private Preferences preferences;
    private String ID;
    private ObservableList<String> observableList;
    private File directory;
    private int lowerVal = 10;
    private int upperVal = 27;
    private ListView<String> songs;
    @Override
    public void start(Stage primaryStage) throws Exception{
        DirectoryChooser directoryChooser = new DirectoryChooser();
        primaryStage.setTitle("Musical Chairs");
        /*Setting up the preference settings for the user*/
        preferences = Preferences.userRoot().node(this.getClass().getName());
        ID = "Preferred Directory";
        preferences.get(ID,"");
        if(preferences.get(ID,"").equals("")){
            directory = directoryChooser.showDialog(primaryStage);
            observableList = FXCollections.observableList(makePlayList(directory));
            while(observableList.isEmpty()) {
                directory = directoryChooser.showDialog(primaryStage);
                observableList = FXCollections.observableList(makePlayList(directory));
            }
            preferences.put(ID,directory.toString());

        }
        else{
            directory = new File(preferences.get(ID,""));
            observableList = FXCollections.observableList(makePlayList(directory));//observableList is like a list model, it is the model for our list, and it will update my list.

        }

        Menu menuFile = new Menu("File");
        menuFile.setMnemonicParsing(true);
        menuFile.setAccelerator(KeyCombination.keyCombination("SHORTCUT+F"));
        MenuBar menuBar = new MenuBar();
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());
        menuBar.getMenus().add(menuFile);
        MenuItem chooseDirectory = new MenuItem("Select Music Folder");
        chooseDirectory.setMnemonicParsing(true);
        chooseDirectory.setAccelerator(KeyCombination.keyCombination("SHORTCUT+S"));
        chooseDirectory.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                File directoryTemp = directoryChooser.showDialog(primaryStage);
                if(directoryTemp == null)
                    ;
                else {
                    observableList = FXCollections.observableList(makePlayList(directoryTemp));
                    while(observableList.isEmpty()){
                        directoryTemp = directoryChooser.showDialog(primaryStage);
                        observableList = FXCollections.observableList(makePlayList(directoryTemp));
                        
                    }
                    preferences.put(ID, directory.toString());
                }
            }
        });
        chooseDirectory.setMnemonicParsing(true);
        menuFile.getItems().add(chooseDirectory);
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));

        BorderPane borderPane = new BorderPane();
        Scene panel = new Scene(borderPane,260,200);
        Button play = new Button();
        Image icon = new Image(getClass().getResourceAsStream("Play24.gif"));
        ImageView iconView = new ImageView();
        iconView.setImage(icon);
        play.setGraphic(iconView);
        HBox hbButton =  new HBox(10);
        hbButton.setAlignment(Pos.BOTTOM_LEFT);
        hbButton.getChildren().add(play);


        borderPane.setTop(menuBar);
        borderPane.setLeft(songs);
        borderPane.setBottom(hbButton);
        primaryStage.setScene(panel);
        primaryStage.show();
        play.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(player != null && game != null && player.getStatus().equals(MediaPlayer.Status.PAUSED)) {
                    randomNumGenerator();
                    game = new Thread(new Game());
                    game.start();
                    player.play();
                    player.getOnPlaying();

                }

            }


        });
        songs.setOnMouseClicked(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
                if(event.getClickCount() == 2) {
                    if(game != null){
                        if(game.getState().equals(Thread.State.TIMED_WAITING)) {
                            System.out.println(game.getState());
                            game.interrupt();

                        }
                        System.out.println(game.getState());
                    }
                    if(player != null){
                        if(playing)
                            player.stop();
                    }
                    Media aSong = new Media(new File(directory,songs.getSelectionModel().getSelectedItem()).toURI().toString()); // In side the () the two strings combine
                    player = new MediaPlayer(aSong);
                    player.setOnPlaying(new Runnable() {
                        @Override
                        public void run() {
                            playing = true;
                        }
                    });
                    player.setOnPaused(new Runnable() {
                        @Override
                        public void run() {
                            playing = false;
                        }
                    });
                    player.play();
                    player.getOnPlaying();
                    randomNumGenerator();
                    game = new Thread(new Game());
                    game.start();
                }

            }
        });
        Menu settings = new Menu("Settings");
        settings.setMnemonicParsing(true);
        settings.setAccelerator(KeyCombination.keyCombination("ALT+S"));
        MenuItem  boundaries = new MenuItem("Set Bounds(Time)");
        boundaries.setMnemonicParsing(true);
        boundaries.setAccelerator(KeyCombination.keyCombination("ALT+B"));
        boundaries.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Stage temporaryStage = new Stage();
                temporaryStage.setTitle("Settings");
                GridPane gp = new GridPane();
                gp.setPadding(new Insets(1,1,1,1));
                gp.setAlignment(Pos.CENTER);
                Scene temporaryScene = new Scene(gp,210,210);
                temporaryStage.setScene(temporaryScene);
                TextField lower = new TextField();
                TextField upper = new TextField();
                Text lowerLabel = new Text("Lower:  ");
                Text upperLabel = new Text("Upper:  ");
                lower.setPrefColumnCount(2);
                upper.setPrefColumnCount(2);
                HBox organize = new HBox();
                Button Ok = new Button("Ok");
                Button cancel = new Button("Cancel");
                organize.getChildren().add(Ok);
                organize.getChildren().add(cancel);
                Text instructions = new Text("The song will play for a \nrandom amount of seconds \nbetween Lower and Upper \ninclusive");
                gp.add(instructions,1,1);
                gp.add(lowerLabel,1,2);
                gp.add(lower,2,2);
                gp.add(upperLabel,1,3);
                gp.add(upper,2,3);
                gp.add(organize,1,4);
                Ok.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                       lowerVal = Integer.parseInt(lower.getText());
                       upperVal = Integer.parseInt(upper.getText());
                        System.out.println(lowerVal);
                        System.out.println(upperVal);
                        temporaryStage.close();
                    }
                });
                cancel.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        temporaryStage.close();
                    }
                });
                temporaryStage.show();
            }
        });
        settings.getItems().add(boundaries);
        menuBar.getMenus().add(settings);

    }
    private   void randomNumGenerator(){
        System.out.println(lowerVal);
        System.out.println(upperVal);
        randomNum = ThreadLocalRandom.current().nextInt(lowerVal,upperVal);

    }
    private void addItems(String song,int index){
        observableList.add(index,song);
    }
    private void clearItems(){
        observableList.clear();
    }
    class Game implements Runnable{
        @Override
        public void run() {
            synchronized (game) {
                try {
                    game.wait(randomNum * 1000);
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
                if (player != null) {
                    player.pause();
                    player.getOnPaused();
                }


            }
        }
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
