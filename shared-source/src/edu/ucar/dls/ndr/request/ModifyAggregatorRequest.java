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
package edu.ucar.dls.ndr.request;

import edu.ucar.dls.ndr.apiproxy.NDRConstants;

public class ModifyAggregatorRequest extends SignedNdrRequest {
	
	public ModifyAggregatorRequest() {
		super ("modifyAggregator");
		this.setObjectType (NDRConstants.NDRObjectType.AGGREGATOR);
	}	

	public ModifyAggregatorRequest (String mdHandle) {
		this();
		this.handle = mdHandle;
	}
	
	public void setAggregatorFor(String agentHandle) {
		this.addCommand("relationship", "aggregatorFor", agentHandle);
	}
	
	public void associateWith(String resourceHandle) {
		this.addCommand("relationship", "associatedWith", resourceHandle);
	}	

}
