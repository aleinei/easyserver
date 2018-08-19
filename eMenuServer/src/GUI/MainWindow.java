/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

/**
 *
 * @author Server
 */

import emenuserver.*;
import emenuserver.Database.eMenuSQL;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
public class MainWindow extends javax.swing.JFrame {

    /**
     * Creates new form MainWindow
     */
    boolean isConnected = false;
    Connect con;
    public ArrayList<eMenuServerThread> clients;
    public MainWindow() {
        initComponents();
        statusLabel.setForeground(Color.RED);
        portNumber.setText(String.valueOf(2550));
        connectBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
            if(!isConnected) {
            int number = 2550;
            if(number > 0) {
                connectBtn.setEnabled(false);
                disconnectedBtn.setEnabled(true);
                statusLabel.setText("متصل");
                statusLabel.setForeground(Color.GREEN);
                con = new Connect();
                con.portNumber = number;
                con.start();
                isConnected = true;
            }
        }
            }
        });
        connectBtn.doClick();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        topPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        portNumber = new javax.swing.JTextField();
        connectBtn = new javax.swing.JButton();
        statusLabel = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        disconnectedBtn = new javax.swing.JButton();
        bottomPanel = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        scrollPanel = new javax.swing.JScrollPane();
        consoleLogText = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Easy Server");
        setName("Easy Server"); // NOI18N

        jLabel1.setText("رقم الاتصال");

        connectBtn.setText("اتصال");
        connectBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                onConnectButtonClick(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                onConnectButtonClick(evt);
            }
        });
        connectBtn.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                onC(evt);
            }
        });

        statusLabel.setBackground(new java.awt.Color(255, 82, 82));
        statusLabel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        statusLabel.setForeground(new java.awt.Color(255, 82, 82));
        statusLabel.setText("غير متصل");

        jLabel3.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel3.setText("الحالة : ");

        disconnectedBtn.setText("قطع الاتصال");
        disconnectedBtn.setEnabled(false);
        disconnectedBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                disconnectButtonClick(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                disconnectButtonClick(evt);
            }
        });

        javax.swing.GroupLayout topPanelLayout = new javax.swing.GroupLayout(topPanel);
        topPanel.setLayout(topPanelLayout);
        topPanelLayout.setHorizontalGroup(
            topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(topPanelLayout.createSequentialGroup()
                .addGroup(topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, topPanelLayout.createSequentialGroup()
                        .addGap(62, 62, 62)
                        .addComponent(statusLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 432, Short.MAX_VALUE)
                        .addComponent(portNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel1))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, topPanelLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(disconnectedBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(connectBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        topPanelLayout.setVerticalGroup(
            topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(topPanelLayout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(portNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(statusLabel)
                    .addComponent(jLabel3))
                .addGap(18, 18, 18)
                .addGroup(topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(connectBtn)
                    .addComponent(disconnectedBtn))
                .addContainerGap(85, Short.MAX_VALUE))
        );

        jButton1.setText("مسح ");
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                clearLogText(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                clearLogText(evt);
            }
        });

        scrollPanel.setBorder(null);
        scrollPanel.setVerifyInputWhenFocusTarget(false);

        consoleLogText.setEditable(false);
        consoleLogText.setColumns(20);
        consoleLogText.setRows(5);
        scrollPanel.setViewportView(consoleLogText);

        javax.swing.GroupLayout bottomPanelLayout = new javax.swing.GroupLayout(bottomPanel);
        bottomPanel.setLayout(bottomPanelLayout);
        bottomPanelLayout.setHorizontalGroup(
            bottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bottomPanelLayout.createSequentialGroup()
                .addGroup(bottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(bottomPanelLayout.createSequentialGroup()
                        .addGap(0, 637, Short.MAX_VALUE)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(scrollPanel))
                .addContainerGap())
        );
        bottomPanelLayout.setVerticalGroup(
            bottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, bottomPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(bottomPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(topPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(topPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(bottomPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void onConnectButtonClick(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_onConnectButtonClick

        if(!isConnected) {
            int number = Integer.parseInt(this.portNumber.getText());
            if(number > 0) {
                connectBtn.setEnabled(false);
                disconnectedBtn.setEnabled(true);
                statusLabel.setText("متصل");
                statusLabel.setForeground(Color.GREEN);
                con = new Connect();
                con.portNumber = number;
                con.start();
                isConnected = true;
            }
        }
  
    }//GEN-LAST:event_onConnectButtonClick

    private void disconnectButtonClick(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_disconnectButtonClick
       if(isConnected) {
           logMessage("Server Disconnecting...");
           isConnected = false;
            connectBtn.setEnabled(true);
            statusLabel.setText("غير متصل");
            statusLabel.setForeground(Color.RED);
            disconnectedBtn.setEnabled(false);

            if(con != null) {
                try {
                    con.close();
                    con.interrupt();
                } catch (IOException ex) {
                    logMessage(ex.toString());
                }
            }
           logMessage("Server Disconnected");
           isConnected = false;
       }
    }//GEN-LAST:event_disconnectButtonClick

    private void clearLogText(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clearLogText
        consoleLogText.setText("");
    }//GEN-LAST:event_clearLogText

    private void onC(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_onC
        System.out.println("clicked");
    }//GEN-LAST:event_onC

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) throws ClassNotFoundException {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                MainWindow main =  new MainWindow();
                main.setVisible(true);
            }
        });
    }
    
    public class Connect extends Thread{

        public int portNumber;
        boolean listening = true;
        boolean canConnect = true;
        ServerSocket serverSocket;

        
        public Connect() {
            clients = new ArrayList<>();
        }
        @Override
        public void run() {
        if(portNumber != 0) {
            
            try
            {
                serverSocket = new ServerSocket(portNumber);
                //eMenuSQL SQL = new eMenuSQL();
                /*JSONArray test = SQL.getSections();
                //JSONArray test2 = SQL.getCateogiresWithItems();
                JSONArray test2 = SQL.getCategories(4);
                JSONArray test3 = SQL.getCategoryItems(4, 19);
                System.out.println(test.toString());
                System.out.println(test2.toString());
                System.out.println(test3.toString());*/
                //JSONArray usernames = SQL.GetUsernames();
                //System.out.println(usernames.toString());
                logMessage("Connected and ready");
                while(listening) {
                    if(canConnect) {
                        eMenuServerThread client = new eMenuServerThread(serverSocket.accept(), MainWindow.this);
                        client.start();
                        clients.add(client);
                    } else 
                        break;
                }
                serverSocket.close();
            } catch (IOException ex) {
                connectBtn.setEnabled(true);
                statusLabel.setText("غير متصل");
                statusLabel.setForeground(Color.RED);
                disconnectedBtn.setEnabled(false);
                isConnected = false;
            } catch (ClassNotFoundException ex) {
                 Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
          }
        }
        
        public void close() throws IOException {
            listening = false;
            canConnect = false;
            if(serverSocket != null) {
                serverSocket.close();
            }
            clients.stream().filter((c) -> (c != null)).forEachOrdered((c) -> {
                c.Close();
            });
            clients.clear();
        }
    }
    
    public void logMessage(String message) {
        consoleLogText.append(message + "\n");
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bottomPanel;
    private javax.swing.JButton connectBtn;
    private javax.swing.JTextArea consoleLogText;
    private javax.swing.JButton disconnectedBtn;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JTextField portNumber;
    private javax.swing.JScrollPane scrollPanel;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JPanel topPanel;
    // End of variables declaration//GEN-END:variables
}