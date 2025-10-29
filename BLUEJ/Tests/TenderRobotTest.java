package Tests;

import SilkRoads.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Prueba unitaria para la clase TenderRobot.
 * Verifica movimiento, ganancias y recolección de tiendas.
 *
 * Autor: Juan David Castellanos, Julian Morales
 */
public class TenderRobotTest {

    // Clase Store simulada para las pruebas (mock simple)
    static class MockStore extends Store {
        public MockStore(int tenges) {
            super(0, tenges); // posición 0, tenges inicial
        }
    }

    @Test
    public void testInicializacion() {
        TenderRobot robot = new TenderRobot(5, 10);

        assertEquals(5, robot.getPosition(), "Debe iniciar en posición 5");
        assertEquals(10, robot.getEarnings(), "Debe iniciar con 10 tenges");
        assertEquals("tender", robot.getType(), "Tipo debe ser 'tender'");
    }

    @Test
    public void testMovimiento() {
        TenderRobot robot = new TenderRobot(0, 0);
        robot.move();
        assertEquals(1, robot.getPosition(), "Debe avanzar una posición");
    }

    @Test
    public void testCollectFromStore_MitadDelDinero() {
        TenderRobot robot = new TenderRobot(0, 0);
        MockStore store = new MockStore(100);

        robot.collectFromStore(store);

        // El robot debe ganar la mitad
        assertEquals(50, robot.getEarnings(), "Debe recolectar la mitad (50)");
        // La tienda debe quedarse con la mitad restante
        assertEquals(50, store.getTenges(), "La tienda debe conservar la mitad (50)");
    }

    @Test
    public void testCollectFromStore_TiendaVacia() {
        TenderRobot robot = new TenderRobot(0, 0);
        MockStore store = new MockStore(0);

        robot.collectFromStore(store);

        assertEquals(0, robot.getEarnings(), "No debe ganar nada si la tienda está vacía");
        assertEquals(0, store.getTenges(), "La tienda sigue vacía");
    }

    @Test
    public void testCollectFromStore_Null() {
        TenderRobot robot = new TenderRobot(0, 0);
        robot.collectFromStore(null);

        assertEquals(0, robot.getEarnings(), "No debe lanzar error si la tienda es null");
    }

    @Test
    public void testNoSumaGananciaNegativa() {
        TenderRobot robot = new TenderRobot(0, 10);
        robot.addEarnings(-20);
        assertEquals(10, robot.getEarnings(), "No debe aceptar ganancias negativas");
    }
}
