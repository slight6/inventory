import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.MouseInputAdapter;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InventoryApp {
    JTextField jtf_itemname;
    JTextField jtf_itemprice;
    JTextArea jta_itemdesc;
    JButton jb_add, jb_delete, jb_update, jb_search;
    JTable jt;
    JFrame frame;
    JLabel lbl_itemname, lbl_itemprice, lbl_itemdesc;
    ArrayList<InventoryItem> itemlist;
    InventoryItem item;
    String header[] = new String[] {
            "ID",
            "Item Name",
            "Item Price",
            "Item Description"
    };
    DefaultTableModel dtm = new DefaultTableModel(0, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    static Connection conn;
    ResultSet rs;
    int row, col;

    public static void main(String[] args) throws Exception {
        String url = "jdbc:sqlite:inventory.db";
        conn = DriverManager.getConnection(url);
        InventoryApp app = new InventoryApp();
        app.mainInterface();
        app.checkTables();
        app.loadData();
    }

    private void mainInterface() {
        frame = new JFrame();
        lbl_itemname = new JLabel();
        lbl_itemname.setText("InventoryItem Name");
        lbl_itemname.setBounds(10, 10, 100, 50);
        frame.add(lbl_itemname);

        jtf_itemname = new JTextField();
        jtf_itemname.setBounds(100, 25, 250, 25);
        frame.add(jtf_itemname);

        lbl_itemprice = new JLabel();
        lbl_itemprice.setText("Price");
        lbl_itemprice.setBounds(10, 35, 100, 50);
        frame.add(lbl_itemprice);

        jtf_itemprice = new JTextField();
        jtf_itemprice.setBounds(100, 50, 100, 25);
        frame.add(jtf_itemprice);

        lbl_itemdesc = new JLabel();
        lbl_itemdesc.setText("Description");
        lbl_itemdesc.setBounds(10, 55, 100, 50);
        frame.add(lbl_itemdesc);

        jta_itemdesc = new JTextArea();
        jta_itemdesc.setBounds(100, 75, 250, 50);
        jta_itemdesc.setBorder(new JTextField().getBorder());
        frame.add(jta_itemdesc);

        jb_add = new JButton();
        jb_add.setText("Add");
        jb_add.setBounds(10, 140, 100, 25);
        frame.add(jb_add);
        jb_add.addActionListener(addInventoryItemListener);

        jb_delete = new JButton();
        jb_delete.setText("Delete");
        jb_delete.setBounds(120, 140, 100, 25);
        frame.add(jb_delete);
        jb_delete.addActionListener(delInventoryItemListener);

        jb_update = new JButton();
        jb_update.setText("Update");
        jb_update.setBounds(230, 140, 100, 25);
        frame.add(jb_update);
        jb_update.addActionListener(updateInventoryItemListener);

        jb_search = new JButton();
        jb_search.setText("Search");
        jb_search.setBounds(340, 140, 100, 25);
        frame.add(jb_search);
        jb_search.addActionListener(searchInventoryItemListener);

        jt = new JTable();
        jt.setModel(dtm);
        dtm.setColumnIdentifiers(header);
        JScrollPane sp = new JScrollPane(jt);
        sp.setBounds(10, 170, 430, 600);
        frame.add(sp);
        jt.addMouseListener(mouseListener);

        frame.setSize(480, 800);
        frame.setLayout(null); // using no layout managers
        frame.setVisible(true); // making the frame visible
    }

    ActionListener addInventoryItemListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String itemname = jtf_itemname.getText().toString();
            String itemprice = jtf_itemprice.getText().toString();
            String itemdesc = jta_itemdesc.getText().toString();
            if (itemname.isEmpty() || itemprice.isEmpty() || itemdesc.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter item info");
                jtf_itemname.requestFocus();
            } else {
                int result = JOptionPane.showConfirmDialog(frame, "Insert this item data " + itemname + "?", "Insert",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                if (result == JOptionPane.YES_OPTION) {
                    try {
                        Statement stmt = conn.createStatement();
                        stmt.executeUpdate("insert into tbl_items (`item_name`, `item_price`, `item_desc`) VALUES ('" +
                                itemname + "','" + itemprice + "','" + itemdesc + "')");
                        loadData();
                    } catch (Exception err) {
                        System.out.println(err);
                    }
                }
            }
        }
    };

    private void checkTables() {
        System.out.println("Check table");
        String sql = "CREATE TABLE IF NOT EXISTS tbl_items (" +
                "	id integer PRIMARY KEY AUTOINCREMENT," +
                "	item_name text NOT NULL," +
                "	item_price real NOT NULL," +
                "	item_desc text NOT NULL" +
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
        rs = stmt.executeQuery("select * from tbl_items");
        itemlist.clear();
        while (rs.next()) {
            itemlist.add(new InventoryItem(rs.getInt(1), rs.getString(2), rs.getFloat(3), rs.getString(4)));
        }
        dtm.setRowCount(0); // reset data model
        for (int i = 0; i < itemlist.size(); i++) {
            Object[] objs = {
                    itemlist.get(i).itemid,
                    itemlist.get(i).itemname,
                    itemlist.get(i).itemprice,
                    itemlist.get(i).itemdesc,
            };
            dtm.addRow(objs);
        }
    }

    MouseInputAdapter mouseListener = new MouseInputAdapter() {
        @Override
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            int row = jt.rowAtPoint(evt.getPoint());
            int col = jt.columnAtPoint(evt.getPoint());
            if (row >= 0 && col >= 0) {
                jtf_itemname.setText(jt.getValueAt(row, 1).toString());
                jtf_itemprice.setText(jt.getValueAt(row, 2).toString());
                jta_itemdesc.setText(jt.getValueAt(row, 3).toString());
                item = new InventoryItem(Integer.parseInt(jt.getValueAt(row, 0).toString()), jt.getValueAt(row, 1).toString(),
                        Double.parseDouble(jt.getValueAt(row, 0).toString()), jt.getValueAt(row, 0).toString());
            }
        }
    };

    ActionListener updateInventoryItemListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String itemname = jtf_itemname.getText().toString();
            String itemprice = jtf_itemprice.getText().toString();
            String itemdesc = jta_itemdesc.getText().toString();
            if (item == null) {
                System.out.println("Null");
            } else {

                int result = JOptionPane.showConfirmDialog(frame, "Update " + item.itemname + "?", "Swing Tester",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                if (result == JOptionPane.YES_OPTION) {
                    try {
                        System.out.println("InventoryItem " + item.itemname);
                        Statement stmt = conn.createStatement();
                        stmt.executeUpdate("update tbl_items set item_name = '" + itemname + "', item_price = " +
                                itemprice + ", item_desc='" + itemdesc + "' where id =" + item.itemid + "");
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
            if (item == null) {
                System.out.println("Null");
            } else {

                int result = JOptionPane.showConfirmDialog(frame, "Delete " + item.itemname + "?", "Swing Tester",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                if (result == JOptionPane.YES_OPTION) {
                    try {
                        System.out.println("InventoryItem " + item.itemname);
                        Statement stmt = conn.createStatement();
                        stmt.executeUpdate("delete from tbl_items where id = '" + item.itemid + "'");
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

            String search = JOptionPane.showInputDialog("Enter partial name or description -");
            System.out.println(search);

            itemlist = new ArrayList<>();
            try {

                Statement stmt = conn.createStatement();
                rs = stmt.executeQuery(
                		"select * from tbl_items where item_name LIKE '%" + search + "%' OR item_desc LIKE '%" + search + "%'");
                itemlist.clear();
                while (rs.next()) {
                    itemlist.add(new InventoryItem(rs.getInt(1), rs.getString(2), rs.getFloat(3), rs.getString(4)));
                }
                dtm.setRowCount(0); // reset data model
                for (int i = 0; i < itemlist.size(); i++) {
                    Object[] objs = {
                            itemlist.get(i).itemid,
                            itemlist.get(i).itemname,
                            itemlist.get(i).itemprice,
                            itemlist.get(i).itemdesc,
                    };
                    dtm.addRow(objs);
                }

            } catch (Exception err) {
                System.out.println(err);
            }
        }

    };

}