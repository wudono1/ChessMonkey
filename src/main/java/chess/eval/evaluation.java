package chess.eval;

public class evaluation {
    public static int totalEval(int turn, long wp, long wn, long wb, long wr, long wq, long wk, long bp, long bn,
                                long bb, long br, long bq, long bk) {
        int score = 0;
        score = score + matCount(turn, wp, wn, wb, wr, wq, bp, bn, bb, br, bq);
        score = score + (PSTables.pawnEval(wp, bp) + PSTables.knightEval(wn, bn) + PSTables.bishopEval(wb, bb) +
                PSTables.rookEval(wr, br) + PSTables.queenEval(wq, bq) + PSTables.kingEval(wk, bk)) * turn;
        return score;
    }

    public static int pcCount(long pcBB) {
        int pcCount = 0;
        for (int i = Long.numberOfTrailingZeros(pcBB); i < 64 - Long.numberOfLeadingZeros(pcBB); i++) {
            if ((pcBB>>>i & 1) == 1) {pcCount++;}
        } return pcCount;
    }
    public static int matCount(int turn, long wp, long wn, long wb, long wr, long wq, long bp, long bn, long bb, long br,
                        long bq) {
        return ((100 * (pcCount(wp) - pcCount(bp)) + 350 * (pcCount(wn) - pcCount(bn)) + 350 * (pcCount(wb) - pcCount(bb)) +
                525 * (pcCount(wr) - pcCount(br)) + 1000 * (pcCount(wq) - pcCount(bq))) * turn);
    }
}
