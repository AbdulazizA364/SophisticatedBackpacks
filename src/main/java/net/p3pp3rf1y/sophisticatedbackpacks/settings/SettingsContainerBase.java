package net.p3pp3rf1y.sophisticatedbackpacks.settings;

//import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.SettingsContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.network.PacketHandler;
import net.p3pp3rf1y.sophisticatedbackpacks.network.SyncContainerClientDataMessage;

import java.util.function.Supplier;

public abstract class SettingsContainerBase<C extends ISettingsCategory> {
	private final SettingsContainer settingsContainer;
	private final String categoryName;
	private final C category;

	protected SettingsContainerBase(SettingsContainer settingsContainer, String categoryName, C category) {
		this.settingsContainer = settingsContainer;
		this.categoryName = categoryName;
		this.category = category;
	}

	protected C getCategory() {
		return category;
	}

	protected SettingsContainer getSettingsContainer() {
		return settingsContainer;
	}

	public void sendIntToServer(String key, int value) {
		sendDataToServer(() -> {
			NBTTagCompound data = new NBTTagCompound();
			data.setInteger(key, value);
			return data;
		});
	}

	public void sendStringToServer(String key, String value) {
		sendDataToServer(() -> {
            NBTTagCompound data = new NBTTagCompound();
			data.setString(key, value);
			return data;
		});
	}

	public void sendDataToServer(Supplier<NBTTagCompound> supplyData) {
		if (isServer()) {
			return;
		}
        NBTTagCompound data = supplyData.get();
		data.setString("categoryName", categoryName);
		PacketHandler.sendToServer(new SyncContainerClientDataMessage(data));
	}

	protected boolean isServer() {
		return !settingsContainer.getPlayer().worldObj.isRemote;
	}

	public abstract void handleMessage(NBTTagCompound data);
}
