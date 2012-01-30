/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.api.wizard.displayer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import junit.framework.TestCase;
import org.netbeans.api.wizard.WizardResultReceiver;
import org.netbeans.spi.wizard.Wizard;
import org.netbeans.spi.wizard.WizardException;
import org.netbeans.spi.wizard.WizardPage;
import org.netbeans.spi.wizard.WizardPage.WizardResultProducer;

/**
 *
 * @author Tim Boudreau
 */
public class WizardInAdHocContainerTest extends TestCase {
    
    /** Creates a new instance of WizardInAdHocContainerTest */
    public WizardInAdHocContainerTest(String nm) {
        super (nm);
    }
    
    private JDesktopPane pane;
    private JFrame frm;
    private JInternalFrame in;
    private Wizard wiz;
    private WRR wrr;
    private WRP wrp;
    private WizardDisplayerImpl displayer;
    private JButton next, prev, finish, cancel;
    private PageOne pageOne;
    private PageTwo pageTwo;
    public void setUp() {
        pane = new JDesktopPane();
        frm = new JFrame();
        in = new JInternalFrame();
        in.getContentPane().setLayout (new BorderLayout());
        frm.getContentPane().setLayout (new BorderLayout());
        frm.getContentPane().add(pane, BorderLayout.CENTER);
        pane.setLayout (new BorderLayout());
        pane.add (in, BorderLayout.CENTER);
        in.setVisible (true);
        wrr = new WRR();
        wrp = new WRP();
        displayer = new WizardDisplayerImpl();
        wiz = WizardPage.createWizard(new WizardPage[] { pageOne = new PageOne(), 
            pageTwo = new PageTwo() }, wrp);
        displayer.install(in, BorderLayout.CENTER, wiz, new HA(), null, wrr);
        NavButtonManager mgr = displayer.getButtonManager();
        next = mgr.getNext();
        prev = mgr.getPrev();
        finish = mgr.getFinish();
        cancel = mgr.getCancel();
        new WaitWindow (frm);
    }
    
    public void testFinishInvokesWizardResultReceiver() throws Exception {
        System.out.println("testFinishInvokesWizardResultReceiver");
        assertFalse (next.isEnabled());
        click (pageOne.box);
        assertTrue (next.isEnabled());
        click (next);
        assertFalse (next.isEnabled());
        assertFalse (finish.isEnabled());
        click (pageTwo.box);
        assertTrue (finish.isEnabled());
        click (finish);
        wrp.assertFinished();
        wrr.assertFinished();
        wrr.assertNotCancelled();
        wrp.assertNotCancelled();
    }
    
    public void testCancelInvokesWizardResultReceiver() throws Exception {
        System.out.println("testCancelInvokesWizardResultReceiver");
        assertFalse (next.isEnabled());
        click (pageOne.box);
        assertTrue (next.isEnabled());
        click (next);
        assertFalse (next.isEnabled());
        assertFalse (finish.isEnabled());
        click (pageTwo.box);
        assertTrue (finish.isEnabled());
        assertTrue (cancel.isEnabled());
        click (cancel);
        wrp.assertCancelled();
        wrr.assertCancelled();
        wrp.assertNotFinished();
        wrr.assertNotFinished();
    }
    
    private static void click(final AbstractButton button) {
        try {
            EventQueue.invokeAndWait(new Runnable() {
                public void run() {
                    button.doClick();
                }
            });
        } catch (Exception ie) {
            ie.printStackTrace();
            fail("interrupted");
        }
    }
    
    private static final class WRP implements WizardResultProducer {
        Map fm;
        Map cm;
        public Object finish(Map wizardData) throws WizardException {
            fm = wizardData;
            //System.err.println("WRP FINISH " + wizardData);
            return "Hello";
        }

        public boolean cancel(Map settings) {
            cm = settings;
            //System.err.println("WRP CANCEL " + settings);
            return true;
        }
        
        public Map assertCancelled() {
            Map m = cm;
            cm = null;
            assertNotNull (m);
            return m;
        }
        
        public Map assertFinished() {
            Map m = fm;
            fm = null;
            assertNotNull (m);
            return m;
        }
        
        public void assertNotCancelled() {
            assertNull (cm);
        }
        
        public void assertNotFinished() {
            assertNull (fm);
        }
    }
    
    private final class WRR implements WizardResultReceiver {
        private Map m;
        private Object r;
        public void finished(Object wizardResult) {
            r = wizardResult;
            //System.err.println("WRR FINISH " + wizardResult);
            frm.setVisible(false);
            frm.dispose();
        }

        public void cancelled(Map settings) {
            //System.err.println("WRR CANCELLED " + settings);
            m = settings;
            frm.setVisible (false);
            frm.dispose();
        }
        
        public Map assertCancelled() {
            Map mm = m;
            m = null;
            assertNotNull (mm);
            return mm;
        }
        
        public void assertNotCancelled() {
            assertNull (m);
        }
        
        public Object assertFinished() {
            Object o = r;
            r = null;
            assertNotNull (o);
            return o;
        }
        
        public void assertNotFinished() {
            assertNull (r);
        }
    }
    
    private static final class WaitWindow extends WindowAdapter {
        public WaitWindow (JFrame frm) {
            frm.setBounds (20, 20, 500, 300);
            frm.addWindowListener (this);
            frm.setVisible(true);
            synchronized (this) {
                try {
                    wait (10000);
                } catch (Exception e) {}
            }
        }
    
        public void windowOpened(WindowEvent arg0) {
            synchronized (this) {
                notifyAll();
            }
        }
    }    
    
    // A help action for testing
    private static class HA extends AbstractAction {
        ActionEvent ae = null;

        public void actionPerformed(ActionEvent ae) {
            this.ae = ae;
        }

        public void assertPerformed() {
            ActionEvent x = ae;
            ae = null;
            assertNotNull(x);
        }
    }
    
    private static class PageOne extends WizardPage {
        final JCheckBox box = new JCheckBox ("Check me tender, " +
                "check me sweet, never let me go");
        PageOne() {
            this ("box");
        }
        
        PageOne(String boxName) {
            super (boxName,"First step");
            box.setName (boxName);
            add (box);
        }
        
        public String validateContents (Component c, Object event) {
            return box.isSelected() ? null : "No.";
        }
    }
    
    private static class PageTwo extends WizardPage {
        final JCheckBox box = new JCheckBox ("Another checkbox");
        PageTwo() {
            this ("other");
        }
        
        PageTwo(String boxName) {
            super (boxName,"First step");
            box.setName (boxName);
            add (box);
        }
        
        public String validateContents (Component c, Object event) {
            return box.isSelected() ? null : "No.";
        }
    }
    
    
    
}
