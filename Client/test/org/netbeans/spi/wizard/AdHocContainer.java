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
 * AdHocContainer.java
 *
 * Created on September 22, 2006, 12:14 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.netbeans.spi.wizard;

import java.awt.FlowLayout;
import javax.swing.JComponent;

/**
 *
 * @author Tim Boudreau
 */
public class AdHocContainer extends JComponent {
    public AdHocContainer() {
        setLayout (new FlowLayout());
    }
}
