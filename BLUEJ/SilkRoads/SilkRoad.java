package SilkRoads;
import Shapes.*;
import java.util.*;

/**
 * Clase principal SilkRoad: maneja tablero, tiendas, robots y lógica de la espiral.
 * Autor: Juan David Castellanos - Julian Morales
 */
public class SilkRoad {

    // ----- Estado principal -----
    private final int initialLength;     
    private int length;                  

    // Ahora usamos StoreBase
    private final List<StoreBase> stores;  
    private final List<RobotBase> robots;

    private RobotBase currentLeader;

    // ----- Visual y control -----
    private Board board;
    private boolean showing;
    private boolean lastOperationOk;
    private int profit;
    private int lastPrintedProfit = -1;

    // Barra de progreso
    private Rectangle profitBarBackground;
    private Rectangle profitBarFill;
    private static final int MAX_PROFIT = 1000;

    // Visibilidad de la espiral
    private int visibleTarget;

    // Paleta
    private final String[] robotColors = {"red", "blue", "green", "orange", "magenta", "pink"};
    private final String[] storeColors = {"yellow", "cyan", "gray", "black", "lightgray"};

    // Offset para mapear coords lógicas a board
    private int originRow = 0;
    private int originCol = 0;

    // Log de ganancias
    private final List<int[]> robotGainLog = new ArrayList<>();

    // ================== CONSTRUCTOR ==================
    public SilkRoad(int length) {
        this.initialLength = length;
        this.length = 0;

        this.stores = new ArrayList<>();
        this.robots = new ArrayList<>();

        this.lastOperationOk = true;
        this.profit = 0;
        this.showing = false;

        this.board = new Board(1, 1);
        this.originRow = 0;
        this.originCol = 0;

        // Preparar espiral visible inicial
        this.visibleTarget = Math.max(1, initialLength);
        preGrowToVisibleTarget();
        applyVisibleMaskForFirstN();

        // Mostrar tablero y crear barra (modo gráfico)
        board.makeVisible();

        // Crear la barra de progreso visual
        profitBarBackground = new Rectangle();
        profitBarFill = new Rectangle();

        profitBarBackground.changeSize(20, 200);
        profitBarBackground.changeColor("gray");
        profitBarFill.changeSize(20, 0);
        profitBarFill.changeColor("green");

        layoutProfitBarAboveBoard();

        profitBarBackground.makeVisible();
        profitBarFill.makeVisible();

        showing = true;
        updateProfitBar();
    }

    // ================== VISIBILIDAD ==================
    public void makeVisible() {
        board.makeVisible();

        for (StoreBase s : stores) s.makeVisible();
        for (RobotBase r : robots) r.makeVisible();

        if (isBarReady()) {
            profitBarBackground.makeVisible();
            profitBarFill.makeVisible();
        }

        showing = true;
        updateProfitBar();
    }

    public void makeInvisible() {
        for (StoreBase s : stores) s.makeInvisible();
        for (RobotBase r : robots) r.makeInvisible();
        board.makeInvisible();

        if (isBarReady()) {
            profitBarBackground.makeInvisible();
            profitBarFill.makeInvisible();
        }
        showing = false;
    }

    public RobotBase getRichestRobot() {
        if (robots.isEmpty()) return null;
        RobotBase richest = robots.get(0);
        for (RobotBase r : robots) if (r.getEarnings() > richest.getEarnings()) richest = r;
        return richest;
    }

    // ================== ALTAS / BAJAS ==================
    private boolean storeExistsAt(int location) {
        for (StoreBase s : stores) if (s.getPosition() == location) return true;
        return false;
    }

    public void placeStore(int location, String type, int tenges) {
        if (storeExistsAt(location)) { lastOperationOk = false; return; }

        ensureBoardCanFitForLocation(location);
        int[] rcBoard = boardCoordsForLocation(location);

        StoreBase newStore;
        switch (type.toLowerCase()) {
            case "autonomous" -> newStore = new AutonomousStore(location, tenges);
            case "fighter"    -> newStore = new FighterStore(location, tenges);
            default           -> newStore = new Store(location, tenges);
        }

        newStore.setRoad(this);
        newStore.moveStore(rcBoard[0], rcBoard[1]);
        newStore.setColor(storeColors[stores.size() % storeColors.length]);

        stores.add(newStore);

        if (showing) {
            newStore.makeVisible();
            resyncVisuals();
        }

        lastOperationOk = true;
    }

    public void placeStore(int location, int tenges) {
        placeStore(location, "normal", tenges);
    }

    public void placeRobot(int location, String type) {
        ensureBoardCanFitForLocation(location);
        int[] rcBoard = boardCoordsForLocation(location);

        RobotBase newRobot;
        switch (type.toLowerCase()) {
            case "neverback" -> newRobot = new NeverBackRobot(location, 0);
            case "tender"    -> newRobot = new TenderRobot(location, 0);
            default          -> newRobot = new Robot(location, 0);
        }

        newRobot.setRoad(this);
        newRobot.moveRobot(rcBoard[0], rcBoard[1]);
        robots.add(newRobot);

        if (showing) {
            newRobot.makeVisible();
            resyncVisuals();
        }

        lastOperationOk = true;
    }

    public void placeRobot(int position) { placeRobot(position, "normal"); }

    public void removeStore(int location) {
        boolean removed = stores.removeIf(s -> {
            if (s.getPosition() == location) {
                s.makeInvisible();
                return true;
            }
            return false;
        });
        lastOperationOk = removed;
    }

    public void removeRobot(int location) {
        boolean removed = robots.removeIf(r -> {
            if (r.getInitialPosition() == location) {
                r.makeInvisible();
                return true;
            }
            return false;
        });
        lastOperationOk = removed;
    }

    // ================== MOVIMIENTO (manual) ==================
    public void moveRobot(int currentPosition, int meters) {
        for (RobotBase r : robots) {
            if (r.getPosition() == currentPosition) {
                int newLocation = currentPosition + meters;
                if (!insideVisibleTarget(newLocation)) ensureBoardCanFitForLocation(newLocation);
                r.currentPosition = newLocation;

                int[] rcBoard = boardCoordsForLocation(newLocation);
                r.moveRobot(rcBoard[0], rcBoard[1]);
                r.makeVisible();

                for (StoreBase s : stores) {
                    if (s.getPosition() == newLocation && s.getTenges() > 0) {
                        int collected = s.getTenges();
                        s.emptyOnce();
                        int gain = collected - Math.abs(meters);
                        if (gain > 0) {
                            profit += gain;
                            updateProfitBar();
                            r.addEarnings(gain);
                        }
                    }
                }

                resyncVisuals();
                lastOperationOk = true;
                return;
            }
        }
        lastOperationOk = false;
    }

    // ================== MOVIMIENTO (automático) ==================
    public void moveRobot() {
        int bestGain = Integer.MIN_VALUE;
        RobotBase bestRobot = null;
        StoreBase bestStore = null;

        for (RobotBase r : robots) {
            for (StoreBase s : stores) {
                if (s.getTenges() <= 0) continue;
                int dist = Math.abs(r.getPosition() - s.getPosition());
                int gain = s.getTenges() - dist;
                if (gain > bestGain) {
                    bestGain = gain;
                    bestRobot = r;
                    bestStore = s;
                }
            }
        }

        if (bestRobot == null || bestStore == null || bestGain <= 0) {
            lastOperationOk = false;
            return;
        }

        int meters = bestStore.getPosition() - bestRobot.getPosition();
        if (!insideVisibleTarget(bestStore.getPosition())) ensureBoardCanFitForLocation(bestStore.getPosition());
        int[] rc = boardCoordsForLocation(bestStore.getPosition());

        bestRobot.currentPosition = bestStore.getPosition();
        bestRobot.moveRobot(rc[0], rc[1]);

        int collected = bestStore.getTenges();
        bestStore.emptyOnce();
        int gain = collected - Math.abs(meters);

        if (gain > 0) {
            bestRobot.addEarnings(gain);
            profit += gain;
            updateProfitBar();
        }

        resyncVisuals();
        lastOperationOk = true;
    }

    // ================== RESETEO DE DÍA ==================
    public void resupplyStores() {
        for (StoreBase s : stores) s.resupply();
        lastOperationOk = true;
    }

    public void returnRobots() {
        for (RobotBase r : robots) {
            r.reset();
            int loc = r.getInitialPosition();
            ensureBoardCanFitForLocation(loc);
            int[] rc = boardCoordsForLocation(loc);
            r.moveRobot(rc[0], rc[1]);
            r.makeVisible();
        }
        lastOperationOk = true;
    }

    public void reboot() {
        resupplyStores();
        returnRobots();
        profit = 0;
        updateProfitBar();
        lastOperationOk = true;
    }

    // ================== PROFIT BAR ==================
    private void updateProfitBar() {
        if (!showing) return;
        int fillHeight = (int) (200.0 * profit / MAX_PROFIT);
        if (fillHeight > 200) fillHeight = 200;
        profitBarFill.changeSize(20, Math.max(fillHeight, 0));

        if (profit != lastPrintedProfit) {
            System.out.println("Profit actual: " + profit);
            lastPrintedProfit = profit;
        }
    }

    private boolean isBarReady() {
        return profitBarBackground != null && profitBarFill != null;
    }

    private void layoutProfitBarAboveBoard() {
        profitBarBackground.moveHorizontal(0);
        profitBarBackground.moveVertical(0);
        profitBarFill.moveHorizontal(0);
        profitBarFill.moveVertical(0);
    }

    // ================== ESPIRAL ==================
    public int[] locationToCoords(int location) {
        if (location == 0) return new int[]{0, 0};
        int r = 0, c = 0;
        final int[] dr = {0, +1, 0, -1};
        final int[] dc = {+1, 0, -1, 0};
        int dir = 0, segLen = 1, used = 0, steps = location;
        while (steps-- > 0) {
            r += dr[dir];
            c += dc[dir];
            used++;
            if (used == segLen) {
                used = 0;
                dir = (dir + 1) % 4;
                if (dir % 2 == 0) segLen++;
            }
        }
        return new int[]{r, c};
    }

    private int[] boardCoordsFromLogical(int rLog, int cLog) {
        return new int[]{rLog + originRow, cLog + originCol};
    }

    private int[] boardCoordsForLocation(int location) {
        int[] rcLog = locationToCoords(location);
        return boardCoordsFromLogical(rcLog[0], rcLog[1]);
    }

    // ================== CRECIMIENTO / VISIBILIDAD ==================
    private void preGrowToVisibleTarget() {
        int minR = 0, maxR = 0, minC = 0, maxC = 0;
        for (int i = 0; i < visibleTarget; i++) {
            int[] rc = locationToCoords(i);
            minR = Math.min(minR, rc[0]);
            maxR = Math.max(maxR, rc[0]);
            minC = Math.min(minC, rc[1]);
            maxC = Math.max(maxC, rc[1]);
        }

        originRow = -minR;
        originCol = -minC;

        int needRows = maxR - minR + 1;
        int needCols = maxC - minC + 1;

        if (needRows > board.getRows() || needCols > board.getCols()) {
            boolean wasVisible = showing;
            if (wasVisible) board.makeInvisible();
            board = new Board(needRows, needCols);
            if (wasVisible) board.makeVisible();
            if (wasVisible) resyncVisuals();
            if (isBarReady()) layoutProfitBarAboveBoard();
        }
    }

    private void applyVisibleMaskForFirstN() {
        int rows = board.getRows();
        int cols = board.getCols();
        boolean[][] mask = new boolean[rows][cols];
        for (int i = 0; i < visibleTarget; i++) {
            int[] rc = locationToCoords(i);
            int br = rc[0] + originRow;
            int bc = rc[1] + originCol;
            if (br >= 0 && br < rows && bc >= 0 && bc < cols) mask[br][bc] = true;
        }
        board.setVisibleCells(mask);
    }

    private void ensureBoardCanFitForLocation(int location) {
        if (location + 1 > visibleTarget) {
            visibleTarget = location + 1;
            preGrowToVisibleTarget();
            applyVisibleMaskForFirstN();
        }
        int[] rcLog = locationToCoords(location);
        ensureBoardCanFitLogical(rcLog[0], rcLog[1]);
        applyVisibleMaskForFirstN();
        layoutProfitBarAboveBoard();
    }

    private void ensureBoardCanFitLogical(int rLog, int cLog) {
        int br = rLog + originRow;
        int bc = cLog + originCol;

        int newRows = board.getRows();
        int newCols = board.getCols();
        int addTop = 0, addLeft = 0;

        if (br < 0) { addTop = -br; originRow += addTop; newRows += addTop; br = 0; }
        if (bc < 0) { addLeft = -bc; originCol += addLeft; newCols += addLeft; bc = 0; }
        if (br >= newRows) newRows = br + 1;
        if (bc >= newCols) newCols = bc + 1;

        if (newRows == board.getRows() && newCols == board.getCols() && addTop == 0 && addLeft == 0) return;

        boolean wasVisible = showing;
        if (wasVisible) board.makeInvisible();

        board = new Board(newRows, newCols);

        if (wasVisible) {
            board.makeVisible();
            resyncVisuals();
        }
        if (isBarReady()) layoutProfitBarAboveBoard();
    }

    private boolean insideVisibleTarget(int loc) {
        return loc >= 0 && loc < visibleTarget;
    }

    private void resyncVisuals() {
        for (StoreBase s : stores) {
            int[] rc = boardCoordsForLocation(s.getPosition());
            s.moveStore(rc[0], rc[1]);
            s.makeVisible();
        }
        for (RobotBase rr : robots) {
            int[] rc = boardCoordsForLocation(rr.getPosition());
            rr.moveRobot(rc[0], rc[1]);
            rr.makeVisible();
        }
    }

    // ================== LÍDER ==================
    public void updateLeader() {
        if (robots.isEmpty()) return;
        RobotBase leader = robots.get(0);
        for (RobotBase r : robots) if (r.getEarnings() > leader.getEarnings()) leader = r;

        if (leader != currentLeader) {
            if (currentLeader != null) currentLeader.setBlinking(false);
            leader.setBlinking(true);
            currentLeader = leader;
        }
    }

    // =================== AUXILIARES ===================
    public boolean isFreeForStore(int loc) {
        for (StoreBase s : stores) if (s.getPosition() == loc) return false;
        return true;
    }

    public int getRouteSize() {
        return Math.max(visibleTarget, 1);
    }
    
    public int getProfit() {
    return profit;
    }
    
    public java.util.List<RobotBase> getRobots() {
        // Devolvemos una copia para no exponer la lista interna
        return new java.util.ArrayList<RobotBase>(robots);
    }
    
    public java.util.List<StoreBase> getStores() {
        return new java.util.ArrayList<StoreBase>(stores);
    }
}
