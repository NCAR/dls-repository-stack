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

package edu.ucar.dls.schemedit.struts;


import org.apache.struts.config.impl.ModuleConfigImpl;

/**
 * <p>The collection of static configuration information that describes a
 * Struts-based module.  Multiple modules are identified by
 * a <em>prefix</em> at the beginning of the context
 * relative portion of the request URI.  If no module prefix can be
 * matched, the default configuration (with a prefix equal to a zero-length
 * string) is selected, which is elegantly backwards compatible with the
 * previous Struts behavior that only supported one module.</p>
 *
 * @version $Rev: 54929 $ $Date: 2009/03/20 23:33:57 $
 * @since Struts 1.1
 */
public class MyModuleConfig extends ModuleConfigImpl {
    
    /**
     * Construct an ModuleConfigImpl object according to the specified
     * parameter values.
     *
     * @param prefix Context-relative URI prefix for this module
     */
    public MyModuleConfig(String prefix) {
        super(prefix);

        this.actionMappingClass = "edu.ucar.dls.schemedit.struts.HotActionMapping";

    }


}
