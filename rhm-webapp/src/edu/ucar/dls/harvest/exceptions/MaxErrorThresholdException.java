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
package edu.ucar.dls.harvest.exceptions;

import edu.ucar.dls.harvest.Config;

/**
 * Custom exception class used when max errors occur. This differs from HarvestException
 * in the fact, that we report on the record errors to the user and its a custom error message
 * @author dfinke
 *
 */

public class MaxErrorThresholdException extends HarvestException {
	public MaxErrorThresholdException(float errorRatio)
	{
		super(Config.Exceptions.MAX_ERROR_THRESHOLD_ERROR_CODE,
				String.format(Config.Exceptions.MAX_ERROR_THRESHOLD_ERROR_MSG, 
						errorRatio), "MaxErrorThresholdException"
						);
	}
}
