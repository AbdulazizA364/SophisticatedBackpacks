package net.p3pp3rf1y.sophisticatedbackpacks.upgrades;


import net.minecraft.nbt.NBTTagCompound;

import java.util.function.Supplier;

public interface IServerUpdater {
	void sendBooleanToServer(String key, boolean value);

	void sendDataToServer(Supplier<NBTTagCompound> supplyData);
}
