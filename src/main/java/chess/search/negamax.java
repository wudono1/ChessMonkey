package chess.search;

import chess.bitboard;
import chess.eval.evaluation;
import chess.moveGen;
import chess.notationKey;
import java.util.*;
public class negamax {
    bitboard btb = new bitboard();
    moveGen mover = new moveGen();
    final int MATE_SCORE = -25000;
    int bestMoveOverall = -1; //best move after all searching
    int bestMoveCurrentDepth = -1; //best move at current depth
    transpositionTable tt = new transpositionTable();
    int currentMaxSearchDepth = 6;
    int maxSearchDepth = 6;

    int alpha = -32000;
    int beta = 32000;

    public int negamaxEval() { //negamax caller
        alpha = -32000;
        beta = 32000;
        return negamaxFunction(0, alpha, beta);
    }

    public int negamaxFunction(int depth, int alpha, int beta) {
        if (btb.plyCount_50Move == 100) {
            return 0;
        }

        ArrayList<Integer> moveList = mover.moveGenerator(btb.wp, btb.wn, btb.wb, btb.wr, btb.wq, btb.wk, btb.bp, btb.bn,
                btb.bb, btb.br, btb.bq, btb.bk, btb.turn, btb.wCastle, btb.bCastle, btb.lastPawnJump);
        if (moveList.isEmpty()) {
            switch (btb.turn) {
                case (1) -> {
                    if (mover.squareInCheck(Long.numberOfTrailingZeros(btb.wk), btb.bp, btb.bn, btb.bb, btb.br, btb.bq, btb.bk, btb.turn)) {
                        return MATE_SCORE + depth * 10; //encourages engine to go for longer forced mates
                    } else {
                        return 0;
                    }
                }
                case (-1) -> {
                    if (mover.squareInCheck(Long.numberOfTrailingZeros(btb.bk), btb.wp, btb.wn, btb.wb, btb.wr, btb.wq, btb.wk, btb.turn)) {
                        return MATE_SCORE + depth * 10;
                    } else {
                        return 0;
                    }
                }
            }
        }

        if (depth == currentMaxSearchDepth) {
            return evaluation.totalEval(btb.turn, btb.wp, btb.wn, btb.wb, btb.wr, btb.wq, btb.wk, btb.bp, btb.bn,
                    btb.bb, btb.br, btb.bq, btb.bk);
        }
        for (int m : moveList) {
            boolean drawByRep = btb.makeMove(m);
            if (drawByRep) {return 0;}
            int score = tt.returnPastEval(btb.currentZobrist, depth, currentMaxSearchDepth - depth, alpha, beta);
            if (score == tt.lookupFailed) {score = -negamaxFunction(depth + 1, -beta, -alpha);}
            tt.addEval(btb.currentZobrist, score, alpha, beta, bestMoveOverall, (currentMaxSearchDepth - depth));
            if (score >= beta) {
                return beta;
            }
            else if (score > alpha) {
                alpha = score;
                bestMoveCurrentDepth = m;
                if (depth == 0) {
                    bestMoveOverall = bestMoveCurrentDepth;
                }
            }
            btb.unmakeMove1Ply();
        }
        this.alpha = alpha;
        this.beta = beta;
        return alpha;
    }

    public static void main(String[] args) {
        negamax searcher = new negamax();
        System.out.println("Eval: " + searcher.negamaxEval());
        System.out.println("Best move: " + notationKey.SQKEY.get(searcher.bestMoveOverall & 0x3F) +
                notationKey.SQKEY.get(searcher.bestMoveOverall >>> 6 & 0x3F));
    }


}
