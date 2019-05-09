/* This class is linked to the project file "fältkullen.project" and is used to control and alter various graphical objects
 * by user actions such as dragging or dropping. Objects can be imported into this class and altered programmicly
 * by using their defined fx:id value.
 * NOTE: Only project related to the GUI should go in this class, so army logic or battle calculations should
 * be kept separately.
 *
 * Written by Nils Odin 24 January 2019
 */

package project;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.Glow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class FxmlController {

    // Objects from the project file
    @FXML BorderPane testPane;
    @FXML Slider zoomSlider;
    @FXML Button blueBtn;
    @FXML Button redBtn;
    @FXML Button addUnitBtn;
    @FXML Button moveUnitBtn;

    // Objects related to the tabmenu
    @FXML AnchorPane tabmenu;
    @FXML AnchorPane tab1, tab2, tab3, tab4, tab5, tab6, tab7, tab8;
    private AnchorPane[] tabs;
    private int selectedTab = 0;
    private TranslateTransition moveDown, moveUp;
    public static boolean isFriendlySelected = true;

    // Panes, ie the screens shown
    @FXML AnchorPane pane1, pane2, pane3, pane4, pane5, pane6, pane7, pane8;
    AnchorPane[] panes;

    // zoomable pane related stuff
    ZoomablePaneTest zpt;
    ZoomablePaneTest.ZoomingPane zoomingPane;

    /**
     * initialize() is called right after the constructor, but unlike the constructor, has access to all FXML
     * elements. Therefore, this method is used to set some behaviour of the FXML objects.
     */
    @FXML
    public void initialize(){

        panes = new AnchorPane[] {pane1, pane2, pane3, pane4, pane5, pane6, pane7, pane8};
        tabs = new AnchorPane[] {tab1, tab2, tab3, tab4, tab5, tab6, tab7, tab8};
        setUpCustomTabDrawer();
        setUpCustomTabTransitions();
        setUpCustomTabsClicked();
        addZoomablePane();
    }

    public void addZoomablePane(){
        zpt = new ZoomablePaneTest(new Pane(),zoomSlider, addUnitBtn, moveUnitBtn);
        zoomingPane = zpt.getZoomingPane();
        testPane.setCenter(zoomingPane);
    }

    /**
     * This method opens a window which lets the user pick a new image from their harddrive to use as the map.
     * Only .jpg and .png images are accepted.
     */
    public void openFileAsNewMap(){
        zpt.changeMap();
    }

    public void pressBlueBtn(){
        isFriendlySelected = true;
        blueBtn.setEffect(new Glow(0.6));
        redBtn.setEffect(new Glow(0));
    }

    public void pressRedBtn(){
        isFriendlySelected = false;
        redBtn.setEffect(new Glow(1));
        redBtn.setEffect(new Bloom(0.3));
        blueBtn.setEffect(new Glow(0));
    }

    /**
     * Sets up animations for moving the tab menu. What actually moves is the Anchorpane "tabmenu", which has
     * bounds set so only non transparent parts of it set off the animation.
     */
    private void setUpCustomTabDrawer(){
        moveDown = new TranslateTransition(Duration.millis(140), tabmenu);
        moveUp = new TranslateTransition(Duration.millis(140), tabmenu);

        tabmenu.setPickOnBounds(false);

        // pull down tabs
        tabmenu.setOnMouseEntered(event -> {
            moveDown.setFromY(tabmenu.getTranslateY());
            moveDown.setToY(0);
            moveDown.play();
        });

        // fold up tabs
        tabmenu.setOnMouseExited(event -> {
            moveUp.setFromY(tabmenu.getTranslateY());
            moveUp.setToY(-tabmenu.getHeight() + 80);
            moveUp.play();
        });
    }

    /**
     * Sets up animations that changes the color of a tab either from transparent to half white or vice versa.
     */
    private void setUpCustomTabTransitions(){
        for (AnchorPane tab: tabs) {
            // what happens when hovering over a tab
            tab.setOnMouseEntered(event -> {
                fadeTab(tab, 1);
            });

            // what happens when not hovering anymore
            tab.setOnMouseExited(event -> {
                fadeTab(tab, 2);
            });
        }
    }

    /**
     * Sets up what happens when a tab is clicked. Makes the tab highlighted and changes what is shown on screen.
     */
    private void setUpCustomTabsClicked(){
        fadeTab(tab1, 3);

        tab1.setOnMouseClicked(event -> {
            fadeTab(tabs[selectedTab], 2);
            disablePane(panes[selectedTab]);
            selectedTab = 0;
            fadeTab(tab1, 3);
            enablePane(panes[selectedTab]);
        });

        tab2.setOnMouseClicked(event -> {
            fadeTab(tabs[selectedTab], 2);
            disablePane(panes[selectedTab]);
            selectedTab = 1;
            fadeTab(tab2, 3);
            enablePane(panes[selectedTab]);
        });

        tab3.setOnMouseClicked(event -> {
            fadeTab(tabs[selectedTab], 2);
            disablePane(panes[selectedTab]);
            selectedTab = 2;
            fadeTab(tab3, 3);
            enablePane(panes[selectedTab]);
        });

        tab4.setOnMouseClicked(event -> {
            fadeTab(tabs[selectedTab], 2);
            disablePane(panes[selectedTab]);
            selectedTab = 3;
            fadeTab(tab4, 3);
            enablePane(panes[selectedTab]);
        });

        tab5.setOnMouseClicked(event -> {
            fadeTab(tabs[selectedTab], 2);
            disablePane(panes[selectedTab]);
            selectedTab = 4;
            fadeTab(tab5, 3);
            enablePane(panes[selectedTab]);
        });

        tab6.setOnMouseClicked(event -> {
            fadeTab(tabs[selectedTab], 2);
            disablePane(panes[selectedTab]);
            selectedTab = 5;
            fadeTab(tab6, 3);
            enablePane(panes[selectedTab]);
        });

        tab7.setOnMouseClicked(event -> {
            fadeTab(tabs[selectedTab], 2);
            disablePane(panes[selectedTab]);
            selectedTab = 6;
            fadeTab(tab7, 3);
            enablePane(panes[selectedTab]);
        });

        tab8.setOnMouseClicked(event -> {
            fadeTab(tabs[selectedTab], 2);
            disablePane(panes[selectedTab]);
            selectedTab = 7;
            fadeTab(tab8, 3);
            enablePane(panes[selectedTab]);
        });

    }

    /**
     * Helper method. Fades a tab in, out, or highlighted depending on given int.
     * @param tab
     */
    private void fadeTab(AnchorPane tab, int inOutOrSelect){
        Animation fadeIn = new Transition() {
            {
                setCycleDuration(Duration.millis(100));
                setInterpolator(Interpolator.EASE_OUT);
            }
            @Override
            protected void interpolate(double frac) {
                switch (inOutOrSelect){
                    // if int is 1, fade in
                    case 1:
                        if(tabs[selectedTab] != tab)
                            tab.setBackground(new Background(new BackgroundFill(new Color(1, 1, 1, 0 + (frac/4)), new CornerRadii(10), Insets.EMPTY)));
                        break;

                    // if int is 2, fade out
                    case 2:
                        if(tabs[selectedTab] != tab)
                            tab.setBackground(new Background(new BackgroundFill(new Color(1, 1, 1, 0.25 - (frac/4)), new CornerRadii(10), Insets.EMPTY)));
                        break;

                    // if int is 3, select
                    case 3:
                        tab.setBackground(new Background(new BackgroundFill(new Color(1, 1, 1, 0.25 + (frac/4)), new CornerRadii(10), Insets.EMPTY)));
                        break;

                    default:
                        System.out.println("Error, a faulty input has been given to the fadeTab function!");
                        break;
                }

            }
        };
        fadeIn.play();
    }

    private void disablePane(AnchorPane pane){
        panes[selectedTab].setOpacity(0);
        panes[selectedTab].setDisable(true);
    }

    private void enablePane(AnchorPane pane){
        pane.setOpacity(1);
        pane.setDisable(false);
    }

}
