package chess;



public class move {
    public int start;
    public int dest;
    public int moveType;
    public int promo;

    public move(int start, int dest, int moveType, int promo) {
        this.start = start;
        this.dest = dest;
        this.moveType = moveType; //0 = normal move, 1 = castle, 2 = en passant, 3 = promotion
        this.promo = promo; //0 = none, 2 = n, 3 = b, 4 = r, 5 = q
    }

    public String toString() {
        return start + "-" + dest;
    }


}