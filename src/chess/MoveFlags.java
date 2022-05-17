package chess;

public class MoveFlags {
    final static int RM_PROMOTION = 1;
    final static int RM_CASTLE_KINGSIDE = 2;
    final static int RM_CASTLE_QUEENSIDE = 4;
    final static int RM_WHITE_CASTLE_KINGSIDE_IMPOSSIBLE = 8;
    final static int RM_WHITE_CASTLE_QUEENSIDE_IMPOSSIBLE = 16;
    final static int RM_BLACK_CASTLE_KINGSIDE_IMPOSSIBLE = 32;
    final static int RM_BLACK_CASTLE_QUEENSIDE_IMPOSSIBLE = 64;

    public static boolean hasFlag(int bitmask, int flag) {
        return (bitmask & flag) == flag;
    }
}
