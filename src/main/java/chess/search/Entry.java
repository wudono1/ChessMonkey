package chess.search;
import chess.move;

public class Entry {
    public int depth; //4bytes
    public short eval; //4bytes
    public long zKey; //8bytes
    public move bestMove; //16 bytes == 30bytes total
    public short flag;
    /*
    key for bounds-
    0: eval <= alpha cutoff at position; 1: alpha <= eval <= beta at position. Eval score may not be accurate
    2: alpha <= eval <= beta at position. Eval value is accurate
    3: beta <= eval at position. Eval score may not be accurate
    If bounds = 0 or bounds = 2 AND bounds is within current alpha-beta range, then position must be reevaluated
     */

    //May change move to 16bit system

    public Entry() {
        this.depth = -1;
        this.eval = -30000;
        this.zKey = 0;
        this.bestMove = new move(-1, -1, -1, -1);
        this.flag = -1;
    }
    public Entry( long zKey, int eval, int bounds, int depth, move bestMove) {
        this.depth = depth;
        this.eval = (short)(eval);
        this.zKey = zKey;
        this.bestMove = bestMove;
        this.flag = (short)(bounds);
    }
}
