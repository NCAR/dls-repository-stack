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
package edu.ucar.dls.repository.action.form;


import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  Bean for sending attributes to the repository details page
 */
public final class RepositoryAnalyticsForm extends RepositoryForm implements Serializable {

	public static String COLLECTION = "Collection";
	public static String ALL = "all";
	
	private Map<String, Map<String, String>> dataTypes = null;
	private Map<String, String> collections = null;
	private String dataType = COLLECTION;
	private String[] selectedCollections = null;
	private String[] selectedDataTypeOptions = null;
	

	private String collectionSelectionOption = ALL;
	private String dataTypeSelectionOption = ALL;
	
	private String fromDate = null;
	private String toDate = null;
	private List<String> includedFacetResultTypes = null;
	private boolean lineLabels =false;
	private boolean cumulative = true;
	
	private HashMap<String, List<HashMap<String, String>>> graphData = null;

	public Map<String, Map<String, String>> getDataTypes() {
		return dataTypes;
	}

	public void setDataTypes(Map<String, Map<String, String>> dataTypes) {
		this.dataTypes = dataTypes;
	}


	public HashMap<String, List<HashMap<String, String>>> getGraphData() {
		return graphData;
	}

	public void setGraphData(
			HashMap<String, List<HashMap<String, String>>> graphData) {
		this.graphData = graphData;
	}

	

	public boolean isCumulative() {
		return cumulative;
	}

	public void setCumulative(boolean cumulative) {
		this.cumulative = cumulative;
	}

	

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String selectedDataType) {
		this.dataType = selectedDataType;
	}

	public Map<String, String> getCollections() {
		return collections;
	}

	public void setCollections(Map<String, String> collections) {
		this.collections = collections;
	}
	public String[] getSelectedCollections() {
		return selectedCollections;
	}

	public void setSelectedCollections(String[] selectedCollections) {
		this.selectedCollections = selectedCollections;
	}
	
	public String[] getSelectedDataTypeOptions() {
		return selectedDataTypeOptions;
	}

	public void setSelectedDataTypeOptions(String[] selectedDataTypeOptions) {
		this.selectedDataTypeOptions = selectedDataTypeOptions;
	}

	public String getCollectionSelectionOption() {
		return collectionSelectionOption;
	}

	public void setCollectionSelectionOption(String collectionSelectionOption) {
		this.collectionSelectionOption = collectionSelectionOption;
	}

	public String getDataTypeSelectionOption() {
		return dataTypeSelectionOption;
	}

	public void setDataTypeSelectionOption(String dataTypeSelectionOption) {
		this.dataTypeSelectionOption = dataTypeSelectionOption;
	}
	public String getFromDate() {
		return fromDate;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}
	public String getToDate() {
		return toDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
	}
	public List<String> getIncludedFacetResultTypes() {
		return includedFacetResultTypes;
	}

	public void setIncludedFacetResultTypes(List<String> includedFacetResultTypes) {
		this.includedFacetResultTypes = includedFacetResultTypes;
	}
	public boolean isLineLabels() {
		return lineLabels;
	}

	public void setLineLabels(boolean lineLabels) {
		this.lineLabels = lineLabels;
	}
}


