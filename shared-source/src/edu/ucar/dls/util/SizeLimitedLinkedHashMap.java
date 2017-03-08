/*
	Copyright 2017 Digital Learning Sciences (DLS) at the
	University Corporation for Atmospheric Research (UCAR),
	P.O. Box 3000, Boulder, CO 80307

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
*/
package edu.ucar.dls.util;


import java.util.LinkedHashMap;
import java.util.Map;

/**
 *  A Map that limits it's size automatically by removing the oldest entry if max size is exceeded. This is
 *  useful if the map represents a cache: it allows the map to reduce memory consumption by deleting stale
 *  entries. Can configure whether the age of the entries is determined by insertion-order or
 *  access-order.
 *
 * @author    John Weatherley
 */
public class SizeLimitedLinkedHashMap extends LinkedHashMap {
    private int _maxSize = -1;

    private static final float defaultLoadFactor = 0.75f;


    /**
     *  Constructs an empty SizeLimitedLinkedHashMap instance with the specified initial capacity,
     *  ordering mode, and maximum size, using default load factor 0.75f.
     *
     * @param  initialCapacity  the initial capacity.
     * @param  accessOrder      the ordering mode - true for access-order, false for insertion-order.
     * @param  maxSize          the maximum size allowed for this Map.
     */
    public SizeLimitedLinkedHashMap(int initialCapacity, boolean accessOrder, int maxSize) {
        super(initialCapacity, defaultLoadFactor, accessOrder);
        _maxSize = maxSize;
    }


    /**
     *  Constructs an empty insertion-ordered SizeLimitedLinkedHashMap instance with the given maximum size, a
     *  default capacity (16) and load factor (0.75).
     *
     * @param  maxSize  the maximum size allowed for this Map.
     */
    public SizeLimitedLinkedHashMap(int maxSize) {
        super();
        _maxSize = maxSize;
    }


    /**
     *  Called by put to determine if the oldest entry should be removed.
     *
     * @param  eldest  Description of the Parameter.
     * @return         True if the eldest entry should be removed.
     */
    protected boolean removeEldestEntry(Map.Entry eldest) {
        return size() > _maxSize;
    }
}
