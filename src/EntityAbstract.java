import java.util.*;

import processing.core.PImage;

/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 */

abstract class EntityAbstract implements Entity {

    private final String id;
    private Point position;
    private final List<PImage> images;
    private int imageIndex;

    public EntityAbstract (String id, Point position, List<PImage> images) {
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
    }

    public String log(){
        return this.id.isEmpty() ? null :
                String.format("%s %d %d %d", this.id, this.position.getX(), this.position.getY(), this.imageIndex);
    }

    public Optional<Entity> findNearest(WorldModel world, Point pos, List<Class> kinds) {
        List<Entity> ofType = new LinkedList<>();
        for (Class kind : kinds) {
            for (Entity entity : world.getEntities()) {
                if (entity.getClass() == kind) {
                    ofType.add(entity);
                }
            }
        }

        return nearestEntity(ofType, pos);
    }

    public Optional<Entity> nearestEntity(List<Entity> entities, Point pos) {
        if (entities.isEmpty()) {
            return Optional.empty();
        } else {
            Entity nearest = entities.get(0);
            int nearestDistance = nearest.getPosition().distanceSquared(pos);

            for (Entity other : entities) {
                int otherDistance = other.getPosition().distanceSquared(pos);

                if (otherDistance < nearestDistance) {
                    nearest = other;
                    nearestDistance = otherDistance;
                }
            }

            return Optional.of(nearest);
        }
    }

    public Point getPosition() {
        return this.position;
    }

    public void setPosition(Point newPosition) {
        this.position = newPosition;
    }

    public String getId() {
        return this.id;
    }

    public List<PImage> getImages() {
        return this.images;
    }
    public void nextImage() {
        this.imageIndex += 1;
    }

    public int getImageIndex() {
        return this.imageIndex;
    }

    public PImage getCurrentImage() {
        return this.getImages().get(this.getImageIndex() % this.getImages().size());
    }

}
