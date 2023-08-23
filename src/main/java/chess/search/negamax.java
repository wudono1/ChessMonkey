package chess.search;

import chess.bitboard;
import chess.eval.evaluation;
import chess.moveGen;
import chess.notationKey;
import java.util.*;
public class negamax {

    moveGen mover = new moveGen();
    final int MATE_SCORE = -25000;
    int bestMoveCurrentDepth = -1; //best move at current depth
    int bestEvalCurrentDepth;
    int bestMoveOverall; //best move after all searching
    int bestEvalOverall = -32000;
    transpositionTable tt = new transpositionTable();
    int currentMaxSearchDepth = 4;
    int maxSearchDepth = 6;

    int nodesSearched = 0;
    int nodesFromTT = 0;

    public void getTTSize() {
        System.out.println("Number of position evals in transposition table: " + tt.numTTElements());
    }

    public int negamaxSearch(bitboard btb) { //negamax caller
        //btb.setBitboardPos(testPos);
        int alpha = -32000;
        int beta = 32000;
        return negamaxFunction(btb, 0, alpha, beta);
    }

    public int iterativeDeepeningSearch(bitboard btb) {
        for (int i = 1; i <= maxSearchDepth; i++) {
            currentMaxSearchDepth = i;
            negamaxFunction(btb, 0, -32000, 32000);
            if (bestEvalCurrentDepth > bestEvalOverall) {
                bestEvalOverall = bestEvalCurrentDepth;
                bestMoveOverall = bestMoveCurrentDepth;
            }
        }
        return bestEvalOverall;
    }

    public int negamaxFunction(bitboard btb, int currentDepthSearched, int alpha, int beta) {
        if (btb.plyCount_50Move == 100) {
            return 0;
        }
        if (currentDepthSearched == currentMaxSearchDepth) {
            return quiescenceSearch(btb, alpha, beta, currentDepthSearched);
        }
        ArrayList<Integer> allMoves = new ArrayList<>();
        ArrayList<Integer> captures = new ArrayList<>();
        ArrayList<Integer> quietMoves = new ArrayList<>();
        ArrayList<Integer> checks = new ArrayList<>();
        mover.generateAllLegalMoves(btb.wp, btb.wn, btb.wb, btb.wr, btb.wq, btb.wk, btb.bp, btb.bn,
                btb.bb, btb.br, btb.bq, btb.bk, btb.turn, btb.wCastle, btb.bCastle, btb.lastPawnJump,
                captures, quietMoves, checks);
        allMoves.addAll(captures); allMoves.addAll(checks); allMoves.addAll(quietMoves);
        if (allMoves.isEmpty()) {
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

        for (int m : allMoves) {
            boolean drawByRep = btb.makeMove(m);
            if (drawByRep) {return 0;}
            int score = tt.returnPastEval(btb.currentZobrist, currentDepthSearched, currentMaxSearchDepth - currentDepthSearched,
                    alpha, beta);
            if (score != tt.lookupFailed) {nodesFromTT++;}
            if (score == tt.lookupFailed) {
                nodesSearched++;
                score = -negamaxFunction(btb, currentDepthSearched + 1, -beta, -alpha);}

            //int score = -negamaxFunction(depth + 1, -beta, -alpha);
            tt.addEval(btb.currentZobrist, score, alpha, beta, m, (currentMaxSearchDepth - currentDepthSearched));
            if (score >= beta) {
                btb.unmakeMove1Ply();
                return beta;
            }
            btb.unmakeMove1Ply();
            if (score > alpha) {
                alpha = score;
                if (currentDepthSearched == 0) {
                    bestMoveCurrentDepth = m;
                    bestEvalCurrentDepth = score;
                }
            }
        }
        return alpha;
    }

    public int quiescenceSearch(bitboard btb, int alpha, int beta, int quiescentStartDepth){
        //simple quiescent search
        //preQuiescentEval = evaluation before quiescent search began, in case captures/checks make position worse
        ArrayList<Integer> captures = new ArrayList<>();
        ArrayList<Integer> quietMoves = new ArrayList<>();
        ArrayList<Integer> checks = new ArrayList<>();
        mover.generateAllLegalMoves(btb.wp, btb.wn, btb.wb, btb.wr, btb.wq, btb.wk, btb.bp, btb.bn,
                btb.bb, btb.br, btb.bq, btb.bk, btb.turn, btb.wCastle, btb.bCastle, btb.lastPawnJump,
                captures, quietMoves, checks);
        if (captures.isEmpty() && checks.isEmpty() && quietMoves.isEmpty()) {
            switch (btb.turn) {
                case (1) -> {
                    if (mover.squareInCheck(Long.numberOfTrailingZeros(btb.wk), btb.bp, btb.bn, btb.bb, btb.br, btb.bq, btb.bk, btb.turn)) {
                        return MATE_SCORE + quiescentStartDepth * 10; //encourages engine to go for longer forced mates
                    } else {
                        return 0;
                    }
                }
                case (-1) -> {
                    if (mover.squareInCheck(Long.numberOfTrailingZeros(btb.bk), btb.wp, btb.wn, btb.wb, btb.wr, btb.wq, btb.wk, btb.turn)) {
                        return MATE_SCORE + quiescentStartDepth * 10;
                    } else {
                        return 0;
                    }
                }
            }
        }
        /*evaluate current position first. If not capturing is better than capturing, then alpha = currentpositioneval
        after looking for capture moves
         */
        int currentPosEval = evaluation.totalEval(btb);
        if (currentPosEval >= beta) { return beta;}
        if (currentPosEval > alpha) {alpha = currentPosEval;}
        for (int m : captures) {
            btb.makeMove(m);
            int score = - quiescenceSearch(btb, -beta, -alpha, quiescentStartDepth + 1);
            btb.unmakeMove1Ply();
            if (score >= beta) {
                return beta;
            }
            if (score > alpha) {
                alpha = score;
            }
        }
        return alpha;
    }

    public static void main(String[] args) {
        bitboard btb = new bitboard();
        negamax searcher = new negamax();
        System.out.println("Eval: " + searcher.iterativeDeepeningSearch(btb));
        System.out.println("Depth searched: " + searcher.currentMaxSearchDepth);
        System.out.println("Best move: " + notationKey.SQKEY.get(searcher.bestMoveOverall & 0x3F) +
                notationKey.SQKEY.get(searcher.bestMoveOverall >>> 6 & 0x3F));
        System.out.println("16 bit move notation: " + searcher.bestMoveOverall);

        System.out.println();

        System.out.println("Total move sequences possible: 3,195,901,860");
        System.out.println("Number positions evaluated: " + searcher.nodesSearched);
        searcher.getTTSize();
        System.out.println("Number of times transposition table used: " + searcher.nodesFromTT);
    }


}
