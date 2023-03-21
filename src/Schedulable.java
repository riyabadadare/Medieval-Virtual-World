public interface Schedulable extends Entity {
    //void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore);
    default void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent(this, this.createAnimationAction(0), this.getAnimationPeriod());
    }
    default Action createAnimationAction(int repeatCount) {
        return new Animation(this, repeatCount);
    }
    void nextImage();
    double getAnimationPeriod();

}