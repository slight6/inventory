package com.twosevenpro.inventory;

public class InventoryItem {
    int itemid;
    String itemnum;
    int itemqty;
    String itemdept;
    String itemdesc;
    double itemcost;
 
    
    public InventoryItem(int itemid, String itemnum, int itemqty, String itemdept, String itemdesc, double itemcost) {
        this.itemid = itemid;
        this.itemnum = itemnum;
        this.itemqty = itemqty;
        this.itemdept = itemdept;
        this.itemdesc = itemdesc;
        this.itemcost = itemcost;
    }


}