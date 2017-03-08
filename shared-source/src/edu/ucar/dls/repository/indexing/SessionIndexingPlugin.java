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
package edu.ucar.dls.repository.indexing;

import edu.ucar.dls.repository.*;

import java.io.*;
import java.util.*;

import edu.ucar.dls.index.*;
import edu.ucar.dls.index.writer.*;
import java.text.*;

import org.apache.lucene.document.Field;

public class SessionIndexingPlugin implements FileIndexingPlugin {
	
	// Multiple sessionIds used in the case of multidoc de-duping...
	private List sessionIds = new ArrayList();
	
	public SessionIndexingPlugin() { }
	
	public SessionIndexingPlugin(String sessionId) { 
		addSessionId(sessionId);	
	}
	
	public void addAllSessionIds(String[] sessionIds) {
		if(sessionIds != null)
			for(int i = 0; i < sessionIds.length; i++)
				addSessionId(sessionIds[i]);
	}

	public void addSessionId(String sessionId) {
		if(sessionId != null && !sessionIds.contains(sessionId))
			sessionIds.add(sessionId);
	}

	
	public void addFields(
				File file, 
				org.apache.lucene.document.Document newDoc, 
				org.apache.lucene.document.Document existingDoc, 
				String docType, 
				String docGroup) {
		for(int i = 0; i < sessionIds.size(); i++)
			newDoc.add(new Field("indexSessionId", sessionIds.get(i).toString(),Field.Store.YES,Field.Index.NOT_ANALYZED));
	}
	
	public List getSessionIds() {
		return sessionIds;
	}
	
	public String[] getSessionIdsArray() {
		return (String[])sessionIds.toArray(new String[]{});
	}	
	
	public String toString() {
		return "SessionIndexingPlugin sessionIds: " + Arrays.toString(getSessionIdsArray());
	}
}

