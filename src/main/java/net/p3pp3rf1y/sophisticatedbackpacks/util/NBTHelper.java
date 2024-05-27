package net.p3pp3rf1y.sophisticatedbackpacks.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
//import net.minecraft.util.IStringSerializable;
//import net.minecraft.util.UUIDCodec;
import com.mojang.util.UUIDTypeAdapter;
import net.minecraftforge.common.util.Constants;
import net.p3pp3rf1y.sophisticatedbackpacks.polyfill.mc.util.IStringSerializable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class NBTHelper {
	private NBTHelper() {}

	public static Optional<Integer> getInt(ItemStack stack, String key) {
		return getTagValue(stack, key, NBTTagCompound::getInteger);
	}

	public static Optional<Integer> getInt(NBTTagCompound tag, String key) {
		return getTagValue(tag, key, NBTTagCompound::getInteger);
	}

	private static <T> Optional<T> getTagValue(ItemStack stack, String key, BiFunction<NBTTagCompound, String, T> getValue) {
		return getTagValue(stack, "", key, getValue);
	}

	public static <T> Optional<T> getTagValue(ItemStack stack, String parentKey, String key, BiFunction<NBTTagCompound, String, T> getValue) {
		NBTTagCompound tag = stack.getTagCompound();

		if (tag == null) {
			return Optional.empty();
		}

		if (!parentKey.isEmpty()) {
			NBTBase parentTag = tag.getTag(parentKey);
			if (!(parentTag instanceof NBTTagCompound)) {
				return Optional.empty();
			}
			tag = (NBTTagCompound) parentTag;
		}

		return getTagValue(tag, key, getValue);
	}

	public static Optional<Boolean> getBoolean(NBTTagCompound tag, String key) {
		return getTagValue(tag, key, NBTTagCompound::getBoolean);
	}

	public static Optional<NBTTagCompound> getCompound(NBTTagCompound tag, String key) {
		return getTagValue(tag, key, NBTTagCompound::getCompoundTag);
	}

	private static <T> Optional<T> getTagValue(NBTTagCompound tag, String key, BiFunction<NBTTagCompound, String, T> getValue) {
		if (!tag.hasKey(key)) {
			return Optional.empty();
		}

		return Optional.of(getValue.apply(tag, key));
	}

	public static <E, C extends Collection<E>> Optional<C> getCollection(ItemStack stack, String parentKey, String tagName, int listType, Function<NBTBase, Optional<E>> getElement, Supplier<C> initCollection) {
		return getTagValue(stack, parentKey, tagName, (c,n) -> c.getTagList(n, listType)).map(listNbt -> {
			C ret = initCollection.get();
			listNbt.forEach(elementNbt -> getElement.apply(elementNbt).ifPresent(ret::add));
			return ret;
		});
	}

	public static Optional<NBTTagCompound> getCompound(ItemStack stack, String parentKey, String tagName) {
		return getTagValue(stack, parentKey, tagName, NBTTagCompound::getCompoundTag);
	}

	public static Optional<NBTTagCompound> getCompound(ItemStack stack, String tagName) {
		return getTagValue(stack, tagName, NBTTagCompound::getCompoundTag);
	}

	public static <T extends Enum<T>> Optional<T> getEnumConstant(ItemStack stack, String parentKey, String key, Function<String, T> deserialize) {
		return getTagValue(stack, parentKey, key, (t, k) -> deserialize.apply(t.getString(k)));
	}

	public static <T extends Enum<T>> Optional<T> getEnumConstant(ItemStack stack, String key, Function<String, T> deserialize) {
		return getTagValue(stack, key, (t, k) -> deserialize.apply(t.getString(k)));
	}

	public static Optional<Boolean> getBoolean(ItemStack stack, String parentKey, String key) {
		return getTagValue(stack, parentKey, key, NBTTagCompound::getBoolean);
	}

	public static Optional<Boolean> getBoolean(ItemStack stack, String key) {
		return getTagValue(stack, key, NBTTagCompound::getBoolean);
	}

	public static Optional<Long> getLong(ItemStack stack, String key) {
		return getTagValue(stack, key, NBTTagCompound::getLong);
	}

	public static Optional<UUID> getUniqueId(ItemStack stack, String key) {
		//noinspection ConstantConditions - contains check is run before this get so it won't be null
        NBTTagCompound stackTag = stack.getTagCompound();
        if (stackTag != null && stackTag.hasKey(key + "Most", Constants.NBT.TAG_LONG) && stackTag.hasKey(key + "Least", Constants.NBT.TAG_LONG)) {
            long most = stackTag.getLong(key + "Most");
            long least = stackTag.getLong(key + "Least");
            return Optional.of(new UUID(most, least));
        }
        return Optional.empty();
    }

	public static void setNBTTagCompound(ItemStack stack, String key, NBTTagCompound tag) {
		setNBTTagCompound(stack, "", key, tag);
	}

	public static void setNBTTagCompound(ItemStack stack, String parentKey, String key, NBTTagCompound tag) {
		if (parentKey.isEmpty()) {
			stack.getOrCreateTag().put(key, tag);
			return;
		}
		stack.getOrCreateTagElement(parentKey).put(key, tag);
	}

	public static void setBoolean(ItemStack stack, String parentKey, String key, boolean value) {
		if (parentKey.isEmpty()) {
			setBoolean(stack, key, value);
			return;
		}
		putBoolean(stack.getOrCreateTagElement(parentKey), key, value);
	}

	public static void setBoolean(ItemStack stack, String key, boolean value) {
		putBoolean(stack.getOrCreateTag(), key, value);
	}

	public static <T extends Enum<T> & IStringSerializable> void setEnumConstant(ItemStack stack, String parentKey, String key, T enumConstant) {
		if (parentKey.isEmpty()) {
			setEnumConstant(stack, key, enumConstant);
			return;
		}
		putEnumConstant(stack.getOrCreateTagElement(parentKey), key, enumConstant);
	}

	public static <T extends Enum<T> & IStringSerializable> void setEnumConstant(ItemStack stack, String key, T enumConstant) {
		putEnumConstant(stack.getOrCreateTag(), key, enumConstant);
	}

	public static NBTTagCompound putBoolean(NBTTagCompound tag, String key, boolean value) {
		tag.setBoolean(key, value);
		return tag;
	}

	public static NBTTagCompound putInt(NBTTagCompound tag, String key, int value) {
		tag.setInteger(key, value);
		return tag;
	}

	public static NBTTagCompound putString(NBTTagCompound tag, String key, String value) {
		tag.setString(key, value);
		return tag;
	}

	public static <T extends Enum<T> & IStringSerializable> NBTTagCompound putEnumConstant(NBTTagCompound tag, String key, T enumConstant) {
		tag.setString(key, enumConstant.getSerializedName());
		return tag;
	}

	public static void setLong(ItemStack stack, String key, long value) {
		stack.getOrCreateTag().putLong(key, value);
	}

	public static void setInteger(ItemStack stack, String key, int value) {
		stack.getOrCreateTag().putInt(key, value);
	}

	public static void setUniqueId(ItemStack stack, String key, UUID uuid) {
		stack.getOrCreateTag().putIntArray(key, UUIDCodec.uuidToIntArray(uuid));
	}

	public static void removeTag(ItemStack stack, String key) {
		if (stack.getTagCompound() == null) {
			return;
		}
		stack.getTagCompound().removeTag(key);
	}

	public static Optional<String> getString(ItemStack stack, String key) {
		return getTagValue(stack, key, NBTTagCompound::getString);
	}

	public static Optional<Float> getFloat(ItemStack stack, String key) {
		return getTagValue(stack, key, NBTTagCompound::getFloat);
	}

	public static <K, V> Optional<Map<K, V>> getMap(ItemStack stack, String key, Function<String, K> getKey, BiFunction<String, NBTBase, V> getValue) {
		Optional<NBTTagCompound> parentTag = getCompound(stack, key);

		if (!parentTag.isPresent()) {
			return Optional.empty();
		}
		NBTTagCompound tag = parentTag.get();
		return getMap(tag, getKey, (k, v) -> Optional.of(getValue.apply(k, v)));
	}

	public static <K, V> Optional<Map<K, V>> getMap(NBTTagCompound tag, Function<String, K> getKey, BiFunction<String, NBTBase, Optional<V>> getValue) {
		Map<K, V> map = new HashMap<>();

		for (String tagName : tag.getAllKeys()) {
			getValue.apply(tagName, tag.getTag(tagName)).ifPresent(value -> map.put(getKey.apply(tagName), value));
		}

		return Optional.of(map);
	}

	public static <K, V> void putMap(NBTTagCompound tag, String key, Map<K, V> map, Function<K, String> getStringKey, Function<V, NBTBase> getNbtValue) {
		NBTTagCompound mapNbt = new NBTTagCompound();
		for (Map.Entry<K, V> entry : map.entrySet()) {
			mapNbt.setTag(getStringKey.apply(entry.getKey()), getNbtValue.apply(entry.getValue()));
		}
		tag.setTag(key, mapNbt);
	}

	public static <K, V> void setMap(ItemStack stack, String key, Map<K, V> map, Function<K, String> getStringKey, Function<V, NBTBase> getNbtValue) {
		NBTTagCompound mapNbt = new NBTTagCompound();
		for (Map.Entry<K, V> entry : map.entrySet()) {
			mapNbt.setTag(getStringKey.apply(entry.getKey()), getNbtValue.apply(entry.getValue()));
		}
		stack.getOrCreateTag().put(key, mapNbt);
	}

	public static <T> void setList(ItemStack stack, String parentKey, String key, Collection<T> values, Function<T, NBTBase> getNbtValue) {
		NBTTagList list = new NBTTagList();
		values.forEach(v -> list.add(getNbtValue.apply(v)));
		if (parentKey.isEmpty()) {
			stack.getOrCreateTag().put(key, list);
		} else {
			stack.getOrCreateTagElement(parentKey).put(key, list);
		}
	}
}
