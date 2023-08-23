package chess.eval;
import chess.bitboard;

public class material {

    public static int pcCount(long pcBB) {
        int pcCount = 0;
        for (int i = Long.numberOfTrailingZeros(pcBB); i < 64 - Long.numberOfLeadingZeros(pcBB); i++) {
            if ((pcBB>>>i & 1) == 1) {pcCount++;}
        } return pcCount;
    }
    /*public static int matCount(int turn, long wp, long wn, long wb, long wr, long wq, long bp, long bn, long bb, long br,
                               long bq) {
        return ((100 * (pcCount(wp) - pcCount(bp)) + 350 * (pcCount(wn) - pcCount(bn)) + 350 * (pcCount(wb) - pcCount(bb)) +
                525 * (pcCount(wr) - pcCount(br)) + 1000 * (pcCount(wq) - pcCount(bq))) * turn);
    }*/

    public static int matCount(bitboard btb) {
        return ((100 * (btb.wpCount - btb.bpCount) + 350 * (btb.wnCount - btb.bnCount) + 350 * (btb.wbCount - btb.bbCount) +
                525 * (btb.wrCount - btb.brCount) + 1000 * (btb.wqCount - btb.bqCount)) * btb.turn);
    }
}
