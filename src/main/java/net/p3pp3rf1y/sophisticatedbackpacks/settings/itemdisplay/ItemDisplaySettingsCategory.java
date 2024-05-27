package net.p3pp3rf1y.sophisticatedbackpacks.settings.itemdisplay;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.p3pp3rf1y.sophisticatedbackpacks.api.IBackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackRenderInfo;
import net.p3pp3rf1y.sophisticatedbackpacks.polyfill.mc.util.DyeColor;
import net.p3pp3rf1y.sophisticatedbackpacks.settings.ISettingsCategory;
import net.p3pp3rf1y.sophisticatedbackpacks.settings.ISlotColorCategory;
import net.p3pp3rf1y.sophisticatedbackpacks.util.NBTHelper;

import java.util.Optional;
import java.util.function.Consumer;

public class ItemDisplaySettingsCategory implements ISettingsCategory, ISlotColorCategory {
	public static final String NAME = "item_display";
	private static final String SLOT_TAG = "slot";
	private static final String ROTATION_TAG = "rotation";
	private static final String COLOR_TAG = "color";
	private NBTTagCompound categoryNbt;
	private final Consumer<NBTTagCompound> saveNbt;
	private DyeColor color = DyeColor.RED;
	private final IBackpackWrapper backpackWrapper;
	private int slotIndex = -1;
	private int rotation = 0;

	public ItemDisplaySettingsCategory(IBackpackWrapper backpackWrapper, NBTTagCompound categoryNbt, Consumer<NBTTagCompound> saveNbt) {
		this.backpackWrapper = backpackWrapper;
		this.categoryNbt = categoryNbt;
		this.saveNbt = saveNbt;

		deserialize();
	}

	public void unselectSlot() {
		slotIndex = -1;
		categoryNbt.removeTag(SLOT_TAG);
		saveNbt.accept(categoryNbt);
		updateRenderInfo();
	}

	private void updateRenderInfo() {
		BackpackRenderInfo renderInfo = backpackWrapper.getRenderInfo();
		if (slotIndex >= 0) {
			ItemStack stackCopy = backpackWrapper.getInventoryHandler().getStackInSlot(slotIndex).copy();
			stackCopy.stackSize = (1);
			renderInfo.setItemDisplayRenderInfo(stackCopy, rotation);
		} else {
			renderInfo.setItemDisplayRenderInfo(null, 0);
		}
	}

	public void selectSlot(int slot) {
		slotIndex = slot;
		categoryNbt.setInteger(SLOT_TAG, slot);
		saveNbt.accept(categoryNbt);
		updateRenderInfo();
	}

	public Optional<Integer> getSlot() {
		return slotIndex >= 0 ? Optional.of(slotIndex) : Optional.empty();
	}

	public int getRotation() {
		return rotation;
	}

	public void rotate(boolean clockwise) {
		rotation = (rotation + ((clockwise ? 1 : -1) * 45) + 360) % 360;
		categoryNbt.setInteger(ROTATION_TAG, rotation);
		saveNbt.accept(categoryNbt);
		updateRenderInfo();
	}

	public void setColor(DyeColor color) {
		this.color = color;
		categoryNbt.setInteger(COLOR_TAG, color.getColor());
		saveNbt.accept(categoryNbt);
	}

	public DyeColor getColor() {
		return color;
	}

	@Override
	public void reloadFrom(NBTTagCompound categoryNbt) {
		this.categoryNbt = categoryNbt;
		deserialize();
	}

	private void deserialize() {
		slotIndex = NBTHelper.getInt(categoryNbt, SLOT_TAG).orElse(-1);
		rotation = NBTHelper.getInt(categoryNbt, ROTATION_TAG).orElse(0);
		color = NBTHelper.getInt(categoryNbt, COLOR_TAG).map(c -> color = DyeColor.fromIndex(c)).orElse(DyeColor.RED);
	}

	public void itemChanged(int changedSlotIndex) {
		if (changedSlotIndex != slotIndex) {
			return;
		}
		updateRenderInfo();
	}

	@Override
	public Optional<Integer> getSlotColor(int slotNumber) {
		return slotIndex == slotNumber ? Optional.of(color.getColor()) : Optional.empty();
	}
}
