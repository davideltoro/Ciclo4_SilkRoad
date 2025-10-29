package SilkRoads;

import Shapes.*;

/**
 * Julian Morales - Juan David Castellanos
 * Clase abstracta StoreBase:
 * Representa la estructura general de una tienda en el simulador SilkRoads.
 * Define atributos, tamaños, color, ganancias, visibilidad y lógica compartida.
 *
 * Subclases: Store (normal), AutonomousStore, FighterStore, etc.
 */
public abstract class StoreBase {

    // ------- Estado lógico -------
    protected String type;       // Tipo de tienda
    protected int position;      // Posición lógica en la SilkRoad
    protected int tenges;        // Dinero actual disponible
    protected int initialTenges; // Dinero inicial (para resupply)
    protected int timesEmptied;  // Número de veces vaciada
    protected int drawRow = 0;
    protected int drawCol = 0;
    
    // ------- Integración con el camino -------
    protected SilkRoad road;     // referencia al SilkRoad (opcional)

    // ------- Componentes visuales -------
    protected Rectangle base;
    protected Triangle roof;

    public StoreBase(String type, int position, int tenges) {
        this.type = type;
        this.position = position;
        this.tenges = tenges;
        this.initialTenges = tenges;
        this.timesEmptied = 0;

        // Configuración visual básica
        base = new Rectangle();
        roof = new Triangle();
        base.changeColor("magenta");
        roof.changeColor("green");
    }

    // ========== LÓGICA ==========
    /** Vacía la tienda una vez. Aumenta timesEmptied solo si había dinero. */
    public int emptyOnce() {
        if (tenges > 0) {
            int collected = tenges;
            tenges = 0;
            timesEmptied++;
            return collected;
        }
        return 0;
    }

    /** Restaura al valor inicial (sin condiciones). */
    public void resupply() {
        tenges = initialTenges;
    }

    // ========== GETTERS ==========
    public String getType() { return type; }
    public int getPosition() { return position; }
    public int getTenges() { return tenges; }
    public int getTimesEmptied() { return timesEmptied; }

    // ========== VISTA ==========
    public void makeVisible() {
        base.makeVisible();
        roof.makeVisible();
    }

    public void makeInvisible() {
        base.makeInvisible();
        roof.makeInvisible();
    }

    public void moveStore(int row, int col) {
        base.moveHorizontal(col * 60);
        base.moveVertical(row * 60);
        roof.moveHorizontal(col * 60);
        roof.moveVertical(row * 60 - 20);
    }

    /** Alias para mantener compatibilidad con SilkRoad.placeStore(...) */
    public void setColor(String color) { changeColor(color); }

    public void changeColor(String color) { base.changeColor(color); }

    // ========== ROAD / HOOKS ==========
    /** Punto de acople oficial: asigna el road y dispara el hook de colocación. */
    public final void attachToRoad(SilkRoad road) {
        this.road = road;
        onPlaced(road);
    }

    /** Alias por compatibilidad (tu SilkRoad usa setRoad). */
    @Deprecated
    public final void setRoad(SilkRoad road) {
        attachToRoad(road);
    }

    /** Hook sobrescribible por subclases (por defecto no hace nada). */
    public void onPlaced(SilkRoad road) {
        // no-op por defecto
    }
}
