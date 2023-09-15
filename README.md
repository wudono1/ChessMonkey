# ChessMonkey
Java chess engine\
ChessMonkey takes a position as input in [FEN Notation](https://en.wikipedia.org/wiki/Forsyth%E2%80%93Edwards_Notation) and outputs the best move for the position and a numerical evaluation in centipawns, where 100 centipawns are equivalent to a one pawn advantage, a positive evaluation is in favor of white, and a negative evaluation is in favor of black.\
To run ChessMonkey, download the files and input a position into the negamax.java file.

# Features:
Move generation:\
bitboard representation with hyperbola quintessence for sliding pieces\
testing using perft function

Search:\
Negamax evaluation with alpha-beta pruning\
Zobrist hashing and transposition tables\
Quiescence search

Evaluation heuristics:\
Material count\
Middlegame and endgame piece-square tables\
Tapered evaluation for game phase transitions

