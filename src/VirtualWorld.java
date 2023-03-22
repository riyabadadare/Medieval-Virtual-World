import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

import com.sun.jdi.ArrayReference;
import processing.core.*;

public final class VirtualWorld extends PApplet {
    private static String[] ARGS;

    private static final int VIEW_WIDTH = 640;
    private static final int VIEW_HEIGHT = 480;
    private static final int TILE_WIDTH = 32;
    private static final int TILE_HEIGHT = 32;

    private static final int VIEW_COLS = VIEW_WIDTH / TILE_WIDTH;
    private static final int VIEW_ROWS = VIEW_HEIGHT / TILE_HEIGHT;

    private static final String IMAGE_LIST_FILE_NAME = "imagelist";
    private static final String DEFAULT_IMAGE_NAME = "background_default";
    private static final int DEFAULT_IMAGE_COLOR = 0x808080;

    private static final String FAST_FLAG = "-fast";
    private static final String FASTER_FLAG = "-faster";
    private static final String FASTEST_FLAG = "-fastest";
    private static final double FAST_SCALE = 0.5;
    private static final double FASTER_SCALE = 0.25;
    private static final double FASTEST_SCALE = 0.10;

    private String loadFile = "world.sav";
    private long startTimeMillis = 0;
    private double timeScale = 1.0;

    private ImageStore imageStore;
    private WorldModel world;
    private WorldView view;
    private EventScheduler scheduler;

    public void settings() {
        size(VIEW_WIDTH, VIEW_HEIGHT);
    }

    /*
       Processing entry point for "sketch" setup.
    */
    public void setup() {
        parseCommandLine(ARGS);
        loadImages(IMAGE_LIST_FILE_NAME);
        loadWorld(loadFile, this.imageStore);

        this.view = new WorldView(VIEW_ROWS, VIEW_COLS, this, world, TILE_WIDTH, TILE_HEIGHT);
        this.scheduler = new EventScheduler();
        this.startTimeMillis = System.currentTimeMillis();
        world.scheduleActions(scheduler, imageStore);
    }

    public void draw() {
        double appTime = (System.currentTimeMillis() - startTimeMillis) * 0.001;
        double frameTime = (appTime - scheduler.getCurrentTime())/timeScale;
        this.update(frameTime);
        view.drawViewport();
    }

    public void update(double frameTime){
        scheduler.updateOnTime(frameTime);
    }

    // Just for debugging and for P5
    // Be sure to refactor this method as appropriate
    public void mousePressed() {
        Point pressed = mouseToPoint();

        System.out.println("CLICK! " + pressed.getX() + ", " + pressed.getY());

        Optional<Entity> entityOptional = world.getOccupant(pressed);
//        Entity entity = world.getOccupant(pressed);
//        if (entityOptional.isPresent()) {
//            Entity entity = entityOptional.get();
//            System.out.println(entity.getId() + ": " + entity.getClass() + " : ");
//        }
        if(entityOptional.isPresent() && entityOptional.get().getClass() == Obstacle.class) {
            world.removeEntity(scheduler, entityOptional.get());
            Background lilypad = new Background("lilypad", imageStore.getImageList("lilypad"));
            world.setBackgroundCell(pressed, lilypad);
            Frog frog = new Frog("frog", pressed, imageStore.getImageList("frog"), 1, 1);
            world.addEntity(frog);
            frog.scheduleActions(scheduler, world, imageStore);

//            Background frogBack = new Background("frog", imageStore.getImageList("frog"));
//            world.setBackgroundCell(new Point(pressed.x + 2, pressed.y + 2), frogBack);
        }
        else if(entityOptional.isEmpty()){

            List<Point> pressedNeighbors = neighbors(pressed);
            boolean nearFrog = false;
            for(Point pN : pressedNeighbors) {
                if(world.getOccupancyCell(pN) != null && world.getOccupancyCell(pN).getClass() == Frog.class) {
                    nearFrog = true;
                }
            }
            System.out.println(nearFrog);
            if(nearFrog) {
                //spawn princess
                Princess princess = new Princess("princess", pressed, imageStore.getImageList("princess"), 1, 1);
                world.addEntity(princess);
                princess.scheduleActions(scheduler, world, imageStore);
                System.out.println("Hi");
            }
            else {
                //spawn cow
                Cow cow = new Cow("cow", pressed, imageStore.getImageList("cow"), 1, 1);
                world.addEntity(cow);
                cow.scheduleActions(scheduler, world, imageStore);
                Background dirtCow = new Background("dirt", imageStore.getImageList("dirt"));

                List<Point> validneighbors =
                        neighbors(pressed).stream()
                                .filter(p -> world.getBackgroundImage(p).get() != imageStore.getImageList("lilypad").get(0))
                                .collect(Collectors.toList());
                for(Point n : validneighbors) {
                    world.setBackgroundCell(n, dirtCow);
                }
            }
        }
    }

    private ArrayList<Point> neighbors(Point p) {
        ArrayList<Point> neighbors = new ArrayList<Point>();
        neighbors.add(new Point(p.x + 1, p.y));
        neighbors.add(new Point(p.x + 1, p.y + 1));
        neighbors.add(new Point(p.x + 1, p.y -1));
        neighbors.add(new Point(p.x, p.y + 1));
        neighbors.add(new Point(p.x, p.y - 1));
        neighbors.add(new Point(p.x - 1, p.y));
        neighbors.add(new Point(p.x - 1, p.y + 1));
        neighbors.add(new Point(p.x - 1, p.y - 1));
        return neighbors;
    }

    private Point mouseToPoint() {
        return view.getViewport().viewportToWorld(mouseX / TILE_WIDTH, mouseY / TILE_HEIGHT);
    }

    public void keyPressed() {
        if (key == CODED) {
            int dx = 0;
            int dy = 0;

            switch (keyCode) {
                case UP -> dy -= 1;
                case DOWN -> dy += 1;
                case LEFT -> dx -= 1;
                case RIGHT -> dx += 1;
            }
            view.shiftView(dx, dy);
        }
    }

    public static Background createDefaultBackground(ImageStore imageStore) {
        return new Background(DEFAULT_IMAGE_NAME, imageStore.getImageList(DEFAULT_IMAGE_NAME));
    }

    public static PImage createImageColored(int width, int height, int color) {
        PImage img = new PImage(width, height, RGB);
        img.loadPixels();
        Arrays.fill(img.pixels, color);
        img.updatePixels();
        return img;
    }

    public void loadImages(String filename) {
        this.imageStore = new ImageStore(createImageColored(TILE_WIDTH, TILE_HEIGHT, DEFAULT_IMAGE_COLOR));
        try {
            Scanner in = new Scanner(new File(filename));
            imageStore.loadImages(in, this);
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    public void loadWorld(String file, ImageStore imageStore) {
        this.world = new WorldModel();
        try {
            Scanner in = new Scanner(new File(file));
            world.load(in, imageStore, createDefaultBackground(imageStore));
        } catch (FileNotFoundException e) {
            Scanner in = new Scanner(file);
            world.load(in, imageStore, createDefaultBackground(imageStore));
        }
    }

    public void parseCommandLine(String[] args) {
        for (String arg : args) {
            switch (arg) {
                case FAST_FLAG -> timeScale = Math.min(FAST_SCALE, timeScale);
                case FASTER_FLAG -> timeScale = Math.min(FASTER_SCALE, timeScale);
                case FASTEST_FLAG -> timeScale = Math.min(FASTEST_SCALE, timeScale);
                default -> loadFile = arg;
            }
        }
    }

    public static void main(String[] args) {
        VirtualWorld.ARGS = args;
        PApplet.main(VirtualWorld.class);
    }

    public static List<String> headlessMain(String[] args, double lifetime){
        VirtualWorld.ARGS = args;

        VirtualWorld virtualWorld = new VirtualWorld();
        virtualWorld.setup();
        virtualWorld.update(lifetime);

        return virtualWorld.world.log();
    }
}
