public class InventoryItem {
    int itemid;
    String itemname,itemdesc;
    double itemprice;
    
    public InventoryItem(int itemid, String itemname,double itemprice,String itemdesc){
        this.itemid = itemid;
        this.itemname = itemname;
        this.itemprice = itemprice;
        this.itemdesc = itemdesc;
    }
}