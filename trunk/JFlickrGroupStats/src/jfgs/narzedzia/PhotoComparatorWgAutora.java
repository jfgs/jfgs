/*
 */

package jfgs.narzedzia;

import com.aetrion.flickr.photos.Photo;
import java.util.Comparator;

/**
 * Porównywacz zdjęć wg identyfikatora autora
 * @author michalus
 */
public class PhotoComparatorWgAutora implements Comparator<Photo> {

    public int compare(Photo o1, Photo o2) {
        return
            o1.getOwner().getId().compareTo(
                o2.getOwner().getId());
    }

}
