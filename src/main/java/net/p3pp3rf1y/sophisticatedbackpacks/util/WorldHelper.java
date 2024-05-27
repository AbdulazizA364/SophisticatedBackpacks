package net.p3pp3rf1y.sophisticatedbackpacks.util;

import com.gtnewhorizons.angelica.api.BlockPos;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Optional;

public class WorldHelper {
	private WorldHelper() {}

	public static Optional<TileEntity> getTile(@Nullable IBlockAccess world, BlockPos pos) {
		return getTile(world, pos, TileEntity.class);
	}

	public static <T> Optional<T> getTile(@Nullable IBlockAccess world, BlockPos pos, Class<T> teClass) {
		if (world == null) {
			return Optional.empty();
		}

		TileEntity te = world.getTileEntity(pos.getX(), pos.getY(), pos.getZ());

		if (teClass.isInstance(te)) {
			return Optional.of(teClass.cast(te));
		}

		return Optional.empty();
	}

    public static void notifyBlockUpdate(TileEntity tile) {
        World world = tile.getWorldObj();
        if (world == null) {
            return;
        }
        int x = tile.xCoord;
        int y = tile.yCoord;
        int z = tile.zCoord;
        world.notifyBlockChange(x, y, z, tile.getBlockType());
    }
}
