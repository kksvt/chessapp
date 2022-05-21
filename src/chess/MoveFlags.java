package chess;

public class MoveFlags {
    public final static int RM_PROMOTION = 1;
    public final static int RM_CASTLE_KINGSIDE = 2;
    public final static int RM_CASTLE_QUEENSIDE = 4;
    public final static int RM_WHITE_CASTLE_KINGSIDE_IMPOSSIBLE = 8;
    public final static int RM_WHITE_CASTLE_QUEENSIDE_IMPOSSIBLE = 16;
    public final static int RM_BLACK_CASTLE_KINGSIDE_IMPOSSIBLE = 32;
    public final static int RM_BLACK_CASTLE_QUEENSIDE_IMPOSSIBLE = 64;
    public final static int RM_ROOK_CASTLING = 128;

    public static boolean hasFlag(int bitmask, int flag) {
        return (bitmask & flag) == flag;
    }
}
