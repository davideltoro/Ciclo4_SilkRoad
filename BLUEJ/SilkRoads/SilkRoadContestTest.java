package SilkRoads
;

import java.util.Arrays;

/**
 * Simula la lógica y parte visual del proyecto SilkRoad.
 * Autor: Juan David Castellanos - Julian Morales
 */
public class SilkRoadContestTest {

    /**
     * Ejecuta la simulación lógica (sin interfaz gráfica).
     */
    public int[] solve(int[][] days) {
        SilkRoad silkroad = new SilkRoad(20);
        int[] results = new int[days.length];

        for (int i = 0; i < days.length; i++) {
            int tipo = days[i][0];
            int pos = days[i][1];
            int c = (days[i].length > 2) ? days[i][2] : 0;

            // Crear elementos según tipo
            switch (tipo) {
                case 1 -> silkroad.placeRobot(pos, "normal");
                case 2 -> silkroad.placeStore(pos, c);
                case 3 -> silkroad.placeRobot(pos, "tender");
                case 4 -> silkroad.placeRobot(pos, "neverback");
                default -> System.out.println("⚠️ Tipo no reconocido en día " + (i + 1));
            }

            // Avanzar un día
            silkroad.moveRobot();

            // Guardar profit actual
            results[i] = silkroad.getProfit();

            // Mostrar información del día
            mostrarEstadoDia(silkroad, i + 1);
        }

        return results;
    }

    /**
     * Simula visualmente los días.
     */
    public void simulate(int[][] days, boolean slow) {
        SilkRoad silkroad = new SilkRoad(20);
        silkroad.makeVisible();

        for (int i = 0; i < days.length; i++) {
            int tipo = days[i][0];
            int pos = days[i][1];
            int c = (days[i].length > 2) ? days[i][2] : 0;

            switch (tipo) {
                case 1 -> silkroad.placeRobot(pos, "normal");
                case 2 -> silkroad.placeStore(pos, c);
                case 3 -> silkroad.placeRobot(pos, "tender");
                case 4 -> silkroad.placeRobot(pos, "neverback");
                default -> System.out.println("Tipo no reconocido en día " + (i + 1));
            }

            silkroad.moveRobot();
            mostrarEstadoDia(silkroad, i + 1);

            if (slow) pausa(800);
        }

        // Mostrar el robot con más tenges al final
        RobotBase mejor = silkroad.getRichestRobot();
        if (mejor != null) {
            System.out.println("\nRobot con más tenges al final: " + mejor);
            mejor.blinkOnce();
        }

        System.out.println("\n Profit final: " + silkroad.getProfit());
    }

    /**
     * Muestra el estado actual del SilkRoad.
     */
    private void mostrarEstadoDia(SilkRoad silkroad, int dia) {
        System.out.println("==============================");
        System.out.println(" Día " + dia);
        System.out.println("Robots: " + silkroad.getRobots());
        System.out.println("Tiendas: " + silkroad.getStores());
        System.out.println(" Profit total: " + silkroad.getProfit());

        // Mostrar el robot con más tenges en este día
        RobotBase richest = silkroad.getRichestRobot();
        if (richest != null) {
            System.out.println("Robot líder del día: " + richest.getType() +
                               " | Tenges: " + richest.getEarnings() +
                               " | Posición: " + richest.getPosition());
        }

        System.out.println("==============================\n");
    }

    /**
     * Pausa visual.
     */
    private void pausa(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

