/*
 *  License and Copyright:
 *
 *  The contents of this file are subject to the Educational Community License v1.0 (the "License"); you may
 *  not use this file except in compliance with the License. You should have received a copy of the License
 *  along with this software; if not, you may obtain a copy of the License at
 *  http://www.opensource.org/licenses/ecl1.php.
 *
 *  Software distributed under the License is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 *  either express or implied. See the License for the specific language governing rights and limitations
 *  under the License.
 *
 *  Copyright 2002-2009 by Digital Learning Sciences, University Corporation for Atmospheric Research (UCAR).
 *  All rights reserved.
 */

package edu.ucar.dls.index;



/**

 *  This interface is used by Objects wishing to determine when a background intexing process has completed

 *  and to perform additional processing at that time.

 *

 * @author     John Weatherley

 * @see        FileIndexingService#indexFiles(boolean, FileIndexingObserver)

 * @see        FileIndexingService#indexFiles(boolean, File, FileIndexingObserver)

 */

public interface FileIndexingObserver {



	/**  Indicates that indexing completed normally */

	public final static int INDEXING_COMPLETED_SUCCESS = FileIndexingService.INDEXING_SUCCESS;



	/**  Indicates that indexing was aborted by request */

	public final static int INDEXING_COMPLETED_ABORTED = FileIndexingService.INDEXING_ABORTED;



	/**  Indicates that indexing completed with a severe error */

	public final static int INDEXING_COMPLETED_ERROR = FileIndexingService.INDEXING_ERROR;



	/**  Indicates that indexing completed successfully, but one or more item was indexed with errors */

	public final static int INDEXING_COMPLETED_ITEM_ERROR = FileIndexingService.INDEXING_ITEM_ERROR;	



	/**  Indicates that one or more of the indexing directories does not exist */

	public final static int INDEXING_COMPLETED_DIR_DOES_NOT_EXIST = FileIndexingService.INDEXING_DIR_DOES_NOT_EXIST;



	/**  Indicates a read error on one or more of the directories */

	public final static int INDEXING_COMPLETED_DIR_READ_ERROR = FileIndexingService.INDEXING_DIR_READ_ERROR;		

	

	/**

	 *  This method is called when the indexing is complete. This method may then do additional processing that

	 *  is required after indexing and will execute within the same indexing thread, thus blocking all other

	 *  indexing operations until this method is returned.

	 *

	 * @param  status   The status code upon completion

	 * @param  message  A message describing how the indexer completed

	 */

	public void indexingCompleted(int status, String message);



}



