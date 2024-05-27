package net.p3pp3rf1y.sophisticatedbackpacks.util;

import com.google.common.collect.Lists;
import net.MUI2.future.IItemHandler;
import net.MUI2.future.IItemHandlerModifiable;
import net.MUI2.future.ItemHandlerHelper;
import net.MUI2.future.ItemStackHandler;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
//import net.minecraft.util.SoundCategory;
//import net.minecraft.util.SoundEvents;
import net.minecraftforge.client.event.sound.SoundEvent;
import net.p3pp3rf1y.sophisticatedbackpacks.polyfill.mc.util.math.MathHelper;
import net.minecraft.world.World;
import net.p3pp3rf1y.sophisticatedbackpacks.api.IBackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.api.IPickupResponseUpgrade;
import org.apache.commons.lang3.mutable.MutableInt;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class InventoryHelper {
	private InventoryHelper() {}

	public static boolean hasItem(IItemHandler inventory, Predicate<ItemStack> matches) {
		AtomicBoolean result = new AtomicBoolean(false);
		iterate(inventory, (slot, stack) -> {
			if (stack != null && matches.test(stack)) {
				result.set(true);
			}
		}, result::get);
		return result.get();
	}

	public static Set<Integer> getItemSlots(IItemHandler inventory, Predicate<ItemStack> matches) {
		Set<Integer> slots = new HashSet<>();
		iterate(inventory, (slot, stack) -> {
			if (stack != null && matches.test(stack)) {
				slots.add(slot);
			}
		});
		return slots;
	}

	public static void copyTo(IItemHandlerModifiable handlerA, IItemHandlerModifiable handlerB) {
		int slotsA = handlerA.getSlots();
		int slotsB = handlerB.getSlots();
		for (int slot = 0; slot < slotsA && slot < slotsB; slot++) {
			ItemStack slotStack = handlerA.getStackInSlot(slot);
			if (slotStack != null) {
				handlerB.setStackInSlot(slot, slotStack);
			}
		}
	}

	public static List<ItemStack> insertIntoInventory(List<ItemStack> stacks, IItemHandler inventory, boolean simulate) {
		if (stacks.isEmpty()) {
			return stacks;
		}
		IItemHandler targetInventory = inventory;
		if (simulate) {
			targetInventory = cloneInventory(inventory);
		}

		List<ItemStack> remaining = new ArrayList<>();
		for (ItemStack stack : stacks) {
			ItemStack result = insertIntoInventory(stack, targetInventory, false);
			if (result != null) {
				remaining.add(result);
			}
		}
		return remaining;
	}

	public static IItemHandler cloneInventory(IItemHandler inventory) {
		IItemHandler cloned = new ItemStackHandler(inventory.getSlots());
		for (int slot = 0; slot < inventory.getSlots(); slot++) {
			cloned.insertItem(slot, inventory.getStackInSlot(slot).copy(), false);
		}
		return cloned;
	}

	public static ItemStack insertIntoInventory(ItemStack stack, IItemHandler inventory, boolean simulate) {
		if (inventory instanceof IItemHandlerSimpleInserter) {
			return ((IItemHandlerSimpleInserter) inventory).insertItem(stack, simulate);
		}

		ItemStack remainingStack = stack.copy();
		int slots = inventory.getSlots();
		for (int slot = 0; slot < slots && remainingStack != null; slot++) {
			remainingStack = inventory.insertItem(slot, remainingStack, simulate);
		}
		return remainingStack;
	}

	public static ItemStack extractFromInventory(Item item, int count, IItemHandler inventory, boolean simulate) {
		ItemStack ret = null ;
		int slots = inventory.getSlots();
		for (int slot = 0; slot < slots && ret.stackSize < count; slot++) {
			ItemStack slotStack = inventory.getStackInSlot(slot);
			if (slotStack.getItem() == item && (ret == null || ret.stackSize <= 0 || ItemHandlerHelper.canItemStacksStack(ret, slotStack))) {
				int toExtract = Math.min(slotStack.stackSize, count - ret.stackSize);
				ItemStack extractedStack = inventory.extractItem(slot, toExtract, simulate);
				if (ret == null || ret.stackSize <= 0) {
					ret = extractedStack;
				} else {
					ret.stackSize = (ret.stackSize + extractedStack.stackSize);
				}
			}
		}
		return ret;
	}

	public static ItemStack extractFromInventory(ItemStack stack, IItemHandler inventory, boolean simulate) {
		int extractedCount = 0;
		int slots = inventory.getSlots();
		for (int slot = 0; slot < slots && extractedCount < stack.stackSize; slot++) {
			ItemStack slotStack = inventory.getStackInSlot(slot);
			if (ItemHandlerHelper.canItemStacksStack(stack, slotStack)) {
				int toExtract = Math.min(slotStack.stackSize, stack.stackSize - extractedCount);
				extractedCount += inventory.extractItem(slot, toExtract, simulate).stackSize;
			}
		}

		if (extractedCount == 0) {
			return null;
		}

		ItemStack result = stack.copy();
		result.stackSize = (extractedCount);

		return result;
	}

	public static ItemStack runPickupOnBackpack(World world, ItemStack remainingStack, IBackpackWrapper backpackWrapper, boolean simulate) {
		return runPickupOnBackpack(world, null, remainingStack, backpackWrapper, simulate);
	}

	public static ItemStack runPickupOnBackpack(World world, @Nullable EntityPlayer player, ItemStack remainingStack, IBackpackWrapper backpackWrapper, boolean simulate) {
		List<IPickupResponseUpgrade> pickupUpgrades = backpackWrapper.getUpgradeHandler().getWrappersThatImplement(IPickupResponseUpgrade.class);

		for (IPickupResponseUpgrade pickupUpgrade : pickupUpgrades) {
			int countBeforePickup = remainingStack.stackSize;
			remainingStack = pickupUpgrade.pickup(world, remainingStack, simulate);
			if (!simulate && player != null && remainingStack.stackSize != countBeforePickup) {
				playPickupSound(world, player);
			}

			if (remainingStack == null || remainingStack.stackSize <= 0) {
				return null;
			}
		}

		return remainingStack;
	}

	@SuppressWarnings("squid:S1764") // this actually isn't a case of identical values being used as both side are random float value thus -1 to 1 as a result
	private static void playPickupSound(World world, @Nonnull EntityPlayer player) {
        world.playSoundAtEntity(player, "random.pop", 0.2F, (world.rand.nextFloat() - world.rand.nextFloat()) * 1.4F + 2.0F);
	}

	public static void iterate(IItemHandler handler, BiConsumer<Integer, ItemStack> actOn) {
		iterate(handler, actOn, () -> false);
	}

	public static void iterate(IItemHandler handler, BiConsumer<Integer, ItemStack> actOn, BooleanSupplier shouldExit) {
		int slots = handler.getSlots();
		for (int slot = 0; slot < slots; slot++) {
			ItemStack stack = handler.getStackInSlot(slot);
			actOn.accept(slot, stack);
			if (shouldExit.getAsBoolean()) {
				break;
			}
		}
	}

	public static int getCountMissingInHandler(IItemHandler itemHandler, ItemStack filter, int expectedCount) {
		MutableInt missingCount = new MutableInt(expectedCount);
		iterate(itemHandler, (slot, stack) -> {
			if (ItemHandlerHelper.canItemStacksStack(stack, filter)) {
				missingCount.subtract(Math.min(stack.stackSize, missingCount.getValue()));
			}
		}, () -> missingCount.getValue() == 0);
		return missingCount.getValue();
	}

	public static <T> T iterate(IItemHandler handler, BiFunction<Integer, ItemStack, T> getFromSlotStack, Supplier<T> supplyDefault, Predicate<T> shouldExit) {
		T ret = supplyDefault.get();
		int slots = handler.getSlots();
		for (int slot = 0; slot < slots; slot++) {
			ItemStack stack = handler.getStackInSlot(slot);
			ret = getFromSlotStack.apply(slot, stack);
			if (shouldExit.test(ret)) {
				break;
			}
		}
		return ret;
	}

	public static void transfer(IItemHandler handlerA, IItemHandler handlerB, Consumer<Supplier<ItemStack>> onInserted) {
		int slotsA = handlerA.getSlots();
		for (int slot = 0; slot < slotsA; slot++) {
			ItemStack slotStack = handlerA.getStackInSlot(slot);
			if (slotStack == null || slotStack.stackSize <= 0) {
				continue;
			}

			ItemStack resultStack = insertIntoInventory(slotStack, handlerB, true);
			int countToExtract = slotStack.stackSize - resultStack.stackSize;
			if (countToExtract > 0 && handlerA.extractItem(slot, countToExtract, true).stackSize == countToExtract) {
				InventoryHelper.insertIntoInventory(handlerA.extractItem(slot, countToExtract, false), handlerB, false);
				onInserted.accept(() -> {
					ItemStack copiedStack = slotStack.copy();
					copiedStack.stackSize = (countToExtract);
					return copiedStack;
				});
			}
		}
	}

	public static boolean isEmpty(IItemHandler itemHandler) {
		int slots = itemHandler.getSlots();
		for (int slot = 0; slot < slots; slot++) {
			if (itemHandler.getStackInSlot(slot) != null) {
				return false;
			}
		}
		return true;
	}

	public static ItemStack getAndRemove(IItemHandler itemHandler, int slot) {
		if (slot >= itemHandler.getSlots()) {
			return null;
		}
		return itemHandler.extractItem(slot, itemHandler.getStackInSlot(slot).stackSize, false);
	}

	public static void insertOrDropItem(EntityPlayer player, ItemStack stack, IItemHandler... inventories) {
		ItemStack ret = stack;
		for (IItemHandler inventory : inventories) {
			ret = insertIntoInventory(ret, inventory, false);
			if (ret == null || ret.stackSize <= 0) {
				return;
			}
		}
		if (ret != null) {
			player.dropPlayerItemWithRandomChoice(ret, true);
		}

	}

	static Map<ItemStackKey, Integer> getCompactedStacks(IItemHandler handler) {
		return getCompactedStacks(handler, new HashSet<>());
	}

	static Map<ItemStackKey, Integer> getCompactedStacks(IItemHandler handler, Set<Integer> ignoreSlots) {
		Map<ItemStackKey, Integer> ret = new HashMap<>();
		iterate(handler, (slot, stack) -> {
			if (stack== null || stack.stackSize <= 0 || ignoreSlots.contains(slot)) {
				return;
			}
			ItemStackKey itemStackKey = new ItemStackKey(stack);
			ret.put(itemStackKey, ret.computeIfAbsent(itemStackKey, fs -> 0) + stack.stackSize);
		});
		return ret;
	}

	public static List<ItemStack> getCompactedStacksSortedByCount(IItemHandler handler) {
		Map<ItemStackKey, Integer> compactedStacks = getCompactedStacks(handler);
		List<Map.Entry<ItemStackKey, Integer>> sortedList = new ArrayList<>(compactedStacks.entrySet());
		sortedList.sort(InventorySorter.BY_COUNT);

		List<ItemStack> ret = new ArrayList<>();
		sortedList.forEach(e -> {
			ItemStack stackCopy = e.getKey().getStack().copy();
			stackCopy.stackSize = (e.getValue());
			ret.add(stackCopy);
		});
		return ret;
	}

	public static Set<ItemStackKey> getUniqueStacks(IItemHandler handler) {
		Set<ItemStackKey> uniqueStacks = new HashSet<>();
		iterate(handler, (slot, stack) -> {
			if (stack == null || stack.stackSize <= 0) {
				return;
			}
			ItemStackKey itemStackKey = new ItemStackKey(stack);
			uniqueStacks.add(itemStackKey);
		});
		return uniqueStacks;
	}

	public static List<Integer> getEmptySlotsRandomized(IItemHandler inventory, Random rand) {
		List<Integer> list = Lists.newArrayList();

		for (int i = 0; i < inventory.getSlots(); ++i) {
			if (inventory.getStackInSlot(i) == null || inventory.getStackInSlot(i).stackSize <= 0) {
				list.add(i);
			}
		}

		Collections.shuffle(list, rand);
		return list;
	}

	public static void shuffleItems(List<ItemStack> stacks, int emptySlotsCount, Random rand) {
		List<ItemStack> list = Lists.newArrayList();
		Iterator<ItemStack> iterator = stacks.iterator();

		while (iterator.hasNext()) {
			ItemStack itemstack = iterator.next();
			if (itemstack == null || itemstack.stackSize <= 0) {
				iterator.remove();
			} else if (itemstack.stackSize > 1) {
				list.add(itemstack);
				iterator.remove();
			}
		}

		while (emptySlotsCount - stacks.size() - list.size() > 0 && !list.isEmpty()) {
			ItemStack itemstack2 = list.remove(MathHelper.nextInt(rand, 0, list.size() - 1));
			int i = MathHelper.nextInt(rand, 1, itemstack2.stackSize / 2);
			ItemStack itemstack1 = itemstack2.splitStack(i);
			if (itemstack2.stackSize > 1 && rand.nextBoolean()) {
				list.add(itemstack2);
			} else {
				stacks.add(itemstack2);
			}

			if (itemstack1.stackSize > 1 && rand.nextBoolean()) {
				list.add(itemstack1);
			} else {
				stacks.add(itemstack1);
			}
		}

		stacks.addAll(list);
		Collections.shuffle(stacks, rand);
	}
}
