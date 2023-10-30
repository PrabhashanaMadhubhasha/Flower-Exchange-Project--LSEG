/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package inf;
import codes.DBconet;
import codes.process;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import net.proteanit.sql.DbUtils;

import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;

/**
 *
 * @author MSI
 */
public final class mainframe extends javax.swing.JFrame {
    Connection conn = null;
    PreparedStatement pat = null;
    ResultSet rs = null;
    /**
     * Creates new form mainframe
     */
    public mainframe() {

        initComponents();
        conn = DBconet.connect();
        tableload();

    }
    public void tableload() {

        try {
            String sq1 = "SELECT id AS ID,orderid AS ClOrderID,inst AS Instrument,side AS Side,quantity AS Quantity,price AS Price FROM students1";
            pat = conn.prepareStatement(sq1);
            rs = pat.executeQuery();
            table1.setModel(DbUtils.resultSetToTableModel(rs));
        
        } catch (Exception e){
            JOptionPane.showMessageDialog(null,e);
        }
    }
    public void tableload1() {

        try {
            String sq1 = "SELECT id2 AS ID_2,clid AS OrderID,orderid2 AS ClOrderID,inst2 AS Instrument,side2 AS Side,price2 AS Price,quantity2 AS Quantity,status AS Status,reason AS Reason,tt AS Transaction_Time FROM exchanged";
            pat = conn.prepareStatement(sq1);
            rs = pat.executeQuery();
            table2.setModel(DbUtils.resultSetToTableModel(rs));
        
        } catch (Exception e){
            JOptionPane.showMessageDialog(null,e);
        }
    }
    
    public void tabledata(){
        int r = table1.getSelectedRow();
        String id = table1.getValueAt(r,0).toString();
        String orderid = table1.getValueAt(r,1).toString();
        String inst = table1.getValueAt(r,2).toString();
        String side = table1.getValueAt(r,3).toString();
        String quantity = table1.getValueAt(r,4).toString();
        String price = table1.getValueAt(r,5).toString();
        idbox.setText(id);
        orderbox.setText(orderid);
        quantitybox.setText(quantity);
        instbox.setSelectedItem(inst);
        sidebox.setSelectedItem(side);
        pricebox.setText(price);
    }
    
    public void search() {
        String srch = searchbox.getText();
        

        try {
            String sq1 = "SELECT id,orderid,inst,side,quantity,price FROM students1 WHERE orderid LIKE '%"+srch+"%' OR id LIKE '%"+srch+"%'";
            pat = conn.prepareStatement(sq1);
            rs = pat.executeQuery();
            table1.setModel(DbUtils.resultSetToTableModel(rs));
        
        } catch (Exception e){
            JOptionPane.showMessageDialog(null,e);
        }
    }
    public void search1() {
        String srch = searchbox.getText();
        

        try {
            String sq1 = "SELECT id2,clid,orderid2,inst2,side2,price2,quantity2,status,reason,tt FROM exchanged WHERE orderid2 LIKE '%"+srch+"%' OR id2 LIKE '%"+srch+"%' OR clid LIKE '%"+srch+"%'";
            pat = conn.prepareStatement(sq1);
            rs = pat.executeQuery();
            table2.setModel(DbUtils.resultSetToTableModel(rs));
        
        } catch (Exception e){
            JOptionPane.showMessageDialog(null,e);
        }
    }
    
    private void update() {   
        String id = idbox.getText();
        String orderid = orderbox.getText();
        String inst = instbox.getSelectedItem().toString();
        String side = sidebox.getSelectedItem().toString();

        String quantity = quantitybox.getText();
        String price = pricebox.getText();
        
        
        
        
        
        
       
//        String id = idbox.getText();
//        String name = orderbox.getText();
//        String age = quantitybox.getText();
//        String grade = instbox.getSelectedItem().toString();
        
        try {
            
            String sq1 = "UPDATE students1 SET orderid='"+orderid+"',inst='"+inst+"',side='"+side+"',quantity='"+quantity+"',price='"+price+"' WHERE id='"+id+"'";
            pat = conn.prepareStatement(sq1);
            pat.execute();
            JOptionPane.showMessageDialog(null,"Updated Data");
        
        } catch (Exception e){
            JOptionPane.showMessageDialog(null,e);
        }
        tableload();
        
    }
    
    public void clear(){
        searchbox.setText("");
        idbox.setText("ID");
        orderbox.setText("");
        quantitybox.setText("");
        instbox.setSelectedItem("");
        sidebox.setSelectedItem("");
        pricebox.setText("");
        
    }
    
    public void export(){

//        Connection connection = null; // Your established database connection

        String tableName = "students1"; // Replace with the name of the table you want to export
        String csvFileName = "C:\\Users\\MSI\\Documents\\NetBeansProjects\\project1\\src\\codes\\orders.csv"; // Name of the CSV file

         try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT orderid,inst,side,quantity,price FROM " + tableName);
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            FileOutputStream fileOutputStream = new FileOutputStream(csvFileName);
            BufferedWriter csvFile = new BufferedWriter(new OutputStreamWriter(fileOutputStream, "UTF-8"));

            // Write column headers to the CSV file
            for (int i = 1; i <= columnCount; i++) {
                csvFile.append(metaData.getColumnName(i));
                if (i < columnCount) {
                    csvFile.append(",");
                } else {
                    csvFile.append("\n");
                }
            }

            // Write data to the CSV file
            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    csvFile.append(resultSet.getString(i));
                    if (i < columnCount) {
                        csvFile.append(",");
                    } else {
                        csvFile.append("\n");
                    }
                }
            }

            System.out.println("Table data exported to " + csvFileName);

            csvFile.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
}
    public void import1(){
   
        String tableName = "exchanged"; // Replace with the name of the table where you want to insert the data
        String csvFileName = "C:\\Users\\MSI\\Documents\\NetBeansProjects\\project1\\src\\codes\\exchanged.csv"; // Name of the CSV file to import

        try {
            BufferedReader reader = new BufferedReader(new FileReader(csvFileName));
            String line;
            boolean firstLine = true; // To skip the first row
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue; // Skip the first row
                }
                String[] data = line.split(","); // Assuming CSV fields are separated by commas
                String insertSQL = "INSERT INTO " + tableName + "(clid, orderid2, inst2,side2,price2,quantity2,status,reason,tt) VALUES ('"+data[0]+"', '"+data[1]+"', '"+data[2]+"', '"+data[3]+"','"+data[4]+"','"+data[5]+"','"+data[6]+"','"+data[7]+"','"+data[8]+"')"; // Adjust the number of placeholders to match your table columns

                pat = conn.prepareStatement(insertSQL);
                pat.execute();
            }

            System.out.println("Data imported from " + csvFileName + " to " + tableName);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        try (BufferedReader br = new BufferedReader(new FileReader(csvFileName))) {
//            String line;
//            br.readLine(); // Read and discard the header line
//
//            while ((line = br.readLine()) != null) {
//                String[] data = line.split(",");
//                String insertSQL = "INSERT INTO " + tableName + "(clid, orderid2, inst2,side2,price2,quantity2,status,reason,tt) VALUES ('"+data[0]+"', '"+data[1]+"', '"+data[2]+"', '"+data[3]+"','"+data[4]+"','"+data[5]+"','"+data[6]+"','"+data[7]+"','"+data[8]+"')"; // Adjust the number of placeholders to match your table columns
//
//                pat = conn.prepareStatement(insertSQL);
//                pat.execute();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
        public void deleteall(String tableName) {

        try {
            Statement statement = conn.createStatement();
//            String tableName= "exchanged";
            String deleteSQL = "DELETE FROM " + tableName;
            statement.executeUpdate(deleteSQL);

            System.out.println("All rows deleted from " + tableName);

        } catch (Exception e) {
            e.printStackTrace();
        }
        clear();
        tableload();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        searchbox = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        orderbox = new javax.swing.JTextField();
        quantitybox = new javax.swing.JTextField();
        instbox = new javax.swing.JComboBox<>();
        sidebox = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        pricebox = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        insertbtn = new javax.swing.JButton();
        updatebtn = new javax.swing.JButton();
        clearbtn = new javax.swing.JButton();
        deletebtn = new javax.swing.JButton();
        clearall = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        idbox = new javax.swing.JLabel();
        build = new javax.swing.JButton();
        exchange = new javax.swing.JButton();
        show = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table1 = new javax.swing.JTable();
        exitbtn = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        table2 = new javax.swing.JTable();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(255, 153, 204));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(102, 102, 255));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel4.setBackground(new java.awt.Color(153, 255, 255));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        searchbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchboxActionPerformed(evt);
            }
        });
        searchbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchboxKeyReleased(evt);
            }
        });
        jPanel4.add(searchbox, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 20, 240, -1));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel4.setText("Search");
        jPanel4.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, -1, -1));

        jPanel2.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 40, 310, 70));

        jPanel3.setBackground(new java.awt.Color(102, 255, 255));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel1.setText("Order ID");
        jPanel3.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, -1, -1));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel2.setText("Quantity");
        jPanel3.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 160, -1, -1));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel3.setText("Instrument");
        jPanel3.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 60, -1, -1));

        orderbox.setToolTipText("Enter your name");
        jPanel3.add(orderbox, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 10, 150, -1));

        quantitybox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quantityboxActionPerformed(evt);
            }
        });
        jPanel3.add(quantitybox, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 160, 150, -1));

        instbox.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        instbox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "Rose", "Lavender", "Tulip", "Orchid", "Lotus", " " }));
        instbox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                instboxItemStateChanged(evt);
            }
        });
        instbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                instboxActionPerformed(evt);
            }
        });
        jPanel3.add(instbox, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 60, 150, -1));

        sidebox.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        sidebox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select", "1", "2" }));
        sidebox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sideboxActionPerformed(evt);
            }
        });
        jPanel3.add(sidebox, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 110, 150, -1));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel6.setText("Side");
        jPanel3.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 110, -1, -1));

        pricebox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                priceboxActionPerformed(evt);
            }
        });
        jPanel3.add(pricebox, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 210, 150, -1));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel7.setText("Expected Price");
        jPanel3.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 210, -1, -1));

        jPanel2.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 130, 310, 240));

        jPanel5.setBackground(new java.awt.Color(204, 153, 255));
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        insertbtn.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        insertbtn.setText("Insert");
        insertbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                insertbtnActionPerformed(evt);
            }
        });
        jPanel5.add(insertbtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, -1, -1));

        updatebtn.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        updatebtn.setText("Update");
        updatebtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updatebtnActionPerformed(evt);
            }
        });
        jPanel5.add(updatebtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 10, -1, -1));

        clearbtn.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        clearbtn.setText("Clear");
        clearbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearbtnActionPerformed(evt);
            }
        });
        jPanel5.add(clearbtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 40, -1, -1));

        deletebtn.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        deletebtn.setText("Delete");
        deletebtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deletebtnActionPerformed(evt);
            }
        });
        jPanel5.add(deletebtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 40, -1, -1));

        clearall.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        clearall.setText("Clear All");
        clearall.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearallActionPerformed(evt);
            }
        });
        jPanel5.add(clearall, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 40, -1, -1));

        jPanel2.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 380, 310, 70));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel5.setText("ID");
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 110, 20, -1));

        idbox.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        idbox.setText("ID");
        jPanel2.add(idbox, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 110, 20, -1));

        build.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        build.setText("Build Orders");
        build.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buildActionPerformed(evt);
            }
        });
        jPanel2.add(build, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 470, -1, -1));

        exchange.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        exchange.setText("Exchange");
        exchange.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exchangeActionPerformed(evt);
            }
        });
        jPanel2.add(exchange, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 470, -1, -1));

        show.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        show.setText("Show");
        show.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showActionPerformed(evt);
            }
        });
        jPanel2.add(show, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 470, -1, -1));

        jLabel10.setBackground(new java.awt.Color(255, 51, 51));
        jLabel10.setFont(new java.awt.Font("Britannic Bold", 0, 30)); // NOI18N
        jLabel10.setText("Flower Exchanged Project");
        jPanel2.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 0, 410, -1));

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 390, 530));

        table1.setBackground(new java.awt.Color(255, 204, 204));
        table1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4", "Title 5", "Title 6"
            }
        ));
        table1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                table1MouseClicked(evt);
            }
        });
        table1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                table1KeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(table1);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 20, 680, 230));

        exitbtn.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        exitbtn.setText("Exit");
        exitbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitbtnActionPerformed(evt);
            }
        });
        jPanel1.add(exitbtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(990, 500, -1, -1));

        table2.setBackground(new java.awt.Color(255, 255, 204));
        table2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4", "Title 5", "Title 6", "Title 7", "Title 8", "Title 9", "Title 10"
            }
        ));
        jScrollPane2.setViewportView(table2);

        jPanel1.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 270, 680, 230));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel8.setText("Order Table");
        jPanel1.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 0, -1, -1));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel9.setText("Exchanged Table");
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 250, -1, -1));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1070, 530));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void searchboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_searchboxActionPerformed

    private void quantityboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quantityboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_quantityboxActionPerformed

    private void insertbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_insertbtnActionPerformed
        String orderid;
        String inst;
        int side;
        int quantity;
        int price;

        
        orderid = orderbox.getText();
        quantity = Integer.parseInt(quantitybox.getText());
        price = Integer.parseInt(pricebox.getText());
        inst = instbox.getSelectedItem().toString();
        side =Integer.parseInt(sidebox.getSelectedItem().toString());

        
        try {
            String sq1 = "INSERT INTO students1(orderid,inst,side,quantity,price)VALUES ('"+orderid+"','"+inst+"','"+side+"','"+quantity+"','"+price+"')";
            pat = conn.prepareStatement(sq1);
            pat.execute();
            JOptionPane.showMessageDialog(null,"Inserted Data");
        
        } catch (Exception e){
            JOptionPane.showMessageDialog(null,e);
        }
        tableload();

                
        
    }//GEN-LAST:event_insertbtnActionPerformed

    private void exitbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitbtnActionPerformed
        int check = JOptionPane.showConfirmDialog(null, "Do you want to Exit ?");
        if (check==0){
            System.exit(0);
        }
        
    }//GEN-LAST:event_exitbtnActionPerformed

    private void table1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_table1MouseClicked
        tabledata();
    }//GEN-LAST:event_table1MouseClicked

    private void table1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_table1KeyReleased
        tabledata();
    }//GEN-LAST:event_table1KeyReleased

    private void searchboxKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchboxKeyReleased
        search();
        search1();
    }//GEN-LAST:event_searchboxKeyReleased

    private void updatebtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updatebtnActionPerformed
        update();
        tabledata();
    }//GEN-LAST:event_updatebtnActionPerformed

    private void deletebtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deletebtnActionPerformed
        int check = JOptionPane.showConfirmDialog(null, "Do you want to Delete ?");
        if (check==0){
            String id = idbox.getText();
            try {
                String sq1 = "DELETE FROM students1 WHERE id='"+id+"'";
                pat = conn.prepareStatement(sq1);
                pat.execute();
                JOptionPane.showMessageDialog(null,"Deleted Data");
        
            } catch (Exception e){
                JOptionPane.showMessageDialog(null,e);
            }
        }
        clear();
        tableload();
    }//GEN-LAST:event_deletebtnActionPerformed

    private void clearbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearbtnActionPerformed
        clear();
    }//GEN-LAST:event_clearbtnActionPerformed

    private void instboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_instboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_instboxActionPerformed

    private void instboxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_instboxItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_instboxItemStateChanged

    private void sideboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sideboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_sideboxActionPerformed

    private void priceboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_priceboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_priceboxActionPerformed

    private void buildActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buildActionPerformed
        export();
    }//GEN-LAST:event_buildActionPerformed

    private void exchangeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exchangeActionPerformed
        deleteall("exchanged");
        process.exchanger();
        import1();
    }//GEN-LAST:event_exchangeActionPerformed

    private void showActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showActionPerformed
        
        tableload1();
    }//GEN-LAST:event_showActionPerformed

    private void clearallActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearallActionPerformed
        int check = JOptionPane.showConfirmDialog(null, "Do you want to Clear All data ?");
        if (check==0){
            String id = idbox.getText();
            try {
                deleteall("students1");
                tableload();
        
            } catch (Exception e){
                JOptionPane.showMessageDialog(null,e);
            }
        }
        
        
    }//GEN-LAST:event_clearallActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(mainframe.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(mainframe.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(mainframe.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(mainframe.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new mainframe().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton build;
    private javax.swing.JButton clearall;
    private javax.swing.JButton clearbtn;
    private javax.swing.JButton deletebtn;
    private javax.swing.JButton exchange;
    private javax.swing.JButton exitbtn;
    private javax.swing.JLabel idbox;
    private javax.swing.JButton insertbtn;
    private javax.swing.JComboBox<String> instbox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField orderbox;
    private javax.swing.JTextField pricebox;
    private javax.swing.JTextField quantitybox;
    private javax.swing.JTextField searchbox;
    private javax.swing.JButton show;
    private javax.swing.JComboBox<String> sidebox;
    private javax.swing.JTable table1;
    private javax.swing.JTable table2;
    private javax.swing.JButton updatebtn;
    // End of variables declaration//GEN-END:variables
}
