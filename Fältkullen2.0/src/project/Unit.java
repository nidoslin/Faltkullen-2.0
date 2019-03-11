/* An object of this class corresponds to a graphical unit on the map, and the methods herein are what should be
 * called to move that unit around on the zoomablePane (the simulation map).
 *
 * Written by Nils Odin 6:th of March 2019
 */

package project;

import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class Unit {
    // data about the map
    private Pane map;
    PixelReader pixelReader;
    double TOP_EDGE, BOTTOM_EDGE, LEFT_EDGE, RIGHT_EDGE;

    // data about the unit
    private int xPos, yPos;
    private boolean goNorth, goSouth, goWest, goEast, running;
    private AnimationTimer timer;
    private boolean isSelectedUnit = false;

    // default unit image
    private Image unitImage = new Image("http://icons.iconarchive.com/icons/raindropmemory/legendora/64/Hero-icon.png");
    private ImageView unitView = new ImageView(unitImage);


    /**
     * Constructor
     * @param content the pane which the unit should be added to
     */
    public Unit(Pane content, int x, int y){
        // instantiate variables
        map = content;
        setMapEdges();
        xPos = x - (int)(unitImage.getWidth()/2);
        yPos = y - (int)(unitImage.getHeight()/2);

        unitView.setLayoutX(xPos);
        unitView.setLayoutY(yPos);

        pixelReader = map.getBackground().getImages().get(0).getImage().getPixelReader();

        // add unit to map
        map.getChildren().add(unitView);

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (isSelectedUnit) {
                    int dx = 0, dy = 0;

                    if (goNorth) dy -= 1;
                    if (goSouth) dy += 1;
                    if (goEast) dx += 1;
                    if (goWest) dx -= 1;
                    if (running) {
                        dx *= 3;
                        dy *= 3;
                    }

                    moveUnitBy(dx, dy);
                }
            }
        };
        timer.start();
    }

    /**
     * This method is ran a bunch of times per second and sligthly adjusts unit position with the distance given as
     * arguments.
     * @param dx The distance the unit is moved in the x axis
     * @param dy The distance the unit is moved in the y axis
     */
    private void moveUnitBy(int dx, int dy) {
        if (dx == 0 && dy == 0) return;

        // calculates the center position of the unit image
        final double centerX = unitView.getBoundsInLocal().getWidth()  / 2;
        final double centerY = unitView.getBoundsInLocal().getHeight() / 2;

        // calculates the next player position
        double nextX = centerX + unitView.getLayoutX() + dx;
        double nextY = centerY + unitView.getLayoutY() + dy;

        // slows the unit down if unit is in water (ie on a blue pixel)
        if(isInWater(nextX, nextY)) {
            nextX = centerX + unitView.getLayoutX() + (dx/5.0);
            nextY = centerY + unitView.getLayoutY() + (dy/5.0);
        }

        // makes sure the unit cannot leave the map
        if (isInMap(nextX, centerX, nextY, centerY)) {
            unitView.relocate(nextX - centerX, nextY - centerY);
        }
    }

    /**
     * Helper method:
     * Checks if a position corresponds to a blue pixel on the map
     * @param x x position to check
     * @param y y position to check
     * @return if the pixel at given position is "water"
     */
    private boolean isInWater(double x, double y){
        Color color = pixelReader.getColor((int)x, (int)y);
        return color.getBlue() > 0.92;
    }


    /**
     * Helper method:
     * Checks whether a position is in the map
     * @return true if position is no outside of map
     */
    private boolean isInMap(double nextX, double centerX, double nextY, double centerY){
        return (
                nextX - centerX >= LEFT_EDGE &&
                nextX + centerX <= RIGHT_EDGE &&
                nextY - centerY >= TOP_EDGE &&
                nextY + centerY <= BOTTOM_EDGE
        );
    }

    // helper method
    private void setMapEdges(){
        LEFT_EDGE = 0;
        RIGHT_EDGE = map.getBackground().getImages().get(0).getImage().getWidth();
        TOP_EDGE = 0;
        BOTTOM_EDGE = map.getBackground().getImages().get(0).getImage().getHeight();
    }

    public void setIsSelectedUnit(boolean selected){
        if(selected){
            // set an effect so the unit is highlighted
            DropShadow selectedEffect = new DropShadow();
            selectedEffect.setRadius(20);
            unitView.setEffect(selectedEffect); //TODO fixa så effecten inte räknas med i bounds för pixelreader

            // set controls so this unit is the one that can be controlled
            updateControls();

            // make the selected unit appear in front of all other units
            unitView.toFront();
        }
        else{
            // remove effect
            unitView.setEffect(null);
        }
        isSelectedUnit = selected;
    }

    public ImageView getImageview(){
        return unitView;
    }

    public void updateControls(){
        // set up temporary unit movement
        map.getScene().setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case W:  goNorth = true; break;
                    case S:  goSouth = true; break;
                    case A:  goWest  = true; break;
                    case D:  goEast  = true; break;
                    case SHIFT: running = true; break;
                }
            }
        });

        map.getScene().setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case W:  goNorth = false; break;
                    case S:  goSouth = false; break;
                    case A:  goWest  = false; break;
                    case D:  goEast  = false; break;
                    case SHIFT: running = false; break;
                }
            }
        });
    }



}
