package network;

public interface ClientEventListener {
    void onPlayerConnect(int plyID, String playerName, int[] cardIDs);
    void onPlayerDisconnect(int plyID, String playerName);

    void onStartGame();

    void onTurnChange(int idOfPlayer);

    void onUpdateHand(int plyID, int[] cardIDs);
    void onUpdateShields(int plyID, int shieldCount);

    void onDrawCard(int plyID, int cardID);
    void onDrawCardX(int plyID, int[] cardIDs);
    void onCardDiscard(int plyID, int cardID);

    void onStoryDrawCard(int plyID, int cardID);

    void onQuestBegin(int plyID, int questCardID);
    void onQuestSponsorQuery(int questCardID);
    void onQuestParticipateQuery(int sponsorPlyID, int questID);
    void onQuestStageResult(int questCardID, boolean wonStage, int[] stageCardsIDs, int[] playerCardsIDs);
    void onQuestFinalResult(int winnerID, int[][] sponsorCards);

    void onEventStoryBegin(int plyID, int eventCardID);

}
