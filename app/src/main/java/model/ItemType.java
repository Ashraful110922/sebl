package model;

import java.util.ArrayList;
import java.util.List;

public class ItemType {
    private String itemType;
    private int image;
    private List<ItemDeals>deals = new ArrayList<ItemDeals>();

    public ItemType(String itemType,int image){
        this.itemType = itemType;
        this.image = image;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }


    public List<ItemDeals> getDeals() {
        return deals;
    }

    public void setDeals(List<ItemDeals> deals) {
        this.deals = deals;
    }
}
