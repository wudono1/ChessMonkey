package chess;
import java.util.ArrayList;
import static chess.notationKey.SQKEY;
public class perftTest {
    bitboard btb = new bitboard("rnbqkbnr/ppp1pppp/8/3p4/8/6P1/PPPPPP1P/RNBQKBNR w KQkq - 0 2");
    moveGen mover = new moveGen();

    public long perft(int depth) { //calls perft method
        btb.printArrayBoard();
        return perftAlg(depth, depth);
    }
    public long perftAlg(int depth, int currentDepth) { // 1 depth = 1ply
        long nodes = 0;
        if (currentDepth == 0) { return 1L; }
        else {
            ArrayList<move> moveList = mover.moveGenerator(btb.wp, btb.wn, btb.wb, btb.wr, btb.wq, btb.wk, btb.bp, btb.bn,
                    btb.bb, btb.br, btb.bq, btb.bk, btb.turn, btb.wCastle, btb.bCastle, btb.lastPawnJump);
            for (move m : moveList) {
                btb.makeMove(m);
                long nodeForMove = perftAlg(depth, currentDepth - 1);
                nodes = nodes + nodeForMove;
                if (currentDepth == depth) {
                    System.out.printf("%s%s: %d%n", SQKEY.get(m.start), SQKEY.get(m.dest), nodeForMove);
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