package net.p3pp3rf1y.sophisticatedbackpacks.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackInventoryHandler;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackRenderInfo;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackSettingsHandler;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackUpgradeHandler;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.SortBy;
import net.p3pp3rf1y.sophisticatedbackpacks.util.IItemHandlerSimpleInserter;
import net.p3pp3rf1y.sophisticatedbackpacks.util.energy.IEnergyStorage;

import java.util.Optional;
import java.util.UUID;

public interface IBackpackWrapper {

	void setBackpackSaveHandler(Runnable saveHandler);

	default void setInventorySlotChangeHandler(Runnable slotChangeHandler) {
		//noop
	}

	IItemHandlerSimpleInserter getInventoryForUpgradeProcessing();

	BackpackInventoryHandler getInventoryHandler();

	IItemHandlerSimpleInserter getInventoryForInputOutput();

	void copyDataTo(IBackpackWrapper otherBackpackWrapper);

	BackpackSettingsHandler getSettingsHandler();

	BackpackUpgradeHandler getUpgradeHandler();

	Optional<UUID> getContentsUuid();

	int getClothColor();

	int getBorderColor();

	Optional<Integer> getOpenTabId();

	void setOpenTabId(int openTabId);

	void removeOpenTabId();

	void setColors(int clothColor, int borderColor);

	void setSortBy(SortBy sortBy);

	SortBy getSortBy();

	ItemStack getBackpack();

	ItemStack cloneBackpack();

	void sort();

	void onContentsNbtUpdated();

	void refreshInventoryForUpgradeProcessing();

	void refreshInventoryForInputOutput();

	void setPersistent(boolean persistent);

	void setSlotNumbers(int numberOfInventorySlots, int numberOfUpgradeSlots);

	void setLoot(ResourceLocation lootTableName, float lootPercentage);

	void fillWithLoot(EntityPlayer playerEntity);

	void setContentsUuid(UUID backpackUuid);

	BackpackRenderInfo getRenderInfo();

	void setColumnsTaken(int columnsTaken);

	int getColumnsTaken();

	default int getNumberOfSlotRows() {
		return 0;
	}

	default Optional<IBackpackFluidHandler> getFluidHandler() {
		return Optional.empty();
	}

	default Optional<IEnergyStorage> getEnergyStorage() {return Optional.empty();}
}
