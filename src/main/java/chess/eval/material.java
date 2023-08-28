package chess.eval;
import chess.bitboard;

public class material {

    public static final int[] BISHOP_PAIR_VS_NUM_PAWNS = {30, 34, 40, 50, 56, 58, 55, 49, 39};

    public static int totalMatScore(int wpCount, int wnCount, int wbCount, int wrCount, int wqCount,
                                    int bpCount, int bnCount, int bbCount, int brCount, int bqCount, int turn) {
        return (matCount(wpCount, wnCount, wbCount, wrCount, wqCount, bpCount, bnCount, bbCount, brCount, bqCount) +
                bishopPairEval(wbCount, bbCount, wpCount, bpCount)) * turn;
    }

    public static int matCount(int wpCount, int wnCount, int wbCount, int wrCount, int wqCount,
                               int bpCount, int bnCount, int bbCount, int brCount, int bqCount) {
        return (100 * (wpCount - bpCount) + 350 * (wnCount - bnCount) + 350 * (wbCount - bbCount) +
                525 * (wrCount - brCount) + 1000 * (wqCount - bqCount));
    }

    public static int bishopPairEval(int wbCount, int bbCount, int wpCount, int bpCount) {
        int bishopPairScore = 0;
        if (wbCount >= 2) { bishopPairScore = bishopPairScore + BISHOP_PAIR_VS_NUM_PAWNS[wpCount]; }
        if (bbCount >= 2) { bishopPairScore = bishopPairScore - BISHOP_PAIR_VS_NUM_PAWNS[bpCount]; }
        return bishopPairScore;
    }
}
