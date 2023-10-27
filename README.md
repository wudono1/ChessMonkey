# ChessMonkey, a Java chess engine
ChessMonkey takes a position as input in [FEN Notation](https://en.wikipedia.org/wiki/Forsyth%E2%80%93Edwards_Notation) and outputs the best move for the position and a numerical evaluation in centipawns, where 100 centipawns are equivalent to a one pawn advantage, a positive evaluation is in favor of white, and a negative evaluation is in favor of black.\
To run ChessMonkey, download the files and input a position in FEN Notation in String format into the negamax.java file within the iterativeDeepeningSearch() method. Most popular chess websites such as chess.com and lichess.org automatically return a FEN notation in their analysis boards.

# Features:
Move generation:\
bitboard representation with hyperbola quintessence for sliding pieces\
movegen testing with perft

Search:\
Negamax evaluation with alpha-beta pruning\
Zobrist hashing and transposition tables\
Quiescence search

Evaluation heuristics:\
Material count\
Middlegame and endgame piece-square tables\
Tapered evaluation for game phase transitions

In progress:\
Static exchange evaluation\
endgame piece-square tables/tapered evaluation and game phase transitions

