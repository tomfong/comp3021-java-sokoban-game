package viewmodel.panes;

import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.Exceptions.InvalidMapException;
import model.LevelManager;
import model.Map.Map;
import viewmodel.AudioManager;
import viewmodel.MapRenderer;
import viewmodel.SceneManager;
import viewmodel.customNodes.GameplayInfoPane;

import java.util.Optional;

/**
 * Represents the gameplay pane in the game
 */
public class GameplayPane extends BorderPane {

    private final GameplayInfoPane info;
    private VBox canvasContainer;
    private Canvas gamePlayCanvas;
    private HBox buttonBar;
    private Button restartButton;
    private Button quitToMenuButton;

    /**
     * Instantiate the member components and connect and style them. Also set the callbacks.
     * Use 20 for the VBox spacing
     */
    public GameplayPane() {
        //TODO
        this.info = new GameplayInfoPane(LevelManager.getInstance().currentLevelNameProperty(),
                LevelManager.getInstance().curGameLevelExistedDurationProperty(),
                LevelManager.getInstance().getGameLevel().numPushesProperty(),
                LevelManager.getInstance().curGameLevelNumRestartsProperty());
        this.canvasContainer = new VBox();
        this.gamePlayCanvas = new Canvas();
        this.buttonBar = new HBox();
        this.restartButton = new Button("Restart");
        this.quitToMenuButton = new Button("Quit to menu");
        connectComponents();
        styleComponents();
        setCallbacks();
        renderCanvas();
    }

    /**
     * Connects the components together (think adding them into another, setting their positions, etc).
     */
    private void connectComponents() {
        //TODO
        canvasContainer.getChildren().add(gamePlayCanvas);
        buttonBar.getChildren().addAll(info,restartButton,quitToMenuButton);
        this.setCenter(canvasContainer);
        this.setBottom(buttonBar);
    }

    /**
     * Apply CSS styling to components.
     */
    private void styleComponents() {
        //TODO
        canvasContainer.getStyleClass().add("big-vbox");
        buttonBar.getStyleClass().add("big-hbox");
        buttonBar.getStyleClass().add("bottom-menu");
        restartButton.getStyleClass().add("big-button");
        quitToMenuButton.getStyleClass().add("big-button");
    }

    /**
     * Set the event handlers for the 2 buttons.
     * <p>
     * Also listens for key presses (w, a, s, d), which move the character.
     * <p>
     * Hint: {@link GameplayPane#setOnKeyPressed(EventHandler)}  is needed.
     * You will need to make the move, rerender the canvas, play the sound (if the move was made), and detect
     * for win and deadlock conditions. If win, play the win sound, and do the appropriate action regarding the timers
     * and generating the popups. If deadlock, play the deadlock sound, and do the appropriate action regarding the timers
     * and generating the popups.
     */
    private void setCallbacks() {
        //TODO
        restartButton.setOnAction(e->{doRestartAction();});
        quitToMenuButton.setOnAction(e->{doQuitToMenuAction();});
        this.setOnKeyPressed(e -> {
            if (e.getCode()== KeyCode.W) {
                if (LevelManager.getInstance().getGameLevel().makeMove('w')) {
                    renderCanvas();
                    AudioManager.getInstance().playMoveSound();
                }
            } else if (e.getCode()== KeyCode.A) {
                if (LevelManager.getInstance().getGameLevel().makeMove('a')){
                    renderCanvas();
                    AudioManager.getInstance().playMoveSound();
                }
            } else if (e.getCode()== KeyCode.S) {
                if (LevelManager.getInstance().getGameLevel().makeMove('s')){
                    renderCanvas();
                    AudioManager.getInstance().playMoveSound();
                }
            } else if (e.getCode()== KeyCode.D) {
                if (LevelManager.getInstance().getGameLevel().makeMove('d')){
                    renderCanvas();
                    AudioManager.getInstance().playMoveSound();
                }
            }
            if (LevelManager.getInstance().getGameLevel().isWin()){
                AudioManager.getInstance().playWinSound();
                LevelManager.getInstance().resetLevelTimer();
                createLevelClearPopup();
            } else if (LevelManager.getInstance().getGameLevel().isDeadlocked()){
                AudioManager.getInstance().playDeadlockSound();
                LevelManager.getInstance().resetLevelTimer();
                createDeadlockedPopup();
            }
        });
    }

    /**
     * Called when the tries to quit to menu. Show a popup (see the documentation). If confirmed,
     * do the appropriate action regarding the level timer, level number of restarts, and go to the
     * main menu scene.
     */
    private void doQuitToMenuAction() {
        //TODO
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm");
        alert.setHeaderText("Return to menu?");
        alert.setContentText("Game progress will be lost.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            LevelManager.getInstance().resetLevelTimer();
            LevelManager.getInstance().resetNumRestarts();
            SceneManager.getInstance().showMainMenuScene();
            alert.close();
        } else {
            alert.close();
        }
    }

    /**
     * Called when the user encounters deadlock. Show a popup (see the documentation).
     * If the user chooses to restart the level, call {@link GameplayPane#doRestartAction()}. Otherwise if they
     * quit to menu, switch to the level select scene, and do the appropriate action regarding
     * the number of restarts.
     */
    private void createDeadlockedPopup() {
        //TODO
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm");
        alert.setHeaderText("Level deadlocked!");
        ButtonType restartButton = new ButtonType("Restart", ButtonBar.ButtonData.OK_DONE);
        ButtonType returnButton = new ButtonType("Return", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(restartButton,returnButton);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == restartButton){
            doRestartAction();
            alert.close();
        } else {
            LevelManager.getInstance().resetNumRestarts();
            SceneManager.getInstance().showLevelSelectMenuScene();
            alert.close();
        }
    }

    /**
     * Called when the user clears the level successfully. Show a popup (see the documentation).
     * If the user chooses to go to the next level, set the new level, rerender, and do the appropriate action
     * regarding the timers and num restarts. If they choose to return, show the level select menu, and do
     * the appropriate action regarding the number of level restarts.
     * <p>
     * Hint:
     * Take care of the edge case for when the user clears the last level. In this case, there shouldn't
     * be an option to go to the next level.
     */
    private void createLevelClearPopup() {
        //TODO
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm");
        alert.setHeaderText("Level cleared!");
        ButtonType returnButton = new ButtonType("Return", ButtonBar.ButtonData.CANCEL_CLOSE);
        if (LevelManager.getInstance().getNextLevelName() == null){
            alert.getButtonTypes().setAll(returnButton);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == returnButton){
                LevelManager.getInstance().resetNumRestarts();
                SceneManager.getInstance().showLevelSelectMenuScene();
                alert.close();
            }
        } else {
            ButtonType nextLevelButton = new ButtonType("Next level", ButtonBar.ButtonData.OK_DONE);
            alert.getButtonTypes().setAll(nextLevelButton, returnButton);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == nextLevelButton) {
                try {
                    LevelManager.getInstance().setLevel(LevelManager.getInstance().getNextLevelName());
                    renderCanvas();
                    LevelManager.getInstance().startLevelTimer();
                    alert.close();
                } catch (InvalidMapException e1) {
                    e1.printStackTrace();
                }
            } else {
                LevelManager.getInstance().resetNumRestarts();
                SceneManager.getInstance().showLevelSelectMenuScene();
                alert.close();
            }
        }
    }

    /**
     * Set the current level to the current level name, rerender the canvas, reset and start the timer, and
     * increment the number of restarts
     */
    private void doRestartAction() {
        //TODO
        try {
            LevelManager.getInstance().incrementNumRestarts();
            var numRestart = LevelManager.getInstance().curGameLevelNumRestartsProperty().get();
            LevelManager.getInstance().setLevel(LevelManager.getInstance().currentLevelNameProperty().getValue());
            LevelManager.getInstance().curGameLevelNumRestartsProperty().set(numRestart);
            renderCanvas();
            LevelManager.getInstance().startLevelTimer();
        } catch (InvalidMapException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * Render the canvas with updated data
     * <p>
     * Hint: {@link MapRenderer}
     */
    private void renderCanvas() {
        //TODO
        var mapRenderer = new MapRenderer();
        mapRenderer.render(gamePlayCanvas,LevelManager.getInstance().getGameLevel().getMap().getCells());
    }
}
