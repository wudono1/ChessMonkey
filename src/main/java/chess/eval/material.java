package chess.eval;
import chess.bitboard;
import chess.constants;

public class material {

    public static final int[] BISHOP_PAIR_VS_NUM_PAWNS = {30, 34, 40, 50, 56, 58, 55, 49, 39};
    public static final int KING_VALUE = 20000;

    public static int totalMatScore(int wpCount, int wnCount, int wbCount, int wrCount, int wqCount,
                                    int bpCount, int bnCount, int bbCount, int brCount, int bqCount, int turn) {
        return (matCount(wpCount, wnCount, wbCount, wrCount, wqCount, bpCount, bnCount, bbCount, brCount, bqCount) +
                bishopPairEval(wbCount, bbCount, wpCount, bpCount)) * turn;
    }

    public static int matCount(int wpCount, int wnCount, int wbCount, int wrCount, int wqCount,
                               int bpCount, int bnCount, int bbCount, int brCount, int bqCount) {
        return (constants.PIECE_VALS[constants.PAWN] * (wpCount - bpCount) +
        constants.PIECE_VALS[constants.KNIGHT] * (wnCount - bnCount) + constants.PIECE_VALS[constants.BISHOP] * (wbCount - bbCount) +
        constants.PIECE_VALS[constants.ROOK] * (wrCount - brCount) + constants.PIECE_VALS[constants.QUEEN] * (wqCount - bqCount));
    }

    public static int bishopPairEval(int wbCount, int bbCount, int wpCount, int bpCount) {
        int bishopPairScore = 0;
        if (wbCount >= 2) { bishopPairScore = bishopPairScore + BISHOP_PAIR_VS_NUM_PAWNS[wpCount]; }
        if (bbCount >= 2) { bishopPairScore = bishopPairScore - BISHOP_PAIR_VS_NUM_PAWNS[bpCount]; }
        return bishopPairScore;
    }
}
