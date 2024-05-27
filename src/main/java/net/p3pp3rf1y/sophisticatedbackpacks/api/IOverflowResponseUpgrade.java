package net.p3pp3rf1y.sophisticatedbackpacks.api;

import net.minecraft.item.ItemStack;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.FilterLogic;
import net.p3pp3rf1y.sophisticatedbackpacks.upgrades.PrimaryMatch;
import net.p3pp3rf1y.sophisticatedbackpacks.util.ItemStackHelper;

public interface IOverflowResponseUpgrade {
	default boolean stackMatchesFilterStack(ItemStack stack, ItemStack filterStack) {
		if (stack.getItem() != filterStack.getItem()) {
			return false;
		}

		if (getFilterLogic().getPrimaryMatch() == PrimaryMatch.TAGS) {
			return true;
		}

		if (getFilterLogic().shouldMatchDurability() && stack.getItemDamage() != filterStack.getItemDamage()) {
			return false;
		}

		return !getFilterLogic().shouldMatchNbt() || ItemStackHelper.areItemStackTagsEqualIgnoreDurability(stack, filterStack);
	}

	FilterLogic getFilterLogic();

	boolean worksInGui();

	ItemStack onOverflow(ItemStack stack);
	boolean stackMatchesFilter(ItemStack stack);
}
