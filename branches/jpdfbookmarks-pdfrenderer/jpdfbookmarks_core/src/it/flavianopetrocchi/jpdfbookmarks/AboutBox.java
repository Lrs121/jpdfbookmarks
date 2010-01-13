/*
 * AboutBox.java
 *
 * Copyright (c) 2010 Flaviano Petrocchi <flavianopetrocchi at gmail.com>.
 * All rights reserved.
 *
 * This file is part of JPdfBookmarks.
 *
 * JPdfBookmarks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JPdfBookmarks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JPdfBookmarks.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.flavianopetrocchi.jpdfbookmarks;

import it.flavianopetrocchi.linklabel.LinkLabel;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/**
 *
 * @author  fla
 */
public class AboutBox extends javax.swing.JDialog {

    private URI homePage;
    private Color focusedColor = new Color(128, 0, 128);
    private Color nonFocusedColor = new Color(0, 0, 255);

    /** Creates new form AboutBox5 */
    public AboutBox(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        try {
            homePage = new URI("http://flavianopetrocchi.blogspot.com");
        } catch (URISyntaxException ex) {
        }
        initComponents();
        Cursor handCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
        txtMail.setCursor(handCursor);
        txtHomepage.setCursor(handCursor);
        txtBlog.setCursor(handCursor);
        btnClose.requestFocusInWindow();
        txtLibraries.addHyperlinkListener(new MyHyperlinkListener());
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        progIconLabel = new javax.swing.JLabel();
        rightPanel = new javax.swing.JPanel();
        javax.swing.JLabel appTitleLabel = new javax.swing.JLabel();
        javax.swing.JLabel appDescLabel = new javax.swing.JLabel();
        javax.swing.JLabel versionLabel = new javax.swing.JLabel();
        javax.swing.JLabel vendorLabel = new javax.swing.JLabel();
        javax.swing.JLabel homepageLabel = new javax.swing.JLabel();
        javax.swing.JLabel appVersionLabel = new javax.swing.JLabel();
        javax.swing.JLabel appVendorLabel = new javax.swing.JLabel();
        javax.swing.JLabel homepageLabel1 = new javax.swing.JLabel();
        btnClose = new javax.swing.JButton();
        txtHomepage = new LinkLabel(homePage);
        txtMail = new LinkLabel(homePage);
        txtBlog = new LinkLabel(homePage);
        javax.swing.JLabel homepageLabel2 = new javax.swing.JLabel();
        javax.swing.JLabel appDescLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtLibraries = new javax.swing.JTextPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("JPdfBookmarks");
        setModal(true);

        progIconLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/it/flavianopetrocchi/jpdfbookmarks/gfx/jpdfbookmarks.png"))); // NOI18N

        rightPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtHomepageMouseClicked(evt);
            }
        });

        appTitleLabel.setFont(appTitleLabel.getFont().deriveFont(appTitleLabel.getFont().getStyle() | java.awt.Font.BOLD, appTitleLabel.getFont().getSize()+4));
        appTitleLabel.setText(JPdfBookmarks.APP_NAME);

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("it/flavianopetrocchi/jpdfbookmarks/locales/localizedText"); // NOI18N
        appDescLabel.setText(bundle.getString("APP_DESCRIPTION")); // NOI18N

        versionLabel.setFont(versionLabel.getFont().deriveFont(versionLabel.getFont().getStyle() | java.awt.Font.BOLD));
        versionLabel.setText(bundle.getString("VERSION_LABEL")); // NOI18N

        vendorLabel.setFont(vendorLabel.getFont().deriveFont(vendorLabel.getFont().getStyle() | java.awt.Font.BOLD));
        vendorLabel.setText(bundle.getString("AUTHOR_LABEL")); // NOI18N

        homepageLabel.setFont(homepageLabel.getFont().deriveFont(homepageLabel.getFont().getStyle() | java.awt.Font.BOLD));
        homepageLabel.setText(bundle.getString("EMAIL_LABEL")); // NOI18N

        appVersionLabel.setText(JPdfBookmarks.VERSION);

        appVendorLabel.setText("Flaviano Petrocchi");

        homepageLabel1.setFont(homepageLabel1.getFont().deriveFont(homepageLabel1.getFont().getStyle() | java.awt.Font.BOLD));
        homepageLabel1.setText(bundle.getString("HOME_PAGE_LABEL")); // NOI18N

        btnClose.setMnemonic(java.util.ResourceBundle.getBundle("it/flavianopetrocchi/jpdfbookmarks/locales/localizedText").getString("ACTION_CLOSE_MNEMONIC").charAt(0));
        btnClose.setText(bundle.getString("ACTION_CLOSE")); // NOI18N
        btnClose.setToolTipText("Close this dialog");
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });

        txtHomepage.setEditable(false);
        txtHomepage.setFont(txtHomepage.getFont());
        txtHomepage.setForeground(new java.awt.Color(0, 0, 255));
        txtHomepage.setText(" http://sourceforge.net/projects/jpdfbookmarks/");
        txtHomepage.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(0, 0, 255)));
        txtHomepage.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtHomepageMouseClicked(evt);
            }
        });
        txtHomepage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtHomepageActionPerformed(evt);
            }
        });
        txtHomepage.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtHomepageFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtHomepageFocusLost(evt);
            }
        });

        txtMail.setEditable(false);
        txtMail.setForeground(new java.awt.Color(0, 0, 255));
        txtMail.setText(" flavianopetrocchi@gmail.com ");
        txtMail.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(0, 0, 255)));
        txtMail.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtMailMouseClicked(evt);
            }
        });
        txtMail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMailActionPerformed(evt);
            }
        });
        txtMail.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtMailFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtMailFocusLost(evt);
            }
        });

        txtBlog.setEditable(false);
        txtBlog.setFont(txtBlog.getFont());
        txtBlog.setForeground(new java.awt.Color(0, 0, 255));
        txtBlog.setText(" http://flavianopetrocchi.blogspot.com ");
        txtBlog.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(0, 0, 255)));
        txtBlog.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtBlogMouseClicked(evt);
            }
        });
        txtBlog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBlogActionPerformed(evt);
            }
        });
        txtBlog.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtBlogFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtBlogFocusLost(evt);
            }
        });

        homepageLabel2.setFont(homepageLabel2.getFont().deriveFont(homepageLabel2.getFont().getStyle() | java.awt.Font.BOLD));
        homepageLabel2.setText("Blog:");

        appDescLabel1.setText(bundle.getString("ABOUT_LIBRARIES")); // NOI18N

        txtLibraries.setContentType("text/html");
        txtLibraries.setEditable(false);
        txtLibraries.setText("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">\n<html>\n<head>\n</head>\n<body>\n<span style=\"font-weight: bold;\">Apache Commons CLI</span><br>\nCopyright 2001-2009 The Apache Software Foundation <br>\n<a href=\"http://commons.apache.org/cli/\">http://commons.apache.org/cli/</a><br>\nApache License Version 2.0, January 2004<br>\n<a href=\"http://www.apache.org/licenses/\">http://www.apache.org/licenses/</a><br>\n<br>\n<span style=\"font-weight: bold;\">iText-2.1.7</span><br>\nCopyright 1999, 2000, 2001, 2002 by Bruno Lowagie.<br>\n<a href=\"http://www.lowagie.com/iText/\">http://www.lowagie.com/iText/</a><br>\nGNU LIBRARY GENERAL PUBLIC LICENSE Version 2 (or later version)<br>\n<a href=\"http://www.gnu.org/licenses\">http://www.gnu.org/licenses</a>/<br>\n<br>\n<b>Bouncy Castle Crypto APIs<br>\n</b>Copyright (c) 2000 - 2009 The Legion Of The Bouncy Castle<br>\n<a href=\"http://www.bouncycastle.org\">http://www.bouncycastle.org</a>\n<br>\nAdaptation of the <a\n href=\"http://opensource.org/licenses/mit-license.php\">MIT\nX11 License</a><br>\n<a href=\"http://www.bouncycastle.org/licence.html\">http://www.bouncycastle.org/licence.html</a><br>\n<br>\n<span style=\"font-weight: bold;\">PDF Renderer</span><br>\nCopyright 2004 Sun Microsystems, Inc. <br>\n4150 Network Circle, Santa Clara, California 95054, U.S.A. All rights\nreserved.<br>\n<a href=\"https://pdf-renderer.dev.java.net/\">https://pdf-renderer.dev.java.net/</a><br>\nGNU LESSER GENERAL PUBLIC LICENSE Version 2.1 (or later version)<br>\n<a href=\"http://www.gnu.org/licenses/\">http://www.gnu.org/licenses/</a>\n</body>\n</html>\n");
        jScrollPane1.setViewportView(txtLibraries);

        org.jdesktop.layout.GroupLayout rightPanelLayout = new org.jdesktop.layout.GroupLayout(rightPanel);
        rightPanel.setLayout(rightPanelLayout);
        rightPanelLayout.setHorizontalGroup(
            rightPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, rightPanelLayout.createSequentialGroup()
                .add(rightPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(rightPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(rightPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 486, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, appDescLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 486, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, appTitleLabel)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, rightPanelLayout.createSequentialGroup()
                                .add(rightPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(versionLabel)
                                    .add(vendorLabel)
                                    .add(homepageLabel)
                                    .add(homepageLabel1)
                                    .add(homepageLabel2))
                                .add(16, 16, 16)
                                .add(rightPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(txtBlog, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(txtMail, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(txtHomepage, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(appVendorLabel)
                                    .add(appVersionLabel))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 191, Short.MAX_VALUE))
                            .add(org.jdesktop.layout.GroupLayout.LEADING, appDescLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 486, Short.MAX_VALUE)))
                    .add(btnClose, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 89, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        rightPanelLayout.setVerticalGroup(
            rightPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(rightPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(appTitleLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(appDescLabel)
                .add(18, 18, 18)
                .add(rightPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(versionLabel)
                    .add(appVersionLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(rightPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(vendorLabel)
                    .add(appVendorLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(rightPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(homepageLabel)
                    .add(txtMail, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(rightPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(homepageLabel1)
                    .add(txtHomepage, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(rightPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(homepageLabel2)
                    .add(txtBlog, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(appDescLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(btnClose)
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(18, 18, 18)
                .add(progIconLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 140, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(rightPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(progIconLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 179, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(rightPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        // TODO add your handling code here:
        setVisible(false);
    }//GEN-LAST:event_btnCloseActionPerformed

	private void txtMailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMailActionPerformed
            mail(txtMail.getText().trim());
	}//GEN-LAST:event_txtMailActionPerformed

	private void txtHomepageMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtHomepageMouseClicked
            goToWebLink(txtHomepage.getText().trim());
	}//GEN-LAST:event_txtHomepageMouseClicked

	private void txtMailMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtMailMouseClicked
            mail(txtMail.getText().trim());
	}//GEN-LAST:event_txtMailMouseClicked

	private void txtMailFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMailFocusGained
            setFocusedColor((JComponent) evt.getSource());
	}//GEN-LAST:event_txtMailFocusGained

	private void txtMailFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtMailFocusLost
            setNonFocusedColor((JComponent) evt.getSource());
	}//GEN-LAST:event_txtMailFocusLost

	private void txtHomepageFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtHomepageFocusGained
            setFocusedColor((JComponent) evt.getSource());
	}//GEN-LAST:event_txtHomepageFocusGained

	private void txtHomepageFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtHomepageFocusLost
            setNonFocusedColor((JComponent) evt.getSource());
	}//GEN-LAST:event_txtHomepageFocusLost

        private void txtHomepageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtHomepageActionPerformed
            goToWebLink(((JTextField) evt.getSource()).getText().trim());
        }//GEN-LAST:event_txtHomepageActionPerformed

        private void txtBlogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBlogActionPerformed
            goToWebLink(((JTextField) evt.getSource()).getText().trim());
        }//GEN-LAST:event_txtBlogActionPerformed

        private void txtBlogMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtBlogMouseClicked
            goToWebLink(((JTextField) evt.getSource()).getText().trim());
        }//GEN-LAST:event_txtBlogMouseClicked

        private void txtBlogFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBlogFocusGained
            setFocusedColor((JComponent) evt.getSource());
        }//GEN-LAST:event_txtBlogFocusGained

        private void txtBlogFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBlogFocusLost
            setNonFocusedColor((JComponent) evt.getSource());
        }//GEN-LAST:event_txtBlogFocusLost

    private void setFocusedColor(JComponent component) {
        //component.setForeground(focusedColor);
        component.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1,
                nonFocusedColor));
    }

    private void setNonFocusedColor(JComponent component) {
        //component.setForeground(nonFocusedColor);
        component.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0,
                nonFocusedColor));
    }

    private void goToWebLink(String uri) {
        int answer = JOptionPane.showConfirmDialog(this,
                Res.getString("MSG_LAUNCH_BROWSER"), JPdfBookmarks.APP_NAME,
                JOptionPane.OK_CANCEL_OPTION);

        if (answer != JOptionPane.OK_OPTION) {
            return;
        }

        Desktop desktop = Desktop.getDesktop();
        try {
            desktop.browse(new URI(uri));
        } catch (URISyntaxException ex) {
            JOptionPane.showMessageDialog(this,
                    Res.getString("ERROR_WRONG_URI"), JPdfBookmarks.APP_NAME,
                    JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    Res.getString("ERROR_LAUNCHING_BROWSER"), JPdfBookmarks.APP_NAME,
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mail(String mail) {
        int answer = JOptionPane.showConfirmDialog(this,
                Res.getString("MSG_LAUNCH_MAIL_CLIENT"), JPdfBookmarks.APP_NAME,
                JOptionPane.OK_CANCEL_OPTION);

        if (answer != JOptionPane.OK_OPTION) {
            return;
        }

        Desktop desktop = Desktop.getDesktop();
        try {
            desktop.mail(new URI("mailto:" + mail));
        } catch (URISyntaxException ex) {
            JOptionPane.showMessageDialog(this,
                    Res.getString("ERROR_WRONG_URI"), JPdfBookmarks.APP_NAME,
                    JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    Res.getString("ERROR_LAUNCHING_BROWSER"), JPdfBookmarks.APP_NAME,
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    class MyHyperlinkListener implements HyperlinkListener {

        public void hyperlinkUpdate(HyperlinkEvent e) {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                goToWebLink(e.getURL().toString());
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                AboutBox dialog = new AboutBox(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {

                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel progIconLabel;
    private javax.swing.JPanel rightPanel;
    private javax.swing.JTextField txtBlog;
    private javax.swing.JTextField txtHomepage;
    private javax.swing.JTextPane txtLibraries;
    private javax.swing.JTextField txtMail;
    // End of variables declaration//GEN-END:variables
}