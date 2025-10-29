package SilkRoads;

/**
 * Tienda Fighter:
 * Solo permite que robots con más dinero la atiendan.
 * Si un robot más pobre intenta recoger su dinero, no se lo entrega.
 */
public class FighterStore extends StoreBase {

    public FighterStore(int position, int tenges) {
        // StoreBase: (type, position, tenges)
        super("fighter", position, tenges);
        // Si necesitas un color, ver opciones abajo.
    }

    public void onPlaced() {
        // No cambia su posición, solo conserva su comportamiento especial
    }

    /**
     * Verifica si un robot puede tomar el dinero de esta tienda.
     * @param robotEarnings cantidad de dinero del robot
     * @return true si puede recoger, false si no.
     */
    public boolean canBeServedBy(int robotEarnings) {
        return robotEarnings > this.tenges;
    }

    /**
     * Intenta entregar el dinero al robot.
     * Si no puede (por ser más pobre), no hace nada.
     */
    public int giveTengesToRobot(int robotEarnings) {
        if (canBeServedBy(robotEarnings)) {
            int amount = this.tenges;
            emptyOnce(); // deja la tienda en 0 y suma timesEmptied
            System.out.println("Robot rico tomó " + amount + " tenges de la tienda fighter en " + this.position);
            return amount;
        } else {
            System.out.println("Robot pobre no pudo recoger dinero de la tienda fighter en " + this.position);
            return 0;
        }
    }
}
