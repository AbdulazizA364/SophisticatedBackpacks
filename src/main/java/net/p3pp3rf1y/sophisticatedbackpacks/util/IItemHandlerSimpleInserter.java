package net.p3pp3rf1y.sophisticatedbackpacks.util;

import net.MUI2.future.IItemHandlerModifiable;
import net.minecraft.item.ItemStack;

public interface IItemHandlerSimpleInserter extends IItemHandlerModifiable {
	ItemStack insertItem(ItemStack stack, boolean simulate);
}
