/* This class is used to handle import and export to and from files. This could be stuff like loading new images
 * as maps, saving a configuration as a file, or exporting results, amongst other things
 *
 * Written by Nils Odin 24 January 2019
 */

package project;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.FileInputStream;

public class IoControl {

    public boolean changeMapImage(ImageView map){

        // Create file chooser
        FileChooser fc = new FileChooser();
        FileChooser.ExtensionFilter png = new FileChooser.ExtensionFilter("png", "*.png");
        FileChooser.ExtensionFilter jpg = new FileChooser.ExtensionFilter("jpg", "*.jpg");
        fc.getExtensionFilters().addAll(png,jpg);

        // Attempt to change image to new one
        try {
            String newImagePath = fc.showOpenDialog(Main.stage).getAbsolutePath();
            Image source = new Image(new FileInputStream(newImagePath));
            map.setImage(source);
            return true;
        } catch (Exception e) {
            e.getMessage();
            return false;
        }
    }
}
