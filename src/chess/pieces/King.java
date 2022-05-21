package chess.pieces;

import chess.ChessPosition;
import chess.MoveFlags;
import chess.PieceMove;
import chess.RealMove;

public class King extends Piece {
    public King(char sign) {
        super(sign);
    }

    @Override
    void setMoves() {
        this.moves.add(new PieceMove(1, 1, true, false));
        this.moves.add(new PieceMove(1, -1, true, false));
        this.moves.add(new PieceMove(-1, 1, true, false));
        this.moves.add(new PieceMove(-1, -1, true, false));
        this.moves.add(new PieceMove(1, 0, true, false));
        this.moves.add(new PieceMove(-1, 0, true, false));
        this.moves.add(new PieceMove(0, 1, true, false));
        this.moves.add(new PieceMove(0, -1, true, false));
        //castling
        this.moves.add(new PieceMove(2, 0, false, false));
        this.moves.add(new PieceMove(-2, 0, false, false));
    }


    public boolean canMove(int file, int rank, PieceMove m, ChessPosition position) {
        if (position.isAttackedSquare(rank + m.getVector().y, file + m.getVector().x)) {
            return false;
        }
        int vx = m.getVector().x, vy = m.getVector().y;
        //castling
        if (Math.abs(vx) == 2 && vy == 0) {
            if (position.kingInCheck()) {
                return false;
            }
            //[0][0] = black queenside rook
            //[0][1] = black kingside rook
            //[1][0] = white queenside rook
            //[1][1] = white kingside rook
            //kingside
            if (vx == 2) {
                if (!position.canCastleKingSide()) {
                    return false;
                }
                for (int y = file + 1; y < position.width() - 1; ++y) {
                    if (position.isAttackedSquare(rank,y)) {
                        return false;
                    }
                    if (!position.getSquare(rank,y).isEmpty()) {
                        return false;
                    }
                }
                return true;
            }
            //queenside
            else {
                if (!position.canCastleQueenSide()) {
                    return false;
                }
                for (int y = file - 1; y >= 2; --y) {
                    if (position.isAttackedSquare(rank,y)) {
                        return false;
                    }
                    if (!position.getSquare(rank,y).isEmpty()) {
                        return false;
                    }
                }
                return true;
            }
            /*if (!position.kingInCheck()) {
                if (this.isWhite()) {
                    if (x == 2 && position.whiteCastleKingSide()) {
                        return true;
                    } else if (x == -2 && position.whiteCastleQueenSide()) {
                        return true;
                    }
                } else {
                    if (x == 2 && position.blackCastleKingSide()) {
                        return true;
                    } else if (x == -2 && position.blackCastleQueenSide()) {
                        return true;
                    }
                }
            }
            return false;*/
        }
        return true;
    }

    public void commitMove(RealMove m, ChessPosition position) {
        if (position.canCastleKingSide() || position.canCastleQueenSide()) {
            if (MoveFlags.hasFlag(m.flags, MoveFlags.RM_CASTLE_KINGSIDE) ||
                    MoveFlags.hasFlag(m.flags, MoveFlags.RM_CASTLE_QUEENSIDE)) {
                m.to =  position.getSquare(m.to.getRank(),
                        MoveFlags.hasFlag(m.flags, MoveFlags.RM_CASTLE_KINGSIDE) ? position.width() - 2 : 2); //for 960 positions // co tu sie dzieje :c
            }
            if (position.isWhiteToMove()) {
                if (position.whiteCastleKingSide()) {
                    m.flags |= MoveFlags.RM_WHITE_CASTLE_KINGSIDE_IMPOSSIBLE;
                }
                if (position.whiteCastleQueenSide()) {
                    m.flags |= MoveFlags.RM_WHITE_CASTLE_QUEENSIDE_IMPOSSIBLE;
                }
                position.castleFlags &= ~(ChessPosition.WHITE_KINGSIDE | ChessPosition.WHITE_QUEENSIDE);
            } else {
                if (position.blackCastleKingSide()) {
                    m.flags |= MoveFlags.RM_BLACK_CASTLE_KINGSIDE_IMPOSSIBLE;
                }
                if (position.blackCastleQueenSide()) {
                    m.flags |= MoveFlags.RM_BLACK_CASTLE_QUEENSIDE_IMPOSSIBLE;
                }
                position.castleFlags &= ~(ChessPosition.BLACK_KINGSIDE | ChessPosition.BLACK_QUEENSIDE);
            }
        }
    }

}
