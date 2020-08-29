import org.junit.Test;

import java.util.*;

import static  org.junit.Assert.*;
public class tests {

    /**
     * just ensuring Junit is correctly set up
     */
    @Test
    public void sampleTest(){
        int num = 6;
        assert (num == 6);

    }

    /**
     * the first player refuted (first after player making suggestion) has cards used
     */
    @Test
    public void possibleRefutesFirstPlayer(){
        List<Player> players = new ArrayList<>();
        Player p1 = new Player(CharacterCard.MISS_SCARLETT, null, "PLayer 1");
        players.add(p1);
        Player p2 = new Player(CharacterCard.COLONEL_MUSTARD, null, "PLayer 2");
        players.add(p2);
        p2.addCard(WeaponCard.SPANNER);
        p2.addCard(WeaponCard.CANDLESTICK);
        p2.addCard(RoomCard.LOUNGE);


        CardTriplet guess = new CardTriplet(CharacterCard.MISS_SCARLETT, WeaponCard.SPANNER, RoomCard.LOUNGE);
        Card[] expected = {WeaponCard.SPANNER, RoomCard.LOUNGE};
        assert(checkRefute(guess, expected, p1, players) == 0);
    }

    /**
     * the first player refuted (first after player making suggestion) has cards used
     * The player has multiple possible cards
     */
    @Test
    public void incorrectRefutesFirstPlayer(){
        List<Player> players = new ArrayList<>();
        Player p1 = new Player(CharacterCard.MISS_SCARLETT, null, "PLayer 1");
        players.add(p1);
        Player p2 = new Player(CharacterCard.COLONEL_MUSTARD, null, "PLayer 2");
        players.add(p2);
        p2.addCard(WeaponCard.SPANNER);
        p2.addCard(WeaponCard.CANDLESTICK);
        p2.addCard(RoomCard.LOUNGE);


        CardTriplet guess = new CardTriplet(CharacterCard.MISS_SCARLETT, WeaponCard.SPANNER, RoomCard.LOUNGE);
        Card[] expected = {WeaponCard.SPANNER};
        assertFalse(checkRefute(guess, expected, p1, players) == 0);
        assert(checkRefute(guess, expected, p1, players) == 1);
    }
    /**
     * No players can refute a given suggestion
     */
    @Test
    public void noPossibleRefute(){
        List<Player> players = new ArrayList<>();
        Player p1 = new Player(CharacterCard.MISS_SCARLETT, null, "PLayer 1");
        players.add(p1);
        Player p2 = new Player(CharacterCard.COLONEL_MUSTARD, null, "PLayer 2");
        players.add(p2);
        p2.addCard(WeaponCard.SPANNER);
        p2.addCard(WeaponCard.CANDLESTICK);
        p2.addCard(RoomCard.LOUNGE);


        CardTriplet guess = new CardTriplet(CharacterCard.MISS_SCARLETT, WeaponCard.LEAD_PIPE, RoomCard.BILLIARD_ROOM);
        Card[] expected = {WeaponCard.SPANNER};
        assert (checkRefute(guess, expected, p1, players) == 0);
    }

    /**
     * the second player refuted (second after player making suggestion) has cards used
     */
    @Test
    public void possibleRefutesSecondPlayer(){
        List<Player> players = new ArrayList<>();
        Player p1 = new Player(CharacterCard.MISS_SCARLETT, null, "PLayer 1");
        players.add(p1);
        Player p2 = new Player(CharacterCard.COLONEL_MUSTARD, null, "PLayer 2");
        players.add(p2);
        Player p3 = new Player(CharacterCard.MR_GREEN, null, "PLayer 3");
        players.add(p3);
        p2.addCard(WeaponCard.SPANNER);
        p2.addCard(WeaponCard.CANDLESTICK);
        p2.addCard(RoomCard.LOUNGE);
        p3.addCard(CharacterCard.COLONEL_MUSTARD);
        p3.addCard(RoomCard.BALLROOM);
        p3.addCard(RoomCard.LIBRARY);

        CardTriplet guess = new CardTriplet(CharacterCard.COLONEL_MUSTARD, WeaponCard.LEAD_PIPE, RoomCard.BALLROOM);
        Card[] expected = {CharacterCard.COLONEL_MUSTARD, RoomCard.BALLROOM};
        assert(checkRefute(guess, expected, p1, players) == 0);
    }

    /**
     * Player makes a correct accusation
     */
    @Test
    public void correctAssumption(){
        Game g = new Game();
        Player p1 = new Player(CharacterCard.MISS_SCARLETT, null, "PLayer 1");
        CardTriplet answer = new CardTriplet(CharacterCard.COLONEL_MUSTARD, WeaponCard.LEAD_PIPE, RoomCard.BALLROOM);
        assert(g.makeAccusation(p1, "COLONEL_MUSTARD", "LEAD_PIPE", "BALLROOM", answer));
    }

    /**
     * Player makes an incorrect accusation
     */
    @Test
    public void incorrectAssumption(){
        Game g = new Game();
        Player p1 = new Player(CharacterCard.MISS_SCARLETT, null, "PLayer 1");
        CardTriplet answer = new CardTriplet(CharacterCard.COLONEL_MUSTARD, WeaponCard.LEAD_PIPE, RoomCard.BALLROOM);
        assertFalse(g.makeAccusation(p1, "MISS_SCARLETT", "LEAD_PIPE", "BALLROOM", answer));
    }

    private int checkRefute(CardTriplet guess, Card[] hand, Player p, List<Player> players){
        Game g = new Game();

        List<Card> retrieved = g.doRefutations(p,guess,players);
        if(retrieved == null) return 0;
        for(int i = 0; i < hand.length; i ++){
            if(retrieved.contains(hand[i])) retrieved.remove(hand[i]);
        }
        return retrieved.size();


    }

}
