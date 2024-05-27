package net.p3pp3rf1y.sophisticatedbackpacks.util;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
//import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
//import net.minecraft.util.NonNullList;
import net.p3pp3rf1y.sophisticatedbackpacks.Config;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;

import java.util.List;

public class ItemBase extends Item {
    public ItemBase() {
        super();
        setCreativeTab(SophisticatedBackpacks.ITEM_GROUP); // Set the creative tab for the item
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> items) {
        if (Config.COMMON.enabledItems.isItemEnabled(this)) {
            super.getSubItems(item, tab, items);
        }
    }
}
