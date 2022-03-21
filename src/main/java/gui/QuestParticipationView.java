package gui;

import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import network.*;
import model.Card;
import model.FoeCard;
import model.WeaponCard;

import java.io.File;
import java.util.ArrayList;

public class QuestParticipationView {

    //Window sizes
    private int width = 1000;
    private int height = 1000;

    //List of cards
    ArrayList<Card> hand;
    ArrayList<Card> selectedCards;

    //Image of all cards
    Image advCards;

    //For drawing each card
    ArrayList<ImageView> handViews;
    ArrayList<ImageView> selectionViews;

    //Rectangles to show each area
    Rectangle selectArea;
    Rectangle handArea;

    //Labels to show the total BP of the selection and for an error
    Label totalBPLabel;
    Label errorLabel;

    //Groups of each ImageView that contains only the card ImageViews.
    Group selectionCardGroup = new Group();
    Group handCardGroup = new Group();

    //Shows the decline button or not. It wont show the decline button if on a seperate stage of the quest.
    private boolean showDeclineButton = false;


    public QuestParticipationView(boolean showDeclineButton){
        //Gets the hand of the local player
        hand = new ArrayList<>(LocalGameManager.get().getLocalPlayer().hand);
        selectedCards = new ArrayList<>();
        handViews = new ArrayList<>();
        selectionViews = new ArrayList<>();

        //Loads the image
        advCards = new Image(new File("src/resources/advComposite.jpg").toURI().toString());

        this.showDeclineButton = showDeclineButton;


        //To prevent JavaFX from yelling about threads.
        Platform.runLater(this::setup);

    }

    //This is called to redraw all the cards when a change is made.
    private void updateCards(){
        //Clears both groups.
        selectionCardGroup.getChildren().clear();
        handCardGroup.getChildren().clear();

        //Calculates the BP of all selected cards
        int bpVal = LocalGameManager.get().getLocalPlayer().getBattlePoints();
        //todo clean this up
        for(Card c: selectedCards){
            if(c instanceof FoeCard){
                bpVal += ((FoeCard) c).getBP();
            }
            else if(c instanceof WeaponCard){
                bpVal += ((WeaponCard) c).getBP();
            }
        }
        totalBPLabel.setText("You have selected " + selectedCards.size() + " card(s) for a total BP value of " + bpVal);

        //Drawing the selected cards
        for(int i = 0; i < selectedCards.size(); i++){
            ImageView aCard = new ImageView();
            aCard.setFitWidth(100);
            aCard.setFitHeight(140);
            aCard.setPreserveRatio(true);
            aCard.setX(selectArea.getX() + 10 + (i%8)*110);
            aCard.setY(selectArea.getY() + 10 + Math.floorDiv(i,8)*150);
            aCard.setImage(advCards);
            aCard.setViewport(View.getAdvCard(selectedCards.get(i).getID()));
            selectionCardGroup.getChildren().add(aCard);

            int finalI = i;
            aCard.setOnMouseClicked(mouseEvent -> {
                if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                    hand.add(selectedCards.get(finalI));
                    selectedCards.remove(finalI);
                    //Once moving a card, reupdate all the cards.
                    updateCards();
                    errorLabel.setVisible(false);
                }
            });
        }

        //Drawing all cards in hand.
        for(int i = 0; i < hand.size(); i++){
            ImageView aCard = new ImageView();
            aCard.setFitWidth(100);
            aCard.setFitHeight(140);
            aCard.setPreserveRatio(true);
            aCard.setX(handArea.getX() + 10 + (i%8)*110);
            aCard.setY(handArea.getY() + 10 + Math.floorDiv(i,8)*150);
            aCard.setImage(advCards);
            aCard.setViewport(View.getAdvCard(hand.get(i).getID()));
            handCardGroup.getChildren().add(aCard);

            int finalI = i;
            aCard.setOnMouseClicked(mouseEvent -> {
                if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                    if(!validateCardSelection(hand.get(finalI))){
                        //Shows an error label because two of the same cards are selected.
                        errorLabel.setVisible(true);
                        return;
                    }
                    selectedCards.add(hand.get(finalI));
                    hand.remove(finalI);
                    updateCards();
                    errorLabel.setVisible(false);
                }
            });
        }

    }

    //Inital setup for the window.
    private void setup(){
        Stage stage = new Stage();
        //Everything is added to this main group.
        Group mainGroup = new Group();

        //The areas for the selected cards and the cards in the hand
        selectArea = new Rectangle(55,200,890,200);
        handArea = new Rectangle(55,height-400,890,310);

        Font largeFont = new Font("Arial", 20);

        //Selected Cards subgroup
        Group selectionGroup = new Group();
        //TODO: add info about stage number and number of cards in the stage
        Label selectionLabel = new Label("Select cards for the stage of the quest!");
        selectionLabel.setFont(largeFont);
        selectionLabel.setLayoutX(20);
        selectionLabel.setLayoutY(50);
        selectionGroup.getChildren().add(selectionLabel);

        totalBPLabel = new Label("You have selected 0 card(s) for a total BP value of 0");
        totalBPLabel.setFont(largeFont);
        totalBPLabel.setLayoutX(20);
        totalBPLabel.setLayoutY(100);
        selectionGroup.getChildren().add(totalBPLabel);


        selectArea.setFill(Color.DARKBLUE);
        selectArea.setStroke(Color.SADDLEBROWN);
        selectArea.setArcWidth(30);
        selectArea.setArcHeight(20);
        selectionGroup.getChildren().add(selectArea);

        //Error label to notify that two cards of the same type cant be added. Set to invisible by default
        errorLabel = new Label("You cannot add two cards of the same type or play any foe cards!");
        errorLabel.setFont(largeFont);
        errorLabel.setLayoutX(20);
        errorLabel.setLayoutY(150);
        errorLabel.setTextFill(Color.RED);
        errorLabel.setVisible(false);
        selectionGroup.getChildren().add(errorLabel);

        selectionGroup.getChildren().add(selectionCardGroup);
        mainGroup.getChildren().add(selectionGroup);


        //Hand Area
        Group handGroup = new Group();
        handArea.setFill(Color.DARKGRAY);
        handArea.setStroke(Color.SADDLEBROWN);
        handArea.setArcWidth(30);
        handArea.setArcHeight(20);
        handGroup.getChildren().add(handArea);

        handGroup.getChildren().add(handCardGroup);
        mainGroup.getChildren().add(handGroup);

        //Decline to participate button
        if(showDeclineButton) {
            Button declineButton = new Button();
            declineButton.setLayoutX(10);
            declineButton.setLayoutY(height - 450);
            declineButton.setText("Decline");

            declineButton.setOnAction(e -> {
                LocalClientMessage msg = new LocalClientMessage(NetworkMsgType.QUEST_PARTICIPATE_QUERY, NetworkMessage.pack(true));
                NetworkManager.get().sendNetMessageToServer(msg);
                stage.close();
            });

            mainGroup.getChildren().add(declineButton);
        }

        //Accept button
        Button acceptButton = new Button();
        acceptButton.setLayoutX(width - 55);
        acceptButton.setLayoutY(height - 450);
        acceptButton.setText("Battle!");

        acceptButton.setOnAction(e -> {
            if(selectedCards.size() != 0){
                int[] sendCards = new int[selectedCards.size()];
                for (int i = 0; i < selectedCards.size(); i++){
                    sendCards[i] = selectedCards.get(i).getID();
                }

                LocalClientMessage msg = new LocalClientMessage(NetworkMsgType.QUEST_PARTICIPATE_QUERY,NetworkMessage.pack(false,sendCards));
                NetworkManager.get().sendNetMessageToServer(msg);

                int[] cardIDs = new int[hand.size()];
                for(int i = 0; i < hand.size(); i++){
                    cardIDs[i] = hand.get(i).getID();
                }

                //LocalClientMessage msg2 = new LocalClientMessage(NetworkMsgType.UPDATE_HAND,NetworkMessage.pack(cardIDs));
                //NetworkManager.get().sendNetMessageToServer(msg2);

                //For discarding
                LocalClientMessage msg3 = new LocalClientMessage(NetworkMsgType.CARD_DISCARD_X, NetworkMessage.pack(Card.getCardIDsFromArrayList(selectedCards)));
                NetworkManager.get().sendNetMessageToServer(msg3);

                LocalGameManager.get().getLocalPlayer().hand = hand;
                View.get().update();
                
                stage.close();
            }
        });

        mainGroup.getChildren().add(acceptButton);


        //Puts everything together
        Scene s1 = new Scene(mainGroup,width,height);

        //Draws all the cards
        updateCards();

        //Opens the window and waits.
        stage.setScene(s1);
        stage.setTitle((LocalGameManager.get().getLocalPlayer().getPlayerNum() + 1) + " " + LocalGameManager.get().getLocalPlayer().getPlayerName());
        stage.showAndWait();

    }

    public boolean validateCardSelection(Card c){
        //Simply checks to see if card is already in selection or is a foe card.
        if(c instanceof FoeCard)
            return false;

        for (Card x: selectedCards) {
            if(c.getID() == x.getID())
                return false;
        }
        return true;
    }

}
