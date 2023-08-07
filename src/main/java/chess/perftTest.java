package chess;
import java.util.ArrayList;

public class perftTest {
    bitboard btb = new bitboard();
    moveGen mover = new moveGen();

    public int perft(int depth) {
        return perftAlg(depth, depth);
    }
    public int perftAlg(int depth, int currentDepth) { // 1 depth = 1ply
        int nodes = 0;
        if (currentDepth == 0) { return 1; }
        else {
            ArrayList<move> moveList = mover.moveGenerator(btb.wp, btb.wn, btb.wb, btb.wr, btb.wq, btb.wk, btb.bp, btb.bn,
                    btb.bb, btb.br, btb.bq, btb.bk, btb.turn, btb.wCastle, btb.bCastle, btb.lastPawnJump);
            for (int i = 0; i < moveList.size(); i++) {
                btb.makeMove(moveList.get(i));
                nodes = nodes + perftAlg(depth, currentDepth - 1);
                btb.unmakeMove1Ply();
            }
        }
        return nodes;
    }
    public static void main(String[] args) {
        perftTest pt = new perftTest();
        int numNodes = pt.perft(1);
        System.out.println(numNodes);
    }
}
