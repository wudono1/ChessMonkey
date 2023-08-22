package chess.testing;
import chess.bitboard;

import java.util.ArrayList;
import static chess.notationKey.SQKEY;
public class perftTest {
    bitboard btb = new bitboard();
    moveGenTesting2 mover = new moveGenTesting2();

    public long perft(int depth) { //calls perft method
        btb.printArrayBoard();
        return perftAlg(depth, depth);
    }
    public long perftAlg(int depth, int currentDepth) { // 1 depth = 1ply
        long nodes = 0;
        if (currentDepth == 0) { return 1L; }
        else {
            ArrayList<Integer> allMoves = new ArrayList<>();
            ArrayList<Integer> captures = new ArrayList<>();
            ArrayList<Integer> quietMoves = new ArrayList<>();
            ArrayList<Integer> checks = new ArrayList<>();
            mover.generateAllLegalMoves(btb.wp, btb.wn, btb.wb, btb.wr, btb.wq, btb.wk, btb.bp, btb.bn,
                    btb.bb, btb.br, btb.bq, btb.bk, btb.turn, btb.wCastle, btb.bCastle, btb.lastPawnJump,
                    captures, quietMoves, checks);
            allMoves.addAll(quietMoves); allMoves.addAll(checks); allMoves.addAll(captures);
            for ( int m : allMoves) {
                btb.makeMove(m);
                long nodeForMove = perftAlg(depth, currentDepth - 1);
                nodes = nodes + nodeForMove;
                if (currentDepth == depth) {
                    System.out.printf("%s%s: %d%n", SQKEY.get(m & 0b111111), SQKEY.get(m >>> 6 & 0b111111), nodeForMove);
                }
                btb.unmakeMove1Ply();
            }
        }
        return nodes;
    }
    public static void main(String[] args) {

        perftTest pt = new perftTest();
        int d = 5;
        long numNodes = pt.perft(d);
        System.out.printf("Total nodes at depth %d ply: %d%n", d, numNodes);


    }
}
