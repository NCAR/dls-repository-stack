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
