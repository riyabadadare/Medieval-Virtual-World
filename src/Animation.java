/**
 * An action that can be taken by an entity
 */
public final class Animation implements Action {
    private final Schedulable entity;
    private final int repeatCount;

    public Animation(Schedulable entity, int repeatCount) {
        this.entity = entity;
        this.repeatCount = repeatCount;
    }

    public void executeAction(EventScheduler scheduler) {
            this.entity.nextImage();
            if (this.repeatCount != 1) {
                scheduler.scheduleEvent(this.entity, this.entity.createAnimationAction(Math.max(this.repeatCount - 1, 0)), this.entity.getAnimationPeriod());
            }
    }
}
