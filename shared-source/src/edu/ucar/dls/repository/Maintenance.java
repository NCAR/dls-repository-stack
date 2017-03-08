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
package edu.ucar.dls.repository;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryWrapperFilter;
import edu.ucar.dls.dds.RepositoryInfoBean;
import edu.ucar.dls.index.FileIndexingService;
import edu.ucar.dls.index.SimpleLuceneIndex;
import edu.ucar.dls.index.writer.FileIndexingPlugin;
import edu.ucar.dls.index.writer.ServletContextFileIndexingPlugin;
import edu.ucar.dls.propertiesmgr.PropertiesManager;
import edu.ucar.dls.repository.indexing.IndexingManager;
import edu.ucar.dls.repository.validation.IndexDataBean;
import edu.ucar.dls.repository.validation.IndexValidation;
import edu.ucar.dls.util.Beans;
import edu.ucar.dls.util.Files;
import edu.ucar.dls.vocab.MetadataVocab;
import edu.ucar.dls.vocab.VocabNode;
import edu.ucar.dls.webapps.tools.GeneralServletTools;
import edu.ucar.dls.xml.XMLConversionService;

/**
 Class that is initialized by the servelet directly and is used to alter the current
 indexes that are being served by the servelet.
 */
public final class Maintenance {
	private boolean debug = true;
	private ServletContext servletContext = null;
	private Object indexingOperationsLock = new Object();
	private String importRepositoryDestinationString = "";
	
	private boolean isBackgroundIndex = false;
	private boolean isForegroundIndex = false;
	private boolean isImportIndex = false;
	private boolean doPerformContinuousIndexing = false;
	private String repositoryConfigDirPath = null;
	private String defaultSearchFieldName = null;
	private String ddsContext = "unknown";
	private boolean indexDisabledDueToActionRequired = false;
	
	// Name of the default field used by the query parser:
	private final String DEFAULT_SEARCH_FIELD = "default";
	
	// Number of valid index backups to keep around, this does not include the
	// current repository nor the background one. So in eccence their are 5
	// indexes that are kept around
	private long NUM_REPO_BACKUPS = 4;
	
	// Number of indexes that were marked as permanently ignore by an admin. We 
	// want to keep at least one around, so if another one errors out admins have
	// something to compare it to.
	private long NUM_ERROR_REPO_BACKUPS = 1;

	/**
	 * Constuctor that is used to make sure that the servelet params make sense. And sets
	 * up this bean instance to be ready to handle change requests
	 * @param servletContext
	 */
	public Maintenance(ServletContext servletContext, String ddsContext, boolean debug)
	{
		this.servletContext = servletContext;
		this.debug = debug;
		this.ddsContext = ddsContext;
		this.defaultSearchFieldName = (String) servletContext.getInitParameter("defaultSearchFieldName");
		if (defaultSearchFieldName == null || defaultSearchFieldName.trim().length() == 0)
			this.defaultSearchFieldName = DEFAULT_SEARCH_FIELD;

		prtln("defaultSearchFieldName set to " + defaultSearchFieldName);

		// Set up background indexing if requested:
		String performBackgroundIndexing = (String) servletContext.getInitParameter("performBackgroundIndexing");
		boolean doPerformBackgroundIndexing = (performBackgroundIndexing != null && performBackgroundIndexing.equalsIgnoreCase("true"));

		// Set up continuous background indexing if requested:
		String performContinuousIndexing = (String) servletContext.getInitParameter("performContinuousIndexing");
		doPerformContinuousIndexing = (performContinuousIndexing != null && performContinuousIndexing.equalsIgnoreCase("true"));
	
		// This tells us that we are importing repositories and not creating them ourselves
		importRepositoryDestinationString = servletContext.getInitParameter("importRepositoryDestinations");
		boolean doImportIndex = !(importRepositoryDestinationString == null || importRepositoryDestinationString.trim().equals(""));
		
		if(doPerformBackgroundIndexing&&doImportIndex)
		{
			// This is a setup that is not allowed. Consider it just a read only import index
			isImportIndex = true;
			prtlnErr("Both performBackgroundIndexing and importRepositoryDestinations are set. This is an invalid combination."+
					"importRepositoryDestinations must be empty to set performBackgroundIndexing to true. Defaulting to " +
					"import repostiroy only.");
		}
		else if(doPerformBackgroundIndexing)
			isBackgroundIndex = true;
		else if(doImportIndex)
			isImportIndex = true;
		else
			isForegroundIndex = true;
		
		//We place these also in the servelet context so jsp's have access to them.
		// This is better then them trying to access the init params directly and trying
		// to figure out what they mean
		servletContext.setAttribute("isImportIndex", isImportIndex);
		servletContext.setAttribute("isBackgroundIndex", isBackgroundIndex);
		servletContext.setAttribute("isForegroundIndex", isForegroundIndex);
	}
	
	private RepositoryManager getCurrentRepositoryManager()
	{
		return (RepositoryManager) servletContext.getAttribute("repositoryManager");
	}
	private RepositoryManager getBackgroundRepositoryManager()
	{
		return (RepositoryManager) servletContext.getAttribute("backgroundIndexingRepositoryManager");
	}
	
	/**
	 * Commits the current background index by first validating it, if it doesn't have any
	 * errors then we set it as current. Otherwise this will start up the repositories as
	 * normal. In order for a bad commit to be the same when the server is started up again.
	 * @throws Exception
	 */
	public void commitBackgroundIndex() throws Exception {
		prtln("commitBackgroundIndex() " + isBackgroundIndex);
		try {
			if (isBackgroundIndex)
			{
				RepositoryManager repositoryManager = getCurrentRepositoryManager();
				RepositoryManager backgroundIndexingRepositoryManager = getBackgroundRepositoryManager();
				boolean validIndex = IndexValidation.validateNewIndex(backgroundIndexingRepositoryManager, 
												 repositoryManager,
												 servletContext, true);
				
				// If its valid we want a commit and create a new background index. 
				if(validIndex)
				{
					String backgroundIndexingPath = backgroundIndexingRepositoryManager.getRepositoryDirectory().getAbsolutePath();
					setCurrentRepository(
							backgroundIndexingPath, 
						true, false
						);
					// Method will only export the repository if the destinations were set.
					// Otherwise nothing will happen
					exportRepository(backgroundIndexingPath);
				}
				else
				{
					// If not we do not want it to increment
					// instead we want the background indexer to stop and wait till an admin deals with it so we will send
					// false as incrementBackgroundIndexingRepository, which setCurrentRepository will see the background having
					// an error and stop the background indexer. We do this by a reqular startupRepositories
					startupRepositories();
				}
			}
		} catch (Exception t) {
			t.printStackTrace();
			prtln("Error commitBackgroundIndex(): " + t);
			throw t;
		}
	}
	
	/**
	 * Archive a repository's index summary. This archive will only keep one per month.
	 * So only the first one per month that calls this will be archived
	 * @param repositoryPath
	 */
	public void archiveIndexDataBean(
			String repositoryPath) {
		RepositoryManager repositoryManager = getCurrentRepositoryManager();
		File writtenSummaryData = repositoryManager.getSummaryDataFolder(new File(repositoryPath));
		
		
		boolean isForegroundIndex = ((Boolean)servletContext.getAttribute("isForegroundIndex")).booleanValue();

		if(isForegroundIndex || writtenSummaryData.listFiles().length==0)
		{
			FileUtils.deleteQuietly(writtenSummaryData);
			// Chances are this means that this is a foreground index
			// In that case create summary data then remove it
			
			// just validate the manager against itself since we really don't care about differences
			// Just the overall stats. This will create the stats folder that we need in the correct place
			IndexValidation.validateNewIndex(repositoryManager, 
											 repositoryManager,
											 servletContext, false);
			
		}
		
		
		
		String repositoryAnalyticsData;
		try {
			repositoryAnalyticsData = getAbsolutePath(servletContext.getInitParameter(
			"repositoryAnalyticsData"));
		} catch (ServletException e1) {
			e1.printStackTrace();
			return;
		}
		if(repositoryAnalyticsData==null)
			return;
		
		
		// The name will be the same name that we use inside of the repository except we prefix it with yean-month. For
		// ease of access for the histogram and summarizing data.
		String fileName =  new SimpleDateFormat("yyyy-MM").format(new Date())+"-"+RepositoryManager.SUMMARY_DATA_FOLDER;
		
		File monthlyArchiveFile = new File(repositoryAnalyticsData, fileName);
		if(monthlyArchiveFile.exists())
		{
			FileUtils.deleteQuietly(monthlyArchiveFile);
		}

		try {
			FileUtils.copyDirectory(writtenSummaryData, monthlyArchiveFile);
		} catch (IOException e) {
			e.printStackTrace();
		}


	
	}
	
	/** 
	 * Start up the repositories by default. Which will map exactly to whats in the
	 * the repository_bean.xml
	 * @throws Exception
	 */
	public void startupRepositories() throws Exception
	{
		setCurrentRepository(null, false, false);
	}
	
	
	/** The main method of this entire class. This method can change what the current
	 * repository is, start up the background index and start up the repository with 
	 * defaults in place which are given in the repository_bean.
	 * @param reposPath
	 * @param incrementBackgroundIndexingRepository
	 * @param clearCurrentBackgroundRepository
	 * @throws Exception
	 */
	public void setCurrentRepository(String reposPath, 
			boolean incrementBackgroundIndexingRepository,
			boolean clearCurrentBackgroundRepository
						) throws Exception
	{
		// This can only be accessed by one thread at a time
		synchronized (indexingOperationsLock) {
			try {
				
			RepositoryInfoBean repositoryInfoBean = getRepositoryInfoBean();

			// Grab the current live instances to shut down after new ones created:
			IndexingManager indexingManager = (IndexingManager) servletContext.getAttribute("indexingManager");
			RepositoryManager repositoryManager = getCurrentRepositoryManager();

			// The the repos path that was sent in is null. Use the defaults from the
			// repository beans. This will be the case when the server first starts up,
			// but may also be used when errors happen
			if(reposPath==null)
			{
				reposPath = repositoryInfoBean.getCurrentRepository();
				// If this is a brand new repository we start with 1
				if(reposPath==null)
				{
					reposPath = createNewRepository(1).getAbsolutePath();
					repositoryInfoBean.setCurrentRepository(reposPath);
				}
			}
			else
			{
				repositoryInfoBean.setCurrentRepository(reposPath);
			}
			// Stop the current index in process
			if(indexingManager!=null)
				indexingManager.fireAbortIndexingEvent(null);
			
			File repositoryDataDir = new File(reposPath, "data");
			File indexLocationDir = new File(reposPath, "index");

			boolean isReadOnly = false;
			// The current repository manager is always read only unless its a foreground
			// indexer
			if (isBackgroundIndex||isImportIndex)
				isReadOnly = true;
			RepositoryManager currentRepositoryManager = createNewRepositoryManager(repositoryDataDir, indexLocationDir, 
					isReadOnly, null);
			
			// If we are switching the repositories move the old admin data over
			if(repositoryManager!=null && currentRepositoryManager.getRepositoryDirectory()!=repositoryManager.getRepositoryDirectory())
				currentRepositoryManager.copyAdminDataFrom(repositoryManager);

			// Optimize the index before we do anything else
			currentRepositoryManager.optimizeIndexes();
			
			RepositoryManager repositoryManagerForIndexing=null;
			
			RepositoryManager backgroundRepositoryManager=null;
			boolean disableBackgroundIndexing = false;
			if (isBackgroundIndex)
			{
				// If is backgorund index we now setup the background indexer 
				
				String backgroundReposPath = repositoryInfoBean.getBackgroundIndexerRepository();
				
				// If this is a new repository or we want to create a new background indexer
				if(incrementBackgroundIndexingRepository || backgroundReposPath==null)
				{
					File backgroundIndexingRepository = createNewRepository(
							repositoryInfoBean.getNextBackgroundRepositoryNumber());
					repositoryInfoBean.setBackgroundIndexerRepository(backgroundIndexingRepository.getAbsolutePath());
					repositoryInfoBean.incrementNextBackgroundRepositoryNumber();
				}
				else
				{
					// Otherwise use the current one.
					IndexDataBean backgroundSummaryReport = currentRepositoryManager.getIndexSummary(
							new File(repositoryInfoBean.getBackgroundIndexerRepository()));
					
					// If the background indexer is completed by has an error we need to handle it
					if(backgroundSummaryReport!=null && 
							backgroundSummaryReport.getIndexStatus().equals(IndexDataBean.STATUS_ERROR))
					{
						// Means that the background index failed for some reason. We want to stop
						// the indexer and not let any more indexes go through until an admin
						// takes action and changes the status of the index
						disableBackgroundIndexing = true;
						
					}
				}
				// Finally create the bean
				backgroundRepositoryManager = createBackgroundRepositoryManager(
						repositoryInfoBean.getBackgroundIndexerRepository(),
						currentRepositoryManager.getIndex(), clearCurrentBackgroundRepository,
						disableBackgroundIndexing);
				setBackgroundIndexingRepositoryManagerLive(backgroundRepositoryManager);
				if(!disableBackgroundIndexing)
				{					
					repositoryManagerForIndexing = backgroundRepositoryManager;
					indexDisabledDueToActionRequired = false;
				}
				else
				{
					// null the indexer out so no indexes can go forward
					// This is a way to force admins to view the errors and make an action on them
					repositoryManagerForIndexing = null;
					indexDisabledDueToActionRequired = true;
				}
			}
			else
				repositoryManagerForIndexing = currentRepositoryManager;
			
			// Create the new IndexingManager, if applicable:
			String recordDataSource = getRecordDataSource();
			boolean filesAreSavedToDisc = true;
			if (!recordDataSource.equals("fileSystem") && !recordDataSource.equals("dcs")) {
				initNewIndexingManager(repositoryManagerForIndexing);

				// If records are being handled by the IndexingManager, files are not saved to disk:
				filesAreSavedToDisc = false;
			}
			servletContext.setAttribute("filesAreSavedToDisc", filesAreSavedToDisc);
			
			// Set the current repostiroy to live
			setRepositoryManagerLive(currentRepositoryManager);
			saveRepositoryInfoBean(repositoryInfoBean);
			
			// Clean up the repository directories, only keeping a certain amount of backup ones
			cleanupRepositories();
			
			}
			catch (Exception e) {
				prtlnErr("Error createRepositories(): " + e);
				e.printStackTrace();
				throw e;
			} catch (Throwable t) {
				prtlnErr("Error createRepositories(): " + t);
				t.printStackTrace();
			}
			

		}
	}
	
	/*
	 * Cleanup the repositories directory
	 */
	private void cleanupRepositories() throws Exception
	{
		RepositoryManager rm =
			(RepositoryManager) servletContext.getAttribute("repositoryManager");
		
		RepositoryManager backgroundIndexingRepositoryManager = null;
		if (isBackgroundIndex)
			backgroundIndexingRepositoryManager = getBackgroundRepositoryManager();

		File repositoryBaseDir = new File(servletContext.getInitParameter("repositoryData"));
		File[] repositoryDirectories = repositoryBaseDir.listFiles();
		if(repositoryDirectories!=null && repositoryDirectories.length!=0)
		{
			Arrays.sort(repositoryDirectories, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
			int succesfullBackupCount = 0;
			int errorBackupCount = 0;
			for(File repositoryDirectory:repositoryDirectories)
			{
				
				// If the file is not a director and does not start with the prefix repository_, then its not a 
				// repos. If the directory we are looking at is the current repository or the background don't count
				// those either.
				if(!repositoryDirectory.isDirectory() || !repositoryDirectory.getName().startsWith("repository_")||
						repositoryDirectory.getAbsolutePath().equals(rm.getRepositoryDirectory().getAbsolutePath())||
						(backgroundIndexingRepositoryManager!=null && 
								repositoryDirectory.getAbsolutePath().equals(
										backgroundIndexingRepositoryManager.getRepositoryDirectory().getAbsolutePath()))
						)
					continue;
				
				// Otherwise its a backup, now figure out if its a valid repos or invalid,
				// If the repos doesn't have a summary, might be a legacy repository treat it as
				// a successful one
				IndexDataBean summary = rm.getIndexSummary(repositoryDirectory.getAbsoluteFile());
				if(summary==null || summary.getIndexStatus().equals(IndexDataBean.STATUS_VALID))
				{
					if(succesfullBackupCount<NUM_REPO_BACKUPS)
					{
						succesfullBackupCount++;
						continue;
					}
				}
				else
				{
					// Ignored statuses are considered errored backups
					if(errorBackupCount<NUM_ERROR_REPO_BACKUPS)
					{
						errorBackupCount++;
						continue;
					}
				}
				FileUtils.deleteDirectory(repositoryDirectory);
			}
		}		
	}
	
	/*
	 * Create a new repository directory given a number. This uses number format to create
	 * a consistent naming system which we can sort on.
	 */
	private File createNewRepository(int nextBackgroundRepositoryNumber) throws Exception {
		File repositoryDataBaseDir = new File(getAbsolutePath((String) servletContext.getInitParameter("repositoryData")));

		NumberFormat numberFormat = NumberFormat.getNumberInstance();
		numberFormat.setMinimumIntegerDigits(6);
		numberFormat.setGroupingUsed(false);
		String newRepoNum = numberFormat.format(nextBackgroundRepositoryNumber);
		File newReposDirectory = new File(repositoryDataBaseDir, "repository_" + newRepoNum);
		if(newReposDirectory.exists())
			throw new Exception(String.format("New Repository %s could not be created alerady existed ", 
					newReposDirectory.toString()));
		return newReposDirectory;
	}


	/*
	 * Creates a background repository manger given some arguments
	 */
	private RepositoryManager createBackgroundRepositoryManager(String backgroundIndexReposPath,
			SimpleLuceneIndex priorIndex,
			boolean forceStartFromScratch,
			boolean disableBackgroundIndexing) throws Exception
	{

		String doResourceDeDuplication = servletContext.getInitParameter("doResourceDeDuplication");
		// If there is a force start from scratch or if the backgroundindex not disabled and we are doing resource dedups 
		// then also restart the background from scratch
		if (forceStartFromScratch || (!disableBackgroundIndexing && doResourceDeDuplication.equalsIgnoreCase("true")))
		{
			prtln("Reseting the the index since it was not all the way completed, starting over: ");
			Files.deleteDirectory(new File(backgroundIndexReposPath));
		}
		File repositoryDataDir = new File(backgroundIndexReposPath, "data");
		File indexLocationDir = new File(backgroundIndexReposPath, "index");


		// Create new repository background indexer:
		RepositoryManager reposistoryManager = createNewRepositoryManager(repositoryDataDir, indexLocationDir, 
				false, priorIndex);

		return reposistoryManager;
	}
	
	/*
	 * Gets the repository info bean from the base repository folder
	 */
	public RepositoryInfoBean getRepositoryInfoBean() throws ServletException
	{
		RepositoryInfoBean repositoryInfoBean = null;
		try {
			repositoryInfoBean = (RepositoryInfoBean) Beans.file2Bean(getRepositoryInfoBeanPath());
			if(repositoryInfoBean==null)
				repositoryInfoBean = new RepositoryInfoBean();
		} catch (Exception fe) {
			repositoryInfoBean = new RepositoryInfoBean();
		} 
		return repositoryInfoBean;
	}
	
	/*
	 * Saves the repository bean to file
	 */
	public void saveRepositoryInfoBean(RepositoryInfoBean repositoryInfoBean) throws ServletException
	{
		try {
			prtln("Writing repositoryInfoBean: " + repositoryInfoBean);
			Beans.bean2File(repositoryInfoBean, getRepositoryInfoBeanPath());
		} catch (Throwable t) {
			prtlnErr("Error writing repositoryInfoBean: " + t);
		}
	}
	
	/*
	 * Gets the repository info bean's path
	 */
	private File getRepositoryInfoBeanPath() throws ServletException
	{
		File repositoryDataBaseDir = new File(getAbsolutePath((String) servletContext.getInitParameter("repositoryData")));
		File repositoryInfoBeanFile = new File(repositoryDataBaseDir, "repository_info_bean.xml");
		return repositoryInfoBeanFile;
	}

	/*
	 * Exports a repository to a destination folder
	 */
	public void exportRepository(String repositoryPath)
	{
		String exportRepositoryDestinationString = servletContext.getInitParameter("exportRepositoryDestinations");
		if(exportRepositoryDestinationString!=null)
		{
			for(String destination:exportRepositoryDestinationString.split(","))
			{
				
				try {
					File destinationDirectory = new File(destination);
					if(!destinationDirectory.exists())
						destinationDirectory.mkdir();
					else
					{
						File[] possibleRepositories = new File(destination).listFiles();
						for(File possibleRepository:possibleRepositories)
						{
							if(possibleRepository.isDirectory() && possibleRepository.getName().startsWith("repository_"))
							{
								// Its a non-imported directory, get rid of it so no confusion
								FileUtils.deleteDirectory(possibleRepository);
							}
						}
					}
					FileUtils.copyDirectoryToDirectory(new File(repositoryPath), destinationDirectory);
				} catch (IOException e) {
					prtlnErr(String.format("Repository Path %s could not be exported to %s.", repositoryPath, destination));
				}
			}
		}
	}
	
	/*
	 * Imports repositories from a folder, this will only be called if this is a importIndexer
	 * instance
	 */
	public void importRepositories() throws Exception
	{		
		RepositoryManager repositoryManager = (RepositoryManager) servletContext.getAttribute("repositoryManager");
		File repositoryBaseDir = new File(servletContext.getInitParameter("repositoryData"));
		
		File firstAddedRepository = null;
		if(importRepositoryDestinationString!=null)
		{
			for(String destination:importRepositoryDestinationString.split(","))
			{
				File destinationDirecotry = new File(destination);
				if(!destinationDirecotry.exists() || !destinationDirecotry.isDirectory())
				{
					prtlnErr(destination+ " is not a valid directory that can hold repositories.");
					continue;
				}
				File[] possibleRepositories = new File(destination).listFiles();
				Arrays.sort(possibleRepositories, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
				for(File possibleRepository:possibleRepositories)
				{
					if(!possibleRepository.isDirectory() || !possibleRepository.getName().startsWith("repository_"))
							continue;
					IndexDataBean summary = repositoryManager.getIndexSummary(possibleRepository);
					if(summary!=null && summary.getIndexStatus().equals(IndexDataBean.STATUS_VALID))
					{
						prtln(String.format("External repository %s was found at %s and is being copied into a local directory.", 
								possibleRepository.getName(), destination));
						FileUtils.moveDirectoryToDirectory(possibleRepository, repositoryBaseDir, false);
						
						// We need to change the index path to point the the new location, set the new one
						// and write it
						File newRepository = new File(repositoryBaseDir, possibleRepository.getName());
						summary.getIndexDetail().put(IndexDataBean.INDEX_PATH,  newRepository.getAbsolutePath());
						repositoryManager.writeIndexSummary(summary, newRepository);
						if(firstAddedRepository==null)
							firstAddedRepository = newRepository;
					}
					else
					{
						// if its not a valid index or if it doesn't have a summary get rid of it
						FileUtils.deleteDirectory(possibleRepository);
					}
				}
			}
		}
		if(firstAddedRepository!=null)
		{
			prtln(String.format("Making imported repository %s current.", firstAddedRepository.getName()));
			// Only set it at live if the option for makeImportedRepositories is true
			if(getRepositoryInfoBean().isMakeImportedRepositoriesCurrentRepository())
				this.setCurrentRepository(firstAddedRepository.getAbsolutePath(),false, true);
			
			// To make sure a repository that is never turned off nor does its own indexing.
			// gets to many indexes loaded we call cleanup to keep the file disk usage down
			this.cleanupRepositories();
		}
	}
	
	// Make the given RepositoryManager the live one servicing background indexing:
	private void setBackgroundIndexingRepositoryManagerLive(RepositoryManager repositoryManager) throws Exception {
		// Shut down existing ones:
		RepositoryManager existingRepositoryManager = (RepositoryManager) servletContext.getAttribute("backgroundIndexingRepositoryManager");
		if (existingRepositoryManager != null)
			existingRepositoryManager.destroy();

		servletContext.setAttribute("backgroundIndexingRepositoryManager", repositoryManager);
	}
	
	// Make the given RepositoryManager the live one servicing Web requests:
	private void setRepositoryManagerLive(RepositoryManager repositoryManager) throws Exception {

		// Set the URL that is used to display the baseURL in DDSWS1-1 responses and elsewhere in the UI (for now only used in JSPs via app scope variable):
		String ddsws11BaseUrlOverride = servletContext.getInitParameter("ddsws11BaseUrlOverride");
		if (ddsws11BaseUrlOverride != null && !ddsws11BaseUrlOverride.equals("[determine-from-client]"))
			servletContext.setAttribute("ddsws11BaseUrlOverride", ddsws11BaseUrlOverride);

		// Set up a dir for the record meta-metadata
		File recordMetaMetadataDir = new File(repositoryManager.getRepositoryDataDir(), "record_meta_metadata");
		recordMetaMetadataDir.mkdirs();
		servletContext.setAttribute("recordMetaMetadataDir", recordMetaMetadataDir);

		// Perform context-specific initialization (right now just dds but can be used for dcs and joai if needed):
		if (ddsContext.equals("dds-webapp") || ddsContext.equals("dcs-webapp")) {
			// Set up OAI sets for each collection (run in the background...):
			repositoryManager.defineOaiSetsForCollections(true);
		}

		String enableNewSets = servletContext.getInitParameter("enableNewSets");
		servletContext.setAttribute("enableNewSets", enableNewSets);

		String queryLogFile = getAbsolutePath((String) servletContext.getInitParameter("queryLogFile"));
		servletContext.setAttribute("queryLogFile", queryLogFile);

		String resourceResultLinkRedirectURL = (String) servletContext.getInitParameter("resourceResultLinkRedirectURL");
		if ((resourceResultLinkRedirectURL == null) || (resourceResultLinkRedirectURL.equals("none"))) {
			resourceResultLinkRedirectURL = "";
		}
		else if (!resourceResultLinkRedirectURL.endsWith("/"))
			resourceResultLinkRedirectURL = resourceResultLinkRedirectURL + "/";
		servletContext.setAttribute("resourceResultLinkRedirectURL", resourceResultLinkRedirectURL);

		setCollectionsVocabDisplay(repositoryManager);

		// Load DDS-related properties from file
		Properties ddsConfigProperties = null;
		try {
			ddsConfigProperties = new PropertiesManager(repositoryConfigDirPath + "/dds_config.properties");
			servletContext.setAttribute("ddsConfigProperties", ddsConfigProperties);
		} catch (Throwable t) {
			prtlnErr("Error loading DDS config properties: " + t);
		}

		// Shut down existing ones:
		RepositoryManager existingRepositoryManager = (RepositoryManager) servletContext.getAttribute("repositoryManager");
		if (existingRepositoryManager != null)
			existingRepositoryManager.destroy();

		SimpleLuceneIndex existingIndex = (SimpleLuceneIndex) servletContext.getAttribute("index");
		if (existingIndex != null)
			existingIndex.close();
		
		
		
		servletContext.setAttribute("repositoryManager", repositoryManager);
		servletContext.setAttribute("index", repositoryManager.getIndex());
		//prtln("\n\n***setRepositoryManagerLive() to: " + repositoryManager.getRepositoryDataDir().getAbsolutePath() + " repo.numRecords: " + repositoryManager.getNumRecordsInIndex() + " index.numRecords: " + repositoryManager.getIndex().getNumDocs() + "\n\n");
	}
	
	private RepositoryManager createNewRepositoryManager(
            File repositoryDataDir,
            File indexLocationDir,
            boolean isReadOnly,
            SimpleLuceneIndex previousIndex) throws Exception 
    {
		if (!repositoryDataDir.exists()) {
			prtln("Creating directory " + repositoryDataDir.getAbsolutePath());
			boolean created = repositoryDataDir.mkdirs();
			if (created)
				prtln("Created directory " + repositoryDataDir.getAbsolutePath());
			else
				throw new Exception("Unable to create data dir: " + repositoryDataDir.getAbsolutePath());
		}
	
		if (!indexLocationDir.exists()) {
			prtln("Creating directory " + indexLocationDir.getAbsolutePath());
			boolean created = indexLocationDir.mkdirs();
			if (created)
				prtln("Created directory " + indexLocationDir.getAbsolutePath());
			else
				throw new Exception("Unable to create index dir: " + indexLocationDir.getAbsolutePath());
		}
	
		// Context init params set in the context definition in server.xml or web.xml:
		String contentCacheBaseUrl = servletContext.getInitParameter("contentCacheBaseUrl");
		String dbURL = servletContext.getInitParameter("dbURL");
		String idMapperExclusionFile = servletContext.getInitParameter("idMapperExclusionFile");
		String collectionRecordsLocation = (String) servletContext.getInitParameter("collectionRecordsLocation");
		String maxNumResultsDDSWS = (String) servletContext.getInitParameter("maxNumResultsDDSWS");
	
		// Determine the metadata records location:
		String collBaseDir = null;
		String collBaseDirVal = (String) servletContext.getInitParameter("collBaseDir");
	
		// Use the location specified, if available:
		if (collBaseDirVal != null && collBaseDirVal.trim().length() > 0) {
			collBaseDir = getAbsolutePath(collBaseDirVal);
		}
		// Otherwise, use the default location inside repositoryData (one level up from repositoryDataDir):
		else {
			File collBaseDirFile = new File(repositoryDataDir.getParentFile(), "records");
			collBaseDir = collBaseDirFile.getAbsolutePath();
		}
	
		// By default, only use records located in the ../dlese_collect/collect directory to configure
		// the collections for this repository:
		if (collectionRecordsLocation == null)
			collectionRecordsLocation = getAbsolutePath(collBaseDir + "/dlese_collect/collect");
		else
			collectionRecordsLocation = getAbsolutePath(collectionRecordsLocation);
	
		//String enableNewSets = servletContext.getInitParameter("enableNewSets");
		String doResourceDeDuplication = servletContext.getInitParameter("doResourceDeDuplication");
	
		// Indexing start time in 24hour time, for example 0:35 or 23:35
		String indexingStartTime = getIndexingStartTime();
		if (indexingStartTime == null || indexingStartTime.equalsIgnoreCase("disabled"))
			indexingStartTime = null;
	
		String recordDataSource = getRecordDataSource();
	
		// If an IndexingManager is being used, don't run the RepositoryManager file indexing timer.
		String indexingStartTimeFileSystem = indexingStartTime;
		if (!recordDataSource.equals("fileSystem"))
			indexingStartTimeFileSystem = null;
	
		// Indexing days of the week as a comma separated list of integers, for example 1,3,5 where 1=Sunday, 2=Monday, 7=Saturday etc. 'all' for all days
		String indexingDaysOfWeek = getIndexingDaysOfWeek();
	
		prtln("Using collection-level metadata files located at " + collectionRecordsLocation);
		prtln("Using/writing metadata files located at " + collBaseDir);
		prtln("Using index located at " + indexLocationDir.getAbsolutePath());
	
		// Load metadata controlled vocabularies:
		String vocabConfigDir = getAbsolutePath((String) servletContext.getInitParameter("vocabConfigDir"));
		String sqlDriver = (String) servletContext.getInitParameter("sqlDriver");
		String sqlURL = (String) servletContext.getInitParameter("sqlURL");
		String sqlUser = (String) servletContext.getInitParameter("sqlUser");
		String sqlPassword = (String) servletContext.getInitParameter("sqlPassword");
		String annotationPathwaysSchemaUrl = (String) servletContext.getInitParameter("annotationPathwaysSchemaUrl");
		String vocabTextFile = (String) servletContext.getInitParameter("vocabTextFile");
	
		// MetadataVocab is instantiated by MetadataVocabServlet (LoadMetadataOPML class):
		MetadataVocab metadataVocab = (MetadataVocab) servletContext.getAttribute("MetadataVocab");
		if (metadataVocab == null)
			prtlnErr("WARNING: MetadataVocab is null");
	
		// Get the config directory.
		File repositoryConfigDir = null;
		try {
			repositoryConfigDir = new File(getAbsolutePath((String) servletContext.getInitParameter("repositoryConfigDir")));
			repositoryConfigDirPath = repositoryConfigDir.getAbsolutePath();
		} catch (Throwable e) {
			prtlnErr("Error getting repositoryConfigDir: " + e);
		}
	
		// Get the ItemIndexer config directory.
		File itemIndexerConfigDir = null;
		try {
			itemIndexerConfigDir = new File(getAbsolutePath((String) servletContext.getInitParameter("itemIndexerConfigDir")));
		} catch (Throwable e) {
			prtlnErr("Error getting itemIndexerConfigDir: " + e);
		}
	
		boolean doResourceDeDuplicationBoolean = false;
		if (doResourceDeDuplication != null && doResourceDeDuplication.equalsIgnoreCase("true"))
			doResourceDeDuplicationBoolean = true;
	
		// The maximum number of files to index per session. Set to 1 if de-duping is enabled:
		int maxNumFilesToIndex = 500;
		if (doResourceDeDuplication != null && doResourceDeDuplication.equalsIgnoreCase("true"))
			maxNumFilesToIndex = 1;
	
		// Set up the RecordDataService for use in indexing and search results:
		RecordDataService recordDataService = new RecordDataService(dbURL, metadataVocab, collBaseDir, annotationPathwaysSchemaUrl);
	
		// Set up an XMLConversionService for use in DDS web service, OAI and elsewhere:
		XMLConversionService xmlConversionService = null;
		File xslFilesDirecory = new File(GeneralServletTools.getAbsolutePath("WEB-INF/xsl_files/", servletContext));
		File xmlCachDir = new File(repositoryDataDir, "converted_xml_cache");
		try {
			xmlConversionService = XMLConversionService.xmlConversionServiceFactoryForServlets(servletContext, xslFilesDirecory, xmlCachDir, true);
		} catch (Throwable t) {
			prtlnErr("ERROR: Unable to initialize xmlConversionService: " + t);
		}
	
		RepositoryManager repositoryManager = null;
		prtln("Starting up RepositoryManager");
		repositoryManager = new RepositoryManager(
			this.defaultSearchFieldName,
			repositoryConfigDir,
			itemIndexerConfigDir,
			repositoryDataDir.getAbsolutePath(),
			indexLocationDir.getAbsolutePath(),
			indexingStartTimeFileSystem,
			indexingDaysOfWeek,
			recordDataService,
			collectionRecordsLocation,
			collBaseDir,
			xmlConversionService,
			contentCacheBaseUrl,
			servletContext,
			doResourceDeDuplicationBoolean,
			true,
			true,
			isReadOnly);
	
		// Set up the default metadata audience and language used by the indexer and search resultDocs for display:
		String metadataVocabAudience = (String) servletContext.getInitParameter("metadataVocabAudience");
		if (metadataVocabAudience == null) {
			metadataVocabAudience = "community";
		}
		String metadataVocabLanguage = (String) servletContext.getInitParameter("metadataVocabLanguage");
		if (metadataVocabLanguage == null) {
			metadataVocabLanguage = "en-us";
		}
	
		repositoryManager.setMetadataVocabAudienceDefault(metadataVocabAudience);
		repositoryManager.setMetadataVocabLanguageDefault(metadataVocabLanguage);
	
		// Set the URL that is used to display the baseURL in OAI-PMH responses and elsewhere:
		String oaiBaseUrlOverride = servletContext.getInitParameter("oaiBaseUrlOverride");
		if (oaiBaseUrlOverride != null && !oaiBaseUrlOverride.equals("[determine-from-client]"))
			repositoryManager.setOaiBaseUrlOverride(oaiBaseUrlOverride);
	
		// Set up the ending portion of the baseUrl for the data provider, if indicated:
		String dataProviderBaseUrlPathEnding = servletContext.getInitParameter("dataProviderBaseUrlPathEnding");
		if (dataProviderBaseUrlPathEnding != null)
			repositoryManager.setProviderBaseUrlEnding(dataProviderBaseUrlPathEnding);
	
		// Disable regular OAI-PMH responses to ListRecords and ListIdentifiers requests (accept ODL requests only), if indicated:
		String oaiPmhEnabled = (String) servletContext.getInitParameter("oaiPmhEnabled");
		if (oaiPmhEnabled != null && oaiPmhEnabled.equals("false"))
			repositoryManager.setIsOaiPmhEnabled(false);
	
		// Set the location of the file used to exclude IDs in the IDMapper service
		if (idMapperExclusionFile != null && idMapperExclusionFile.length() > 0 && !idMapperExclusionFile.equals("none")) {
			prtln("Using IDMapper ID exclusion file located at: " + idMapperExclusionFile);
			repositoryManager.setIdMapperExclusionFilePath(idMapperExclusionFile);
		}
	
		// Set up the configured FileIndexingPlugins prior to calling rm.init()
		setFileIndexingPlugins(repositoryManager);
	
		FileIndexingService fileIndexingService = repositoryManager.getFileIndexingService();
	
		boolean indexCollectionRecords = true;
	
		// Initialize RepositoryManager:
		int initResult = repositoryManager.init(indexCollectionRecords, previousIndex);
		if (initResult != 1) {
			String initErrorMsg = "Error initializing the repositoryManager";
			prtlnErr(initErrorMsg);
			throw new Exception(initErrorMsg);
		}
	
		// Add default xmlformat schema and namespaces for OAI:
		Enumeration enumeration = servletContext.getInitParameterNames();
		String param;
		while (enumeration.hasMoreElements()) {
			param = (String) enumeration.nextElement();
			if (param.toLowerCase().startsWith("xmlformatinfo")) {
				try {
					repositoryManager.setDefaultXmlFormatInfo(servletContext.getInitParameter(param));
				} catch (Throwable t) {
					String initErrorMsg = "Error reading init param for xmlformatinfo: " + t;
					t.printStackTrace();
					prtlnErr(initErrorMsg);
				}
			}
		}

		// Set up the global repository filters:
		setGlobalRepositoryFilters(repositoryManager);
	
		return repositoryManager;
	}
		
	
	
	/**
	 *  Sets the fileIndexingPlugins found in the servlet config.
	 *
	 * @param  repositoryManager  The new fileIndexingPlugins value
	 */
	private void setFileIndexingPlugins(RepositoryManager repositoryManager) {
		try {
			// Add configured FileIndexingPlugins:
			Enumeration enumeration = servletContext.getInitParameterNames();
			String param;
			while (enumeration.hasMoreElements()) {
				param = (String) enumeration.nextElement();

				if (param.toLowerCase().startsWith("fileindexingplugin")) {
					String paramVal = servletContext.getInitParameter(param);
					String[] vals = paramVal.split("\\|");
					if (vals.length != 2 && vals.length != 1) {
						prtlnErr("Error: setFileIndexingPlugins(): could not parse parameter '" + paramVal + "'");
						continue;
					}

					try {
						Class pluginClass = Class.forName(vals[0].trim());
						FileIndexingPlugin plugin = (FileIndexingPlugin) pluginClass.newInstance();
						String format = vals.length == 2 ? vals[1].trim() : RepositoryManager.PLUGIN_ALL_FORMATS;

						// Make the ServletContext available to all ServletContextFileIndexingPlugins
						if (plugin instanceof ServletContextFileIndexingPlugin) {
							((ServletContextFileIndexingPlugin) plugin).setServletContext(servletContext);
						}

						//System.out.println("Adding plugin: " + plugin.getClass().getName() + " for format " + format);
						repositoryManager.setFileIndexingPlugin(format, plugin);
					} catch (Throwable e) {
						prtlnErr("Error: setFileIndexingPlugins(): could not instantiate class '" + vals[0].trim() + "'. " + e);
						continue;
					}
				}
			}
		} catch (Throwable e) {
			String initErrorMsg = "Error: setFileIndexingPlugins(): " + e;
			prtlnErr(initErrorMsg);
		}

	}
	
	/**
	 *  Sets the global repository filters used to remove records from the Search API and OAI data provider
	 *  output.
	 *
	 * @param  repositoryManager  The RepositoryManager
	 */
	private void setGlobalRepositoryFilters(RepositoryManager repositoryManager) {

		// Add configured filters:
		Enumeration enumeration = servletContext.getInitParameterNames();
		String param;
		while (enumeration.hasMoreElements()) {
			param = (String) enumeration.nextElement();
			if (param.toLowerCase().startsWith("repositoryfilter")) {
				String paramVal = servletContext.getInitParameter(param);

				String[] vals = paramVal.split("\\|");
				if (vals.length != 3) {
					prtlnErr("Error defining the repositoryfilter '" + param + "': Wrong number of arguments found in '" + paramVal + "'. Must be of the form '[filterName]|[one of 'disableFilterAllowed' or 'disableFilterNotAllowed']|[lucene query]'");
					continue;
				}
				String filterName = vals[0];
				String overrideIsAllowed = vals[1];
				if (!(overrideIsAllowed.equals("disableFilterAllowed") || overrideIsAllowed.equals("disableFilterNotAllowed"))) {
					prtlnErr("Error defining the repositoryfilter '" + param + "': Second argument must be one of 'disableFilterAllowed' or 'disableFilterNotAllowed' but found '" + overrideIsAllowed + "'");
					continue;
				}
				String luceneQuery = vals[2];
				Filter filter;
				try {
					Query parsedQuery = repositoryManager.getIndex().getQueryParser().parse(luceneQuery);
					filter = new QueryWrapperFilter(parsedQuery);
				} catch (ParseException pe) {
					prtlnErr("Error defining the repositoryfilter '" + param + "': Unable to parse the Lucene query '" + luceneQuery + "': " + pe.getMessage());
					continue;
				}
				repositoryManager.addRepositoryFilter(filterName, overrideIsAllowed.equals("disableFilterAllowed"), filter);
			}
		}
	}
	
	
	private void initNewIndexingManager(RepositoryManager repositoryManager) throws Exception {
        IndexingManager existingIndexingManager = (IndexingManager) servletContext.getAttribute("indexingManager");
        if (existingIndexingManager != null)
            existingIndexingManager.destroy();

		IndexingManager indexingManager = null;
		if(repositoryManager!=null)
		{
			indexingManager = new IndexingManager(repositoryManager, this);
			String recordDataSource = getRecordDataSource();
			recordDataSource = recordDataSource.replaceAll("\\s+", "");
			String[] classes = recordDataSource.split(",");
			for (int i = 0; i < classes.length; i++) {
				try {
					prtln("Adding ItemIndexer '" + classes[i] + "'");
					indexingManager.addIndexingEventHandler(classes[i]);
					prtln("Done Adding ItemIndexer '" + classes[i] + "'");
				} catch (Throwable t) {
					prtlnErr("Error initializing data source Java class for IndexingManager: " + t);
				}
			}
	
			try {
				indexingManager.fireIndexerReadyEvent(null);
				indexingManager.fireUpdateCollectionsEvent(null);
			} catch (Throwable t) {
				prtlnErr("Error occured with an ItemIndexer: " + t);
				t.printStackTrace();
			}
	
			String indexingStartTime = getIndexingStartTime();
			String indexingDaysOfWeek = getIndexingDaysOfWeek();
			try {
				if (doPerformContinuousIndexing)
					indexingManager.startContinuousIndexingTimer();
				else if (indexingStartTime != null)
					indexingManager.startIndexingTimer(indexingStartTime, indexingDaysOfWeek);
			} catch (Throwable t) {
				prtlnErr("Error starting the indexing timer: " + t);
			}
		}
		servletContext.setAttribute("indexingManager", indexingManager);
	}

	/**
	 *  Sets the "noDisplay" property of collection vocab nodes according the results of the repository manager's
	 *  getEnabledSetsHashMap()
	 *
	 * @param  repositoryManager  The new collectionsVocabDisplay value
	 */
	public static void setCollectionsVocabDisplay(RepositoryManager repositoryManager) {
		MetadataVocab vocab = repositoryManager.getMetadataVocab();
		if (vocab == null)
			return;
		Set vi = vocab.getVocabSystemInterfaces();
		Iterator i = vi.iterator();
		while (i.hasNext()) {
			doSetCollectionsVocabDisplay(repositoryManager, (String) i.next());
		}
	}
	
	/**
	 *  Description of the Method
	 *
	 * @param  systemInterface    Description of the Parameter
	 * @param  repositoryManager  RepositoryManager
	 */
	private static void doSetCollectionsVocabDisplay(RepositoryManager repositoryManager,
	                                                 String systemInterface) {
		HashMap sets = repositoryManager.getEnabledSetsHashMap();
		MetadataVocab vocab = repositoryManager.getMetadataVocab();
		ArrayList nodes = vocab.getVocabNodes(systemInterface + "/key", "");
		for (int i = 0; i < nodes.size(); i++) {
			if (sets.get(((VocabNode) nodes.get(i)).getId()) == null) {
				((VocabNode) nodes.get(i)).setNoDisplay(true);
			}
			// If the UI groups file said not to display (dwelanno for example), then don't override that:
			else if (!((VocabNode) nodes.get(i)).getNoDisplayOriginal()) {
				((VocabNode) nodes.get(i)).setNoDisplay(false);
			}
		}
	}
	
	/**
	 *  Output a line of text to error out, with datestamp.
	 *
	 * @param  s  The text that will be output to error out.
	 */
	private final void prtlnErr(String s) {
		System.err.println(getDateStamp() + " DDSServlet Error: " + s);
	}


	/**
	 *  Output a line of text to standard out, with datestamp, if debug is set to true.
	 *
	 * @param  s  The String that will be output.
	 */
	private final void prtln(String s) {
		if (debug) {
			System.out.println(getDateStamp() + " DDSServlet: " + s);
		}
	}
	
	public static String getDateStamp() {
		return
			new SimpleDateFormat("MMM d, yyyy h:mm:ss a zzz").format(new Date());
	}
	/**
	 *  Gets the absolute path to a given file or directory. Assumes the path passed in is eithr already absolute
	 *  (has leading slash) or is relative to the context root (no leading slash). If the string passed in does
	 *  not begin with a slash ("/"), then the string is converted. For example, an init parameter to a config
	 *  file might be passed in as "WEB-INF/conf/serverParms.conf" and this method will return the corresponding
	 *  absolute path "/export/devel/tomcat/webapps/myApp/WEB-INF/conf/serverParms.conf." <p>
	 *
	 *  If the string that is passed in already begings with "/", nothing is done. <p>
	 *
	 *  Note: the super.init() method must be called prior to using this method, else a ServletException is
	 *  thrown.
	 *
	 * @param  fname                 An absolute or relative file name or path (relative the the context root).
	 * @return                       The absolute path to the given file or path.
	 * @exception  ServletException  An exception related to this servlet
	 */
	private String getAbsolutePath(String fname)
		 throws ServletException {
		return new File(GeneralServletTools.getAbsolutePath(fname, servletContext)).getAbsolutePath();
	}
	
	private String getIndexingStartTime() {
		// Indexing start time in 24hour time, for example 0:35 or 23:35
		String indexingStartTime = (String) servletContext.getInitParameter("indexingStartTime");
		if (indexingStartTime == null || indexingStartTime.equalsIgnoreCase("disabled"))
			indexingStartTime = null;
		return indexingStartTime;
	}


	private String getIndexingDaysOfWeek() {
		// Indexing days of the week as a comma separated list of integers, for example 1,3,5 where 1=Sunday, 2=Monday, 7=Saturday etc.
		String indexingDaysOfWeek = (String) servletContext.getInitParameter("indexingDaysOfWeek");
		if (indexingDaysOfWeek == null)
			return null;
        if (indexingDaysOfWeek.equalsIgnoreCase("all"))
			return "1,2,3,4,5,6,7";
		return indexingDaysOfWeek;
	}


	private String getRecordDataSource() {
		return (String) servletContext.getInitParameter("recordDataSource");
	}
	public boolean isIndexDisabledDueToActionRequired() {
		return indexDisabledDueToActionRequired;
	}


}

