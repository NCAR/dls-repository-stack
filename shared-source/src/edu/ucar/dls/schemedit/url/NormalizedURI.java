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

package edu.ucar.dls.schemedit.url;

/**
 * @author Edwin Shin
 */
public interface NormalizedURI {
    
    public void normalize();
    
    /**
     * Performs the following:
     *  Case Normalization
     *  Percent-Encoding Normalization
     *  Path Segment Normalization
     *
     */
    public void normalizeSyntax();
    
    /**
     * Case Normalization (see RFC3986 6.2.2.1)
     *
     */
    public void normalizeCase();
    
    /**
     * Percent-Encoding Normalization (see RFC3986 6.2.2.2)
     *
     */
    public void normalizePercentEncoding();
    
    /**
     * Path Segment Normalization (see RFC3986 6.2.2.3)
     *
     */
    public void normalizePathSegment();
    
    /**
     * Scheme-Based Normalization (see RFC3986 6.2.3)
     *
     */
    public void normalizeByScheme();
    
    /**
     * Protocol-Based Normalization (see RFC3986 6.2.4)
     *
     */
    public void normalizeByProtocol();

}
