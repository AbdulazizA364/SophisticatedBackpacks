package net.MUI2.future;

import com.google.common.base.Preconditions;

import net.minecraft.item.ItemStack;

/**
 * A wrapper that composes another IItemHandlerModifiable, exposing only a range of the composed slots. Shifting of slot
 * indices is handled automatically for you.
 */
public class RangedWrapper implements IItemHandlerModifiable {

    private final IItemHandlerModifiable compose;
    private final int minSlot;
    private final int maxSlot;

    public RangedWrapper(IItemHandlerModifiable compose, int minSlot, int maxSlotExclusive) {
        Preconditions.checkArgument(maxSlotExclusive > minSlot, "Max slot must be greater than min slot");
        this.compose = compose;
        this.minSlot = minSlot;
        this.maxSlot = maxSlotExclusive;
    }

    @Override
    public int getSlots() {
        return maxSlot - minSlot;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        if (checkSlot(slot)) {
            return compose.getStackInSlot(slot + minSlot);
        }

        return null;
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (checkSlot(slot)) {
            return compose.insertItem(slot + minSlot, stack, simulate);
        }

        return stack;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (checkSlot(slot)) {
            return compose.extractItem(slot + minSlot, amount, simulate);
        }

        return null;
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        if (checkSlot(slot)) {
            compose.setStackInSlot(slot + minSlot, stack);
        }
    }

    @Override
    public int getSlotLimit(int slot) {
        if (checkSlot(slot)) {
            return compose.getSlotLimit(slot + minSlot);
        }

        return 0;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        if (checkSlot(slot)) {
            return compose.isItemValid(slot + minSlot, stack);
        }

        return false;
    }

    private boolean checkSlot(int localSlot) {
        return localSlot + minSlot < maxSlot;
    }

    public IItemHandlerModifiable getCompose() {
        return compose;
    }
}
