import java.util.*;

import processing.core.PApplet;
import processing.core.PImage;

public final class ImageStore {
    private final Map<String, List<PImage>> images;
    private final List<PImage> defaultImages;

    private static final int COLOR_MASK = 0xffffff;

    private static final int TREE_NUM_PROPERTIES = 3;

    private static final int KEYED_IMAGE_MIN = 5;
    private static final int KEYED_RED_IDX = 2;
    private static final int KEYED_GREEN_IDX = 3;
    private static final int KEYED_BLUE_IDX = 4;
    private static final int TREE_ANIMATION_PERIOD = 0;
    private static final int TREE_ACTION_PERIOD = 1;

    public static final double SAPLING_ACTION_ANIMATION_PERIOD = 1.000; // have to be in sync since grows and gains health at same time
    public static final int SAPLING_HEALTH_LIMIT = 5;

    private static final int PROPERTY_KEY = 0;
    private static final int PROPERTY_ID = 1;
    private static final int PROPERTY_COL = 2;
    private static final int PROPERTY_ROW = 3;
    private static final int ENTITY_NUM_PROPERTIES = 4;

    private static final int STUMP_NUM_PROPERTIES = 0;

    public static final String SAPLING_KEY = "sapling";
    private static final int SAPLING_HEALTH = 0;
    private static final int SAPLING_NUM_PROPERTIES = 1;

    private static final String OBSTACLE_KEY = "obstacle";
    private static final int OBSTACLE_ANIMATION_PERIOD = 0;
    private static final int OBSTACLE_NUM_PROPERTIES = 1;

    private static final String DUDE_KEY = "dude";
    private static final int DUDE_ACTION_PERIOD = 0;
    private static final int DUDE_ANIMATION_PERIOD = 1;
    private static final int DUDE_LIMIT = 2;
    private static final int DUDE_NUM_PROPERTIES = 3;

    private static final String HOUSE_KEY = "house";
    private static final int HOUSE_NUM_PROPERTIES = 0;

    private static final String FAIRY_KEY = "fairy";
    private static final int FAIRY_ANIMATION_PERIOD = 0;
    private static final int FAIRY_ACTION_PERIOD = 1;
    private static final int FAIRY_NUM_PROPERTIES = 2;
    private static final String TREE_KEY = "tree";
    private static final String STUMP_KEY = "stump";
    public static final int TREE_HEALTH = 2;



    public ImageStore(PImage defaultImage) {
        this.images = new HashMap<>();
        defaultImages = new LinkedList<>();
        defaultImages.add(defaultImage);
    }

    private void parseBackgroundRow(WorldModel world, String line, int row) {
        String[] cells = line.split(" ");
        if (row < world.getNumRows()) {
            int rows = Math.min(cells.length, world.getNumCols());
            for (int col = 0; col < rows; col++) {
                world.getBackground()[row][col] = new Background(cells[col], getImageList(cells[col]));
            }
        }
    }

    public void parseSaveFile(WorldModel world, Scanner saveFile, Background defaultBackground) {
        String lastHeader = "";
        int headerLine = 0;
        int lineCounter = 0;
        while (saveFile.hasNextLine()) {
            lineCounter++;
            String line = saveFile.nextLine().strip();
            if (line.endsWith(":")) {
                headerLine = lineCounter;
                lastHeader = line;
                switch (line) {
                    case "Backgrounds:" -> world.setBackground(new Background[world.getNumRows()][world.getNumCols()]);
                    case "Entities:" -> {
                        world.setOccupancy(new Entity[world.getNumRows()][world.getNumCols()]);
                        world.setEntities(new HashSet<>());
                    }
                }
            } else {
                switch (lastHeader) {
                    case "Rows:" -> world.setNumRows(Integer.parseInt(line));
                    case "Cols:" -> world.setNumCols(Integer.parseInt(line));
                    case "Backgrounds:" -> this.parseBackgroundRow(world, line, lineCounter - headerLine - 1);
                    case "Entities:" -> this.parseEntity(world, line);
                }
            }
        }
    }

    private void parseEntity(WorldModel world, String line) {
        String[] properties = line.split(" ", this.ENTITY_NUM_PROPERTIES + 1);
        if (properties.length >= this.ENTITY_NUM_PROPERTIES) {
            String key = properties[PROPERTY_KEY];
            String id = properties[this.PROPERTY_ID];
            Point pt = new Point(Integer.parseInt(properties[this.PROPERTY_COL]), Integer.parseInt(properties[this.PROPERTY_ROW]));

            properties = properties.length == this.ENTITY_NUM_PROPERTIES ?
                    new String[0] : properties[this.ENTITY_NUM_PROPERTIES].split(" ");

            switch (key) {
                case OBSTACLE_KEY -> parseObstacle(world, properties, pt, id);
                case DUDE_KEY -> parseDude(world, properties, pt, id);
                case FAIRY_KEY -> parseFairy(world, properties, pt, id);
                case HOUSE_KEY -> parseHouse(world, properties, pt, id);
                case TREE_KEY -> parseTree(world, properties, pt, id);
                case SAPLING_KEY -> parseSapling(world, properties, pt, id);
                case STUMP_KEY -> parseStump(world, properties, pt, id);
                default -> throw new IllegalArgumentException("Entity key is unknown");
            }
        } else {
            throw new IllegalArgumentException("Entity must be formatted as [key] [id] [x] [y] ...");
        }
    }

    private void processImageLine(String line, PApplet screen) {
        String[] attrs = line.split("\\s");
        if (attrs.length >= 2) {
            String key = attrs[0];
            PImage img = screen.loadImage(attrs[1]);
            if (img != null && img.width != -1) {
                List<PImage> imgs = getImages(key);
                imgs.add(img);

                if (attrs.length >= KEYED_IMAGE_MIN) {
                    int r = Integer.parseInt(attrs[KEYED_RED_IDX]);
                    int g = Integer.parseInt(attrs[KEYED_GREEN_IDX]);
                    int b = Integer.parseInt(attrs[KEYED_BLUE_IDX]);
                    setAlpha(img, screen.color(r, g, b), 0);
                }
            }
        }
    }

    public void loadImages(Scanner in, PApplet screen) {
        int lineNumber = 0;
        while (in.hasNextLine()) {
            try {
                processImageLine(in.nextLine(), screen);
            } catch (NumberFormatException e) {
                System.out.printf("Image format error on line %d\n", lineNumber);
            }
            lineNumber++;
        }
    }

    private List<PImage> getImages(String key) {
        return this.images.computeIfAbsent(key, k -> new LinkedList<>());
    }

    public List<PImage> getImageList(String key) {
        return this.images.getOrDefault(key, this.defaultImages);
    }

    /*
  Called with color for which alpha should be set and alpha value.
  setAlpha(img, color(255, 255, 255), 0));
*/
    private static void setAlpha(PImage img, int maskColor, int alpha) {
        int alphaValue = alpha << 24;
        int nonAlpha = maskColor & COLOR_MASK;
        img.format = PApplet.ARGB;
        img.loadPixels();
        for (int i = 0; i < img.pixels.length; i++) {
            if ((img.pixels[i] & COLOR_MASK) == nonAlpha) {
                img.pixels[i] = alphaValue | nonAlpha;
            }
        }
        img.updatePixels();
    }


    private void parseStump(WorldModel world, String[] properties, Point pt, String id) {
        if (properties.length == STUMP_NUM_PROPERTIES) {
            Entity entity = Creates.createStump(id, pt, getImageList(STUMP_KEY));
            world.tryAddEntity(entity);
        } else {
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", STUMP_KEY, STUMP_NUM_PROPERTIES));
        }
    }

    private void parseHouse(WorldModel world, String[] properties, Point pt, String id) {
        if (properties.length == HOUSE_NUM_PROPERTIES) {
            Entity entity = Creates.createHouse(id, pt, getImageList(HOUSE_KEY));
            world.tryAddEntity(entity);
        } else {
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", HOUSE_KEY, HOUSE_NUM_PROPERTIES));
        }
    }

    private void parseTree(WorldModel world, String[] properties, Point pt, String id) {
        if (properties.length == TREE_NUM_PROPERTIES) {
            Entity entity = Creates.createTree(id, pt, Double.parseDouble(properties[TREE_ACTION_PERIOD]), Double.parseDouble(properties[TREE_ANIMATION_PERIOD]), Integer.parseInt(properties[TREE_HEALTH]), getImageList(TREE_KEY));
            world.tryAddEntity(entity);
        } else {
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", TREE_KEY, TREE_NUM_PROPERTIES));
        }
    }

    private void parseObstacle(WorldModel world, String[] properties, Point pt, String id) {
        if (properties.length == OBSTACLE_NUM_PROPERTIES) {
            Entity entity = Creates.createObstacle(id, pt, Double.parseDouble(properties[OBSTACLE_ANIMATION_PERIOD]), getImageList(OBSTACLE_KEY));
            world.tryAddEntity(entity);
        } else {
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", OBSTACLE_KEY, OBSTACLE_NUM_PROPERTIES));
        }
    }

    private void parseDude(WorldModel world, String[] properties, Point pt, String id) {
        if (properties.length == DUDE_NUM_PROPERTIES) {
            Entity entity = Creates.createDudeNotFull(id, pt, Double.parseDouble(properties[DUDE_ACTION_PERIOD]), Double.parseDouble(properties[DUDE_ANIMATION_PERIOD]), Integer.parseInt(properties[DUDE_LIMIT]), getImageList(DUDE_KEY));
            world.tryAddEntity(entity);
        } else {
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", DUDE_KEY, DUDE_NUM_PROPERTIES));
        }
    }

    private void parseFairy(WorldModel world, String[] properties, Point pt, String id) {
        if (properties.length == FAIRY_NUM_PROPERTIES) {
            Entity entity = Creates.createFairy(id, pt, Double.parseDouble(properties[FAIRY_ACTION_PERIOD]), Double.parseDouble(properties[FAIRY_ANIMATION_PERIOD]), getImageList(FAIRY_KEY));
            world.tryAddEntity(entity);
        } else {
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", FAIRY_KEY, FAIRY_NUM_PROPERTIES));
        }
    }

    private void parseSapling(WorldModel world, String[] properties, Point pt, String id) {
        if (properties.length == SAPLING_NUM_PROPERTIES) {
            int health = Integer.parseInt(properties[SAPLING_HEALTH]);
            Entity entity = Creates.createSapling(id, pt, getImageList(SAPLING_KEY), health);
            world.tryAddEntity(entity);
        } else {
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", SAPLING_KEY, SAPLING_NUM_PROPERTIES));
        }
    }


}
