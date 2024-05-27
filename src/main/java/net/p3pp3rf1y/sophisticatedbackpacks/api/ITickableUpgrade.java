package net.p3pp3rf1y.sophisticatedbackpacks.api;


import com.gtnewhorizons.angelica.api.BlockPos;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public interface ITickableUpgrade {
	void tick(@Nullable EntityLiving entity, World world, BlockPos pos);
}
