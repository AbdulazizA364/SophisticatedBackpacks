package net.p3pp3rf1y.sophisticatedbackpacks.settings.nosort;


import net.minecraft.nbt.NBTTagCompound;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.SettingsContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.polyfill.mc.util.DyeColor;
import net.p3pp3rf1y.sophisticatedbackpacks.settings.SettingsContainerBase;

public class NoSortSettingsContainer extends SettingsContainerBase<NoSortSettingsCategory> {

	private static final String ACTION_TAG = "action";
	private static final String SELECT_ALL_ACTION = "selectAll";
	private static final String UNSELECT_ALL_ACTION = "unselectAll";
	private static final String UNSELECT_SLOT_TAG = "unselectSlot";
	private static final String SELECT_SLOT_TAG = "selectSlot";
	private static final String COLOR_TAG = "color";

	public NoSortSettingsContainer(SettingsContainer settingsContainer, String categoryName, NoSortSettingsCategory category) {
		super(settingsContainer, categoryName, category);
	}

	@Override
	public void handleMessage(NBTTagCompound data) {
		if (data.hasKey (ACTION_TAG)) {
			switch (data.getString(ACTION_TAG)) {
				case SELECT_ALL_ACTION:
					selectAllSlots();
					break;
				case UNSELECT_ALL_ACTION:
					unselectAllSlots();
					break;
				default:
			}
		} else if (data.hasKey (SELECT_SLOT_TAG)) {
			selectSlot(data.getInteger(SELECT_SLOT_TAG));
		} else if (data.hasKey (UNSELECT_SLOT_TAG)) {
			unselectSlot(data.getInteger(UNSELECT_SLOT_TAG));
		} else if (data.hasKey (COLOR_TAG)) {
			setColor(DyeColor.fromIndex(data.getInteger(COLOR_TAG)));
		}
	}

	public void unselectSlot(int slotNumber) {
		if (!isSlotSelected(slotNumber)) {
			return;
		}
		if (isServer()) {
			getCategory().unselectSlot(slotNumber);
		} else {
			sendIntToServer(UNSELECT_SLOT_TAG, slotNumber);
		}
	}

	public void selectSlot(int slotNumber) {
		if (isSlotSelected(slotNumber)) {
			return;
		}
		if (isServer()) {
			getCategory().selectSlot(slotNumber);
		} else {
			sendIntToServer(SELECT_SLOT_TAG, slotNumber);
		}
	}

	public void unselectAllSlots() {
		if (isServer()) {
			getCategory().unselectAllSlots();
		} else {
			sendStringToServer(ACTION_TAG, UNSELECT_ALL_ACTION);
		}
	}

	public void selectAllSlots() {
		if (isServer()) {
			getCategory().selectSlots(0, getSettingsContainer().getNumberOfSlots());
		} else {
			sendStringToServer(ACTION_TAG, SELECT_ALL_ACTION);
		}
	}

	public void setColor(DyeColor color) {
		if (isServer()) {
			getCategory().setColor(color);
		} else {
			sendIntToServer(COLOR_TAG, color.getColor());
		}
	}

	public boolean isSlotSelected(int slotNumber) {
		return getCategory().isSlotSelected(slotNumber);
	}

	public DyeColor getColor() {
		return getCategory().getColor();
	}
}
