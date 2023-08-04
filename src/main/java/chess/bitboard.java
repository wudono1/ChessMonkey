package chess;
import java.util.*;
@SuppressWarnings("SpellCheckingInspection")
public class bitboard {
    public long wp = 0L, wn = 0L, wb = 0L, wr = 0L, wq = 0L, wk = 0L,
            bp = 0L, bn = 0L, bb = 0L, br = 0L, bq = 0L, bk = 0L;
    public int turn;
    public long wCastle = 0L, bCastle = 0L; //2 bits each, left bit is queenside, right bit is kingside
    public int lastPawnMove;
    public int movesSinceLastPawn = -1;
    public int plyCount = 0;
    public int wkPos, bkPos;
    private final String[] arrBoard = new String[64];
    //h1 = arrBoard[0], a8 = arrBoard[63]

    // piece bitboards
    @SuppressWarnings("unused")
    public bitboard() { //default set to startpos FEN
        setBitboards("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR");
        setKingPos();
        setBoardArray();
        turn = 1;
        wCastle = 0B11L;
        bCastle = 0B11L;
        lastPawnMove = -1;
        movesSinceLastPawn = 0;
    }
    public bitboard(String FEN) {  //given a specific FEN
        String[] split = FEN.split("\\s+");
        setBitboards(split[0]);  //set bitboards based on FEN position
        setKingPos(); //set king position
        setBoardArray();
        if (Objects.equals(split[1], "w")) {turn = 1;} //set turn
        if (Objects.equals(split[1], "b")) {turn = -1;}

        if (split[2].contains("K")) {wCastle = wCastle + 0B1L;} //set castling rights
        if (split[2].contains("Q")) {wCastle = wCastle + 0B10L;}
        if (split[2].contains("k")) {bCastle = bCastle + 0B1L;}
        if (split[2].contains("q")) {bCastle = bCastle + 0B10L;}

        Hashtable<String, Integer> fileToInt = new Hashtable<>();
        fileToInt.put("h", 0);
        fileToInt.put("g", 1);
        fileToInt.put("f", 2);
        fileToInt.put("e", 3);
        fileToInt.put("d", 4);
        fileToInt.put("c", 5);
        fileToInt.put("b", 6);
        fileToInt.put("a", 7);

        if (!Objects.equals(split[3], "-")) {
            lastPawnMove = lastPawnMove + fileToInt.get(split[3].substring(0, 1)); //set last pawn move
            lastPawnMove = lastPawnMove + ((Character.getNumericValue(split[3].charAt(2)) - 1) * 8);
        } else { lastPawnMove = -1;}
        movesSinceLastPawn = Character.getNumericValue(split[4].charAt(0)); //set plyCount
        plyCount = (Character.getNumericValue(split[5].charAt(0)) - 1) * 2;
        if (turn == -1) { plyCount = plyCount + 1;}

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
            } else if (FEN.charAt(i) != '/') {
                posCount++;
                long addPos = Long.parseUnsignedLong("1" + zeroes.substring(posCount), 2);
                if (FEN.charAt(i) == 'P') {
                    wp = wp + addPos;
                }
                if (FEN.charAt(i) == 'N') {
                    wn = wn + addPos;
                }
                if (FEN.charAt(i) == 'B') {
                    wb = wb + addPos;
                }
                if (FEN.charAt(i) == 'R') {
                    wr = wr + addPos;
                }
                if (FEN.charAt(i) == 'Q') {
                    wq = wq + addPos;
                }
                if (FEN.charAt(i) == 'K') {
                    wk = wk + addPos;
                }
                if (FEN.charAt(i) == 'p') {
                    bp = bp + addPos;
                }
                if (FEN.charAt(i) == 'n') {
                    bn = bn + addPos;
                }
                if (FEN.charAt(i) == 'b') {
                    bb = bb + addPos;
                }
                if (FEN.charAt(i) == 'r') {
                    br = br + addPos;
                }
                if (FEN.charAt(i) == 'q') {
                    bq = bq + addPos;
                }
                if (FEN.charAt(i) == 'k') {
                    bk = bk + addPos;
                }
            }
        }

    }


    @SuppressWarnings("unused")
    public boolean isInt (String str){
        if (str.isEmpty()) {
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
        String startPos = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        int side = 1;
        bitboard btb = new bitboard(startPos);
        btb.printArrayBoard();
        /*System.out.println("last pawn move: " + btb.lastPawnMove);
        System.out.println("moves since last pawn move: " + btb.movesSinceLastPawn);
        System.out.println("plycount: " + btb.plyCount);
        System.out.println("white castle; " + btb.wCastle);
        System.out.println("black castle; " + btb.bCastle);
        System.out.println("turn: " + btb.turn);
        System.out.println("wkpos: " + btb.wkPos + " bkpos: " + btb.bkPos);*/
        moveGen moves = new moveGen();
        moves.setSquareStatus(btb.wp, btb.wn, btb.wb, btb.wr, btb.wq, btb.wk,
                btb.bp, btb.bn, btb.bb, btb.br, btb.bq, btb.bk, side);


        /*ArrayList<move> pawnMoves = new ArrayList<move>();
        byte lastPawnMove = 0;
        pawnMoves = moves.pseudoWhitePawn(btb.wp, lastPawnMove);
        int i = 1;
        for (move pMove : pawnMoves) {
            System.out.println(i + ": [" + pMove.start + ", " + pMove.dest + "]");
            i++;
        }*/

        ArrayList<move> pm = new ArrayList<>();
        ArrayList<move> knightMoves = moves.allPseudoKnight(btb.wn, pm);
        for (move nMove : knightMoves) {
            System.out.println("[" + nMove.start + ", " + nMove.dest + "]");


        }
    }
}