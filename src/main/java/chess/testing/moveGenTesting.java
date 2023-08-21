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
public class moveGenTesting {
    public ArrayList<Integer> tacticalMoves = new ArrayList<>();
    public ArrayList<Integer> quietMoves = new ArrayList<>();
    public ArrayList<Integer> checks = new ArrayList<>();

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
    static final long RANK_4 = 0B0000000000000000000000000000000011111111000000000000000000000000L;
    static final long RANK_5 = 0B0000000000000000000000001111111100000000000000000000000000000000L;
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

    public ArrayList<Integer> getTacticalMoves() {
        return tacticalMoves;
    }
    public ArrayList<Integer> getQuietMoves() {
        return quietMoves;
    }

    public ArrayList<Integer> getAllMoves() {
        ArrayList<Integer> allMoves = tacticalMoves;
        allMoves.addAll(quietMoves);
        return allMoves;
    }

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

    public void clearMoves() {
        tacticalMoves.clear();
        quietMoves.clear();
        checks.clear();
    }

    public void generatePseudoLegal(long tp, long tn, long tb, long tr, long tq, long tk, int lastPawnJump,
                                                  int turnCastle, int turn) {
        clearMoves();
        if (turn == 1) { pseudoWhitePawn(tp, lastPawnJump);}
        if (turn == -1) { pseudoBlackPawn(tp, lastPawnJump); }
        allPseudoKnight(tn);
        pseudoBishop(tb);
        pseudoRook(tr);
        pseudoQueen(tq);
        pseudoKing(tk, turnCastle);

        //generates pseudo legal moves and stores to legalMoves
    }
    public void moveGenerator(long wp, long wn, long wb, long wr, long wq, long wk, long bp, long bn, long bb,
                                            long br, long bq, long bk, int turn, int wCastle, int bCastle, int lastPawnJump) {
        setSquareStatus(wp, wn, wb, wr, wq, wk, bp, bn, bb, br, bq, bk, turn);
        if (turn == 1) { //if white to move
            generatePseudoLegal(wp, wn, wb, wr, wq, wk, lastPawnJump, wCastle, turn);
            int i = 0;
            int lstSize = tacticalMoves.size();
            while (i < lstSize) {
                if (checkLegality(tacticalMoves.get(i), wp, wn, wb, wr, wq, wk, bp, bn, bb, br, bq, bk, turn)) {i++;}
                else {
                    tacticalMoves.remove(i);
                    lstSize = tacticalMoves.size();}
            }
            lstSize = quietMoves.size();
            while (i < lstSize) {
                if (checkLegality(quietMoves.get(i), wp, wn, wb, wr, wq, wk, bp, bn, bb, br, bq, bk, turn)) {i++;}
                else {
                    quietMoves.remove(i);
                    lstSize = quietMoves.size();}
            }
        } if (turn == -1) { //if black to move
            generatePseudoLegal(bp, bn, bb, br, bq, bk, lastPawnJump, bCastle, turn);
            int i = 0;
            int lstSize = tacticalMoves.size();
            while (i < lstSize) {
                if (checkLegality(tacticalMoves.get(i), bp, bn, bb, br, bq, bk, wp, wn, wb, wr, wq, wk, turn)) {i++;}
                else {
                    tacticalMoves.remove(i);
                    lstSize = tacticalMoves.size();}
            }
            lstSize = quietMoves.size();
            while (i < lstSize) {
                if (checkLegality(quietMoves.get(i), bp, bn, bb, br, bq, bk, wp, wn, wb, wr, wq, wk, turn)) {i++;}
                else {
                    quietMoves.remove(i);
                    lstSize = quietMoves.size();}
            }
        }
        //legalMoves initially contains all legal and pseudolegal, then trimmed down to legal
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
                ((rankFileSliding(sq) & (er| eq)) != 0) | ((kingAttackGen(sq) & ek) != 0);
    }

    @SuppressWarnings("DuplicateExpressions")
    public Boolean checkLegality(int pMove, long tp, long tn, long tb, long tr, long tq, long tk, long ep, long en,
                                 long eb, long er, long eq, long ek, int turn) {
        //takes a pseudolegal move and checks if it is legal
        //maps pseudolegal bitboards to original bitboard to undo move

        //&~ = taking piece off bitboard
        //| = adding piece to bitboard
        int start = pMove & 0b111111;
        int dest = pMove >>> 6 & 0b111111;
        int moveType = pMove >>> 12 & 0b11;
        int promo = pMove >>> 14 & 0b11;

        if ((tp >>> start & 1) == 1) {
            switch (moveType) { //checking pawn move move (normal, en passant, promotion
                case 0 -> tp = (tp & ~(1L << (start))) | (1L << (dest)); //if normal move
                case 2 ->{
                    tp = (tp & ~(1L << (start))) | (1L << (dest));
                    if (turn == 1) { //for changing enemy pawn bitboards
                        ep = ep & ~(1L << (dest - 8));
                    }
                    if (turn == -1) {
                        ep = ep & ~(1L << (dest + 8));}
                }
                case 3 -> { //if promotion
                    tp = tp & ~(1L << (start)); //change turn pawn bitboards
                    switch (promo) { //changing promotion piece bitboards is applicable
                        case 0 -> tn = tn | (1L << dest);
                        case 1 -> tb = tb | (1L << dest);
                        case 2 -> tr = tr | (1L << dest);
                        case 3 -> tq = tq | (1L << dest);
                    }
                }
            }
            /*if (moveType == 3) { //if promotion
                tp = tp & ~(1L << (start)); //change turn pawn bitboards
                switch (promo){ //changing promotion piece bitboards is applicable
                    case 0-> tn = tn | (1L << dest);
                    case 1-> tb = tb | (1L << dest);
                    case 2-> tr = tr | (1L << dest);
                    case 3-> tq = tq | (1L << dest);
                }
            }
            else {
                tp = (tp & ~(1L << (start))) | (1L << (dest)); // making the pseudomove
                if (moveType == 2) { //if en passant
                    if (turn == 1) { //for changing enemy pawn bitboards
                        ep = ep & ~(1L << (dest - 8));
                    }
                    if (turn == -1) {
                        ep = ep & ~(1L << (dest + 8));}
                }
            }*/
        } if ((tn >>> start & 1) == 1) { //knight
            tn = (tn & ~(1L << (start))) | (1L << (dest));
        } if ((tb >>> start & 1) == 1) { //bishop
            tb = (tb & ~(1L << (start))) | (1L << (dest));
        } if ((tr >>> start & 1) == 1) { //rook
            tr = (tr & ~(1L << (start))) | (1L << (dest));
        } if ((tq >>> start & 1) == 1) { //queen
            tq = (tq & ~(1L << (start))) | (1L << (dest));
        } if ((tk >>> start & 1) == 1) { //king
            tk = (tk & ~(1L << (start))) | (1L << (dest));
            switch (moveType) {
                case 1 -> { //if castling
                    if (squareInCheck(start, ep, en, eb, er, eq, ek, turn)) {return false;}
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
                case 0 -> {}
            }
            /*if (moveType == 1) { //if castling
                if (squareInCheck(start, ep, en, eb, er, eq, ek, turn)) {return false;}
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
            }*/
        }

        //checking if capture at dest square
        switch ((int)(ep >>> dest & 1)) { //enemy pawn captured
            case 0 -> {}
            case 1 -> ep = ep & ~(1L << (dest));
        } switch ((int)(en >>> dest & 1)) { // enemy knight captured
            case 0 -> {}
            case 1 -> en = en & ~(1L << (dest));
        } switch ((int)(eb >>> dest & 1)) { // enemy bishop captured
            case 0 -> {}
            case 1 -> eb = eb & ~(1L << (dest));
        } switch ((int)(er >>> dest & 1)) { // enemy rook captured
            case 0 -> {}
            case 1 -> er = er & ~(1L << (dest));
        } switch ((int)(eq >>> dest & 1)) { // enemy queen captured
            case 0 -> {}
            case 1 -> eq = eq & ~(1L << (dest));
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
        turnPieces = origTPs;
        enemyPieces = origEnemy;
        empty = origEmpty;
        occupied = origOcc;
        return true; // if no checks found, return true
    }

    public long rankFileSliding(int pos) {  //horizontal and vertical sliding
        long slider = 0x1L << pos;
        long rankOccupancy = RANK_MASKS[pos / 8] & occupied; //horizontal piece slide
        /*long posRankAttacks = (rankOccupancy ^ (rankOccupancy - 2 * slider)) & (enemyPieces | empty)
                & RANK_MASKS[pos / 8]; //positive rank
                ^^^example    */
        /*long negRankAttacks = (rankOccupancy ^ Long.reverse(Long.reverse(rankOccupancy) - 2 * Long.reverse(slider))) &
                (enemyPieces | empty) & RANK_MASKS[pos / 8]; //negative rank   */
        long posNegRankAttacks = ((rankOccupancy - 2 * slider) ^ Long.reverse(Long.reverse(rankOccupancy) - 2 * Long.reverse(slider))) &
                RANK_MASKS[pos / 8];
        long fileOccupancy = FILE_MASKS[pos % 8] & occupied; //check file
        //long posFileAttacks = (fileOccupancy ^ (fileOccupancy - 2 * slider)) & (enemyPieces | empty) & FILE_MASKS[pos % 8];
        //pos file
        /*long negFileAttacks = (fileOccupancy ^ Long.reverse(Long.reverse(fileOccupancy) - 2 * Long.reverse(slider))) &
                (enemyPieces | empty) & FILE_MASKS[pos % 8]; //
                ^^^ example neg file*/
        long posNegFileAttacks = ((fileOccupancy - 2 * slider) ^ Long.reverse(Long.reverse(fileOccupancy) - 2 * Long.reverse(slider))) &
                FILE_MASKS[pos % 8];
        return (posNegRankAttacks| posNegFileAttacks);
    }

    public long diagSliding(int pos) {
        long slider = 0x1L << pos;
        long diag_occ = occupied & TR_BL_DIAG_MASKS[(pos / 8) + (pos % 8)]; //first check top right bottom left diag
        long posNeg_TRBL = ((diag_occ - 2 * slider) ^ Long.reverse(Long.reverse(diag_occ) - 2 * Long.reverse(slider))) &
                TR_BL_DIAG_MASKS[(pos / 8) + (pos % 8)];
        /*long posAttacks_TRBL = (diag_occ ^ (diag_occ - 2 * slider)) & (enemyPieces | empty) &
                (TR_BL_DIAG_MASKS[(pos / 8) + (pos % 8)]);
        long negAttacks_TRBL = (diag_occ ^ Long.reverse((Long.reverse(diag_occ) - 2 * Long.reverse(slider)))) &
                (enemyPieces | empty) & TR_BL_DIAG_MASKS[(pos / 8) + (pos % 8)];      */
        diag_occ = occupied & TL_BR_DIAG_MASKS[(pos / 8) + 7 - (pos % 8)]; //check top left bot right diag
        long posNeg_TLBR = ((diag_occ - 2 * slider) ^ Long.reverse(Long.reverse(diag_occ) - 2 * Long.reverse(slider))) &
                TL_BR_DIAG_MASKS[(pos / 8) + 7 - (pos % 8)];
        /*long posAttacks_TLBR = (diag_occ ^ (diag_occ - 2 * slider)) & (enemyPieces | empty) &
                TL_BR_DIAG_MASKS[(pos / 8) + 7 - (pos % 8)];
        long negAttacks_TLBR = (diag_occ ^ Long.reverse((Long.reverse(diag_occ) - 2 * Long.reverse(slider)))) &
                (enemyPieces | empty) & TL_BR_DIAG_MASKS[(pos / 8) + 7 - (pos % 8)];  */
        return (posNeg_TRBL | posNeg_TLBR);
    }

    public void promoCheckTactical(long wp, int start, int dest, long rank) {
        //given rank (rank 2 for black or rank 7 for white) if pawn at rank 2 or 7, then add promotion move
        switch ((int)((wp >>> (start) & rank >>> (start) & 1) )) {  //checks if startpos is at rank 7
            case 1 -> {
                tacticalMoves.add(addMove(start, dest, 3, 3));
                tacticalMoves.add(addMove(start, dest, 3, 2));
                tacticalMoves.add(addMove(start, dest, 3, 1));
                tacticalMoves.add(addMove(start, dest, 3, 1));
            } case 0 -> tacticalMoves.add(addMove(start, dest, 0, 0));  //if not then add normal move
        }
    }
    public void promoCheckQuiet(long wp, int start, int dest, long rank) {
        //given rank (rank 2 for black or rank 7 for white) if pawn at rank 2 or 7, then add promotion move
        switch ((int)((wp >>> (start) & rank >>> (start) & 1) )) {
            case 1 -> {
                tacticalMoves.add(addMove(start, dest, 3, 3));
                tacticalMoves.add(addMove(start, dest, 3, 2));
                tacticalMoves.add(addMove(start, dest, 3, 1));
                tacticalMoves.add(addMove(start, dest, 3, 1));
            } case 0 -> quietMoves.add(addMove(start, dest, 0, 0));
        }

        /*if ((wp >>> (start) & rank >>> (start) & 1) == 1) { //checks if startpos is at rank 7
            tacticalMoves.add(addMove(start, dest, 3, 3));
            tacticalMoves.add(addMove(start, dest, 3, 2));
            tacticalMoves.add(addMove(start, dest, 3, 1));
            tacticalMoves.add(addMove(start, dest, 3, 1));
        } else { quietMoves.add(addMove(start, dest, 0, 0)); } //if not then add normal move*/

    }

    public void pseudoWhitePawn(long wp, int lastPawnJump) {
        long pawnCaptureRight = wp & ~H_FILE & enemyPieces>>>7; //bitwise right shift 7 for right pawn captures
        long pawnCaptureLeft = wp & ~A_FILE & enemyPieces>>>9; //bitwise right shift 9 for left pawn capture
        long verticalMove8 = wp & (empty >>> 8); //bitwise right shift for pawn moving up one square
        long verticalMove16 = verticalMove8 & RANK_MASKS[1] & (empty >>> 16); //pawn vertical jump 2 squares
        long epSquare = 0L;
        if (lastPawnJump != -1) { epSquare = 1L << lastPawnJump;}

        if (((wp & ~A_FILE) & epSquare >>> 9) != 0) {
            tacticalMoves.add(addMove(lastPawnJump - 9, lastPawnJump, 2, 0));
        } if (((wp & ~H_FILE) & epSquare >>> 7) != 0) {
            tacticalMoves.add(addMove(lastPawnJump - 7, lastPawnJump, 2, 0));
        }
        //looping thru each pawn
        for (int i = Long.numberOfTrailingZeros(pawnCaptureRight); i < 64 - Long.numberOfLeadingZeros(pawnCaptureRight);
             i++) { //right pawn capture
            if ((pawnCaptureRight >>> i & 1) == 1) {promoCheckTactical(wp, i, i + 7, RANK_7);}
        } for (int i = Long.numberOfTrailingZeros(pawnCaptureLeft); i < 64 - Long.numberOfLeadingZeros(pawnCaptureLeft);
               i++) { //left pawn capture
            if ((pawnCaptureLeft >>> i & 1) == 1) {promoCheckTactical(wp, i, i + 9, RANK_7);}
        } for (int i = Long.numberOfTrailingZeros(verticalMove8); i < Long.numberOfLeadingZeros(verticalMove8); i++) {
            //pawn move up 1
            if ((verticalMove8 >>> i & 1) == 1) {promoCheckQuiet(wp, i, i + 8, RANK_7); }
        } for (int i = Long.numberOfTrailingZeros(verticalMove16); i < Long.numberOfLeadingZeros(verticalMove16); i++) {
            if ((verticalMove16 >>> i & 1) == 1) {quietMoves.add(addMove(i, i + 16, 0, 0)); }
        }

        /*for (int i = Long.numberOfTrailingZeros(wp); i < 64 - Long.numberOfLeadingZeros(wp); i++) {
            if ((wp >>> i & 1) == 1) { //if pawn exists at ith right shift
                if ((pawnCaptureRight >>> i & 1) == 1) { //check for pseudolegal right pawn capture
                    promoCheckTactical(wp, i, i + 7, RANK_7);
                } else if (((RANK_5 >>> i & NOT_H >>> i & 1) == 1) & (lastPawnJump == i + 7)  &
                        ((empty >>> (i + 7) & 1) == 1)) {
                    tacticalMoves.add(addMove(i, lastPawnJump, 2, 0));
                    //if capture square empty, check right en passant
                }
                if ((pawnCaptureLeft >>> i & 1) == 1) { //check for pseudolegal right pawn capture
                    promoCheckTactical(wp, i, i + 9, RANK_7);
                } else if (((RANK_5 >>> i & NOT_A>>> i & 1) == 1) & (lastPawnJump == i + 9) &
                        ((empty >>> (i + 9) & 1) == 1)) {
                    tacticalMoves.add(addMove(i, lastPawnJump, 2, 0));
                    //if capture square empty, check left en passant
                }

                if ((verticalMove8 >>> i & 1) == 1) { ///check if can move forward one square
                    promoCheckQuiet(wp, i, i + 8, RANK_7);
                    //check if pawn can jump 2 squares
                } if ((verticalMove16 >>> i & 1) == 1) { quietMoves.add(addMove(i, i + 16, 0, 0)); }
            }
        }*/
    }

    public void pseudoBlackPawn(long bp, int lastPawnJump) {
        long pawnCaptureLeft = bp & ~A_FILE & enemyPieces<<7; //bitwise left shift 7 for left pawn captures
        long pawnCaptureRight = bp & ~H_FILE & enemyPieces<<9; //bitwise left shift 9 for right pawn capture
        long verticalMove8 = bp & (empty << 8); //bitwise right shift for pawn moving up one square
        long verticalMove16 = verticalMove8 & RANK_MASKS[6] & (empty << 16); //pawn vertical jump 2 squares
        long epSquare = 0L;
        if (lastPawnJump != -1) { epSquare = 1L << lastPawnJump;}

        if (((bp & ~A_FILE) & epSquare << 7) != 0) {
            tacticalMoves.add(addMove(lastPawnJump + 7, lastPawnJump, 2, 0));
        } if (((bp & ~H_FILE) & epSquare << 9) != 0) {
            tacticalMoves.add(addMove(lastPawnJump + 9, lastPawnJump, 2, 0));
        }
        //looping thru each pawn
        for (int i = Long.numberOfTrailingZeros(pawnCaptureRight); i < 64 - Long.numberOfLeadingZeros(pawnCaptureRight);
             i++) { //right pawn capture
            if ((pawnCaptureRight >>> i & 1) == 1) { promoCheckTactical(bp, i, i - 9, RANK_2);}
        }
        for (int i = Long.numberOfTrailingZeros(pawnCaptureLeft); i < 64 - Long.numberOfLeadingZeros(pawnCaptureLeft);
               i++) { //left pawn capture
            if ((pawnCaptureLeft >>> i & 1) == 1) { promoCheckTactical(bp, i, i - 7, RANK_2); }
        }
        for (int i = Long.numberOfTrailingZeros(verticalMove8); i < 64 - Long.numberOfLeadingZeros(verticalMove8); i++) {
            //pawn move up 1
            if ((verticalMove8 >>> i & 1) == 1) {promoCheckQuiet(bp, i, i - 8, RANK_2); }
        }
        for (int i = Long.numberOfTrailingZeros(verticalMove16); i < 64 - Long.numberOfLeadingZeros(verticalMove16); i++) {
            if ((verticalMove16 >>> i & 1) == 1) {quietMoves.add(addMove(i, i - 16, 0, 0)); }
        }
        /*for (int i = Long.numberOfTrailingZeros(bp); i < 64 - Long.numberOfLeadingZeros(bp); i++) {
            if ((bp >>> i & 1) == 1) { //if pawn exists at ith right shift
                if ((pawnCaptureRight >>> i & 1) == 1) { //check for pseudolegal right pawn capture
                    promoCheckTactical(bp, i, i - 9, RANK_2);
                } else if (((RANK_4 >>> i & NOT_H >>> 1 & 1) == 1) & (lastPawnJump == i - 9) &
                        ((empty >>> (i - 9) & 1) == 1)) {
                    tacticalMoves.add(addMove(i, lastPawnJump, 2, 0));
                    //if capture square empty, check right en passant
                }
                if ((pawnCaptureLeft >>> i & 1) == 1) { //check for pseudolegal right pawn capture
                    promoCheckTactical(bp, i, i - 7, RANK_2);
                } else if (((RANK_4 >>> i & NOT_A >>> 1 & 1) == 1) & (lastPawnJump == i - 7) &
                        ((empty >>> (i - 7) & 1) == 1)) {
                    tacticalMoves.add(addMove(i, lastPawnJump, 2, 0));
                    //if capture square empty, check right en passant
                }

                if ((verticalMove8 >>> i & 1) == 1) { ///check if can move forward one square
                    promoCheckQuiet(bp, i, i - 8, RANK_2);
                    //check if pawn can jump 2 squares
                } if ((verticalMove16 >>> i & 1) == 1) { quietMoves.add(addMove(i, i - 16, 0, 0)); }
            }
        }*/
    }

    public void pseudoPieceAtPos(int i, long atkSqs) {
        //given list of attack squares, generate pseudo legal moves for any piece
        for (int sq = Long.numberOfTrailingZeros(atkSqs); sq < 64 - Long.numberOfLeadingZeros(atkSqs); sq++) {
            if ((atkSqs >>> sq & 1) == 1) {
                if ((empty >>> sq & 1) == 1) {
                    quietMoves.add(addMove(i, sq, 0, 0));
                } if ((enemyPieces >>> sq & 1) == 1) {
                    tacticalMoves.add(addMove(i, sq, 0, 0));
                }
            }}
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
        /*if ((nPos << 10 & (NOT_GH)) != 0) {attackGen = attackGen + (nPos << 10); }
        if ((nPos >>> 10 & (NOT_AB)) != 0) {attackGen = attackGen + (nPos >>> 10); }
        if ((nPos << 6 & (NOT_AB)) != 0) {attackGen = attackGen + (nPos << 6); }
        if ((nPos >>> 6 & (NOT_GH)) != 0) {attackGen = attackGen + (nPos >>> 6); }
        if ((nPos << 15 & NOT_A) != 0) {attackGen = attackGen + (nPos << 15); }
        if ((nPos >>> 15 & NOT_H) != 0) {attackGen = attackGen + (nPos >>> 15); }
        if ((nPos << 17 & NOT_H) != 0) {attackGen = attackGen + (nPos << 17); }
        if ((nPos >>> 17 & NOT_A) != 0) {attackGen = attackGen + (nPos >>> 17);}*/

        return attackGen;
    }

    public void allPseudoKnight(long turnKnight) {
        for (int i = Long.numberOfTrailingZeros(turnKnight); i < 64 - Long.numberOfLeadingZeros(turnKnight); i++) {
            if ((turnKnight >>> i & 1) == 1) {
                pseudoPieceAtPos(i, knightAttackGen(i));}
        }
    }

    public void pseudoBishop(long turnBishop) {
        for (int i = Long.numberOfTrailingZeros(turnBishop); i < 64 - Long.numberOfLeadingZeros(turnBishop); i++) {
            if ((turnBishop >>> i & 1) == 1) { //if queen at square
                //find possible attack squares for all files, ranks, diags
                pseudoPieceAtPos(i, diagSliding(i));
                //add all attack squares to ArrayList pseudoMoves
            }
        }
    }

    public void pseudoRook(long turnRook) {
        for (int i = Long.numberOfTrailingZeros(turnRook); i < 64 - Long.numberOfLeadingZeros(turnRook); i++) {
            if ((turnRook >>> i & 1) == 1) { //if queen at square
                //find possible attack squares for all files, ranks, diags
                pseudoPieceAtPos(i, rankFileSliding(i));
                //add all attack squares to ArrayList pseudoMoves
            }
        }
    }

    public void pseudoQueen(long turnQueen) {
        for (int i = Long.numberOfTrailingZeros(turnQueen); i < 64 - Long.numberOfLeadingZeros(turnQueen); i++) {
            if ((turnQueen >>> i & 1) == 1) { //if queen at square
                //find possible attack squares for all files, ranks, diags
                pseudoPieceAtPos(i, (rankFileSliding(i) | diagSliding(i)));
                //add all attack squares to ArrayList pseudoMoves
            }
        }
    }

    public long kingAttackGen(int i) {
        //calculate all POTENTIAL pseudo legal moves for knight at position i
        //can also be used to see if king at position i is under attack by king
        long kingPos = 0B1L << i;
        long attackGen = 0L;
        //generate bitboard for specific position
        attackGen = attackGen | (kingPos >>> 8 & ~RANK_MASKS[7]) | (kingPos << 8 & ~RANK_MASKS[0]) |
                (kingPos >>> 1 & ~FILE_MASKS[7]) | (kingPos << 1 & ~FILE_MASKS[0]) | (kingPos << 9 & ~FILE_MASKS[0]) |
                (kingPos >>> 9 & ~FILE_MASKS[7]) | (kingPos << 7 & ~FILE_MASKS[7]) | (kingPos >>> 7 & ~FILE_MASKS[0]);
        /*if ((kingPos & (~RANK_MASKS[0])) != 0) {attackGen = attackGen + (1L << (i - 8)); } //move down vertically
        if ((kingPos & (~RANK_MASKS[7])) != 0) {attackGen = attackGen + (1L << (i + 8)); } //move up vertically
        if ((kingPos & (~FILE_MASKS[0])) != 0) {attackGen = attackGen + (1L << (i - 1)); } //move right horizontal
        if ((kingPos & (~FILE_MASKS[7])) != 0) {attackGen = attackGen + (1L << (i + 1)); } //move right horizontal
        //bottom left diag
        if ((kingPos & (~FILE_MASKS[7]) & (~RANK_MASKS[0])) != 0) {attackGen = attackGen + (1L<<(i - 7)); }
        //bottom right diag
        if ((kingPos & (~FILE_MASKS[0]) & (~RANK_MASKS[0])) != 0) {attackGen = attackGen + (1L<<(i - 9)); }
        //top left diag
        if ((kingPos & (~FILE_MASKS[7]) & (~RANK_MASKS[7])) != 0) {attackGen = attackGen + (1L<<(i + 9)); }
        //top right diag
        if ((kingPos & (~FILE_MASKS[0]) & (~RANK_MASKS[7])) != 0) {attackGen = attackGen + (1L<<(i + 7)); }*/

        return attackGen;
    }

    @SuppressWarnings("UnusedReturnValue")
    public void pseudoKing(long turnKingBits, int turnCastling) {
        int kingPos = Long.numberOfTrailingZeros(turnKingBits);

        pseudoPieceAtPos(kingPos, kingAttackGen(kingPos));

        if ((turnCastling & 1) == 1) { //kingside castling
            if ((empty >>> (kingPos - 1) & empty >>> (kingPos - 2) & 1) == 1) {
                quietMoves.add(addMove(kingPos, kingPos - 2, 1, 0));}
        } if ((turnCastling >>> 1 & 1) == 1) { //queenside castling
            if ((empty >>> (kingPos + 1) & empty >>> (kingPos + 2) & empty>>> (kingPos + 3) & 1) == 1) {
                quietMoves.add(addMove(kingPos, kingPos + 2, 1, 0));}
        }

    }

    public static void main(String[] args) {
        moveGenTesting mg = new moveGenTesting();
        System.out.println(mg.diagSliding(31));
    }

}