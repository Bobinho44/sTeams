package fr.bobinho.steams.utils.item;

import org.apache.commons.lang.Validate;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BItemBuilder {

    /**
     * Fields
     */
    private ItemStack item;
    private ItemMeta meta;

    /**
     * Creates the practice item builder
     *
     * @param item the item stack
     */
    public BItemBuilder(@Nonnull ItemStack item) {
        Validate.notNull(item, "item is null");

        //Configures the bukkit item stack
        this.item = item;
        this.meta = this.item.getItemMeta();
    }

    /**
     * Creates the practice item builder
     *
     * @param material the material
     */
    public BItemBuilder(@Nonnull Material material) {
        Validate.notNull(material, "material is null");

        //Configures the bukkit item stack
        this.item = new ItemStack(material);
        this.meta = this.item.getItemMeta();
    }

    /**
     * Creates the practice item builder
     *
     * @param material the material
     * @param amount   the amount
     */
    public BItemBuilder(@Nonnull Material material, int amount) {
        Validate.notNull(material, "material is null");

        //Configures the bukkit item stack
        this.item = new ItemStack(material, amount);
        this.meta = this.item.getItemMeta();
    }

    /**
     * Gets the current item stack
     *
     * @return the item stack
     */
    @Nonnull
    public ItemStack getItem() {
        return item;
    }

    /**
     * Sets the item stack
     *
     * @param item the item stack
     * @return the practice item builder
     */
    @Nonnull
    public BItemBuilder item(@Nonnull ItemStack item) {
        Validate.notNull(item, "item is null");

        this.item = item;
        return this;
    }

    /**
     * Sets the item material
     *
     * @param material the material
     * @return the practice item Builder
     */
    @Nonnull
    public BItemBuilder material(@Nonnull Material material) {
        Validate.notNull(material, "material is null");
        item.setType(material);
        return this;
    }

    /**
     * Sets the item durability
     *
     * @param durability the durability
     * @return the practice item builder
     */
    @Nonnull
    public BItemBuilder durability(int durability) {

        //Checks if the item is not damageable
        if (!(this.meta instanceof Damageable)) {
            return this;
        }

        //Sets item durability.
        ((Damageable) meta).setDamage(durability);
        item.setItemMeta(meta);
        return this;
    }

    /**
     * Sets the item amount
     *
     * @param amount the amount
     * @return the practice item builder
     */
    @Nonnull
    public BItemBuilder amount(int amount) {
        item.setAmount(amount);
        return this;
    }

    /**
     * Sets the item name
     *
     * @param name the name
     * @return the practice item builder
     */
    @Nonnull
    public BItemBuilder name(@Nonnull String name) {
        Validate.notNull(name, "name is null");

        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return this;
    }

    /**
     * Sets the item lore
     *
     * @param lore the lore
     * @return the practice item builder
     */
    @Nonnull
    public BItemBuilder setLore(@Nonnull List<String> lore) {
        Validate.notNull(lore, "lore is null");

        //Declares the lore
        List<String> lore_array = new ArrayList<>();
        for (@Nonnull String text : lore) {
            lore_array.add(text);
        }

        //Sets the item lore
        meta.setLore(lore_array);
        item.setItemMeta(meta);
        return this;
    }

    /**
     * Adds new lines to the lore
     *
     * @param lore the lore
     * @return the practice item builder
     */
    @Nonnull
    public BItemBuilder lore(@Nonnull List<String> lore) {
        Validate.notNull(lore, "lore is null");

        //Declare the lore array.
        List<String> lore_array = meta.getLore();

        //If the lore is null or empty, create empty list.
        if (lore_array == null) {
            lore_array = new ArrayList<>();
        }

        //Adds new lines to the existed lore
        for (String text : lore) {
            lore_array.add(text);
        }

        //Sets the item lore
        meta.setLore(lore_array);
        item.setItemMeta(meta);
        return this;
    }

    /**
     * Adds new lines to the lore
     *
     * @param lore the lore
     * @return the practice item builder
     */
    @Nonnull
    public BItemBuilder lore(@Nonnull String... lore) {
        Validate.notNull(lore, "lore is null");

        lore(new ArrayList<>(Arrays.asList(lore)));
        return this;
    }

    /**
     * Adds new lines to the lore
     *
     * @param color the color
     * @param lore  the lore
     * @return the practice item builder
     */
    @Nonnull
    public BItemBuilder lore(@Nonnull String color, @Nonnull List<String> lore) {
        Validate.notNull(color, "color is null");
        Validate.notNull(lore, "lore is null");

        //Declares the colorized lore
        List<String> colorizedLore = new ArrayList<>();
        lore.forEach(text -> colorizedLore.add(color + text));

        //Sets the item lore
        this.lore(colorizedLore);
        return this;
    }

    /**
     * Adds a new enchantment
     *
     * @param enchantment the enchantment
     * @param level       the level
     * @return the practice item builder
     */
    @Nonnull
    public BItemBuilder enchantment(@Nonnull Enchantment enchantment, int level) {
        Validate.notNull(enchantment, "enchantment is null");

        item.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    /**
     * Sets the item unbreakable
     *
     * @param unbreakable the unbreakable statue
     * @return the practice item builder
     */
    @Nonnull
    public BItemBuilder unbreakable(boolean unbreakable) {
        meta.setUnbreakable(unbreakable);
        item.setItemMeta(meta);
        return this;
    }

    /**
     * Adds an item flag
     *
     * @param flag the flag
     * @return the practice item builder
     */
    @Nonnull
    public BItemBuilder flag(@Nonnull ItemFlag... flag) {
        Validate.notNull(flag, "flag is null");

        meta.addItemFlags(flag);
        item.setItemMeta(meta);
        return this;
    }

    /**
     * Sets the item color
     *
     * @param red   the red
     * @param green the green
     * @param blue  the blue
     * @return the practice item builder
     */
    @Nonnull
    public BItemBuilder color(int red, int green, int blue) {

        //Checks if item is colorable
        if (!(meta instanceof LeatherArmorMeta)) {
            return this;
        }

        //Colorizes the item
        ((LeatherArmorMeta) meta).setColor(Color.fromRGB(red, green, blue));
        item.setItemMeta(meta);
        return this;
    }

    /**
     * Sets the item glow
     *
     * @return the practice item builder
     */
    @Nonnull
    public BItemBuilder glow() {
        flag(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
        enchantment(Enchantment.DURABILITY, 1);
        return this;
    }

    /**
     * Sets the item glow
     *
     * @return the practice item builder
     */
    @Nonnull
    public BItemBuilder setGlow(boolean glow) {
        if (!glow) {
            return this;
        }
        glow();
        return this;
    }

    /**
     * Builds the item
     *
     * @return the item stack
     */
    @Nonnull
    public ItemStack build() {
        return item;
    }

}