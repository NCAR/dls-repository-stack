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
package edu.ucar.dls.repository.validation;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 Bean that is used for storing metadata about the repository. This bean is created once an
 index finishes and then written into the repos dir itself so the site can see stats. This
 Bean is used for exporting and importing repositories as well as repository maintenance. 
 All repos should contain one, otherwise it will not be able to be used for anything except
 current index
 */
@JacksonXmlRootElement(localName = "index")
public class IndexDataBean extends HashMap{

	// Used within to house different types of stats
	private static String PRIOR_INDEX_INFO = "prior_index_detail";
	private static String INDEX_INFO = "index_detail";
	public static String REMOVED = "removed";
	public static String ADDED = "added";
	private static String STATUS = "status";
	private static String CONTAINS_THRESHOLD_ERRORS = "contains_threshold_errors";
	
	// Used by both
	public static String COLLECTION = "collection";
	public static String COLLECTIONS = "collections";
	public static String RECORD_DETAIL = "record_detail";
	public static String COLLECTIONS_DETAILS = "collections_details";
	
	public static String COLLECIONS_ADDED = "collections_added";
	public static String COLLECIONS_REMOVED = "collections_removed";
	
	public static String PERCENT_CHANGED = "percent_changed";
	public static String MAX_THRESHOLD_FOR_RECORD_REMOVALS = "max_threshold_for_record_removals";
	public static String MAX_THRESHOLD_FOR_COLLECTION_COUNT_CHANGES = "max_threshold_for_collection_count_changes";
	
	public static String PASSED_MAX_THRESHOLD_FOR_RECORD_REMOVALS = "passed_all_max_threshold_for_record_removals";
	public static String PASSED_MAX_THRESHOLD_FOR_COLLECTION_COUNT_CHANGES = "passed_max_threshold_for_collection_count_changes";

	public static String PASSED = "passed_test";
	public static String STARTED = "started";
	public static String FINISHED = "finished";
	public static String COUNT_CHANGED = "count_changed";

	public static String INDEX_PATH = "path";
	public static String NAME = "name";
	public static String RECORDS = "records";
	public static String SET_SPEC = "set_spec";
	
	// Statuses for an index
	public static String STATUS_VALID = "valid";
	public static String STATUS_ERROR = "error";
	public static String STATUS_IGNORE = "ignore";
	
	// Used specifically for facets
	public static String LABEL = "label";
	public static String FACET = "facet";
	public static String RESULT = "result";
	public static String RESULTS = "results";
	public static String FACETS_DETAILS = "facets_details";
	public static String SORTED_BY = "sorted_by";
	
	/**
	 * The next three overriden methods are used to make sure that index summary
	 * is the same when used when created and used when written and then read again.
	 * The difference comes back when this bean is read in by jackson all fields are Strings
	 * Therefore right off the bat we want all values as Strings, No primitives 
	 */
	
	/**
	 * Overwritten method to make sure when ints are put into the map they
	 * are strings
	 * @param key
	 * @param int i
	 */
	
	public void put(String key, int i)
	{
		super.put(key, String.valueOf(i));
	}
	
	/**
	 * Overwritten method to make sure when ints are put into the map they
	 * are strings
	 * @param key
	 * @param int i
	 */
	public void put(String key, double d)
	{
		super.put(key, String.valueOf(d));
	}
	
	/**
	 * Overwritten method to make sure when booleans are put into the map they
	 * are strings
	 * @param key
	 * @param int i
	 */
	public void put(String key, boolean b)
	{
		super.put(key, String.valueOf(b));
	}
	
	/**
	 * The rest of these methods are helper methods so the classes that use this class
	 * do not need to know what params to use getting and setting
	 */
    @JsonIgnore
    public void setPriorIndexDetail(IndexDataBean priorIndexDetail)
    {
    	this.put(PRIOR_INDEX_INFO, priorIndexDetail);
    }
    @JsonIgnore
    public void setIndexDetail(IndexDataBean indexDetail)
    {
    	this.put(INDEX_INFO, indexDetail);
    }
   
	@JsonIgnore
	public void setRecordDetail(IndexDataBean recordDetail) {
		this.put(RECORD_DETAIL, recordDetail);	
	}
	
	@JsonIgnore
	public Map getRecordDetail() {
		return (Map)this.get(RECORD_DETAIL);
	}
		
	@JsonIgnore
	public void setCollectionsDetails(IndexDataBean colectionsDetail) {
		this.put(COLLECTIONS_DETAILS, colectionsDetail);
	}
	@JsonIgnore
	public void setFacetDetails(Map facetsDetails) {
		this.put(FACETS_DETAILS, facetsDetails);
	}
	
	@JsonIgnore
	public Map getFacetDetails() {
		return (Map)this.get(FACETS_DETAILS);
	}
	
	public Map getCollectionsDetails() {
		return (Map)this.get(COLLECTIONS_DETAILS);
	}	
	
	@JsonIgnore
	public Map getIndexDetail()
	{
		return (Map)this.get(INDEX_INFO);
	}
	@JsonIgnore
	public Map getPriorIndexDetail()
	{
		return (Map)this.get(PRIOR_INDEX_INFO);
	}
	@JsonIgnore
	public void setIndexStatus(String status) {
		getIndexDetail().put(STATUS, status);
	}
	
	@JsonIgnore
	public String getIndexStatus() {
		return (String)getIndexDetail().get(STATUS);
	}
	
	@JsonIgnore
	public void setContainsThresholdErrors(boolean containsThresholdErrors) {
		getIndexDetail().put(CONTAINS_THRESHOLD_ERRORS, containsThresholdErrors);
		
	}
	
	/**
	 * Sorts the collections inside collections details by map key
	 * @return
	 */
	public Map<String, Map> getSortedCollections() {
		Map<String, Map> collections = (Map<String, Map>)getCollectionsDetails().get(
				IndexDataBean.COLLECTIONS);
		return new TreeMap(collections);
	}

}

