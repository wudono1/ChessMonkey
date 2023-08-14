package chess.eval;

public class PSTables { //piece square tables and calculations
    public static int[] pawns_mg = {

            0,  0,  0,  0,  0,  0,  0,  0,
            5, 10, 10,-20,-20, 10, 10,  5,
            -5, -5,-10,  0, 0,-10, -5, -5,
            0,  0,  0, 20, 20,  0,  0,  0,
            5,  5, 10, 25, 25, 10,  5,  5,
            10, 10, 20, 30, 30, 20, 10, 10,
            50, 50, 50, 50, 50, 50, 50, 50,
            0,  0,  0,  0,  0,  0,  0,  0};
    public static int[] knights_mg = {
            //0                          7
            -50,-40,-30,-30,-30,-30,-40,-50, //first rank
            -40,-20,  0,  0,  0,  0,-20,-40,
            -30,  0, 10, 15, 15, 10,  0,-30,
            -30,  5, 15, 20, 20, 15,  5,-30,
            -30,  0, 15, 20, 20, 15,  0,-30,
            -30,  5, 10, 15, 15, 10,  5,-30,
            -40,-20,  0,  5,  5,  0,-20,-40,
            -50,-40,-30,-30,-30,-30,-40,-50 }; //eighth rank
    public static int[] bishops_mg = {
            //0                          7
            -20,-10,-10,-10,-10,-10,-10,-20,  //first rank
            -10,  5,  0,  0,  0,  5,  5,-10,
            -10, 10,  8, 10, 10,  8, 10,-10,
            -10,  0, 10, 10, 10, 10,  0,-10,
            -10,  5,  5, 10, 10,  5,  5,-10,
            -10,  0,  5, 10, 10,  5,  0,-10,
            -10,  0,  0,  0,  0,  0,  0,-10,
            -20,-10,-10,-10,-10,-10,-10,-20};  //eighth rank
    public static int[] rooks_mg = {
          //0                            7
            0,   0,  5, 10, 10,  5,  0,  0, //first rank
            -5,  0,  3,  5,  5,  2,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            -5,  0,  0,  0,  0,  0,  0, -5,
            5, 10, 10, 10, 10, 10, 10,  5,
            0,  0,  0,  0,  0,  0,  0,  0 }; //eighth rank

    public static int[] queens_mg = {
            //0                            7
            -20,-10,-10, -5, -5,-10,-10, -20,  //first rank
            -10,  0,  0,  0,  0,  5,  0, -10,
            -10,  5,  5,  5,  5,  5,  0, -10,
              0,  0,  5,  5,  5,  5,  0,  -5,
             -5,  0,  5,  5,  5,  5,  0,  -5,
            -10,  0,  5,  5,  5,  5,  0, -10,
            -10,  0,  0,  0,  0,  0,  0, -10,
            -20,-10,-10, -5, -5,-10,-10, -20};  //last rank
    public static int[] king_mg = {
          //0                               7
            20, 30,  -5,  -5, -10, 10, 30,  20,   //first rank
            20, 20,  -5, -10,-10,  5,  15,  15,
            -10,-20, -20,-20,-20,-20, -20, -10,
            -20,-30, -30,-40,-40,-30, -30, -20,
            -30,-40, -40,-50,-50,-40, -40, -30,
            -30,-40, -40,-50,-50,-40, -40, -30,
            -30,-40, -40,-50,-50,-40, -40, -30,
            -30,-40, -40,-50,-50,-40, -40, -30 }; //eighth rank

    public static int pawnEval(long wp, long bp) {
        int score = 0;
        for (int i = 0; i < 63; i++) { if ((wp >>> i & 1) == 1) {score = score + pawns_mg[i];} }
        for (int i = 0; i < 63; i++) {
            if ((bp >>> i & 1) == 1) {score = score - pawns_mg[ 8 * (7-i/8) + i % 8 ];} }
        return score;
    }
    public static int bishopEval(long wb, long bb) {
        int score = 0;
        for (int i = 0; i < 63; i++) { if ((wb >>> i & 1) == 1) {score = score + bishops_mg[i];} }
        for (int i = 0; i < 63; i++) {
            if ((bb >>> i & 1) == 1) {score = score - bishops_mg[ 8 * (7-i/8) + i % 8 ];} }
        return score;
    }
    public static int knightEval(long wn, long bn) {
        int score = 0;
        for (int i = 0; i < 63; i++) { if ((wn >>> i & 1) == 1) {score = score + knights_mg[i];} }
        for (int i = 0; i < 63; i++) {
            if ((bn >>> i & 1) == 1) {score = score - knights_mg[ 8 * (7-i/8) + i % 8 ];} }
        return score;
    }
    public static int rookEval(long wr, long br) {
        int score = 0;
        for (int i = 0; i < 63; i++) { if ((wr >>> i & 1) == 1) {score = score + rooks_mg[i];} }
        for (int i = 0; i < 63; i++) {
            if ((br >>> i & 1) == 1) {score = score - rooks_mg[ 8 * (7-i/8) + i % 8 ];} }
        return score;
    }
    public static int queenEval(long wq, long bq) {
        int score = 0;
        for (int i = 0; i < 63; i++) { if ((wq >>> i & 1) == 1) {score = score + queens_mg[i];} }
        for (int i = 0; i < 63; i++) {
            if ((bq >>> i & 1) == 1) {score = score - queens_mg[ 8 * (7-i/8) + i % 8 ];} }
        return score;
    }
    public static int kingEval(long wk, long bk) {
        int score = 0;
        for (int i = 0; i < 63; i++) { if ((wk >>> i & 1) == 1) {score = score + king_mg[i];} }
        for (int i = 0; i < 63; i++) {
            if ((bk >>> i & 1) == 1) {score = score - king_mg[ 8 * (7-i/8) + i % 8 ];} }
        return score;
    }
}
