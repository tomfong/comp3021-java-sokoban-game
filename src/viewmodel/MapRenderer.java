package viewmodel;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import model.Map.Cell;
import model.Map.Map;
import model.Map.Occupant.Crate;
import model.Map.Occupant.Player;
import model.Map.Occupiable.DestTile;
import model.Map.Occupiable.Occupiable;

import java.net.URISyntaxException;

import static viewmodel.Config.LEVEL_EDITOR_TILE_SIZE;

/**
 * Renders maps onto canvases
 */
public class MapRenderer {
    private static Image wall = null;
    private static Image crateOnTile = null;
    private static Image crateOnDest = null;

    private static Image playerOnTile = null;
    private static Image playerOnDest = null;

    private static Image dest = null;
    private static Image tile = null;

    static {
        try {
            wall = new Image(MapRenderer.class.getResource("/assets/images/wall.png").toURI().toString());
            crateOnTile = new Image(MapRenderer.class.getResource("/assets/images/crateOnTile.png").toURI().toString());
            crateOnDest = new Image(MapRenderer.class.getResource("/assets/images/crateOnDest.png").toURI().toString());
            playerOnTile = new Image(MapRenderer.class.getResource("/assets/images/playerOnTile.png").toURI().toString());
            playerOnDest = new Image(MapRenderer.class.getResource("/assets/images/playerOnDest.png").toURI().toString());
            dest = new Image(MapRenderer.class.getResource("/assets/images/dest.png").toURI().toString());
            tile = new Image(MapRenderer.class.getResource("/assets/images/tile.png").toURI().toString());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * Render the map onto the canvas. This method can be used in Level Editor
     * <p>
     * Hint: set the canvas height and width as a multiple of the rows and cols
     *
     * @param canvas The canvas to be rendered onto
     * @param map    The map holding the current state of the game
     */
    static void render(Canvas canvas, LevelEditorCanvas.Brush[][] map) {
        //TODO
        Image cellImage = null;
        var w = map[0].length*Config.LEVEL_EDITOR_TILE_SIZE;
        var h = map.length*Config.LEVEL_EDITOR_TILE_SIZE;
        canvas.setWidth(w);
        canvas.setHeight(h);
        var gc = canvas.getGraphicsContext2D();
        for (int r=0; r<map.length; r++){
            for (int c=0; c<map[0].length; c++){
                if (map[r][c] == LevelEditorCanvas.Brush.WALL){
                    cellImage = wall;
                } else if (map[r][c] == LevelEditorCanvas.Brush.CRATE_ON_TILE){
                    cellImage = crateOnTile;
                } else if (map[r][c] == LevelEditorCanvas.Brush.CRATE_ON_DEST){
                    cellImage = crateOnDest;
                } else if (map[r][c] == LevelEditorCanvas.Brush.PLAYER_ON_TILE){
                    cellImage = playerOnTile;
                } else if (map[r][c] == LevelEditorCanvas.Brush.PLAYER_ON_DEST){
                    cellImage = playerOnDest;
                } else if (map[r][c] == LevelEditorCanvas.Brush.DEST){
                    cellImage = dest;
                } else if (map[r][c] == LevelEditorCanvas.Brush.TILE){
                    cellImage = tile;
                }
                gc.drawImage(cellImage,c*Config.LEVEL_EDITOR_TILE_SIZE,r*Config.LEVEL_EDITOR_TILE_SIZE);
            }
        }
    }

    /**
     * Render the map onto the canvas. This method can be used in GamePlayPane and LevelSelectPane
     * <p>
     * Hint: set the canvas height and width as a multiple of the rows and cols
     *
     * @param canvas The canvas to be rendered onto
     * @param map    The map holding the current state of the game
     */
    public static void render(Canvas canvas, Cell[][] map) {
        //TODO
        Image cellImage = null;
        var w = map[0].length*Config.LEVEL_EDITOR_TILE_SIZE;
        var h = map.length*Config.LEVEL_EDITOR_TILE_SIZE;
        canvas.setWidth(w);
        canvas.setHeight(h);
        var gc = canvas.getGraphicsContext2D();
        for (int r=0; r<map.length; r++){
            for (int c=0; c<map[0].length; c++){
                if (map[r][c] instanceof Occupiable) {
                    Occupiable ocp = (Occupiable) map[r][c];
                    if (ocp.getOccupant().isPresent()){
                        if (ocp.getOccupant().get() instanceof Player){
                            if (map[r][c] instanceof DestTile){
                                cellImage = playerOnDest;
                            } else {
                                cellImage = playerOnTile;
                            }
                        } else {
                            if (map[r][c] instanceof DestTile){
                                cellImage = crateOnDest;
                            } else {
                                cellImage = crateOnTile;
                            }
                        }
                    } else {
                        if (map[r][c] instanceof DestTile){
                            cellImage = dest;
                        } else {
                            cellImage = tile;
                        }
                    }
                } else {
                    cellImage = wall;
                }
                gc.drawImage(cellImage,c*Config.LEVEL_EDITOR_TILE_SIZE,r*Config.LEVEL_EDITOR_TILE_SIZE);
            }
        }
    }
}
