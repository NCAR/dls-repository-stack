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

import java.util.regex.Pattern;

import edu.ucar.dls.index.writer.*;
import edu.ucar.dls.xml.*;
import edu.ucar.dls.index.*;
import edu.ucar.dls.util.*;
import edu.ucsb.adl.LuceneGeospatialQueryConverter;
import org.apache.commons.lang3.ArrayUtils;
import edu.ucar.dls.schemedit.url.UrlHelper;

import org.apache.lucene.document.*;
import edu.ucar.dls.repository.indexing.IndexFieldPreprocessor;
import org.apache.lucene.facet.taxonomy.CategoryPath;
import org.apache.lucene.facet.index.params.DefaultFacetIndexingParams;

/**
 * Adds index fields to a Lucene {@link org.apache.lucene.document.Document} from any well-formed XML. Individual
 * field names are derived from the xPath to each element and attribute in the XML instance document. Fields
 * are encoded to support text, keyword and stemmed search. Also creates standard fields for IDs, URLs, title,
 * description and geospatial bounding box footprint. The 'default' and 'stems' fields are also indexed as text and stemmed text, respectively.
 * <p/>
 * <p/>
 * A {@link XMLIndexerFieldsConfig} may be supplied to configure specific search fields for given XML
 * formats. If a field is defined in the XMLIndexerFieldsConfig, and content is avialable at the given xPath,
 * it will override the value set for ids, urls,
 * title or description. In addition, field values configured by schema override those configured by xmlFormat.
 *
 * @author John Weatherley
 * @see XMLIndexerFieldsConfig
 */
public class XMLIndexer {
    private boolean debug = false;

    private String xmlFormat = null;
    private org.dom4j.Document xmlDoc = null;
    private String fullXmlElementContent = ""; // Extract the full content from the XML Elements or JavaBean properties
    private String fullXmlAttributeContent = ""; // Extract the full content from the XML Attributes
    private boolean xmlProcessed = false;
    private XMLIndexerFieldsConfig xmlIndexerFieldsConfig = null;

    private String title = null;
    private String description = null;
    private String[] urls = null;
    private String[] ids = null;
    private String[] _ids_encoded = null;
    private List _relatedIds = null;
    private List _relatedUrls = null;
    private Map _relatedIdsMap = null;
    private Map _relatedUrlsMap = null;
    private boolean indexDefaultAndStemsField = true;
    private BoundingBox boundingBox = null;
    private String xPathFieldsPrefix = null;

    private final static String illegalPreProssingMsg = "The method indexFields() must be called prior to using this method.";
    private final static String illegalPostProssingMsg = "This method may not be called after the indexFields() method has been called.";


    /**
     * Sets whether to index the default, admindefault, and stems field for this record.
     *
     * @param indexDefaultAndStemsField The value to assign indexDefaultAndStemsField.
     * @throws IllegalStateException If called after method #indexFields has been called
     */
    public void setIndexDefaultAndStemsField(boolean indexDefaultAndStemsField) throws IllegalStateException {
        if (xmlProcessed)
            throw new IllegalStateException(illegalPostProssingMsg);
        this.indexDefaultAndStemsField = indexDefaultAndStemsField;
    }


    /**
     * Returns the value of title.
     *
     * @return The title value
     * @throws IllegalStateException If called prior to calling method #indexFields
     */
    public String getTitle() throws IllegalStateException {
        if (!xmlProcessed)
            throw new IllegalStateException(illegalPreProssingMsg);
        return title;
    }


    /**
     * Sets the value of title.
     *
     * @param title The value to assign title.
     * @throws IllegalStateException If called after method #indexFields has been called
     */
    public void setTitle(String title) throws IllegalStateException {
        if (xmlProcessed)
            throw new IllegalStateException(illegalPostProssingMsg);
        this.title = title;
    }


    /**
     * Returns the value of description.
     *
     * @return The description value
     * @throws IllegalStateException If called prior to calling method #indexFields
     */
    public String getDescription() throws IllegalStateException {
        if (!xmlProcessed)
            throw new IllegalStateException(illegalPreProssingMsg);
        return description;
    }


    /**
     * Sets the value of description.
     *
     * @param description The value to assign description.
     * @throws IllegalStateException If called after method #indexFields has been called
     */
    public void setDescription(String description) throws IllegalStateException {
        if (xmlProcessed)
            throw new IllegalStateException(illegalPostProssingMsg);
        this.description = description;
    }


    /**
     * Returns the value of urls.
     *
     * @return The urls value
     * @throws IllegalStateException If called prior to calling method #indexFields
     */
    public String[] getUrls() throws IllegalStateException {
        if (!xmlProcessed)
            throw new IllegalStateException(illegalPreProssingMsg);
        return urls;
    }


    /**
     * Sets the value of urls and normalizes them. URLs are normalized using the scheme
     * that was used in the NDR and is used in the Handle Resolution Service to generate handles for resource URLs and the NCS
     * to verify URL input fields.
     *
     * @param theUrls The value to assign urls.
     * @throws IllegalStateException If called after method #indexFields has been called
     */
    public void setUrls(String[] theUrls) throws IllegalStateException {
        if (xmlProcessed)
            throw new IllegalStateException(illegalPostProssingMsg);

        if (theUrls != null) {
            this.urls = new String[theUrls.length];
            for (int i = 0; i < theUrls.length; i++) {
                try {
                    this.urls[i] = UrlHelper.normalize(theUrls[i]);
                } catch (Throwable t){
                    // If unable to normalize (e.g. java.net.URISyntaxException), leave verbatim
                    this.urls[i] = theUrls[i];
                }
            }
        }
    }


    /**
     * Returns the value of ids.
     *
     * @return The ids value
     * @throws IllegalStateException If called prior to calling method #indexFields
     */
    public String[] getIds() throws IllegalStateException {
        if (!xmlProcessed)
            throw new IllegalStateException(illegalPreProssingMsg);
        return ids;
    }


    /**
     * Sets the value of ids.
     *
     * @param ids The value to assign ids.
     * @throws IllegalStateException If called after method #indexFields has been called
     */
    public void setIds(String[] ids) throws IllegalStateException {
        if (xmlProcessed)
            throw new IllegalStateException(illegalPostProssingMsg);
        this.ids = ids;
    }


    /**
     * Returns unique IDs for the item being indexed encoded for indexing. If more than one ID is present, the
     * first one is the primary.
     *
     * @return The id Strings encoded for indexing
     * @throws IllegalStateException If called prior to calling method #indexFields
     * @see #getIds
     */
    public String[] getIdsEncoded() throws IllegalStateException {
        if (_ids_encoded == null) {
            String[] ids = getIds();
            if (ids == null || ids.length == 0)
                return _ids_encoded;
            _ids_encoded = new String[ids.length];
            for (int i = 0; i < ids.length; i++)
                _ids_encoded[i] = SimpleLuceneIndex.encodeToTerm(ids[i]);
        }
        return _ids_encoded;
    }


    /**
     * Gets the ids of related records.
     *
     * @return The related ids
     * @throws IllegalStateException If called prior to calling method #indexFields
     */
    public List getRelatedIds() throws IllegalStateException {
        if (!xmlProcessed)
            throw new IllegalStateException(illegalPreProssingMsg);
        return _relatedIds;
    }

    /**
     * Gets the urls of related records.
     *
     * @return The related urls
     * @throws IllegalStateException If called prior to calling method #indexFields
     */
    public List getRelatedUrls() throws IllegalStateException {
        if (!xmlProcessed)
            throw new IllegalStateException(illegalPreProssingMsg);
        return _relatedUrls;
    }


    /**
     * Gets the ids of related records. The Map key contains the relationship (isAnnotatedBy, etc.) and the
     * Map value contains a List of Strings that indicate the ids of the target records.
     *
     * @return The related ids
     * @throws IllegalStateException If called prior to calling method #indexFields
     */
    public Map getRelatedIdsMap() throws IllegalStateException {
        if (!xmlProcessed)
            throw new IllegalStateException(illegalPreProssingMsg);
        return _relatedIdsMap;
    }

    /**
     * Gets the urls of related records. The Map key contains the relationship (isAnnotatedBy, etc.) and the
     * Map value contains a List of Strings that indicate the urls of the target records.
     *
     * @return The related urls
     * @throws IllegalStateException If called prior to calling method #indexFields
     */
    public Map getRelatedUrlsMap() throws IllegalStateException {
        if (!xmlProcessed)
            throw new IllegalStateException(illegalPreProssingMsg);
        return _relatedUrlsMap;
    }


    /**
     * Sets the ids of related records. The Map key contains the relationship (isAnnotatedBy, etc.) and the
     * Map value contains a List of Strings that indicate the ids of the target records.
     *
     * @param relations Map of related record ids .
     * @throws IllegalStateException If called after method #indexFields has been called
     */
    private void setRelatedIdsMap(Map relatedIdsMap) throws IllegalStateException {
        if (xmlProcessed)
            throw new IllegalStateException(illegalPostProssingMsg);
        this._relatedIdsMap = relatedIdsMap;
    }

    /**
     * Sets the urls of related records. The Map key contains the relationship (isAnnotatedBy, etc.) and the
     * Map value contains a List of Strings that indicate the urls of the target records.
     *
     * @param relations Map of related record urls.
     * @throws IllegalStateException If called after method #indexFields has been called
     */
    private void setRelatedUrlsMap(Map relatedUrlsMap) throws IllegalStateException {
        if (xmlProcessed)
            throw new IllegalStateException(illegalPostProssingMsg);
        this._relatedUrlsMap = relatedUrlsMap;
    }


    /**
     * Sets the ids of related records.
     *
     * @param relations List of related record ids .
     * @throws IllegalStateException If called after method #indexFields has been called
     */
    private void setRelatedIds(List relatedIds) throws IllegalStateException {
        if (xmlProcessed)
            throw new IllegalStateException(illegalPostProssingMsg);
        this._relatedIds = relatedIds;
    }


    /**
     * Sets the urls of related records.
     *
     * @param relations List of related record urls.
     * @throws IllegalStateException If called after method #indexFields has been called
     */
    private void setRelatedUrls(List relatedUrls) throws IllegalStateException {
        if (xmlProcessed)
            throw new IllegalStateException(illegalPostProssingMsg);
        this._relatedUrls = relatedUrls;
    }


    /**
     * Returns the value of xPathFieldsPrefix, or null if none.
     */
    public String getXPathFieldsPrefix() {
        return xPathFieldsPrefix;
    }

    /**
     * Sets the value of xPathFieldsPrefix, which is appended at the front of the xPath fields when indexed. Set to null to use none (default).
     *
     * @param xPathFieldsPrefix The value to append to the xPath fields, or null for none
     */
    public void setXPathFieldsPrefix(String xPathFieldsPrefix) throws IllegalStateException {
        if (xmlProcessed)
            throw new IllegalStateException(illegalPostProssingMsg);
        this.xPathFieldsPrefix = xPathFieldsPrefix;
    }


    /**
     * Returns the value of boundingBox.
     */
    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    /**
     * Sets the value of boundingBox.
     *
     * @param boundingBox The value to assign boundingBox.
     */
    public void setBoundingBox(BoundingBox boundingBox) {
        this.boundingBox = boundingBox;
    }

    /**
     * Gets the full content of each Element in the XML. Attribute content is not included. If this is a Java Bean, gets the contnet of all Bean properties.
     * Method #indexFields must be called prior to using this method.
     *
     * @return The full Element content
     * @throws IllegalStateException If called prior to calling method #indexFields
     */
    public String getFullXmlElementContent() throws IllegalStateException {
        if (!xmlProcessed)
            throw new IllegalStateException(illegalPreProssingMsg);
        return fullXmlElementContent;
    }

    /**
     * Gets the full content of each Attribute in the XML. Element content is not included. Method #indexFields must be called prior to using this
     * method.
     *
     * @return The full Attribute content
     * @throws IllegalStateException If called prior to calling method #indexFields
     */
    public String getFullXmlAttributeContent() throws IllegalStateException {
        if (!xmlProcessed)
            throw new IllegalStateException(illegalPreProssingMsg);
        return fullXmlAttributeContent;
    }

    /**
     * Gets the localized Dom4j Document for this XML instance.
     *
     * @return The xml Document
     */
    public org.dom4j.Document getXmlDocument() {
        return xmlDoc;
    }


    /**
     * Constructor for the XMLIndexer object
     *
     * @param localizedXmlDocument   A localized XML Document
     * @param xmlFormat              The XML format being indexed, for example adn or oai_dc
     * @param xmlIndexerFieldsConfig The config, or null if not used
     */
    public XMLIndexer(org.dom4j.Document localizedXmlDocument, String xmlFormat, XMLIndexerFieldsConfig xmlIndexerFieldsConfig) {
        this.xmlFormat = xmlFormat;
        this.xmlIndexerFieldsConfig = xmlIndexerFieldsConfig;
        xmlDoc = localizedXmlDocument;
    }


    /**
     * Constructor for the XMLIndexer object
     *
     * @param xmlString              A valid XML string
     * @param xmlFormat              The XML format being indexed, for example adn or oai_dc
     * @param xmlIndexerFieldsConfig The config, or null if not used
     * @throws Exception If error
     */
    public XMLIndexer(String xmlString, String xmlFormat, XMLIndexerFieldsConfig xmlIndexerFieldsConfig) throws Exception {
        this.xmlFormat = xmlFormat;
        this.xmlIndexerFieldsConfig = xmlIndexerFieldsConfig;
        //prtln("Transforming xml '" + xmlString + "'");
        xmlDoc = Dom4jUtils.getXmlDocumentLocalizedThrowsExceptions(xmlString);
    }


    /**
     * Constructor for the XMLIndexer object
     *
     * @param urlToXml               URL to an XML document
     * @param xmlFormat              The XML format being indexed, for example adn or oai_dc
     * @param xmlIndexerFieldsConfig The config, or null if not used
     * @throws Exception If error
     */
    public XMLIndexer(URL urlToXml, String xmlFormat, XMLIndexerFieldsConfig xmlIndexerFieldsConfig) throws Exception {
        this.xmlFormat = xmlFormat;
        this.xmlIndexerFieldsConfig = xmlIndexerFieldsConfig;
        xmlDoc = Dom4jUtils.localizeXml(Dom4jUtils.getXmlDocument(urlToXml));
    }


    /**
     * Indexes the contents of the XML, adding fields to the Lucene Document that is supplied.
     *
     * @param DocumentWrapper The DocumentWrapper to add fields to
     * @throws Exception If error, provides an appropriate message to display in indexing reports.
     */
    public void indexFields(DocumentWrapper documentWrapper) throws Exception {

        // Index fields for Java Beans (property/value pairs):
        boolean isJavaBean = indexJavaBeanFields(documentWrapper);

        // Index fields generacally as xPaths, if not a Java Bean:
        if (!isJavaBean)
            indexXpathFields(documentWrapper);

        // Index specific fields for this xmlFormat, if configured:
        indexCustomFields(xmlFormat, documentWrapper, null);
        extractStandardFields(xmlFormat);
        extractRelationships(xmlFormat);

        // Index specific fields for this schema, if configured:
        String schema = xmlDoc.getRootElement().valueOf("@schemaLocation").trim();
        if (schema.length() > 0) {
            String[] s = schema.split("\\s+");
            schema = s[s.length - 1];
            indexCustomFields(schema, documentWrapper, null);
            extractStandardFields(schema);
            extractRelationships(schema);
        }

        xmlProcessed = true;

        // Index the default fields:
        indexStandardFields(documentWrapper);
    }


    /**
     * Indexes the configured custom fields for this xmlFormat or schema instance doc. If indixing the primary document,
     * relationString should be null. If indxing content from a related document, the XML from the related document shoule have
     * been passed into the constructor for this XMLIndexer, the xmlFormatOrSchema should indicate the format
     * of the prmary document (not the related document), and the relation string should
     * indicate the relation prefix, for example '/relation.isAnnotatedBy/'.
     *
     * @param xmlFormatOrSchema The xmlFormat or schema that is being indexed
     * @param documentWrapper   The DocumentWrapper to add fields to
     * @param relationPrefix    The relation prefix (example '/relation.isAnnotatedBy/') if fields from a related doc are being indexed, otherwise null
     * @throws Exception If error, provides an appropriate message to display in indexing reports.
     */
    public void indexCustomFields(String xmlFormatOrSchema, DocumentWrapper documentWrapper, String relationPrefix) throws Exception {

        if (xmlIndexerFieldsConfig == null)
            return;

        org.apache.lucene.document.Document luceneDoc = documentWrapper.getDocument();

        HashMap<String,Object>facetPathIndexedMap = new HashMap<String,Object>();

        // Loop through each field and add the contents of the xPath to the index for that field
        org.dom4j.Document configXmlDoc = xmlIndexerFieldsConfig.getFormatConfig(xmlFormatOrSchema);
        if (configXmlDoc != null) {

            List fields = configXmlDoc.selectNodes("/XMLIndexerFieldsConfig/customFields/customField");

            prtln("indexCustomFields() for " + xmlFormatOrSchema + " (found " + fields.size() + " customField defined)");

            // For each custom field:
            for (int j = 0; j < fields.size(); j++) {
                Node field = (Node) fields.get(j);
                String fieldName = field.valueOf("@name").trim();
                String type = field.valueOf("@type").trim().toLowerCase();
                String store = field.valueOf("@store").trim().toLowerCase();
                String analyzer = field.valueOf("@analyzer").trim();
                String facetCategory = field.valueOf("@facetCategory").trim();
                String facetPathDelimeter = field.valueOf("@facetPathDelimeter").trim();
                boolean isFacetField = facetCategory.length() > 0;

                // Set up the index Field properties
                Field.Store fieldStore = Field.Store.YES;
                if (store.equals("no"))
                    fieldStore = Field.Store.NO;

                Field.Index fieldIndex = Field.Index.ANALYZED;
                if (type.equals("key") || analyzer.indexOf("KeywordAnalyzer") >= 0)
                    fieldIndex = Field.Index.NOT_ANALYZED;

                prtln("indexCustomFields() for " + xmlFormatOrSchema + " field:" + fieldName);

                // Get the content from the XML at the given xpaths and index it in this field
                List xPaths = field.selectNodes("xpaths/xpath");
                for (Object xp : xPaths) {
                    try {
                        Node xPathElement = (Node) xp;
                        String xPath = xPathElement.getText();

                        List contentNodes = null;

                        // If indexing a custom field from my own document:
                        if (relationPrefix == null) {
                            // Ignore xPaths for related documents:
                            if (xPath.startsWith("/relation."))
                                continue;
                            contentNodes = xmlDoc.selectNodes(xPath);
                            prtln("Indexing custom field '" + fieldName + facetCategory + "'. Found " + contentNodes.size() + " nodes at xPath '" + xPath + "'");
                        }
                        // If indexing a custom field from a related document:
                        else {
                            // Only process xPaths for related documents:
                            if (!xPath.startsWith("/relation."))
                                continue;
                            // Remove the relationPrefix to get the actual xPath within the related document:
                            contentNodes = xmlDoc.selectNodes(xPath.replace(relationPrefix, ""));
                            prtln("Indexing custom field '" + fieldName + facetCategory + "' from a related doc. Found " + contentNodes.size() + " nodes at xPath '" + xPath + "'");
                        }

                        for (Object c : contentNodes) {
                            String indexContent;
                            if (c instanceof String)
                                indexContent = ((String) c).trim();
                            else {
                                Node contentNode = (Node) c;
                                indexContent = contentNode.getText().trim();
                            }

                            // Index the content and preprocess if indicated:
                            String processorFieldOrCategoryName = (isFacetField ? facetCategory : fieldName);
                            IndexFieldPreprocessor indexFieldPreprocessor = xmlIndexerFieldsConfig.getIndexFieldPreprocessor(processorFieldOrCategoryName);
                            if (debug) {
                                String indexFieldPreprocessorName = "[none]";
                                if (indexFieldPreprocessor != null)
                                    indexFieldPreprocessorName = indexFieldPreprocessor.getClass().getName();
                                prtln("Adding configured field to index (isFacetField["+isFacetField+"]):\n  fieldName:" + fieldName + " store:" + fieldStore + " tokenize:" + fieldIndex + " indexFieldPreprocessor:" + indexFieldPreprocessorName + "\n  content:'" + indexContent + "'");
                            }


                            if (indexContent.length() > 0) {
                                String[] contentArray = null;

                                // If we have a preprocessor, use it:
                                if (indexFieldPreprocessor != null)
                                    contentArray = indexFieldPreprocessor.processFieldContent(indexContent, luceneDoc, xmlDoc);

                                    // If no preprocessor, index the content verbatim:
                                else
                                    contentArray = new String[]{indexContent};

                                if (contentArray != null) {
                                    for (int i = 0; i < contentArray.length; i++) {
                                        // We don't want blank values as facets. This was first seen for asn values from PBS.
                                        // They were coming in as http://asn.jesandco.org/resources/1. Which cannot be
                                        // resolved. Therefore don't add them to facets
                                        if (contentArray[i] == null || contentArray[i].equals(""))
                                            continue;
                                        // Process facet fields:
                                        if (isFacetField) {
                                            String pathRoot[] = null;
                                            if (facetCategory.length() > 0) {
                                                pathRoot = new String[]{facetCategory};
                                            }

                                            String pathLeaves[] = null;
                                            if (facetPathDelimeter.length() > 0)
                                                pathLeaves = Pattern.compile(facetPathDelimeter, Pattern.LITERAL).split(contentArray[i]);
                                            else
                                                pathLeaves = new String[]{contentArray[i]};

                                            String[] fullPath = ArrayUtils.addAll(pathRoot, pathLeaves);

                                            if (fullPath != null && fullPath.length > 0) {
                                                // Example new CategoryPath("root","a","f1")
                                                CategoryPath categoryPath = new CategoryPath(fullPath);
                                                //CategoryPath categoryPath = new CategoryPath("root","b","jw1");
                                                prtln("Adding facet CategoryPath: " + categoryPath);
                                                documentWrapper.addCategoryPath(categoryPath);


                                                fieldName = "facet." + facetCategory;
                                                String fieldContent = "";
                                                for (int jj = 0; jj < pathLeaves.length; jj++){
                                                    fieldContent += pathLeaves[jj];
                                                    String facetPath = fieldName + ":" + fieldContent;
                                                    if(!facetPathIndexedMap.containsKey(facetPath)) {
                                                        prtln("Indexing " + facetPath);
                                                        facetPathIndexedMap.put(facetPath,null);
                                                        // Add each facet path segment as stored field for retrieval in search results:
                                                        luceneDoc.add(new Field(fieldName, fieldContent, Field.Store.YES, Field.Index.NOT_ANALYZED));
                                                    }

                                                    if(jj < pathLeaves.length-1)
                                                        fieldContent += ":";
                                                }

                                            }

											/* for(int m = 0; m < SimpleUtils.categories2.length; m++){
												List<CategoryPath> facetList = SimpleUtils.categoryPathArrayToList(SimpleUtils.categories2[m]);
												for(int k = 0; k < facetList.size(); k++) {
													categoryPath = facetList.get(k);
													prtln("Adding facet CategoryPath: " + categoryPath);
													documentWrapper.addCategoryPath(categoryPath);
												}
											} */
                                        }
                                        // Process regular custom fields:
                                        else {
                                            luceneDoc.add(new Field(fieldName, contentArray[i], fieldStore, fieldIndex));
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        throw new Exception("Error indexing custom field:'" + fieldName + "' for format:'" + xmlFormatOrSchema + "': " + e.getMessage());
                    }
                }
            }
        }
    }

    /**
     * Extracts the standard field data from the XML, such as title, description, url, id, boundingBox, if configured.
     *
     * @param xmlFormatOrSchema The xmlFormat or schema
     * @throws Exception If error, provides an appropriate message to display in indexing reports.
     */
    private void extractStandardFields(String xmlFormatOrSchema) throws Exception {

        if (xmlIndexerFieldsConfig == null)
            return;

        // Loop through each field and add the contents of the xPath to the index for that field
        org.dom4j.Document configXmlDoc = xmlIndexerFieldsConfig.getFormatConfig(xmlFormatOrSchema);
        if (configXmlDoc != null) {

            String geoBBNorth = null;
            String geoBBWest = null;
            String geoBBEast = null;
            String geoBBSouth = null;

            List fields = configXmlDoc.selectNodes("/XMLIndexerFieldsConfig/standardFields/standardField");
            prtln("extractStandardFields() for " + xmlFormatOrSchema + " (found " + fields.size() + " XPaths defined)");
            for (int j = 0; j < fields.size(); j++) {
                Node field = (Node) fields.get(j);
                String fieldName = field.valueOf("@name").trim();

                List extractedContent = new ArrayList();

                // Get the content from the XML at the given xpaths and index it in this field
                List xPaths = field.selectNodes("xpaths/xpath");
                for (Object xp : xPaths) {
                    try {
                        Node xPathElement = (Node) xp;
                        String xPath = xPathElement.getText();
                        List contentNodes = xmlDoc.selectNodes(xPath);
                        for (Object c : contentNodes) {
                            String indexContent;
                            if (c instanceof String)
                                indexContent = ((String) c).trim();
                            else {
                                Node contentNode = (Node) c;
                                indexContent = contentNode.getText().trim();
                            }
                            prtln("Adding standard field to index:\n  fieldName:" + fieldName + "\n  content:'" + indexContent + "'");
                            if (indexContent.length() > 0)
                                extractedContent.add(indexContent);
                        }
                    } catch (Exception e) {
                        throw new Exception("Error indexing standard field:'" + fieldName + "' for format:'" + xmlFormatOrSchema + "': " + e.getMessage());
                    }
                }

                // Index the standard fields:
                if (extractedContent.size() > 0) {
                    if (fieldName.equals("id")) {
                        setIds((String[]) extractedContent.toArray(new String[]{}));
                    } else if (fieldName.equals("url")) {
                        setUrls((String[]) extractedContent.toArray(new String[]{}));
                    } else if (fieldName.equals("title")) {
                        String title = "";
                        for (Object str : extractedContent)
                            title += " " + str;
                        setTitle(title.trim());
                    } else if (fieldName.equals("description")) {
                        String description = "";
                        for (Object str : extractedContent)
                            description += " " + str;
                        setDescription(description.trim());
                    } else if (fieldName.equals("geoBBNorth")) {
                        warnIfExceedsMaxOccurances(1, extractedContent.size(), fieldName);
                        geoBBNorth = (String) extractedContent.get(0);
                    } else if (fieldName.equals("geoBBWest")) {
                        warnIfExceedsMaxOccurances(1, extractedContent.size(), fieldName);
                        geoBBWest = (String) extractedContent.get(0);
                    } else if (fieldName.equals("geoBBEast")) {
                        warnIfExceedsMaxOccurances(1, extractedContent.size(), fieldName);
                        geoBBEast = (String) extractedContent.get(0);
                    } else if (fieldName.equals("geoBBSouth")) {
                        warnIfExceedsMaxOccurances(1, extractedContent.size(), fieldName);
                        geoBBSouth = (String) extractedContent.get(0);
                    }
                }
            }

            // If any bounding box data was indicated, index it or print warning message if data error:
            if (geoBBSouth != null || geoBBWest != null || geoBBEast != null || geoBBNorth != null) {
                try {
                    setBoundingBox(new BoundingBox(Double.parseDouble(geoBBNorth), Double.parseDouble(geoBBSouth), Double.parseDouble(geoBBEast), Double.parseDouble(geoBBWest)));
                } catch (NumberFormatException ne) {
                    String id = "";
                    if (ids != null && ids.length > 0)
                        id = " for ID " + ids[0];
                    prtlnErr("Unable to index Bounding Box coordinates" + id + ". Invalid coordinate value: " + ne.getMessage());
                } catch (NullPointerException npe) {
                    String id = "";
                    if (ids != null && ids.length > 0)
                        id = " for ID " + ids[0];
                    prtlnErr("Unable to index Bounding Box coordinates" + id + ". One or more coordinates were null or missing.");
                } catch (Exception e) {
                    String id = "";
                    if (ids != null && ids.length > 0)
                        id = " for ID " + ids[0];
                    prtlnErr("Unable to index Bounding Box coordinates" + id + ": " + e.getMessage());
                }
            }
        }
    }

    /**
     * Extracts the relationship data from the XML including the target ids and urls and relationship type 'isAnnotatedBy'
     * etc. URLs are normalized using the scheme
     * that was used in the NDR and is used in the Handle Resolution Service to generate handles for resource URLs and the NCS
     * to verify URL input fields.
     *
     * @param xmlFormatOrSchema The xmlFormat or schema
     * @throws Exception If error, provides an appropriate message to display in indexing reports.
     */
    private void extractRelationships(String xmlFormatOrSchema) throws Exception {

        if (xmlIndexerFieldsConfig == null)
            return;

        // Loop through each field and add the contents of the xPath to the index for that field
        org.dom4j.Document configXmlDoc = xmlIndexerFieldsConfig.getFormatConfig(xmlFormatOrSchema);
        if (configXmlDoc != null) {

            Map relatedIdsMap = new TreeMap();
            Map relatedUrlsMap = new TreeMap();

            List relatedIds = new ArrayList();
            List relatedUrls = new ArrayList();

            List relationships = configXmlDoc.selectNodes("/XMLIndexerFieldsConfig/relationships/relationship");

            prtln("extractRelationships() for " + xmlFormatOrSchema + " (found " + relationships.size() + " XPaths defined)");
            for (int j = 0; j < relationships.size(); j++) {
                Node relationship = (Node) relationships.get(j);

                String relationName = relationship.valueOf("@name").trim();
                if (relationName == null || relationName.length() == 0)
                    continue;

                // Get the content from the XML at the given xpaths and index it in this field
                List xPaths = relationship.selectNodes("xpaths/xpath");
                for (Object xp : xPaths) {
                    try {
                        Node xPathElement = (Node) xp;
                        String xPath = xPathElement.getText();
                        String type = xPathElement.valueOf("@type");
                        if (type == null || !(type.equalsIgnoreCase("id") || type.equalsIgnoreCase("url")))
                            throw new Exception("XMLIndexerFieldsConfig relationships: xpath type must be either 'id' or 'url' but found: " + type);
                        List contentNodes = xmlDoc.selectNodes(xPath);
                        for (Object c : contentNodes) {
                            String indexContent;
                            if (c instanceof String)
                                indexContent = ((String) c).trim();
                            else {
                                Node contentNode = (Node) c;
                                indexContent = contentNode.getText().trim();
                            }
                            prtln("Adding relation type '" + type + "':\n  relationName:" + relationName + "\n  content:'" + indexContent + "'");

                            Map relationMap;
                            List relationList;
                            if (type.equalsIgnoreCase("id")) {
                                relationMap = relatedIdsMap;
                                relationList = relatedIds;
                            } else {
                                relationMap = relatedUrlsMap;
                                relationList = relatedUrls;
                            }
                            List extractedIds = (List) relationMap.get(relationName);
                            if (extractedIds == null)
                                extractedIds = new ArrayList();

                            if (indexContent.length() > 0) {
                                // Normalize URLs so the relationship is established to the proper record:
                                if (type.equalsIgnoreCase("url")) {
                                    try {
                                        indexContent = UrlHelper.normalize(indexContent);
                                    } catch (Throwable t) {
                                        // If unable to normalize (e.g. java.net.URISyntaxException), leave verbatim
                                    }
                                }
                                extractedIds.add(indexContent);
                                relationList.add(indexContent);
                                relationMap.put(relationName, extractedIds);
                            }
                        }
                    } catch (Exception e) {
                        throw new Exception("Error extracting relationships:'" + relationName + "' for format:'" + xmlFormatOrSchema + "': " + e.getMessage());
                    }
                }
            }

            if (relatedIdsMap.size() > 0)
                setRelatedIdsMap(relatedIdsMap);
            if (relatedUrlsMap.size() > 0)
                setRelatedUrlsMap(relatedUrlsMap);

            if (relatedIds.size() > 0)
                setRelatedIds(relatedIds);
            if (relatedUrls.size() > 0)
                setRelatedUrls(relatedUrls);
        }
    }


    private void warnIfExceedsMaxOccurances(int maxOccurances, int numOccurances, String fieldName) {
        if (numOccurances > maxOccurances) {
            System.out.println("XMLIndexer warning: " + numOccurances + " values for field '" +
                    fieldName + "' were found in the XML instance document but only " +
                    maxOccurances + (maxOccurances == 1 ? "occurance is" : "occurances are") +
                    " allowed. Remaining value(s) were dropped.");
        }
    }

    /**
     * Indexes the default fields title, description, ids, and urls.
     *
     * @param documentWrapper The DocumentWrapper to add fields to
     * @throws Exception If error, provides an appropriate message to display in indexing reports.
     */
    private void indexStandardFields(DocumentWrapper documentWrapper) throws Exception {

        org.apache.lucene.document.Document luceneDoc = documentWrapper.getDocument();

        String title = getTitle();
        if (title != null) {
            luceneDoc.add(new Field("title", title, Field.Store.YES, Field.Index.ANALYZED));
            luceneDoc.add(new Field("titlestems", title, Field.Store.NO, Field.Index.ANALYZED));
            luceneDoc.add(new Field("titlekey", title, Field.Store.NO, Field.Index.NOT_ANALYZED));
        }

        String description = getDescription();
        if (description != null) {
            luceneDoc.add(new Field("description", description, Field.Store.YES, Field.Index.ANALYZED));
            luceneDoc.add(new Field("descriptionstems", description, Field.Store.NO, Field.Index.ANALYZED));
        }

        // Store the resource url(s) for reference
        String[] urls = getUrls();
        if (urls != null && urls.length > 0) {
            luceneDoc.add(new Field("url", urls[0], Field.Store.YES, Field.Index.NOT_ANALYZED));
            String urlterms = SimpleLuceneIndex.encodeToTerm(urls[0]);
            for (int i = 1; i < urls.length; i++) {
                urlterms += " " + SimpleLuceneIndex.encodeToTerm(urls[i]);
                luceneDoc.add(new Field("uri", IndexingTools.tokenizeURI(urls[i]), Field.Store.NO, Field.Index.ANALYZED));
                if (indexDefaultAndStemsField) {
                    String tokenizedURI = IndexingTools.tokenizeURI(urls[i]);
                    IndexingTools.addToDefaultAndStemsFields(luceneDoc, tokenizedURI);
                    IndexingTools.addToAdminDefaultField(luceneDoc, tokenizedURI);
                }
            }
            // The urlenc field allows for wild card searches over URLs:
            luceneDoc.add(new Field("urlenc", urlterms, Field.Store.NO, Field.Index.ANALYZED));
        }

        // Add all of the ids to the index:
        String[] idsEncoded = getIdsEncoded();
        if (idsEncoded != null) {
            for (int i = 0; i < idsEncoded.length; i++) {
                luceneDoc.add(new Field("id", idsEncoded[i], Field.Store.NO, Field.Index.ANALYZED));
                // Remove all IDs in the index that are the same as this one
                //addDocToRemove("id", idsEncoded[i]);
            }
        }

        // Add the id(s) unencoded for reading:
        String[] ids = getIds();

        for (int i = 0; i < ids.length; i++)
            luceneDoc.add(new Field("idvalue", ids[i], Field.Store.YES, Field.Index.NOT_ANALYZED));

        // The geospatial bounding box footprint
        String hasBoundingBox = "false";
        try {
            BoundingBox bb = getBoundingBox();

            if (bb != null) {
                String n = LuceneGeospatialQueryConverter.encodeLatitude(bb.getNorthCoord());
                String s = LuceneGeospatialQueryConverter.encodeLatitude(bb.getSouthCoord());
                String e = LuceneGeospatialQueryConverter.encodeLongitude(bb.getEastCoord());
                String w = LuceneGeospatialQueryConverter.encodeLongitude(bb.getWestCoord());
                luceneDoc.add(new Field("northCoord", n, Field.Store.YES, Field.Index.NOT_ANALYZED));
                luceneDoc.add(new Field("southCoord", s, Field.Store.YES, Field.Index.NOT_ANALYZED));
                luceneDoc.add(new Field("eastCoord", e, Field.Store.YES, Field.Index.NOT_ANALYZED));
                luceneDoc.add(new Field("westCoord", w, Field.Store.YES, Field.Index.NOT_ANALYZED));
                hasBoundingBox = "true";

                //System.out.println("Indexed BoundingBox: " + bb);
            }
        } catch (Throwable e) {
            prtlnErr("Unable to index Bounding Box coordinates: " + e.getMessage());
        }
        luceneDoc.add(new Field("hasBoundingBox", hasBoundingBox, Field.Store.YES, Field.Index.NOT_ANALYZED));
    }


    /**
     * Indexes the content of each element and attribute in the source XML as individual search fields, using
     * the xPath to the element or attribute as the field name. If an xPath field prefix has been indicated it
     * will be inserted at the beginning of the field path.
     *
     * @param documentWrapper The DocumentWrapper to add fields to
     * @throws Exception If error, provides an appropriate message to display in indexing reports.
     * @see #setXPathFieldsPrefix
     */
    public void indexXpathFields(DocumentWrapper documentWrapper) throws Exception {
        String prefix = (xPathFieldsPrefix == null ? "" : xPathFieldsPrefix);
        org.apache.lucene.document.Document luceneDoc = documentWrapper.getDocument();

        // Grab the content and index it in the xPath fields, Elements and Attributes...
        List nodes = xmlDoc.selectNodes("//*");
        if (nodes != null) {
            for (int i = 0; i < nodes.size(); i++) {

                // Index the Elements:
                Node node = ((Node) nodes.get(i));
                if (node instanceof Element) {
                    String text = node.getText().trim();
                    if (text.length() > 0 && luceneDoc != null) {
                        luceneDoc.add(new Field(prefix + "/text/" + node.getPath(), text, Field.Store.YES, Field.Index.ANALYZED));
                        luceneDoc.add(new Field(prefix + "/stems/" + node.getPath(), text, Field.Store.NO, Field.Index.ANALYZED)); // No need to stem: handled by the PerFieldAnalyzer...
                        luceneDoc.add(new Field(prefix + "/key/" + node.getPath(), text, Field.Store.NO, Field.Index.NOT_ANALYZED));
                        luceneDoc.add(new Field("indexedXpaths", node.getPath(), Field.Store.NO, Field.Index.NOT_ANALYZED));

                        //prtln("element field: " + node.getPath() + " value: " + text);
                        if (indexDefaultAndStemsField) {
                            IndexingTools.addToDefaultAndStemsFields(luceneDoc, text);
                            IndexingTools.addToAdminDefaultField(luceneDoc, text);
                        }

                        fullXmlElementContent += " " + text;
                    }

                    // Index the Attributes:
                    List attributes = ((Element) node).attributes();
                    for (int j = 0; j < attributes.size(); j++) {
                        Attribute attribute = (Attribute) attributes.get(j);
                        String attText = attribute.getText().trim();
                        if (attText.length() > 0 && luceneDoc != null) {
                            luceneDoc.add(new Field(prefix + "/text/" + attribute.getPath(), attText, Field.Store.YES, Field.Index.ANALYZED));
                            luceneDoc.add(new Field(prefix + "/stems/" + attribute.getPath(), attText, Field.Store.NO, Field.Index.ANALYZED)); // No need to stem: handled by the PerFieldAnalyzer...
                            luceneDoc.add(new Field(prefix + "/key/" + attribute.getPath(), attText, Field.Store.NO, Field.Index.NOT_ANALYZED));
                            luceneDoc.add(new Field("indexedXpaths", attribute.getPath(), Field.Store.NO, Field.Index.NOT_ANALYZED));

                            if (indexDefaultAndStemsField) {
                                IndexingTools.addToDefaultAndStemsFields(luceneDoc, attText);
                                IndexingTools.addToAdminDefaultField(luceneDoc, attText);
                            }

                            fullXmlAttributeContent += " " + attText;
                        }
                        //prtln("attribute field: " + attribute.getPath() + " value: " + attText);
                    }
                    //prtln("nodeElementName: " + node.getName());
                } else if (node instanceof Attribute) {
                    //prtln("nodeAttributeName: " + ((Attribute)node).getQName());
                } else if (node instanceof Namespace) {
                    //prtln("nodeNamespaceName: " + ((Namespace)node).getName());
                } else {
                    //prtln("node"+node.getClass()+"Name: " + node.getName());
                }
            }
        }
    }

    /**
     * Indexes Java Bean XML that was encoded with the java.beans.XMLEncoder class, using the bean properties
     * as field names. If this is not Java Bean encoded XML, nothing is done, returns false.
     *
     * @param documentWrapper The DocumentWrapper to add fields to
     * @return True if this is a Java Bean and property fields were indexed.
     * @throws Exception If error, provides an appropriate message to display in indexing reports.
     */
    public boolean indexJavaBeanFields(DocumentWrapper documentWrapper) throws Exception {

        Element rootEle = xmlDoc.getRootElement();
        org.apache.lucene.document.Document luceneDoc = documentWrapper.getDocument();

        try {
            Attribute classAtt = rootEle.attribute("class");
            if (!rootEle.getName().equals("java") || classAtt == null || !classAtt.getText().equals("java.beans.XMLDecoder")) {
                //prtln(rootEle.getName() + " is not a Java Bean!");
                return false;
            }
        } catch (Throwable t) {
            return false;
        }

        //System.out.println("XMLIndexer indexJavaBeanFields (true)");

        // Index the bean class name:
        String beanClass = xmlDoc.valueOf("/java/object/@class");
        if (beanClass != null && beanClass.length() > 0)
            luceneDoc.add(new Field("/javaBean.class//key//className", beanClass, Field.Store.YES, Field.Index.NOT_ANALYZED));

        // Boolean to indicate this is a bean:
        luceneDoc.add(new Field("isJavaBean", "true", Field.Store.NO, Field.Index.NOT_ANALYZED));

        boolean fieldsIndexed = false;

        // Index each propery as a separate field:
        List propertyElements = xmlDoc.selectNodes("//void");
        prtln("Found " + propertyElements.size() + " java propery names");
        for (int i = 0; i < propertyElements.size(); i++) {
            Element propertyElement = (Element) propertyElements.get(i);
            if (propertyElement != null) {
                Attribute propertyAtt = propertyElement.attribute("property");
                if (propertyAtt != null) {
                    String propertyName = propertyAtt.getText();
                    List stringElements = propertyElement.selectNodes(".//string");
                    for (int j = 0; j < stringElements.size(); j++) {
                        String value = ((Node) stringElements.get(j)).getText();
                        if (value.trim().length() > 0) {
                            prtln("indexJavaBeanFields() indexing property:" + propertyName + " value: " + value);

                            luceneDoc.add(new Field("/javaBean.property//text//" + propertyName, value, Field.Store.YES, Field.Index.ANALYZED));
                            luceneDoc.add(new Field("/javaBean.property//stems//" + propertyName, value, Field.Store.NO, Field.Index.ANALYZED)); // No need to stem: handled by the PerFieldAnalyzer...
                            luceneDoc.add(new Field("/javaBean.property//key//" + propertyName, value, Field.Store.NO, Field.Index.NOT_ANALYZED));

                            if (indexDefaultAndStemsField) {
                                IndexingTools.addToDefaultAndStemsFields(luceneDoc, value);
                                IndexingTools.addToAdminDefaultField(luceneDoc, value);
                            }

                            fullXmlElementContent += " " + value;

                            fieldsIndexed = true;
                        }
                    }
                }
            }
        }
        return fieldsIndexed;
    }


    private void prtln(String s) {
        if (debug)
            System.out.println("XMLIndexer: " + s);
    }

    private void prtlnErr(String s) {
        System.err.println("XMLIndexer Error: " + s);
    }

}

