package SilkRoads;

import Shapes.*;

/**
 * Juan David Castellanos - Julian Morales
 * Clase abstracta RobotBase:
 * Representa la estructura general de un robot en el simulador SilkRoads.
 * Define atributos, tamaños, color, movimiento y lógica compartida entre todos los robots.
 *
 * Subclases: NormalRobot, NeverBackRobot, TenderRobot, etc.
 */
public abstract class RobotBase {

    // --- ATRIBUTOS GENERALES ---
    protected String type; // tipo de robot (normal, tender, neverback, etc.)
    protected int currentPosition;
    protected int startingPosition;
    protected int tengesCollected;
    protected SilkRoad road;
    protected String color;

    // --- COMPONENTES VISUALES ---
    protected Circle head;
    protected Rectangle neck;
    protected Rectangle body;
    protected Rectangle leftleg;
    protected Rectangle rightleg;

    // Última celda dibujada
    protected int lastRow = 0, lastCol = 0;

    // --- TAMAÑOS (CONSTANTES GLOBALES PARA TODOS LOS ROBOTS) ---
    protected static final int S = Cell.SIZE;
    protected static final double K = 0.65;
    protected static final int HEAD_D = (int)(S * 0.28 * K);
    protected static final int NECK_H = (int)(S * 0.08 * K);
    protected static final int NECK_W = (int)(S * 0.12 * K);
    protected static final int BODY_H = (int)(S * 0.40 * K);
    protected static final int BODY_W = (int)(S * 0.48 * K);
    protected static final int LEG_H  = (int)(S * 0.22 * K);
    protected static final int LEG_W  = (int)(S * 0.10 * K);

    // --- CONSTRUCTOR ---
    public RobotBase(String type, int position, int tenges, String color) {
        this.type = type;
        this.currentPosition = position;
        this.startingPosition = position;
        this.tengesCollected = tenges;
        this.color = color;

        // Inicializar formas
        head = new Circle();      head.changeSize(HEAD_D);
        neck = new Rectangle();   neck.changeSize(NECK_H, NECK_W);
        body = new Rectangle();   body.changeSize(BODY_H, BODY_W);
        leftleg  = new Rectangle();  leftleg.changeSize(LEG_H, LEG_W);
        rightleg = new Rectangle();  rightleg.changeSize(LEG_H, LEG_W);

        // Posición base en el tablero
        head.moveHorizontal(134); head.moveVertical(45);
        neck.moveHorizontal(90);  neck.moveVertical(45 + HEAD_D);
        body.moveHorizontal(80);  body.moveVertical(45 + HEAD_D + NECK_H);
        leftleg.moveHorizontal (BODY_W/3 + 80 - LEG_W/2);
        leftleg.moveVertical   (45 + HEAD_D + NECK_H + BODY_H);
        rightleg.moveHorizontal(2*BODY_W/3 + 80 - LEG_W/2);
        rightleg.moveVertical  (45 + HEAD_D + NECK_H + BODY_H);

        applyColor(color);
    }

    // --- ENLACE CON EL MUNDO ---
    public void setRoad(SilkRoad road) {
        this.road = road;
    }

    // --- GANANCIAS ---
    public synchronized void addEarnings(int amount) {
        if (amount <= 0) return;
        tengesCollected += amount;
        if (road != null) road.updateLeader();
    }

    public synchronized int getEarnings() {
        return tengesCollected;
    }

    // --- COLOR ---
    public void colorChange(String newColor) {
        if (newColor != null) color = newColor;
        applyColor(color);
    }

    protected void applyColor(String color) {
        head.changeColor(color);
        neck.changeColor(color);
        body.changeColor(color);
        leftleg.changeColor(color);
        rightleg.changeColor(color);
    }

    // --- PARPADEO ---
    public void setBlinking(boolean active) {
        if (active) blinkRepeated(6);
        else applyColor(color);
    }

    public void blinkOnce() {
        try {
            applyColor("white");
            Thread.sleep(250);
            applyColor(color);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void blinkRepeated(int times) {
        for (int i = 0; i < times; i++) {
            blinkOnce();
        }
    }

    // --- VISIBILIDAD ---
    public void makeVisible() {
        neck.makeVisible();
        body.makeVisible();
        leftleg.makeVisible();
        rightleg.makeVisible();
        head.makeVisible();
    }

    public void makeInvisible() {
        head.makeInvisible();
        neck.makeInvisible();
        body.makeInvisible();
        leftleg.makeInvisible();
        rightleg.makeInvisible();
    }

    // --- MOVIMIENTO VISUAL EN EL TABLERO ---
    public void moveRobot(int row, int col) {
        int dx = (col - lastCol) * Cell.STEP_X;
        int dy = (row - lastRow) * Cell.STEP_Y;

        head.moveHorizontal(dx);   head.moveVertical(dy);
        neck.moveHorizontal(dx);   neck.moveVertical(dy);
        body.moveHorizontal(dx);   body.moveVertical(dy);
        leftleg.moveHorizontal(dx); leftleg.moveVertical(dy);
        rightleg.moveHorizontal(dx); rightleg.moveVertical(dy);

        lastRow = row;
        lastCol = col;
    }

    // --- ESTADO ---
    public int getPosition() { return currentPosition; }
    public int getInitialPosition() { return startingPosition; }
    public String getType() { return type; }

    public void reset() {
        this.currentPosition = startingPosition;
        this.tengesCollected = 0;
    }

    // --- MÉTODO ABSTRACTO DE MOVIMIENTO LÓGICO ---
    public abstract void move();

    @Override
    public String toString() {
        return "Robot{" +
                "tipo='" + type + '\'' +
                ", posición=" + currentPosition +
                ", inicio=" + startingPosition +
                ", ganancias=" + tengesCollected +
                ", color='" + color + '\'' +
                '}';
    }
}
