package chess.eval;
import chess.bitboard;

public class evaluation {
    public static int totalEval(bitboard btb) {
        int score = 0;
        score = score + material.totalMatScore(btb.wpCount, btb.wnCount, btb.wbCount, btb.wrCount, btb.wqCount,
                btb.bpCount, btb.bnCount, btb.bbCount, btb.brCount, btb.bqCount, btb.turn);
        score = score + (PST.pawnEval(btb.wp, btb.bp) + PST.knightEval(btb.wn, btb.bn) + PST.bishopEval(btb.wb, btb.bb) +
                PST.rookEval(btb.wr, btb.br) + PST.queenEval(btb.wq, btb.bq) + PST.kingEval(btb.wk, btb.bk)) * btb.turn;
        return score;
    }
}
