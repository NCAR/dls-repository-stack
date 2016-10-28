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

import org.apache.struts.action.ActionMapping;
import java.util.*;


/**
 * <p>Subclass of <code>ActionMapping</code> that allows roles to be set (by default they are
 frozen).</p>
 *
 * @version $Rev: 54929 $ $Date: 2009/03/20 23:33:57 $
 */

public class HotActionMapping extends ActionMapping {


    public void setRoles(String roles) {
		// System.out.println ("\tHotActionMapping (" + this.getPath() + ") role set to \"" + roles + "\"");
        this.roles = roles;
        if (roles == null) {
            roleNames = new String[0];
            return;
        }
        ArrayList list = new ArrayList();
        while (true) {
            int comma = roles.indexOf(',');
            if (comma < 0)
                break;
            list.add(roles.substring(0, comma).trim());
            roles = roles.substring(comma + 1);
        }
        roles = roles.trim();
        if (roles.length() > 0)
            list.add(roles);
        roleNames = (String[]) list.toArray(new String[list.size()]);
    }
	
    /**
     * <p>Construct a new instance of this class with the desired default
     * form bean scope.</p>
     */
    public HotActionMapping() {

        super();

    }

}
