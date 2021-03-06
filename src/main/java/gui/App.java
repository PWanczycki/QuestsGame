package gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import network.LocalClientMessage;
import network.NetworkManager;
import network.NetworkMsgType;

import java.util.Random;


/**
 * JavaFX App
 */
public class App extends Application {

    @Override
    public void start(Stage stage) {
        var scene = new Scene(View.get(), View.get().getWidth(), View.get().getHeight());

        stage.setScene(scene);
        stage.setResizable(false);

        //Join/Create Game popup.
        doJoinPopup();
        scene.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                //For quick testing
                LocalClientMessage msg = new LocalClientMessage(NetworkMsgType.TEST_MESSAGE,null);
                NetworkManager.get().sendNetMessageToServer(msg);
            }
        });

        View.get().doWaitPopup();

        stage.setTitle("Quests of the Round Table");
        stage.show();

        stage.setOnCloseRequest(e -> {
            stop();
            Platform.exit();
        });
    }

    public static void main(String[] args) {
        launch();
    }

    //Called when closed.
    @Override
    public void stop(){
        if(NetworkManager.isInstantiated())
            NetworkManager.get().close();
    }

    public void doJoinPopup(){

        Stage joinPopup = new Stage();
        joinPopup.initModality(Modality.APPLICATION_MODAL);
        joinPopup.setTitle("Start New Game or Join Game");
        joinPopup.initStyle(StageStyle.UNDECORATED);

        //Quick names for testing/default input
        String[] fakeNames = {"Alex", "Henry", "Philip", "Walker", "Joe", "John", "Carl", "Trent", "Sam"};

        Label label1 = new Label("Join Game or Start New Game");

        //For quick ip selection
        ComboBox<String> ipDropdown = new ComboBox<String>();

        ipDropdown.getItems().addAll("localhost", "174.115.221.185");
        ipDropdown.setEditable(true);
        ipDropdown.setValue("localhost");

        //TextField ipField = new TextField("localhost");

        Label label2 = new Label("Enter your name or select one:");
        //Random name from list
        ComboBox<String> nameDropdown = new ComboBox<String>();
        nameDropdown.getItems().addAll(fakeNames);
        nameDropdown.setEditable(true);
        nameDropdown.setValue(fakeNames[new Random().nextInt(fakeNames.length)]);

        //TextField nameField = new TextField(fakeNames[new Random().nextInt(fakeNames.length)]);

        Button joinButton = new Button("Join Game");
        Button newGameButton = new Button("Start New Game");


        joinButton.setOnAction(e -> {
            if(ipDropdown.getValue().toString().isEmpty())
                return;

            label1.setText("Joining...");
            NetworkManager.get().joinGame(ipDropdown.getValue().toString(),nameDropdown.getValue().toString());
            joinPopup.close();
        });

        newGameButton.setOnAction(e -> {
            NetworkManager.get().createGame(nameDropdown.getValue().toString());
            joinPopup.close();
        });


        VBox layout= new VBox(10);
        layout.getChildren().addAll(label1, ipDropdown, label2, nameDropdown, joinButton, newGameButton);
        layout.setAlignment(Pos.CENTER);
        Scene scene1= new Scene(layout, 300, 250);
        joinPopup.setScene(scene1);
        joinPopup.showAndWait();
    }

}