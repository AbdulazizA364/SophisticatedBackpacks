package net.MUI2.future;

import net.MUI2.future.IItemHandler;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public interface IItemHandlerModifiable extends IItemHandler {

    /**
     * Overrides the stack in the given slot. This method is used by the
     * standard Forge helper methods and classes. It is not intended for
     * general use by other mods, and the handler may throw an error if it
     * is called unexpectedly.
     *
     * @param slot  Slot to modify
     * @param stack ItemStack to set slot to (may be empty).
     * @throws RuntimeException if the handler is called in a way that the handler
     * was not expecting.
     **/
    void setStackInSlot(int slot, @Nullable ItemStack stack);
}
