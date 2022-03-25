package network;

public enum NetworkMsgType {

    //For Network core/ Not directly game related.
    UNKNOWN, //Should never be this
    HEARTBEAT, //DEPRECATED
    CONNECT, //Initial Connection. Server Sends ID to be assigned. Client replies with Name to use.
    DISCONNECT, //Unimplemented
    UPDATE_PLAYERLIST, //Sent from server with ID,Name, and CardIDs
    START_GAME, //Sent by host to start game.
    TEST_MESSAGE, //For Testing


    //Note that all messages received from clients include their playerID embedded in the message object. Plurals (eg cardIDs vs cardID) imply array.
    UPDATE_HAND, //DEPRECATED
    UPDATE_SHIELDS, //To Client[playerID, shieldCount] - To Server UNHANDLED
    UPDATE_ALLIES, //To Client[playerID, cardIDs] - To Server [cardIDs]
    UPDATE_AMOUR, //To Client[playerID, cardID] - To Server [cardID] | If ply == -1 then clear all players amours
    CLEAR_ALLIES, //To Client[playerID] - To Server[UNHANDLED] | If plyID == -1 then clear all players amours

    TURN_CHANGE, //To Client [playerID] - To Server [NULL]
    CARD_DRAW, //To Client[playerID, cardID] - To Server [NULL]
    CARD_DRAW_X, //To Client [playerID, cardIDs] - To Server[drawAmount]
    CARD_DISCARD, //To Client [playerID, cardID] - To Server [cardID]
    CARD_DISCARD_X, //To Client[playerID, cardIDs] - To Server [cardIDs]

    STORY_CARD_DRAW, //To Client [playerID, drawnCardID] - To Server [playerID]

    QUEST_BEGIN, //To Client [playerID, questID] - To Server UNHANDLED
    QUEST_SPONSOR_QUERY, //To Client[questID] - To Server [declinedBoolean, questSponsorCardIDs]
    QUEST_PARTICIPATE_QUERY, //To Client[sponsorID, questID, stageCardIDs] - To Server [declineBoolean, cardIDs]
    QUEST_STAGE_RESULT, //To Client[questID, wonBoolean, stageCardIDs, playerCardIDs] - To Server UNHANDLED
    QUEST_FINAL_RESULT, //To Client [winnerID, sponsorCardIDs] - To Server UNHANDLED

    EVENT_BEGIN, //To client[drawerID, cardID] - To Server UNHANDLED

    TOURNAMENT_BEGIN, //To Client[drawerID, tournamentID] - To Server UNHANDLED
    TOURNAMENT_PARTICIPATION_QUERY, //To Client[tournamentID] - To Server [declineBoolean, cardIDs]
    TOURNAMENT_TIE, //To Client[tournamentID] - To Server UNHANDLED
    TOURNAMENT_FINAL_RESULT, //To Client[winnerID, scoreTable?] - To Server UNHANDLED

    TEST_BEGIN, //To Client [drawerID, testID] - To Server UNHANDLED
    TEST_BID_QUERY, //To Client[testID, questID, currentBid] - To Server [declineBoolean, currentBid]
    TEST_FINAL_RESULT, //To Client[testID, winnerID, currentBid] - To Server UNHANDLED


}
