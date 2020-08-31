import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;

/**
 * Test suite for game mechanics
 */
public class CluedoTests {
    /**
     * the first player refuted (first after player making suggestion) has cards used
     */
    @Test
    public void possibleRefutesFirstPlayer(){
        List<Player> players = new ArrayList<>();
        Player p1 = new Player(CharacterCard.MISS_SCARLETT,  "PLayer 1");
        players.add(p1);
        Player p2 = new Player(CharacterCard.COLONEL_MUSTARD,  "PLayer 2");
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
        Player p1 = new Player(CharacterCard.MISS_SCARLETT, "PLayer 1");
        players.add(p1);
        Player p2 = new Player(CharacterCard.COLONEL_MUSTARD, "PLayer 2");
        players.add(p2);
        p2.addCard(WeaponCard.SPANNER);
        p2.addCard(WeaponCard.CANDLESTICK);
        p2.addCard(RoomCard.LOUNGE);


        CardTriplet guess = new CardTriplet(CharacterCard.MISS_SCARLETT, WeaponCard.SPANNER, RoomCard.LOUNGE);
        Card[] expected = {WeaponCard.SPANNER};
        assertNotEquals(0, checkRefute(guess, expected, p1, players));
        assert(checkRefute(guess, expected, p1, players) == 1);
    }
    /**
     * No players can refute a given suggestion
     */
    @Test
    public void noPossibleRefute(){
        List<Player> players = new ArrayList<>();
        Player p1 = new Player(CharacterCard.MISS_SCARLETT,  "PLayer 1");
        players.add(p1);
        Player p2 = new Player(CharacterCard.COLONEL_MUSTARD, "PLayer 2");
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
        Player p1 = new Player(CharacterCard.MISS_SCARLETT, "PLayer 1");
        players.add(p1);
        Player p2 = new Player(CharacterCard.COLONEL_MUSTARD, "PLayer 2");
        players.add(p2);
        Player p3 = new Player(CharacterCard.MR_GREEN, "PLayer 3");
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
        Player p1 = new Player(CharacterCard.MISS_SCARLETT, "PLayer 1");
        CardTriplet answer = new CardTriplet(CharacterCard.COLONEL_MUSTARD, WeaponCard.LEAD_PIPE, RoomCard.BALLROOM);
        assert(g.makeAccusation(p1, "COLONEL_MUSTARD", "LEAD_PIPE", "BALLROOM", answer));
    }

    /**
     * Player makes an incorrect accusation
     */
    @Test
    public void incorrectAssumption(){
        Game g = new Game();
        Player p1 = new Player(CharacterCard.MISS_SCARLETT, "PLayer 1");
        CardTriplet answer = new CardTriplet(CharacterCard.COLONEL_MUSTARD, WeaponCard.LEAD_PIPE, RoomCard.BALLROOM);
        assertFalse(g.makeAccusation(p1, "MISS_SCARLETT", "LEAD_PIPE", "BALLROOM", answer));
    }

    /**
     * Adding players - happens at start of new game
     */
    @Test
    public void createPlayers(){
        Game g = new Game();
        g.createPlayer("Player1", CharacterCard.MR_GREEN, false);
        g.createPlayer("Player2", CharacterCard.COLONEL_MUSTARD, false);
        assert (g.getPlayers().size() == 2);
        assert (g.getPlayers().get(0).toString().equals("\u001B[32mg\u001B[0m"));
        assert (g.getPlayers().get(1).toString().equals("\u001B[33mc\u001B[0m"));
    }

    private int checkRefute(CardTriplet guess, Card[] hand, Player p, List<Player> players){
        Game g = new Game();

        List<Card> retrieved = g.doRefutations(p,guess,players);
        if(retrieved == null) return 0;
        for (Card card : hand) {
            if (retrieved.contains(card)) retrieved.remove(card);
        }
        return retrieved.size();


    }

}
