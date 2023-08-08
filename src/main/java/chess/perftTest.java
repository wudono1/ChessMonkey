package chess;
import java.util.ArrayList;
import static chess.notationKey.SQKEY;
public class perftTest {
    bitboard btb = new bitboard();
    moveGen mover = new moveGen();

    public int perft(int depth) { //calls perft method
        return perftAlg(depth, depth);
    }
    public int perftAlg(int depth, int currentDepth) { // 1 depth = 1ply
        int nodes = 0;
        if (currentDepth == 0) { return 1; }
        else {
            ArrayList<move> moveList = mover.moveGenerator(btb.wp, btb.wn, btb.wb, btb.wr, btb.wq, btb.wk, btb.bp, btb.bn,
                    btb.bb, btb.br, btb.bq, btb.bk, btb.turn, btb.wCastle, btb.bCastle, btb.lastPawnJump);
            for (move m : moveList) {
                btb.makeMove(m);
                int nodeForMove = perftAlg(depth, currentDepth - 1);
                nodes = nodes + nodeForMove;
                if (currentDepth == depth) {
                    System.out.printf("%s %s: %d%n", SQKEY.get(m.start), SQKEY.get(m.dest), nodeForMove);
                }
                btb.unmakeMove1Ply();
            }
        }
        return nodes;
    }
    public static void main(String[] args) {

        perftTest pt = new perftTest();
        int d = 7;
        int numNodes = pt.perft(d);
        System.out.printf("Total Nodes Searched at depth %d ply: %d%n", d, numNodes);
    }
}
