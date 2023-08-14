package chess;

import chess.eval.evaluation;

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

    public int negamaxEval(int depth) { //negamax caller
        originalDepth = depth;
        int alpha = -10000000; //10 million
        int beta = 10000000;
        return negamaxFunction(depth, alpha, beta);
    }

    public int negamaxFunction(int depth, int alpha, int beta) {

        if (depth == 0) {
            return evaluation.totalEval(btb.turn, btb.wp, btb.wn, btb.wb, btb.wr, btb.wq, btb.wk, btb.bp, btb.bn,
                    btb.bb, btb.br, btb.bq, btb.bk); }
        ArrayList<move> moveList = mover.moveGenerator(btb.wp, btb.wn, btb.wb, btb.wr, btb.wq, btb.wk, btb.bp, btb.bn,
                btb.bb, btb.br, btb.bq, btb.bk, btb.turn, btb.wCastle, btb.bCastle, btb.lastPawnJump);
        /*if (depth == originalDepth) {
            System.out.print(btb.turn + "'s turn at depth " + depth + ": ");
            int c = 0;
            for (move mv : moveList) {
                System.out.print(mv + ", ");
                c++;
                if (c % 10 == 0) {
                    System.out.println();
                }
            }
            System.out.println();
        }*/

        if (moveList.isEmpty()) {
            if (btb.turn == 1) {
                if (mover.squareInCheck(Long.numberOfTrailingZeros(btb.wk), btb.bp, btb.bn, btb.bb, btb.br, btb.bq, btb.bk, btb.turn)) {
                    return -100000;
                } else {return 0;}
            }
            if (btb.turn == -1) {
                if (mover.squareInCheck(Long.numberOfTrailingZeros(btb.bk), btb.wp, btb.wn, btb.wb, btb.wr, btb.wq, btb.wk, btb.turn)) {
                    return -100000;
                } else {return 0;}
            }
        }

        for (move m : moveList) {
            btb.makeMove(m);
            int score = -negamaxFunction(depth- 1, -beta, -alpha);
            btb.unmakeMove1Ply();
            if (score >= beta) {
                return beta; }
            if (score > alpha) {
                alpha = score;
                bestMove = m;
                System.out.println("depth: " + depth + ", Move: " + bestMove + " turn: " + btb.turn);
            }

        }
        return alpha;
    }

    public static void main(String[] args) {
        negamax searcher = new negamax();
        System.out.println(searcher.negamaxEval(3));
    }



}
