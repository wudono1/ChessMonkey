package chess.testing;

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
   to convert to little endian: 8 * (7 - n / 8) + n % 8
*/


@SuppressWarnings({"SpellCheckingInspection", "GrazieInspection"})
public class moveGenTesting2 {

    static long turnPieces; //All pieces of current side to play except king
    static long enemyPieces; //All pieces of enemy side except king
    static long empty; //All empty squares
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
    static final long RANK_2 = 0B0000000000000000000000000000000000000000000000001111111100000000L;
    static final long RANK_7 = 0B0000000011111111000000000000000000000000000000000000000000000000L;

    static final long[] RANK_MASKS = //rank 1 to rank 8
            { 0xFFL, 0xFF00L, 0xFF0000L, 0xFF000000L, 0xFF00000000L, 0xFF0000000000L, 0xFF000000000000L, 0xFF00000000000000L};
    static final long[] FILE_MASKS = //h file to a file. Remainder 0 = h file, remainder 7 = a file
            { 0x0101010101010101L, 0x0202020202020202L, 0x0404040404040404L, 0x0808080808080808L, 0x1010101010101010L,
                    0x2020202020202020L, 0x4040404040404040L, 0x8080808080808080L};
    static long[] TR_BL_DIAG_MASKS =/*from top right to bottom left*/
            //index [(s / 8) + (s % 8)]
            { 0x1L, 0x102L, 0x10204L, 0x1020408L, 0x102040810L, 0x10204081020L, 0x1020408102040L, 0x102040810204080L,
                    0x204081020408000L, 0x408102040800000L, 0x810204080000000L, 0x1020408000000000L,
                    0x2040800000000000L, 0x4080000000000000L, 0x8000000000000000L};
    static final long[] TL_BR_DIAG_MASKS =
            //index (s / 8) + 7 - (s % 8)
            {0x80L, 0x8040L, 0x804020L, 0x80402010L, 0x8040201008L, 0x804020100804L, 0x80402010080402L,
                    0x8040201008040201L, 0x4020100804020100L, 0x2010080402010000L, 0x1008040201000000L,
                    0x804020100000000L, 0x402010000000000L, 0x201000000000000L, 0x100000000000000L};

    public int addMove(int start, int dest, int flag, int promoPiece) {
        /* Notes:
        16 BIT INT: 0000 0000 0000 0000
        BITS 0-5: START SQUARE
        BITS 6-11: DEST SQUARE
        BITS 12-13: PIECE FLAG- 0=REGULAR MOVE; CASTLE = 1; EN PASSANT = 2; PROMOTION = 3
        BITS: 14-15: PROMOPIECE- KNIGHT PROMO/NON PROMOTION MOVE = 0, BISHOP PROMO = 1, ROOK PROMO = 2, QUEEN PROMO = 3
         */
        return (start | dest << 6 | flag << 12 | promoPiece << 14);
    }

    public void setSquareStatus(long wp, long wn, long wb, long wr, long wq, long wk, long bp,
                                long bn, long bb, long br, long bq, long bk, int turn) {
        //sets turnPieces, enemyPieces, and empty
        //turn = 1 for white, -1 for black
        if (turn == 1) {
            turnPieces = wp | wn | wb | wr | wq;
            enemyPieces = bp | bn | bb | br | bq;
        } if (turn == -1) {
            enemyPieces = wp | wn | wb | wr | wq;
            turnPieces = bp | bn | bb | br | bq;
        }
        occupied = turnPieces | enemyPieces | wk | bk;
        empty = ~occupied;
    }

    public ArrayList<Integer> generatePseudoLegal(long tp, long tn, long tb, long tr, long tq, long tk,
        int lastPawnJump, int turnCastle, int turn, ArrayList<Integer> pseudoTactical, ArrayList<Integer> pseudoQuiet) {
        if (turn == 1) {
            pseudoWhitePawnTactical(tp, lastPawnJump, pseudoTactical);
            pseudoWhitePawnQuiet(tp, pseudoQuiet);
        } if (turn == -1) {
            pseudoBlackPawnTactical(tp, lastPawnJump, pseudoTactical);
            pseudoBlackPawnQuiet(tp, pseudoQuiet);
        }
        pseudoKnightMoves(tn, pseudoTactical, pseudoQuiet);
        pseudoBishopMoves(tb, pseudoTactical, pseudoQuiet);
        pseudoRookMoves(tr, pseudoTactical, pseudoQuiet);
        pseudoQueenMoves(tq, pseudoTactical, pseudoQuiet);
        pseudoKingMoves(tk, turnCastle, pseudoTactical, pseudoQuiet);

        return pseudoTactical;
        //generates pseudo legal moves and stores to legalMoves
    }

    public void generateAllLegalMoves(long wp, long wn, long wb, long wr, long wq, long wk, long bp, long bn, long bb,
                                      long br, long bq, long bk, int turn, int wCastle, int bCastle, int lastPawnJump,
                                      ArrayList<Integer>captures, ArrayList<Integer> quietMoves, ArrayList<Integer> checks) {
        setSquareStatus(wp, wn, wb, wr, wq, wk, bp, bn, bb, br, bq, bk, turn);
        if (turn == 1) { //if white to move
            generatePseudoLegal(wp, wn, wb, wr, wq, wk, lastPawnJump, wCastle, turn, captures, quietMoves);
            int i = 0;
            int lstSize = captures.size();
            while (i < lstSize) {
                if (checkLegalityTactical(captures.get(i), wp, wn, wb, wr, wq, wk, bp, bn, bb, br, bq, bk, turn, checks)) {
                    i++;
                } else {
                    captures.remove(i);
                    lstSize = captures.size();}
            }
            lstSize = quietMoves.size();
            while (i < lstSize) {
                if (checkLegalityQuiet(quietMoves.get(i), wp, wn, wb, wr, wq, wk, bp, bn, bb, br, bq, bk, turn, checks)) {
                    i++;
                } else {
                    quietMoves.remove(i);
                    lstSize = quietMoves.size();}
            }
        } if (turn == -1) { //if black to move
            generatePseudoLegal(bp, bn, bb, br, bq, bk, lastPawnJump, bCastle, turn, captures, quietMoves);
            int i = 0;
            int lstSize = captures.size();
            while (i < lstSize) {
                if (checkLegalityTactical(captures.get(i), bp, bn, bb, br, bq, bk, wp, wn, wb, wr, wq, wk, turn, checks)) {
                    i++;
                } else {
                    captures.remove(i);
                    lstSize = captures.size();}
            }
            lstSize = quietMoves.size();
            while (i < lstSize) {
                if (checkLegalityQuiet(quietMoves.get(i), bp, bn, bb, br, bq, bk, wp, wn, wb, wr, wq, wk, turn, checks)) {
                    i++;
                } else {
                    quietMoves.remove(i);
                    lstSize = quietMoves.size();}
            }
        }
    }

    public Boolean squareInCheck(int sq, long ep, long en, long eb, long er, long eq, long ek, int turn) {
        //checks if square is in check. Used for seeing if king in check, but also for castling purposes
        if (turn == 1) { //if pawn checks, return true
            if (((NOT_H >>> sq & 1) == 1) && ((ep >>> (sq + 7) & 1) == 1)) { return true; } //right pawn check
            if (((NOT_A >>> sq & 1) == 1) && ((ep >>> (sq + 9) & 1) == 1)) { return true; } //left pawn check
        } if (turn == -1) {
            if (((NOT_A >>> sq & 1) == 1) && ((ep >>> (sq - 7) & 1) == 1)) { return true; } //left pawn check
            if (((NOT_H >>> sq & 1) == 1) && ((ep >>> (sq - 9) & 1) == 1)) { return true; } //right pawn check
        }
        return ((knightAttackGen(sq) & en) != 0) | ((diagSliding(sq) & (eb | eq)) != 0) |
                ((rankFileSliding(sq) & (er | eq)) != 0) | ((kingAttackGen(sq) & ek) != 0);
    }

    public Boolean checkLegalityQuiet(int pMove, long tp, long tn, long tb, long tr, long tq, long tk, long ep, long en,
                                 long eb, long er, long eq, long ek, int turn, ArrayList<Integer> checks) {
        //for checking quiet moves
        int start = pMove & 0b111111;
        int dest = pMove >>> 6 & 0b111111;
        int moveType = pMove >>> 12 & 0b11;
        switch (moveType) {
            //only checking for normal moves and castling for quiet moves
            case 0 -> {//movetype is normal move
                if ((tp >>> start & 1) == 1) { //pawn
                    tp = (tp & ~(1L << (start))) | (1L << (dest));
                } if ((tn >>> start & 1) == 1) { //knight
                    tn = (tn & ~(1L << (start))) | (1L << (dest));
                } if ((tb >>> start & 1) == 1) { //bishop
                    tb = (tb & ~(1L << (start))) | (1L << (dest));
                } if ((tr >>> start & 1) == 1) { //rook
                    tr = (tr & ~(1L << (start))) | (1L << (dest));
                } if ((tq >>> start & 1) == 1) { //queen
                    tq = (tq & ~(1L << (start))) | (1L << (dest));
                } if ((tk >>> start & 1) == 1) { //queen
                    tk = (tk & ~(1L << (start))) | (1L << (dest));
                }
            }

            case 1 -> { //movetype is castling
                if (squareInCheck(start, ep, en, eb, er, eq, ek, turn)) {return false;}
                tk = (tk & ~(1L << (start))) | (1L << (dest));
                //if about to castle but king is in check, return false
                if (start > dest) { //kingside castling
                    if (squareInCheck(Long.numberOfTrailingZeros(tk) + 1, ep, en, eb, er, eq, ek, turn)) {return false;}
                    //if king not in check but moves thru check when castling, return False
                    tr = (tr & ~(1L<<(dest - 1))) | (1L << (dest + 1));
                } if (start < dest) { //queenside castling
                    if (squareInCheck(Long.numberOfTrailingZeros(tk) - 1, ep, en, eb, er, eq, ek, turn)) {return false;}
                    //if king not in check but moves thru check when castling, return False
                    tr = (tr & ~(1L<<(dest + 2))) | (1L << (dest - 1));
                }
            }
        }
        //update occupancy bitboards from new piece bitboards
        long origTPs = turnPieces;
        long origEmpty = empty;
        long origOcc = occupied;
        turnPieces = tp | tn | tb | tr | tq;
        occupied = turnPieces | enemyPieces | tk | ek;
        empty = ~occupied;
        if (squareInCheck(Long.numberOfTrailingZeros(tk), ep, en, eb, er, eq, ek, turn)) {
            turnPieces = origTPs;
            empty = origEmpty;
            occupied = origOcc;
            return false;
        }
        if (squareInCheck(Long.numberOfTrailingZeros(ek), tp, tn, tb, tr, tq, tk, turn)) {
            //if move is legal and puts enemy king in check, move it to checks array
            turnPieces = origTPs;
            empty = origEmpty;
            occupied = origOcc;
            checks.add(pMove);
            return false;
        }
        turnPieces = origTPs;
        empty = origEmpty;
        occupied = origOcc;
        return true; // if no checks found, return true
    }

    public Boolean checkLegalityTactical(int pMove, long tp, long tn, long tb, long tr, long tq, long tk, long ep, long en,
                                      long eb, long er, long eq, long ek, int turn, ArrayList<Integer> checks) {
        //for checking quiet moves
        int start = pMove & 0b111111;
        int dest = pMove >>> 6 & 0b111111;
        int moveType = pMove >>> 12 & 0b11;
        int promo = pMove >>> 14 & 0b11;
        switch (moveType) {
            //only checking for normal moves and castling for quiet moves
            case 0 -> {//movetype is normal move
                if ((tp >>> start & 1) == 1) { //pawn
                    tp = (tp & ~(1L << (start))) | (1L << (dest));
                } if ((tn >>> start & 1) == 1) { //knight
                    tn = (tn & ~(1L << (start))) | (1L << (dest));
                } if ((tb >>> start & 1) == 1) { //bishop
                    tb = (tb & ~(1L << (start))) | (1L << (dest));
                } if ((tr >>> start & 1) == 1) { //rook
                    tr = (tr & ~(1L << (start))) | (1L << (dest));
                } if ((tq >>> start & 1) == 1) { //queen
                    tq = (tq & ~(1L << (start))) | (1L << (dest));
                } if ((tk >>> start & 1) == 1) { //queen
                    tk = (tk & ~(1L << (start))) | (1L << (dest));
                }
            }

            case 2 -> { //if movetype is en passant
                tp = (tp & ~(1L << (start))) | (1L << (dest));
                if (turn == 1) { //for changing enemy pawn bitboards
                    ep = ep & ~(1L << (dest - 8)); //if white playing en passant
                } if (turn == -1) {
                    ep = ep & ~(1L << (dest + 8));} //if black playing en passant
            }

            case 3 -> { //if movetype is promotion
                tp = tp & ~(1L << (start)); //change turn pawn bitboards
                switch (promo) { //changing promotion piece bitboards is applicable
                    case 0 -> tn = tn | (1L << dest);
                    case 1 -> tb = tb | (1L << dest);
                    case 2 -> tr = tr | (1L << dest);
                    case 3 -> tq = tq | (1L << dest);
                }
            }
        }
        //checking if capture at dest square
        if ((ep >>> dest & 1) == 1) {
            ep = ep & ~(1L << (dest));
        } if ((en >>> dest & 1) == 1) {
            en = en & ~(1L << (dest));
        } if ((eb >>> dest & 1) == 1) {
            eb = eb & ~(1L << (dest));
        } if ((er >>> dest & 1) == 1) {
            er = er & ~(1L << (dest));
        } if ((eq >>> dest & 1) == 1) {
            eq = eq & ~(1L << (dest));
        }

        //update occupancy bitboards from new piece bitboards
        long origTPs = turnPieces;
        long origEnemy = enemyPieces;
        long origEmpty = empty;
        long origOcc = occupied;
        turnPieces = tp | tn | tb | tr | tq;
        enemyPieces = ep | en | eb | er | eq;
        occupied = turnPieces | enemyPieces | tk | ek;
        empty = ~occupied;

        //check if turn king still in check
        if (squareInCheck(Long.numberOfTrailingZeros(tk), ep, en, eb, er, eq, ek, turn)) {
            turnPieces = origTPs;
            enemyPieces = origEnemy;
            empty = origEmpty;
            occupied = origOcc;
            return false;
        }
        if (squareInCheck(Long.numberOfTrailingZeros(ek), tp, tn, tb, tr, tq, tk, turn)) {
            //if move is legal and puts enemy king in check, move it to checks array
            turnPieces = origTPs;
            enemyPieces = origEnemy;
            empty = origEmpty;
            occupied = origOcc;
            checks.add(pMove);
            return false;
        }
        turnPieces = origTPs;
        enemyPieces = origEnemy;
        empty = origEmpty;
        occupied = origOcc;
        return true; // if no checks found, return true
    }

    public long rankFileSliding(int pos) {  //horizontal and vertical sliding
        long slider = 0x1L << pos;
        long rankOccupancy = RANK_MASKS[pos / 8] & occupied; //horizontal piece slide
        long posNegRankAttacks = ((rankOccupancy - 2 * slider) ^ Long.reverse(Long.reverse(rankOccupancy) - 2 * Long.reverse(slider))) &
                RANK_MASKS[pos / 8];
        long fileOccupancy = FILE_MASKS[pos % 8] & occupied; //check file
        long posNegFileAttacks = ((fileOccupancy - 2 * slider) ^ Long.reverse(Long.reverse(fileOccupancy) - 2 * Long.reverse(slider))) &
                FILE_MASKS[pos % 8];
        return (posNegRankAttacks| posNegFileAttacks);
    }

    public long diagSliding(int pos) {
        long slider = 0x1L << pos;
        long diag_occ = occupied & TR_BL_DIAG_MASKS[(pos / 8) + (pos % 8)]; //first check top right bottom left diag
        long posNeg_TRBL = ((diag_occ - 2 * slider) ^ Long.reverse(Long.reverse(diag_occ) - 2 * Long.reverse(slider))) &
                TR_BL_DIAG_MASKS[(pos / 8) + (pos % 8)];
        diag_occ = occupied & TL_BR_DIAG_MASKS[(pos / 8) + 7 - (pos % 8)]; //check top left bot right diag
        long posNeg_TLBR = ((diag_occ - 2 * slider) ^ Long.reverse(Long.reverse(diag_occ) - 2 * Long.reverse(slider))) &
                TL_BR_DIAG_MASKS[(pos / 8) + 7 - (pos % 8)];
        return (posNeg_TRBL | posNeg_TLBR);
    }

    public void promoCheckCaptures(long tp, int start, int dest, long rank, ArrayList<Integer> pseudoTactical) {
        switch ((int)((tp >>> (start) & rank >>> (start) & 1) )) {  //checks if startpos is at rank 7
            case 1 -> {
                pseudoTactical.add(addMove(start, dest, 3, 3));
                pseudoTactical.add(addMove(start, dest, 3, 2));
                pseudoTactical.add(addMove(start, dest, 3, 1));
                pseudoTactical.add(addMove(start, dest, 3, 0));
            } case 0 -> pseudoTactical.add(addMove(start, dest, 0, 0));  //if not then add normal move
        }
    }

    public void pseudoWhitePawnTactical(long wp, int lastPawnJump, ArrayList<Integer>pseudoTactical) {
        //for moves that change immediate material imbalance
        long pawnCaptureRight = wp & ~H_FILE & enemyPieces>>>7; //bitwise right shift 7 for right pawn captures
        long pawnCaptureLeft = wp & ~A_FILE & enemyPieces>>>9; //bitwise right shift 9 for left pawn capture
        long pawnPromotion8 = (wp & RANK_7) & (empty >>> 8); //non capture pawn promotions
        long epSquare = 0L; //for en passant
        if (lastPawnJump != -1) { epSquare = 1L << lastPawnJump;} //checking for en passant

        if (((wp & ~A_FILE) & epSquare >>> 9 & empty >>> 9) != 0) { //left en passant
            pseudoTactical.add(addMove(lastPawnJump - 9, lastPawnJump, 2, 0));
        } if (((wp & ~H_FILE) & epSquare >>> 7 & empty >>> 7) != 0) { //right en passant
            pseudoTactical.add(addMove(lastPawnJump - 7, lastPawnJump, 2, 0));
        }
        for (int i = Long.numberOfTrailingZeros(pawnCaptureRight); i < 64 - Long.numberOfLeadingZeros(pawnCaptureRight);
               i++) { //right pawn capture
            if ((pawnCaptureRight >>> i & 1) == 1) {promoCheckCaptures(wp, i, i + 7, RANK_7, pseudoTactical);}
        } for (int i = Long.numberOfTrailingZeros(pawnCaptureLeft); i < 64 - Long.numberOfLeadingZeros(pawnCaptureLeft);
               i++) { //left pawn capture
            if ((pawnCaptureLeft >>> i & 1) == 1) {promoCheckCaptures(wp, i, i + 9, RANK_7, pseudoTactical);}
        } for (int i = Long.numberOfTrailingZeros(pawnPromotion8); i < 64 - Long.numberOfLeadingZeros(pawnPromotion8); i++) {
            if ((pawnPromotion8 >>> i & 1) == 1) {
                pseudoTactical.add(addMove(i, i + 8, 3, 3));
                pseudoTactical.add(addMove(i, i + 8, 3, 2));
                pseudoTactical.add(addMove(i, i + 8, 3, 1));
                pseudoTactical.add(addMove(i, i + 8, 3, 0));
            }
        }
    }

    public void pseudoWhitePawnQuiet(long wp, ArrayList<Integer> pseudoQuiet) {
        //for quiet moves and checks. Quiet moves and checks differentaited later
        long verticalMove8 = (wp & ~RANK_7) & (empty >>> 8); //bitwise right shift for pawn moving up one square
        long verticalMove16 = verticalMove8 & RANK_MASKS[1] & (empty >>> 16); //pawn vertical jump 2 squares

        //check pawn move up 1
        for (int i = Long.numberOfTrailingZeros(verticalMove8); i < 64 - Long.numberOfLeadingZeros(verticalMove8); i++) {
            if ((verticalMove8 >>> i & 1) == 1) { pseudoQuiet.add(addMove(i, i + 8, 0, 0)); }
        }

        //check pawn move up 2
        for (int i = Long.numberOfTrailingZeros(verticalMove16); i < 64 - Long.numberOfLeadingZeros(verticalMove16); i++) {
            if ((verticalMove16 >>> i & 1) == 1) {pseudoQuiet.add(addMove(i, i + 16, 0, 0)); }
        }
    }

    public void pseudoBlackPawnTactical(long bp, int lastPawnJump, ArrayList<Integer> pseudoTactical) {
        long pawnCaptureLeft = bp & ~A_FILE & enemyPieces<<7; //bitwise left shift 7 for left pawn captures
        long pawnCaptureRight = bp & ~H_FILE & enemyPieces<<9; //bitwise left shift 9 for right pawn capture
        long pawnPromotion8 = (bp & RANK_2) & (empty << 8); //bitwise right shift for pawn moving up one square
        long epSquare = 0L; //en passant square

        if (lastPawnJump != -1) { epSquare = 1L << lastPawnJump;} //set en passant square

        if (((bp & ~A_FILE) & epSquare << 7 & empty << 7) != 0) { //left en passant
            pseudoTactical.add(addMove(lastPawnJump + 7, lastPawnJump, 2, 0));
        } if (((bp & ~H_FILE) & epSquare << 9 & empty << 9) != 0) { //right en passant
            pseudoTactical.add(addMove(lastPawnJump + 9, lastPawnJump, 2, 0));
        } for (int i = Long.numberOfTrailingZeros(pawnCaptureRight); i < 64 - Long.numberOfLeadingZeros(pawnCaptureRight);
             i++) { //right pawn capture
            if ((pawnCaptureRight >>> i & 1) == 1) { promoCheckCaptures(bp, i, i - 9, RANK_2, pseudoTactical);}
        }
        for (int i = Long.numberOfTrailingZeros(pawnCaptureLeft); i < 64 - Long.numberOfLeadingZeros(pawnCaptureLeft);
             i++) { //left pawn capture
            if ((pawnCaptureLeft >>> i & 1) == 1) { promoCheckCaptures(bp, i, i - 7, RANK_2, pseudoTactical); }
        }
        //for non capture promotions
        for (int i = Long.numberOfTrailingZeros(pawnPromotion8); i < 64 - Long.numberOfLeadingZeros(pawnPromotion8); i++) {
            if ((pawnPromotion8 >>> i & 1) == 1) {
                pseudoTactical.add(addMove(i, i - 8, 3, 3));
                pseudoTactical.add(addMove(i, i - 8, 3, 2));
                pseudoTactical.add(addMove(i, i - 8, 3, 1));
                pseudoTactical.add(addMove(i, i - 8, 3, 0));
            }
        }
    }

    public void pseudoBlackPawnQuiet(long bp, ArrayList<Integer> pseudoQuiet) {
        //for quiet moves and checks. Quiet moves and checks differentaited later
        long verticalMove8 = (bp & ~RANK_2) & (empty << 8); //bitwise right shift for pawn moving up one square
        long verticalMove16 = verticalMove8 & RANK_MASKS[6] & (empty << 16); //pawn vertical jump 2 squares

        //check pawn move up 1
        for (int i = Long.numberOfTrailingZeros(verticalMove8); i < 64 - Long.numberOfLeadingZeros(verticalMove8); i++) {
            if ((verticalMove8 >>> i & 1) == 1) { pseudoQuiet.add(addMove(i, i - 8, 0, 0)); }
        }
        //check pawn move up 2
        for (int i = Long.numberOfTrailingZeros(verticalMove16); i < 64 - Long.numberOfLeadingZeros(verticalMove16); i++) {
            if ((verticalMove16 >>> i & 1) == 1) {pseudoQuiet.add(addMove(i, i - 16, 0, 0)); }
        }
    }

    public void pseudoCaptureAtPos(int i, long atkSqs, ArrayList<Integer> pseudoTactical) {
        //given list of attack squares, generate pseudo legal moves for any piece
        for (int sq = Long.numberOfTrailingZeros(atkSqs); sq < 64 - Long.numberOfLeadingZeros(atkSqs); sq++) {
            if ((atkSqs >>> sq & enemyPieces >>> sq & 1) == 1) {
                pseudoTactical.add(addMove(i, sq, 0, 0));
            }
        }
    }

    public void pseudoQuietAtPos(int i, long atkSqs, ArrayList<Integer> pseudoQuiet) {
        //given list of attack squares, generate pseudo legal moves for any piece
        for (int sq = Long.numberOfTrailingZeros(atkSqs); sq < 64 - Long.numberOfLeadingZeros(atkSqs); sq++) {
            if ((atkSqs >>> sq & empty >>> sq & 1) == 1) {
                pseudoQuiet.add(addMove(i, sq, 0, 0));
            }
        }
    }

    public long knightAttackGen(int i) {
        //calculate all POTENTIAL pseudo legal moves for knight at position i
        //can also be used to see if king at position i is under attack by knights
        long attackGen = 0L;
        //generate bitboard for specific position
        long nPos = 0B1L << i;
        attackGen = attackGen | (nPos << 10 & (NOT_GH)) | (nPos >>> 10 & (NOT_AB)) | (nPos << 6 & (NOT_AB)) |
                (nPos >>> 6 & (NOT_GH)) | (nPos << 15 & NOT_A) | (nPos >>> 15 & NOT_H) | (nPos << 17 & NOT_H) |
                (nPos >>> 17 & NOT_A);

        return attackGen;
    }

    public void pseudoKnightMoves(long turnKnight, ArrayList<Integer> pseudoCaptures, ArrayList<Integer> pseudoQuiet) {
        for (int i = Long.numberOfTrailingZeros(turnKnight); i < 64 - Long.numberOfLeadingZeros(turnKnight); i++) {
            if ((turnKnight >>> i & 1) == 1) {
                long knightMoveSquares = knightAttackGen(i);
                pseudoCaptureAtPos(i, knightMoveSquares, pseudoCaptures);
                pseudoQuietAtPos(i, knightMoveSquares, pseudoQuiet);
            }
        }
    }

    public void pseudoBishopMoves(long turnBishop, ArrayList<Integer> pseudoCaptures, ArrayList<Integer> pseudoQuiet) {
        for (int i = Long.numberOfTrailingZeros(turnBishop); i < 64 - Long.numberOfLeadingZeros(turnBishop); i++) {
            if ((turnBishop >>> i & 1) == 1) {
                long bishopMoveSquares = diagSliding(i);
                pseudoCaptureAtPos(i, bishopMoveSquares, pseudoCaptures);
                pseudoQuietAtPos(i, bishopMoveSquares, pseudoQuiet);
            }
        }
    }

    public void pseudoRookMoves(long turnRook, ArrayList<Integer> pseudoCaptures, ArrayList<Integer> pseudoQuiet) {
        for (int i = Long.numberOfTrailingZeros(turnRook); i < 64 - Long.numberOfLeadingZeros(turnRook); i++) {
            if ((turnRook >>> i & 1) == 1) { //if queen at square
                long rookMovesSquares = rankFileSliding(i);
                //find possible attack squares for all files, ranks, diags
                pseudoCaptureAtPos(i, rookMovesSquares, pseudoCaptures);
                pseudoQuietAtPos(i, rookMovesSquares, pseudoQuiet);
            }
        }
    }

    public void pseudoQueenMoves(long turnQueen, ArrayList<Integer> pseudoCaptures, ArrayList<Integer> pseudoQuiet) {
        for (int i = Long.numberOfTrailingZeros(turnQueen); i < 64 - Long.numberOfLeadingZeros(turnQueen); i++) {
            if ((turnQueen >>> i & 1) == 1) { //if queen at square
                long queenMoveSquares = (rankFileSliding(i) | diagSliding(i));
                //find possible attack squares for all files, ranks, diags
                //divide between tactical and quiet moves
                pseudoCaptureAtPos(i, queenMoveSquares, pseudoCaptures);
                pseudoQuietAtPos(i, queenMoveSquares, pseudoQuiet);
            }
        }
    }

    public long kingAttackGen(int i) {
        //calculate all POTENTIAL pseudo legal moves for knight at position i
        //can also be used to see if king at position i is under attack by king
        long kingPos = 0B1L << i;
        //generate bitboard for specific position
        long attackGen = (kingPos >>> 8 & ~RANK_MASKS[7]) | (kingPos << 8 & ~RANK_MASKS[0]) |
                (kingPos >>> 1 & ~FILE_MASKS[7]) | (kingPos << 1 & ~FILE_MASKS[0]) | (kingPos << 9 & ~FILE_MASKS[0]) |
                (kingPos >>> 9 & ~FILE_MASKS[7]) | (kingPos << 7 & ~FILE_MASKS[7]) | (kingPos >>> 7 & ~FILE_MASKS[0]);
        return attackGen;
    }

    @SuppressWarnings("UnusedReturnValue")
    public void pseudoKingMoves(long turnKingBits, int turnCastling,
                                ArrayList<Integer> pseudoCaptures, ArrayList<Integer> pseudoQuiet) {
        int kingPos = Long.numberOfTrailingZeros(turnKingBits);
        long kingMoveSquares = kingAttackGen(kingPos);
        //checking for tactical moves
        pseudoCaptureAtPos(kingPos, kingMoveSquares, pseudoCaptures);

        //checking for quiet moves
        pseudoQuietAtPos(kingPos, kingMoveSquares, pseudoQuiet);

        if ((turnCastling & 1) == 1) { //kingside castling
            if ((empty >>> (kingPos - 1) & empty >>> (kingPos - 2) & 1) == 1) {
                pseudoQuiet.add(addMove(kingPos, kingPos - 2, 1, 0));}
        } if ((turnCastling >>> 1 & 1) == 1) { //queenside castling
            if ((empty >>> (kingPos + 1) & empty >>> (kingPos + 2) & empty>>> (kingPos + 3) & 1) == 1) {
                pseudoQuiet.add(addMove(kingPos, kingPos + 2, 1, 0));}
        }
    }

    public static void main(String[] args) {
        moveGenTesting mg = new moveGenTesting();
        System.out.println(mg.diagSliding(31));
    }

}