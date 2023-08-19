package chess.search;

public class Entry {
    public long zKey; //8bits
    public int evalAndMove; //4bits
    public short depth; //2bytes
    public byte flag; //1byte = 16 bytes
    /*
    key for bounds-
    0: eval <= alpha cutoff at position; 1: alpha <= eval <= beta at position. Eval score may not be accurate
    2: alpha <= eval <= beta at position. Eval value is accurate
    3: beta <= eval at position. Eval score may not be accurate
    If bounds = 0 or bounds = 2 AND bounds is within current alpha-beta range, then position must be reevaluated

    Alternative instantiation:
    long evalInfo: 0000 0000 0000 0000 0000 | 0000 | 0000 0000 | 0000 0000 0000 0000 | 0000 0000 0000 0000
                                                                                       16 bits for best move
                                                                 16 bits for eval score
                                                     8 bits depth
                                              2 bits flag
    public long zKey; //8bytes
    public int depth; //4bytes, depth searched starting from current position
    public short eval; //2bytes
    public int bestMove; //4 bytes == 30bytes total
    public short flag;
     */

    public Entry() {
        this.zKey = -1;
        this.evalAndMove = -1;
        this.depth = -1;
        this.flag = -1;
    }
    public Entry( long zKey, int eval, short depth, int bestMove, byte flag) {
        this.zKey = zKey;
        this.evalAndMove = bestMove | (eval << 16);
        this.depth = depth;
        this.flag = flag;
    }
    public void changeEvals(int eval, int bestMove, short depth, byte flag) {
        this.evalAndMove = bestMove | (eval << 16);
        this.depth = depth;
        this.flag = flag;
    }
}
