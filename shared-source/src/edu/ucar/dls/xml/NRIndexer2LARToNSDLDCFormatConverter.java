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
package edu.ucar.dls.xml;

import java.io.File;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.servlet.ServletContext;
import javax.sql.DataSource;
import javax.xml.transform.Transformer;

import edu.ucar.dls.dds.nr.NRIndexer2;
import edu.ucar.dls.index.reader.*;
import edu.ucar.dls.repository.RepositoryManager;
import org.dom4j.Document;
import org.dom4j.Element;




/**
 *  Converts LAR records into NSDL_DC records. This conversion first tries to grab the
 *  nsdl_dc version from the database. If that fails it uses a lar -> nsdl_dc transformation.
 *  Which was the default process prior to this converter
 *
 * @author    Dave Finke
 * @see       XMLConversionService
 */
public class NRIndexer2LARToNSDLDCFormatConverter implements XMLDocumentFormatConverter {
	
	// The backup xsl that will be used if the db connection couldn't be made
	// or if the record wasn't found
	private final static String BACKUP_LAR_TO_NSDL_DC_XSL = 
			"lar-v1.00-to-nsdl_dc-v1.02.020.xsl";
	
	private Transformer transformer = null;
	private Connection connection = null;
	
	// SQL statement to fetch the nsdl_dc record which is target_xml
	private final static String GET_NSDL_DC_DATA = 
		"SELECT target_xml " +
		"FROM metadata " +
		"WHERE metadatahandle='%s'";
	
	
	/**
	 * Get the XSL transformer for lar-> nsdl_dc. Note this is the backup if the nsdl_dc cannot
	 * be retrieved from the database
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private Transformer getTransformer(ServletContext context) throws Exception
	{
		if (this.transformer == null) {
			String xslPath = (String)context.getAttribute("xslFilesDirecoryPath");
			this.transformer = XSLTransformer.getTransformer(
					new File(xslPath, BACKUP_LAR_TO_NSDL_DC_XSL).getAbsolutePath());
		}
		return this.transformer;
	}
	
	/**
	 * Gets the nsdl_repository connection from the NRIndexer2, This is so this file
	 * doesn't need to know the contents of that file
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private Connection getConnection(ServletContext context) throws Exception
	{
		if(this.connection==null || this.connection.isClosed())
		{
			File repositoryConfigDir = ((RepositoryManager) context.getAttribute(
					"repositoryManager")).getItemIndexerConfigDir();
			DataSource ds = NRIndexer2.createDataSource(repositoryConfigDir);
			this.connection = ds.getConnection();
		}
		
		return this.connection;
	}
	/**
	 *  Converts from the ADN format.
	 *
	 * @return    The String "adn".
	 */
	public String getFromFormat() {
		return "lar";
	}


	/**
	 *  Converts to the nsdl_dc format.
	 *
	 * @return    The String "nsdl_dc".
	 */
	public String getToFormat() {
		return "nsdl_dc";
	}


	/**
	 *  Gets the time this converter code was last modified. If unknown, this method should
	 *  return -1.
	 *
	 * @param  context  The context in which this is running.
	 * @return          The time this converter code was last modified.
	 */
	public long lastModified(ServletContext context) {
		return Long.MAX_VALUE;
	}


	/**
	 *  Performs XML conversion from LAR to nsdl_dc format. Characters are encoded as UTF-8.
	 *  This first tries to grab the nsdl_dc version of the record from the database.
	 *  If that fails it then tries the backup lar->nsdl_dc xsl transformation
	 *
	 * @param  xml        XML input in the 'lar' format.
	 * @param  context    The context in which this is running.
	 * @param  docReader  Lucene DocReader for the item.
	 * @return            XML in the converted 'nsdl_dc' format.
	 */
	public String convertXML(String xml, XMLDocReader docReader, ServletContext context) {
		String nsdlDcRecord = null;
		
		Statement stmt = null;
		ResultSet rs = null;

		try {
			Document document = Dom4jUtils.getXmlDocument(xml);
			// Get the record ID from the lar record
			Element recordIdElement = (Element)document.getRootElement().selectSingleNode(
					"*[name()='recordID']");
			String metadataHandle = recordIdElement.getTextTrim();
			stmt = this.getConnection(context).createStatement();
			rs = stmt.executeQuery(String.format(GET_NSDL_DC_DATA, metadataHandle));
			
			if(rs.next())
			{
				// Convert the blob 
				Blob blob = rs.getBlob(1);
				byte[] blobBytes= blob.getBytes(1, (int)blob.length());
				nsdlDcRecord = new String(blobBytes, "UTF-8");
			}
			else
			{
				// This might happen if the index is older, And in the database the record
				// was removed per a deletion 
				throw new Exception("Indexed LAR record could not be found in the database. Must "+
						"have been deleted since the index was last ran.");
			}
		} catch (Exception e) {
			// Was not able to get the nsdl_dc version from the database try to use
			// the default XSL file instead.
			System.out.println("Error trying to connect to the nsdl_repository database to pull "+
					"the nsdl_dc version of a lar record. Reason: "+e.getMessage()+". Now trying to use "+
					"the XSL transformer instead.");
			Transformer transformer;
			try {
				transformer = this.getTransformer(context);
				synchronized (transformer) {
					nsdlDcRecord = XSLTransformer.transformString(xml, transformer);
				}
			} catch (Exception e1) {
				// Nothing else we can do besides return null now.
				System.out.println(
						String.format(
						"Unable to transform LAR records with a XSL transform %s, because %s ",
						BACKUP_LAR_TO_NSDL_DC_XSL, e.getMessage()));
				return null;
			}
			
		}
		finally
		{
			// Close all database object if applicable
			if(rs!=null)
				try {
					rs.close();
				} catch (SQLException e) {

				}
			if(stmt!=null)
				try {
					stmt.close();
				} catch (SQLException e) {

				}
		}
		return nsdlDcRecord;
	}

	/**
	 * Custom destroy method that tries to close the DB connection down.
	 */
	public void destroy()
	{
		
		if(this.connection!=null)
		{
			try {
				this.connection.close();
			} catch (SQLException e) {
			}
		}
		
	}
	
	

}

