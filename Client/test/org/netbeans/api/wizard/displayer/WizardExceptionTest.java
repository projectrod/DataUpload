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
package org.netbeans.api.wizard.displayer;

import java.awt.event.ComponentEvent;
import org.netbeans.spi.wizard.*;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ComponentListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import junit.framework.TestCase;

/**
 * Tests that WizardException causes the wizard to return to the correct
 * step.
 *
 * @author Tim Boudreau
 */
public class WizardExceptionTest extends TestCase {
    public WizardExceptionTest(String x) {
        super (x);
    }

    WPP wpp;
    Wizard wiz;
    WizardDisplayerImpl displayer;
    Object wizardResult;
    JButton next;
    JButton prev;
    JButton finish;
    JButton cancel;
    NavButtonManager mgr;
    protected void setUp() throws Exception {
        wpp = new WPP();
        wiz = wpp.createWizard();
        show (wiz, null);
        synchronized (wpp) {
            wpp.wait(2000);
        }
        mgr = displayer.getButtonManager();
        mgr.suppressMessageDialog = true;
        assertNotNull(mgr);
        next = mgr.getNext();
        prev = mgr.getPrev();
        finish = mgr.getFinish();
        cancel = mgr.getCancel();
        assertNotNull (next);
        assertNotNull(prev);
        assertNotNull (finish);
        assertNotNull (cancel);
    }

    public void testFinishThrowsExceptionAndReturnsToCorrectStep() throws Exception {
        assertEquals ("One", wiz.getCurrentStep());
        assertTrue (click (next, false));
        assertEquals ("Two", wiz.getCurrentStep());
        assertFalse (finish.isEnabled());
        assertTrue (click (next, false));
        assertEquals ("Three", wiz.getCurrentStep());
        assertFalse (finish.isEnabled());
        assertTrue (click (next, false));
        assertEquals ("Four", wiz.getCurrentStep());
        assertTrue (finish.isEnabled());
        assertTrue (click (finish, false));
        assertEquals ("Two", wiz.getCurrentStep());
        assertNotNull (wpp.get("Two"));
        assertTrue (wpp.get("Two").isDisplayable());
        assertTrue (wpp.get("Two").isShowing());
    }

    public void testFinishThrowsExceptionAndReturnsToCorrectStepWhenItIsFirstStep() throws Exception {
        wpp.returnTo = "One";
        assertEquals ("One", wiz.getCurrentStep());
        assertTrue (click (next, false));
        assertEquals ("Two", wiz.getCurrentStep());
        assertFalse (finish.isEnabled());
        assertTrue (click (next, false));
        assertEquals ("Three", wiz.getCurrentStep());
        assertFalse (finish.isEnabled());
        assertTrue (click (next, false));
        assertEquals ("Four", wiz.getCurrentStep());
        assertTrue (finish.isEnabled());
        assertTrue (click (finish, false));
        assertEquals ("One", wiz.getCurrentStep());
        assertNotNull (wpp.get("One"));
        assertTrue (wpp.get("One").isDisplayable());
        assertTrue (wpp.get("One").isShowing());
    }

    public void testFinishFailsCorrectlyWhenReturnToStepDoesNotExist() throws Exception {
        wpp.returnTo = "Does Not Exist";
        assertEquals ("One", wiz.getCurrentStep());
        assertTrue (click (next,false));
        assertEquals ("Two", wiz.getCurrentStep());
        assertFalse (finish.isEnabled());
        assertTrue (click (next,false));
        assertEquals ("Three", wiz.getCurrentStep());
        assertFalse (finish.isEnabled());
        assertTrue(click (next,false));
        assertEquals ("Four", wiz.getCurrentStep());
        assertTrue (finish.isEnabled());
        assertFalse ("Exception should have been thrown pressing finish if the " +
                "thrown WizardException says to return to a bogus step", 
                click (finish, true));
        assertEquals ("Four", wiz.getCurrentStep());
        assertNotNull (wpp.get("Four"));
        assertTrue (wpp.get("Four").isDisplayable());
        assertTrue (wpp.get("Four").isShowing());
    }

    private static boolean click(final AbstractButton button, boolean expectFail) {
        boolean result = true;
//        System.err.println("Click " + button.getText());
        try {
            EventQueue.invokeAndWait(new Runnable() {
                public void run() {
                    button.doClick();
                }
            });
        } catch (Exception ie) {
            result = false;
            if (!expectFail) {
                ie.printStackTrace();
                if (ie.getCause() != null) {
                    ie.getCause().printStackTrace();
                }
            }
            if (!expectFail) {
                fail("Exception navigating");
            }
        }
        System.err.println("exit Click " + button.getText());
        return result;
    }


    private static final String [] STEPS = new String[] {
        "One", "Two", "Three", "Four"
    };
    private static final class WPP extends WizardPanelProvider implements ComponentListener {
        boolean fail = true;
        private Map /* <String, Component> */ map = new HashMap();
        String returnTo = "Two";
        WPP () {
            this (STEPS);
        }

        WPP (String[] steps) {
            super (steps, steps);
        }

        Component get(String id) {
            return (Component) map.get(id);
        }

        protected JComponent createPanel(WizardController controller, String id, Map settings) {
            JPanel result = new JPanel (new BorderLayout());
            map.put (id, result);
            JCheckBox box = new JCheckBox (id);
            box.setName (id);
            result.add (box, BorderLayout.CENTER);
            result.addComponentListener(this);
            return result;
        }

        protected Object finish(Map settings) throws WizardException {
            if (fail)
                throw new WizardException ("Life is not good", returnTo);
            else
                return "Foo";
        }

        public void componentResized(ComponentEvent e) {
        }

        public void componentMoved(ComponentEvent e) {
        }

        public void componentShown(ComponentEvent e) {
            synchronized (this) {
                notifyAll();
            }
        }

        public void componentHidden(ComponentEvent e) {
        }
    }

    private void show(final Wizard wiz, final Action helpAction) {
        try {
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    displayer = new WizardDisplayerImpl ();
                    wizardResult = displayer.show(wiz, null, helpAction, null);
                }
            });
            Thread.sleep(1000);
        } catch (Exception e) {
            fail(e.getMessage());
        }

    }

}
