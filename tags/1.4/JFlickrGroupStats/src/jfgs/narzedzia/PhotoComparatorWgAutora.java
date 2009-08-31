/*
 *  This file is part of JFlickrGroupStats.
 *
 *  JFlickrGroupStats is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  JFlickrGroupStats is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with JFlickrGroupStats.  If not, see <http://www.gnu.org/licenses/>.
 *
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
