/**
 * A simple class representing a location in 2D space.
 */
public final class Point {

    public final int x;
    public final int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public String toString() {
        return "(" + x + "," + y + ")";
    }

    public boolean equals(Object other) {
        return other instanceof Point && ((Point) other).x == this.x && ((Point) other).y == this.y;
    }

    public boolean adjacent(Point p2) {
        return (this.getX() == p2.getX() && Math.abs(this.getY() - p2.getY()) == 1)
                || (this.getY() == p2.getY() && Math.abs(this.getX() - p2.getX()) == 1);
    }

    public int distanceSquared(Point p2) {
        int deltaX = this.getX() - p2.getX();
        int deltaY = this.getY() - p2.getY();

        return deltaX * deltaX + deltaY * deltaY;
    }

    public int hashCode() {
        int result = 17;
        result = result * 31 + x;
        result = result * 31 + y;
        return result;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public double distance (Point p) {
        double xDiff = this.x - p.x;
        double yDiff = this.y - p.y;
        return Math.sqrt(Math.pow(xDiff, 2) + Math.pow(yDiff, 2));
    }
}
