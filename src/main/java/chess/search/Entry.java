package chess.search;
import chess.move;

public class Entry {
    public int depth; //4bytes
    public int eval; //4bytes
    public long zKey; //8bytes
    public move bestMove; //16 bytes == 30bytes total
    //May change move to 16bit system

    public Entry(int depth, int eval, long zKey, move bestMove) {
        this.depth = depth;
        this.eval = eval;
        this.zKey = zKey;
        this.bestMove = bestMove;
    }
}
