import java.util.*;

import processing.core.PImage;

public final class Creates {

    public static House createHouse(String id, Point position, List<PImage> images) {
        return new House(id, position, images);
    }

    public static Obstacle createObstacle(String id, Point position, double animationPeriod, List<PImage> images) {
        return new Obstacle(id, position, images, animationPeriod);
    }

    public static Tree createTree(String id, Point position, double actionPeriod, double animationPeriod, int health, List<PImage> images) {
        return new Tree(id, position, images, actionPeriod, animationPeriod, health);
    }

    public static Stump createStump(String id, Point position, List<PImage> images) {
        return new Stump(id, position, images);
    }

    // health starts at 0 and builds up until ready to convert to Tree
    public static Sapling createSapling(String id, Point position, List<PImage> images, int health) {
        return new Sapling(id, position, images, ImageStore.SAPLING_ACTION_ANIMATION_PERIOD, ImageStore.SAPLING_ACTION_ANIMATION_PERIOD, 0, ImageStore.SAPLING_HEALTH_LIMIT);
    }

    public static Fairy createFairy(String id, Point position, double actionPeriod, double animationPeriod, List<PImage> images) {
        return new Fairy(id, position, images, actionPeriod, animationPeriod);
    }

    // need resource count, though it always starts at 0
    public static DudeNotFull createDudeNotFull(String id, Point position, double actionPeriod, double animationPeriod, int resourceLimit, List<PImage> images) {
        return new DudeNotFull(id, position, images, resourceLimit, 0, actionPeriod, animationPeriod);
    }

    // don't technically need resource count ... full
    public static DudeFull createDudeFull(String id, Point position, double actionPeriod, double animationPeriod, int resourceLimit, List<PImage> images) {
        return new DudeFull(id, position, images, resourceLimit, actionPeriod, animationPeriod);
    }

    public static Cow createCow(String id, Point position, double actionPeriod, double animationPeriod, List<PImage> images) {
        return new Cow(id, position, images, actionPeriod, animationPeriod);
    }

    public static Frog createFrog(String id, Point position, double actionPeriod, double animationPeriod, List<PImage> images) {
        return new Frog(id, position, images, actionPeriod, animationPeriod);
    }

    public static Princess createPrincess(String id, Point position, double actionPeriod, double animationPeriod, List<PImage> images) {
        return new Princess(id, position, images, actionPeriod, animationPeriod);
    }

}