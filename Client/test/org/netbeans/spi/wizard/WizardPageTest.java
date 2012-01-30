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
 * WizardPageTest.java
 * JUnit based test
 *
 * Created on August 20, 2005, 10:15 AM
 */

package org.netbeans.spi.wizard;

import java.lang.reflect.InvocationTargetException;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.netbeans.api.wizard.WizardDisplayer;

/**
 * @author tim
 */
public class WizardPageTest extends TestCase {
    public WizardPageTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(WizardPageTest.class);
    }

    public void testCreateWizardFromClassArray() {
        System.out.println("testCreateWizardFromClassArray");
        Class[] clazz = new Class[]{
                A.class, B.class
        };

        Wizard result = WizardPage.createWizard(clazz);
        assertNotNull(result);

        clazz = new Class[]{
                A.class, BadNoDescription.class, B.class
        };

        try {
            WizardPage.createWizard(clazz);
            fail("Only WizardPage classes should be permitted");
        } catch (Exception e) {
            assertNotNull(e);
        }

        clazz = new Class[]{
                A.class, B.class, BadNoDefaultConstructor.class
        };

        try {
            result = WizardPage.createWizard(clazz);
            String[] steps = result.getAllSteps();
            System.err.println("THE LOGGED EXCEPTION THAT FOLLOWS IS SUPPOSED " +
                    "TO BE THROWN AND IS NOT AN ERROR");
            for (int i = 0; i < steps.length; i++) {
                // Attempt to use the default constructor will throw an
                // exception when we get to BadNoDefaultConstructor
                result.navigatingTo(steps[i], new HashMap());
            }
            // SimpleWizardInfo absorbs the runtime exception and returns
            // a valid panel with the error message attached.
            // fail("Invalid constructor should not be permitted");
        } catch (Exception e) {
            assertNotNull(e);
        }
    }

    public void testCreateWizardFromPageArray() {
        System.out.println("testCreateWizardFromPageArray");

        String[] ids = new String[]{
                "1", "2", "3"
        };

        Wizard wiz = WizardPage.createWizard(new WizardPage[]{
                new AP(ids[0]),
                new BP(ids[1]),
                new CP(ids[2]),
        });

        String[] fromWizIds = wiz.getAllSteps();
        assertEquals("Step IDs should match", Arrays.asList(ids), Arrays.asList(fromWizIds));
    }

    public void testUniquify() {
        Set s = new HashSet();
        String hello = "Hello".intern();
        for (int i=0; i < 5; i++) {
            String test = Util.uniquify(hello, s);
            if (i > 0) {
                assertFalse (hello.equals(test));
            }
            s.add (test);
        }
    }
    
    public void testWizardPageWithNullIdGetsClassNameAsId() {
        System.out.println("testWizardPageWithNullIdGetsClassNameAsId");
        DP dp = new DP();
        assertNotNull (dp.id);
        assertEquals (DP.class.getName(), dp.id);
    }
    
    public void testTwoPagesOfSameTypeDoNotGetDuplicateIdsInWizard() {
        System.out.println("testTwoPagesOfSameTypeDoNotGetDuplicateIdsInWizard");
        DP[] dp = new DP[] { new DP(), new DP(), new DP() };
        Set s = new HashSet();
        for (int i = 0; i < dp.length; i++) {
            assertFalse (s.contains(dp[i].id));
        }
        WizardPage.createWizard(dp); //will throw exception if duplicate ids
    }
    
    public void testLongDescription() throws Exception {
        System.out.println("testLongDescription");
        final Wizard w = WizardPage.createWizard(new Class[] { AP.class });
        JComponent jc = w.navigatingTo("org.netbeans.spi.wizard.WizardPageTest$AP", new HashMap());
        assertEquals (AP.class, jc.getClass());
        String s = w.getLongDescription("org.netbeans.spi.wizard.WizardPageTest$AP");
        assertEquals ("LONG DESCRIPTION", ((AP) jc).getLongDescription());
        assertEquals ("LONG DESCRIPTION", s);
    }

    public static final class A extends WizardPage {
        public static String getID() {
            return "A";
        }

        public static String getDescription() {
            return "Step a";
        }
    }

    public static final class B extends WizardPage {
        public static String getID() {
            return "B";
        }

        public static String getDescription() {
            return "Step b";
        }
    }

    public static final class BadNoDescription extends WizardPage {
        public static String getID() {
            return "No Description Page";
        }
    }

    public static final class BadNoDefaultConstructor extends WizardPage {
        public BadNoDefaultConstructor(String foo) {
        }

        public static String getID() {
            return "No Default Constructor Page";
        }

        public static String getDescription() {
            return "No Default Constructor";
        }
    }

    public static final class AP extends WizardPage {
        public AP() {
            this ("one");
        }
        
        public AP(String id) {
            super(id, "one", true);

            setLayout(new BorderLayout());
            JCheckBox jcb = new JCheckBox("The sky is blue");
            jcb.setName("blueSky");
            add(jcb, BorderLayout.CENTER);
            setLongDescription("LONG DESCRIPTION");
        }
        
        public static String getID() {
            return "one";
        }
        
        public static String getDescription() {
            return "one desc";
        }
    }

    public static final class BP extends WizardPage {
        public BP(String id) {
            super(id, "two", true);

            setLayout(new BorderLayout());
            JTextField field = new JTextField("I am a dog");
            field.setName("dogField");
            add(field, BorderLayout.CENTER);
        }
    }

    public static final class CP extends WizardPage {
        public CP(String id) {
            super(id, "three", true);

            setLayout(new BorderLayout());
            JComboBox combo = new JComboBox();
            ComboBoxModel mdl = new DefaultComboBoxModel(new String[]{"first", "second", "third"});
            mdl.setSelectedItem("second");
            combo.setModel(mdl);
            combo.setName("combo");
            add(combo, BorderLayout.CENTER);
        }
    }
    
    private static final class DP extends WizardPage {
        public DP() {
            super("Something");
        }
    }
}
