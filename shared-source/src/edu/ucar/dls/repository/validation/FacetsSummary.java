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

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.facet.search.params.CountFacetRequest;
import org.apache.lucene.facet.search.params.FacetRequest;
import org.apache.lucene.facet.search.params.FacetRequest.ResultMode;
import org.apache.lucene.facet.search.results.FacetResult;
import org.apache.lucene.facet.search.results.FacetResultNode;
import org.apache.lucene.facet.taxonomy.CategoryPath;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import edu.ucar.dls.index.ResultDocList;
import edu.ucar.dls.index.SimpleLuceneIndex;
import edu.ucar.dls.repository.RepositoryManager;
import edu.ucar.dls.serviceclients.asn.AsnResolutionServiceClient;
import edu.ucar.dls.standards.asn.AsnStandardsTextFieldPreprocessor;
import edu.ucar.dls.standards.asn.AsnConstants;


/**
 Helper class that creates a facets detail to be used in Index Summary xml. This facets detail
 will contain all facets that are in the repository with special consideration for ASNStandardID.
 ASNStandardID facet will only contain the top level categories and counts since there are so many
 */

public class FacetsSummary {
	public static String ASN_FACET_NAME = "ASNStandardID";
	
	/**
	 * Method that creates a facets summary for both the old repository manager and the new repository manager
	 * then merges them into one. Showing changes between them. THis will be exactly the same syntax and form
	 * if you just created a facets details for one manager except that it also contains extra fields, percent_changed
	 * count_changed
	 * @param newRepositoryManager
	 * @param currentRepositoryManager
	 * @return
	 */
	public static Map<String, IndexDataBean> createFacetsDetails(
			RepositoryManager newRepositoryManager,
			RepositoryManager currentRepositoryManager
			)
	{
		Map<String, IndexDataBean> newFacetsDetails = createFacetsDetails(newRepositoryManager, new MatchAllDocsQuery());
		Map<String, IndexDataBean> currentFacetsDetails = createFacetsDetails(currentRepositoryManager, new MatchAllDocsQuery());

		return mergeFacetsDetails(newFacetsDetails, currentFacetsDetails, IndexDataBean.LABEL);
	}
	
	public static HashMap<String, Map<String, IndexDataBean>> createCollectionsFacetDetails(List<String> setSpecs, 
			RepositoryManager rm)
	{
		HashMap<String, Map<String, IndexDataBean>> collectionFacetDetails = 
				new HashMap<String, Map<String, IndexDataBean>>();
		for(String setSpec:setSpecs)
		{
			Term setSpecTerm = new Term("ky", setSpec);
			collectionFacetDetails.put(setSpec, createFacetsDetails(rm, 
					new TermQuery(setSpecTerm)));
		}
		return collectionFacetDetails;
	}

	
	/*
	 * Merge two facet detail structues. comparing the two and adding changed fields
	 */
	private static Map<String, IndexDataBean> mergeFacetsDetails(
			Map<String, IndexDataBean> newFacetsDetails,
			Map<String, IndexDataBean> currentFacetsDetails,
			String sortBy) {
		
		List<IndexDataBean> mergedFacetList = new ArrayList<IndexDataBean>();
		
		// Since this method is going to use recursion to both create the details for the overview facets and 
		// for the results of each facet, we use keyToMapOn. To make it so we can label the correct way
		String keyToMapOn = IndexDataBean.RESULT;
		
		Collection<IndexDataBean>newFacets = null; 
		Collection<IndexDataBean>currentFacets = null; 
		
		// If null is sent in for newFacets or current facets create an empty array so we can
		// treat it like nothing was in there
		if(newFacetsDetails!=null)
			newFacets = newFacetsDetails.values();
		else
			newFacets = new ArrayList<IndexDataBean>();
		
		if(currentFacetsDetails!=null)
			currentFacets = currentFacetsDetails.values();
		else
			currentFacets = new ArrayList<IndexDataBean>();
		
		/*Loop through all the new facets trying to make match to the current facets so we can compare
		 * numbers
		 */
		for(IndexDataBean newFacet:newFacets)
		{
			String label = (String)newFacet.get(IndexDataBean.LABEL);
			// Find this object in current Collections
			IndexDataBean matchingFacet = null;
			for(IndexDataBean currentFacet:currentFacets)
			{
				if(label.equals((String)currentFacet.get(IndexDataBean.LABEL)))
				{
					matchingFacet=currentFacet;
				}
			}
			String newRecordsString = (String)newFacet.get(IndexDataBean.RECORDS);
			IndexDataBean facetDetail = new IndexDataBean();
			int newRecords = Integer.parseInt(newRecordsString);
			int countChanged = 0;
			double percentChanged = 0;
			
			if(matchingFacet!=null)
			{
				// If there was matching facet from the current repos, create the changed amounts
				int currentRecords = Integer.parseInt((String)matchingFacet.get(IndexDataBean.RECORDS));
				countChanged = newRecords-currentRecords;
				percentChanged = (double)countChanged/(double)currentRecords;
				
				// remove the matching facet so in the end we know which ones were in currentFacets but not in 
				// the new one
				currentFacets.remove(matchingFacet);
			}
			else
			{
				// Else its an add. The changed count is the same as the record count and the percent changed
				// is 100%
				facetDetail.put(IndexDataBean.ADDED, true);
				countChanged = newRecords;
				percentChanged = 1.0;
			}
			// Add all the fields to the new facet merged detail
			facetDetail.put(IndexDataBean.LABEL, label);
			facetDetail.put(IndexDataBean.RECORDS, newRecordsString);
			facetDetail.put(IndexDataBean.COUNT_CHANGED, countChanged);
			facetDetail.put(IndexDataBean.PERCENT_CHANGED, roundPercentChanged(percentChanged));
			// If it contains name add the name also to the new facet. This name is more descriptive then the
			// label, which is shown instead of the label to the user. Example is asns, we want to show the resolved name
			if(newFacet.containsKey(IndexDataBean.NAME))
				facetDetail.put(IndexDataBean.NAME, newFacet.get(IndexDataBean.NAME));
			
			// If it contains results, recurse into it
			if(newFacet.containsKey(IndexDataBean.RESULTS))
			{
				keyToMapOn = IndexDataBean.FACET;
				Map<String, IndexDataBean> newResults = (Map<String, IndexDataBean>)newFacet.get(IndexDataBean.RESULTS);
				Map<String, IndexDataBean> currentResults = null;
				
				if(matchingFacet!=null && matchingFacet.containsKey(IndexDataBean.RESULTS))
					currentResults = (Map<String, IndexDataBean>)matchingFacet.get(IndexDataBean.RESULTS);
				
				// The sort by will be label unless something else was sent in. Might be NAME also
				String sortedBy = IndexDataBean.LABEL;
				if(newFacet.containsKey(IndexDataBean.SORTED_BY))
					sortedBy = (String)newFacet.get(IndexDataBean.SORTED_BY);
				
				facetDetail.put(IndexDataBean.RESULTS, mergeFacetsDetails(newResults, currentResults, sortedBy));
			}
			mergedFacetList.add(facetDetail);
		}
		
		
		// Now do the same as above but for the facets that are in current but not in new facets. This list is
		// just the difference because we were removing the facets as they were found above
		for(IndexDataBean currentFacet:currentFacets)
		{
			int records = Integer.parseInt((String)currentFacet.get(IndexDataBean.RECORDS));
			
			// Changed values are all negative since they were removed
			currentFacet.put(IndexDataBean.COUNT_CHANGED, -records);
			currentFacet.put(IndexDataBean.PERCENT_CHANGED, -1);
			currentFacet.put(IndexDataBean.REMOVED, true);
			if(currentFacet.containsKey(IndexDataBean.RESULTS))
			{
				// Same as above recurse into the results
				keyToMapOn = IndexDataBean.FACET;
				Map<String, IndexDataBean> results = (Map<String, IndexDataBean>)currentFacet.get(IndexDataBean.RESULTS);
				String sortedBy = IndexDataBean.LABEL;
				if(currentFacet.containsKey(IndexDataBean.SORTED_BY))
					sortedBy = (String)currentFacet.get(IndexDataBean.SORTED_BY);
				currentFacet.put(IndexDataBean.RESULTS, mergeFacetsDetails(null, results, sortedBy));

			}
			currentFacet.put(IndexDataBean.RECORDS, 0);
			mergedFacetList.add(currentFacet);	
		}
		
		
		// Finally order them by the sortBy value and return 
		return convertToOrderedMap(
				mergedFacetList, sortBy, keyToMapOn);
	}
	
	/*
	 * Inner method that round the percent changed in a global way
	 */
	private static double roundPercentChanged(double d) {
        return (double)Math.round(d * 100) / 100;
	}
	
	
	
	/**
	 * Create facet details map
	 * @param rm
	 * @return
	 */
	private static Map<String, IndexDataBean> createFacetsDetails(RepositoryManager rm, Query docQuery)
	{
		List<FacetRequest> facetRequests = new ArrayList<FacetRequest>();
		
		Map facetCategoryDelimiterMap = rm.getXmlIndexerFieldsConfig().getFacetCategoryDelimiterMap();
		Set<String> facets = facetCategoryDelimiterMap.keySet();
		for(String facetCategory: facets)
		{
			// Note the count needs to be really high to pull back all for ASNStandardID facet. 
			// Since we want to make sure that if a high level category has a super low number
			// Its still found. Therefore we are pulling back all but only using the top levels
			FacetRequest facetRequest = new CountFacetRequest(
					new CategoryPath(facetCategory), 100000);
			facetRequest.setDepth(1000);
			facetRequests.add(facetRequest);
			
			// ASNStandardID need to be treated as a tree so its easy for us to use the top levels
			// All other facets will stay flat since they are all on the same level anyway
			if(facetCategory.equals(ASN_FACET_NAME))
				facetRequest.setResultMode(ResultMode.PER_NODE_IN_TREE);
		}

		SimpleLuceneIndex index = rm.getIndex();
		ResultDocList resultDocList = index.searchDocs(docQuery, null, null, null, facetRequests);
		List<IndexDataBean> facetsDetailsList = new ArrayList<IndexDataBean>();
		if(resultDocList.getFacetResults()!=null)
		{
			for(FacetResult result: resultDocList.getFacetResults())
			{
				facetsDetailsList.add(createFacetDetail(result));
			}
		}
		
		// Sort and number the facet list, thus it will become an ordered hashMap
		Map<String, IndexDataBean> facetsDetails = convertToOrderedMap(
				facetsDetailsList, IndexDataBean.LABEL, IndexDataBean.FACET);
		
		return facetsDetails;

	}
	
	/*
	 * Create a facet detail for a facet result. This detail is in the form
	 * {label:aLabel;records:recordCount;results:{result1:{label:aLabel;records:recordCount},
	 * result2:{label:aLabel;records:recordCount}, etc...}}
	 * }}}
	 */
	private static IndexDataBean createFacetDetail(FacetResult facetResult) {
		FacetResultNode resultNode = facetResult.getFacetResultNode();
		IndexDataBean facetDetail = new IndexDataBean();
		
		String facetName = resultNode.getLabel().toString();
		facetDetail.put(IndexDataBean.LABEL, facetName);
		facetDetail.put(IndexDataBean.RECORDS, (int)resultNode.getValue());
		
		List<IndexDataBean> facetList = new ArrayList<IndexDataBean>();
		
		// Default sort field is label, if name is found its changed then
		String sortField = IndexDataBean.LABEL;
		
		// Loop through all the results creating a list
		for(FacetResultNode subResultNode: resultNode.getSubResults())
		{
			IndexDataBean facet = new IndexDataBean();
			String label = subResultNode.getLabel().lastComponent();
			facet.put(IndexDataBean.LABEL, label);
			facet.put(IndexDataBean.RECORDS, (int)subResultNode.getValue());
			
			// Possibly to come up with a name for the label. This would be 
			// good time to make the label displable. ie ASNStandardID to 
			// its real name
			String labelName = getLabelName(facetName, label);
			if(labelName!=null)
			{
				// If name is able to be derived from the label add that to the facet 
				// And use name as the sort field
				facet.put(IndexDataBean.NAME, labelName);
				sortField = IndexDataBean.NAME;
			}
			facetList.add(facet);
		}
		
		facetDetail.put(IndexDataBean.SORTED_BY, sortField);
		// Convert the results to a ordered hashMap and add it to the detail map
		facetDetail.put(IndexDataBean.RESULTS, 
				convertToOrderedMap(facetList, sortField, IndexDataBean.RESULT));
		
		return facetDetail;
	}
	
	/*
	 * Resolve the label if applicable
	 */
	private static String getLabelName(String facetName, String label) {
		if(facetName.equals(ASN_FACET_NAME))
		{
			// Benchmarks for Science Literacy have to editions, in order to distinquish them we are 
			// hard coding their values with version
			if(label.equals("D2365735"))
				return "Benchmarks for Science Literacy 2008 version";
			else if(label.equals("D1000152"))
				return "Benchmarks for Science Literacy 1993 version";
			
			// Otherwise use the resolver to figure out what the names are
			
			// We can resolve the asn id using the resolver to figure out the name
			try {
				String asnId = AsnConstants.ASN_ID_BASE+label;
				return AsnStandardsTextFieldPreprocessor.getAsnResolutionServiceClient().getAsnTextIndexFieldValue(asnId);
			} catch (Exception e) {
				
				e.printStackTrace();
				return label;
			}
		}
		return null;
	}

	/*
	 * Convert a list to a ordered map sorting by key value. This ordered map is done by making
	 * the keys have a numerical part and thus by making the keys sortable later on
	 */
	private static IndexDataBean convertToOrderedMap(List<IndexDataBean> indexSummarylist, String sortKey,
			String prefix)
	{
		class IndexDataBeanComparator implements Comparator<IndexDataBean>{
			private String key=null;
			public IndexDataBeanComparator(String key)
			{
				this.key = key;
			}
			public int compare(IndexDataBean m1, IndexDataBean m2) {
                return (((String)m1.get(key)).compareTo((String)m2.get(key)));
            }
		}
		
		Collections.sort(indexSummarylist, new IndexDataBeanComparator(sortKey));
		
		IndexDataBean orderedMap = new IndexDataBean();
		int count=1;
		NumberFormat numberFormat = NumberFormat.getNumberInstance();
		numberFormat.setMinimumIntegerDigits(3);
		numberFormat.setGroupingUsed(false);
		for(IndexDataBean indexSummary: indexSummarylist)
		{
			String facetNumber = numberFormat.format(count);
			orderedMap.put(prefix+facetNumber, indexSummary);
			count++;
		}
		return orderedMap;
	}
}
