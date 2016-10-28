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

package edu.ucar.dls.logging;

import java.util.logging.Level;


/**
 * Overall log manager.  See {@link ClfLogger ClfLogger} and
 * {@link DebugLogger DebugLogger} for usage info.
 */

public class DleseLogManager {

/** Standard debug log format. */
public static int LOG_STD = 1;

/** Debug log is created in text format, as opposed to XML. */
public static int LOG_TEXT = 1;

/** Debug log is created in XML format, as opposed to text. */
public static int LOG_XML = 2;

/** Time stamps debug logs are in UTC (GMT) time, as opposed to local time */
public static int LOG_UTCTIME = 1;

/** Time stamps debug logs are in local time, as opposed to UTC time */
public static int LOG_LOCALTIME = 2;

/**
 * Returns a {@link ClfLogger ClfLogger}.
 * @param loggerLevel  The minumum level message this logger will accept.
 * @param appendFlag   If true, we append to outfile
 * @param outfilename  The name of output file
 */

public static ClfLogger getClfLogger(
	Level loggerLevel,		// one of java.util.logging.Level.*
	boolean appendFlag,		// if true, we append to outfile
	String outfilename)		// name of output file
throws LogException
{
	return new ClfLogger( loggerLevel, appendFlag, outfilename);
}




/**
 * Returns a {@link DebugLogger DebugLogger}.
 * @param outputType   Must always be LOG_STD.  For future flexibility.
 * @param timeType     The type of time stamp:
 *                     Either LOG_UTCTIME or LOG_LOCALTIME
 * @param outputFormat Either LOG_XML or LOG_TEXT.  Normally LOG_TEXT.
 * @param loggerLevel  The minumum level message this logger will accept.
 * @param appendFlag   If true, we append to outfile
 * @param outfilename  The name of output file
 */

public static DebugLogger getDebugLogger(
	int outputType,			// For now, always LOG_STD
	int timeType,			// Either LOG_UTCTIME or LOG_LOCALTIME
	int outputFormat,		// Either LOG_XML or LOG_TEXT
	Level loggerLevel,		// minimum level that will be logged
	boolean appendFlag,		// if true, append to outputFile
	String outfilename)		// name of output file
throws LogException
{
	return DebugLogger.getDebugLogger(
		outputType,
		timeType,
		outputFormat,
		loggerLevel,
		appendFlag,
		outfilename);

} // end getDebugLogger

} // end class DleseLogManager

