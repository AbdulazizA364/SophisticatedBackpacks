package net.p3pp3rf1y.sophisticatedbackpacks.util;

import net.MUI2.future.ItemStackHandler;
import net.minecraft.item.ItemStack;

public class FilterItemStackHandler extends ItemStackHandler {
	private boolean onlyEmptyFilters = true;

	public FilterItemStackHandler(int size) {super(size);}

	@Override
	public int getSlotLimit(int slot) {
		return 1;
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		return null;
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		return null;
	}

	@Override
	protected void onContentsChanged(int slot) {
		super.onContentsChanged(slot);

		updateEmptyFilters();
	}

	@Override
	protected void onLoad() {
		super.onLoad();

		updateEmptyFilters();
	}

	private void updateEmptyFilters() {
		onlyEmptyFilters = InventoryHelper.iterate(this, (s, filter) -> (filter == null || filter.stackSize <= 0), () -> true, result -> !result);
	}

	public boolean hasOnlyEmptyFilters() {
		return onlyEmptyFilters;
	}
}
