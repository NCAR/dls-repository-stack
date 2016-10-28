package edu.ucar.dls.harvestmanager;


import java.util.ArrayList;
import java.util.List;

/**
 *  DAO for collections
 */
public class HarvestCollectionDAO implements Comparable<HarvestCollectionDAO>{
	private String title = null;
	private String frequency = null;
	private String libraryFormat = null;
	private String setSpec = null;
	private String collectionId = null;
	private String baseHarvestURL= null;
	private String collectionHandle = null;
	private String url= null;
	private String description = null;
	private String metadataPrefix = null;
	private String imageHeight = null;
	private String brandURL = null;
	private String ingestProtocol = null;
	private String larReadiness = null;
    private List<String> viewContexts = new ArrayList<String>();
	
	public HarvestCollectionDAO(
			String ingestProtocol,
            String collectionId,
            String collectionHandle,
            String title,
			String setSpec,
            String frequency,
			String libraryFormat,
            String baseHarvestURL,
            List<String> viewContexts,
            String url,
            String description,
			String imageHeight,
            String brandURL,
            String larReadiness
    )
	{
		if(frequency==null)
			frequency = "0";
		this.collectionId = collectionId;
		this.collectionHandle = collectionHandle;
		this.title = title;
		this.setSpec = setSpec;
		this.frequency = frequency;
		this.libraryFormat = libraryFormat;
		this.baseHarvestURL = baseHarvestURL;
        this.viewContexts = viewContexts;
		this.url = url;
		this.description = description;
		this.imageHeight = imageHeight;
		this.brandURL = brandURL;
		this.ingestProtocol = ingestProtocol;
		this.larReadiness = larReadiness;
	}
	
	public String getLarReadiness() {
		return larReadiness;
	}

	public String getCollectionHandle() {
		return collectionHandle;
	}
	public String getSetSpec() {
		return setSpec;
	}
	public String getTitle() {
		return title;
	}
    public List<String> getViewContexts() {
        return viewContexts;
    }
	public String getFrequency() {
		return frequency;
	}
	public String getLibraryFormat() {
		return libraryFormat;
	}

	public int compareTo(HarvestCollectionDAO arg0) {
		return this.title.compareTo(arg0.getTitle());
	}
	public String getCollectionId() {
		return collectionId;
	}
	
	public String getMetadataPrefix() {
		return metadataPrefix;
	}
	public void setMetadataPrefix(String metadataPrefix) {
		this.metadataPrefix = metadataPrefix;
	}
	public String getBaseHarvestURL() {
		return baseHarvestURL;
	}
	public String getUrl() {
		return url;
	}
	public String getDescription() {
		return description;
	}
	public String getImageHeight() {
		return imageHeight;
	}

	public String getBrandURL() {
		return brandURL;
	}
	
	public String getIngestProtocol() {
		return ingestProtocol;
	}

}

