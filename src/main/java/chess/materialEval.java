package chess;

public class materialEval {
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
