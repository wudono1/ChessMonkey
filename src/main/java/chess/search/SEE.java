package chess.search;
import chess.moveGen;
import chess.constants;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class SEE {
    public static void getSEEMoves(int move, long tp, long tn, long tb, long tr, long tq, long tk,
                                            long ep, long en, long eb, long er, long eq, long ek, int turn) {
        int totalSEEScore = 0;
        int initialAttackStart = move & 0b111111;
        int squareSEE = move >>> 6 & 0b111111;
        ArrayList<Integer> turnAttackStartSqs = new ArrayList<>();
        ArrayList<Integer> turnAttackerPcType = new ArrayList<>();
        ArrayList<Integer> enemyAttackStartSqs = new ArrayList<>();
        ArrayList<Integer> enemyAttackerPcType = new ArrayList<>();

        turnAttackStartSqs.add(initialAttackStart); //add initial attacker's start square
        if ((tp >>> initialAttackStart & 1) == 1) {turnAttackerPcType.add(constants.PAWN);}
        if ((tn >>> initialAttackStart & 1) == 1) {turnAttackerPcType.add(constants.KNIGHT);}
        if ((tb >>> initialAttackStart & 1) == 1) {turnAttackerPcType.add(constants.BISHOP);}
        if ((tr >>> initialAttackStart & 1) == 1) {turnAttackerPcType.add(constants.ROOK);}
        if ((tq >>> initialAttackStart & 1) == 1) {turnAttackerPcType.add(constants.QUEEN);}
        if ((tk >>> initialAttackStart & 1) == 1) {turnAttackerPcType.add(constants.KING);}

        if ((ep >>> squareSEE & 1) == 1) {totalSEEScore += constants.PIECE_VALS[constants.PAWN];}
        if ((en >>> squareSEE & 1) == 1) {totalSEEScore += constants.PIECE_VALS[constants.KNIGHT];;}
        if ((eb >>> squareSEE & 1) == 1) {totalSEEScore += constants.PIECE_VALS[constants.BISHOP];;}
        if ((er >>> squareSEE & 1) == 1) {totalSEEScore += constants.PIECE_VALS[constants.ROOK];;}
        if ((eq >>> squareSEE & 1) == 1) {totalSEEScore += constants.PIECE_VALS[constants.QUEEN];;}

    }
}
