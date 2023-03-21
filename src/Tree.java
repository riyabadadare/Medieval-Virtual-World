import processing.core.PImage;

import java.util.*;

/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 */

public final class Tree extends EntityAbstract implements Executable, Transformable {

    private final double animationPeriod;
    private final double actionPeriod;
    private int health;
    private static final String STUMP_KEY = "stump";


    public Tree(String id, Point position, List<PImage> images, double actionPeriod, double animationPeriod, int health) {
        super(id, position, images);
        this.actionPeriod = actionPeriod;
        this.animationPeriod = animationPeriod;
        this.health = health;
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        if (!this.transform(world, scheduler, imageStore)) {
            scheduler.scheduleEvent(this, this.createActivityAction(world, imageStore), this.getActionPeriod());
        }
    }

    public boolean transform(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (this.getHealth() <= 0) {
            Stump stump = Creates.createStump(STUMP_KEY + "_" + this.getId(), this.getPosition(), imageStore.getImageList(STUMP_KEY));
            world.removeEntity(scheduler, this);
            world.addEntity(stump);

            return true;
        }
        return false;
    }

    public void setHealth(int newHealth) {
        this.health = newHealth;
    }

    public int getHealth() {
        return this.health;
    }

    public double getActionPeriod() {
        return this.actionPeriod;
    }

    public double getAnimationPeriod() {
        return this.animationPeriod;
    }
}
