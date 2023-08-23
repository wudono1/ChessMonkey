package chess;
import java.util.*;
@SuppressWarnings("SpellCheckingInspection")

public class bitboard {
    //piece bitboards
    public long wp = 0L, wn = 0L, wb = 0L, wr = 0L, wq = 0L, wk = 0L,
            bp = 0L, bn = 0L, bb = 0L, br = 0L, bq = 0L, bk = 0L;

    //piece counts
    public int wpCount = 0, bpCount = 0, wnCount = 0, bnCount = 0, wbCount = 0, bbCount = 0, wrCount = 0, brCount = 0,
            wqCount = 0, bqCount = 0;
    public long currentZobrist;

    //storing past bitboards for make and unmake move
    public List<Long> wpList = new ArrayList<>(), wnList = new ArrayList<>(), wbList = new ArrayList<>(),
            wrList = new ArrayList<>(), wqList = new ArrayList<>(), wkList = new ArrayList<>(),
            bpList = new ArrayList<>(), bnList = new ArrayList<>(), bbList = new ArrayList<>(),
            brList = new ArrayList<>(), bqList = new ArrayList<>(), bkList = new ArrayList<>();
    public List<Integer> wpPastCounts = new ArrayList<>(), wnPastCounts = new ArrayList<>(), wbPastCounts = new ArrayList<>(),
            wrPastCounts = new ArrayList<>(), wqPastCounts = new ArrayList<>(), bpPastCounts = new ArrayList<>(),
            bnPastCounts = new ArrayList<>(), bbPastCounts = new ArrayList<>(), brPastCounts = new ArrayList<>(),
            bqPastCounts = new ArrayList<>();
    public ArrayList<Long> zobristKeyList = new ArrayList<>();
    public List<Integer> wCastleList = new ArrayList<>(), bCastleList = new ArrayList<>();
    public Hashtable<Long, Integer> repCounter = new Hashtable<>();

    //storing move information in bitboards. ply count = length of any list.
    public List<Integer> pawnJumpList = new ArrayList<>(), pawnJumpPlyList = new ArrayList<>(),
            moveCount50List = new ArrayList<>(), turnList = new ArrayList<>(), plyCtList = new ArrayList<>();

    public int turn = 1;
    public int wCastle = 0, bCastle = 0; //2 bits each, left bit is kingside, right bit is queenside
    public int lastPawnJump = -1; //if e2-e4, lastPawnJump = e3. if e7 e5, lastPawnJump == e6
    public int pawnJumpPly = -1;  //if pawnJumpPly == plyCount + 2; lastPawnJump == -1
    public int plyCount_50Move = 0; //draw when == 100
    public int plyCount = 0; //2ply = 1 full move

    private final String[] arrBoard = new String[64];
    //h1 = arrBoard[0], a8 = arrBoard[63]

    // piece bitboards
    public bitboard() { //default set to startpos FEN
        setBitboards("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR");
        turn = 1;
        wCastle = 0B11;
        bCastle = 0B11;
        lastPawnJump = -1;
        pawnJumpPly = -1;
        setBoardArray();
        currentZobrist = zobrist.getZobristKey(wp,wn, wb, wr, wq, wk, bp, bn, bb, br, bq, bk, turn, wCastle, bCastle, lastPawnJump);
        notateLists();
        repCounter.put(currentZobrist, 1);
    }
    public void setBitboardPos(String FEN) {  //given a specific FEN
        FEN = FEN.trim();
        wp = wn = wb = wr = wq = wk = bp = bn = bb = br = bq = bk = 0;
        wpCount = 0; wnCount = 0; wbCount = 0; wrCount = 0; wqCount = 0;
        bpCount = 0; bnCount = 0; bbCount = 0; brCount = 0; bqCount = 0;
        wCastle = 0; bCastle = 0;
        clearLists();
        String[] split = FEN.split("\\s+");
        setBitboards(split[0]);  //set bitboards based on FEN position
        if (Objects.equals(split[1].toLowerCase(), "w")) {turn = 1;} //set turn
        if (Objects.equals(split[1].toLowerCase(), "b")) {turn = -1;}

        if ((wk >>> 3 & 1) == 1) {
            if (split[2].contains("K") && ((wr & 1) == 1)) { wCastle = wCastle + 0B1; } //set castling rights
            if (split[2].contains("Q") && ((wr >>> 7 & 1) == 1)) { wCastle = wCastle + 0B10; }
        } if ((bk >>> 59 & 1) == 1) {
            if (split[2].contains("k") && ((br >>> 56 & 1) == 1)) { bCastle = bCastle + 0B1; }
            if (split[2].contains("q") && ((br >>> 63 & 1) == 1)) { bCastle = bCastle + 0B10; }
        }

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
            lastPawnJump = lastPawnJump + fileToInt.get(split[3].substring(0, 1)) + 1; //set last pawn move
            lastPawnJump = lastPawnJump + ((Character.getNumericValue(split[3].charAt(1)) - 1) * 8);
        } else { lastPawnJump = -1;}
        plyCount_50Move = Character.getNumericValue(split[4].charAt(0)); //set plyCount
        plyCount = (Character.getNumericValue(split[5].charAt(0)) - 1) * 2;
        if (turn == -1) { plyCount = plyCount + 1;}
        setBoardArray();
        currentZobrist = zobrist.getZobristKey(wp, wn, wb, wr, wq, wk, bp, bn, bb, br, bq, bk, turn, wCastle, bCastle, lastPawnJump);
        notateLists();
        repCounter.put(currentZobrist, 1);
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
                arrBoard[i] = "-";
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
                    wpCount++;
                } if (FEN.charAt(i) == 'N') {
                    wn = wn + addPos;
                    wnCount++;
                } if (FEN.charAt(i) == 'B') {
                    wb = wb + addPos;
                    wbCount++;
                } if (FEN.charAt(i) == 'R') {
                    wr = wr + addPos;
                    wrCount++;
                } if (FEN.charAt(i) == 'Q') {
                    wq = wq + addPos;
                    wqCount++;
                } if (FEN.charAt(i) == 'K') {
                    wk = wk + addPos;
                } if (FEN.charAt(i) == 'p') {
                    bp = bp + addPos;
                    bpCount++;
                } if (FEN.charAt(i) == 'n') {
                    bn = bn + addPos;
                    bnCount++;
                } if (FEN.charAt(i) == 'b') {
                    bb = bb + addPos;
                    bbCount++;
                } if (FEN.charAt(i) == 'r') {
                    br = br + addPos;
                    brCount++;
                } if (FEN.charAt(i) == 'q') {
                    bq = bq + addPos;
                    bqCount++;
                } if (FEN.charAt(i) == 'k') {
                    bk = bk + addPos;
                }
            }
        }
    }

    public void checkCastlingRights() { //checking validity of input FEN castling rights
        if ((wk>>>3 & 1L) != 1) {wCastle = 0;}
        if ((bk>>>59 & 1L) != 1) {bCastle = 0;}

        if ((wr & 1) != 1) {wCastle = wCastle & (~0b01);}
        if ((wr>>>7 & 1) != 1) {wCastle = wCastle & (~0b10);}
        if ((br>>>56 & 1) != 1) {bCastle = bCastle & (~0b01);}
        if ((br>>>63 & 1) != 1) {bCastle = bCastle & (~0b10);}

    }

    public void clearLists() { //adds current bitboard position to notation lists
        wpList.clear(); wnList.clear(); wbList.clear(); wrList.clear(); wqList.clear(); wkList.clear();
        bpList.clear(); bnList.clear(); bbList.clear(); brList.clear(); bqList.clear(); bkList.clear();
        wCastleList.clear(); bCastleList.clear();

        wpPastCounts.clear(); wnPastCounts.clear(); wbPastCounts.clear(); wrPastCounts.clear();
        wqPastCounts.clear(); bpPastCounts.clear(); bnPastCounts.clear(); bbPastCounts.clear();
        brPastCounts.clear(); bqPastCounts.clear();

        pawnJumpList.clear(); pawnJumpPlyList.clear(); moveCount50List.clear(); plyCtList.clear();
        turnList.clear(); zobristKeyList.clear();
        repCounter.clear();
    }

    public void notateLists() { //adds current bitboard position to notation lists
        wpList.add(wp); wnList.add(wn); wbList.add(wb); wrList.add(wr); wqList.add(wq); wkList.add(wk);
        bpList.add(bp); bnList.add(bn); bbList.add(bb); brList.add(br); bqList.add(bq); bkList.add(bk);

        wpPastCounts.add(wpCount); wnPastCounts.add(wnCount); wbPastCounts.add(wbCount); wrPastCounts.add(wrCount);
        wqPastCounts.add(wqCount); bpPastCounts.add(bpCount); bnPastCounts.add(bnCount); bbPastCounts.add(bbCount);
        brPastCounts.add(brCount); bqPastCounts.add(bqCount);

        wCastleList.add(wCastle); bCastleList.add(bCastle);
        pawnJumpList.add(lastPawnJump); pawnJumpPlyList.add(pawnJumpPly);
        moveCount50List.add(plyCount_50Move); plyCtList.add(plyCount); turnList.add(turn);
        zobristKeyList.add(currentZobrist);
    }

    public boolean makeMove(int move) {
        int start = move & 0b111111;
        int dest = move >>> 6 & 0b111111;
        int moveType = move >>> 12 & 0b11;
        int promo = move >>> 14 & 0b11;
        if (turn == 1) {
            makeMoveFunctionWhite(start, dest, moveType, promo);
        } if (turn == -1) {
            makeMoveFunctionBlack(start, dest, moveType, promo);
        }
        if (lastPawnJump != -1 & plyCount > pawnJumpPly) {lastPawnJump = -1; pawnJumpPly = -1;}
        checkCastlingRights();
        turn = turn * -1;
        currentZobrist = zobrist.getZobristKey(wp, wn, wb, wr, wq, wk, bp, bn, bb, br, bq, bk, turn, wCastle, bCastle, lastPawnJump);
        notateLists();
        if (repCounter.containsKey(currentZobrist)) {repCounter.put(currentZobrist, repCounter.get(currentZobrist) + 1);}
        else {repCounter.put(currentZobrist, 1); }
        return (repCounter.get(currentZobrist) == 3);
    }

    public void makeMoveFunctionWhite(int start, int dest, int moveType, int promo) {
        plyCount++;
        plyCount_50Move++;
        switch (moveType) {
            case 0 -> {//regular move
                if ((wp >>> start & 1) == 1) { //pawn
                    wp = (wp & ~(1L << (start))) | (1L << (dest));
                    plyCount_50Move = 0;
                    if (dest == start + 16) {
                        lastPawnJump = dest - 8;
                        pawnJumpPly = plyCount;
                    }
                } if ((wn >>> start & 1) == 1) {wn = (wn & ~(1L << (start))) | (1L << (dest));}  //knight
                if ((wb >>> start & 1) == 1) {wb = (wb & ~(1L << (start))) | (1L << (dest));}    //bishop
                if ((wr >>> start & 1) == 1) {wr = (wr & ~(1L << (start))) | (1L << (dest));}    //rook
                if ((wq >>> start & 1) == 1) {wq = (wq & ~(1L << (start))) | (1L << (dest));}    //queen
                if ((wk >>> start & 1) == 1) {wk = (wk & ~(1L << (start))) | (1L << (dest));}    //king
            }
            case 1 -> { //castling
                wk = (wk & ~(1L << (start))) | (1L << (dest));
                //kingside castling
                if (start > dest) { wr = (wr & ~(1L << (dest - 1))) | (1L << (dest + 1)); }
                //queenside castling
                if (start < dest) { wr = (wr & ~(1L << (dest + 2))) | (1L << (dest - 1)); }
            }
            case 2 -> { //en passant
                plyCount_50Move = 0;
                wp = (wp & ~(1L << (start))) | (1L << (dest));
                bpCount--;
                bp = bp & ~(1L << (dest - 8)); //if white en passant
            }
            case 3 -> { //promotion
                plyCount_50Move = 0;
                wp = wp & ~(1L << (start)); //change turn pawn bitboards
                wpCount--;
                switch (promo) { //changing promotion piece bitboards is applicable
                    case 0 -> { //promote to knight
                        wn = wn | (1L << dest);
                        wnCount++;
                    } case 1 -> { //promote to bishop
                        wb = wb | (1L << dest);
                        wbCount++;
                    } case 2 -> { //promote to rook
                        wr = wr | (1L << dest);
                        wrCount++;
                    } case 3 -> { //promote to queen
                        wq = wq | (1L << dest);
                        wqCount++;}
                }
            }
        }
        if ((bp >>> dest & 1) == 1) {   //enemy pawn cature
            bp = bp & ~(1L << (dest));
            bpCount--;
            plyCount_50Move = 0;
        } if ((bn >>> dest & 1) == 1) { //enemy knight capture
            bn = bn & ~(1L << (dest));
            bnCount--;
            plyCount_50Move = 0;
        } if ((bb >>> dest & 1) == 1) { //enemy bishop captured
            bb = bb & ~(1L << (dest));
            bbCount--;
            plyCount_50Move = 0;
        } if ((br >>> dest & 1) == 1) { //enemy rook captured
            br = br & ~(1L << (dest));
            brCount--;
            plyCount_50Move = 0;
        } if ((bq >>> dest & 1) == 1) { //enemy queen captured
            bq = bq & ~(1L << (dest));
            bqCount--;
            plyCount_50Move = 0;
        }
    }

    public void makeMoveFunctionBlack(int start, int dest, int moveType, int promo) {
        plyCount++;
        plyCount_50Move++;
        switch (moveType) {
            case 0 -> {//regular move
                if ((bp >>> start & 1) == 1) { //pawn
                    bp = (bp & ~(1L << (start))) | (1L << (dest));
                    plyCount_50Move = 0;
                    if (dest == start - 16) {
                        lastPawnJump = dest + 8;
                        pawnJumpPly = plyCount;
                    }
                } if ((bn >>> start & 1) == 1) {bn = (bn & ~(1L << (start))) | (1L << (dest));}  //knight
                if ((bb >>> start & 1) == 1) {bb = (bb & ~(1L << (start))) | (1L << (dest));}    //bishop
                if ((br >>> start & 1) == 1) {br = (br & ~(1L << (start))) | (1L << (dest));}    //rook
                if ((bq >>> start & 1) == 1) {bq = (bq & ~(1L << (start))) | (1L << (dest));}    //queen
                if ((bk >>> start & 1) == 1) {bk = (bk & ~(1L << (start))) | (1L << (dest));}    //king
            }
            case 1 -> { //castling
                bk = (bk & ~(1L << (start))) | (1L << (dest));
                //kingside castling
                if (start > dest) { br = (br & ~(1L << (dest - 1))) | (1L << (dest + 1)); }
                //queenside castling
                if (start < dest) { br = (br & ~(1L << (dest + 2))) | (1L << (dest - 1)); }
            }
            case 2 -> { //en passant
                plyCount_50Move = 0;
                bp = (bp & ~(1L << (start))) | (1L << (dest));
                wpCount--;
                wp = wp & ~(1L << (dest + 8)); //if white en passant
            }
            case 3 -> { //promotion
                plyCount_50Move = 0;
                bp = bp & ~(1L << (start)); //change turn pawn bitboards
                bpCount--;
                switch (promo) { //changing promotion piece bitboards is applicable
                    case 0 -> { //promote to knight
                        bn = bn | (1L << dest);
                        bnCount++;
                    } case 1 -> { //promote to bishop
                        bb = bb | (1L << dest);
                        bbCount++;
                    }case 2 -> { //promote to rook
                        br = br | (1L << dest);
                        brCount++;
                    }case 3 -> { //promote to queen
                        bq = bq | (1L << dest);
                        bqCount++;}
                }
            }
        }
        if ((wp >>> dest & 1) == 1) {   //enemy pawn cature
            wp = wp & ~(1L << (dest));
            wpCount--;
            plyCount_50Move = 0;
        } if ((wn >>> dest & 1) == 1) { //enemy knight capture
            wn = wn & ~(1L << (dest));
            wnCount--;
            plyCount_50Move = 0;
        } if ((wb >>> dest & 1) == 1) { //enemy bishop captured
            wb = wb & ~(1L << (dest));
            wbCount--;
            plyCount_50Move = 0;
        } if ((wr >>> dest & 1) == 1) { //enemy rook captured
            wr = wr & ~(1L << (dest));
            wrCount--;
            plyCount_50Move = 0;
        } if ((wq >>> dest & 1) == 1) { //enemy queen captured
            wq = wq & ~(1L << (dest));
            wqCount--;
            plyCount_50Move = 0;
        }
    }

    public void unmakeMove1Ply() { //half move (1ply) unmake
        if (plyCtList.size() > 1) {
            int i = plyCtList.size() - 1;
            //remove last move from notation lists
            wpList.remove(i); wnList.remove(i); wbList.remove(i); wrList.remove(i); wqList.remove(i); wkList.remove(i);
            bpList.remove(i); bnList.remove(i); bbList.remove(i); brList.remove(i); bqList.remove(i); bkList.remove(i);
            wCastleList.remove(i); bCastleList.remove(i); pawnJumpList.remove(i); pawnJumpPlyList.remove(i);

            wpPastCounts.remove(i); wnPastCounts.remove(i); wbPastCounts.remove(i); wrPastCounts.remove(i);
            wqPastCounts.remove(i); bpPastCounts.remove(i); bnPastCounts.remove(i); bbPastCounts.remove(i);
            brPastCounts.remove(i); bqPastCounts.remove(i);

            zobristKeyList.remove(i);
            moveCount50List.remove(i); turnList.remove(i); plyCtList.remove(i);
            repCounter.put(currentZobrist, repCounter.get(currentZobrist) - 1);
            if (repCounter.get(currentZobrist) == 0) { repCounter.remove(currentZobrist); }

            wp = wpList.get(i-1); wn = wnList.get(i-1); wb = wbList.get(i-1); wr = wrList.get(i-1); wq = wqList.get(i-1);
            wk = wkList.get(i-1);
            bp = bpList.get(i-1); bn = bnList.get(i-1); bb = bbList.get(i-1); br = brList.get(i-1); bq = bqList.get(i-1);
            bk = bkList.get(i-1);

            wpCount = wpPastCounts.get(i - 1); wnCount = wnPastCounts.get(i - 1); wbCount = wbPastCounts.get(i - 1);
            wrCount = wrPastCounts.get(i - 1); wqCount = wqPastCounts.get(i - 1);
            bpCount = bpPastCounts.get(i - 1); bnCount = bnPastCounts.get(i - 1); bbCount = bbPastCounts.get(i - 1);
            brCount = brPastCounts.get(i - 1); bqCount = bqPastCounts.get(i - 1);

            currentZobrist = zobristKeyList.get(i - 1);
            wCastle = wCastleList.get(i-1); bCastle = bCastleList.get(i-1);
            lastPawnJump = pawnJumpList.get(i-1); pawnJumpPly = pawnJumpPlyList.get(i-1);
            plyCount_50Move = moveCount50List.get(i-1); turn = turnList.get(i-1); plyCount = plyCtList.get(i-1);
        }
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
        /*String testPos = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

        bitboard btb = new bitboard();
        btb.setBitboardPos(testPos);
        btb.printArrayBoard();
        System.out.println(btb.currentZobrist);
        System.out.println();*/

    }
}