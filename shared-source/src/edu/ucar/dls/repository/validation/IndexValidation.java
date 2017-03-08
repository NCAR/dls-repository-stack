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

import java.io.File;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletContext;

import edu.ucar.dls.repository.RepositoryManager;
import edu.ucar.dls.repository.SetInfo;

/**
  Class that creates a SummaryInfo for a repository, sees if it therefore is valid using
  threshold values. And finally sends a summary email to admins. Telling them of success or
  failure
 */
public class IndexValidation {

	/**
	 * Validate the index, and thus creating the summaryInfo file and emailing admins
	 * @param newReposManager
	 * @param currentReposManager
	 * @param servletContext
	 * @return
	 */
	public static boolean validateNewIndex(RepositoryManager newReposManager, 
							RepositoryManager currentReposManager,
							ServletContext servletContext, boolean emailSummary)
	{
		// First create the index summary
		IndexDataBean indexSummary = createIndexDataBean(newReposManager, currentReposManager,
				servletContext);

		// See if the summary file shows thats its a valid repos
		boolean passedThresholdTests = passedThresholdTests(indexSummary);
		
		// Set the status within the bean, 
		if(passedThresholdTests)
			indexSummary.setIndexStatus(IndexDataBean.STATUS_VALID);
		else
			indexSummary.setIndexStatus(IndexDataBean.STATUS_ERROR);
		indexSummary.setContainsThresholdErrors(!passedThresholdTests);


		// Write the index summary to file
		try {
			newReposManager.writeIndexSummary(indexSummary);
		} catch (Exception e) {
			e.printStackTrace();
		}


		HashMap<String, Map<String, IndexDataBean>>  collectionsFacetDetails = FacetsSummary.createCollectionsFacetDetails(getSetSpecs(indexSummary), newReposManager);
		for(Entry<String, Map<String, IndexDataBean>> collectionFacetDetails :collectionsFacetDetails.entrySet())
		{
			IndexDataBean containerBean = new IndexDataBean();
			containerBean.setFacetDetails(collectionFacetDetails.getValue());
			try
			{
				newReposManager.writeCollectionFacetSummary(collectionFacetDetails.getKey(), 
						containerBean);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				//todo
			}
		}
		
		if(emailSummary)
		{
			// Finally email the index summary report to admins
			SummaryEmail.emailIndexDataBean(indexSummary, passedThresholdTests, servletContext);
		}
		
		return passedThresholdTests;
	}

	/*
	 * The universal date format within the index summary file
	 */
	private static String formatDate(Date aDate)
	{
		return new SimpleDateFormat("EEE, dd MMM yyyy HH:mm").format(aDate);
	}
	
	private static List<String> getSetSpecs(IndexDataBean indexSummary)
	{
		List<String> setSpecs = new ArrayList<String>();
		Map<String, Map> collectionDetails = (Map) indexSummary.getCollectionsDetails().get(IndexDataBean.COLLECTIONS);
		for(Map collectionInfo: collectionDetails.values())
			setSpecs.add((String)collectionInfo.get(IndexDataBean.SET_SPEC));
		return setSpecs;
	}
	/*
	 * Create a repositoryInfo Map based on a repository manager
	 */
	private static IndexDataBean createReposInfo(RepositoryManager repositoryManager)
	{
		int records = getTotalIndexedRecords(repositoryManager);
		ArrayList currentCollections = repositoryManager.getSetInfos();
		int collections = 0;
		if(currentCollections!=null)
			collections = currentCollections.size();
		IndexDataBean info = new IndexDataBean();
		info.put(IndexDataBean.STARTED, formatDate(repositoryManager.getCreationDate()));
		info.put(IndexDataBean.FINISHED, formatDate(repositoryManager.getLastModifiedDate()));
		info.put(IndexDataBean.COLLECTIONS, collections);
		info.put(IndexDataBean.RECORDS, records);
		
		File repositoryDirectory = repositoryManager.getRepositoryDirectory();

		info.put(IndexDataBean.INDEX_PATH, repositoryDirectory.toString());
		info.put(IndexDataBean.NAME, repositoryDirectory.getName());

		return info;
	}

	/*
	 * Checks to see if the indexSummary passed all the threshold tests
	 */
	private static boolean passedThresholdTests(IndexDataBean indexSummary)
	{
		return (passedThresholdForRecordRemovals(indexSummary) && 
				passedCollectionCountChangesThreshold(indexSummary));
	}

	/*
	 *	Checks to see if any record removal threshold were maxed out
	 */
	private static boolean passedThresholdForRecordRemovals(IndexDataBean indexSummary) {
		return passedThresholdTest(indexSummary.getCollectionsDetails(), 
				IndexDataBean.PASSED_MAX_THRESHOLD_FOR_RECORD_REMOVALS);	 
	}
	
	/*
	 *	Checks to see if any record removal threshold were maxed out
	 */
	private static boolean passedCollectionCountChangesThreshold(IndexDataBean indexSummary) {
		return passedThresholdTest(indexSummary.getCollectionsDetails(), 
				IndexDataBean.PASSED_MAX_THRESHOLD_FOR_COLLECTION_COUNT_CHANGES);	
	}
	
	
	/**
	 * Validates to see if a summary passed threshold test given a field. If the field
	 * doesn't exist it still passes
	 * @param differencesMap
	 * @param passedField
	 * @return
	 */
	public static boolean passedThresholdTest(Map differencesMap, String passedField)
	{
		if(differencesMap==null || !differencesMap.containsKey(passedField))
			return true;
		else 
			return (Boolean.parseBoolean((String)differencesMap.get(passedField)));
	}

	/*
	 * Creates an index summary bean to be used within the site. This method does not
	 * write the file yet though.
	 */
	private static IndexDataBean createIndexDataBean(RepositoryManager newReposManager, 
							RepositoryManager currentReposManager,
							ServletContext servletContext)
	{
		IndexDataBean summary = new IndexDataBean();
		
		// Add both the new index as well as the prior one to the bean for comparisons
		summary.setIndexDetail(createReposInfo(newReposManager));
		summary.setPriorIndexDetail(createReposInfo(currentReposManager));
		
		
		
		// Figure out record count differences
		int currentTotalRecords = Integer.parseInt((String)summary.getPriorIndexDetail().get(IndexDataBean.RECORDS));
		int newTotalRecords = Integer.parseInt((String)summary.getIndexDetail().get(IndexDataBean.RECORDS));
		int diff = newTotalRecords-currentTotalRecords;
		
		double percent_changed = 1;
		if(currentTotalRecords!=0)
			percent_changed = Math.abs((double)diff/(double)currentTotalRecords);

		IndexDataBean recordDifferences = new IndexDataBean();
		
		recordDifferences.put(IndexDataBean.PERCENT_CHANGED, roundPercentChanged(percent_changed));
		recordDifferences.put(IndexDataBean.COUNT_CHANGED, diff);
		recordDifferences.put(IndexDataBean.RECORDS, newTotalRecords);
		summary.setRecordDetail(recordDifferences);
				
		// Add collecitons details
		summary.setCollectionsDetails(  
				createCollectionDetails(newReposManager, currentReposManager, servletContext));

		// Add facet details
		summary.setFacetDetails(FacetsSummary.createFacetsDetails(newReposManager, currentReposManager ));
		
		return summary;	
	}
	
	/*
	 * Creates the collection details section for index summary. This section not only contains
	 * all collections in repos and counts but also if each collection passed certain thresholds
	 */
	private static IndexDataBean createCollectionDetails(
			RepositoryManager newRepositoryManager, RepositoryManager currentRepositoryManager,
			ServletContext servletContext) {
		
		String maxThresholdForRecordRemovalsString = servletContext.getInitParameter(
			"maxThresholdForRecordRemovals");
		double maxThresholdForRecordRemovals = Double.MAX_VALUE;
		if(maxThresholdForRecordRemovalsString!=null && !maxThresholdForRecordRemovalsString.trim().equals(""))
			maxThresholdForRecordRemovals = Double.parseDouble(maxThresholdForRecordRemovalsString);
		
		String maxThresholdForCollectionCountChangesString = servletContext.getInitParameter(
			"maxThresholdForCollectionCountChanges");
		int maxThresholdForCollectionCountChanges = Integer.MAX_VALUE;
		if(maxThresholdForCollectionCountChangesString!=null && !maxThresholdForCollectionCountChangesString.trim().equals(""))
			maxThresholdForCollectionCountChanges = Integer.parseInt(maxThresholdForCollectionCountChangesString);
		

		ArrayList newCollections = newRepositoryManager.getSetInfos();
		if(newCollections==null)
			newCollections = new ArrayList();
		ArrayList currentCollections = currentRepositoryManager.getSetInfos();

		if(currentCollections==null)
			currentCollections = new ArrayList();
		// Loop through the new collections comparing it to old one. This is thereby figuring
		// out what collections were in both and which ones were added and removed
		ArrayList<IndexDataBean> collections = new ArrayList<IndexDataBean>();
		for(Object newSetInfoObj:newCollections)
		{
			SetInfo newSetInfo = (SetInfo)newSetInfoObj;
			newSetInfo.setSetInfoData(newRepositoryManager);
			// Find this object in current Collections
			SetInfo matchingSetInfo = null;
			for(Object currentSetInfoObj:currentCollections)
			{
				SetInfo currentSetInfo = (SetInfo)currentSetInfoObj;
				if(currentSetInfo.getSetSpec().equals(newSetInfo.getSetSpec()))
				{
					matchingSetInfo=currentSetInfo;
				}
			}
			if(matchingSetInfo!=null)
			{
				// Remove it so after the whole loop is over we know which collections were removed
				currentCollections.remove(matchingSetInfo);
				matchingSetInfo.setSetInfoData(currentRepositoryManager);
			}
			
			// This add will be when the collection was found in both managers and also when
			// its a new collection, and thus matchingSetInfo is null
			collections.add( createCollectionDetail(newSetInfo,matchingSetInfo, maxThresholdForRecordRemovals));
			
		}
		
		// These are the collections that were removed from the new index, which is why we
		// have null as the first argument
		for(Object currentSetInfoObj:currentCollections)
		{
			SetInfo currentSetInfo = (SetInfo)currentSetInfoObj;
			currentSetInfo.setSetInfoData(currentRepositoryManager);
			collections.add( createCollectionDetail(null, currentSetInfo, maxThresholdForRecordRemovals));

		}
		
		// Sort the collections
		Collections.sort(collections, new Comparator<IndexDataBean>() {

            public int compare(IndexDataBean m1, IndexDataBean m2) {
                return (((String)m1.get(IndexDataBean.NAME)).compareTo((String)m2.get(IndexDataBean.NAME)));
            }
        });
		
		// Now calcualte the totals
		int addedCount = 0;
		int removedCount = 0;
		int count=1;
		boolean passedMaxThresholdForRecordRemovals = true;
		Map<String, Map> collectionDetails = new HashMap<String, Map>();
		NumberFormat numberFormat = NumberFormat.getNumberInstance();
		numberFormat.setMinimumIntegerDigits(4);
		numberFormat.setGroupingUsed(false);
		
		// Create the sorted hashmap by adding a number after the collection prefix. And thus
		// by letting us sort this map by key later on
		for(IndexDataBean collectionSummary: collections)
		{
			// If one collection failes the threshold test then the collections in general does
			passedMaxThresholdForRecordRemovals &= passedThresholdTest(collectionSummary, 
													IndexDataBean.PASSED);
			// Keep track of how many collections were addded and removed
			if(collectionSummary.containsKey(IndexDataBean.ADDED))
				addedCount++;
			if(collectionSummary.containsKey(IndexDataBean.REMOVED))
				removedCount++;
			;
			String collectionNumber = numberFormat.format(count);
			collectionDetails.put(IndexDataBean.COLLECTION+collectionNumber, collectionSummary);
			count++;
		}
		
		// Finally add all the calculated stuff to the map
		IndexDataBean collectionsDetails = new IndexDataBean(); 
		collectionsDetails.put(IndexDataBean.COLLECIONS_ADDED, addedCount);
		collectionsDetails.put(IndexDataBean.COLLECIONS_REMOVED, removedCount);
		
		boolean passedMaxThresholdForCollectionCountChanges = true;
		if(addedCount>maxThresholdForCollectionCountChanges || removedCount>maxThresholdForCollectionCountChanges)
			passedMaxThresholdForCollectionCountChanges = false;
		
		collectionsDetails.put(IndexDataBean.PASSED_MAX_THRESHOLD_FOR_COLLECTION_COUNT_CHANGES,
						passedMaxThresholdForCollectionCountChanges);
		collectionsDetails.put(IndexDataBean.PASSED_MAX_THRESHOLD_FOR_RECORD_REMOVALS,
				passedMaxThresholdForRecordRemovals);
		
		collectionsDetails.put(IndexDataBean.MAX_THRESHOLD_FOR_RECORD_REMOVALS, 
				-maxThresholdForRecordRemovals);
		collectionsDetails.put(IndexDataBean.MAX_THRESHOLD_FOR_COLLECTION_COUNT_CHANGES, 
				maxThresholdForCollectionCountChanges);
		
		collectionsDetails.put(IndexDataBean.COLLECTIONS, collectionDetails);
		
		return collectionsDetails;
	}

	private static int getTotalIndexedRecords(RepositoryManager repositoryManager)
	{
		int totalCount = 0;
		ArrayList<SetInfo> setInfos = repositoryManager.getSetInfos();
		if(setInfos == null)
            return 0;
        for(SetInfo setInfo:setInfos)
		{
			setInfo.setSetInfoData(repositoryManager);
			totalCount+=setInfo.getNumIndexedInt();
		}
		return totalCount;
	}
	
	/*
	 * Create a collection detail comparing the new set info with the current set info.
	 * In this method currentSetInfo can be null or newSetInfo can be null. Thereby letting
	 * the method now it was a add or delete collection respectively
	 */
	private static IndexDataBean createCollectionDetail(SetInfo newSetInfo, SetInfo currentSetInfo, 
			double maxThresholdForRecordRemovals)
	{
		int recordDifference = 0;
		double percent_changed = 1;
		int recordCount = 0;
		
		SetInfo activeSetInfo = newSetInfo;
		
		IndexDataBean collectionDetail = new IndexDataBean();
		
		// If both are not null compare the numbers and decide if it didn't meet threshold
		if(newSetInfo!=null && currentSetInfo!=null)
		{
			recordDifference = newSetInfo.getNumIndexedInt()-currentSetInfo.getNumIndexedInt();
			percent_changed = (double)recordDifference/(double)currentSetInfo.getNumIndexedInt();
			if(recordDifference<0)
				collectionDetail.put(IndexDataBean.PASSED, (-1*maxThresholdForRecordRemovals)<percent_changed);
			recordCount = newSetInfo.getNumIndexedInt();
		}
		else if(newSetInfo==null)
		{
			// If newSetInfo is null is a collection deletion, mark it as such. Giving negative
			// numbers to everything
			recordDifference = -currentSetInfo.getNumIndexedInt();
			percent_changed = -1;
			collectionDetail.put(IndexDataBean.REMOVED, true);
			activeSetInfo = currentSetInfo;
		}
		else
		{
			// If currentSetInfo is null its a collection addition, mark it as such.
			// 100% increase
			recordDifference = newSetInfo.getNumIndexedInt();
			collectionDetail.put(IndexDataBean.ADDED, true);
			percent_changed = 1;
			recordCount = recordDifference;
		}
		
		// Put calculated fields in map and return
		collectionDetail.put(IndexDataBean.NAME, activeSetInfo.getName());
		collectionDetail.put(IndexDataBean.SET_SPEC, activeSetInfo.getSetSpec());
		collectionDetail.put(IndexDataBean.RECORDS, recordCount);
		collectionDetail.put(IndexDataBean.COUNT_CHANGED, recordDifference);
		collectionDetail.put(IndexDataBean.PERCENT_CHANGED, roundPercentChanged(percent_changed));
		
		return collectionDetail;
	}
	
	/*
	 * Helper method for rounding the double correctly
	 */
	private static double roundPercentChanged(double d) {
        return (double)Math.round(d * 100) / 100;
	}
}
