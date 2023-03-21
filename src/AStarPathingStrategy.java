import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class AStarPathingStrategy
        implements PathingStrategy
{

    public List<Point> computePath(Point start, Point end,
                                   Predicate<Point> canPassThrough,
                                   BiPredicate<Point, Point> withinReach,
                                   Function<Point, Stream<Point>> potentialNeighbors)
    {
        List<Point> path = new LinkedList<Point>();
        PriorityQueue<Node> openList = new PriorityQueue<>(Comparator.comparing(Node::getF));
        HashMap<Point, Node> fakeOpenList = new HashMap<>();
        HashSet<Point> closedList = new HashSet();
        Node current = new Node(0, start.distance(end), null, start);
        openList.add(current);
        fakeOpenList.put(current.getLoc(), current);
        while (!openList.isEmpty()) {
            current = openList.poll();
            if (withinReach.test(current.getLoc(), end)) {
                break;
            }
            List<Point> filteredNeighbors = potentialNeighbors.apply(current.getLoc())
                    .filter(canPassThrough)
                    .filter(p -> !closedList.contains(p))
                    .collect(Collectors.toList());
            for (Point fn: filteredNeighbors) {
                Node n = new Node(current.getG()+1, fn.distance(end), current, fn);
                if (fakeOpenList.containsKey(fn)) {
                    if (n.getG() < fakeOpenList.get(fn).getG()) {
                        openList.remove(fakeOpenList.get(fn));
                        openList.add(n);
                        fakeOpenList.replace(fn, n);
                    }
                }
                else {
                    openList.add(n);
                    fakeOpenList.put(n.getLoc(), n);
                }
            }
            closedList.add(current.getLoc());
        }
        if (!withinReach.test(current.getLoc(), end)) {
            return path;
        }
        while (current.getPrior() != null) {
            path.add(0, current.getLoc());
            current = current.getPrior();
        }
        return path;
    }
}

class Node {
    private double f;
    private double g;
    private double h;
    private Node prior;
    private Point loc;

    public Node (double g, double h, Node prior, Point loc) {
        this.f = g+h;
        this.g = g;
        this.h = h;
        this.prior = prior;
        this.loc = loc;
    }

    public boolean equals(Object o) {
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Node n = (Node) o;
        return Objects.equals(this.loc, n.loc);
    }

    public int hashCode() {
        int result = 23;
        result = result * 37 + this.loc.getX();
        result = result * 37 + this.loc.getY();
        return result;
    }

    public double getF() {
        return this.f;
    }
    public double getG() {
        return this.g;
    }

    public Point getLoc() {
        return this.loc;
    }

    public Node getPrior() {
        return this.prior;
    }
}
