package net.p3pp3rf1y.sophisticatedbackpacks.polyfill.mc.util;

import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;


public enum DyeColor {

    BLACK,
    RED,
    GREEN,
    BROWN,
    BLUE,
    PURPLE,
    CYAN,
    SILVER,
    GRAY,
    PINK,
    LIME,
    YELLOW,
    LIGHT_BLUE,
    MAGENTA,
    ORANGE,
    WHITE;

    public static final String[] DYE_ORE_NAMES = { "dyeBlack", "dyeRed", "dyeGreen", "dyeBrown", "dyeBlue", "dyePurple",
        "dyeCyan", "dyeLightGray", "dyeGray", "dyePink", "dyeLime", "dyeYellow", "dyeLightBlue", "dyeMagenta",
        "dyeOrange", "dyeWhite" };

    public static final String[] DYE_ORE_UNLOCAL_NAMES = {

        "item.fireworksCharge.black", "item.fireworksCharge.red", "item.fireworksCharge.green",
        "item.fireworksCharge.brown", "item.fireworksCharge.blue", "item.fireworksCharge.purple",
        "item.fireworksCharge.cyan", "item.fireworksCharge.silver", "item.fireworksCharge.gray",
        "item.fireworksCharge.pink", "item.fireworksCharge.lime", "item.fireworksCharge.yellow",
        "item.fireworksCharge.lightBlue", "item.fireworksCharge.magenta", "item.fireworksCharge.orange",
        "item.fireworksCharge.white"

    };

    public static DyeColor getNext(DyeColor col) {
        int ord = col.ordinal() + 1;
        if (ord >= DyeColor.values().length) {
            ord = 0;
        }
        return DyeColor.values()[ord];
    }

    public static DyeColor getColorFromDye(ItemStack dye) {
        if (dye == null) {
            return null;
        }
        int[] oreIDs = OreDictionary.getOreIDs(dye);
        for (int i = 0; i < DYE_ORE_NAMES.length; i++) {
            int dyeID = OreDictionary.getOreID(DYE_ORE_NAMES[i]);
            for (int oreId : oreIDs) {
                if (dyeID == oreId) {
                    return DyeColor.values()[i];
                }
            }
        }
        return null;
    }

    public static DyeColor fromIndex(int index) {
        return DyeColor.values()[index];
    }

    private DyeColor() {}

    public int getColor() {
        return ItemDye.field_150922_c[ordinal()];
    }

    public String getName() {
        return ItemDye.field_150921_b[ordinal()];
    }

    @Override
    public String toString() {
        return getName();
    }

}
