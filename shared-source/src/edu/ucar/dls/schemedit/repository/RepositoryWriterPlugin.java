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
package edu.ucar.dls.schemedit.repository;

import edu.ucar.dls.schemedit.dcs.DcsDataRecord;
import edu.ucar.dls.schemedit.config.CollectionConfig;

import javax.servlet.ServletContext;

/**
 *  Specifies functionality for a plugin that writes metadata to a repository
 *  source (e.g., the NDR, a Database) other than the file-based repository.
 *
 * @author    ostwald
 */
public interface RepositoryWriterPlugin {

	/**
	 *  Writes a metadata record to the repository source for this plugin.
	 *
	 * @param  recId                                metadata record Id
	 * @param  dcsDataRecord                        dcsData for the record to be
	 *      written
	 * @param  recordXml                            metadata as an xml String
	 * @param  xmlFormat                            format of metadata record
	 * @exception  RepositoryWriterPluginException  if unable to perform write
	 */
	public void putRecord(String recId, String recordXml, String xmlFormat, DcsDataRecord dcsDataRecord)
		 throws RepositoryWriterPluginException;


	/**
	 *  Write collection-level data to the repository source for this plugin
	 *
	 * @param  id                                   collection recordID
	 * @param  collectionConfig                     collection configuration
	 * @param  dcsDataRecord                        dcsData for the collection
	 *      metadata
	 * @exception  RepositoryWriterPluginException  if collectionData cannot be
	 *      written
	 */
	public void putCollectionData(String id, CollectionConfig collectionConfig, DcsDataRecord dcsDataRecord)
		 throws RepositoryWriterPluginException;


	/**
	 *  Delete a record from the repository source for this plugin
	 *
	 * @param  recId                                ID of record to be deleted
	 * @param  dcsDataRecord                        status record associated with
	 *      rec to be deleted
	 * @exception  RepositoryWriterPluginException  NOT YET DOCUMENTED
	 */
	public void deleteRecord(String recId, DcsDataRecord dcsDataRecord)
		 throws RepositoryWriterPluginException;

}

