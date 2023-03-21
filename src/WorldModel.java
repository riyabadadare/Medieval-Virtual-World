import processing.core.PImage;

import java.util.*;

/**
 * Represents the 2D World in which this simulation is running.
 * Keeps track of the size of the world, the background image for each
 * location in the world, and the entities that populate the world.
 */
public final class WorldModel {
    private int numRows;
    private int numCols;
    private Background[][] background;
    private Entity[][] occupancy;
    private Set<Entity> entities;

    public WorldModel() {

    }

    public Optional<PImage> getBackgroundImage(Point pos) {
        if (this.withinBounds(pos)) {
            return Optional.of(this.getBackgroundCell(pos).getCurrentImage());
        } else {
            return Optional.empty();
        }
    }

    public Optional<Entity> getOccupant(Point pos) {
        if (isOccupied(pos)) {
            return Optional.of(getOccupancyCell(pos));
        } else {
            return Optional.empty();
        }
    }

    public void moveEntity(EventScheduler scheduler, Entity entity, Point pos) {
        Point oldPos = entity.getPosition();
        if (withinBounds(pos) && !pos.equals(oldPos)) {
            setOccupancyCell(oldPos, null);
            Optional<Entity> occupant = this.getOccupant(pos);
            occupant.ifPresent(target -> this.removeEntity(scheduler, target));
            setOccupancyCell(pos, entity);
            entity.setPosition(pos);
        }
    }

    public void load(Scanner saveFile, ImageStore imageStore, Background defaultBackground) {
        imageStore.parseSaveFile(this, saveFile, defaultBackground);
        if (background == null) {
            background = new Background[numRows][numCols];
            for (Background[] row : background)
                Arrays.fill(row, defaultBackground);
        }
        if (occupancy == null) {
            occupancy = new Entity[numRows][numCols];
            entities = new HashSet<>();
        }
    }


    public void tryAddEntity(Entity entity) {
        if (isOccupied(entity.getPosition())) {
            // arguably the wrong type of exception, but we are not
            // defining our own exceptions yet
            throw new IllegalArgumentException("position occupied");
        }

        this.addEntity(entity);
    }

    /**
     * Helper method for testing. Don't move or modify this method.
     */
    public List<String> log(){
        List<String> list = new ArrayList<>();
        for (Entity entity : entities) {
            String log = entity.log();
            if(log != null) list.add(log);
        }
        return list;
    }

    public boolean withinBounds(Point pos) {
        return pos.getY() >= 0 && pos.getY() < this.numRows && pos.getX() >= 0 && pos.getX() < this.numCols;
    }

    public boolean isOccupied(Point pos) {
        return this.withinBounds(pos) && this.getOccupancyCell(pos) != null;
    }

    private Background getBackgroundCell(Point pos) {
        return this.background[pos.getY()][pos.getX()];
    }

    public void setBackgroundCell(Point pos, Background background) {
        this.background[pos.getY()][pos.getX()] = background;
    }

    public Entity getOccupancyCell(Point pos) {
        return this.occupancy[pos.getY()][pos.getX()];
    }

    public void setOccupancyCell(Point pos, Entity entity) {
        this.occupancy[pos.getY()][pos.getX()] = entity;
    }

    public void scheduleActions(EventScheduler scheduler, ImageStore imageStore) {
        for (Entity entity : this.entities) {
            if (entity instanceof Schedulable) {
                ((Schedulable)entity).scheduleActions(scheduler, this, imageStore);
            }
        }
    }

    private void removeEntityAt(Point pos) {
        if (this.withinBounds(pos) && this.getOccupancyCell(pos) != null) {
            Entity entity = this.getOccupancyCell(pos);

            /* This moves the entity just outside of the grid for
             * debugging purposes. */
            entity.setPosition(new Point(-1, -1));
            this.entities.remove(entity);
            this.setOccupancyCell(pos, null);
        }
    }

    /*
   Assumes that there is no entity currently occupying the
   intended destination cell.
    */
    public void addEntity(Entity entity) {
        if (this.withinBounds(entity.getPosition())) {
            this.setOccupancyCell(entity.getPosition(), entity);
            this.entities.add(entity);
        }
    }

//    public void addFrog(Entity entity) {
//        if (this.withinBounds(entity.getPosition())) {
//            this.setOccupancyCell(new Point(entity.getPosition().x + 2, entity.getPosition().y + 2), entity);
//            this.entities.add(entity);
//        }
//    }

    public void removeEntity(EventScheduler scheduler, Entity entity) {
        scheduler.unscheduleAllEvents(entity);
        this.removeEntityAt(entity.getPosition());
    }

    public int getNumRows() {
        return numRows;
    }

    public int getNumCols() {
        return numCols;
    }

    public Background[][] getBackground() {
        return background;
    }

    public Set<Entity> getEntities() {
        return entities;
    }

    public Entity[][] getOccupancy() {
        return occupancy;
    }

    public void setOccupancy(Entity[][] occupancy) {
        this.occupancy = occupancy;
    }

    public void setEntities(Set<Entity> entities) {
        this.entities = entities;
    }

    public void setBackground(Background[][] background) {
        this.background = background;
    }

    public void setNumRows(int numRows) {
        this.numRows = numRows;
    }

    public void setNumCols(int numCols) {
        this.numCols = numCols;
    }

}
