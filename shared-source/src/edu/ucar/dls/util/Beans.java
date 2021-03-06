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

import java.beans.*;
import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;

import edu.ucar.dls.dds.RepositoryInfoBean;
import edu.ucar.dls.dds.nr.IndexingStatusBean;


/**
 * Utility methods for working with Java Beans.
 *
 * @author John Weatherley
 */
public class Beans {

    /**
     * Fetch a Java Bean from a file on disc.
     *
     * @param file File to fetch from
     * @return A JavaBean Object
     * @throws Exception If error
     */
    public static Object file2Bean(File file) throws Exception {
        XMLDecoder d = new XMLDecoder(new BufferedInputStream(new FileInputStream(file)));
        Object result = d.readObject();
        d.close();

        // For repository installation migration, update to Beans to new type:
        if (result != null) {
            if (result instanceof org.dlese.dpc.dds.RepositoryInfoBean)
                return convertLegacyRepositoryInfoBean((org.dlese.dpc.dds.RepositoryInfoBean) result);
            if (result instanceof org.dlese.dpc.dds.nr.IndexingStatusBean)
                return convertLegacyIndexingStatusBean((org.dlese.dpc.dds.nr.IndexingStatusBean) result);
        }

        return result;
    }


    /**
     * Save a Java Bean to file on disc.
     *
     * @param bean A JavaBean Object, which must implement serializable
     * @param file File to save to
     * @return The same JavaBean Object
     * @throws Exception If error
     */
    public static void bean2File(Object bean, File file) throws Exception {
        XMLEncoder e = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(file)));
        e.writeObject(bean);
        e.close();
    }

    /**
     * Convert a legacy org.dlese.dpc package RepositoryInfoBean to the new edu.ucar.dls package Bean type.
     * Used to migrate existing beans after package were updated.
     */
    public static RepositoryInfoBean convertLegacyRepositoryInfoBean(org.dlese.dpc.dds.RepositoryInfoBean legacyBean) throws Exception {
        if (legacyBean == null)
            return null;
        RepositoryInfoBean newBean = new RepositoryInfoBean();
        newBean.setBackgroundIndexerRepository(legacyBean.getBackgroundIndexerRepository());
        newBean.setCurrentRepository(legacyBean.getCurrentRepository());
        newBean.setMakeImportedRepositoriesCurrentRepository(legacyBean.isMakeImportedRepositoriesCurrentRepository());
        newBean.setNextBackgroundRepositoryNumber(legacyBean.getNextBackgroundRepositoryNumber());
        return newBean;
    }

    /**
     * Convert a legacy org.dlese.dpc package IndexingStatusBean to the new edu.ucar.dls package Bean type.
     * Used to migrate existing beans after package were updated.
     */
    public static IndexingStatusBean convertLegacyIndexingStatusBean(org.dlese.dpc.dds.nr.IndexingStatusBean legacyBean) throws Exception {
        if (legacyBean == null)
            return null;
        IndexingStatusBean newBean = new IndexingStatusBean();
        newBean.setCompletedCollections(legacyBean.getCompletedCollections());
        newBean.setProgressStatus(legacyBean.getProgressStatus());
        newBean.setStatusLastModifiedDate(legacyBean.getStatusLastModifiedDate());
        return newBean;
    }


    private final static void prtlnErr(String s) {
        System.err.println("Beans Error: " + s);
    }


    private final static void prtln(String s) {
        System.out.println("Beans: " + s);
    }

}


