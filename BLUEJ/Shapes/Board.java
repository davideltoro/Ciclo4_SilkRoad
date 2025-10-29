package Shapes;

import java.util.Arrays;
import SilkRoads.*;

/**
 * Write a description of class Board here.
 *
 * Autor: Juan David Castellanos
 * Tablero simple con margen externo.
 */
public class Board {

    private static final int MARGIN_X = 60;
    private static final int MARGIN_Y = 20;

    private final int rows;
    private final int cols;
    private final Cell[][] cells;
    private boolean visible = false;

    // Máscara de visibilidad por celda (true = visible)
    private boolean[][] visibleMask;

    public Board(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.cells = new Cell[rows][cols];

        // Usamos los pasos con GAP para separar visualmente los "caminos"
        int stepX = Cell.STEP_X; // SIZE + GAP_X
        int stepY = Cell.STEP_Y; // SIZE + GAP_Y

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int x = MARGIN_X + c * stepX;
                int y = MARGIN_Y + r * stepY;
                cells[r][c] = new Cell("white", false, x, y);
            }
        }

        // Por defecto todas visibles
        visibleMask = new boolean[rows][cols];
        for (int r = 0; r < rows; r++) {
            Arrays.fill(visibleMask[r], true);
        }
    }

    public int getRows() { return rows; }
    public int getCols() { return cols; }
    public boolean isVisible() { return visible; }

    /** Muestra el tablero respetando la máscara de visibilidad. */
    public void makeVisible() {
        if (visible) return;
        visible = true;
        applyMaskToCells();
    }

    /** Oculta todo el tablero (todas las celdas). */
    public void makeInvisible() {
        if (!visible) return;
        visible = false;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                cells[r][c].makeInvisible();
            }
        }
    }

    public Cell getCell(int row, int col) {
        return cells[row][col];
    }

    /** Establece una nueva máscara (mismo tamaño que el board) y la aplica si está visible. */
    public void setVisibleCells(boolean[][] mask) {
        if (mask == null || mask.length != rows || mask[0].length != cols) {
            throw new IllegalArgumentException("Mask size must match board size");
        }
        this.visibleMask = mask;
        if (visible) applyMaskToCells();
    }

    /** Aplica la máscara a cada celda. */
    private void applyMaskToCells() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (visibleMask[r][c]) {
                    cells[r][c].makeVisible();
                } else {
                    cells[r][c].makeInvisible();
                }
            }
        }
    }

    // ==== Helpers públicos de tamaño/posición (en píxeles) ====
    public int getPixelXForCol(int col) {           // x de la esquina izq de una columna
        return MARGIN_X + col * Cell.STEP_X;
    }
    public int getPixelYForRow(int row) {           // y de la esquina sup de una fila
        return MARGIN_Y + row * Cell.STEP_Y;
    }
    public int getBoardRightX() {                   // x del borde derecho del tablero
        return getPixelXForCol(cols - 1) + Cell.SIZE;
    }
    public int getBoardBottomY() {                  // y del borde inferior del tablero
        return getPixelYForRow(rows - 1) + Cell.SIZE;
    }
    public int getBoardLeftX() {                    // por comodidad
        return getPixelXForCol(0);
    }
    public int getBoardTopY() {
        return getPixelYForRow(0);
    }
}
