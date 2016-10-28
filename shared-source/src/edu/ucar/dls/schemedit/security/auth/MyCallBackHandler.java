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

package edu.ucar.dls.schemedit.security.auth;

import javax.security.auth.callback.*;
import javax.servlet.http.*;

/** 
* Callback handler used by {@link edu.ucar.dls.schemedit.security.auth.SchemEditAuth}
*/
public class MyCallBackHandler implements CallbackHandler {
	
    public void handle(Callback[] callbacks) {
        for (int i = 0; i< callbacks.length; i++) {
            if (callbacks[i] instanceof NameCallback) {
                NameCallback nc = (NameCallback)callbacks[i];
                nc.setName(username);
            } else if (callbacks[i] instanceof PasswordCallback) {
                PasswordCallback pc = (PasswordCallback)callbacks[i];
                pc.setPassword(password.toCharArray());
            }
        }
    }
    public MyCallBackHandler(HttpSession sess) {
        // yet to be implemented
    }

    private String username;
    private String password;

    public MyCallBackHandler(String username, String password) {
        this.username=username;
        this.password=password;
    }

}
