import processing.core.PImage;

import java.util.*;

/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 */

public final class Stump extends EntityAbstract {

    public Stump(String id, Point position, List<PImage> images) {
        super(id, position, images);
    }

}
