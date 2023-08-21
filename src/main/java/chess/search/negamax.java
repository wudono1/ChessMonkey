package chess.search;

import chess.bitboard;
import chess.eval.evaluation;
import chess.moveGen;
import chess.notationKey;
import java.util.*;
public class negamax {
    String testPos = "rnbqkbnr/pppppppp/8/8/8/5N2/PPPPPPPP/RNBQKB1R b KQkq - 1 1";
    public bitboard btb = new bitboard();

    moveGen mover = new moveGen();
    final int MATE_SCORE = -25000;
    int bestMoveCurrentDepth = -1; //best move at current depth
    int bestEvalCurrentDepth;
    int bestMoveOverall = -1; //best move after all searching
    int bestEvalOverall;
    transpositionTable tt = new transpositionTable();
    int currentMaxSearchDepth = 3;
    int maxSearchDepth = 6;


    public int negamaxEval() { //negamax caller
        //btb.setBitboardPos(testPos);
        int alpha = -32000;
        int beta = 32000;
        return negamaxFunction(0, alpha, beta);
    }

    public int iterativeDeepeningSearch() {
        for (int i = 1; i <= maxSearchDepth; i++) {
            currentMaxSearchDepth = i;
            negamaxFunction(0, -32000, 32000);
        }
        return bestEvalOverall;
    }

    public int negamaxFunction(int currentDepthSearched, int alpha, int beta) {
        if (btb.plyCount_50Move == 100) {
            return 0;
        }

        ArrayList<Integer> moveList = mover.moveGenerator(btb.wp, btb.wn, btb.wb, btb.wr, btb.wq, btb.wk, btb.bp, btb.bn,
                btb.bb, btb.br, btb.bq, btb.bk, btb.turn, btb.wCastle, btb.bCastle, btb.lastPawnJump);
        if (moveList.isEmpty()) {
            switch (btb.turn) {
                case (1) -> {
                    if (mover.squareInCheck(Long.numberOfTrailingZeros(btb.wk), btb.bp, btb.bn, btb.bb, btb.br, btb.bq, btb.bk, btb.turn)) {
                        return MATE_SCORE + currentDepthSearched * 10; //encourages engine to go for longer forced mates
                    } else {
                        return 0;
                    }
                }
                case (-1) -> {
                    if (mover.squareInCheck(Long.numberOfTrailingZeros(btb.bk), btb.wp, btb.wn, btb.wb, btb.wr, btb.wq, btb.wk, btb.turn)) {
                        return MATE_SCORE + currentDepthSearched * 10;
                    } else {
                        return 0;
                    }
                }
            }
        }

        if (currentDepthSearched == currentMaxSearchDepth) {
            return evaluation.totalEval(btb.turn, btb.wp, btb.wn, btb.wb, btb.wr, btb.wq, btb.wk, btb.bp, btb.bn,
                    btb.bb, btb.br, btb.bq, btb.bk);
        }
        for (int m : moveList) {
            boolean drawByRep = btb.makeMove(m);
            if (drawByRep) {return 0;}
            int score = tt.returnPastEval(btb.currentZobrist, currentDepthSearched, currentMaxSearchDepth - currentDepthSearched,
                    alpha, beta);
            if (score == tt.lookupFailed) {score = -negamaxFunction(currentDepthSearched + 1, -beta, -alpha);}
            //int score = -negamaxFunction(depth + 1, -beta, -alpha);
            tt.addEval(btb.currentZobrist, score, alpha, beta, m, (currentMaxSearchDepth - currentDepthSearched));
            if (score >= beta) {
                btb.unmakeMove1Ply();
                return beta;
            }
            btb.unmakeMove1Ply();
            if (score > alpha) {
                alpha = score;
                bestMoveCurrentDepth = m;
                bestEvalCurrentDepth = score;
                if (currentDepthSearched == 0) {
                    bestMoveOverall = bestMoveCurrentDepth;
                    bestEvalOverall = bestEvalCurrentDepth;
                }
            }
        }
        return alpha;
    }

    public static void main(String[] args) {
        negamax searcher = new negamax();
        System.out.println("Eval: " + searcher.negamaxEval());
        System.out.println("Best move: " + notationKey.SQKEY.get(searcher.bestMoveOverall & 0x3F) +
                notationKey.SQKEY.get(searcher.bestMoveOverall >>> 6 & 0x3F));
        System.out.println(searcher.bestMoveOverall);
    }


}
