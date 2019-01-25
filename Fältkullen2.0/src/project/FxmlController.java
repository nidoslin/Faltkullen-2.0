/* This class is linked to the project file "fältkullen.project" and is used to control and alter various graphical objects
 * by user actions such as dragging or dropping. Objects can be imported into this class and altered programmicly
 * by using their defined fx:id value.
 * NOTE: Only project related to the GUI should go in this class, so army logic or battle calculations should
 * be kept separately.
 *
 * Written by Nils Odin 24 January 2019
 */

package project;

import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.Glow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class FxmlController {

    // Objects from the project file
    @FXML StackPane mapPane;
    @FXML ImageView mainMap;
    @FXML Button blueBtn;
    @FXML Button redBtn;

    // Values related to the zooming and moving of the map
    private double height, width;
    private static double initX, initY;
    private static double offSetX,offSetY, zoomLevel;

    /**
     * initialize() is called right after the constructor, but unlike the constructor, has access to all FXML
     * elements. Therefore, this method is used to set some behaviour of the FXML objects.
     */
    @FXML
    public void initialize(){
        setUpMapControls();
    }

    /**
     * This method opens a window which lets the user pick a new image from their harddrive to use as the map.
     * Only .jpg and .png images are accepted.
     */
    public void openFileAsNewMap(){
        boolean changeSucceded = new IoControl().changeMapImage(mainMap);

        if(changeSucceded) {
            // restore map values
            height = mainMap.getImage().getHeight();
            width = mainMap.getImage().getWidth();
            offSetX = width / 2;
            offSetY = height / 2;
            zoomLevel = 1;

            // restore image zoom and location
            mainMap.setViewport(new Rectangle2D(0, 0, width, height));
        }
    }

    /**
     * Sets up the initial values of the map as well as zoom and drag functionality
     */
    private void setUpMapControls(){
        height = mainMap.getImage().getHeight();
        width = mainMap.getImage().getWidth();
        offSetX = width/2;
        offSetY = height/2;
        zoomLevel = 1;

        makeMapDraggable();
        makeMapZoomable();
    }

    private void zoomMap(double zoom){
        zoomLevel += zoom;
        if(zoomLevel < 0.3) zoomLevel -= zoom;
        mainMap.setViewport(new Rectangle2D(offSetX-((width/zoomLevel)/2), offSetY-((height/zoomLevel)/2), width/zoomLevel, height/zoomLevel));
    }

    private void makeMapDraggable(){
        mainMap.setOnMousePressed(event -> {
            initX = event.getSceneX();
            initY = event.getSceneY();
        });
        mainMap.setOnMouseDragged(event -> {
            mainMap.setViewport(new Rectangle2D(offSetX-((width/zoomLevel)/2), offSetY-((height/zoomLevel)/2), width/zoomLevel, height/zoomLevel));
            offSetX += (initX-event.getSceneX());
            offSetY += (initY-event.getSceneY());
            if (offSetX > width || offSetX < 0) offSetX -= (initX-event.getSceneX());     //makes sure you cant drag the map out of the scene
            if (offSetY > height || offSetY < 0) offSetY -= (initY-event.getSceneY());    //makes sure you cant drag the map out of the scene
            initX = event.getSceneX();
            initY = event.getSceneY();
        });
    }

    private void makeMapZoomable(){
        mapPane.setOnScroll(event -> {
            double zoomFactor = 0.1;
            if(event.getDeltaY() < 0){
                zoomFactor=-0.1;
            }
            zoomMap(zoomFactor);
        });
    }

    public void pressBlueBtn(){
        blueBtn.setEffect(new Glow(0.6));
        redBtn.setEffect(new Glow(0));

    }

    public void pressRedBtn(){
        redBtn.setEffect(new Glow(1));
        redBtn.setEffect(new Bloom(0.3));
        blueBtn.setEffect(new Glow(0));
    }

}
