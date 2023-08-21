package chess.testing;
import chess.bitboard;
import chess.moveGen;

import java.util.ArrayList;
import static chess.notationKey.SQKEY;
public class perftTest {
    bitboard btb = new bitboard();
    moveGenTesting mover = new moveGenTesting();

    public long perft(int depth) { //calls perft method
        btb.setBitboardPos("rnbqkbnr/pppppppp/8/8/8/7P/PPPPPPP1/RNBQKBNR b KQkq - 0 1");
        btb.printArrayBoard();
        return perftAlg(depth, depth);
    }
    public long perftAlg(int depth, int currentDepth) { // 1 depth = 1ply
        long nodes = 0;
        if (currentDepth == 0) { return 1L; }
        else {
            mover.moveGenerator(btb.wp, btb.wn, btb.wb, btb.wr, btb.wq, btb.wk, btb.bp, btb.bn,
                    btb.bb, btb.br, btb.bq, btb.bk, btb.turn, btb.wCastle, btb.bCastle, btb.lastPawnJump);
            ArrayList<Integer> moveList = mover.getAllMoves();
            for ( int m : moveList) {
                btb.makeMove(m);
                if (currentDepth == depth) {
                    btb.printArrayBoard();
                    System.out.printf("%s%s: ", SQKEY.get(m & 0b111111), SQKEY.get(m >>> 6 & 0b111111));
                }
                long nodeForMove = perftAlg(depth, currentDepth - 1);
                nodes = nodes + nodeForMove;
                if (currentDepth == depth) {
                    System.out.printf("%d%n", nodeForMove);
                }
                btb.unmakeMove1Ply();
            }
        }
        return nodes;
    }
    public static void main(String[] args) {

        perftTest pt = new perftTest();
        int d = 2;
        long numNodes = pt.perft(d);
        System.out.printf("Total nodes at depth %d ply: %d%n", d, numNodes);
    }
}
