package chess.eval;

public class evaluation {
    public static int totalEval(int turn, long wp, long wn, long wb, long wr, long wq, long wk, long bp, long bn,
                                long bb, long br, long bq, long bk) {
        int score = 0;
        score = score + material.matCount(turn, wp, wn, wb, wr, wq, bp, bn, bb, br, bq);
        score = score + (PSTables.pawnEval(wp, bp) + PSTables.knightEval(wn, bn) + PSTables.bishopEval(wb, bb) +
                PSTables.rookEval(wr, br) + PSTables.queenEval(wq, bq) + PSTables.kingEval(wk, bk)) * turn;
        return score;
    }
}
