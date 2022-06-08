package chess;

import chess.players.HumanPlayer;
import chess.players.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest {
    @Test
    public void playerWhiteHuman(){
        var player = new HumanPlayer("Joe");
        assertEquals(player.getName(), "Joe");
        assertTrue(player.getIsHuman());
    }

    @Test
    public void playerBlackRobot(){
        /*var player = new Player("Jim", false, false);
        assertEquals(player.getName(), "Jim");
        assertFalse(player.getIsHuman());
        assertFalse(player.getIsWhite());*/
    }


}