/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.spi.wizard;

import javax.swing.event.DocumentListener;
import junit.framework.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import javax.swing.table.*;
import javax.swing.text.*;
import java.awt.*;
import org.netbeans.api.wizard.WizardDisplayer;

/**
 *
 * @author Tim Boudreau
 */
public class AvoidCellEditorsTest extends TestCase {

    JTable table;
    org.netbeans.spi.wizard.Wizard w;
    Ed ed;

    public void setUp() throws Exception {
        DefaultTableModel mdl = new DefaultTableModel();
        mdl.addColumn("Stuff");
        mdl.addColumn("More stuff");
        mdl.addRow(new Object[]{"hello", "world"});
        mdl.addRow(new Object[]{"goodbye", "world"});
        table = new JTable(mdl);
        table.setName("stuff");
        ed = new Ed();
        table.setCellEditor(ed);
        table.setDefaultEditor(Object.class, ed);
        WizardPage page = new WizardPage("one", true);
        page.setLayout(new BorderLayout());
        table.getSelectionModel().setAnchorSelectionIndex(0);
        table.getSelectionModel().setLeadSelectionIndex(0);

        page.add(table, BorderLayout.CENTER);

        w = WizardPage.createWizard(new WizardPage[]{page});

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                org.netbeans.api.wizard.WizardDisplayer.showWizard(w);
            }
        });
    }

    public void tearDown() throws Exception {
        table.getTopLevelAncestor().setVisible(false);
    }

    private static Component getFocusOwner() throws Exception {
        final Component[] x = new Component[1];
        EventQueue.invokeAndWait(new Runnable() {

            public void run() {
                x[0] = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
            }
        });
        return x[0];
    }

    public void testCellEditorsNotListenedTo() throws Exception {
        int ct = 0;
        System.out.println("Waiting for table to be visible");
        while (!table.isVisible() && ct < 25) {
            Thread.sleep(100);
            ct++;
        }
        if (ct == 10) {
            fail("Timed out waiting for window to appear");
        }
        ct = 0;
        table.requestFocusInWindow();
        table.requestFocus();

        System.out.println("Waiting for focus gained");

        while (getFocusOwner() != table && ct < 30) {
            Thread.sleep(100);
            ct++;
        }
        if (ct == 10) {
            fail("Timed out waiting for table to receive focus - focus owner is " +
                    KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner());
        }
        table.editCellAt(0, 0);
        table.setEditingColumn(0);
        table.setEditingRow(0);
        ed.textField.requestFocus();
        Thread.yield();
        ct = 0;
        EventQueue.invokeAndWait(new Runnable() {

            public void run() {
                Rectangle r = table.getCellRect(0, 0, false);
                MouseEvent[] e = createMouseEvents(table, r.x + 3, r.y + 3, true);
                for (int i = 0; i < e.length; i++) {
                    System.err.println("Dispatching " + e[i]);
                    table.dispatchEvent(e[i]);
                }
                KeyEvent[] k = createKeyEvents(ed.textField);
                for (int i = 0; i < k.length - 3; i++) {
                    System.err.println("Dispatching " + k[i]);
                    ed.textField.dispatchEvent(k[i]);
                }
            }
            });
        Thread.yield();
        Thread.sleep (1000);
        final TableCellEditor editor = table.getCellEditor();
        assertTrue("Expected " + Ed.class.getName() + " not " +
                editor.getClass().getName(), editor instanceof Ed);
        final Ed ee = (Ed) editor;
        ActionListener[] ls = ee.textField.getActionListeners();
        for (int i = 0; i < ls.length; i++) {
            assertFalse("Cell editors should not be listened to", ls[i] instanceof GenericListener);
        }
        Set s = ee.doc.listeners;
        for (Iterator i = s.iterator(); i.hasNext();) {
            assertFalse("Cell editors' documents should not be listened to",
                    i.next() instanceof GenericListener);
        }
        assertTrue ("Text field was not added", ee.textField.isShowing());
        FocusListener[] fl = ee.textField.getFocusListeners();
        
        EventQueue.invokeAndWait(new Runnable() {

            public void run() {
                Rectangle r = table.getCellRect(0, 0, false);
                MouseEvent[] e = createMouseEvents(table, r.x + 3, r.y + 3, true);
                for (int i = 0; i < e.length; i++) {
                    System.err.println("Dispatching " + e[i]);
                    table.dispatchEvent(e[i]);
                }
                KeyEvent[] k = createKeyEvents(ee.textField);
                for (int i = 0; i < k.length; i++) {
                    System.err.println("Dispatching " + k[i]);
                    ee.textField.dispatchEvent(k[i]);
                }
            }
            });

        Thread.yield();
        String val = (String) table.getModel().getValueAt(0, 0);
        assertFalse("Edit did not occur", "hello".equals(val));
        Thread.sleep(10000);
    }

    private MouseEvent[] createMouseEvents(Component c, int x, int y, boolean doubleClick) {
        MouseEvent[] result = new MouseEvent[3];

        long now = System.currentTimeMillis();
        result[0] = new MouseEvent(c, MouseEvent.MOUSE_PRESSED, now, 0, x, y, doubleClick ? 2 : 1, false, MouseEvent.BUTTON1);
        result[1] = new MouseEvent(c, MouseEvent.MOUSE_RELEASED, now, 0, x, y, doubleClick ? 2 : 1, false, MouseEvent.BUTTON1);
        result[2] = new MouseEvent(c, MouseEvent.MOUSE_CLICKED, now, 0, x, y, doubleClick ? 2 : 1, false, MouseEvent.BUTTON1);

        return result;
    }

    private KeyEvent[] createKeyEvents(Component c) {
        KeyEvent[] result = new KeyEvent[6];

        long now = System.currentTimeMillis();
        result[0] = new KeyEvent(c, KeyEvent.KEY_PRESSED, now, 0, KeyEvent.VK_H, 'H', KeyEvent.KEY_LOCATION_STANDARD);
        result[1] = new KeyEvent(c, KeyEvent.KEY_RELEASED, now, 0, KeyEvent.VK_H, 'H', KeyEvent.KEY_LOCATION_STANDARD);
        result[2] = new KeyEvent(c, KeyEvent.KEY_TYPED, now, 0, KeyEvent.VK_UNDEFINED, 'H', KeyEvent.KEY_LOCATION_UNKNOWN);

        result[3] = new KeyEvent(c, KeyEvent.KEY_PRESSED, now, 0, KeyEvent.VK_I, 'I', KeyEvent.KEY_LOCATION_STANDARD);
        result[4] = new KeyEvent(c, KeyEvent.KEY_RELEASED, now, 0, KeyEvent.VK_I, 'I', KeyEvent.KEY_LOCATION_STANDARD);
        result[5] = new KeyEvent(c, KeyEvent.KEY_TYPED, now, 0, KeyEvent.VK_UNDEFINED, 'I', KeyEvent.KEY_LOCATION_UNKNOWN);

        result[3] = new KeyEvent(c, KeyEvent.KEY_PRESSED, now, 0, KeyEvent.VK_ENTER, '\n', KeyEvent.KEY_LOCATION_STANDARD);
        result[4] = new KeyEvent(c, KeyEvent.KEY_RELEASED, now, 0, KeyEvent.VK_ENTER, '\n', KeyEvent.KEY_LOCATION_STANDARD);
        result[5] = new KeyEvent(c, KeyEvent.KEY_TYPED, now, 0, KeyEvent.VK_UNDEFINED, '\n', KeyEvent.KEY_LOCATION_UNKNOWN);
        return result;
    }

    private static final class Ed extends DefaultCellEditor {

        final JTextField textField;
        final Doc doc;

        Ed() {
            this(new JTextField());
        }

        Ed(JTextField textField) {
            super(textField);
            this.textField = textField;
            doc = new Doc();
            textField.setDocument(doc);
        }
    }

    private static final class Doc extends DefaultStyledDocument {

        Set listeners = new HashSet();

        public void addDocumentListener(DocumentListener listener) {
            super.addDocumentListener(listener);
            listeners.add(listener);
        }
    }
}
