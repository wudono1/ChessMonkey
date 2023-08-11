package chess;

import java.util.*;
public class search {
    bitboard btb = new bitboard();
    moveGen mover = new moveGen();
    int originalDepth;
    move bestMove = new move(-1, 1, -1, -1);

    public void setBitboardFEN(String FEN) {
        btb.setBitboardPos(FEN);
    }

    public move returnBestMove() {return bestMove; }

    public int negamax(int depth) {
        originalDepth = depth;
        return negamaxFunction(depth);
    }
    public int negamaxFunction(int depth) {
        int max = Integer.MIN_VALUE;

        if (depth == 0) {
            return materialEval.matCount(btb.turn, btb.wp, btb.wn, btb.wb, btb.wr, btb.wq, btb.bp, btb.bn, btb.bb, btb.br, btb.bq); }
        ArrayList<move> moveList = mover.moveGenerator(btb.wp, btb.wn, btb.wb, btb.wr, btb.wq, btb.wk, btb.bp, btb.bn,
                btb.bb, btb.br, btb.bq, btb.bk, btb.turn, btb.wCastle, btb.bCastle, btb.lastPawnJump);
        for (move m : moveList) {
            btb.makeMove(m);
            int score = -negamaxFunction(depth- 1);
            if (score > max) {
                max = score;
                if (depth == originalDepth) { bestMove = m;}
            }
            btb.unmakeMove1Ply();
        }
        return max;
    }

    public static void main(String[] args) {
        search searcher = new search();
        System.out.println(searcher.negamax(5));
        move best = searcher.returnBestMove();
        System.out.println(best);
        System.out.println();
    }



}
