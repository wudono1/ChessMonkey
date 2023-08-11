package chess;

import java.util.*;
public class negamax {
    bitboard btb = new bitboard();
    moveGen mover = new moveGen();
    int originalDepth;
    move bestMove = new move(-1, 1, -1, -1);

    public void setBitboardFEN(String FEN) {
        btb.setBitboardPos(FEN);
    }

    public move returnBestMove() {return bestMove; }

    public int negamax(int depth) { //negamax caller
        originalDepth = depth;
        int alpha = -10000000; //10 million
        int beta = 10000000;
        return negamaxFunction(depth, alpha, beta);
    }
    public int negamaxFunction(int depth, int alpha, int beta) {

        if (depth == 0) {
            return evaluation.matCount(btb.turn, btb.wp, btb.wn, btb.wb, btb.wr, btb.wq, btb.bp, btb.bn, btb.bb, btb.br, btb.bq); }
        ArrayList<move> moveList = mover.moveGenerator(btb.wp, btb.wn, btb.wb, btb.wr, btb.wq, btb.wk, btb.bp, btb.bn,
                btb.bb, btb.br, btb.bq, btb.bk, btb.turn, btb.wCastle, btb.bCastle, btb.lastPawnJump);
        for (move m : moveList) {
            btb.makeMove(m);
            int score = -negamaxFunction(depth- 1, -beta, -alpha);
            if (score > alpha) {
                    alpha = score;
                    bestMove = m;
            }
            if (score >= beta) { return beta; }
            btb.unmakeMove1Ply();
        }
        return alpha;
    }

    public static void main(String[] args) {
        negamax searcher = new negamax();
        System.out.println(searcher.negamax(2));
        move best = searcher.returnBestMove();
        System.out.println(best);
    }



}
