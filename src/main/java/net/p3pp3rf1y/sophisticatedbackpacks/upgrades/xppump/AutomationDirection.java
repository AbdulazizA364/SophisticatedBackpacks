package net.p3pp3rf1y.sophisticatedbackpacks.upgrades.xppump;

import com.google.common.collect.ImmutableMap;
import net.p3pp3rf1y.sophisticatedbackpacks.polyfill.mc.util.IStringSerializable;
//import net.minecraft.util.IStringSerializable;

import java.util.Map;

public enum AutomationDirection implements IStringSerializable {
	INPUT("input"),
	OUTPUT("output"),
	OFF("off");

	private final String name;

	AutomationDirection(String name) {this.name = name;}

	@Override
	public String getName () {
		return name;
	}

	public AutomationDirection next() {
		return VALUES[(ordinal() + 1) % VALUES.length];
	}

	private static final Map<String, AutomationDirection> NAME_VALUES;
	private static final AutomationDirection[] VALUES;

	static {
		ImmutableMap.Builder<String, AutomationDirection> builder = new ImmutableMap.Builder<>();
		for (AutomationDirection value : AutomationDirection.values()) {
			builder.put(value.getName(), value);
		}
		NAME_VALUES = builder.build();
		VALUES = values();
	}

	public static AutomationDirection fromName(String name) {
		return NAME_VALUES.getOrDefault(name, INPUT);
	}
}
