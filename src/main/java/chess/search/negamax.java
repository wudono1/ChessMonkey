package chess.search;

import chess.bitboard;
import chess.eval.evaluation;
import chess.move;
import chess.moveGen;

import java.util.*;
public class negamax {
    bitboard btb = new bitboard();
    moveGen mover = new moveGen();

    int searchDepth = 7;
    move bestMove = new move(-1, 1, -1, -1);

    public void setBitboardFEN(String FEN) {
        btb.setBitboardPos(FEN);
    }

    public move returnBestMove() {return bestMove; }

    public int negamaxEval() { //negamax caller
        int alpha = -1000000;
        int beta = 1000000;
        return negamaxFunction(0, alpha, beta);
    }

    public int negamaxFunction(int depth, int alpha, int beta) {

        ArrayList<move> moveList = mover.moveGenerator(btb.wp, btb.wn, btb.wb, btb.wr, btb.wq, btb.wk, btb.bp, btb.bn,
                btb.bb, btb.br, btb.bq, btb.bk, btb.turn, btb.wCastle, btb.bCastle, btb.lastPawnJump);
        if (moveList.isEmpty()) {
            switch (btb.turn) {
                case (1) -> {
                    if (mover.squareInCheck(Long.numberOfTrailingZeros(btb.wk), btb.bp, btb.bn, btb.bb, btb.br, btb.bq, btb.bk, btb.turn)) {
                        return -20000 + depth * 100; //encourages bot to go for longer forced mates
                    } else {
                        return 0;
                    }
                }
                case (-1) -> {
                    if (mover.squareInCheck(Long.numberOfTrailingZeros(btb.bk), btb.wp, btb.wn, btb.wb, btb.wr, btb.wq, btb.wk, btb.turn)) {
                        return -20000 + depth * 100;
                    } else {
                        return 0;
                    }
                }
            }
        }

        if (depth == searchDepth) {
            return evaluation.totalEval(btb.turn, btb.wp, btb.wn, btb.wb, btb.wr, btb.wq, btb.wk, btb.bp, btb.bn,
                    btb.bb, btb.br, btb.bq, btb.bk);
        }

        for (move m : moveList) {
            btb.makeMove(m);
            int score = -negamaxFunction(depth + 1, -beta, -alpha);
            btb.unmakeMove1Ply();
            if (score >= beta) {
                return beta;
            }
            if (score > alpha) {
                alpha = score;
                if (depth == 0) {
                    bestMove = m;
                }

            }
        }
        return alpha;
    }

    public static void main(String[] args) {
        negamax searcher = new negamax();
        System.out.println(searcher.negamaxEval());
        System.out.println(searcher.bestMove);
    }


}