package net.p3pp3rf1y.sophisticatedbackpacks.common.gui;

import net.minecraft.nbt.NBTTagCompound;

public interface ISyncedContainer {
	void handleMessage(NBTTagCompound data);
}
