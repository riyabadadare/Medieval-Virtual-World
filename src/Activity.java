/**
 * An action that can be taken by an entity
 */

public final class Activity implements Action {
    private final Entity entity;
    private final WorldModel world;
    private final ImageStore imageStore;

    public Activity(Entity entity, WorldModel world, ImageStore imageStore) {
        this.entity = entity;
        this.world = world;
        this.imageStore = imageStore;
    }

    public void executeAction(EventScheduler scheduler) {
        if (this.entity instanceof Executable) {
            ((Executable) this.entity).executeActivity(this.world, this.imageStore, scheduler);
        }
        else {
            throw new UnsupportedOperationException(String.format("executeActivityAction not supported for %s", this.entity.getClass()));
        }
    }
}
