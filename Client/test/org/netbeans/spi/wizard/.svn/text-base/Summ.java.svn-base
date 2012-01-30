/*  The contents of this file are subject to the terms of the Common Development
and Distribution License (the License). You may not use this file except in
compliance with the License.
    You can obtain a copy of the License at http://www.netbeans.org/cddl.html
or http://www.netbeans.org/cddl.txt.
    When distributing Covered Code, include this CDDL Header Notice in each file
and include the License file at http://www.netbeans.org/cddl.txt.
If applicable, add the following below the CDDL Header, with the fields
enclosed by brackets [] replaced by your own identifying information:
"Portions Copyrighted [year] [name of copyright owner]" */
/*
 * Summ.java
 *
 * Created on September 24, 2006, 4:47 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.spi.wizard;

import java.awt.Component;

/**
 * Subclass of summary for tests
 *
 * @author Tim Boudreau
 */
public class Summ extends Summary {
    public Summ (String s, Object result) {
        super (s, result);
    }
    public Summ (String[] s, Object result) {
        super (s, result);
    }
    public Summ (Component s, Object result) {
        super (s, result);
    }

    public Component getSummaryComponent() {
        Component retValue;
        s = true;
        retValue = super.getSummaryComponent();
        return retValue;
    }

    private boolean s;
    private boolean r;
    public Object getResult() {
        Object retValue;
        r = true;
        retValue = super.getResult();
        return retValue;
    }
    
    public boolean summaryComponentWasCalled() {
        boolean result = s;
        s = false;
        return result;
    }
    
    public boolean getResultWasCalled() {
        boolean result = r;
        r = false;
        return result;
    }
}
