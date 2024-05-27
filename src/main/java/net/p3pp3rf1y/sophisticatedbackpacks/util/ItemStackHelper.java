package net.p3pp3rf1y.sophisticatedbackpacks.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
//import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nullable;
import java.util.Objects;

public class ItemStackHelper {
	private ItemStackHelper() {}

	public static boolean areItemStackTagsEqualIgnoreDurability(ItemStack stackA, ItemStack stackB) {
		if (stackA == null || stackA.stackSize <= 0 && stackB == null || stackB.stackSize <= 0) {
			return true;
		} else if (stackA != null && stackB != null) {
			if (stackA.getTagCompound() == null && stackB.getTagCompound() != null) {
				return false;
			} else {
				return (stackA.getTagCompound() == null || areTagsEqualIgnoreDurability(stackA.getTagCompound(), stackB.getTagCompound())) && ItemStack.areItemStacksEqual(stackA, stackB);
			}
        } else {
            return false;
        }
	}

	public static boolean areTagsEqualIgnoreDurability(NBTTagCompound tagA, @Nullable NBTTagCompound tagB) {
		if (tagA == tagB) {
			return true;
		}
		if (tagB == null || tagA.func_150296_c().size() != tagB.func_150296_c().size()) {
			return false;
		}

		for (String key : tagA.func_150296_c()) {
			if (!tagB.hasKey (key)) {
				return false;
			}
			if (key.equals("Damage")) {
				continue;
			}
			if (!Objects.equals(tagA.getTag(key), tagB.getTag(key))) {
				return false;
			}
		}
		return true;
	}
}
