package org.example;

import java.util.ArrayList;

public class LocalGameManager implements ClientEventListener{

    //Singleton
    public static LocalGameManager localGameManager;

    private ArrayList<Player> _players;

    private Player localPlayer;

    private int turnID = -1;
    private ArrayList<Card> _cardsOnBoard;

    private Card latestDiscardedCard;


    private boolean gameStarted = false;
    private int connectedPlayerCount = 0;


    private LocalGameManager(){
        _players = new ArrayList<Player>();
        _cardsOnBoard = new ArrayList<>();
        NetworkManager.get().addListener(this);
    }

    public static LocalGameManager get(){
        if(localGameManager == null)
            localGameManager = new LocalGameManager();
        return localGameManager;
    }


    //region Helpers
    private Player getPlayerByID(int plyID){
        for(Player p: _players){
            if(p.getPlayerNum() == plyID)
                return p;
        }
        return null;
    }

    public Player getLocalPlayer(){
        if(localPlayer != null)
            return localPlayer;
        else {
            for (int i = 0; i < _players.size(); i++) {
                if (_players.get(i).getPlayerNum() == NetworkManager.get().getLocalPlayerID()) {
                    return _players.get(i);
                }
            }
            return null;
        }
    }

    public boolean isMyTurn(){
        return localPlayer.getPlayerNum() == turnID;
    }

    public void finishTurn(){
        if(!isMyTurn())
            return;

        LocalClientMessage msg = new LocalClientMessage(NetworkMsgType.TURN_CHANGE,null);
        NetworkManager.get().sendNetMessage(msg);
    }

    public String[] getAllPlayerNames(){
        String[] x = new String[_players.size()];
        for (int i = 0; i < _players.size(); i++){
            x[i] = _players.get(i).getPlayerName();
        }
        return x;
    }

    public ArrayList<Player> getPlayers(){
        return _players;
    }

    public boolean isGameStarted(){return gameStarted;}

    public int getConnectedPlayerCount(){return connectedPlayerCount;}

    //Could be null
    public Card getLatestDiscardedCard(){return latestDiscardedCard;}

    //endregion

    @Override
    public void onPlayerConnect(int plyID, String playerName, int[] cardIDs) {
        Player p = new Player(plyID,playerName);

        //todo addcardsByIDs not complete
        p.addCardsByIDs(cardIDs);

        _players.add(p);

        connectedPlayerCount++;
    }

    @Override
    public void onPlayerDisconnect(int plyID, String playerName) {
        int x = -1;
        for (int i = 0; i < _players.size(); i++) {
            if(_players.get(i).getPlayerNum() == plyID){
                x = i;
            }
        }

        if(x != -1)
            _players.remove(x);

        connectedPlayerCount--;

        System.out.println("CLIENT: Player " + playerName + " disconnected. ID: " + String.valueOf(plyID));
    }

    @Override
    public void onStartGame() {
        gameStarted = true;
    }

    @Override
    public void onTurnChange(int idOfPlayer) {
        turnID = idOfPlayer;
    }

    @Override
    public void onDrawCard(int plyID, int cardID) {
        getPlayerByID(plyID).addCardByID(cardID);
    }

    @Override
    public void onCardDiscard(int plyID, int cardID) {
        getPlayerByID(plyID).discardCardFromHand(cardID);

        //todo lookup card by ID and set it to latestDiscardedCard
    }
}