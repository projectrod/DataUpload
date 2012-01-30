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
 * SimpleWizardTest.java
 * JUnit based test
 *
 * Created on March 2, 2005, 11:18 PM
 */

package org.netbeans.spi.wizard;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.netbeans.modules.wizard.MergeMap;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author tim
 */
public class SimpleWizardTest extends TestCase {
    public SimpleWizardTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(SimpleWizardTest.class);
    }

    /**
     * Test of getAllIDs method, of class org.netbeans.spi.wizard.SimpleWizard.
     */
    public void testGetAllIDs() {
        System.out.println("testGetAllIDs");
        PanelProviderImpl impl = new PanelProviderImpl();
        SimpleWizard wiz = new SimpleWizard(impl);
        List l = Arrays.asList(wiz.getAllSteps());
        assertEquals(l, Arrays.asList(new String[]{"a", "b", "c"}));
    }

    public void testRemoveWizardListener() {
        System.out.println("testRemoveWizardListener");
        PanelProviderImpl impl = new PanelProviderImpl();
        SimpleWizardInfo info = new SimpleWizardInfo(impl);
        SimpleWizard wiz = new SimpleWizard(info);
        WL wl = new WL(wiz);

        info.fire();
        wl.assertCanProceedChanged("Should have fired");

        wiz.removeWizardObserver(wl);

        info.fire();
        wl.assertNoChange("Should no longer be listening, but got an event");
    }

    public void testCanFinish() {
        // TODO add your test code below by replacing the default call to fail.
    }

    public void testGetDescription() {
        System.out.println("testGetDescription");
        PanelProviderImpl impl = new PanelProviderImpl();
        SimpleWizard wiz = new SimpleWizard(impl);

        assertEquals("d_a", wiz.getStepDescription("a"));
        assertEquals("d_b", wiz.getStepDescription("b"));
        assertEquals("d_c", wiz.getStepDescription("c"));

        try {
            wiz.getStepDescription("something");
            fail("IAE should have been thrown");
        } catch (IllegalArgumentException e) {
            assertNotNull("IllegalArgumentException should have been thrown", e);
        }
    }

    public void testNavToPanel() {
        System.out.println("testNavToPanel");
        PanelProviderImpl impl = new PanelProviderImpl();
        SimpleWizardInfo info = new SimpleWizardInfo(impl);
        SimpleWizard wiz = new SimpleWizard(info);

        assertNotNull(wiz.getNextStep());
        assertNull(wiz.getPreviousStep());

        MergeMap settings = new MergeMap("a");
        JComponent comp = wiz.navigatingTo("a", settings);
        assertNotNull(comp);
        assertEquals("a", comp.getName());

        info.setProblem(null);
        assertEquals("b", wiz.getNextStep());
        assertNull(wiz.getPreviousStep());

        settings.push("b");
        JComponent comp1 = wiz.navigatingTo("b", settings);
        assertNotSame(comp, comp1);
        assertEquals("c", wiz.getNextStep());
        assertEquals("a", wiz.getPreviousStep());
        assertEquals("b", comp1.getName());

        settings.push("c");
        JComponent comp2 = wiz.navigatingTo("c", settings);
        assertNotSame(comp2, comp);
        assertNotSame(comp2, comp1);
        assertEquals("c", comp2.getName());
        info.setProblem(null);
        info.setForwardNavigationMode(WizardController.MODE_CAN_FINISH);
        assertNull(wiz.getNextStep());

        settings.popAndCalve();
        JComponent comp3 = wiz.navigatingTo("b", settings);
        assertSame(comp3, comp1);
        assertEquals("b", comp3.getName());
        info.setProblem(null);
        assertEquals("c", wiz.getNextStep());
        assertEquals("a", wiz.getPreviousStep());
        info.setProblem("problem");
        assertNull("On invalid pane, nextID should be null", wiz.getNextStep());
        info.setProblem(null);
        assertEquals("c", wiz.getNextStep());

        impl.clear();

        settings.push("c");
        JComponent comp4 = wiz.navigatingTo("c", settings);
        assertSame(comp4, comp2);
        impl.assertCurrent("c");
        impl.assertRecycled(comp4);
        impl.assertRecycledId("c");

        boolean canContinue = (wiz.getForwardNavigationMode() & WizardController.MODE_CAN_CONTINUE) != 0;
        boolean canFinish = (wiz.getForwardNavigationMode() & WizardController.MODE_CAN_FINISH) != 0;

        assertFalse(canContinue);
        assertTrue(canFinish);

        try {
            assertEquals("finished", wiz.finish(settings));
        } catch (WizardException e) {
            fail("Exception thrown");
        }
    }

    private static class WL implements WizardObserver {
        private WizardImplementation wiz;
        private boolean cpChanged;

        public WL(SimpleWizard wiz) {
            this.wiz = wiz;
            wiz.addWizardObserver(this);
        }

        public void stepsChanged(Wizard wizard) {
//            assertEquals(wizard, wizWiz);
        }

        public void navigabilityChanged(Wizard wizard) {
//            assertEquals(wizard, wizWiz);
            cpChanged = true;
        }

        public void selectionChanged(Wizard wizard) {
//            assertEquals(wizard, wizWiz);
        }

        public void assertNoChange(String msg) {
            assertFalse(msg, cpChanged);
        }

        public void assertCanProceedChanged(String msg) {
            boolean was = cpChanged;
            cpChanged = false;
            assertTrue(msg, was);
        }
    }

    private static class PanelProviderImpl extends WizardPanelProvider {
        private boolean finished = false;
        private int step = -1;
        private String currId;

        private JComponent recycled;
        private String recycledId;
        private Map recycledSettings;

        PanelProviderImpl(String title, String[] steps, String[] descriptions) {
            super(title, steps, descriptions);
        }

        PanelProviderImpl() {
            super("Test Wizard", new String[]{"a", "b", "c"}, new String[]{"d_a", "d_b", "d_c"});
        }


        protected JComponent createPanel(WizardController c, String id, Map settings) {
            step++;
            currId = id;
            settings.put(id, Boolean.TRUE);

            JPanel result = new JPanel();
            result.setName(id);

            return result;
        }

        protected Object finish(Map settings) {
            return "finished";
        }

        public void assertCurrent(String id) {
            assertEquals("Current Step", currId, id);
        }

        protected void recycleExistingPanel(String id, WizardController controller, Map settings, JComponent panel) {
            recycled = panel;
            recycledId = id;
            recycledSettings = settings;
        }

        public void assertRecycledSettingsContains(String key, String value) {
            assertNotNull(recycledSettings);
            assertEquals(value, recycledSettings.get(key));
        }

        public void assertRecycled(JComponent panel) {
            assertNotNull(recycled);
            assertSame(panel, recycled);
        }

        public void assertRecycledId(String id) {
            assertNotNull(recycledId);
            assertEquals(id, recycledId);
        }

        public void clear() {
            recycled = null;
            recycledId = null;
            recycledSettings = null;
        }

        public void assertStep(int step, String msg) {
            assertEquals(msg, step, this.step);
        }

        public void assertFinished(String msg) {
            assertTrue(msg, finished);
        }

        public void assertNotFinished(String msg) {
            assertFalse(msg, finished);
        }
    }
}
