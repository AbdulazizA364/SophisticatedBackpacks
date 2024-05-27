package net.p3pp3rf1y.sophisticatedbackpacks.upgrades;


import net.minecraft.inventory.Slot;
import net.minecraft.nbt.NBTTagCompound;
import net.p3pp3rf1y.sophisticatedbackpacks.util.NBTHelper;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ContentsFilterLogicContainer extends FilterLogicContainer<ContentsFilterLogic> {
	private static final String DATA_CONTENTS_FILTER_TYPE = "contentsFilterType";

	public ContentsFilterLogicContainer(Supplier<ContentsFilterLogic> filterLogic, IServerUpdater serverUpdater, Consumer<Slot> addSlot) {
		super(filterLogic, serverUpdater, addSlot);
		if (getFilterType() == ContentsFilterType.BACKPACK) {
			getFilterSlots().forEach(s -> s.setEnabled(false));
		}
	}

	public void setFilterType(ContentsFilterType depositFilterType) {
		filterLogic.get().setDepositFilterType(depositFilterType);
		serverUpdater.sendDataToServer(() -> NBTHelper.putEnumConstant(new NBTTagCompound(), DATA_CONTENTS_FILTER_TYPE, depositFilterType));
	}

	@Override
	public boolean handleMessage(NBTTagCompound data) {
		if (data.hasKey (DATA_CONTENTS_FILTER_TYPE)) {
			setFilterType(ContentsFilterType.fromName(data.getString(DATA_CONTENTS_FILTER_TYPE)));
		}
		return super.handleMessage(data);
	}

	public ContentsFilterType getFilterType() {
		return filterLogic.get().getFilterType();
	}
}
