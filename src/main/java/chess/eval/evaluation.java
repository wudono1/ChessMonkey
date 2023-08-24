package chess.eval;
import chess.bitboard;

public class evaluation {
    public static int totalEval(bitboard btb) {
        int score = 0;
        score = score + material.totalMatScore(btb.wpCount, btb.wnCount, btb.wbCount, btb.wrCount, btb.wqCount,
                btb.bpCount, btb.bnCount, btb.bbCount, btb.brCount, btb.bqCount, btb.turn);
        score = score + (PSTables.pawnEval(btb.wp, btb.bp) + PSTables.knightEval(btb.wn, btb.bn) + PSTables.bishopEval(btb.wb, btb.bb) +
                PSTables.rookEval(btb.wr, btb.br) + PSTables.queenEval(btb.wq, btb.bq) + PSTables.kingEval(btb.wk, btb.bk)) * btb.turn;
        return score;
    }
}
