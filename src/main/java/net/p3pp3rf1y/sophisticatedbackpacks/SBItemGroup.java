package net.p3pp3rf1y.sophisticatedbackpacks;

import net.minecraft.creativetab.CreativeTabs;
//import net.minecraft.item.ItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems;

public class SBItemGroup extends CreativeTabs {
	private ItemStack tabIcon;

	SBItemGroup() {
		super(SophisticatedBackpacks.MOD_ID);
	}

	@Override
	public ItemStack makeIcon() {
		if (tabIcon == null) {
			tabIcon = new ItemStack(ModItems.BACKPACK.get);
		}
		return tabIcon;
	}

    @Override
    public Item getTabIconItem() {
        return null;
    }
}
