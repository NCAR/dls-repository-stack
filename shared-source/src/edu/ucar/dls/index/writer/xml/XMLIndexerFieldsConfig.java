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
package edu.ucar.dls.index.writer.xml;

import java.io.*;
import java.util.*;
import java.text.*;
import java.net.URL;

import org.dom4j.*;

import edu.ucar.dls.xml.*;
import edu.ucar.dls.util.*;
import edu.ucar.dls.index.analysis.PerFieldAnalyzer;
import edu.ucar.dls.repository.indexing.IndexFieldPreprocessor;

/**
 * Holds the configuration settings for the search fields for each XML framework (nsdl_dc, mps2, lar, etc.).
 * The configured search fields are indexed by XMLIndexer and used in PerFieldAnalyzer.
 *
 * @author John Weatherley
 * @see XMLIndexer
 */
public class XMLIndexerFieldsConfig {

    private Document configIndexXmlDoc = null;
    private Map formatConfigDocs = new TreeMap();
    private Map fieldAnalyzers = new TreeMap();
    private Map indexFieldPreprocessors = new TreeMap();
    private Map facetCategories = new TreeMap();
    private Object context = null;


    /**
     * Initialize the XMLIndexerFieldsConfig.
     *
     * @param configIndexUrl URL to the config index file.
     * @param context        Object that contains configuration settings for the given context. For webapps,
     *                       this is an instance of ServletContext. May be null.
     * @throws Exception If error
     */
    public XMLIndexerFieldsConfig(URL configIndexUrl, Object context) throws Exception {
        this.context = context;

        configIndexXmlDoc = Dom4jUtils.localizeXml(Dom4jUtils.getXmlDocument(configIndexUrl));

        // Loop through each format's configuration file:
        List nodes = configIndexXmlDoc.selectNodes("/XMLIndexerFieldsConfigIndex/configurationFiles/configurationFile");
        if (nodes != null) {
            for (int i = 0; i < nodes.size(); i++) {
                try {
                    URL confUrl = new URL(configIndexUrl, ((Node) nodes.get(i)).getText().trim());
                    Document confDoc = Dom4jUtils.localizeXml(Dom4jUtils.getXmlDocument(confUrl));
                    //prtln("confDoc: " + confDoc.asXML());
                    String xmlFormat = confDoc.valueOf("/XMLIndexerFieldsConfig/@xmlFormat").trim();
                    String schema = confDoc.valueOf("/XMLIndexerFieldsConfig/@schema").trim();
                    if (xmlFormat.length() > 0 && schema.trim().length() > 0) {
                        prtlnErr("Not valid to specify both an XML format and a schema. Skipping xmlFormat: '" + xmlFormat + "' schema: '" + schema + "'");
                        continue;
                    } else if (xmlFormat.length() > 0)
                        formatConfigDocs.put(xmlFormat, confDoc);
                    else if (schema.length() > 0)
                        formatConfigDocs.put(schema, confDoc);

                    // Loop through all custom fields and extract their type/analyzers and IndexFieldPreprocessor:
                    List fields = confDoc.selectNodes("/XMLIndexerFieldsConfig/customFields/customField");
                    for (int j = 0; j < fields.size(); j++) {
                        Node field = (Node) fields.get(j);
                        String fieldName = field.valueOf("@name").trim();
                        String analyzer = field.valueOf("@analyzer").trim();
                        String indexFieldPreprocessorClassName = field.valueOf("@indexFieldPreprocessor").trim();
                        String type = field.valueOf("@type").trim();
                        String facetCategory = field.valueOf("@facetCategory").trim();
                        String facetPathDelimeter = field.valueOf("@facetPathDelimeter").trim();

                        // Do some error checking on the config sytax:
                        if (fieldName.length() > 0 && facetCategory.length() > 0)
                            throw new Exception("Both a field and facetCategory were indicated, but only one or the other must be present.");
                        if (fieldName.length() == 0 && facetCategory.length() == 0)
                            throw new Exception("Neither a field or facetCategory were indicated, but one or the other must be present.");

                        // Create an instance of the IndexFieldProcessor class and place it into the config for use during indexing:
                        if (indexFieldPreprocessorClassName.length() > 0) {
                            String processorFieldOrCategoryName = (fieldName.length() > 0 ? fieldName : facetCategory);
                            try {
                                Class ifpClass = Class.forName(indexFieldPreprocessorClassName);
                                IndexFieldPreprocessor indexFieldPreprocessor = (IndexFieldPreprocessor) ifpClass.newInstance();
                                indexFieldPreprocessor.contextConfigListener(context);
                                indexFieldPreprocessors.put(processorFieldOrCategoryName, indexFieldPreprocessor);
                            } catch (Throwable t) {
                                throw new Exception("Error instantiating the IndexFieldPreprocessor class '" + indexFieldPreprocessorClassName + "' for field '" + fieldName + "'. Reason: " + t);
                            }
                        }

                        // Process regular custom Lucene fields:
                        if (fieldName.length() > 0) {
                            if (analyzer.length() > 0)
                                fieldAnalyzers.put(fieldName, analyzer);
                            else {
                                if (type.equals("text"))
                                    fieldAnalyzers.put(fieldName, PerFieldAnalyzer.TEXT_ANALYZER);
                                else if (type.equals("key"))
                                    fieldAnalyzers.put(fieldName, PerFieldAnalyzer.KEYWORD_ANALYZER);
                                else if (type.equals("stems"))
                                    fieldAnalyzers.put(fieldName, PerFieldAnalyzer.STEMS_ANALYZER);
                                else
                                    throw new Exception("Valid field type or analyzer must be specified for field '" + fieldName + "'. Found type:'" + type + "' analyzer:'" + analyzer + "'");
                            }
                        }

                        // Process facet categories:
                        else if (facetCategory.length() > 0) {
                            facetCategories.put(facetCategory, facetPathDelimeter);
                            fieldAnalyzers.put("facet." + facetCategory, PerFieldAnalyzer.KEYWORD_ANALYZER);
                        }
                    }
                    //prtln("fieldAnalyzers: " + fieldAnalyzers + " size: " + fieldAnalyzers.size());
                } catch (Exception e) {
                    prtlnErr("Error processing configuration file: '" + configIndexUrl + "'");
                    throw e;
                }
            }
        }
    }


    /**
     * Gets a Map of field/analyzer pairs where keys are field or schema names and values are the corresponding
     * Analyzer class names as Strings for the custom fields that are defined in this configuration.
     *
     * @return Map of field/analyzer pairs
     */
    public Map getFieldAnalyzers() {
        return fieldAnalyzers;
    }


    /**
     * Gets the indexFieldPreprocessor attribute of the XMLIndexerFieldsConfig object
     *
     * @param field The field
     * @return The indexFieldPreprocessor value
     */
    public IndexFieldPreprocessor getIndexFieldPreprocessor(String field) {
        return (IndexFieldPreprocessor) indexFieldPreprocessors.get(field);
    }


    /**
     * Gets the delimiter used in the path for a given facet category, or empty String if none is being used
     * (flat category).
     *
     * @param facetCategory The facet cateogry
     * @return The facetCategoryPathDelimiter value
     */
    public String getFacetCategoryPathDelimiter(String facetCategory) {
        return (String) facetCategories.get(facetCategory);
    }


    /**
     * Gets the facet category delimiter Map. Keys are category names, values are the delimiters used for that
     * category, or null if no delimeter was defined for a category.
     *
     * @return The facet categoryh delimiter map
     */
    public Map getFacetCategoryDelimiterMap() {
        return facetCategories;
    }


    /**
     * Gets the configuration Document for a given xmlFormat or schema. Example xmlFormat keys are 'oai_dc',
     * 'library_dc'. An example schema is 'http://www.openarchives.org/OAI/2.0/oai_dc.xsd'.
     *
     * @param xmlFormatOrSchema An xmlFormat key or schema location for the format
     * @return A configuration Document or null if not available
     */
    public Document getFormatConfig(String xmlFormatOrSchema) {
        return (Document) formatConfigDocs.get(xmlFormatOrSchema);
    }


    /**
     * Determine if the given xmlFormat or schema has a configuration. Example xmlFormat keys are 'oai_dc',
     * 'library_dc'. An example schema is 'http://www.openarchives.org/OAI/2.0/oai_dc.xsd'.
     *
     * @param xmlFormatOrSchema An xmlFormat key or schema location for the format
     * @return True if the xmlFormat or schema has a configuration
     */
    public boolean formatIsConfigured(String xmlFormatOrSchema) {
        return formatConfigDocs.containsKey(xmlFormatOrSchema);
    }


    private void prtln(String s) {
        System.out.println("XMLIndexerFieldsConfig: " + s);
    }


    private void prtlnErr(String s) {
        System.err.println("XMLIndexerFieldsConfig ERROR: " + s);
    }

}

