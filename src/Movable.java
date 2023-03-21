public interface Movable extends Executable {
    boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler);
}
