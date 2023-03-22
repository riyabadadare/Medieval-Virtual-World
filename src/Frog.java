import processing.core.PImage;

import java.util.*;
import java.util.function.Predicate;

/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 */

public final class Frog extends EntityAbstract implements Being {

    private final double animationPeriod;
    private final double actionPeriod;

    public Frog(String id, Point position, List<PImage> images, double actionPeriod, double animationPeriod) {
        super(id, position, images);
        this.actionPeriod = actionPeriod;
        this.animationPeriod = animationPeriod;
    }

    @Override
    public Point nextPosition(WorldModel world, Point destPos) {
        PathingStrategy strat = new AStarPathingStrategy();
        List<Point> path = strat.computePath(getPosition(), destPos,
                ps -> world.withinBounds(ps) && !world.isOccupied(ps), Point::adjacent,
                PathingStrategy.CARDINAL_NEIGHBORS
        );
        if (path == null || path.size() == 0) {
            return getPosition();
        }
        return path.get(0);
    }

    public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {
        if (getPosition().adjacent(target.getPosition())) {
            world.removeEntity(scheduler, target);
            return true;
        } else {
            Point nextPos = this.nextPosition(world, target.getPosition());

            Point currPos = getPosition();
            Obstacle water = new Obstacle("obstacle", currPos, virtualWorld.getImageStore().getImageList("obstacle"), 1);
            world.setOccupancyCell(currPos, water);

            if (!getPosition().equals(nextPos)) {
                world.moveEntity(scheduler, this, nextPos);
            }
            return false;
        }
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> frogTarget = findNearest(world, this.getPosition(), new ArrayList<>(List.of(Stump.class)));

        if (frogTarget.isPresent()) {
            Point tgtPos = frogTarget.get().getPosition();

            if (this.moveTo(world, frogTarget.get(), scheduler)) {

                Sapling sapling = Creates.createSapling(ImageStore.SAPLING_KEY + "_" + frogTarget.get().getId(), tgtPos, imageStore.getImageList(ImageStore.SAPLING_KEY), 0);

                world.addEntity(sapling);
                sapling.scheduleActions(scheduler, world, imageStore);
            }
        }
        scheduler.scheduleEvent(this, this.createActivityAction(world, imageStore), this.getActionPeriod());
    }

    public double getActionPeriod() {
        return this.actionPeriod;
    }

    public double getAnimationPeriod() {
        return this.animationPeriod;
    }

}
