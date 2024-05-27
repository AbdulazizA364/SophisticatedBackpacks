package net.p3pp3rf1y.sophisticatedbackpacks.settings;


import net.minecraft.nbt.NBTTagCompound;

public interface ISettingsCategory {
	void reloadFrom(NBTTagCompound categoryNbt);
}
