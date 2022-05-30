package chess.pieces;

import chess.core.ChessPosition;
import chess.core.MoveFlags;
import chess.core.PieceMove;
import chess.core.RealMove;

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
        this.moves.add(new PieceMove(2, 0, false, false, MoveFlags.RM_CASTLE_KINGSIDE));
        this.moves.add(new PieceMove(-2, 0, false, false, MoveFlags.RM_CASTLE_QUEENSIDE));
    }


    public boolean canMove(int file, int rank, PieceMove m, ChessPosition position) {
        if (position.isAttackedSquare(rank + m.getVector().y, file + m.getVector().x)) {
            return false;
        }
        //int vx = m.getVector().x, vy = m.getVector().y;
        //castling
        //if (Math.abs(vx) == 2 && vy == 0) {
        if (MoveFlags.hasFlag(m.flags(), MoveFlags.RM_CASTLE_KINGSIDE) ||
        MoveFlags.hasFlag(m.flags(), MoveFlags.RM_CASTLE_QUEENSIDE)) {
            if (position.kingInCheck()) {
                return false;
            }
            //safety measures in case of a messed up FEN
            if ((isWhite() && rank != position.height() - 1) ||
                    (!isWhite() && rank != 0)) {
                return false;
            }
            //kingside
            if (MoveFlags.hasFlag(m.flags(), MoveFlags.RM_CASTLE_KINGSIDE)) {
                if (!position.canCastleKingSide()) {
                    return false;
                }
                if ((isWhite() && position.getWhiteKingsideRookFile() == -1) ||
                        (!isWhite() && position.getBlackKingsideRookFile() == -1)) {
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
                if ((isWhite() && position.getWhiteQueensideRookFile() == -1) ||
                    (!isWhite() && position.getBlackQueensideRookFile() == -1)) {
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
            if (MoveFlags.hasFlag(m.flags(), MoveFlags.RM_CASTLE_KINGSIDE) ||
                MoveFlags.hasFlag(m.flags(), MoveFlags.RM_CASTLE_QUEENSIDE)) {

                m.moveTo(position.getSquare(m.to().getRank(),
                         MoveFlags.hasFlag( m.flags(),
                                            MoveFlags.RM_CASTLE_KINGSIDE) ? position.width() - 2 : 2));  //for 960 positions // co tu sie dzieje :c
            }
            if (position.isWhiteToMove()) {
                if (position.whiteCastleKingSide()) {
                    m.setFlagsBitwise(MoveFlags.RM_WHITE_CASTLE_KINGSIDE_IMPOSSIBLE);
                }
                if (position.whiteCastleQueenSide()) {
                    m.setFlagsBitwise(MoveFlags.RM_WHITE_CASTLE_QUEENSIDE_IMPOSSIBLE);
                }
                position.setCastleFlags((ChessPosition.WHITE_KINGSIDE | ChessPosition.WHITE_QUEENSIDE));
            } else {
                if (position.blackCastleKingSide()) {
                    m.setFlagsBitwise(MoveFlags.RM_BLACK_CASTLE_KINGSIDE_IMPOSSIBLE);
                }
                if (position.blackCastleQueenSide()) {
                    m.setFlagsBitwise(MoveFlags.RM_BLACK_CASTLE_QUEENSIDE_IMPOSSIBLE);
                }
                position.setCastleFlags((ChessPosition.BLACK_KINGSIDE | ChessPosition.BLACK_QUEENSIDE));
            }
        }
    }

}
