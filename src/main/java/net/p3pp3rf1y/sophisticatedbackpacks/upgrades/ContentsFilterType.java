package net.p3pp3rf1y.sophisticatedbackpacks.upgrades;

import com.google.common.collect.ImmutableMap;
import net.p3pp3rf1y.sophisticatedbackpacks.polyfill.mc.util.IStringSerializable;

import java.util.Map;

public enum ContentsFilterType implements IStringSerializable {
	ALLOW("allow"),
	BLOCK("block"),
	BACKPACK("backpack");

	private final String name;

	ContentsFilterType(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	public ContentsFilterType next() {
		return VALUES[(ordinal() + 1) % VALUES.length];
	}

	private static final Map<String, ContentsFilterType> NAME_VALUES;
	private static final ContentsFilterType[] VALUES;

	static {
		ImmutableMap.Builder<String, ContentsFilterType> builder = new ImmutableMap.Builder<>();
		for (ContentsFilterType value : values()) {
			builder.put(value.getName(), value);
		}
		NAME_VALUES = builder.build();
		VALUES = values();
	}

	public static ContentsFilterType fromName(String name) {
		return NAME_VALUES.getOrDefault(name, BLOCK);
	}

}
