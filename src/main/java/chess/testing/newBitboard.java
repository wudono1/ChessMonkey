package chess.testing;
import java.util.*;
@SuppressWarnings("SpellCheckingInspection")

public class newBitboard {
    public long wp = 0L, wn = 0L, wb = 0L, wr = 0L, wq = 0L, wk = 0L,
            bp = 0L, bn = 0L, bb = 0L, br = 0L, bq = 0L, bk = 0L;

    //storing past bitboards for make and unmake move
    public List<Long> wpList = new ArrayList<>(), wnList = new ArrayList<>(), wbList = new ArrayList<>(),
            wrList = new ArrayList<>(), wqList = new ArrayList<>(), wkList = new ArrayList<>(),
            bpList = new ArrayList<>(), bnList = new ArrayList<>(), bbList = new ArrayList<>(),
            brList = new ArrayList<>(), bqList = new ArrayList<>(), bkList = new ArrayList<>();
    public List<Integer> wCastleList = new ArrayList<>(), bCastleList = new ArrayList<>();

    //storing move information in bitboards. ply count = length of any list.
    public List<Integer> pawnJumpList = new ArrayList<>(), pawnJumpPlyList = new ArrayList<>(),
            moveCount50List = new ArrayList<>(), turnList = new ArrayList<>(), plyCtList = new ArrayList<>();

    public int turn = 1;
    public int wCastle = 0, bCastle = 0; //2 bits each, left bit is queenside, right bit is kingside
    public int lastPawnJump = -1; //if e2-e4, lastPawnJump = e3. if e7 e5, lastPawnJump == e6
    public int pawnJumpPly = -1;  //if pawnJumpPly == plyCount + 2; lastPawnJump == -1
    public int plyCount_50Move = 0; //draw when == 100
    public int plyCount = 0; //2ply = 1 full move

    private final String[] arrBoard = new String[64];
    //h1 = arrBoard[0], a8 = arrBoard[63]

    // piece bitboards
    public newBitboard() { //default set to startpos FEN
        setBitboards("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR");
        turn = 1;
        wCastle = 0B11;
        bCastle = 0B11;
        lastPawnJump = -1;
        pawnJumpPly = -1;
        setBoardArray();

        notateLists();
    }
    public void setBitboardPos(String FEN) {  //given a specific FEN
        String[] split = FEN.split("\\s+");
        setBitboards(split[0]);  //set bitboards based on FEN position
        if (Objects.equals(split[1].toLowerCase(), "w")) {turn = 1;} //set turn
        if (Objects.equals(split[1].toLowerCase(), "b")) {turn = -1;}

        if (split[2].contains("K")) {wCastle = wCastle + 0B1;} //set castling rights
        if (split[2].contains("Q")) {wCastle = wCastle + 0B10;}
        if (split[2].contains("k")) {bCastle = bCastle + 0B1;}
        if (split[2].contains("q")) {bCastle = bCastle + 0B10;}
        checkCastlingRights();

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
            lastPawnJump = lastPawnJump + fileToInt.get(split[3].substring(0, 1)); //set last pawn move
            lastPawnJump = lastPawnJump + ((Character.getNumericValue(split[3].charAt(2)) - 1) * 8);
        } else { lastPawnJump = -1;}
        plyCount_50Move = Character.getNumericValue(split[4].charAt(0)); //set plyCount
        plyCount = (Character.getNumericValue(split[5].charAt(0)) - 1) * 2;
        if (turn == -1) { plyCount = plyCount + 1;}
        notateLists();
    }

    public void checkCastlingRights() { //checking validity of input FEN castling rights
        if ((wk>>>3 & 1L) != 1) {
            wCastle = 0;
        } else {
            if ((wr & 1L) != 1) { wCastle = wCastle & 0b10; } //if rook not at h1, king cannot castle kingside
            if ((wr>>>7 & 1L) != 1) { wCastle = wCastle & 0b01; }} //if rook not at h1, king cannot castle kingside
        if ((bk>>>59 & 1L) != 1) {
            bCastle = 0;
        } else {
            if ((br>>>56 & 1L) != 1) { bCastle = bCastle & 0b10; } //if rook not at h1, king cannot castle kingside
            if ((br>>>63 & 1L) != 1) { bCastle = bCastle & 0b01; }} //if rook not at h1, king cannot castle kingside
    }

    public void notateLists() { //adds current bitboard position to notation lists
        wpList.add(wp); wnList.add(wn); wbList.add(wb); wrList.add(wr); wqList.add(wq); wkList.add(wk);
        bpList.add(bp); bnList.add(bn); bbList.add(bb); brList.add(br); bqList.add(bq); bkList.add(bk);
        wCastleList.add(wCastle); bCastleList.add(bCastle);
        pawnJumpList.add(lastPawnJump); pawnJumpPlyList.add(pawnJumpPly);
        moveCount50List.add(plyCount_50Move); plyCtList.add(plyCount); turnList.add(turn);
    }

    @SuppressWarnings("unused")
    public void makeMove(int turnMove) {//for making move. Assumes input turnMove is valid
        int start = turnMove & 0b111111;
        int dest = turnMove >>> 6 & 0b111111;
        int moveType = turnMove >>> 12 & 0b11;
        int promo = turnMove >>> 14 & 0b11;
        plyCount++;
        plyCount_50Move++;
        if (turn == 1) {//white to move
            if (((bp | bn | bb | br | bq) & 1L<<dest) != 0) {plyCount_50Move = 0;}
            if ((wp >>> start & 1) == 1) { //for pawn moves
                plyCount_50Move = 0;
                if (moveType == 3) { //if promotion
                    wp = wp & ~(1L << (start)); //change turn pawn bitboards
                    if (promo == 0) {wn = wn | (1L << dest); } //changing promotion piece bitboards
                    if (promo == 1) {wb = wb | (1L << dest); }
                    if (promo == 2) {wr = wr | (1L << dest); }
                    if (promo == 3) {wq = wq | (1L << dest); }
                } else {
                    wp = (wp & ~(1L << (start))) | (1L << (dest)); // making the pseudomove
                    if (moveType == 2) {bp = bp & ~(1L << (dest - 8));}} //if en passant
                if (dest == start + 16) {
                    lastPawnJump = dest - 8;
                    pawnJumpPly = plyCount;
                }
            }
            //knight
            if ((wn >>> start & 1) == 1) {wn = (wn & ~(1L << (start))) | (1L << (dest));}
            //bishop
            if ((wb >>> start & 1) == 1) {wb = (wb & ~(1L << (start))) | (1L << (dest));}
            //rook
            if ((wr >>> start & 1) == 1) {wr = (wr & ~(1L << (start))) | (1L << (dest));}
            //queen
            if ((wq >>> start & 1) == 1) {wq = (wq & ~(1L << (start))) | (1L << (dest));}
            //king
            if ((wk >>> start & 1) == 1) {
                wk = (wk & ~(1L << (start))) | (1L << (dest));
                if (moveType == 1) { //if castling
                    //kingside castling
                    if (start > dest) { wr = (wr & ~(1L << (dest - 1))) | (1L << (dest + 1)); }
                    //queenside castling
                    if (start < dest) { wr = (wr & ~(1L << (dest + 2))) | (1L << (dest - 1)); }
                }
            }
            //checking if capture at dest square
            if ((bp >>> dest & 1) == 1) {bp = bp & ~(1L << (dest)); } //enemy pawn cature
            if ((bn >>> dest & 1) == 1) {bn = bn & ~(1L << (dest)); } //enemy knight capture
            if ((bb >>> dest & 1) == 1) {bb = bb & ~(1L << (dest)); } //enemy bishop captured
            if ((br >>> dest & 1) == 1) {br = br & ~(1L << (dest)); } //enemy rook captured
            if ((bq >>> dest & 1) == 1) {bq = bq & ~(1L << (dest)); } //enemy queen captured

        }if (turn == -1) { //black to move
            if (((wp | wn | wb | wr | wq) & 1L<< dest) != 0) {plyCount_50Move = 0;}
            if ((bp >>> start & 1) == 1) { //for pawn moves
                plyCount_50Move = 0;
                if (moveType == 3) { //if promotion
                    bp = bp & ~(1L << (start)); //change turn pawn bitboards
                    if (promo == 0) {bn = bn | (1L << dest); } //changing promotion piece bitboards
                    if (promo == 1) {bb = bb | (1L << dest); }
                    if (promo == 2) {br = br | (1L << dest); }
                    if (promo == 3) {bq = bq | (1L << dest); }
                } else {
                    bp = (bp & ~(1L << (start))) | (1L << (dest)); // making the pseudomove
                    if (moveType == 2) {wp = wp & ~(1L << (dest + 8));}} //if en passant
                if (dest == start - 16) {
                    lastPawnJump = dest + 8;
                    pawnJumpPly = plyCount;
                }
            }
            //knight
            if ((bn >>> start & 1) == 1) {bn = (bn & ~(1L << (start))) | (1L << (dest));}
            //bishop
            if ((bb >>> start & 1) == 1) {bb = (bb & ~(1L << (start))) | (1L << (dest));}
            //rook
            if ((br >>> start & 1) == 1) {br = (br & ~(1L << (start))) | (1L << (dest));}
            //queen
            if ((bq >>> start & 1) == 1) {bq = (bq & ~(1L << (start))) | (1L << (dest));}
            //king
            if ((bk >>> start & 1) == 1) {
                bk = (bk & ~(1L << (start))) | (1L << (dest));
                if (moveType == 1) { //if castling
                    //kingside castling
                    if (start > dest) { br = (br & ~(1L << (dest - 1))) | (1L << (dest + 1)); }
                    //queenside castling
                    if (start < dest) { br = (br & ~(1L << (dest + 2))) | (1L << (dest - 1)); }
                }
            }
            //checking if capture at dest square
            if ((wp >>> dest & 1) == 1) {wp = wp & ~(1L << (dest)); } //enemy pawn cature
            if ((wn >>> dest & 1) == 1) {wn = wn & ~(1L << (dest)); } //enemy knight capture
            if ((wb >>> dest & 1) == 1) {wb = wb & ~(1L << (dest)); } //enemy bishop captured
            if ((wr >>> dest & 1) == 1) {wr = wr & ~(1L << (dest)); } //enemy rook captured
            if ((wq >>> dest & 1) == 1) {wq = wq & ~(1L << (dest)); } //enemy queen captured
        }
        if (lastPawnJump != -1 & plyCount == pawnJumpPly + 1) {lastPawnJump = -1; pawnJumpPly = -1;}
        checkCastlingRights();
        turn = turn * -1;
        notateLists();
    }

    public void unmakeMove1Ply() { //half move (1ply) unmake
        if (plyCtList.size() > 1) {
            int i = plyCtList.size() - 1;
            //remove last move from notation lists
            wpList.remove(i); wnList.remove(i); wbList.remove(i); wrList.remove(i); wqList.remove(i); wkList.remove(i);
            bpList.remove(i); bnList.remove(i); bbList.remove(i); brList.remove(i); bqList.remove(i); bkList.remove(i);
            wCastleList.remove(i); bCastleList.remove(i); pawnJumpList.remove(i); pawnJumpPlyList.remove(i);
            moveCount50List.remove(i); turnList.remove(i); plyCtList.remove(i);
            wp = wpList.get(i-1); wn = wnList.get(i-1); wb = wbList.get(i-1); wr = wrList.get(i-1); wq = wqList.get(i-1);
            wk = wkList.get(i-1);
            bp = bpList.get(i-1); bn = bnList.get(i-1); bb = bbList.get(i-1); br = brList.get(i-1); bq = bqList.get(i-1);
            bk = bkList.get(i-1);
            wCastle = wCastleList.get(i-1); bCastle = bCastleList.get(i-1);
            lastPawnJump = pawnJumpList.get(i-1); pawnJumpPly = pawnJumpPlyList.get(i-1);
            plyCount_50Move = moveCount50List.get(i-1); turn = turnList.get(i-1); plyCount = plyCtList.get(i-1);
        }
    }

    public void unmakeMove2Ply() { //full move (2ply) unmake
        if (plyCtList.size() > 2) { unmakeMove1Ply(); unmakeMove1Ply();}
        else if (plyCtList.size() > 1) { unmakeMove1Ply();}
    }



    public void printArrayBoard() {
        setBoardArray();
        for (int i = 7; i > -1; i--) {
            for (int j = 7; j > -1; j--) {
                if (j == 7) {
                    System.out.printf("%d |", i + 1);
                }
                System.out.print(arrBoard[8 * i + j] + "| ");
                if ((8 * i + j) % 8 == 0) {
                    System.out.println((i + j) * 8);
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
        String startPos = "rnbqkbnr/pppp1ppp/4p3/8/8/N6P/PPPPPPP1/R1BQKBNR b KQkq - 1 2";

        newBitboard btb = new newBitboard();
        btb.printArrayBoard();
        newMoveGen moves = new newMoveGen();
        System.out.println();

        ArrayList<Integer> legalMoves = moves.moveGenerator(btb.wp, btb.wn, btb.wb, btb.wr, btb.wq, btb.wk, btb.bp, btb.bn,
                btb.bb, btb.br, btb.bq, btb.bk, btb.turn, btb.wCastle, btb.bCastle, btb.lastPawnJump);
        int j = 0;
        for (int pMove : legalMoves) {
            int start = pMove & 0b111111;
            int dest = pMove >>> 6 & 0b111111;
            int moveType = pMove >>> 12 & 0b11;
            int promo = pMove >>> 14 & 0b11;
            System.out.print("[ " + start + ", " + dest + "], ");
            j++;
            if (j % 10 == 0) { System.out.println();}
        }
        System.out.println();



    }
}