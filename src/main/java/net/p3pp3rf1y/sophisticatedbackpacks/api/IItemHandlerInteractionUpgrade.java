package net.p3pp3rf1y.sophisticatedbackpacks.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.items.IItemHandler;

public interface IItemHandlerInteractionUpgrade {
	void onHandlerInteract(IItemHandler itemHandler, EntityPlayer player);
}
