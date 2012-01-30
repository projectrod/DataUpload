package Wizard;


/**
 *
 * @author wolfertdekraker
 */
import com.sun.jmx.snmp.tasks.Task;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import Wizard.SpringUtilities;
import org.netbeans.api.wizard.WizardDisplayer;
import org.netbeans.spi.wizard.Wizard;
import org.netbeans.spi.wizard.WizardController;
import org.netbeans.spi.wizard.WizardException;
import org.netbeans.spi.wizard.WizardPanelProvider;

public class Gui extends WizardPanelProvider {

    JFileChooser fc;
    DataSet ds;
    JProgressBar progressBar;
    Task task;
    private static final String ID0 = "bestandSelecteren";
    private static final String ID1 = "metaGegevens";
    private static final String ID2 = "publicatie";

    public Gui() {
        super("Publicatie App",
                new String[]{ID0, ID1, ID2},
                new String[]{"Bestand", "Meta gegevens", "Publicatie"});
    }

    protected JComponent createPanel(final WizardController wizardController, String str, final Map map) {
        JPanel p = null;

        if (str.equals(ID0)) {
            return Panel0(wizardController);
        } else if (str.equals(ID1)) {
            return Panel1(wizardController);
        } else if (str.equals(ID2)) {
            return Panel2(wizardController);
        }

        return p;
    }

    private JPanel Panel0(final WizardController wizardController) {
        ds = new DataSet();
        getIPAdress();



        ds.setTrackerURL("http://" + getIPAdress() + ":8085");

        wizardController.setProblem("Selecteer een bestand om te publiceren");
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEADING));

        // bestandskiezer
        fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setMultiSelectionEnabled(false);

        // deze willen we er niet op hebben
        fc.remove(4);

        // en deze ook niet
        fc.setControlButtonsAreShown(false);
        p.add(fc);

        ActionListener al = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                File file = fc.getSelectedFile();
                boolean sel = false;

                if (e.getActionCommand().equals("CancelSelection")) {
                    System.out.println("CancelSelection\n");
                }
                if (e.getActionCommand().equals("ApproveSelection")) {
                    System.out.println("ApproveSelection\n");
                    ds.setBestandsNaam(file.getName());
                    ds.setBestandsPath(file.getPath());
                    ds.setOnlyPath(file.getParent());
                    ds.setBestandsGrootte(file.length());
                    sel = true;
                }

                wizardController.setProblem(sel ? null : "Selecteer een bestand om te publiceren");
            }
        };
        fc.addActionListener(al);
        return p;
    }

    private String getIPAdress() {
        String ip = "";
        try {
            InetAddress addr = InetAddress.getLocalHost();
            ip =  addr.getCanonicalHostName();
            //ip = "145.24.222.51";
            System.out.println("todo tracker ip: "+ip);
            
        } catch (Exception e) {
            System.out.println("Kon IP adres niet opvragen: " + e);
        }
        return ip;
    }

    private JPanel Panel1(final WizardController wizardController) {
        //  wizardController.setProblem("Vul de meta gegevens in");


        int width = 200;
        int height = 40;


        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));

        // bestandsformaat:
        JPanel boven = new JPanel(new FlowLayout(FlowLayout.LEADING));
        JLabel ftLabel = new JLabel("Bestands formaat:");
        ftLabel.setMinimumSize(new Dimension(width, height));

        JComboBox ftBox = new JComboBox(new String[]{"XML", "JSON", "CSV", "Plain text", "Word document", "Exel sheet"});

        boven.add(ftLabel);
        boven.add(ftBox);
        p.add(boven);

        p.add(Box.createRigidArea(new Dimension(0, 5)));
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));



        // tabelnaam:
        JPanel mid = new JPanel(new FlowLayout(FlowLayout.LEADING));
        JLabel tbLabel = new JLabel("Bestands formaat:");
        tbLabel.setMinimumSize(new Dimension(width, height));
        JTextField tbField = new JTextField();
        tbField.setSize(width, height);
        mid.add(tbLabel);
        mid.add(tbField);
        p.add(mid);

        String[] labels = {"Bestandsformaat: ", "Datasetnaam: ", "Tabelnaam: ", "Dataset eigenaar ", "Beschrijving: ", "Auteursrechten: "};
        int numPairs = labels.length;

        HashMap<Integer, DocumentListener> map = new HashMap<Integer, DocumentListener>();

        p = new JPanel(new SpringLayout());
        for (int i = 0; i < numPairs; i++) {
            JLabel l = new JLabel(labels[i], JLabel.TRAILING);

            p.add(l);
            if (i == 0) {
                ftBox = new JComboBox(new String[]{"XML", "JSON", "CSV", "Plain text", "Word document", "Excelsheet"});
                ftBox.addActionListener(new java.awt.event.ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        System.out.println("action");
                    }
                });
                l.setLabelFor(ftBox);
                p.add(ftBox);
            } else {

                JTextField textField = new JTextField(1);
                DocumentListener listener = new DocumentListener() {

                    public String s;

                    public void changedUpdate(DocumentEvent e) {
                        updateString(e);
                    }

                    public void removeUpdate(DocumentEvent e) {
                        updateString(e);
                    }

                    public void insertUpdate(DocumentEvent e) {
                        updateString(e);
                    }

                    public void updateString(DocumentEvent e) {
                        try {
                            s = e.getDocument().getText(0, e.getDocument().getLength());
                            System.out.println("" + s);
                        } catch (BadLocationException ex) {
                            ex.printStackTrace();
                        }
                    }
                };
                textField.getDocument().addDocumentListener(listener);

                l.setLabelFor(textField);
                p.add(textField);
            }
        }

        //Lay out the panel.
        SpringUtilities.makeCompactGrid(p,
                numPairs, 2, //rows, cols
                6, 6, //initX, initY
                6, 6);       //xPad, yPad

        //Create and set up the window.


        //Set up the content pane.
        p.setOpaque(true);  //content panes must be opaque

        return p;
    }

    private JPanel Panel2(WizardController wizardController) {
        wizardController.setProblem("Druk op start om het bestand te publiceren");
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEADING));
        return new PublishPanel(ds, wizardController);
    }

    protected Object finish(Map settings) throws WizardException {
        Set keys = settings.keySet();
        Iterator it = keys.iterator();
        while (it.hasNext()) {
            Object key = it.next();
            System.out.println(key + "=" + settings.get(key));
        }
        return settings;
    }

    public static void main(String[] args) {
        WizardPanelProvider provider = new Gui();
        Wizard wizard = provider.createWizard();
        Object result = WizardDisplayer.showWizard(wizard);
    }
}
