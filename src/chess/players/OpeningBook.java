package chess.players;

import chess.core.RealMove;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.*;

public class OpeningBook { //goes up to move 6
    private static List<String> openingLines = new ArrayList<String>();
    private static boolean didRead = false;
    private static Random rand = new Random();
    public static boolean readFromFile(String path) {
        if (didRead) {
            return openingLines.size() > 0;
        }
        didRead = true;
        File openingBook = new File(path);
        try {
            Scanner reader = new Scanner(openingBook);
            while (reader.hasNextLine()) {
                openingLines.add(reader.nextLine());
            }
            reader.close();
            return openingLines.size() > 0;
        } catch (FileNotFoundException e) {
            return false;
        }
    }
    public static boolean bookExists() { return openingLines.size() > 0; }
    public static String getRandomMove(Deque<RealMove> moveHistory) {
        List<String> matchingLines = new ArrayList<String>();
        StringBuilder moveHistoryString = new StringBuilder();
        int i = 0;
        if (moveHistory.size() > 0) {
            for (RealMove m : moveHistory) {
                if (++i % 2 != 0) {
                    moveHistoryString.append(((i + 1) / 2) + ".");
                }
                moveHistoryString.append(m.getAlgebraicMove() + ' ');
            }
            if (i % 2 == 0) {
                moveHistoryString.append((i + 2) / 2 + ".");
            }
            for (String s : openingLines) {
                if (s.contains(moveHistoryString)) {
                    matchingLines.add(s);
                }
            }
            if (matchingLines.size() == 0) {
                return null;
            }
            String splits[] = matchingLines.get(rand.nextInt(matchingLines.size())).split(moveHistoryString.toString());
            switch (splits.length) {
                case 2:
                    return splits[1].split(" ")[0];
                case 1:
                    return splits[0];
                default:
                    return null;
            }
        }
        return openingLines.get(rand.nextInt(openingLines.size())).substring(2).split(" ")[0];
    }
}
