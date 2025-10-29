package SilkRoads;

import Shapes.*;

/**
 * Juan David Castellanos
 * Clase NeverBackRobot:
 * Representa un robot que nunca retrocede en el tablero.
 * Hereda toda la funcionalidad de RobotBase.
 *
 * Autor: Juan David Castellanos - Julian Morales
 */
public class NeverBackRobot extends RobotBase {

    /**
     * Constructor del NeverBackRobot.
     *
     * @param position posición inicial del robot
     * @param tenges cantidad inicial de tenges
     */
    public NeverBackRobot(int position, int tenges) {
        // color amarillo para diferenciarlo
        super("neverback", position, tenges, "yellow");
    }

    /**
     * Movimiento del robot: solo puede avanzar hacia adelante.
     * Nunca se devuelve a una posición anterior.
     */
    @Override
    public void move() {
        // Por defecto avanza una celda hacia adelante
        int nextPosition = currentPosition + 1;

        // Si el movimiento propuesto lo haría retroceder, lo ignora
        if (nextPosition < currentPosition) {
            // No hace nada
            return;
        }

        currentPosition = nextPosition;
    }

    /**
     * Mueve al robot a una posición específica, pero nunca hacia atrás.
     * Puede usarse si en el juego se dan saltos más grandes.
     *
     * newPosition nueva posición a la que se desea mover
     */
    public void moveTo(int newPosition) {
        if (newPosition > currentPosition) {
            currentPosition = newPosition;
        }
        // Si es menor o igual, ignora el movimiento (no retrocede)
    }
}
