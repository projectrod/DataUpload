package Wizard;

/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
import jBittorrentAPI.ConnectionManager;
import jBittorrentAPI.Constants;
import jBittorrentAPI.DownloadManager;
import jBittorrentAPI.IOManager;
import jBittorrentAPI.TorrentFile;
import jBittorrentAPI.TorrentProcessor;
import jBittorrentAPI.Utils;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.beans.*;
import java.io.File;
import java.util.Random;
import org.netbeans.spi.wizard.WizardController;

public class PublishPanel extends JPanel
        implements ActionListener,
        PropertyChangeListener {

    private JProgressBar progressBar;
    private WizardController wizardController;
    private JButton startButton;
    private JTextArea taskOutput;
    private Task task;
    private TorrentProcessor tp;
    private DataSet ds;

    public class Task extends SwingWorker<Void, Void> {
        /*
         * Main task. Executed in background thread.
         */

        private boolean complete = false;

        @Override
        public Void doInBackground() {
            startButton.setText("Annuleren");
            Random random = new Random();
            int progress = 0;
            //Initialize progress property.
            setProgress(0);


//                setProgress(Math.min(progress, 100));
            wizardController.setProblem("Wachten tot het bestand is geupload");
            createTorrent();
            uploadTorrent();

            // voor sharing.
            Constants.SAVEPATH = ds.getOnlyPath() + "/";

            TorrentFile t = tp.getTorrentFile(tp.parseTorrent(ds.getTorrentnaam()));
            System.out.println(t.saveAs);
            if (t != null) {
                DownloadManager dm = new DownloadManager(t, Utils.generateID());
                dm.setT(this);
                dm.startListening(6881, 6889);

                taskOutput.append("Start tracker update.. ");
                dm.startTrackerUpdate();
                taskOutput.append("OK\n");
                taskOutput.append("Bestand beschikbaar stellen, wachten op server..");
                dm.blockUntilCompletion();
                taskOutput.append("Stop Tracker update..");
                dm.stopTrackerUpdate();
                taskOutput.append("OK\n");
                dm.closeTempFiles();
                taskOutput.append("Klaar!\n");

            }
            return null;
        }
        /*
         * Executed in event dispatch thread
         */

        public void done() {
            Toolkit.getDefaultToolkit().beep();
            startButton.setEnabled(true);
            startButton.setText("Voltooid");

            wizardController.setProblem(null);
            startButton.setEnabled(Boolean.FALSE);
        }

        public void updateProgress(int p) {
            setProgress(p);
        }

        public void appendText(String s) {
            taskOutput.append(s);
        }
    }

    public void updateProgress(int p) {
        task.updateProgress(p);
    }

    public void createTorrent() {
        TorrentProcessor tp = new TorrentProcessor();
        tp.setName(ds.getBestandsNaam());
        tp.addFile(ds.getBestandsPath());
        System.out.println(ds.getBestandsPath());

        tp.setAnnounceURL(ds.getTrackerURL() + "/announce");
        tp.setPieceLength(256);
        tp.setComment(ds.toXML());
        tp.setCreator("Wolfert");
        tp.setCreationDate(System.currentTimeMillis());
        tp.setEncoding("UTF8");


        //  tp.setTorrentData(ds.getTrackerURL() + "/announce", 107, ds.toXML(), "UTF8", ds.getBestandsPath()+ds.getBestandsNaam()); //path includes de naam.
        taskOutput.append("Bestand: " + ds.getBestandsNaam() + ", totale grootte: " + ds.getBestandsGrootte() + " bytes\nTorrent Aanmaken..");
        tp.generatePieceHashes();
        System.out.println(ds.getTorrentnaam());
        IOManager.save(tp.generateTorrent(), ds.getTorrentnaam());
        taskOutput.append("OK\n");
    }

    public boolean uploadTorrent() {
        taskOutput.append("Torrent uploaden naar server..");
        System.out.println(ds.getTrackerURL() + "/upload");
        System.out.println(ds.getTorrentnaam());
        boolean succes = ConnectionManager.publish(ds.getTorrentnaam(), ds.getTrackerURL() + "/upload", "", "", ds.getTorrentnaam(), "noInfo", "noComment", "");
        if (succes) {
            taskOutput.append("OK\n");
        } else {
            taskOutput.append("fout bij tracker!");
        }
        return succes;
    }

    public PublishPanel(DataSet ds, WizardController wizardController) {
        super(new BorderLayout());
        this.wizardController = wizardController;
        this.ds = ds;
        tp = new TorrentProcessor();


        //Create the demo's UI.
        startButton = new JButton("Start");
        startButton.setActionCommand("start");
        startButton.addActionListener(this);

        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);

        //Call setStringPainted now so that the progress bar height
        //stays the same whether or not the string is shown.
        progressBar.setStringPainted(true);

        taskOutput = new JTextArea(5, 20);
        taskOutput.setMargin(new Insets(5, 5, 5, 5));
        taskOutput.setEditable(false);

        JPanel panel = new JPanel();
        panel.add(startButton);
        panel.add(progressBar);

        add(panel, BorderLayout.PAGE_START);
        add(new JScrollPane(taskOutput), BorderLayout.CENTER);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    /**
     * Invoked when the user presses the start button.
     */
    public void actionPerformed(ActionEvent evt) {
        System.out.println("actionPerformed");

        progressBar.setIndeterminate(false);
        startButton.setEnabled(false);
        //Instances of javax.swing.SwingWorker are not reusuable, so
        //we create new instances as needed.
        task = new Task();
        task.addPropertyChangeListener(this);
        task.execute();
    }

    /**
     * Invoked when task's progress property changes.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        System.out.println("propertyChange  event = " + evt.getPropertyName() + " val :" + evt.getNewValue().toString());
        if ("progress" == evt.getPropertyName()) {

            int progress = (Integer) evt.getNewValue();
            progressBar.setIndeterminate(false);
            progressBar.setValue(progress);
            taskOutput.append(String.format(
                    "Voortang %d%%\n", progress));
        }
    }

    /**
     * Create the GUI and show it. As with all GUI code, this must run
     * on the event-dispatching thread.
     */
    private void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("ProgressBarDemo2");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        JComponent newContentPane = new PublishPanel(ds, wizardController);
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
}
