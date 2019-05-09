/* An object of this class corresponds to a graphical unit on the map, and the methods herein are what should be
 * called to move that unit around on the zoomablePane (the simulation map).
 *
 * Written by Nils Odin 6:th of March 2019
 */

package project;

import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import static java.lang.Float.floatToIntBits;
import static java.lang.Float.intBitsToFloat;

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
    private boolean hasOrder = false;
    public enum typeOfUnit{
        friend,
        enemy
    }
    typeOfUnit type;

    private Image unitImage;
    private ImageView unitView;


    /**
     * Constructor
     * @param content the pane which the unit should be added to
     */
    public Unit(Pane content, int x, int y, typeOfUnit type){
        this.type = type;

        // determine what kind of unit this is using the type argument
        switch(type) {
            case enemy:
                unitImage = new Image("https://upload.wikimedia.org/wikipedia/en/thumb/c/ce/Goomba.PNG/220px-Goomba.PNG", 60, 60, true, false);
                break;

            case friend:
                unitImage = new Image("http://icons.iconarchive.com/icons/raindropmemory/legendora/64/Hero-icon.png", 60, 60, true, false);
                break;

                default:
                    unitImage = new Image("http://icons.iconarchive.com/icons/raindropmemory/legendora/64/Hero-icon.png", 60, 60, true, false);
        }

        // instantiate variables
        map = content;
        setMapEdges();
        xPos = x - (int)(unitImage.getWidth()/2);
        yPos = y - (int)(unitImage.getHeight()/2);

        unitView = new ImageView(unitImage);
        unitView.setLayoutX(xPos);
        unitView.setLayoutY(yPos);
        unitView.setPickOnBounds(false);
        pixelReader = map.getBackground().getImages().get(0).getImage().getPixelReader();

        // add unit to map
        map.getChildren().add(unitView);

        // timer that constantly checks for WASD movement
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

    public void removeUnit(){
        map.getChildren().remove(unitView);
    }

    /**
     * This method is ran a bunch of times per second and sligthly adjusts unit position with the distance given as
     * arguments.
     * @param dx The distance the unit is moved in the x axis
     * @param dy The distance the unit is moved in the y axis
     */
    private void moveUnitBy(double dx, double dy) {
        if (dx == 0 && dy == 0) return;

        // calculates the center position of the unit image
        final double centerX = unitImage.getWidth()  / 2;
        final double centerY = unitImage.getHeight() / 2;

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
    public boolean isInMap(double nextX, double centerX, double nextY, double centerY){
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
            updateWASDControls();

            // make the selected unit appear in front of all other units
            unitView.toFront();
        }
        else{
            // remove effect
            unitView.setEffect(null);
        }
        isSelectedUnit = selected;
    }

    public boolean getIsSelectedUnit(){
        return isSelectedUnit;
    }

    public ImageView getImageview(){
        return unitView;
    }

    public void updateWASDControls(){
        // set up temporary unit movement

        map.setOnKeyPressed(new EventHandler<KeyEvent>() {
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

        map.setOnKeyReleased(new EventHandler<KeyEvent>() {
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

    /**
     * Used to move this unit from where it is to the given x and y (a point on the map).
     * This method makes the unit start moving and keep doing so until it reaches its destination or hits a wall or
     * enemy. This method also checks and handles moving slower through water correctly.
     */
    public void orderMoveToLocation(double x, double y){
        // make sure the requested order is inside the map
        if(!isInMap(x, unitImage.getWidth()/2, y, unitImage.getHeight()/2)){
            return;
        }

        if(hasOrder) return;
        hasOrder = true;

        // get the positions to move between
        Point2D from = new Point2D(unitView.getLayoutX(), unitView.getLayoutY());
        Point2D to = new Point2D(x-unitImage.getWidth()/2, y-unitImage.getHeight()/2);

        // calculate some neccesary values
        double distance = from.distance(to);
        double movemenetSpeed = 1;  // THIS CAN BE CHANGED TO ALTER MOVEMENT SPEED
        Point2D movementAngle = new Point2D(to.getX()-from.getX(), to.getY()-from.getY()).multiply(movemenetSpeed/distance);

        // make an atomic value that can be changed inside of an animationtimer
        class AtomicFloat extends Number {

            private AtomicInteger bits;

            public AtomicFloat() {
                this(0f);
            }

            public AtomicFloat(float initialValue) {
                bits = new AtomicInteger(floatToIntBits(initialValue));
            }

            public final boolean compareAndSet(float expect, float update) {
                return bits.compareAndSet(floatToIntBits(expect),
                        floatToIntBits(update));
            }

            public final void set(float newValue) {
                bits.set(floatToIntBits(newValue));
            }

            public final float get() {
                return intBitsToFloat(bits.get());
            }

            public float floatValue() {
                return get();
            }

            public final float getAndSet(float newValue) {
                return intBitsToFloat(bits.getAndSet(floatToIntBits(newValue)));
            }

            public final boolean weakCompareAndSet(float expect, float update) {
                return bits.weakCompareAndSet(floatToIntBits(expect),
                        floatToIntBits(update));
            }

            public double doubleValue() { return (double) floatValue(); }
            public int intValue()       { return (int) get();           }
            public long longValue()     { return (long) get();          }

        }
        AtomicFloat movedDistance = new AtomicFloat(0.0f);

        // create a line which shows where the unit is heading
        Line travelLine = new Line(
                from.getX()+unitImage.getWidth()/2,
                from.getY()+unitImage.getHeight()/2,
                to.getX()+unitImage.getWidth()/2,
                to.getY()+unitImage.getHeight()/2
        );
        map.getChildren().add(travelLine);


        // run code to move the distance of movementAngle until the destination is reached or an enemy is hit.
        AnimationTimer timer2 = new AnimationTimer() {
            @Override
            public void handle(long now) {
                Point2D before = new Point2D(unitView.getLayoutX(), unitView.getLayoutY());
                moveUnitBy(movementAngle.getX(), movementAngle.getY());
                float distanceTravelled = (float) before.distance(unitView.getLayoutX(), unitView.getLayoutY());
                movedDistance.set(movedDistance.floatValue()+distanceTravelled);

                // update travel line
                travelLine.setStartX(unitView.getLayoutX()+unitImage.getWidth()/2);
                travelLine.setStartY(unitView.getLayoutY()+unitImage.getHeight()/2);

                // moving ends when the accumilated distance travelled is greater then the distance between the original
                // positions
                if(movedDistance.doubleValue() > distance){
                    travelLine.setOpacity(0);
                    hasOrder = false;
                    this.stop();
                }
                if(checkIfUnitCollidedWithEnemy()){
                    //TODO code for deciding what happens when a unit walks into an enemy unit goes here
                    System.out.println("A unit hit an enemy. A fight starts!");
                    travelLine.setOpacity(0);
                    this.stop();
                }
            }
        };
        timer2.start();
    }

    private boolean checkIfUnitCollidedWithEnemy(){
        for (Unit unit:ZoomablePaneTest.units) {
            unit.unitView.setPickOnBounds(false);
            if(!unit.equals(this)) {
                if (this.unitView.getBoundsInParent().intersects(unit.getImageview().getBoundsInParent())) {
                    if (!this.type.equals(unit.type)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }



}
