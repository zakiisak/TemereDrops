package com.icurety.temeredrops;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.*;

public class DropRegistry {

    public static Random rng;

    private static Map<String, Material> blockDrops = new HashMap<String, Material>();
    private static Map<String, Material> mobDrops = new HashMap<String, Material>();
    private static Map<String, Material> mobExplosionDrops = new HashMap<String, Material>();

    //Based on the sheep color, they will drop different things
    private static Map<String, Material> sheepSheerDrops = new HashMap<String, Material>();

    private static List<Material> remainingItems = new ArrayList<Material>();

    private static void clearDrops() {
        blockDrops.clear();
        mobDrops.clear();
        mobExplosionDrops.clear();
        sheepSheerDrops.clear();
    }

    public static void load(long seed) {
        rng = new Random(seed);
        clearDrops();
        List<Material> allItems = getAllItems();
        assignRandomBlocksToItems(allItems);
        assignRandomEntitiesToItems(allItems);
        assignRandomEntityExplosionsToItems(allItems);
        assignRandomSheepSheerDrops(allItems);
        remainingItems = allItems;

        TemereDrops.log("There are " + allItems.size() + " drops left to be assigned");
    }

    //Blocks that are in the world
    private static List<Material> getAllBlocks() {
        List<Material> allBlocks = new ArrayList<Material>();
        Material[] allMaterials = Material.values();

        for (Material material : allMaterials) {
            if (material.isBlock()) {
                allBlocks.add(material);
            }
        }
        return allBlocks;
    }

    //What is meant by items, is everything you can have in your inventory (both blocks and other items)
    public static List<Material> getAllItems() {
        List<Material> allItems = new ArrayList<Material>();
        Material[] allMaterials = Material.values();

        for (Material material : allMaterials) {
            if (material.isItem()) {
                allItems.add(material);
            }
        }
        return allItems;
    }


    public static void assignBlockDrop(Material blockMaterial, Material drop) {
        blockDrops.put(blockMaterial.toString(), drop);
    }

    public static void assignMobDrop(EntityType entityType, Material drop) {
        mobDrops.put(entityType.toString(), drop);
    }

    public static void assignMobExplosionDrop(EntityType entityType, Material drop) {
        mobExplosionDrops.put(entityType.toString(), drop);
    }

    public static void assignSheepSheerDrop(DyeColor color, Material drop) {
        sheepSheerDrops.put(color.name(), drop);
    }

    public static Material getDropMaterialForBlock(Material blockMaterial) {
        return blockDrops.get(blockMaterial.toString());
    }

    public static Material getDropMaterialForEntity(EntityType entityType) {
        return mobDrops.get(entityType.toString());
    }

    public static Material getDropMaterialForEntityExplosion(EntityType entityType) {
        return mobExplosionDrops.get(entityType.toString());
    }

    public static Material getDropMaterialForSheep(DyeColor color) {
        return sheepSheerDrops.get(color.name());
    }

    public static String findDropperFromDrop(String drop) throws MaterialNotFoundException {
        drop = drop.toUpperCase();
        if (Material.getMaterial(drop) == null)
            throw new MaterialNotFoundException();

        for (String key : blockDrops.keySet()) {
            Material blockDrop = blockDrops.get(key);
            if (blockDrop.toString().equals(drop))
                return key;
        }
        for (String key : mobDrops.keySet()) {
            Material mobDrop = mobDrops.get(key);
            if (mobDrop.toString().equals(drop))
                return key;
        }
        for (String key : mobExplosionDrops.keySet()) {
            Material mobDrop = mobExplosionDrops.get(key);
            if (mobDrop.toString().equals(drop))
                return key;
        }

        return null;
    }

    public static String getBlockDropScheme() {
        String result = "";
        for (String blockMaterial : blockDrops.keySet()) {
            result += ChatColor.GRAY + beautifyString(blockMaterial) + ChatColor.WHITE + " -> " + beautifyString(blockDrops.get(blockMaterial).toString()) + "\n";
        }
        return result;
    }

    public static String getMobDropScheme() {
        String result = "";
        for (String entityType : mobDrops.keySet()) {
            result += ChatColor.GRAY + beautifyString(entityType) + ChatColor.WHITE + " -> " + beautifyString(mobDrops.get(entityType).toString()) + "\n";
        }
        return result;
    }

    public static String getMobExplosionDropScheme() {
        String result = "";
        for (String entityType : mobExplosionDrops.keySet()) {
            result += ChatColor.GRAY + beautifyString(entityType) + ChatColor.WHITE + " -> " + beautifyString(mobExplosionDrops.get(entityType).toString()) + "\n";
        }
        return result;
    }

    public static String getUnassignedScheme() {
        String result = ChatColor.GRAY.toString();
        for (int i = 0; i < getUnassignedItems().size(); i++) {
            Material item = getUnassignedItems().get(i);
            result += (i % 2 == 0 ? ChatColor.WHITE : ChatColor.GRAY) + item.name().toLowerCase();
            if(i < getUnassignedItems().size() - 1)
                result += ", ";
            if(i % 2 == 0)
                result += "\n";
        }
        return result;
    }

    public static List<Material> getUnassignedItems() {
        return remainingItems;
    }

    private static String beautifyString(String name) {
        return name.toLowerCase();
    }


    private static void assignRandomBlocksToItems(List<Material> allItems) {

        List<Material> allBlocks = getAllBlocks();

        for (Material block : allBlocks) {

            if (allItems.size() > 0) {
                int index = rng.nextInt(allItems.size());
                Material randomItem = allItems.get(index);

                DropRegistry.assignBlockDrop(block, randomItem);

                allItems.remove(index);
            } else break;
        }

    }

    private static List<EntityType> getAllLivingEntityTypes() {
        EntityType[] allEntityTypes = EntityType.values();
        List<EntityType> livingTypes = new ArrayList<EntityType>();

        for (EntityType entityType : allEntityTypes) {
            if(entityType.isAlive())
                livingTypes.add(entityType);
        }
        return livingTypes;
    }

    private static void assignRandomEntitiesToItems(List<Material> allItems) {
        List<EntityType> allEntityTypes = getAllLivingEntityTypes();

        for (EntityType entityType : allEntityTypes) {

            if (allItems.size() > 0) {
                int index = rng.nextInt(allItems.size());
                Material randomItem = allItems.get(index);
                assignMobDrop(entityType, randomItem);

                allItems.remove(index);
            } else break;
        }
    }

    private static void assignRandomEntityExplosionsToItems(List<Material> allItems) {
        List<EntityType> allEntityTypes = getAllLivingEntityTypes();

        for (EntityType entityType : allEntityTypes) {

            if (allItems.size() > 0) {
                int index = rng.nextInt(allItems.size());
                Material randomItem = allItems.get(index);
                assignMobExplosionDrop(entityType, randomItem);

                allItems.remove(index);
            } else break;
        }
    }

    private static void assignRandomSheepSheerDrops(List<Material> allItems) {
        DyeColor[] colors = DyeColor.values();

        for (DyeColor color : colors) {

            if (allItems.size() > 0) {
                int index = rng.nextInt(allItems.size());
                Material randomItem = allItems.get(index);
                assignSheepSheerDrop(color, randomItem);
                allItems.remove(index);
            } else break;
        }
    }


}
