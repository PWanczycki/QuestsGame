package org.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class NetworkServer extends Thread{

    private static NetworkServer networkServer;

    private ArrayList<ServerEventListener> _listeners;

    private int playerIDs = 0;

    private NewPlayerManager listener;

    private boolean waitingForPlayers = true;
    private int connectedPlayers = 0;

    private ArrayList<NetworkClient> _players;
    private BlockingQueue<NetworkMessage> _messagesReceived;

    private volatile boolean stopThread = false;

    private NetworkServer(){
        _players = new ArrayList<NetworkClient>();
        _messagesReceived = new LinkedBlockingQueue<NetworkMessage>();
        _listeners = new ArrayList<ServerEventListener>();
        listener = new NewPlayerManager();
        listener.start();


    }

    //Singleton
    public static NetworkServer get(){
        if(networkServer == null)
            networkServer = new NetworkServer();
        return networkServer;
    }

    public void handleMessage(NetworkMessage msg,ArrayList<Object> _objs){
        NetworkClient c = getByID(msg.playerID);

        switch (msg.messageType){
            case UNKNOWN:
                System.out.println("Unknown Message Received. Objs: " + _objs.toString());
            case HEARTBEAT:
                //Todo
                break;
            case CONNECT:
                c.setPlayerName(_objs.get(0).toString());

                for(ServerEventListener l: _listeners){
                    l.onPlayerConnect(c.getPlayerId(), c.getPlayerName());
                }

                connectedPlayers++;
                break;
            case DISCONNECT:
                disconnectPlayer(c.getPlayerId());

                for(ServerEventListener l: _listeners){
                    l.onPlayerDisconnect(c.getPlayerId(), c.getPlayerName());
                }

                connectedPlayers--;
                break;
            case START_GAME:
                for(ServerEventListener l: _listeners){
                    l.onGameStart();
                }

            case TEST_MESSAGE:

                break;
            case CARD_DRAW:
                for(ServerEventListener l: _listeners){
                    l.onDrawCard(c.getPlayerId());
                }
                break;
            case CARD_DISCARD:
                for(ServerEventListener l: _listeners){
                    l.onCardDiscard(c.getPlayerId(),(int)msg._objects.get(0));
                }
                break;
            case TURN_CHANGE:
                for(ServerEventListener l: _listeners){
                    l.onTurnChange(c.getPlayerId());
                }
                break;
            default:
                System.out.println("SERVER: Unhandled Message Received.");
                break;
        }
    }

    //General Thread Loop
    @Override
    public void run() {
        while(!stopThread){
            NetworkMessage msg;
            while ((msg = _messagesReceived.poll()) != null){
                ArrayList<Object> _objs = msg._objects;
                handleMessage(msg, _objs);
            }
        }
    }

    //All messages received from clients gets added to this list in no set order
    public void addReceivedMsg(NetworkMessage msg) {
        try {
            _messagesReceived.put(msg);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //Sends all messages in lists to all connected clients, including local player. Will not send to person who sent message.
    //public void echoMessage(NetworkMessage msg) {
    //    for (int i = 0; i < _players.size(); i++) {
    //        if (msg.playerID != _players.get(i).getPlayerId()) {
    //            _players.get(i).sendNetMsg(msg);
    //        }
    //    }
    //}

    //region Helpers
    public NetworkClient getByID(int plyID){
        for(int i = 0; i < _players.size(); i++){
            if(_players.get(i).getPlayerId() == plyID)
                return  _players.get(i);
        }
        System.out.println("SERVER: Couldn't find player with ID: " + String.valueOf(plyID));
        return null;
    }

    public void sendNetMessage(ServerMessage msg){
        for(NetworkClient c: _players){
            c.sendNetMsg(msg);
        }
    }

    public int getConnectedPlayers(){
        return connectedPlayers;
    }

    //Listeners
    public void addListener(ServerEventListener l){
        _listeners.add(l);
    }
    public void removeListener(ServerEventListener l){
        _listeners.remove(l);
    }

    //endregion

    public void disconnectPlayer(int plyID){
        NetworkClient c = getByID(plyID);
        c.close();
        _players.remove(c);
    }

    //Called when closing game
    public void close(){
        stopThread = true;
        this.interrupt();

        //Creates Disconnect Message  to send to all clients
        NetworkMessage msg = new ServerMessage(NetworkMsgType.DISCONNECT,null);

        for (int i = 0; i < _players.size(); i++) {
            _players.get(i).sendNetMsg(msg);
            _players.get(i).close();
        }

        listener.close();
        Game.get().close();
    }

    //Class for listening for new players.
    private class NewPlayerManager extends Thread{

        private ServerSocket serverSocket;
        private volatile boolean stopThread = false;

        public NewPlayerManager(){
            try {
                serverSocket = new ServerSocket(NetworkManager.get().PORT);
            } catch (IOException e) {
                //e.printStackTrace();
                System.out.println("SERVER: Couldn't Start Server: " + e.toString());
            }
        }

        @Override
        public void run() {
            try {
                while (!stopThread) {
                    Socket s = serverSocket.accept();
                    NetworkClient ply = new NetworkClient(s, playerIDs++,false);

                    ply.start();
                    _players.add(ply);
                    ArrayList<Object> o = NetworkMessage.pack(ply.getPlayerId());
                    ply.sendNetMsg(new NetworkMessage(ply.getPlayerId(), NetworkMsgType.CONNECT, o));
                }
            }
            catch (Exception e){
                //Might get here since waiting for accept might throw an exception
                //System.out.println("Exception: " + e.toString());
            }
            finally {
                close();
            }
        }

        public void close(){
            stopThread = true;
            this.interrupt();

            try {
                serverSocket.close();
            }
            catch (Exception e){
                System.out.println("SERVER: Couldn't Close Server: " + e.toString());
            }
        }
    }
}
