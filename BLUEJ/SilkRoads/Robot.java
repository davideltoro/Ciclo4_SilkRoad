package SilkRoads;

public class Robot extends RobotBase {

    public Robot(int position, int tenges) {
        super("normal", position, tenges, "red");
    }

    @Override
    public void move() {
        currentPosition += 1;
    }
}
