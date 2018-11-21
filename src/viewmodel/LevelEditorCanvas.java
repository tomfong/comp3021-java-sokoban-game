package viewmodel;

import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

import static viewmodel.Config.LEVEL_EDITOR_TILE_SIZE;

/**
 * Extends the Canvas class to provide functionality for generating maps and saving them.
 */
public class LevelEditorCanvas extends Canvas {
    private int rows;
    private int cols;

    private Brush[][] map;

    //Stores the last location the player was standing at
    private int oldPlayerRow = -1;
    private int oldPlayerCol = -1;

    /**
     * Call the super constructor. Also resets the map to all {@link Brush#TILE}.
     * Hint: each square cell in the grid has size {@link Config#LEVEL_EDITOR_TILE_SIZE}
     *
     * @param rows The number of rows in the map
     * @param cols The number of tiles in the map
     */
    public LevelEditorCanvas(int rows, int cols) {
        //TODO
        super(rows,cols);
        this.rows = rows;
        this.cols = cols;
        this.map = new Brush[rows][cols];
        for (int r=0; r<rows; r++) {
            for (int c = 0; c < cols; c++) {
                this.map[r][c] = Brush.TILE;
            }
        }
        renderCanvas();
    }

    /**
     * Setter function. Also resets the map
     *
     * @param rows The number of rows in the map
     * @param cols The numbers of cols in the map
     */
    public void changeSize(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        resetMap(rows, cols);
    }

    /**
     * Assigns {@link LevelEditorCanvas#map} to a new instance, sets all the values to {@link Brush#TILE}, and
     * renders the canvas with the updated map.
     *
     * @param rows The number of rows in the map
     * @param cols The numbers of cols in the map
     */
    private void resetMap(int rows, int cols) {
        //TODO
        map = new Brush[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++){
                map[r][c] = Brush.TILE;
            }
        }
        renderCanvas();
    }

    /**
     * Render the map using {@link MapRenderer}
     */
    private void renderCanvas() {
        //TODO
        MapRenderer.render(this,map);
    }

    /**
     * Sets the applicable {@link LevelEditorCanvas#map} cell to the brush the user currently has selected.
     * In other words, when the user clicks somewhere on the canvas, we translate that into updating one of the
     * tiles in our map.
     * <p>
     * There can only be 1 player on the map. As such, if the user clicks a new position using the player brush,
     * the old location must have the player removed, leaving behind either a tile or a destination underneath,
     * whichever the player was originally standing on.
     * <p>
     * Hint:
     * Don't forget to update the player ({@link Brush#PLAYER_ON_DEST} or {@link Brush#PLAYER_ON_TILE})'s last
     * known position.
     * <p>
     * Finally, render the canvas.
     *
     * @param brush The currently selected brush
     * @param x     Mouse click coordinate x
     * @param y     Mouse click coordinate y
     */
    public void setTile(Brush brush, double x, double y) {
        //TODO
        int c = (int) (x/Config.LEVEL_EDITOR_TILE_SIZE);
        int r = (int) (y/Config.LEVEL_EDITOR_TILE_SIZE);
        if (brush == Brush.PLAYER_ON_DEST || brush == Brush.PLAYER_ON_TILE){
            if (oldPlayerCol == -1 && oldPlayerRow == -1){
                oldPlayerCol = c;
                oldPlayerRow = r;
            } else {
                if (map[oldPlayerRow][oldPlayerCol] == Brush.PLAYER_ON_DEST){
                    map[oldPlayerRow][oldPlayerCol] = Brush.DEST;
                    oldPlayerCol = c;
                    oldPlayerRow = r;
                }
                else if (map[oldPlayerRow][oldPlayerCol] == Brush.PLAYER_ON_TILE){
                    map[oldPlayerRow][oldPlayerCol] = Brush.TILE;
                    oldPlayerCol = c;
                    oldPlayerRow = r;
                }
            }
        }
        map[r][c] = brush;
        renderCanvas();
    }

    /**
     * Saves the current map to file. Should prompt the user for the save directory before saving the file to
     * the selected location.
     */
    public void saveToFile() {
        //TODO
        if (isInvalidMap())
            return;
        else {
            File fileDir = getTargetSaveDirectory();
            if (fileDir == null)
                return;
            File outputFile = new File(fileDir.getPath());
            PrintWriter writer = null;
            try {
                writer = new PrintWriter(outputFile);
                writer.println(this.rows);
                writer.println(this.cols);
                for (int r = 0; r < rows; r++) {
                    for (int c = 0; c < cols; c++) {
                        writer.print(map[r][c].getRep());
                    }
                    writer.println();
                }
            } catch (FileNotFoundException e){
                e.printStackTrace();
            } finally {
                if(writer != null)
                    writer.close();
            }
        }
    }

    /**
     * Hint: {@link FileChooser} is needed. Also add an extension filter with the following information:
     * description: "Normal text file"
     * extension: "*.txt"
     *
     * @return The directory the user chose to save the map in.
     */
    private File getTargetSaveDirectory() {
        //TODO
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Map");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Normal text file", "*.txt"));
        File file = fileChooser.showSaveDialog(SceneManager.getInstance().getStage());
        return file;//NOTE: You may also need to modify this line
    }

    /**
     * Check if the map is valid for saving.
     * Conditions to check:
     * 1. Map must at least have 3 rows and 3 cols
     * 2. Must have a player
     * 3. Balanced number of crates and destinations
     * 4. At least 1 crate and destination
     * <p>
     * Show an Alert if there's an error.
     *
     * @return If the map is invalid
     */
    private boolean isInvalidMap() {
        //TODO
        int numPlayer = 0;
        int numCrate = 0;
        int numDest = 0;
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Error");
        alert.setHeaderText("Could not save map!");
        for (int r=0; r<this.rows;r++) {
            for (int c = 0; c < this.cols; c++) {
                if (map[r][c] == Brush.PLAYER_ON_DEST){
                    numPlayer++;
                    numDest++;
                } else if (map[r][c] == Brush.PLAYER_ON_TILE){
                    numPlayer++;
                } else if (map[r][c] == Brush.CRATE_ON_DEST){
                    numCrate++;
                    numDest++;
                } else if (map[r][c] == Brush.CRATE_ON_TILE){
                    numCrate++;
                } else if (map[r][c] == Brush.DEST){
                    numDest++;
                }
            }
        }
        if (numCrate == 0 || numDest == 0){
            alert.setContentText("Please create at least 1 crate and destination.");
            alert.showAndWait();
            return true;
        } else if (numCrate != numDest){
            alert.setContentText("Imbalanced number of crates and destinations.");
            alert.showAndWait();
            return true;
        } else if (numPlayer < 1){
            alert.setContentText("Please add a player.");
            alert.showAndWait();
            return true;
        } else if (rows < 3 || cols < 3){
            alert.setContentText("Minimum size is 3 rows and 3 cols.");
            alert.showAndWait();
            return true;
        }
        return false;//NOTE: You may also need to modify this line
    }

    /**
     * Represents the currently selected brush when the user is making a new map
     */
    public enum Brush {
        TILE("Tile", '.'),
        PLAYER_ON_TILE("Player on Tile", '@'),
        PLAYER_ON_DEST("Player on Destination", '&'),
        CRATE_ON_TILE("Crate on Tile", 'c'),
        CRATE_ON_DEST("Crate on Destination", '$'),
        WALL("Wall", '#'),
        DEST("Destination", 'C');

        private final String text;
        private final char rep;

        Brush(String text, char rep) {
            this.text = text;
            this.rep = rep;
        }

        public static Brush fromChar(char c) {
            for (Brush b : Brush.values()) {
                if (b.getRep() == c) {
                    return b;
                }
            }
            return null;
        }

        @Override
        public String toString() {
            return text;
        }

        char getRep() {
            return rep;
        }
    }


}

