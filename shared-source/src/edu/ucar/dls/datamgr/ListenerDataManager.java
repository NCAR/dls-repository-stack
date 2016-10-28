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

package edu.ucar.dls.datamgr;

import java.util.*;

/**
 * Provides an abstract implementation of a common interface for applications
 * needing to work with a data store. Implementers of <code>DataListener</code>
 * register with a <code>DataManager</code> instance. An instance of data is
 * uniquely identified using an object identifier. Applications must request
 * locks to restrict writing of data.
 * <p>
 * @author	Dave Deniman, John Weatherley
 * @version	1.0,	9/30/02
 */
public abstract class ListenerDataManager {

	/**
	 * Retrieves a single data object.
	 *
	 * @param oid	The data identifier
	 * @return <code>Object</code> of data 
	 */
	public abstract Object get(String oid)
		throws OIDDoesNotExistException;
	
	/**
	 * Retrieves a <code>List</code> of data objects.
	 *
	 * @param oids	<code>List</code> of data identifiers
	 * @return <code>List</code> of corresponding data objects
	 */
	public abstract List get(List oids)
		throws OIDDoesNotExistException;

	/**
	 * Adds a new object of data.
	 *
	 * @param oid	The unique identifier that references the new data object
	 * @param obj	The new data object
	 * @return The added data object, if successful
	 */
	public abstract Object put(String oid, Object obj)
		throws OIDAlreadyExistsException, ObjectNotSupportedException;
	
	/**
	 * Removes an existing data object, assuming the caller had requested and 
	 * recieved the necessary lock.
	 *
	 * @param oid	The unique identifier that references the data object to be removed
	 * @param lockKey	The key corresponding to the lock on this data object
	 * @return The removed data object, if successful
	 */
	public abstract Object remove(String oid, String lockKey)
		throws OIDDoesNotExistException, InvalidLockException;
	/**
	 * Removes an existing data object iff the object is not locked. If the object is locked 
	 * a LockNotAvailableException is thrown.  
	 *
	 * After successful completion the object is removed from the DataManager
	 * and returned to the caller.
	 *
	 * @param oid	The unique identifier that references the data object to be removed
	 */
	public abstract Object remove(String oid)
		throws OIDDoesNotExistException, LockNotAvailableException;

	/**
	 * Deletes an existing data object, assuming the caller had requested and 
	 * recieved the necessary lock. Similar to
	 * <code>remove()</code> except no object is returned and thus is more efficient
	 * if an object is not required.
	 *
	 * @param oid	The unique identifier that references the data object to be deleted
	 * @param lockKey	The key corresponding to the lock on this data object
	 */
	public abstract void delete(String oid, String lockKey)
	throws OIDDoesNotExistException, InvalidLockException;
	
	
	/**
	 * Deletes an existing data object iff the object is not locked. If the object is locked 
	 * a LockNotAvailableException is thrown.  Similar to
	 * <code>remove()</code> except no object is returned and thus is more efficient.
	 *
	 * After successful completion the object is deleted from the DataManager.
	 *
	 *
	 * @param oid	The unique identifier that references the data object to be deleted
	 */
	public abstract void delete(String oid)
		throws OIDDoesNotExistException, LockNotAvailableException;
	

	/**
	 * Updates a new object of data, assuming the caller had requested and 
	 * recieved the necessary lock.
	 *
	 * @param oid	The unique identifier that references the data object to be updated
	 * @param obj	The new updated data object
	 * @param lockKey	The key corresponding to the lock on this data object
	 * @return The updated data object, if successful
	 */
	public abstract Object update(String oid, Object obj, String lockKey)
		throws OIDDoesNotExistException, ObjectNotSupportedException, InvalidLockException;

	/**
	 * Updates a new object of data iff the object is not locked. If the object is locked 
	 * a LockNotAvailableException is thrown. 
	 *
	 * @param oid	The unique identifier that references the data object to be updated
	 * @param obj	The new updated data object
	 * @return 		The updated data object, if successful otherwise null
	 */
	public abstract Object update(String oid, Object obj)
	throws OIDDoesNotExistException, ObjectNotSupportedException, LockNotAvailableException;

	/**
	 * Request a lock for a data object.
	 *
	 * @param oid	The unique identifier that references the data object to be locked
	 * @return The key for this lock, as a String, if successful
	 */
	public abstract String lock(String oid)
		throws OIDDoesNotExistException, LockNotAvailableException;
	
	/**
	 * Remove the lock on a data object.
	 *
	 * @param oid	The unique identifier that references the locked data object
	 * @param lockKey	The key corresponding to the lock on this data object
	 * @return <b>true</b> if successful, false otherwise
	 */
	public abstract boolean unlock(String oid, String lockKey)
		throws OIDDoesNotExistException, InvalidLockException;	

	/**
	 * Determines whether an object with the given oid exists in the 
	 * DataManager.
	 * @param oid	The unique identifier that references the data object
	 * @return 		True iff the given object exists in this DataManager
	 */
	public abstract boolean oidExists(String oid);


	/**
	 * Determine whether a given object is locked.
	 *
	 * @param oid	The unique identifier that references the data object
	 * @return 		True iff the object referred to by this oid is locked
	 */
	public abstract boolean isLocked(String oid);
		
	/**
	 * Determine whether a given object is locked with the given key.
	 *
	 * @param oid		The unique identifier that references the data object
	 * @param lockKey	The lock key
	 * @return 			True iff the object referred to by this oid is locked with 
	 * 					the given key
	 */
	public abstract boolean isValidLock(String oid, String lockKey);


	/**
	 * Add a <code>DataListener</code> to this <code>DataManager</code>.
	 *
	 * @param listener	The listener to add
	 */
	public abstract void addListener(DataListener listener); 

	/**
	 * Remove a <code>DataListener</code> from this <code>DataManager</code>.
	 *
	 * @param listener	The listener to remove
	 */
	public abstract void removeListener(DataListener listener);

	/**
	 * Construct the DataEvent object and invoke the appropriate method
	 * of all the listeners to this DataManager.
	 *
	 * @param dataToAdd	The list of OIDs for data added to this manager
	 */
	protected abstract void fireDataAddedEvent(List dataToAdd);

	/**
	 * Construct the DataEvent object and invoke the appropriate method
	 * of all the listeners to this DataManager.
	 *
	 * @param dataToChange	The list of OIDs for data changed by this manager
	 */
	protected abstract void fireDataChangedEvent(List dataToChange);

	/**
	 * Construct the DataEvent object and invoke the appropriate method
	 * of all the listeners to this DataManager.
	 *
	 * @param dataToRemove	The list of OIDs for data removed by this manager
	 */
	protected abstract void fireDataRemovedEvent(List dataToRemove);

}
