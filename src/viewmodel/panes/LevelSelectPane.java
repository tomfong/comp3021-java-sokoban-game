package viewmodel.panes;

import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import model.Exceptions.InvalidMapException;
import model.LevelManager;
import model.Map.Cell;
import viewmodel.Config;
import viewmodel.MapRenderer;
import viewmodel.SceneManager;

import java.io.File;
import java.io.IOException;

/**
 * Represents the main menu in the game
 */
public class LevelSelectPane extends BorderPane {
    private VBox leftContainer;
    private Button returnButton;
    private Button playButton;
    private Button chooseMapDirButton;
    private ListView<String> levelsListView;
    private VBox centerContainer;
    private Canvas levelPreview;

    /**
     * Instantiate the member components and connect and style them. Also set the callbacks.
     * Use 20 for VBox spacing
     */
    public LevelSelectPane() {
        //TODO
        this.leftContainer = new VBox();
        this.returnButton = new Button("Return");
        this.playButton = new Button("Play");
        playButton.setDisable(true);
        this.chooseMapDirButton = new Button("Choose map directory");
        this.levelsListView = new ListView<>();
        levelsListView.setFixedCellSize(Config.LIST_CELL_HEIGHT);
        this.centerContainer = new VBox();
        this.levelPreview = new Canvas();
        connectComponents();
        styleComponents();
        setCallbacks();

    }

    /**
     * Connects the components together (think adding them into another, setting their positions, etc). Reference
     * the other classes in the {@link javafx.scene.layout.Pane} package.
     */
    private void connectComponents() {
        //TODO
        leftContainer.getChildren().addAll(returnButton,chooseMapDirButton,levelsListView,playButton);
        centerContainer.getChildren().add(levelPreview);
        this.setLeft(leftContainer);
        this.setCenter(centerContainer);
    }

    /**
     * Apply CSS styling to components. Also sets the {@link LevelSelectPane#playButton} to be disabled.
     */
    private void styleComponents() {
        //TODO
        returnButton.getStyleClass().add("big-button");
        playButton.getStyleClass().add("big-button");
        chooseMapDirButton.getStyleClass().add("big-button");
        leftContainer.getStyleClass().add("big-vbox");
        leftContainer.getStyleClass().add("side-menu");
        centerContainer.getStyleClass().add("big-vbox");
        levelsListView.getStyleClass().add("list-cell");
    }

    /**
     * Set the event handlers for the 3 buttons and listview.
     * <p>
     * Hints:
     * The return button should show the main menu scene
     * The chooseMapDir button should prompt the user to choose the map directory, and load the levels
     * The play button should set the current level based on the current level name (see LevelManager), show
     * the gameplay scene, and start the level timer.
     * The listview, based on which item was clicked, should set the current level (see LevelManager), render the
     * preview (see {@link MapRenderer#render(Canvas, Cell[][])}}, and set the play button to enabled.
     */
    private void setCallbacks() {
        //TODO

        returnButton.setOnAction(e -> {
            SceneManager.getInstance().showMainMenuScene();
        });
        chooseMapDirButton.setOnAction(e -> {
            //levelsListView.setItems(null);
            promptUserForMapDirectory();
            playButton.setDisable(true);
        });
        playButton.setOnAction(e -> {
            try {
                LevelManager.getInstance().setLevel(LevelManager.getInstance().currentLevelNameProperty().getValue());
                LevelManager.getInstance().startLevelTimer();
                SceneManager.getInstance().showGamePlayScene();
            } catch (InvalidMapException e1) {
                e1.printStackTrace();
            }
        });
        levelsListView.getSelectionModel().selectedItemProperty().addListener(e -> {
            try {
                playButton.setDisable(false);
                if (LevelManager.getInstance().getLevelNames().size() > 0) {
                    LevelManager.getInstance().setLevel(levelsListView.getSelectionModel().getSelectedItem());
                    var mapRenderer = new MapRenderer();
                    mapRenderer.render(levelPreview, LevelManager.getInstance().getGameLevel().getMap().getCells());
                }
            } catch (InvalidMapException e1) {
                e1.printStackTrace();
            } catch (NullPointerException e1){
                e1.printStackTrace();
            }
        });
    }


    /**
     * Popup a DirectoryChooser window to ask the user where the map folder is stored.
     * Update the LevelManager's map directory afterwards, and potentially
     * load the levels from disk using LevelManager (if the user didn't cancel out the window)
     */
    private void promptUserForMapDirectory() {
        //TODO
        try {
            var dc = new DirectoryChooser();
            dc.setTitle("Load map directory");
            dc.setInitialDirectory(new File(System.getProperty("user.home"), "./"));
            var f = dc.showDialog(SceneManager.getInstance().getStage());
            if (f == null)
                return;
            LevelManager.getInstance().setMapDirectory(f.getPath());
            LevelManager.getInstance().loadLevelNamesFromDisk();
            levelsListView.setItems(LevelManager.getInstance().getLevelNames());
        } catch (NullPointerException e){
           e.printStackTrace();
        }
    }
}
