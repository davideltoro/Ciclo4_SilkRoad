package SilkRoads;

public class AutonomousStore extends StoreBase {

    public AutonomousStore(int position, int tenges) {
        super("autonomous", position, tenges);
    }

    @Override
    public void onPlaced(SilkRoad road) {
        if (road == null) return;

        // Usamos el mismo algoritmo pero con datos del road real
        int routeSize = road.getRouteSize();
        boolean[] isFree = new boolean[routeSize];
        for (int i = 0; i < routeSize; i++) {
            isFree[i] = road.isFreeForStore(i);
        }
        relocateUsingFreeMap(routeSize, isFree);
    }

    /**
     * Helper público para pruebas sin tablero:
     * Busca la primera posición libre en la ventana [position .. position+4],
     * acotada por routeSize.
     */
    public void relocateUsingFreeMap(int routeSize, boolean[] isFree) {
        int limit = Math.min(this.position + 5, routeSize);
        int newPos = this.position;

        for (int i = this.position; i < limit; i++) {
            if (i >= 0 && i < isFree.length && isFree[i]) {
                newPos = i;
                break;
            }
        }
        if (newPos != this.position) {
            this.position = newPos;
        }
    }
}
