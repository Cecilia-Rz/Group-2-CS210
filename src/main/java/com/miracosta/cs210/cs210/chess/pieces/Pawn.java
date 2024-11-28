package com.miracosta.cs210.cs210.chess.pieces;

import com.miracosta.cs210.cs210.chess.board.ChessBoard;
import com.miracosta.cs210.cs210.chess.board.ChessTile;

import static com.miracosta.cs210.cs210.chess.pieces.Color.BLACK;
import static com.miracosta.cs210.cs210.chess.pieces.Color.WHITE;

/**
 * A Pawn that can push forward, capture diagonally, double move on its first move, capture en passant, and be captured en passant
 * TODO Promotion
 */
public class Pawn extends ChessPiece implements EnPassantPiece{
    boolean canBeCapturedEnPassant = false;
    boolean doubleMovable = true;
    int enPassantMove = -1;
    boolean capturingEnPassant = false;
    ChessTile enPassantCaptureTile = null;

    public Pawn(Color color) {
        super(color);
    }
    public Pawn() {
        super();
    }

    @Override
    public void calculateValidMoves(ChessBoard board) {
        legalMoves.clear();
        if (canDoubleMove()) checkAndAddPush(board, 2);
        else checkAndAddPush(board, 1);
        checkAndAddCapture(board);
    }

    /**
     * Get the direction in which the Pawn can step in the rows
     * @return +1 if the pawn can increase the row position in each step, -1 if the row position can decrease
     */
    private int getPushDirection() {
        if (getColor() == WHITE) return -1;
        return 1;
    }

    /**
     * Add tiles to the legal move list if they come from the Pawn pushing forward, and are not blocked by either color piece
     * @param board Current ChessBoard
     * @param range number of tiles allowed to move (typically 1, can be 2 for first move double move)
     */
    private void checkAndAddPush(ChessBoard board, int range) {
        for (int i = 1; i <= range; i++) {
            ChessTile target = board.getTileByOffset(getPosition(), i * getPushDirection(), 0);
            if (target == null) return;
            if (target.isOccupied()) return;
            legalMoves.add(target);
        }
    }

    /**
     * Check for diagonal captures and en passant captures, adds them to the legalMoves ArrayList
     * @param board current ChessBoard
     */
    private void checkAndAddCapture(ChessBoard board) {
        capturingEnPassant = false;
        ChessTile leftTarget = board.getTileByOffset(getPosition(), getPushDirection(), -1);
        ChessTile rightTarget = board.getTileByOffset(getPosition(), getPushDirection(), 1);
        if (leftTarget!= null) {
            if (leftTarget.getColor() == getOppositeColor()) legalMoves.add(leftTarget);
        }
        if (rightTarget != null) {
            if (rightTarget.getColor() == getOppositeColor()) legalMoves.add(rightTarget);
        }
        //en passant
        ChessTile leftEnPassantTarget = board.getTileByOffset(getPosition(), 0, -1);
        ChessTile rightEnPassantTarget = board.getTileByOffset(getPosition(), 0, 1);

        if (leftEnPassantTarget != null) {
            if (leftEnPassantTarget.hasEnPassantablePiece(getOppositeColor())) {
                legalMoves.add(leftTarget);
                capturingEnPassant = true;
                enPassantCaptureTile = leftEnPassantTarget;
            }
        }
        if (rightEnPassantTarget != null) {
            if (rightEnPassantTarget.hasEnPassantablePiece(getOppositeColor())) {
                legalMoves.add(rightTarget);
                capturingEnPassant = true;
                enPassantCaptureTile = rightEnPassantTarget;
            }
        }
    }

    /**
     * Check if the Pawn is in the right row to be allowed to double move
     * @return True if the Pawn is in its starting row
     */
    private boolean canDoubleMove() {
        if (getColor() == WHITE) {
            return getPosition().getRow() == 6;
        }
        return getPosition().getRow() == 1;
    }

    @Override
    public void updateEnPassantStatus() {
        //capturingEnPassant = false;
        if (canBeCapturedEnPassant && board.getMoveNumber() > enPassantMove + 1) {
            setEnPassantStatus(false);
        }
        if (getColor() == WHITE && doubleMovable && getPosition().getRow() == 4) setEnPassantStatus(true);
        if (getColor() == BLACK && doubleMovable && getPosition().getRow() == 3) setEnPassantStatus(true);
        if (!canDoubleMove()) doubleMovable = false;
    }

    @Override
    public void setEnPassantStatus(boolean enPassantStatus) {
        if (enPassantStatus) {
            enPassantMove = board.getMoveNumber();
        }
        canBeCapturedEnPassant = enPassantStatus;
    }

    @Override
    public boolean getEnPassantStatus() {
        return canBeCapturedEnPassant;
    }

    @Override
    public boolean isCapturingEnPassant() {
        return capturingEnPassant;
    }

    @Override
    public ChessTile getCaptureTile() {
        return enPassantCaptureTile;
    }
}
