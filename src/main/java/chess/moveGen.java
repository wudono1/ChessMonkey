package chess;

import java.util.*;

/*
   8 [ 63, 62, 61, 60, 59, 58, 57, 56,
   7 | 55, 54, 53, 52, 51, 50, 49, 48,
   6 | 47, 46, 45, 44, 43, 42, 41, 40,
   5 | 39, 38, 37, 36, 35, 34, 33, 32,
   4 | 31, 30, 29, 28, 27, 26, 25, 24,
   3 | 23, 22, 21, 20, 19, 18, 17, 16,
   2 | 15, 14, 13, 12, 11, 10, 9,  8,
   1 | 7,  6,  5,  4,  3,  2,  1,  0 ]   BIG ENDIAN
       a   b   c   d   e   f   g   h
     */


@SuppressWarnings({"SpellCheckingInspection", "GrazieInspection"})
public class moveGen {
    static long turnPieces; //All pieces of current side to play except king
    static long enemyPieces; //All pieces of enemy side except king
    static long empty; //All empty squares
    static long turnKing;
    static long enemyKing;
    static long occupied;

    //FILES
    static final long A_FILE = 0B1000000010000000100000001000000010000000100000001000000010000000L;
    static final long H_FILE = 0B0000000100000001000000010000000100000001000000010000000100000001L;
    static final long AB_FILE = 0B1100000011000000110000001100000011000000110000001100000011000000L;
    static final long GH_FILE = 0B0000001100000011000000110000001100000011000000110000001100000011L;
    static final long NOT_A = ~A_FILE;
    static final long NOT_H = ~H_FILE;
    static final long NOT_AB = ~AB_FILE;
    static final long NOT_GH = ~GH_FILE;

    //RANKS
    static final long RANK_1 = 0B0000000000000000000000000000000000000000000000000000000011111111L;
    static final long RANK_2 = 0B0000000000000000000000000000000000000000000000001111111100000000L;
    static final long RANK_4 = 0B0000000000000000000000000000000011111111000000000000000000000000L;
    static final long RANK_5 = 0B0000000000000000000000001111111100000000000000000000000000000000L;
    static final long RANK_7 = 0B0000000011111111000000000000000000000000000000000000000000000000L;
    static final long RANK_8 = 0B1111111100000000000000000000000000000000000000000000000000000000L;
    static final long RANK_12 = 0B0000000000000000000000000000000000000000000000001111111111111111L;
    static final long RANK_78 = 0B1111111111111111000000000000000000000000000000000000000000000000L;
    static final long NOT_12 = ~RANK_12;
    static final long NOT_78 = ~RANK_78;

    static final long[] RANKS = //rank 1 to rank 8
            { 0xFFL, 0xFF00L, 0xFF0000L, 0xFF000000L, 0xFF00000000L, 0xFF0000000000L, 0xFF000000000000L, 0xFF00000000000000L};
    static final long[] FILES = //h file to a file. Remainder 0 = h file, remainder 7 = a file
            { 0x0101010101010101L, 0x0202020202020202L, 0x0404040404040404L, 0x0808080808080808L, 0x1010101010101010L,
                    0x2020202020202020L, 0x4040404040404040L, 0x8080808080808080L};
    static long[] TR_BL_DIAGS =/*from top right to bottom left*/
            //index [(s / 8) + (s % 8)]
            { 0x1L, 0x102L, 0x10204L, 0x1020408L, 0x102040810L, 0x10204081020L, 0x1020408102040L, 0x102040810204080L,
                    0x204081020408000L, 0x408102040800000L, 0x810204080000000L, 0x1020408000000000L,
                    0x2040800000000000L, 0x4080000000000000L, 0x8000000000000000L};
    static final long[] TL_BR_DIAGS =
            //index (s / 8) + 7 - (s % 8)
            {0x80L, 0x8040L, 0x804020L, 0x80402010L, 0x8040201008L, 0x804020100804L, 0x80402010080402L,
                    0x8040201008040201L, 0x4020100804020100L, 0x2010080402010000L, 0x1008040201000000L,
                    0x804020100000000L, 0x402010000000000L, 0x201000000000000L, 0x100000000000000L};

    public long getTurnPieces() { return turnPieces;}
    public long getEnemyPieces() { return enemyPieces;}
    public long getTurnKing() { return turnKing;}
    public long getEnemyKing() {return enemyKing;}

    public void setSquareStatus(long wp, long wn, long wb, long wr, long wq, long wk, long bp,
                                long bn, long bb, long br, long bq, long bk, int turn) {
        //sets turnPieces, enemyPieces, and empty
        //turn = 1 for white, -1 for black
        if (turn == 1) {
            turnPieces = wp | wn | wb | wr | wq;
            enemyPieces = bp | bn | bb | br | bq;
            turnKing = wk;
            enemyKing = bk;
        }
        if (turn == -1) {
            enemyPieces = wp | wn | wb | wr | wq;
            turnPieces = bp | bn | bb | br | bq;
            turnKing = bk;
            enemyKing = wk;
        }
        empty = (~turnPieces) & (~enemyPieces) & (~wk) & (~bk);
        occupied = ~empty;

    }

    public void allLegalmoves(ArrayList<move> allPseudo, int turn) {

    }

    public ArrayList<move> moveGenerator(long wp, long wn, long wb, long wr, long wq, long wk, long bp, long bn, long bb,
                                   long br, long bq, long bk, int turn, byte wCastle, byte bCastle, int lastPawnMove) {
        byte turnKingPos = 0;
        if (turn == 1) { while (wk >>> turnKingPos != 1) {turnKingPos++;}}
        if (turn == -1) { while (bk >>> turnKingPos != 1) {turnKingPos++;}}
        ArrayList<move> legalMoves = new ArrayList<>();
        //legalMoves initially contains all legal and pseudolegal, then trimmed down to legal
        setSquareStatus(wp, wn, wb, wr, wq, wk, bp, bn, bb, br, bq, bk, turn);
        return legalMoves;
    }

    public ArrayList<move> generatePseudoLegal(long wp, long wn, long wb, long wr, long wq, long wk, long bp, long bn,
                                    long bb, long br, long bq, long bk, int lastPawnJump, int turn,
                                    ArrayList<Integer> legalMoves) {
        ArrayList<move> pseudoMoves = new ArrayList<>();
        if (turn == 1) {pseudoWhitePawn(wp, lastPawnJump, pseudoMoves);}
        return pseudoMoves;
        //generates pseudo legal moves and stores to legalMoves

    }

    public long rankFileSliding(int pos) {  //horizontal and vertical sliding
        long slider = 0x1L << pos;
        long rankOccupancy = RANKS[pos / 8] & occupied; //horizontal piece slide
        long posRankAttacks = (rankOccupancy ^ (rankOccupancy - 2 * slider)) & (enemyPieces | empty)
                & RANKS[pos / 8]; //positive rank
        long negRankAttacks = (rankOccupancy ^ Long.reverse(Long.reverse(rankOccupancy) - 2 * Long.reverse(slider))) &
                (enemyPieces | empty) & RANKS[pos / 8]; //negative rank
        long fileOccupancy = FILES[pos % 8] & occupied; //check file
        long posFileAttacks = (fileOccupancy ^ (fileOccupancy - 2 * slider)) & (enemyPieces | empty) & FILES[pos % 8];
        //pos file
        long negFileAttacks = (fileOccupancy ^ Long.reverse(Long.reverse(fileOccupancy) - 2 * Long.reverse(slider))) &
                (enemyPieces | empty) & FILES[pos % 8]; //neg file
        return (posRankAttacks | negRankAttacks | posFileAttacks | negFileAttacks);
    }

    public long diagSliding(int pos) {
        long slider = 0x1L << pos;
        long diag_occ = occupied & TR_BL_DIAGS[(pos / 8) + (pos % 8)]; //first check top right bottom left diag
        long posAttacks_TRBL = (diag_occ ^ (diag_occ - 2 * slider)) & (enemyPieces | empty) &
                (TR_BL_DIAGS[(pos / 8) + (pos % 8)]);
        long negAttacks_TRBL = (diag_occ ^ Long.reverse((Long.reverse(diag_occ) - 2 * Long.reverse(slider)))) &
                (enemyPieces | empty) & TR_BL_DIAGS[(pos / 8) + (pos % 8)];
        diag_occ = occupied & TL_BR_DIAGS[(pos / 8) + 7 - (pos % 8)]; //check top left bot right diag
        long posAttacks_TLBR = (diag_occ ^ (diag_occ - 2 * slider)) & (enemyPieces | empty) &
                TL_BR_DIAGS[(pos / 8) + 7 - (pos % 8)];
        long negAttacks_TLBR = (diag_occ ^ Long.reverse((Long.reverse(diag_occ) - 2 * Long.reverse(slider)))) &
                (enemyPieces | empty) & TL_BR_DIAGS[(pos / 8) + 7 - (pos % 8)];
        return (posAttacks_TRBL | negAttacks_TRBL | posAttacks_TLBR | negAttacks_TLBR);
    }

    public void promoCheckWhite(long wp, int start, int dest, ArrayList<move> moves, long rank) {
        //given rank (rank 2 for black or rank 7 for white) if pawn at rank 2 or 7, then add promotion move
        if ((wp >>> (start) & rank >>> (start) & 1) == 1) { //checks if startpos is at rank 7
            moves.add(new move(start, dest, 3, 2));
            moves.add(new move(start, dest, 3, 3));
            moves.add(new move(start, dest, 3, 4));
            moves.add(new move(start, dest, 3, 5));
        } else { moves.add(new move(start, dest, 0, 0)); } //if not then add normal move

    }

    public ArrayList<move> pseudoWhitePawn(long wp, int lastPawnJump, ArrayList<move> pseudoMoves) {
        long pawnCaptureRight = wp & ~H_FILE & enemyPieces>>>7; //bitwise right shift 7 for right pawn captures
        long pawnCaptureLeft = wp & ~A_FILE & enemyPieces>>>9; //bitwise right shift 9 for left pawn capture

        //looping thru each pawn
        for (int i = Long.numberOfTrailingZeros(wp); i < 64 - Long.numberOfLeadingZeros(wp); i++) {
            if ((wp >>> i & 1) == 1) { //if pawn exists at ith right shift
                if ((pawnCaptureRight >>> i & 1) == 1) { //check for pseudolegal right pawn capture
                    promoCheckWhite(wp, i, i + 7, pseudoMoves, RANK_7);
                } else if (((RANK_5 >>> i & NOT_H >>> i & 1) == 1) & (lastPawnJump == i + 7)) {
                    pseudoMoves.add(new move(i, lastPawnJump, 2, 0));
                    //if capture square empty, check right en passant
                }
                if ((pawnCaptureLeft >>> i & 1) == 1) { //check for pseudolegal right pawn capture
                    promoCheckWhite(wp, i, i + 9, pseudoMoves, RANK_7);
                } else if (((RANK_5 >>> i & NOT_A>>> i & 1) == 1) & (lastPawnJump == i + 9)) {
                    pseudoMoves.add(new move(i, lastPawnJump, 2, 0));
                    //if capture square empty, check left en passant
                }

                if ((empty >>> (i + 8) & 1) == 1) { ///check if can move forward one square
                    promoCheckWhite(wp, i, i + 8, pseudoMoves, RANK_7);
                    if ((wp >>> i & RANK_2 >>> i & empty >>> (i + 16) & 1) == 1) { //check if pawn can jump 2 squares
                        pseudoMoves.add(new move(i, i + 16, 0, 0));
                    }
                }
            }

        }
        return pseudoMoves;
    }

    public ArrayList<move> pseudoBlackPawn(long bp, int lastPawnJump, ArrayList<move> pseudoMoves) {
        long pawnCaptureLeft = bp & ~A_FILE & enemyPieces<<7; //bitwise left shift 7 for left pawn captures
        long pawnCaptureRight = bp & ~H_FILE & enemyPieces<<9; //bitwise left shift 9 for right pawn capture
        for (int i = Long.numberOfTrailingZeros(bp); i < 64 - Long.numberOfLeadingZeros(bp); i++) {
            if ((bp >>> i & 1) == 1) { //if pawn exists at ith right shift
                if ((pawnCaptureRight >>> i & 1) == 1) { //check for pseudolegal right pawn capture
                    promoCheckWhite(bp, i, i - 9, pseudoMoves, RANK_2);
                } else if (((RANK_4 >>> i & NOT_H >>> 1 & 1) == 1) & (lastPawnJump == i - 9)) {
                    pseudoMoves.add(new move(i, lastPawnJump, 2, 0));
                    //if capture square empty, check right en passant
                }
                if ((pawnCaptureLeft >>> i & 1) == 1) { //check for pseudolegal right pawn capture
                    promoCheckWhite(bp, i, i - 7, pseudoMoves, RANK_2);
                } else if (((RANK_4 >>> i & NOT_A >>> 1 & 1) == 1) & (lastPawnJump == i - 7)) {
                    pseudoMoves.add(new move(i, lastPawnJump, 2, 0));
                    //if capture square empty, check right en passant
                }

                if ((empty >>> (i - 8) & 1) == 1) { ///check if can move forward one square
                    promoCheckWhite(bp, i, i - 8, pseudoMoves, RANK_2);
                    if ((bp >>> i & RANK_7 >>> i & empty >>> (i - 16) & 1) == 1) { //check if pawn can jump 2 squares
                        pseudoMoves.add(new move(i, i - 16, 0, 0));
                    }
                }
            }

        }
        return pseudoMoves;
    }

    public long knightAttackGen(int i) {
        //calculate all POTENTIAL pseudo legal moves for knight at position i
        //can also be used to see if king at position i is under attack by knights
        long attackGen = 0L;
        //generate bitboard for specific position
        long nPos = 0B1L << i;
        if ((nPos << 10 & (NOT_GH)) != 0) {attackGen = attackGen + (nPos << 10); }
        if ((nPos >>> 10 & (NOT_AB)) != 0) {attackGen = attackGen + (nPos >>> 10); }
        if ((nPos << 6 & (NOT_AB)) != 0) {attackGen = attackGen + (nPos << 6); }
        if ((nPos >>> 6 & (NOT_GH)) != 0) {attackGen = attackGen + (nPos >>> 6); }
        if ((nPos << 15 & NOT_A) != 0) {attackGen = attackGen + (nPos << 15); }
        if ((nPos >>> 15 & NOT_H) != 0) {attackGen = attackGen + (nPos >>> 15); }
        if ((nPos << 17 & NOT_H) != 0) {attackGen = attackGen + (nPos << 17); }
        if ((nPos >>> 17 & NOT_A) != 0) {attackGen = attackGen + (nPos >>> 17);}

        return attackGen;
    }

    public void pseudoKnightAtPos(int i, long atkSqs, ArrayList<move> pseudoMoves) {
        //given list of attack squares, generate pseudo legal moves for knight
        for (int sq = Long.numberOfTrailingZeros(atkSqs); sq < 64 - Long.numberOfLeadingZeros(atkSqs); sq++) {
            if ((atkSqs >>> sq & 1) == 1) {
                if ((empty >>> sq & 1) == 1 | (enemyPieces >>> sq & 1) == 1) {
                    pseudoMoves.add(new move(i, sq, 0, 0));
                }
            }
        }
    }

    public ArrayList<move> allPseudoKnight(Long turnKnight, ArrayList<move> pseudoMoves) {
        for (int i = Long.numberOfTrailingZeros(turnKnight); i < 64 - Long.numberOfLeadingZeros(turnKnight); i++) {
            if ((turnKnight >>> i & 1) == 1) {
                pseudoKnightAtPos(i, knightAttackGen(i), pseudoMoves);}
        }
        return pseudoMoves;
    }

    public ArrayList<move> pseudoBishop(long turnBishop, ArrayList<move> pseudoMoves) {
        for (int i = Long.numberOfTrailingZeros(turnBishop); i < 64 - Long.numberOfLeadingZeros(turnBishop); i++) {
            if ((turnBishop >>> i & 1) == 1) { //if queen at square
                long validPseudo = diagSliding(i);
                //find possible attack squares for all files, ranks, diags
                for (int j = Long.numberOfTrailingZeros(validPseudo); j < 64 - Long.numberOfLeadingZeros(validPseudo);
                     j++) { if ((validPseudo >>> j & 1) == 1) { pseudoMoves.add(new move(i, j, 0, 0)); }}
                //add all attack squares to ArrayList pseudoMoves
            }
        }
        return pseudoMoves;
    }

    public ArrayList<move> pseudoRook(long turnRook, ArrayList<move> pseudoMoves) {
        for (int i = Long.numberOfTrailingZeros(turnRook); i < 64 - Long.numberOfLeadingZeros(turnRook); i++) {
            if ((turnRook >>> i & 1) == 1) { //if queen at square
                long validPseudo = rankFileSliding(i);
                //find possible attack squares for all files, ranks, diags
                for (int j = Long.numberOfTrailingZeros(validPseudo); j < 64 - Long.numberOfLeadingZeros(validPseudo);
                     j++) { if ((validPseudo >>> j & 1) == 1) { pseudoMoves.add(new move(i, j, 0, 0)); }}
                //add all attack squares to ArrayList pseudoMoves
            }
        }
        return pseudoMoves;
    }


    public ArrayList<move> pseudoQueen(long turnQueen, ArrayList<move> pseudoMoves) {
        for (int i = Long.numberOfTrailingZeros(turnQueen); i < 64 - Long.numberOfLeadingZeros(turnQueen); i++) {
            if ((turnQueen >>> i & 1) == 1) { //if queen at square
                long validPseudo = rankFileSliding(i) | diagSliding(i);
                //find possible attack squares for all files, ranks, diags
                for (int j = Long.numberOfTrailingZeros(validPseudo); j < 64 - Long.numberOfLeadingZeros(validPseudo);
                     j++) { if ((validPseudo >>> j & 1) == 1) { pseudoMoves.add(new move(i, j, 0, 0)); }}
                //add all attack squares to ArrayList pseudoMoves
            }
        }
        return pseudoMoves;
    }

    public ArrayList<move> pseudoKing(long turnKing, long turnRooks, long turnCastling, int turn,
                                      ArrayList<move> pseudoMoves) {
        int turnKingPos = Long.numberOfTrailingZeros(turnKing);
        if (((turnKing >>> 1 & empty) != 0) || ((turnKing >>> 1 & enemyPieces) != 0)) {
            //if right square is empty or has enemy piece
            pseudoMoves.add(new move(turnKingPos, turnKingPos - 1, 0, 0));
        } if (((turnKing << 1 & empty) != 0) || ((turnKing << 1 & enemyPieces) != 0)) {
            //if left square is empty or has enemy piece
            pseudoMoves.add(new move(turnKingPos, turnKingPos + 1, 0, 0));
        } if (((turnKing >>> 8 & empty) != 0) || ((turnKing >>> 8 & enemyPieces) != 0)) {
            //if square directly below is empty or has enemy piece
            pseudoMoves.add(new move(turnKingPos, turnKingPos - 8, 0, 0));
        } if (((turnKing << 8 & empty) != 0) || ((turnKing << 8 & enemyPieces) != 0)) {
            //if  square directly above is empty or has enemy piece
            pseudoMoves.add(new move(turnKingPos, turnKingPos + 8, 0, 0));
        } if (((turnKing >>> 9 & empty) != 0) || ((turnKing >>> 9 & enemyPieces) != 0)) {
            //if diag lower right square is empty or has enemy piece
            pseudoMoves.add(new move(turnKingPos, turnKingPos - 9, 0, 0));
        } if (((turnKing << 9 & empty) != 0) || ((turnKing << 9 & enemyPieces) != 0)) {
            //if diag upper left square is empty or has enemy piece
            pseudoMoves.add(new move(turnKingPos, turnKingPos + 9, 0, 0));
        } if (((turnKing >>> 7 & empty) != 0) || ((turnKing >>> 7 & enemyPieces) != 0)) {
            //if diag lower left square is empty or has enemy piece
            pseudoMoves.add(new move(turnKingPos, turnKingPos - 7, 0, 0));
        } if (((turnKing << 7 & empty) != 0) || ((turnKing << 7 & enemyPieces) != 0)) {
            //if diag upper left square is empty or has enemy piece
            pseudoMoves.add(new move(turnKingPos, turnKingPos - 7, 0, 0));
        }
        if (turn == 1 && turnKingPos != 3) {turnCastling = 0;}
        if (turn == -1 && turnKingPos != 59) {turnCastling = 0;}
        if ((turnCastling & 1) == 1) { //check if kside castling valid
            if ((turnRooks>>>(turnKingPos - 3) & 1) != 1) { turnCastling = (turnCastling - 1);}
        } if ((turnCastling >>> 1 & 1) == 1) { //check if qside turn castling valid
            if ((turnRooks>>>(turnKingPos + 4) & 1) != 1) { turnCastling = (turnCastling - 2);}
        }
        if (turnCastling != 0) { //for castling moves
            if ((turnCastling & 1) == 1) { //kingside castling
                if ((empty >>> (turnKingPos - 1) & empty >>> (turnKingPos - 2) & 1) == 1) {
                    pseudoMoves.add(new move(turnKingPos, turnKingPos - 2, 1, 0));}
            } if ((turnCastling >>> 1 & 1) == 1) { //queenside castling
                if ((empty >>> (turnKingPos + 1) & empty >>> (turnKingPos + 2) & empty>>> (turnKingPos + 3) & 1) == 1) {
                    pseudoMoves.add(new move(turnKingPos, turnKingPos + 2, 1, 0));}
            }
        }
        return pseudoMoves;
    }

    public static void main(String[] args) {
        System.out.println();
    }

}
