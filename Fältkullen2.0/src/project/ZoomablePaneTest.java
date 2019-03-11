/* An implementation of a custom javafx pane which can be dragged and zoomed using the mouse. This is what will be
 * used as a map for fältkullen.
 *
 * Written by Nils Odin 9:th of March 2019
 */

package project;

import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

import java.util.ArrayList;

public class ZoomablePaneTest extends Application{
    // TODO Clean up class, make it an object rather then a runnable, add javadocs etc

    // This is the bottom screen slider that controls the zoom level.
    // These values can be modified to change how much can be zoomed.
    Slider slider = new Slider(0.5,3,0);
    Pane content;
    ArrayList<Unit> units;
    ZoomingPane zoomingPane;

    public void start(Stage primaryStage){
        // content is a pane which will be zoomable and movable, so anything shown on screen is put in this pane
        content = new Pane();

        // set the image to be used as map
        Image background = new Image("https://i.imgur.com/H6iCEoD.jpg", 1920, 1800, true, false);
        content.setBackground(new Background(new BackgroundImage(background, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, null, null)));

        // zoomingPane is the "black" square in the program and it gives functionality to zoom its content
        zoomingPane = new ZoomingPane(content);
        zoomingPane.zoomFactorProperty().bind(slider.valueProperty());

        Button addUnitBtn = new Button("Add unit!");
        addUnitBtn.setOnAction(event -> {changeOnClickToAddUnit();});
        Text instructions = new Text("Click a unit to select it. Selected units can be moved with WASD. \n New units can be added using button, however, the program currently doesnt handle it well if they are placed outside of the map! \n Zooming can be done using slider on bottom or with mousewheel!");
        primaryStage.setScene(new Scene(new BorderPane(zoomingPane, instructions, addUnitBtn, slider, null)));

        // this method adds new units to the given position
        units = new ArrayList<>();
        addUnit( 80, 80);
        addUnit(400, 200);
        addUnit(700, 150);

        primaryStage.setWidth(1300);
        primaryStage.setHeight(800);
        primaryStage.show();
    }

    private class ZoomingPane extends Region {
        Pane content;
        private DoubleProperty zoomFactor = new SimpleDoubleProperty(0);

        double oldMouseX, oldMouseY;

        private ZoomingPane(Pane map) {
            content = map;

            getChildren().add(content);
            Scale scale = new Scale(1, 1);
            content.getTransforms().add(scale);
            makeMapDraggable();
            makeZoomable();
            clipChildren();

            this.setMaxHeight(700);
            this.setMaxWidth(1200);
            this.setBackground(new Background(new BackgroundFill(Color.BLACK, new CornerRadii(5), Insets.EMPTY)));
            content.setTranslateX(this.getMaxWidth()/2 - content.getBackground().getImages().get(0).getImage().getWidth()/4);
            content.setTranslateY(20);


            zoomFactor.addListener(new ChangeListener<Number>() {
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    scale.setX(newValue.doubleValue());
                    scale.setY(newValue.doubleValue());
                    requestLayout();
                }
            });
        }

        /**
         * Method that is neccesary when extending Region. Defines how children will be added in the Zoomingpane.
         */
        protected void layoutChildren() {
            Pos pos = Pos.BOTTOM_RIGHT;
            double width = getWidth();
            double height = getHeight();
            double top = getInsets().getTop();
            double right = getInsets().getRight();
            double left = getInsets().getLeft();
            double bottom = getInsets().getBottom();
            double contentWidth = 9999;  //TODO ÄNDRA DENNA FÖR ATT UNDVIKA CROPPING
            double contentHeight = 9999; //TODO DEN HÄR OXÅ!!
            layoutInArea(content, left, top,
                    contentWidth, contentHeight,
                    0, null,
                    pos.getHpos(),
                    pos.getVpos());
        }


        public final Double getZoomFactor() {
            return zoomFactor.get();
        }
        public final void setZoomFactor(Double zoomFactor) {
            this.zoomFactor.set(zoomFactor);
        }
        public final DoubleProperty zoomFactorProperty() {
            return zoomFactor;
        }


        /**
         * Makes the node draggable, meaning you can click it and drag it around. The logic is that an initial mouse press
         * position is saved, and then whenever the mouse is moved the Node moves from its previous position the same
         * number of pixels as the mouse moved, using the "setTranslate" method.
         *
         */
        private void makeMapDraggable(){
            // Get initial mouse position
            this.setOnMousePressed(event -> {
                oldMouseX = event.getSceneX();
                oldMouseY = event.getSceneY();
            });
            // Get where the mouse was dragged, move the Node, then update the positions
            this.setOnMouseDragged(event -> {
                double newMouseX = event.getSceneX();
                double newMouseY = event.getSceneY();
                content.setTranslateX(content.getTranslateX()+(newMouseX-oldMouseX));
                content.setTranslateY(content.getTranslateY()+(newMouseY-oldMouseY));
                oldMouseX = newMouseX;
                oldMouseY = newMouseY;
            });
        }

        /**
         * Adds a zoom effect when the mouse scroll wheel is used.
         */
        private void makeZoomable(){
            this.setOnScroll(event -> {

                double zoomChange = 0.01;
                // if zooming out
                if(event.getDeltaY() < 0){
                    zoomChange=-0.01;
                }
                slider.setValue(slider.getValue()+zoomChange);
            });
        }

        /**
         * Adds clipping, ie restrictions so that all maps etc that are outside of an area are not shown. This means
         * that when the map is zoomed or moved, the content of that map will still be within set boundries graphicly.
         */
        private void clipChildren(){
            final Rectangle outputClip = new Rectangle();
            outputClip.setArcWidth(50);
            outputClip.setArcHeight(50);
            this.setClip(outputClip);

            this.layoutBoundsProperty().addListener((ov, oldValue, newValue) -> {
                outputClip.setWidth(newValue.getWidth());
                outputClip.setHeight(newValue.getHeight());
            });
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void addUnit(int X, int Y){
        //create new unit at given position
        Unit unit = new Unit(content, X, Y);
        units.add(unit);
        // make sure all other units are deselected
        updateUnitsOnClick();

        // make it the selected one
        unit.setIsSelectedUnit(true);
    }

    /**
     * Sets up an OnClick so the user can press anywhere on the map to add a unit there.
     */
    private void changeOnClickToAddUnit(){
        content.setCursor(Cursor.CROSSHAIR);
        content.setOnMousePressed(event -> {

            addUnit((int) (event.getX()), (int) event.getY());

            content.setOnMousePressed(event1 -> {});
            content.setCursor(Cursor.DEFAULT);
        });
    }

    /**
     * For every created unit, makes it possible to click that unit to select it and move it and only it around.
     * On clicking a unit, all other units become deselected.
     */
    private void updateUnitsOnClick(){
        for (Unit unit:units){
            unit.setIsSelectedUnit(false);
            unit.getImageview().setOnMousePressed(event -> {
                // makes every other unit deselected and unable to move
                for (Unit unitTemp:units){
                    unitTemp.setIsSelectedUnit(false);
                }

                // select the clicked unit
                unit.setIsSelectedUnit(true);
            });
        }
    }

}