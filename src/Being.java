import java.util.List;

public interface Being extends Movable {
    Point getPosition();
    default Point nextPosition(WorldModel world, Point destPos) {
        PathingStrategy strat = new AStarPathingStrategy();
        List<Point> path = strat.computePath(getPosition(), destPos,
                ps -> world.withinBounds(ps) && (!world.isOccupied(ps) || world.getOccupancyCell(ps).getClass() == Stump.class), Point::adjacent,
                PathingStrategy.CARDINAL_NEIGHBORS);
        if (path == null || path.size() == 0) {
            return getPosition();
        }
        return path.get(0);
    }
}