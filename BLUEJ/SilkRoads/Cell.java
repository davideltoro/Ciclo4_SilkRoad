package SilkRoads;
import Shapes.*;
/**
 * Write a description of class Cell here.
 * 
 * Autor: Juan David Castellanos - Julian Morales
 */
import java.util.ArrayList;
import java.util.List;

public class Cell {
    private String color;   
    private boolean hole;      
    private boolean hasMarbel;
    private Rectangle box;
    private Rectangle border;
    private int size;
    private int margin;
    private boolean isHeadless;

    /** Tamaño básico de cada celda (lado del cuadrado). */
    public static final int SIZE = 80;

    /** Margen interno para que se vea el borde negro alrededor. */
    public static final int MARGIN = Math.max(1, Math.round(SIZE * 0.016f));

    /** --- NUEVO: separación visual entre caminos (filas/columnas nuevas) --- */
    /** Espacio horizontal extra entre columnas consecutivas del tablero. */
    public static final int GAP_X = 10; 
    /** Espacio vertical extra entre filas consecutivas del tablero. */
    public static final int GAP_Y = 10; 

    /** Paso efectivo en X al ubicar celdas en el Board (tamaño + espacio). */
    public static final int STEP_X = SIZE + GAP_X;
    /** Paso efectivo en Y al ubicar celdas en el Board (tamaño + espacio). */
    public static final int STEP_Y = SIZE + GAP_Y;

    public Cell(String color, boolean hole, int x, int y) {
        this(color, hole, x, y, false);
    }

    public Cell(String color, boolean hole, int x, int y, boolean isHeadless) {
        this.color = color;
        this.hole = hole;
        this.isHeadless = isHeadless;

        if (!isHeadless) {
            border = new Rectangle();
            border.changeSize(SIZE, SIZE);
            border.changeColor("black");
            border.moveHorizontal(x);
            border.moveVertical(y);

            box = new Rectangle();
            box.changeSize(SIZE - 2 * MARGIN, SIZE - 2 * MARGIN);
            box.changeColor(color);
            box.moveHorizontal(x + MARGIN);
            box.moveVertical(y + MARGIN);
        }
    }

    public void makeVisible() {
        if (isHeadless) return;
        border.makeVisible();
        box.makeVisible();
    }

    public void makeInvisible() {
        if (isHeadless) return;
        border.makeInvisible();
        box.makeInvisible();
    }

    public String getColor() {
        return color;
    }

    public int getSize() {
        return SIZE;
    }

    public void setColor(String newColor) {
        if (isHeadless) return;
        this.color = newColor;
        box.changeColor(newColor);
        box.makeVisible();
    }
}
