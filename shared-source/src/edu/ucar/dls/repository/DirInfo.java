/*
 *  License and Copyright:
 *
 *  The contents of this file are subject to the Educational Community License v1.0 (the "License"); you may
 *  not use this file except in compliance with the License. You should have received a copy of the License
 *  along with this software; if not, you may obtain a copy of the License at
 *  http://www.opensource.org/licenses/ecl1.php.
 *
 *  Software distributed under the License is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 *  either express or implied. See the License for the specific language governing rights and limitations
 *  under the License.
 *
 *  Copyright 2002-2009 by Digital Learning Sciences, University Corporation for Atmospheric Research (UCAR).
 *  All rights reserved.
 */

package edu.ucar.dls.repository;

import java.io.*;
import java.util.*;

/**
 * Data structure that holds information about a directory of metadata files. Includes
 * the directory name and the metadata format for the files in that directory.
 *
 * @author John Weatherley
 * @see SetInfo
 */
public class DirInfo implements Serializable {


    private String dir = "";
    private String format = "";
    private HashMap attributes = new HashMap();


    /**
     * Constructor for the DirInfo object
     */
    public DirInfo() {
    }


    /**
     * Constructor for the DirInfo object
     *
     * @param dir    The direcotory path
     * @param format The metadata format for the files in the directory
     */
    public DirInfo(String dir, String format) {
        this.dir = dir.trim();
        this.format = format.trim();
    }


    /**
     * Constructor to create a DirInfo object from an existing (legacy) DirInfo object.
     *
     * @param dir                The directory path
     * @param format             The metadata format for the files in the directory
     * @param existingAttributes The attributes
     */
    public DirInfo(String dir, String format, HashMap existingAttributes) {
        this.dir = dir.trim();
        this.format = format.trim();
        if (existingAttributes != null) {
            this.attributes = existingAttributes;
        }
    }


    /**
     * Gets the dir attribute of the DirInfo object
     *
     * @return The dir value
     */
    public String getDirectory() {
        return dir;
    }


    /**
     * Sets the dir attribute of the DirInfo object
     *
     * @param val The new dir value
     */
    public void setDirectory(String val) {
        dir = val.trim();
    }


    /**
     * Determines whether this directory is the same as the given directory.
     *
     * @param directory A directory
     * @return True if this directory is the same
     */
    public boolean hasDirectory(File directory) {
        File f = new File(dir);
        return f.equals(directory);
    }


    /**
     * Gets the format attribute of the DirInfo object
     *
     * @return The format value
     */
    public String getFormat() {
        return format;
    }


    /**
     * Sets the format attribute of the DirInfo object
     *
     * @param val The new format value
     */
    public void setFormat(String val) {
        format = val.trim();
    }


    /**
     * Sets an attribute Object that will be available for access using the given key. The
     * object MUST be serializable.
     *
     * @param key       The key used to reference the attribute.
     * @param attribute Any Java Object that is Serializable.
     */
    public void setAttribute(String key, Object attribute) {
        attributes.put(key, attribute);
    }


    /**
     * Gets an attribute Object from this DirInfo.
     *
     * @param key The key used to reference the attribute.
     * @return The Java Object that was stored under the given key or null if none
     * exists.
     */
    public Object getAttribute(String key) {
        return attributes.get(key);
    }


    /**
     * Provides a String representataion for this DirInfo. This method may be used for
     * debugging to see what is in the DirInfo. This method is also used it the {@link
     * #equals(Object)} method.
     *
     * @return String describing all data in this DirInfo.
     */
    public String toString() {
        StringBuffer ret = new StringBuffer();
        ret.append("format: ");
        ret.append(format);
        ret.append(" directory: ");
        Object[] attributeKeys = attributes.keySet().toArray();
        for (int i = 0; i < attributeKeys.length; i++)
            ret.append("\n attribute: " + attributeKeys[i].toString());
        return ret.toString();
    }


    /**
     * Checks equality of two DirInfo objects.
     *
     * @param o The DirInfo to compare to this
     * @return True iff the compared object is equal
     */
    public boolean equals(Object o) {
        if (o == null || !(o instanceof DirInfo))
            return false;
        return (this.toString().equals(o.toString()));
    }


}


