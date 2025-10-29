package SilkRoads;

import Shapes.*;

/**
 * Juan David Castellanos, Julian Morales
 * Clase TenderRobot:
 * Representa un robot que solo toma la mitad del dinero disponible en las tiendas.
 * Hereda de RobotBase.
 *
 * Autor: Juan David Castellanos - Julian Morales
 */
public class TenderRobot extends RobotBase {

    /**
     * Constructor del TenderRobot.
     *
     * @param position posición inicial del robot
     * @param tenges cantidad inicial de tenges
     */
    public TenderRobot(int position, int tenges) {
        // color azul para distinguirlo de los demás
        super("tender", position, tenges, "blue");
    }

    /**
     * Movimiento del robot tender.
     * En este caso avanza igual que un robot normal (1 posición por turno).
     */
    @Override
    public void move() {
        currentPosition += 1;
    }

    /**
     * Este robot solo toma la mitad del dinero disponible en la tienda.
     *
     * @param store Tienda de la que se tomará el dinero.
     */
    public void collectFromStore(Store store) {
        if (store == null) return;

        int available = store.getTenges();
        if (available > 0) {
            int half = available / 2;

            // Quita la mitad del dinero de la tienda
            int remaining = available - half;
            try {
                // usamos reflexión mínima para actualizar el dinero (sin modificar Store)
                java.lang.reflect.Field f = Store.class.getDeclaredField("tenges");
                f.setAccessible(true);
                f.setInt(store, remaining);
            } catch (Exception e) {
                System.err.println("No se pudo ajustar el dinero de la tienda: " + e.getMessage());
                return;
            }

            // actualiza color si quedó vacía
            if (remaining == 0) {
                try {
                    java.lang.reflect.Method updateColor = Store.class.getDeclaredMethod("updateColor");
                    updateColor.setAccessible(true);
                    updateColor.invoke(store);
                } catch (Exception ignored) {}
            }

            // añade las ganancias al robot
            addEarnings(half);
        }
    }
}

