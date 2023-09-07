package chess.testing;
import chess.zobrist;
import chess.constants;

import java.util.*;
@SuppressWarnings("SpellCheckingInspection")

public class bitboardTesting {
    //piece bitboards
    public long wp = 0L, wn = 0L, wb = 0L, wr = 0L, wq = 0L, wk = 0L,
            bp = 0L, bn = 0L, bb = 0L, br = 0L, bq = 0L, bk = 0L;

    public long[] pieceBitboards = {0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};

    //piece counts
    public int[] pieceCounts = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
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
    public bitboardTesting() { //default set to startpos FEN
        setBitboards("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR");
        turn = 1;
        wCastle = 0B11;
        bCastle = 0B11;
        lastPawnJump = -1;
        pawnJumpPly = -1;
        setBoardArray();
        currentZobrist = zobrist.getZobristKey(pieceBitboards[0], pieceBitboards[1], pieceBitboards[2], pieceBitboards[3],
                pieceBitboards[4], pieceBitboards[5], pieceBitboards[6], pieceBitboards[7], pieceBitboards[8],
                pieceBitboards[9], pieceBitboards[10], pieceBitboards[11], turn, wCastle, bCastle, lastPawnJump);
        notateLists();
        repCounter.put(currentZobrist, 1);
    }
    public void setBitboardPos(String FEN) {  //given a specific FEN
        FEN = FEN.trim();
        pieceBitboards[0] = pieceBitboards[1] = pieceBitboards[2] = pieceBitboards[3] = pieceBitboards[4] = pieceBitboards[5] =
        pieceBitboards[6] = pieceBitboards[7] = pieceBitboards[8] = pieceBitboards[9] = pieceBitboards[10] = pieceBitboards[11] = 0;

        pieceCounts[0] = pieceCounts[1] = pieceCounts[2] = pieceCounts[3] = pieceCounts[4] = pieceCounts[5] = pieceCounts[6] =
        pieceCounts[5] = pieceCounts[6] = pieceCounts[7] = pieceCounts[8] = pieceCounts[9] = pieceCounts[10] = pieceCounts[11] = 0;

        clearLists();
        String[] split = FEN.split("\\s+");
        setBitboards(split[0]);  //set bitboards based on FEN position
        if (Objects.equals(split[1].toLowerCase(), "w")) {turn = 1;} //set turn
        if (Objects.equals(split[1].toLowerCase(), "b")) {turn = -1;}

        if ((pieceBitboards[5] >>> 3 & 1) == 1) {
            if (split[2].contains("K") && ((pieceBitboards[5] & 1) == 1)) { wCastle = wCastle + 0B1; } //set castling rights
            if (split[2].contains("Q") && ((pieceBitboards[5] >>> 7 & 1) == 1)) { wCastle = wCastle + 0B10; }
        } if ((pieceBitboards[11] >>> 59 & 1) == 1) {
            if (split[2].contains("k") && ((pieceBitboards[11] >>> 56 & 1) == 1)) { bCastle = bCastle + 0B1; }
            if (split[2].contains("q") && ((pieceBitboards[11] >>> 63 & 1) == 1)) { bCastle = bCastle + 0B10; }
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
        currentZobrist = zobrist.getZobristKey(pieceBitboards[0], pieceBitboards[1], pieceBitboards[2], pieceBitboards[3],
                pieceBitboards[4], pieceBitboards[5], pieceBitboards[6], pieceBitboards[7], pieceBitboards[8],
                pieceBitboards[9], pieceBitboards[10], pieceBitboards[11], turn, wCastle, bCastle, lastPawnJump);
        notateLists();
        repCounter.put(currentZobrist, 1);
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
                    pieceBitboards[0] = pieceBitboards[0] + addPos;
                    pieceCounts[0]++;
                } if (FEN.charAt(i) == 'N') {
                    pieceBitboards[1] = pieceBitboards[1] + addPos;
                    pieceCounts[1]++;
                } if (FEN.charAt(i) == 'B') {
                    pieceBitboards[2] = pieceBitboards[2] + addPos;
                    pieceCounts[2]++;
                } if (FEN.charAt(i) == 'R') {
                    pieceBitboards[3] = pieceBitboards[3] + addPos;
                    pieceCounts[3]++;
                } if (FEN.charAt(i) == 'Q') {
                    pieceBitboards[4] = pieceBitboards[4] + addPos;
                    pieceCounts[4]++;
                } if (FEN.charAt(i) == 'K') {
                    pieceBitboards[5] = pieceBitboards[5] + addPos;
                    pieceCounts[5]++;
                } if (FEN.charAt(i) == 'p') {
                    pieceBitboards[6] = pieceBitboards[6] + addPos;
                    pieceCounts[6]++;
                } if (FEN.charAt(i) == 'n') {
                    pieceBitboards[7] = pieceBitboards[7] + addPos;
                    pieceCounts[7]++;
                } if (FEN.charAt(i) == 'b') {
                    pieceBitboards[8] = pieceBitboards[8] + addPos;
                    pieceCounts[8]++;
                } if (FEN.charAt(i) == 'r') {
                    pieceBitboards[9] = pieceBitboards[9] + addPos;
                    pieceCounts[9]++;
                } if (FEN.charAt(i) == 'q') {
                    pieceBitboards[10] = pieceBitboards[10] + addPos;
                    pieceCounts[10]++;
                } if (FEN.charAt(i) == 'k') {
                    pieceBitboards[11] = pieceBitboards[11] + addPos;
                    pieceCounts[11]++;
                }
            }
        }
    }

    public void setBoardArray() {
        for (int i = 0; i < 64; i++) {
            if ((pieceBitboards[0] >>> i & 0B1) == 0B1) {
                arrBoard[i] = "P";
            } else if ((pieceBitboards[1] >>> i & 0B1) == 0B1) {
                arrBoard[i] = "N";
            } else if ((pieceBitboards[2] >>> i & 0B1) == 0B1) {
                arrBoard[i] = "B";
            } else if ((pieceBitboards[3] >>> i & 0B1) == 0B1) {
                arrBoard[i] = "R";
            } else if ((pieceBitboards[4] >>> i & 0B1) == 0B1) {
                arrBoard[i] = "Q";
            } else if ((pieceBitboards[5] >>> i & 0B1) == 0B1) {
                arrBoard[i] = "K";
            } else if ((pieceBitboards[6] >>> i & 0B1) == 0B1) {
                arrBoard[i] = "p";
            } else if ((pieceBitboards[7] >>> i & 0B1) == 0B1) {
                arrBoard[i] = "n";
            } else if ((pieceBitboards[8] >>> i & 0B1) == 0B1) {
                arrBoard[i] = "b";
            } else if ((pieceBitboards[9] >>> i & 0B1) == 0B1) {
                arrBoard[i] = "r";
            } else if ((pieceBitboards[10] >>> i & 0B1) == 0B1) {
                arrBoard[i] = "q";
            } else if ((pieceBitboards[11] >>> i & 0B1) == 0B1) {
                arrBoard[i] = "k";
            } else {
                arrBoard[i] = "-";
            }
        }
    }

    public void checkCastlingRights() { //checking validity of input FEN castling rights
        if ((pieceBitboards[5]>>>3 & 1L) != 1) {wCastle = 0;}
        if ((pieceBitboards[11]>>>59 & 1L) != 1) {bCastle = 0;}

        if ((pieceBitboards[3] & 1) != 1) {wCastle = wCastle & (~0b01);}
        if ((pieceBitboards[3]>>>7 & 1) != 1) {wCastle = wCastle & (~0b10);}
        if ((pieceBitboards[9]>>>56 & 1) != 1) {bCastle = bCastle & (~0b01);}
        if ((pieceBitboards[9]>>>63 & 1) != 1) {bCastle = bCastle & (~0b10);}

    }

    public void clearLists() { //clears all past notation lists
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
        wpList.add(pieceBitboards[0]); wnList.add(pieceBitboards[1]); wbList.add(pieceBitboards[2]);wrList.add(pieceBitboards[3]);
        wqList.add(pieceBitboards[4]); wkList.add(pieceBitboards[5]);bpList.add(pieceBitboards[6]); bnList.add(pieceBitboards[7]);
        bbList.add(pieceBitboards[8]); brList.add(pieceBitboards[9]); bqList.add(pieceBitboards[10]); bkList.add(pieceBitboards[11]);

        wpPastCounts.add(pieceCounts[0]); wnPastCounts.add(pieceCounts[1]); wbPastCounts.add(pieceCounts[2]);
        wrPastCounts.add(pieceCounts[3]); wqPastCounts.add(pieceCounts[4]); bpPastCounts.add(pieceCounts[6]);
        bnPastCounts.add(pieceCounts[7]); bbPastCounts.add(pieceCounts[8]); brPastCounts.add(pieceCounts[9]);
        bqPastCounts.add(pieceCounts[10]);

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