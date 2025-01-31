package net.p3pp3rf1y.sophisticatedbackpacks.settings.backpack;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackSettingsManager;
import net.p3pp3rf1y.sophisticatedbackpacks.common.gui.SettingsContainer;
import net.p3pp3rf1y.sophisticatedbackpacks.settings.SettingsContainerBase;

public class BackpackSettingsContainer extends SettingsContainerBase<BackpackSettingsCategory> {
	private static final String CONTEXT_TAG = "context";
	private Context context = Context.PLAYER;

	public BackpackSettingsContainer(SettingsContainer settingsContainer, String categoryName, BackpackSettingsCategory category) {
		super(settingsContainer, categoryName, category);
	}

	@Override
	public void handleMessage(NBTTagCompound data) {
		if (data.hasKey (CONTEXT_TAG)) {
			context = Context.fromId(data.getInteger(CONTEXT_TAG));
		} else {
			for (String tagName : data.func_150296_c()) {
				BackpackSettingsManager.getBackpackSetting(tagName).ifPresent(setting -> setSettingValue(getPlayer(), setting, data));
			}
		}
	}

	public void toggleContext() {
		context = context == Context.PLAYER ? Context.BACKPACK : Context.PLAYER;
		sendIntToServer(CONTEXT_TAG, context.getId());
	}

	public Context getContext() {
		return context;
	}

	private EntityPlayer getPlayer() {
		return getSettingsContainer().getPlayer();
	}

	public void toggleShiftClickIntoOpenTab() {
		toggleBooleanSetting(getPlayer(), BackpackSettingsManager.SHIFT_CLICK_INTO_OPEN_TAB_FIRST);
	}

	public boolean shouldShiftClickIntoOpenTab() {
		return getSettingValue(BackpackSettingsManager.SHIFT_CLICK_INTO_OPEN_TAB_FIRST);
	}

	public void toggleKeepTabOpen() {
		toggleBooleanSetting(getPlayer(), BackpackSettingsManager.KEEP_TAB_OPEN);
	}

	public boolean shouldKeepTabOpen() {
		return getSettingValue(BackpackSettingsManager.KEEP_TAB_OPEN);
	}

	private <T> T getSettingValue(BackpackSettingsManager.BackpackSetting<T> setting) {
		if (context == Context.PLAYER) {
			return BackpackSettingsManager.getPlayerSettingOrDefault(getPlayer(), setting);
		} else {
			return BackpackSettingsManager.getBackpackSettingValue(getPlayer(), getCategory(), setting);
		}
	}

	private <T> void setSettingValue(EntityPlayer player, BackpackSettingsManager.BackpackSetting<T> setting, NBTTagCompound data) {
		setting.getValue(data).ifPresent(value -> {
			if (context == Context.PLAYER) {
				BackpackSettingsManager.setPlayerSetting(player, setting, value);
			} else {
				BackpackSettingsManager.setBackpackSetting(player, getCategory(), setting, value);
			}
		});
	}

	private void toggleBooleanSetting(EntityPlayer player, BackpackSettingsManager.BackpackSetting<Boolean> setting) {
		if (context == Context.PLAYER) {
			boolean value = !BackpackSettingsManager.getPlayerSettingOrDefault(player, setting);
			BackpackSettingsManager.setPlayerSetting(player, setting, value);
			sendSettingValueToServer(setting, value);
		} else {
			boolean value = !BackpackSettingsManager.getBackpackSettingValue(player, getCategory(), setting);
			sendSettingValueToServer(setting, value);
		}
	}

	private void sendSettingValueToServer(BackpackSettingsManager.BackpackSetting<Boolean> setting, boolean value) {
        NBTTagCompound data = new NBTTagCompound();
		setting.setValue(data, value);
		sendDataToServer(() -> data);
	}
}
