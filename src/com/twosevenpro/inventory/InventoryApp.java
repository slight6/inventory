package com.twosevenpro.inventory;

import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.MouseInputAdapter;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FocusTraversalPolicy;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class InventoryApp {
    ArrayList<InventoryItem> itemlist;
    InventoryItem item;
    JFrame jf;
    JTable jt;
    JPanel jp_entry;
    JPanel jp_button;
    JPanel jp_info;
    JLabel jl_itemID, jl_itemnum, jl_itemqty, jl_itemdept, jl_itemdesc, jl_itemcost;
    JTextField jtf_itemid;
    JTextField jtf_itemnum;
    JTextField jtf_itemqty;
    JTextField jtf_itemdept;
    JTextArea jta_itemdesc;
    JTextField jtf_itemcost;
    JButton jb_add, jb_delete, jb_update, jb_search, jb_clear, jb_exit, jb_dropTable, jb_backup;
    String header[] = new String[] {
            "ID",
            "Item Number",
            "Item Quantity",
            "Item Department",
            "Item Description",
            "Item Price",
    };
    static Connection conn;
    ResultSet rs;
    int row;
    int col;

    DefaultTableModel dtm = new DefaultTableModel(0, 0) {
        private static final long serialVersionUID = 1L;

		@Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    public static void main(String[] args) throws Exception {
    	Class.forName("org.sqlite.JDBC");
        String url = "jdbc:sqlite:inventory.db";
        conn = DriverManager.getConnection(url);
        InventoryApp app = new InventoryApp();
        app.InventoryApp();
        app.checkTables();
        app.loadData();
    }

    private class CustomFocusTraversalPolicy extends FocusTraversalPolicy {
        private final Component[] components;

        public CustomFocusTraversalPolicy(Component... components) {
            this.components = components;
        }

        @Override
        public Component getComponentAfter(Container aContainer, Component aComponent) {
            for (int i = 0; i < components.length; i++) {
                if (components[i] == aComponent) {
                    return components[(i + 1) % components.length];
                }
            }
            return components[0];
        }

        @Override
        public Component getComponentBefore(Container aContainer, Component aComponent) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'getComponentBefore'");
        }

        @Override
        public Component getFirstComponent(Container aContainer) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'getFirstComponent'");
        }

        @Override
        public Component getLastComponent(Container aContainer) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'getLastComponent'");
        }

        @Override
        public Component getDefaultComponent(Container aContainer) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'getDefaultComponent'");
        }
    }

    private void InventoryApp() {
        // instantiate labels, textfields, textarea, and entry panel
    	jl_itemID = new JLabel("Item ID");
        jl_itemnum = new JLabel("Item Number");
        jl_itemqty = new JLabel("Item Quantity");
        jl_itemdept = new JLabel("Item Department");
        jl_itemdesc = new JLabel("Item Description");
        jl_itemcost = new JLabel("Item Cost");
        jl_itemID.setSize(100, 50);
        jl_itemnum.setSize(100, 50);
        jl_itemqty.setSize(100, 50);
        jl_itemdept.setSize(100, 50);
        jl_itemdesc.setSize(100, 50);
        jl_itemcost.setSize(100, 50);

        jtf_itemid = new JTextField(15);
        jtf_itemid.setEditable(false);
        jtf_itemnum = new JTextField(15);
        jtf_itemqty = new JTextField(15);
        jtf_itemdept = new JTextField(15);
        jta_itemdesc = new JTextArea(3, 15);
        jtf_itemcost = new JTextField(15);

        // Handle TAB and SHIFT+TAB a little better for the JTextArea
        jta_itemdesc.setFocusTraversalPolicy(new CustomFocusTraversalPolicy(jta_itemdesc));

        jta_itemdesc.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, null);
        jta_itemdesc.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, null);
        jta_itemdesc.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_TAB) {
                if (e.isShiftDown()) {
                    jta_itemdesc.transferFocusBackward();
                } else {
                    jta_itemdesc.transferFocus();
                }
                e.consume();
            }
            }
        });

        // Override behavior of the tab key to allow transfer of focus to the next component

        InputMap inputMap = jta_itemdesc.getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap actionMap = jta_itemdesc.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke("TAB"), "focusNextComponent");
        actionMap.put("focusNextComponent", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
               jta_itemdesc.transferFocus();
            }
        });

        jp_entry = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        jp_entry.add(jl_itemID, gbc);
        gbc.gridx = 1;
        jp_entry.add(jtf_itemid, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        jp_entry.add(jl_itemnum, gbc);
        gbc.gridx = 1;
        jp_entry.add(jtf_itemnum, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        jp_entry.add(jl_itemqty, gbc);
        gbc.gridx = 1;
        jp_entry.add(jtf_itemqty, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        jp_entry.add(jl_itemdept, gbc);
        gbc.gridx = 1;
        jp_entry.add(jtf_itemdept, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        jp_entry.add(jl_itemdesc, gbc);
        gbc.gridx = 1;
        jp_entry.add(new JScrollPane(jta_itemdesc), gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        jp_entry.add(jl_itemcost, gbc);
        gbc.gridx = 1;
        jp_entry.add(jtf_itemcost, gbc);

        // instantiate buttons
        jb_backup = new JButton("Backup");
        jb_add = new JButton("Add Item");
        jb_delete = new JButton("Delete Item");
        jb_update = new JButton("Update Item");
        jb_search = new JButton("Search Item");
        jb_clear = new JButton("Clear");
        jb_exit = new JButton("Exit");
        jb_dropTable = new JButton("Drop Table");
              
        jp_button = new JPanel(new FlowLayout());
        jp_button.add(jb_backup);
        jp_button.add(jb_add);
        jp_button.add(jb_delete);
        jp_button.add(jb_update);
        jp_button.add(jb_search);
        jp_button.add(jb_clear);
        jp_button.add(jb_exit);
        jp_button.add(jb_dropTable);

        jp_info = new JPanel(new BorderLayout());
        jt = new JTable(dtm);
        jp_info.add(new JScrollPane(jt), BorderLayout.CENTER);

        jf = new JFrame("Inventory Management System");
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        jf.setSize(screenSize.width, screenSize.height);
        jf.setExtendedState(JFrame.MAXIMIZED_BOTH);
        jf.setLayout(new BorderLayout());
        jf.add(jp_entry, BorderLayout.NORTH);
        jf.add(jp_button, BorderLayout.SOUTH);
        jf.add(jp_info, BorderLayout.CENTER);
        jf.setVisible(true);
        dtm.setColumnIdentifiers(header);

        // Add listeners

        jt.addMouseListener(mouseListener);

        jb_backup.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(jf, "Backup database?", "Selection Pane",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (result == JOptionPane.YES_OPTION) {
                // Assuming conn is your SQLite Connection object
                String backupFileName = "backup_file_name.db";
                String backupCommand = ".backup main " + backupFileName;
                try (Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate(backupCommand);
                    System.out.println("Database backed up to " + backupFileName);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        jb_add.addActionListener(addInventoryItemListener);
        jb_delete.addActionListener(delInventoryItemListener);
        jb_update.addActionListener(updateInventoryItemListener);
        jb_search.addActionListener(searchInventoryItemListener);
        jb_clear.addActionListener(e -> {
            jtf_itemid.setText("");
            jtf_itemnum.setText("");
            jtf_itemqty.setText("");
            jtf_itemdept.setText("");
            jta_itemdesc.setText("");
            jtf_itemcost.setText("");
        });
        jb_exit.addActionListener(e -> System.exit(0));

        jb_dropTable.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(jf, "Drop table?", "Selection Pane",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (result == JOptionPane.YES_OPTION) {
                try {
                    Statement stmt = conn.createStatement();
                    stmt.execute("DROP TABLE tbl_items");
                } catch (Exception err) {
                    System.out.println(err);
                }
                // Place here

                // *********************
                // *********************
                // *********************
                checkTables();
                
                itemlist = new ArrayList<>();
            try {

                Statement stmt = conn.createStatement();
                rs = stmt.executeQuery(
                		"SELECT * FROM tbl_items");
                itemlist.clear();
                while (rs.next()) {
                    itemlist.add(new InventoryItem(
                        rs.getInt(0),
                        rs.getString(1), 
                        rs.getInt(2), 
                        rs.getString(3), 
                        rs.getString(4), 
                        rs.getDouble(5)
                        ));
                }
                dtm.setRowCount(0); // reset data model
                for (int i = 0; i < itemlist.size(); i++) {
                    Object[] objs = {
                            itemlist.get(i).itemnum,
                            itemlist.get(i).itemqty,
                            itemlist.get(i).itemdept,
                            itemlist.get(i).itemdesc,
                            itemlist.get(i).itemcost,
                        };
                    dtm.addRow(objs);
                }

            } catch (Exception err) {
                System.out.println(err);
            }
                // Place here
            }
        });

        addEnterKeyListener(jtf_itemnum);
        addEnterKeyListener(jtf_itemqty);
        addEnterKeyListener(jtf_itemdept);
        addEnterKeyListener(jta_itemdesc);
        addEnterKeyListener(jtf_itemcost);

        // Set colors for the different elements
        jp_entry.setBackground(Color.LIGHT_GRAY);
        jp_button.setBackground(Color.DARK_GRAY);
        jb_dropTable.setBackground(Color.RED);
    }

    private void addEnterKeyListener(JComponent component) {
        component.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (areFieldsFilled()) {
                        addInventoryItem();
                    } else {
                        JOptionPane.showMessageDialog(jf, "Please fill out all fields");
                    }
                }
            }
        });
    }

    private boolean areFieldsFilled() {
        return !jtf_itemnum.getText().trim().isEmpty() &&
                !jtf_itemqty.getText().trim().isEmpty() &&
                !jtf_itemdept.getText().trim().isEmpty() &&
                !jta_itemdesc.getText().trim().isEmpty() &&
                !jtf_itemcost.getText().trim().isEmpty();
    }

    // Short circuit the addInventoryItemListener to a separate method just for the event of the enter key being pressed from the jp_entry panel

    private void addInventoryItem() {
        int itemnum = Integer.parseInt(jtf_itemnum.getText().toString());
        int itemqty = Integer.parseInt(jtf_itemqty.getText().toString());
        String itemdept = jtf_itemdept.getText().toString();
        String itemdesc = jta_itemdesc.getText().toString();
        Double itemcost = Double.parseDouble(jtf_itemcost.getText().toString());

        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("INSERT INTO tbl_items (`item_num`, `item_qty`, `item_dept`, `item_desc`, `item_cost`) VALUES ('" +
                    itemnum + "','" + itemqty + "','" + itemdept + "','" + itemdesc + "','" + itemcost + "')");
            loadData();
        } catch (Exception err) {
            System.out.println(err);
        }
    }

    ActionListener addInventoryItemListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            int itemnum = Integer.parseInt(jtf_itemnum.getText().toString());
            int itemqty = Integer.parseInt(jtf_itemqty.getText().toString());
            String itemdept = jtf_itemdept.getText().toString();
            String itemdesc = jta_itemdesc.getText().toString();
            if (itemnum <= 0) {
                JOptionPane.showMessageDialog(jf, "Please enter an item number");
                jtf_itemnum.requestFocus();
            } else {
                try {
                    itemnum = Integer.parseInt(jtf_itemnum.getText().toString());
                } catch (Exception err) {
                    JOptionPane.showMessageDialog(jf, "Please enter a valid item number");
                    jtf_itemnum.requestFocus();
                }
            }
            String text = jtf_itemqty.getText().toString();
            if (text.isEmpty()) {
                JOptionPane.showMessageDialog(jf, "Please enter an item quantity");
                jtf_itemqty.requestFocus();
            } else {
                try {
                    itemqty = Integer.parseInt(jtf_itemqty.getText().toString());
                } catch (Exception err) {
                    JOptionPane.showMessageDialog(jf, "Please enter a valid item quantity");
                    jtf_itemqty.requestFocus();
                }
            }
            if(itemdept.isEmpty()) {
                JOptionPane.showMessageDialog(jf, "Please enter an item dept");
                jtf_itemdept.requestFocus();
            }
            if(itemdesc.isEmpty()) {
                JOptionPane.showMessageDialog(jf, "Please enter an item description");
                jta_itemdesc.requestFocus();
            }
            String itemcost = jtf_itemcost.getText().toString();
            if(itemcost.isEmpty()) {
                JOptionPane.showMessageDialog(jf, "Please enter an item price");
                jtf_itemcost.requestFocus();
            }
            int result = JOptionPane.showConfirmDialog(jf, "Insert this item data " + itemdept + "?", "Insert",
                         JOptionPane.YES_NO_OPTION,
                         JOptionPane.QUESTION_MESSAGE);
                    
                if (result == JOptionPane.YES_OPTION) {
                    try {
                        Statement stmt = conn.createStatement();
                        stmt.executeUpdate("INSERT INTO tbl_items (`item_num`, `item_qty`, `item_dept`, `item_desc`, `item_cost`) VALUES ('" +
                                itemnum + "','" + itemqty + "','" + itemdept + "','" + itemdesc + "','" + itemcost + "')");
                        loadData();
                    } catch (Exception err) {
                        System.out.println(err);
                    }
                }
            }
        };

    private void checkTables() {
        System.out.println("Check table");
        String sql = "CREATE TABLE IF NOT EXISTS tbl_items (" +
                "	id integer PRIMARY KEY AUTOINCREMENT," +
                "   item_num text NOT NULL," +
                "	item_qty integer NOT NULL," +
                "	item_dept text NOT NULL," +
                "	item_desc text NOT NULL," +
                "	item_cost real NOT NULL" +
                ");";
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(sql);
        } catch (Exception err) {
            System.out.println(err);
        }
    }


    private void loadData() throws SQLException {
        System.out.println("Load data");
        itemlist = new ArrayList<>();
        Statement stmt = conn.createStatement();
        rs = stmt.executeQuery("SELECT * FROM tbl_items");
        itemlist.clear();
        while (rs.next()) {
            itemlist.add(new InventoryItem(
                rs.getInt(1),
                rs.getString(2), 
                rs.getInt(3), 
                rs.getString(4), 
                rs.getString(5), 
                rs.getDouble(6)));
        dtm.setRowCount(0); // reset data model
        for (int i = 1; i < itemlist.size(); i++) {
            Object[] objs = {
                itemlist.get(i).itemid,
                itemlist.get(i).itemnum,
                itemlist.get(i).itemqty,
                itemlist.get(i).itemdept,
                itemlist.get(i).itemdesc,
                itemlist.get(i).itemcost,
            };
            dtm.addRow(objs);
        }
    }
}

    MouseInputAdapter mouseListener = new MouseInputAdapter() {
        @Override
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            int row = jt.rowAtPoint(evt.getPoint());
            int col = jt.columnAtPoint(evt.getPoint());
            if (row >= 0 && col >= 0) {
                jtf_itemid.setText(jt.getValueAt(row, 0).toString());
                jtf_itemnum.setText(jt.getValueAt(row, 1).toString());
                jtf_itemqty.setText(jt.getValueAt(row, 2).toString());
                jtf_itemdept.setText(jt.getValueAt(row, 3).toString());
                jta_itemdesc.setText(jt.getValueAt(row, 4).toString());
                jtf_itemcost.setText(jt.getValueAt(row, 5).toString());
                item = new InventoryItem(
                    Integer.parseInt(jt.getValueAt(row, 0).toString()),
                    jt.getValueAt(row, 1).toString(), 
                    Integer.parseInt(jt.getValueAt(row, 2).toString()),
                    jt.getValueAt(row, 3).toString(), 
                    jt.getValueAt(row, 4).toString(), 
                    Double.parseDouble(jt.getValueAt(row, 5).toString()));
                /*
                item = new InventoryItem(Integer.parseInt(jt.getValueAt(row, 0).toString()), jt.getValueAt(row, 1).toString(),
                        Double.parseDouble(jt.getValueAt(row, 0).toString()), jt.getValueAt(row, 0).toString());
                */
            }
        }
    };

    ActionListener updateInventoryItemListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String itemid = jtf_itemid.getText().toString();
            String itemnum = jtf_itemnum.getText().toString();
            String itemqty = jtf_itemqty.getText().toString();
            String itemdept = jtf_itemdept.getText().toString();
            String itemdesc = jta_itemdesc.getText().toString();
            String itemprice = jtf_itemcost.getText().toString();
            
            if (item == null) {
                System.out.println("Null");
            } else {

                int result = JOptionPane.showConfirmDialog(jf, "Update " + item.itemdept + "?", "Selection Pane",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                if (result == JOptionPane.YES_OPTION) {
                    try {
                        System.out.println("InventoryItem " + item.itemdept);
                        Statement stmt = conn.createStatement();
                        stmt.executeUpdate("UPDATE tbl_items SET item_num = '" + itemnum + "', item_qty = '" + itemqty + "', item_dept = '" + itemdept + "', item_desc = '" + itemdesc + "', item_cost = '" + itemprice + "' WHERE id = '" + itemid + "'");
                        loadData();
                    } catch (Exception err) {
                        System.out.println(err);
                    }
                }

            }
        }
    };

    ActionListener delInventoryItemListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            String itemid = jtf_itemid.getText().toString();
            if (item == null) {
                System.out.println("Null");
            } else {

                int result = JOptionPane.showConfirmDialog(jf, "Delete item number " + item.itemnum + "?", "Selection Pane",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                if (result == JOptionPane.YES_OPTION) {
                    try {
                        System.out.println("InventoryItem " + item.itemdept);
                        Statement stmt = conn.createStatement();
                        stmt.executeUpdate("DELETE FROM tbl_items WHERE ID = '" + itemid + "'");
                        loadData();
                    } catch (Exception err) {
                        System.out.println(err);
                    }
                }

            }

        }
    };

    ActionListener searchInventoryItemListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {

            String search = JOptionPane.showInputDialog("Enter partial name or description(leave blank to show all)");
            System.out.println(search);

            itemlist = new ArrayList<>();
            try {

                Statement stmt = conn.createStatement();
                rs = stmt.executeQuery(
                		"SELECT * FROM tbl_items WHERE item_num LIKE '%" + search + "%' OR item_desc LIKE '%" + search + "%'");
                itemlist.clear();
                while (rs.next()) {
                    itemlist.add(new InventoryItem(
                        rs.getInt(0),
                        rs.getString(1), 
                        rs.getInt(2), 
                        rs.getString(3), 
                        rs.getString(4), 
                        rs.getDouble(5)
                        ));
                }
                dtm.setRowCount(0); // reset data model
                for (int i = 0; i < itemlist.size(); i++) {
                    Object[] objs = {
                            itemlist.get(i).itemnum,
                            itemlist.get(i).itemqty,
                            itemlist.get(i).itemdept,
                            itemlist.get(i).itemdesc,
                            itemlist.get(i).itemcost,
                        };
                    dtm.addRow(objs);
                }

            } catch (Exception err) {
                System.out.println(err);
            }
        }

    };
}
