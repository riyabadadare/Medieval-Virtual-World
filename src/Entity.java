import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public interface Entity {
    Point getPosition();
    String log();
    Optional<Entity> findNearest(WorldModel world, Point pos, List<Class> kinds);
    PImage getCurrentImage();
    String getId();
    void setPosition(Point newPosition);

}
