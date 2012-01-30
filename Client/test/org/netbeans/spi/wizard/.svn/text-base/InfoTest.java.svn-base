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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JPanel;

import junit.framework.*;
import org.netbeans.modules.wizard.MergeMap;

/**
 * @author tim
 */
public class InfoTest extends TestCase {
    private PanelProviderImpl impl;
    private SimpleWizardInfo info;
    private SimpleWizard wiz;

    public InfoTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(InfoTest.class);
    }

    protected void setUp() throws Exception {
        super.setUp();

        impl = new PanelProviderImpl();
        info = new SimpleWizardInfo(impl);
        wiz = new SimpleWizard(info);
    }

    public void testSetWizard() {
        System.out.println("testSetWizard");
        assertEquals(wiz, info.getWizard());
    }

    public void testGetWizard() {
        System.out.println("testGetWizard");
        assertEquals(wiz, info.getWizard());

        wiz = null;

        for (int i = 0; i < 10; i++) {
            System.gc();
        }

        assertNull(info.getWizard());
    }

    public void testCreatePanel() {
        System.out.println("testCreatePanel");

        //Okay, this tests nothing but our impl...
        String id = "a";
        JComponent comp = impl.createPanel(info.controller, id, new MergeMap("a"));
        assertNotNull(comp);
        assertEquals(id, comp.getName());
    }

    public void testRecycleExistingPanel() {
        System.out.println("testRecycleExistingPanel");

        MergeMap settings = new MergeMap("a");

        JComponent comp = wiz.navigatingTo("a", settings);

        assertEquals(1, settings.size());
        assertEquals(Boolean.TRUE, settings.get(settings.keySet().iterator().next()));

        settings.push("b");

        JComponent comp2 = wiz.navigatingTo("b", settings);
        assertEquals(2, settings.size());
        assertEquals(settings.keySet(), new HashSet(Arrays.asList(new String[]{"a", "b"})));

        settings.popAndCalve();
        JComponent comp3 = wiz.navigatingTo("a", settings);
        assertNull(settings.get("b"));

        assertSame(comp3, comp);

        impl.assertRecycled(comp3);
        impl.assertRecycledId("a");
        impl.clear();

        settings.push("b");
        JComponent comp4 = wiz.navigatingTo("b", settings);
        assertSame(comp4, comp2);

        impl.assertRecycled(comp4);
        impl.assertRecycledId("b");
    }

    public void testSetProblem() {
        System.out.println("testSetProblem");

        WL l = new WL(wiz);

        info.setProblem("problem");
        assertFalse(info.isValid());
        l.assertCanProceedChanged("Setting problem should fire an event");

        info.setProblem("problem");
        assertFalse(info.isValid());
        l.assertCanProceedChanged("Setting the problem again should still fire an event");

        info.setProblem(null);
        assertTrue(info.isValid());
        l.assertCanProceedChanged("Setting problem to null should fire an event");

        info.setProblem("problem");
        assertFalse(info.isValid());
        l.assertCanProceedChanged("ResSetting problem should fire an event");
    }

    public void testSetCanFinish() {
        System.out.println("testSetCanFinish");

        assertFalse(info.canFinish());
        info.setProblem(null);
        info.setForwardNavigationMode(WizardController.MODE_CAN_FINISH);
        assertTrue(info.canFinish());

        info.setProblem("problem");
        assertFalse(info.canFinish());

        info.setProblem(null);
        assertTrue(info.canFinish());
    }

    public void testGetTitle() {
        System.out.println("testGetTitle");
        assertEquals("Test Wizard", wiz.getTitle());
    }

    public void testFire() {
        WL wl = new WL(wiz);
        wl.assertNoChange("No events should have been fired at startup");

        info.fire();
        wl.assertCanProceedChanged("Event should have been fired");
    }

    private static class WL implements WizardObserver {
        private WizardImplementation wiz;
        private boolean cpChanged;

        public WL(SimpleWizard wiz) {
            this.wiz = wiz;
            wiz.addWizardObserver(this);
        }

        public void stepsChanged(Wizard wizard) {
//            assertSame(wizard, wiz);
        }

        public void navigabilityChanged(Wizard wizard) {
//            assertSame(wizard, wiz);
            cpChanged = true;
        }

        public void selectionChanged(Wizard wizard) {
//            assertSame(wizard, wiz);
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
