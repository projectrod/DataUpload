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
package org.netbeans.spi.wizard;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collections;
import java.util.EventObject;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import junit.framework.TestCase;
import org.netbeans.spi.wizard.WizardPage.CustomComponentListener;
import org.netbeans.spi.wizard.WizardPage.CustomComponentNotifier;

/**
 *
 * @author Tim Boudreau
 */
public class CustomComponentListenerTest extends TestCase {
    
    /** Creates a new instance of CustomComponentListenerTest */
    public CustomComponentListenerTest(String nm) {
        super (nm);
    }
    
    private CCL ccl;
    private WP wp;
    public void setUp() {
        ccl = new CCL();
        Logger.getLogger(GenericListener.class.getName()).setLevel(Level.ALL);
        wp = new WP();
        JFrame jf = new JFrame();
        jf.getContentPane().setLayout (new BorderLayout());
        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jf.getContentPane().add (wp, BorderLayout.CENTER);
        jf.pack();
        new WaitWindow (jf);
    }
    
    
    public void testCustomComponentListener () throws Exception {
        System.out.println("testCustomComponentListener");
        Map m = wp.getWizardDataMap();
        assertTrue (m.containsKey ("fooComp"));
        assertTrue (m.containsKey ("barComp"));
        assertTrue (m.containsKey ("bazComp"));
        assertFalse (m.containsKey ("mooComp"));
        assertEquals ("foo", m.get("fooComp"));
        assertEquals ("bar", m.get("barComp"));
        assertEquals ("baz", m.get("bazComp"));
        wp.clear();
        wp.a.setValue("hello");
        synchronized (wp) {
            wp.wait (5000);
        }
        assertTrue (m.containsKey("fooComp"));
        assertEquals ("hello", m.get("fooComp"));
        assertEquals (wp.a, wp.assertValidatedComp());
        doClick (wp.button);
        assertTrue (m.containsKey("other"));
        assertEquals (wp.button, wp.assertValidatedComp());
        wp.clear();
        wp.d.setValue ("nothing");
        assertFalse (m.containsKey ("mooComp"));
        wp.assertNotValidated();
    }
    
    void doClick (final AbstractButton b) throws Exception {
        EventQueue.invokeAndWait(new Runnable() {
            public void run() {
                b.doClick();
            }
        });
    }
    
    private static class Comp extends JComponent {
        Object value;
        Comp (Object value) {
            this.value = value;
        }
        
        public String toString() {
            return "Comp(" + getName() + ":" + getValue() + ")";
        }
        
        void setValue (final Object value) {
            try         {
                java.lang.Runnable r = new java.lang.Runnable() {

                    public void run() {
                        org.netbeans.spi.wizard.CustomComponentListenerTest.Comp.this.value = value;
                        fire();
                    }
                };

                java.awt.EventQueue.invokeLater(r);
            } catch (Exception ex) {
                throw new Error (ex);
            }
        }
        
        public Dimension getPreferredSize() {
            return new Dimension (20, 20);
        }
        
        public void paint (Graphics g) {
            g.setColor (Color.ORANGE);
            g.fillRect (0, 0, getWidth(), getHeight());
        }
        
        private List listeners = Collections.synchronizedList (new LinkedList());
        void fire() {
            ChangeListener[] l = (ChangeListener[]) listeners.toArray (new ChangeListener[0]);
            for (int i = 0; i < l.length; i++) {
                ChangeListener changeListener = l[i];
                l[i].stateChanged(new ChangeEvent(this));
            }
        }
        
        public void addChangeListener (ChangeListener l) {
            listeners.add (l);
        }
        
        public void removeChangeListener (ChangeListener l) {
            listeners.remove (l);
        }
        
        public Object getValue() {
            return value;
        }
    }
    
    private static class CCL extends CustomComponentListener implements ChangeListener {
        private CustomComponentNotifier n;
        public boolean accept(Component c) {
            return c instanceof Comp && c.getName() != null;
        }

        public void startListeningTo(Component c, CustomComponentNotifier n) {
            this.n = n;
            ((Comp) c).addChangeListener (this);
        }

        public void stopListeningTo(Component c) {
            ((Comp) c).removeChangeListener (this);
        }
 
        public void stateChanged(ChangeEvent e) {
            Comp comp = (Comp) e.getSource();
            n.userInputReceived(comp, e);
        }
    
        public Object valueFor(Component c) {
            return ((Comp)c).getValue();
        }
        
        public boolean isContainer(Component c) {
            return c instanceof JPanel;
        }
    }
    
    private class WP extends WizardPage {
        private Object evt = null;

        Comp a = new Comp ("foo");
        Comp b = new Comp ("bar");
        Comp c = new Comp ("baz");
        Comp d = new Comp ("moo");
        AbstractButton button = new JToggleButton ("Other");
        public WP() {
            this("step", "this is a step");
            setLayout (new FlowLayout());
            a.setName ("fooComp");
            b.setName ("barComp");
            c.setName ("bazComp");
            //leave d unnamed - it should not ever get validated
            JPanel jp = new JPanel();
            jp.setLayout (new FlowLayout());
            add (a);
            add (b);
            add (jp);
            jp.add (c);
            jp.add (d);
            button.setName ("other");
            add (button);
        }
        
        WP (String a, String b) {
            super (a, b, true);
        }
        
        protected CustomComponentListener createCustomComponentListener() {
            return ccl;
        }
        
        
        Component validatedComp;
        
        public Component assertValidatedComp () {
            Component result = validatedComp;
            validatedComp = null;
            assertNotNull (result);
            return result;
        }
        
        private void clear() {
            evt = null;
            validatedComp = null;
        }

        public Object get(Object key) {
            return getWizardData(key);
        }
        
        private void setValidatedComp (Component c) {
            validatedComp = c;
        }

        protected String validateContents(Component component, Object event) {
            setValidatedComp (component);
            evt = event;
            synchronized (this) {
                notifyAll();
            }
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
    
    private static final class WaitWindow extends WindowAdapter {
        public WaitWindow (JFrame frm) {
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
}
