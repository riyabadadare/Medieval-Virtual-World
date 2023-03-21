public interface Executable extends Schedulable {
    void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler);
    default Action createActivityAction(WorldModel world, ImageStore imageStore) {
        return new Activity(this, world, imageStore);
    }
    default void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent(this, this.createActivityAction(world, imageStore), this.getActionPeriod());
        Schedulable.super.scheduleActions(scheduler, world, imageStore);
    }
    double getActionPeriod();

}
