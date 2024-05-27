package net.p3pp3rf1y.sophisticatedbackpacks.util;

import net.MUI2.future.IItemHandlerModifiable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
//import net.minecraftforge.items.IItemHandlerModifiable;

import java.util.*;

public class InventorySorter {
	private InventorySorter() {}

	public static final Comparator<Map.Entry<ItemStackKey, Integer>> BY_NAME = Comparator.comparing(o -> getUnlocalizedName(o.getKey()));

	public static final Comparator<Map.Entry<ItemStackKey, Integer>> BY_COUNT = (first, second) -> {
		int ret = second.getValue().compareTo(first.getValue());
		return ret != 0 ? ret : getUnlocalizedName(first.getKey()).compareTo(getUnlocalizedName(second.getKey()));
	};

	public static final Comparator<Map.Entry<ItemStackKey, Integer>> BY_TAGS = new Comparator<Map.Entry<ItemStackKey, Integer>>() {
		@Override
        public int compare(Map.Entry<ItemStackKey, Integer> first, Map.Entry<ItemStackKey, Integer> second) {
            ItemStack firstStack = first.getKey().getStack();
            ItemStack secondStack = second.getKey().getStack();
            Item firstItem = firstStack.getItem();
            Item secondItem = secondStack.getItem();

            if (firstItem == secondItem) {
                return 0;
            }

            Set<String> firstTags = getTagsFromItemStack(firstStack);
            Set<String> secondTags = getTagsFromItemStack(secondStack);
            int ret = compareTags(firstTags, secondTags);

            return ret != 0 ? ret : getUnlocalizedName(first.getKey()).compareTo(getUnlocalizedName(second.getKey()));
        }

        private Set<String> getTagsFromItemStack(ItemStack stack) {
            Set<String> tags = new HashSet<>();

            if (stack.hasTagCompound() && stack.getTagCompound().hasKey("tags")) {
                NBTTagList tagList = stack.getTagCompound().getTagList("tags", Constants.NBT.TAG_STRING);
                for (int i = 0; i < tagList.tagCount(); i++) {
                    tags.add(tagList.getStringTagAt(i));
                }
            }

            return tags;
        }

        private int compareTags(Set<String> firstTags, Set<String> secondTags) {
            int ret = Integer.compare(secondTags.size(), firstTags.size());
            if (ret != 0) {
                return ret;
            }

            if (firstTags.size() == 1) {
                return firstTags.iterator().next().compareTo(secondTags.iterator().next());
            }

            ArrayList<String> firstTagsSorted = new ArrayList<>(firstTags);
            ArrayList<String> secondTagsSorted = new ArrayList<>(secondTags);
            firstTagsSorted.sort(Comparator.naturalOrder());
            secondTagsSorted.sort(Comparator.naturalOrder());

            for (int i = 0; i < firstTagsSorted.size(); i++) {
                ret = firstTagsSorted.get(i).compareTo(secondTagsSorted.get(i));
                if (ret != 0) {
                    return ret;
                }
            }

            return 0;
        }
    };

	private static String getUnlocalizedName(ItemStackKey itemStackKey) {
		//noinspection ConstantConditions - registryName is nonNull by the time it exists in itemstack form
		return itemStackKey.getStack().getItem().getUnlocalizedName().toString();
	}

	public static void sortHandler(IItemHandlerModifiable handler, Comparator<? super Map.Entry<ItemStackKey, Integer>> comparator, Set<Integer> noSortSlots) {
		Map<ItemStackKey, Integer> compactedStacks = InventoryHelper.getCompactedStacks(handler, noSortSlots);
		List<Map.Entry<ItemStackKey, Integer>> sortedList = new ArrayList<>(compactedStacks.entrySet());
		sortedList.sort(comparator);

		int slots = handler.getSlots();
		Iterator<Map.Entry<ItemStackKey, Integer>> it = sortedList.iterator();
		ItemStackKey current = null;
		int count = 0;
		for (int slot = 0; slot < slots; slot++) {
			if (noSortSlots.contains(slot)) {
				continue;
			}
			if ((current == null || count <= 0) && it.hasNext()) {
				Map.Entry<ItemStackKey, Integer> entry = it.next();
				current = entry.getKey();
				count = entry.getValue();
			}
			if (current != null && count > 0) {
				count -= placeStack(handler, current, count, slot);
			} else {
				emptySlot(handler, slot);
			}
		}
	}

	private static void emptySlot(IItemHandlerModifiable handler, int slot) {
		if (handler.getStackInSlot(slot) != null) {
			handler.setStackInSlot(slot, null);
		}
	}

	private static int placeStack(IItemHandlerModifiable handler, ItemStackKey current, int count, int slot) {
		ItemStack copy = current.getStack().copy();
		int slotLimit = handler.getSlotLimit(slot);
		int countPlaced;
		if (slotLimit > 64) {
			countPlaced = Math.min(count, slotLimit / 64 * copy.getMaxStackSize());
		} else {
			countPlaced = Math.min(count, copy.getMaxStackSize());
		}
		copy.stackSize = (countPlaced);
		if (!ItemStack.areItemStacksEqual(handler.getStackInSlot(slot), copy)) {
			handler.setStackInSlot(slot, copy);
		}
		return countPlaced;
	}

}
