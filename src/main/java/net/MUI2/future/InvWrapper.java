package net.MUI2.future;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import java.util.Objects;

public class InvWrapper implements IItemHandlerModifiable {

    private final IInventory inv;

    public InvWrapper(IInventory inv) {
        this.inv = Objects.requireNonNull(inv);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InvWrapper that = (InvWrapper) o;

        return getInv().equals(that.getInv());
    }

    @Override
    public int hashCode() {
        return getInv().hashCode();
    }

    @Override
    public int getSlots() {
        return getInv().getSizeInventory();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return getInv().getStackInSlot(slot);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (stack == null) return null;

        ItemStack stackInSlot = getInv().getStackInSlot(slot);

        int m;
        if (stackInSlot != null) {
            if (stackInSlot.stackSize >= Math.min(stackInSlot.getMaxStackSize(), getSlotLimit(slot))) return stack;

            if (!ItemHandlerHelper.canItemStacksStack(stack, stackInSlot)) return stack;

            if (!getInv().isItemValidForSlot(slot, stack)) return stack;

            m = Math.min(stack.getMaxStackSize(), getSlotLimit(slot)) - stackInSlot.stackSize;

            if (stack.stackSize <= m) {
                if (!simulate) {
                    ItemStack copy = stack.copy();
                    copy.stackSize += stackInSlot.stackSize;
                    getInv().setInventorySlotContents(slot, copy);
                    getInv().markDirty();
                }

                return null;
            } else {
                // copy the stack to not modify the original one
                stack = stack.copy();
                if (!simulate) {
                    ItemStack copy = stack.splitStack(m);
                    copy.stackSize += stackInSlot.stackSize;
                    getInv().setInventorySlotContents(slot, copy);
                    getInv().markDirty();
                } else {
                    stack.stackSize -= m;
                }
                return stack;
            }
        } else {
            if (!getInv().isItemValidForSlot(slot, stack)) return stack;

            m = Math.min(stack.getMaxStackSize(), getSlotLimit(slot));
            if (m < stack.stackSize) {
                // copy the stack to not modify the original one
                stack = stack.copy();
                if (!simulate) {
                    getInv().setInventorySlotContents(slot, stack.splitStack(m));
                    getInv().markDirty();
                } else {
                    stack.stackSize -= m;
                }
                return stack;
            } else {
                if (!simulate) {
                    getInv().setInventorySlotContents(slot, stack);
                    getInv().markDirty();
                }
                return null;
            }
        }
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount == 0) return null;

        ItemStack stackInSlot = getInv().getStackInSlot(slot);

        if (stackInSlot == null) return null;

        if (simulate) {
            if (stackInSlot.stackSize < amount) {
                return stackInSlot.copy();
            } else {
                ItemStack copy = stackInSlot.copy();
                copy.stackSize = amount;
                return copy;
            }
        } else {
            int m = Math.min(stackInSlot.stackSize, amount);

            ItemStack decrStackSize = getInv().decrStackSize(slot, m);
            getInv().markDirty();
            return decrStackSize;
        }
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        getInv().setInventorySlotContents(slot, stack == null ? null : stack);
    }

    @Override
    public int getSlotLimit(int slot) {
        return getInv().getInventoryStackLimit();
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return getInv().isItemValidForSlot(slot, stack);
    }

    @Deprecated
    public IInventory getInv() {
        return inv;
    }
}
