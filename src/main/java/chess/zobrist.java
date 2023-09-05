package chess;
import java.security.SecureRandom;

public class zobrist {
    public static long[][] squareHashes = new long[12][64];
    //0 = wp, 1 = wn.......5 = wk, 6 = bp, 7 = bn.........11 = bk
    public static long[][] castlingHash = new long[2][2];
    //white = [0][0] and [0][1]; black = [1][0] and [1][1]
    public static long[] EPFileHash = new long[8];
    //index 0 = h file, index 7 = a file
    public static long whiteToMove;
    static {
        SecureRandom generator = new SecureRandom();
        for (int i = 0; i < squareHashes.length; i++) {
            for (int j = 0; j < squareHashes[i].length; j++) {
                squareHashes[i][j] = generator.nextLong();
            }
        }
        for (int m = 0; m < EPFileHash.length; m++) {EPFileHash[m] = generator.nextLong();}

        for (int n = 0; n < castlingHash.length; n++) {
            for (int p = 0; p < castlingHash[n].length; p++) {
                castlingHash[n][p] = generator.nextLong();
            }
        }
        whiteToMove = generator.nextLong();

    }



    public static long getZobristKey(long wp, long wn, long wb, long wr, long wq, long wk, long bp, long bn, long bb, long br,
                                  long bq, long bk, int turn, int wCastle, int bCastle, int lastPawnJump) {
        long key = 0L;
        if (turn == 1) { key ^= whiteToMove; }
        if (lastPawnJump >= 0) { key^= EPFileHash[lastPawnJump % 8];}
        for (int i = 0; i < 12; i++) {
            switch(i) {
                case 0:
                    for (int j = Long.numberOfTrailingZeros(wp); j < 64 - Long.numberOfLeadingZeros(wp); j++) {
                        if ((wp >>> j & 1) == 1) { key ^= squareHashes[i][j];}
                    }
                case 1:
                    for (int j = Long.numberOfTrailingZeros(wn); j < 64 - Long.numberOfLeadingZeros(wn); j++) {
                        if ((wn >>> j & 1) == 1) { key ^= squareHashes[i][j];}
                    }
                case 2:
                    for (int j = Long.numberOfTrailingZeros(wb); j < 64 - Long.numberOfLeadingZeros(wb); j++) {
                        if ((wb >>> j & 1) == 1) { key ^= squareHashes[i][j];}
                    }
                case 3:
                    for (int j = Long.numberOfTrailingZeros(wr); j < 64 - Long.numberOfLeadingZeros(wr); j++) {
                        if ((wr >>> j & 1) == 1) { key ^= squareHashes[i][j];}
                    }
                case 4:
                    for (int j = Long.numberOfTrailingZeros(wq); j < 64 - Long.numberOfLeadingZeros(wq); j++) {
                        if ((wq >>> j & 1) == 1) { key ^= squareHashes[i][j];}
                    }
                case 5:
                    for (int j = Long.numberOfTrailingZeros(wk); j < 64 - Long.numberOfLeadingZeros(wk); j++) {
                        if ((wk >>> j & 1) == 1) { key ^= squareHashes[i][j];}
                    }
                case 6:
                    for (int j = Long.numberOfTrailingZeros(bp); j < 64 - Long.numberOfLeadingZeros(bp); j++) {
                        if ((bp >>> j & 1) == 1) { key ^= squareHashes[i][j];}
                    }
                case 7:
                    for (int j = Long.numberOfTrailingZeros(bn); j < 64 - Long.numberOfLeadingZeros(bn); j++) {
                        if ((bn >>> j & 1) == 1) { key ^= squareHashes[i][j];}
                    }
                case 8:
                    for (int j = Long.numberOfTrailingZeros(bb); j < 64 - Long.numberOfLeadingZeros(bb); j++) {
                        if ((bb >>> j & 1) == 1) { key ^= squareHashes[i][j];}
                    }
                case 9:
                    for (int j = Long.numberOfTrailingZeros(br); j < 64 - Long.numberOfLeadingZeros(br); j++) {
                        if ((br >>> j & 1) == 1) { key ^= squareHashes[i][j];}
                    }
                case 10:
                    for (int j = Long.numberOfTrailingZeros(bq); j < 64 - Long.numberOfLeadingZeros(bq); j++) {
                        if ((bq >>> j & 1) == 1) { key ^= squareHashes[i][j];}
                    }
                case 11:
                    for (int j = Long.numberOfTrailingZeros(bk); j < 64 - Long.numberOfLeadingZeros(bk); j++) {
                        if ((bk >>> j & 1) == 1) { key ^= squareHashes[i][j];}
                    }
            }
            i++;
        }
        if (wCastle > 0) {
            if ((wCastle & 1) == 1) { key ^= castlingHash[0][0]; }
            if ((wCastle >>> 1 & 1) == 1) {key ^= castlingHash[0][1]; }
        } if (bCastle > 0) {
            if ((bCastle & 1) == 1) { key ^= castlingHash[1][0]; }
            if ((bCastle >>> 1 & 1) == 1) {key ^= castlingHash[1][1]; }
        }


        return key;
    }
}
