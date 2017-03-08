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
package edu.ucar.dls.harvest.harvesters;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;

import edu.ucar.dls.harvest.Config;
import edu.ucar.dls.harvest.HarvestRequest;
import edu.ucar.dls.harvest.exceptions.HarvestException;
import edu.ucar.dls.harvest.processors.Processor;
import edu.ucar.dls.harvest.processors.harvest.DedupRecords;
import edu.ucar.dls.harvest.processors.harvest.RecordCountValidator;
import edu.ucar.dls.harvest.processors.harvest_files.HarvestFileProcessor;
import edu.ucar.dls.harvest.workspaces.Workspace;

/**
 *  Abstract Class that is used within the ingestor code to download the harvest
 *  files and initially populate the workspace with the record info. This file
 *  must be extended when a new protocol is created.
 *  Authored 4/2/2013
 */

public abstract class Harvester {
	protected String outputDirectoryName = Config.HARVESTED_FILES_DIRECTORY_NAME;
	protected Workspace workspace = null;
	
	// How long we will wait for the server to respond
	public final static int SERVER_REQUEST_TIMEOUT = 120000; 
	
	// If a bad response comes back ie 500, we will wait 10 
	//minutes before trying again
	public final static long SERVER_TRY_TIMEOUT = 600000;
	
	// At the most we will try 3 times. So giving the server 30 minutes 
	// to come back online
	public final static long SERVER_MAX_TRIES = 3;
	
	// Harvest Processors
	protected List<Processor> preProcessors = new ArrayList<Processor>();
	protected List<HarvestFileProcessor> harvestFileProcessors = 
					new ArrayList<HarvestFileProcessor>();
	protected List<Processor> postProcessors = new ArrayList<Processor>();
	

	/**
	 *  initializing method that must be called prior to calling harvest. This is 
	 *  Separated from the constructor due to the possibility of making harvesters
	 *  constructed via config files.
	 */
	public void initialize(Workspace workspace) throws HarvestException{
		this.workspace = workspace;
	}
	
	/**
	 *  Start harvest.
	 */
	public void harvest() throws HarvestException
	{
		this.setPreProcessors();
		Processor.initializeProcessors(this.preProcessors, this.workspace);
		for( Processor preProcessor: this.preProcessors)
		{
			preProcessor.run();
		}
		
		// Download files must be implemented in classes that extend it
		this.downloadFiles();
		
		// Run file processors on the files that were downloaded
		this.setHarvestFileProcessors();
		Processor.initializeProcessors(this.harvestFileProcessors, this.workspace);
		for (File harvestFile: this.getHarvestDirectory().listFiles())
		{
			for( HarvestFileProcessor harvestFileProcessor: this.harvestFileProcessors)
			{
				harvestFileProcessor.run(harvestFile);
			}
			HarvestRequest.checkForInterruption(
					"Harvester.harvest()->running File Processors");
		}
		// Populate records must be implemented in classes that extend
		this.populateRecords();
			
		Processor.initializeProcessors(this.postProcessors, this.workspace);
		for( Processor postProcessor: this.postProcessors)
		{
			postProcessor.run();
			
			HarvestRequest.checkForInterruption(
			"Harvester.harvest()->running Post Processors");
		}
	}
	
	/**
	 *  Abstract method that must be overwritten by subclasses. This method
	 *  must download the files into the directory that is defined when calling
	 *  getHarvestDirectory()
	 *  @exception HarvestException exception that stops the harvest immediately
	 */
	protected abstract void downloadFiles() throws HarvestException;
	
	/**
	 *  Abstract method that must be overwritten by subclasses. This method
	 *  must populate the workspace database with records and their 
	 *  identifiers
	 *  @exception HarvestException exception that stops the harvest immediately 
	 */
	protected abstract void populateRecords() throws HarvestException;
	
	/**
	 *  Gets the harvest directory that sub classes must download their files into.
	 *  @return File harvest directory that files should be downloaded to
	 */
	public File getHarvestDirectory()
	{
		File harvestDirectory = new File(this.workspace.getWorkingFileDirectory(), 
				  Config.HARVESTED_FILES_DIRECTORY_NAME);
		harvestDirectory.mkdirs();
		return harvestDirectory;
	}
	
	/**
	 *  Gets the list of pre processors that have been assigned to this harvester
	 *  @return List of processors
	 */
	public List<Processor> getPreProcessors() {
		return preProcessors;
	}
	
	/**
	 *  Gets the list of post processors that have been assigned to this harvester
	 *  @return List of processors
	 */
	public List<Processor> getPostProcessors() {
		return postProcessors;
	}
	
	/**
	 *  Gets the list of file processors that have been assigned to this harvester
	 *  @return List of processors
	 */
	public List<HarvestFileProcessor> getHarvestFileProcessors() {
		return harvestFileProcessors;
	}
	
	/**
	 *  Sets Post processors, for now this does not take any arguments,
	 *  If harvester is every created via config files, this should take 
	 *  a List of processors and then add them to postProcessors
	 */
	public void setPostProcessors(List<Processor> postProcessors) throws HarvestException{
		this.postProcessors.add(new RecordCountValidator());
		this.postProcessors.add(new DedupRecords());
		if(postProcessors!=null  && postProcessors.size()>0)
			this.postProcessors.addAll(postProcessors);	
	}
	
	/**
	 *  Sets File processors, for now this does not take any arguments,
	 *  If harvester is every created via config files, this should take 
	 *  a List of processors and then add them to fileProcessors
	 */
	protected void setHarvestFileProcessors() throws HarvestException{}
	
	/**
	 *  Sets Pre processors, for now this does not take any arguments,
	 *  If harvester is every created via config files, this should take 
	 *  a List of processors and then add them to preProcessors
	 */
	protected void setPreProcessors() throws HarvestException{}
	
	/**
	 *  Clean up anything that was created and not needed anymore. This
	 *  is called by the ingestor at the end of the harvest
	 * @throws HarvestException 
	 */
	public void clean() throws HarvestException {
		if(Config.ZIP_HARVEST_FILES)
			this.zipDownloadedFiles();
	}
	
	/**
	 * Zip all harvested files together then delete the individual files.
	 * @throws HarvestException
	 */
	protected void zipDownloadedFiles() throws HarvestException
	{	
		if (this.getHarvestDirectory()==null || this.getHarvestDirectory().listFiles().length==0)
			return;
		
		try {
			ZipOutputStream zos =
			    new ZipOutputStream(new FileOutputStream(new File(this.workspace.getWorkingFileDirectory(), 
					  Config.HARVESTED_FILES_ZIPFILE_NAME)));
			byte[] buf = new byte[1024];

        	for (File file: this.getHarvestDirectory().listFiles())
            {
        	   FileInputStream fis = new FileInputStream(file);
               zos.putNextEntry(new ZipEntry(file.getName()));
               int len;
               while ((len = fis.read(buf)) > 0)
                  zos.write(buf, 0, len);
               fis.close();
               zos.closeEntry();
            }
        	zos.close();
        	
        	// Now delete the single files
    		FileUtils.deleteDirectory(this.getHarvestDirectory());

		} catch (FileNotFoundException e) {
			throw new HarvestException(Config.Exceptions.PROGRAMMER_ERROR_CODE,
					"Unable to zip harvest files and remove single files. Leaving as is.",
					e);
		} catch (IOException e) {
			throw new HarvestException(Config.Exceptions.PROGRAMMER_ERROR_CODE,
					"Unable to zip harvest files and remove single files. Leaving as is.",
					e);
		}
	}


	//=========== Debugging/logging ===============================================

	private boolean debug = true;

	/**
	 *  Return a string for the current time and date, sutiable for display in log files and output to standout:
	 *
	 * @return    The dateStamp value
	 */
	public static String getDateStamp() {
		return
				new SimpleDateFormat("MMM d, yyyy h:mm:ss a zzz").format(new Date());
	}


	/**
	 *  Output a line of text to error out, with datestamp.
	 *
	 * @param  s  The text that will be output to error out.
	 */
	protected final void prtlnErr(String s) {
		System.err.println(getDateStamp() + " Harvester Error: " + s);
	}



	/**
	 *  Output a line of text to standard out, with datestamp, if debug is set to true.
	 *
	 * @param  s  The String that will be output.
	 */
    protected final void prtln(String s) {
		if (debug)
			System.out.println(getDateStamp() + " Harvester: " + s);
	}


	/**
	 *  Sets the debug attribute.
	 *
	 * @param  db  The new debug value
	 */
	public final void setDebug(boolean db) {
		debug = db;
	}
}
