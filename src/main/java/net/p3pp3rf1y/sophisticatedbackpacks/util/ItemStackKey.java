package net.p3pp3rf1y.sophisticatedbackpacks.util;

import net.MUI2.future.ItemHandlerHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;

import javax.annotation.Nullable;
import java.lang.reflect.Field;

public class ItemStackKey {
	public ItemStack getStack() {
		return stack;
	}

	private final ItemStack stack;

	public ItemStackKey(ItemStack stack) {
		this.stack = stack.copy();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {return true;}
		if (o == null || getClass() != o.getClass()) {return false;}
		ItemStackKey that = (ItemStackKey) o;
		return ItemHandlerHelper.canItemStacksStack(stack, that.stack);
	}

	@Override
	public int hashCode() {
		return getHashCode(stack);
	}

	public static int getHashCode(ItemStack stack) {
		int hash = stack.getItem().hashCode();
		if (stack.hasTagCompound()) {
			//noinspection ConstantConditions - hasTag call makes sure getTag doesn't return null
			hash = hash * 31 + stack.getTagCompound().hashCode();
		}
        NBTTagCompound capNbt = getCapNbt(stack);
		if (capNbt != null) {
			hash = hash * 31 + capNbt.hashCode();
		}
		return hash;
	}

    private static final Field CAP_NBT;

    static {
        Field capNbt = null;
        try {
            capNbt = ItemStack.class.getDeclaredField(ObfuscationReflectionHelper.remapFieldNames(ItemStack.class.getName(), "capNBT")[0]);
            capNbt.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        CAP_NBT = capNbt;
    }

	@Nullable
	private static NBTTagCompound getCapNbt(ItemStack stack) {
		try {
			return (NBTTagCompound) CAP_NBT.get(stack);
		}
		catch (IllegalAccessException e) {
			SophisticatedBackpacks.LOGGER.error("Error getting capNBT of stack ", e);
			return null;
		}
	}
}
