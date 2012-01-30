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
 * GenericListenerTest.java
 * JUnit based test
 *
 * Created on August 20, 2005, 10:51 AM
 */

package org.netbeans.spi.wizard;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import junit.framework.*;
import java.util.EventObject;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

/**
 * Tests that the generic listener for all different component types works.
 *
 * @author Tim Boudreau
 */
public class GenericListenerTest extends TestCase {
    private GenericListener gl;
    private WP wp;
    private WPwithAdHocComponents adhoc;
    private GenericListener adHocListener;

    public GenericListenerTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(GenericListenerTest.class);
    }

    protected void setUp() throws Exception {
        super.setUp();

        Logger.getLogger(GenericListener.class.getName()).setLevel(Level.ALL);
        wp = new WP();
        gl = new GenericListener(wp);
        adhoc = new WPwithAdHocComponents(false);
        adHocListener = new GenericListener (adhoc);
        adhoc.init();
    }

    public void testVisualComponentsWork() throws Exception {
        Logger.getLogger(WizardPage.class.getName()).setLevel(Level.ALL);

        JFrame frame = new JFrame("GenericListener Test");
        frame.getContentPane().setLayout(new BorderLayout());
        LotsOfComponentsPanel componentsPanel = new LotsOfComponentsPanel();
        frame.getContentPane().add(componentsPanel);
        frame.setBounds(20, 20, 800, 600);
        new WaitWindow (frame);

        componentsPanel.tickleAll();
    }
    
    private static class WaitWindow extends WindowAdapter implements Runnable {
        private JFrame frm;
        WaitWindow (JFrame frm) {
            this.frm = frm;
            frm.addWindowListener(this);
            EventQueue.invokeLater(this);
            try {
            synchronized (this) {
                wait(10000);
            }
            } catch (Exception e) {
                fail ("Huh?");
            }
        }
        
        public void run() {
            frm.setVisible(true);
        }
        
        public void windowOpened(WindowEvent e) {
            synchronized (this) {
                notifyAll();
            }
            frm.removeWindowListener(this);
        }
    }
    
    public void testNestedPanels() throws Exception {
        Logger.getLogger(WizardPage.class.getName()).setLevel(Level.ALL);
        Wizard wizard = WizardPage.createWizard(new WizardPage[] { adhoc });
        Map wizardData = new HashMap();
        JComponent component = wizard.navigatingTo("group", wizardData);

        JFrame frame = new JFrame("Nested Panels Test");
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(adhoc);
        frame.setBounds (20, 20, 200, 200);
        adhoc.assertNotValidated();
        new WaitWindow (frame);
        //System.err.println("now clicking");
        adhoc.box.doClick();
        //System.err.println("clicked");
        adhoc.assertPair("box", Boolean.TRUE); //NOI18N
        adhoc.button.doClick();
        adhoc.assertPair("button", Boolean.TRUE); //NOI18N
        adhoc.box.doClick();
        adhoc.assertPair ("box", Boolean.FALSE); //NOI18N
        adhoc.assertValidated();
    }
    public void testDoublyNestedPanels() throws Exception {
        Logger.getLogger(WizardPage.class.getName()).setLevel(Level.ALL);
        adhoc = new WPwithAdHocComponents(true);
        adHocListener = new GenericListener (adhoc);
        adhoc.init();
        testNestedPanels();
    }
    
    public void testRadioButtonGroup() throws Exception {
        ButtonGroupPage page = new ButtonGroupPage();
        Wizard wizard = WizardPage.createWizard(new WizardPage[] { page });
        
        JFrame jf = new JFrame();
        jf.getContentPane().setLayout (new BorderLayout());
        jf.getContentPane().add (page);
        jf.pack();
        jf.setVisible (true);

        Map wizardData = new HashMap();
        JComponent component = wizard.navigatingTo("group", wizardData);
        assertEquals("Component should match page", component, page);

        assertEquals("b1 state", Boolean.TRUE, wizardData.get("b1"));
        assertEquals("b2 state", Boolean.FALSE, wizardData.get("b2"));
        assertEquals("b3 state", Boolean.TRUE, wizardData.get("b3"));
        assertEquals("b4 state", Boolean.FALSE, wizardData.get("b4"));
        assertEquals("b5 state", Boolean.FALSE, wizardData.get("b5"));

        page.pushButton(1);
        assertEquals("b1 state", Boolean.TRUE, wizardData.get("b1"));
        assertEquals("b2 state", Boolean.FALSE, wizardData.get("b2"));

        page.pushButton(2);
        assertEquals("b1 state", Boolean.FALSE, wizardData.get("b1"));
        assertEquals("b2 state", Boolean.TRUE, wizardData.get("b2"));

        page.pushButton(1);
        assertEquals("b1 state", Boolean.TRUE, wizardData.get("b1"));
        assertEquals("b2 state", Boolean.FALSE, wizardData.get("b2"));

        page.pushButton(1);
        assertEquals("b1 state", Boolean.TRUE, wizardData.get("b1"));
        assertEquals("b2 state", Boolean.FALSE, wizardData.get("b2"));

        page.pushButton(2);
        assertEquals("b1 state", Boolean.FALSE, wizardData.get("b1"));
        assertEquals("b2 state", Boolean.TRUE, wizardData.get("b2"));
        assertEquals("b3 state", Boolean.TRUE, wizardData.get("b3"));
        assertEquals("b4 state", Boolean.FALSE, wizardData.get("b4"));
        assertEquals("b5 state", Boolean.FALSE, wizardData.get("b5"));
    }

    public void testToggleButtonGroup() throws Exception {
        ButtonGroupPage page = new ButtonGroupPage();
        Wizard wizard = WizardPage.createWizard(new WizardPage[] { page });

        JFrame jf = new JFrame();
        jf.getContentPane().setLayout (new BorderLayout());
        jf.getContentPane().add (page);
        jf.pack();
        jf.setVisible (true);
        
        Map wizardData = new HashMap();
        JComponent component = wizard.navigatingTo("group", wizardData);
        assertEquals("Component should match page", component, page);

        // Initial state; nothing here should change b1 or b2
        assertEquals("b1 state", Boolean.TRUE, wizardData.get("b1"));
        assertEquals("b2 state", Boolean.FALSE, wizardData.get("b2"));
        assertEquals("b3 state", Boolean.TRUE, wizardData.get("b3"));
        assertEquals("b4 state", Boolean.FALSE, wizardData.get("b4"));
        assertEquals("b5 state", Boolean.FALSE, wizardData.get("b5"));

        page.pushButton(3);
        assertEquals("b3 state", Boolean.TRUE, wizardData.get("b3"));
        assertEquals("b4 state", Boolean.FALSE, wizardData.get("b4"));

        page.pushButton(4);
        assertEquals("b3 state", Boolean.FALSE, wizardData.get("b3"));
        assertEquals("b4 state", Boolean.TRUE, wizardData.get("b4"));

        page.pushButton(3);
        assertEquals("b3 state", Boolean.TRUE, wizardData.get("b3"));
        assertEquals("b4 state", Boolean.FALSE, wizardData.get("b4"));

        page.pushButton(3);
        assertEquals("b3 state", Boolean.TRUE, wizardData.get("b3"));
        assertEquals("b4 state", Boolean.FALSE, wizardData.get("b4"));

        page.pushButton(4);
        assertEquals("b3 state", Boolean.FALSE, wizardData.get("b3"));
        assertEquals("b4 state", Boolean.TRUE, wizardData.get("b4"));

        page.pushButton(5);
        assertEquals("b3 state", Boolean.FALSE, wizardData.get("b3"));
        assertEquals("b4 state", Boolean.TRUE, wizardData.get("b4"));
        assertEquals("b5 state", Boolean.TRUE, wizardData.get("b5"));

        page.pushButton(5);
        assertEquals("b3 state", Boolean.FALSE, wizardData.get("b3"));
        assertEquals("b4 state", Boolean.TRUE, wizardData.get("b4"));
        assertEquals("b5 state", Boolean.FALSE, wizardData.get("b5"));

        page.pushButton(5);
        assertEquals("b1 state", Boolean.TRUE, wizardData.get("b1"));
        assertEquals("b2 state", Boolean.FALSE, wizardData.get("b2"));
        assertEquals("b3 state", Boolean.FALSE, wizardData.get("b3"));
        assertEquals("b4 state", Boolean.TRUE, wizardData.get("b4"));
        assertEquals("b5 state", Boolean.TRUE, wizardData.get("b5"));
    }

    private static class ButtonGroupPage extends WizardPage {
        private JToggleButton[] buttons;

        public ButtonGroupPage() {
            super("group", "Page with a button group");

            createComponent();
        }

        void pushButton(int button) {
            if ((button > 0) && (button <= buttons.length)) {
                buttons[button-1].doClick();
            }
        }

        private void createComponent() {
            buttons = new JToggleButton[5];

            ButtonGroup group = new ButtonGroup();
            buttons[0] = new JRadioButton("button1", true);
            buttons[0].setName("b1");
            group.add(buttons[0]);
            buttons[1] = new JRadioButton("button2");
            buttons[1].setName("b2");
            group.add(buttons[1]);

            group = new ButtonGroup();
            buttons[2] = new JToggleButton("button3", true);
            buttons[2].setName("b3");
            group.add(buttons[2]);
            buttons[3] = new JToggleButton("button4");
            buttons[3].setName("b4");
            group.add(buttons[3]);
            buttons[4] = new JToggleButton("button5");
            buttons[4].setName("b5");
            // Don't add this to any group

            // Adding the buttons will assign the
            // GenericListener to the buttons
            for (int i = 0; i < buttons.length; i++) {
                add(buttons[i]);
            }
        }
    }
    
    private void assertListenedTo(JPanel pnl) {
        assertTrue (Arrays.asList(pnl.getContainerListeners()).contains(gl));
    }

    public void testImmediateChildrenListenedTo() {
        System.out.println("testImmediateChildrenListenedTo");

        JCheckBox cb = new JCheckBox();
        cb.getModel().setSelected(true);
        cb.setName ("checkBox");
        wp.add (cb);
        assertTrue (Arrays.asList(cb.getItemListeners()).contains(gl));
        
        
        JComboBox box = new JComboBox ();
        box.getModel().setSelectedItem("Hooey");
        box.setName("hooeyBox");
        wp.add (box);
        assertTrue (Arrays.asList(box.getActionListeners()).contains(gl));
        
        
        wp.assertPair ("hooeyBox", "Hooey");
        wp.assertPair ("checkBox", Boolean.TRUE);
    }
    
    public void testIndirectChildrenListenedToIfPanelAddedLast() {
        System.out.println("testIndirectChildrenListenedToIfPanelAddedLast");

        JPanel jp = new JPanel();
        
        JCheckBox cb = new JCheckBox();
        cb.getModel().setSelected(true);
        cb.setName ("checkBox");
        jp.add (cb);
        
        
        JComboBox box = new JComboBox ();
        box.getModel().setSelectedItem("Hooey");
        box.setName("hooeyBox");
        jp.add (box);
        
        wp.add (jp);
        
        assertTrue (Arrays.asList(box.getActionListeners()).contains(gl));
        assertTrue (Arrays.asList(cb.getItemListeners()).contains(gl));
        
        
        wp.assertPair ("hooeyBox", "Hooey");
        wp.assertPair ("checkBox", Boolean.TRUE);
    }    
    
    public void testIndirectChildrenListenedToIfPanelAddedFirst() {
        System.out.println("testIndirectChildrenListenedToIfPanelAddedFirst");

        JPanel jp = new JPanel();
        wp.add (jp);
        
        JCheckBox cb = new JCheckBox();
        cb.getModel().setSelected(true);
        cb.setName ("checkBox");
        jp.add (cb);
        
        
        JComboBox box = new JComboBox ();
        box.getModel().setSelectedItem("Hooey");
        box.setName("hooeyBox");
        jp.add (box);
        
        assertTrue (Arrays.asList(box.getActionListeners()).contains(gl));
        assertTrue (Arrays.asList(cb.getItemListeners()).contains(gl));
        
        
        wp.assertPair ("hooeyBox", "Hooey");
        wp.assertPair ("checkBox", Boolean.TRUE);
    }    
    
    public void testVeryIndirectChildrenListenedToIfPanelAddedFirst() {
        System.out.println("testIndirectChildrenListenedToIfPanelAddedFirst");

        JPanel pnl = new JPanel();
        JPanel pnl2 = new JPanel();
        pnl.add (pnl2);
        JPanel pnl3 = new JPanel();
        pnl2.add (pnl3);
        JPanel jp = new JPanel();
        pnl3.add (jp);
        wp.add (jp);
        
        JCheckBox cb = new JCheckBox();
        cb.getModel().setSelected(true);
        cb.setName ("checkBox");
        jp.add (cb);
        
        
        JComboBox box = new JComboBox ();
        box.getModel().setSelectedItem("Hooey");
        box.setName("hooeyBox");
        jp.add (box);
        
        assertTrue (Arrays.asList(box.getActionListeners()).contains(gl));
        assertTrue (Arrays.asList(cb.getItemListeners()).contains(gl));
        
        wp.assertPair ("hooeyBox", "Hooey");
        wp.assertPair ("checkBox", Boolean.TRUE);
    }  
    
    public void testValuesDisappearWhenChildrenRemoved() {
        System.out.println("testValuesDisappearWhenChildrenRemoved");

        JPanel jp = new JPanel();
        wp.add (jp);
        
        JCheckBox cb = new JCheckBox();
        cb.getModel().setSelected(true);
        cb.setName ("checkBox");
        jp.add (cb);
        
        
        JComboBox box = new JComboBox ();
        box.getModel().setSelectedItem("Hooey");
        box.setName("hooeyBox");
        jp.add (box);
        
        assertTrue (Arrays.asList(box.getActionListeners()).contains(gl));
        assertTrue (Arrays.asList(cb.getItemListeners()).contains(gl));
        
        
        wp.assertPair ("hooeyBox", "Hooey");
        wp.assertPair ("checkBox", Boolean.TRUE);
        
        jp.remove (box);
        
        wp.assertNotPresent("hooeyBox");
        
        assertFalse ("Checkbox removed from panel, but GenericListener is" +
                " still listening to it", 
                Arrays.asList(box.getActionListeners()).contains(gl));
        
    } 
    
    
    public void testValuesDisappearWhenAncestorRemoved() {
        System.out.println("testValuesDisappearWhenAncestorRemoved");

        JPanel pnl = new JPanel();
        JPanel pnl2 = new JPanel();
        pnl.add (pnl2);
        JPanel pnl3 = new JPanel();
        pnl2.add (pnl3);
        JPanel jp = new JPanel();
        pnl3.add (jp);

        wp.add (pnl);
        
        assertListenedTo(pnl);
        assertListenedTo(wp);
        assertListenedTo(pnl2);
        assertListenedTo(pnl3);
        assertListenedTo(jp);
        
        
        JCheckBox cb = new JCheckBox();
        cb.getModel().setSelected(true);
        cb.setName ("checkBox");
        jp.add (cb);
        
        
        JComboBox box = new JComboBox ();
        box.getModel().setSelectedItem("Hooey");
        box.setName("hooeyBox");
        jp.add (box);
        
        assertTrue (Arrays.asList(box.getActionListeners()).contains(gl));
        assertTrue (Arrays.asList(cb.getItemListeners()).contains(gl));
        
        
        wp.assertPair ("hooeyBox", "Hooey");
        wp.assertPair ("checkBox", Boolean.TRUE);

        cb.doClick();
        wp.assertPair ("checkBox", Boolean.FALSE);
        
        pnl.remove (pnl2);
        
        wp.assertNotPresent("hooeyBox");
        wp.assertNotPresent("checkBox");
        
        assertFalse ("Checkbox removed from panel, but GenericListener is" +
                " still listening to it", 
                Arrays.asList(box.getActionListeners()).contains(gl));
        
        assertFalse ("Checkbox removed from panel, but GenericListener is" +
                " still listening to it", 
                Arrays.asList(cb.getActionListeners()).contains(gl));
        
    }     
    
    public void testPlainPanelUseWorks() {
        JTextField fld = new JTextField();
        wp.add (fld);
        
        //XXX send some keystrokes, check validation
        
    }

    
    public void testRenamingComponentChangesMapKey() {
        System.out.println("testRenamingFieldChangesMapKey");
        //XXX maybe delete all this and don't support name changes?
        
        //Most components don't support change events for setName(), but we
        //want to support ones that potentially do
        JCheckBox cb = new JCheckBox() {
            public void setName(String name) {
                String old = getName();
                super.setName(name);
                if (old != null) {
                    firePropertyChange("name", old, name);
                }
            }
        };
        cb.getModel().setSelected(true);
        cb.setName ("checkBox");
        wp.add (cb);
        
        wp.assertPair ("checkBox", Boolean.TRUE);
        
        final boolean[] fired = new boolean[] { false };
        cb.addPropertyChangeListener (new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent pce) {
                //System.err.println(" ***** PROPERTY CHANGE! " + 
                //        pce.getPropertyName() + " " + pce.getOldValue() +
                //        "->" + pce.getNewValue());
                if ("name".equals(pce.getPropertyName())) {
                    fired[0] = true;
                }
            }
        });
        
        cb.setName ("nue");
        
        wp.remove(cb);
        wp.add (cb);
        
        try { Thread.sleep (1000); } catch (Exception e) {}
        if (fired[0]) {
            wp.assertPair("nue", Boolean.TRUE);
            wp.assertNotPresent("checkBox");
        }
    }
    
    private class WPwithAdHocComponents extends WP {
        JToggleButton button = new JToggleButton();
        JCheckBox box = new JCheckBox();
        AdHocContainer adhocContainer = new AdHocContainer();
        private final boolean nest;
        public WPwithAdHocComponents(boolean nest) {
            super("adhoc", "An adhoc panel");
            this.nest = nest;
            setLayout (new BorderLayout());
        }
        
        private void init() {
            if (nest) {
                AdHocContainer outer = new AdHocContainer();
                outer.add (adhocContainer);
                add (outer, BorderLayout.CENTER);
            } else {
                add (adhocContainer, BorderLayout.CENTER);
            }
            button.setName ("button");
            box.setName ("box");
            adhocContainer.add(button);
            adhocContainer.add (box);
        }
    }    


    private class WP extends WizardPage {
        private Object evt = null;

        public WP() {
            this("step", "this is a step");
        }
        
        WP (String a, String b) {
            super (a, b, false);
        }

        public Object get(Object key) {
            return getWizardData(key);
        }

        protected String validateContents(Component component, Object event) {
            evt = event;
            return null;
        }

        public void assertValidated() {
            assertValidated(null);
        }

        public void assertValidated(String msg) {
            Object old = evt;
            evt = null;
            assertNotNull(old);
        }

        public void assertNotValidated() {
            assertNull(evt);
        }

        public void assertPair(Object key, Object val) {
            assertEquals("Didn't find or wrong value for " + key + " in " +
                    getWizardDataMap(), val, get(key));
        }

        public void assertNotPresent(Object key) {
            Map m = super.getWizardDataMap();
            assertNull(m + " does not contain " + key, get(key));
        }

        public void assertEventSource(Object o) {
            Object old = evt;
            assertValidated();
            assertTrue("Wrong event type: " + old, old instanceof EventObject);
            assertSame(o, ((EventObject) old).getSource());
        }
    }
    
    
}
