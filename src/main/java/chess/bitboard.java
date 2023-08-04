package chess;
import java.util.*;
@SuppressWarnings("SpellCheckingInspection")
public class bitboard {
    public long wp = 0L, wn = 0L, wb = 0L, wr = 0L, wq = 0L, wk = 0L,
            bp = 0L, bn = 0L, bb = 0L, br = 0L, bq = 0L, bk = 0L;
    public int wkPos, bkPos;
    public int wCastle, bCastle; //2 bits each, left bit is queenside, right bit is kingside
    private String[] arrBoard = new String[64];
    //h1 = arrBoard[0], a8 = arrBoard[63]

    // piece bitboards
    public bitboard() { //default set to startpos FEN
        setBitboards("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR");
        setKingPos();
        setBoardArray();
        wCastle = 0B11;
        bCastle = 0B11;
    }
    public bitboard(String FEN) {  //given a specific FEN
        setBitboards(FEN);
        setKingPos();
        setBoardArray();
        wCastle = 0B11;
        bCastle = 0B11;
        //SET WHITE AND BLACK CASTLE!!!!!!!!
    }

    public String[] getArrayBoard() {
        return arrBoard;
    }

    public void printArrayBoard() {
        for (int i = 7; i > -1; i--) {
            for (int j = 7; j > -1; j--) {
                if (j == 7) {
                    System.out.printf("%d |", i + 1);
                }
                System.out.print(arrBoard[8 * i + j] + "| ");
                if ((8 * i + j) % 8 == 0) {
                    System.out.println();
                }
            }
        }
        System.out.println("   a  b  c  d  e  f  g  h");
    }
    public void setBoardArray() {
        for (int i = 0; i < 64; i++) {
            if ((wp >>> i & 0B1) == 0B1) {
                arrBoard[i] = "P";
            } else if ((wn >>> i & 0B1) == 0B1) {
                arrBoard[i] = "N";
            } else if ((wb >>> i & 0B1) == 0B1) {
                arrBoard[i] = "B";
            } else if ((wr >>> i & 0B1) == 0B1) {
                arrBoard[i] = "R";
            } else if ((wq >>> i & 0B1) == 0B1) {
                arrBoard[i] = "Q";
            } else if ((wk >>> i & 0B1) == 0B1) {
                arrBoard[i] = "K";
            } else if ((bp >>> i & 0B1) == 0B1) {
                arrBoard[i] = "p";
            } else if ((bn >>> i & 0B1) == 0B1) {
                arrBoard[i] = "n";
            } else if ((bb >>> i & 0B1) == 0B1) {
                arrBoard[i] = "b";
            } else if ((br >>> i & 0B1) == 0B1) {
                arrBoard[i] = "r";
            } else if ((bq >>> i & 0B1) == 0B1) {
                arrBoard[i] = "q";
            } else if ((bk >>> i & 0B1) == 0B1) {
                arrBoard[i] = "k";
            } else {
                arrBoard[i] = "0";
            }
        }
    }

    public void setKingPos() {
        for (int i = 0; i < 64; i++) {
            if (wk >>> i == 1) { wkPos = i; }
            if (bk >>> i == 1) { bkPos = i; }
        }
    }

    public void setBitboards (String FEN){  //FEN is strictly current position for now
        int posCount = -1;
        String zeroes = "000000000000000000000000000000000000000000000000000000000000000"; //length 63 0's
        for (int i = 0; i < FEN.length(); i++) {
            if (isInt(FEN.substring(i, i + 1))) {
                posCount = posCount + Integer.parseInt(FEN.substring(i, i + 1));
            } else if (!FEN.substring(i, i + 1).equals("/")) {
                posCount++;
                long addPos = Long.parseUnsignedLong("1" + zeroes.substring(posCount), 2);
                if (FEN.substring(i, i + 1).equals("P")) {
                    wp = wp + addPos;
                }
                if (FEN.substring(i, i + 1).equals("N")) {
                    wn = wn + addPos;
                }
                if (FEN.substring(i, i + 1).equals("B")) {
                    wb = wb + addPos;
                }
                if (FEN.substring(i, i + 1).equals("R")) {
                    wr = wr + addPos;
                }
                if (FEN.substring(i, i + 1).equals("Q")) {
                    wq = wq + addPos;
                }
                if (FEN.substring(i, i + 1).equals("K")) {
                    wk = wk + addPos;
                }
                if (FEN.substring(i, i + 1).equals("p")) {
                    bp = bp + addPos;
                }
                if (FEN.substring(i, i + 1).equals("n")) {
                    bn = bn + addPos;
                }
                if (FEN.substring(i, i + 1).equals("b")) {
                    bb = bb + addPos;
                }
                if (FEN.substring(i, i + 1).equals("r")) {
                    br = br + addPos;
                }
                if (FEN.substring(i, i + 1).equals("q")) {
                    bq = bq + addPos;
                }
                if (FEN.substring(i, i + 1).equals("k")) {
                    bk = bk + addPos;
                }
            }
        }

    }


    @SuppressWarnings("unused")
    public boolean isInt (String str){
        if (str.length() < 1) {
            return false;
        } else {
            try {
                int test = Integer.parseInt(str);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
    }

    public static void main(String[] args) {
        String startPos = "rnbqkbnr/pppppppp/8/8/8/8/PP1P1PPP/R2QKBNR";
        int side = 1;
        bitboard btb = new bitboard(startPos);
        btb.printArrayBoard();
        moveGen moves = new moveGen();
        moves.setSquareStatus(btb.wp, btb.wn, btb.wb, btb.wr, btb.wq, btb.wk,
                btb.bp, btb.bn, btb.bb, btb.br, btb.bq, btb.bk, side);
        System.out.println(btb.wq);
        int pos = Long.numberOfTrailingZeros(btb.wq);
        System.out.println(pos);



        /*ArrayList<move> pawnMoves = new ArrayList<move>();
        byte lastPawnMove = 0;
        pawnMoves = moves.pseudoWhitePawn(btb.wp, lastPawnMove);
        int i = 1;
        for (move pMove : pawnMoves) {
            System.out.println(i + ": [" + pMove.start + ", " + pMove.dest + "]");
            i++;
        }*/

        ArrayList<move> rookMoves = moves.pseudoRook(btb.wr);
        for (move nMove : rookMoves) {
            System.out.println("[" + nMove.start + ", " + nMove.dest + "]");


        }
    }
}