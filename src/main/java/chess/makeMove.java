package chess;

import java.util.HashMap;

public class makeMove {

    public HashMap<Long, Long> bitboardChange(move pMove, long tp, long tn, long tb, long tr, long tq, long tk, long ep, long en, long eb,
                                              long er, long eq, long ek, int turn) {
        //changing bitboards for one pseudoMove, will store original copies in hashmap and return
        HashMap<Long, Long> changedBitboards= new HashMap<Long, Long>(5);
        //maps pseudolegal bitboards to original bitboard to undo move

        if ((tp >>> pMove.start & 1) == 1) {
            long oStart = tp; //storing original bitboard position
            if (pMove.moveType == 3) { //if promotion
                tp = tp - (1L << (pMove.start)); //change turn pawn bitboards
                changedBitboards.put(tp, oStart);
                if (pMove.promo == 2) { //changing promotion piece bitboards
                    long origPromoPc = tn;
                    tn = tn + (1L << pMove.dest);
                    changedBitboards.put(tn, origPromoPc);
                } if (pMove.promo == 3) {
                    long origPromoPc = tb;
                    tb = tb + (1L << pMove.dest);
                    changedBitboards.put(tb, origPromoPc);
                } if (pMove.promo == 4) {
                    long origPromoPc = tr;
                    tr = tr + (1L << pMove.dest);
                    changedBitboards.put(tr, origPromoPc);
                } if (pMove.promo == 5) {
                    long origPromoPc = tq;
                    tq = tq + (1L << pMove.dest);
                    changedBitboards.put(tq, origPromoPc);
                }
            }
            else {
                tp = tp - (1L << (pMove.start)) + (1L << (pMove.dest)); // making the pseudomove
                changedBitboards.put(tp, oStart);
                if (pMove.moveType == 2) { //if en passant
                    long origEPawn; //original enemy pawn configuration
                    if (turn == 1) { //for changing enemy pawn bitboards
                        origEPawn = ep;
                        ep = ep - (1L << (pMove.dest - 8));
                        changedBitboards.put(ep, origEPawn);
                    }
                    if (turn == -1) {
                        origEPawn = ep;
                        ep = ep - (1L << (pMove.dest + 8));
                        changedBitboards.put(ep, origEPawn);}
                }
            }
        } if ((tn >>> pMove.start & 1) == 1) { //knight
            long oStart = tn;
            tn = tn - (1L << (pMove.start)) + (1L << (pMove.dest));
            changedBitboards.put(tn, oStart);
        } if ((tb >>> pMove.start & 1) == 1) { //bishop
            long oStart = tb;
            tb = tb - (1L << (pMove.start)) + (1L << (pMove.dest));
            changedBitboards.put(tb, oStart);
        } if ((tr >>> pMove.start & 1) == 1) { //rook
            long oStart = tr;
            tr = tr - (1L << (pMove.start)) + (1L << (pMove.dest));
            changedBitboards.put(tr, oStart);
        } if ((tq >>> pMove.start & 1) == 1) { //queen
            long oStart = tq;
            tq = tq - (1L << (pMove.start)) + (1L << (pMove.dest));
            changedBitboards.put(tq, oStart);
        } if ((tk >>> pMove.start & 1) == 1) { //king
            long oStart = tk;
            tk = tk - (1L << (pMove.start)) + (1L << (pMove.dest));
            changedBitboards.put(tk, oStart);
            if (pMove.moveType == 1) { //if castling
                if (pMove.start > pMove.dest) { //kingside castling
                    long oRook = tr;
                    tr = tr - (1L<<(pMove.dest - 1)) + (1L << (pMove.dest + 1));
                    changedBitboards.put(tr, oRook);
                } if (pMove.start < pMove.dest) { //queenside castling
                    long oRook = tr;
                    tr = tr - (1L<<(pMove.dest + 2)) + (1L << (pMove.dest - 1));
                    changedBitboards.put(tr, oRook);
                }
            }
        }

        //checking if capture at dest square
        if ((ep >>> pMove.dest & 1) == 1) { //enemy pawn captured
            long oDest = ep;
            ep = ep - (1L << (pMove.dest));
            changedBitboards.put(ep, oDest);
        } if ((en >>> pMove.dest & 1) == 1) { //enemy knight capture
            long oDest = en;
            en = en - (1L << (pMove.dest));
            changedBitboards.put(en, oDest);
        } if ((eb >>> pMove.dest & 1) == 1) { //enemy bishop captured
            long oDest = eb;
            eb = eb - (1L << (pMove.dest));
            changedBitboards.put(eb, oDest);
        } if ((er >>> pMove.dest & 1) == 1) { //enemy rook captured
            long oDest = er;
            er = er - (1L << (pMove.dest));
            changedBitboards.put(er, oDest);
        } if ((eq >>> pMove.dest & 1) == 1) { //enemy queen captured
            long oDest = eq;
            eq = eq - (1L << (pMove.dest));
            changedBitboards.put(eq, oDest);
        }

        return changedBitboards;

    }
}
