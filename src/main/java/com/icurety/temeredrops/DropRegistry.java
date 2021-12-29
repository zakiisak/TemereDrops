package com.icurety.temeredrops;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import java.util.*;

public class DropRegistry {

    public static Random rng;

    private static Map<String, Material> blockInputOutputMaterials = new HashMap<String, Material>();
    private static Map<String, Material> mobInputOutputMaterials = new HashMap<String, Material>();
    private static Map<String, Material> mobExplosionInputOutputMaterials = new HashMap<String, Material>();

    public static void load(long seed) {
        rng = new Random(seed);
        blockInputOutputMaterials.clear();
        mobInputOutputMaterials.clear();
        mobExplosionInputOutputMaterials.clear();
        List<Material> allItems = getAllItems();
        assignRandomBlocksToItems(allItems);
        assignRandomEntitiesToItems(allItems);
        assignRandomEntityExplosionsToItems(allItems);

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
    private static List<Material> getAllItems() {
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
        blockInputOutputMaterials.put(blockMaterial.toString(), drop);
    }

    public static void assignMobDrop(EntityType entityType, Material drop) {
        mobInputOutputMaterials.put(entityType.toString(), drop);
    }

    public static void assignMobExplosionDrop(EntityType entityType, Material drop) {
        mobExplosionInputOutputMaterials.put(entityType.toString(), drop);
    }

    public static Material getDropMaterialForBlock(Material blockMaterial) {
        return blockInputOutputMaterials.get(blockMaterial.toString());
    }

    public static Material getDropMaterialForEntity(EntityType entityType) {
        return mobInputOutputMaterials.get(entityType.toString());
    }

    public static Material getDropMaterialForEntityExplosion(EntityType entityType) {
        return mobExplosionInputOutputMaterials.get(entityType.toString());
    }

    public static String findDropperFromDrop(String drop) throws MaterialNotFoundException {
        drop = drop.toUpperCase();
        if (Material.getMaterial(drop) == null)
            throw new MaterialNotFoundException();

        for (String key : blockInputOutputMaterials.keySet()) {
            Material blockDrop = blockInputOutputMaterials.get(key);
            if (blockDrop.toString().equals(drop))
                return key;
        }
        for (String key : mobInputOutputMaterials.keySet()) {
            Material mobDrop = mobInputOutputMaterials.get(key);
            if (mobDrop.toString().equals(drop))
                return key;
        }
        for (String key : mobExplosionInputOutputMaterials.keySet()) {
            Material mobDrop = mobExplosionInputOutputMaterials.get(key);
            if (mobDrop.toString().equals(drop))
                return key;
        }

        return null;
    }

    public static String getBlockDropScheme() {
        String result = "";
        for (String blockMaterial : blockInputOutputMaterials.keySet()) {
            result += ChatColor.GRAY + beautifyString(blockMaterial) + ChatColor.WHITE + " -> " + beautifyString(blockInputOutputMaterials.get(blockMaterial).toString()) + "\n";
        }
        return result;
    }

    public static String getMobDropScheme() {
        String result = "";
        for (String entityType : mobInputOutputMaterials.keySet()) {
            result += ChatColor.GRAY + beautifyString(entityType) + ChatColor.WHITE + " -> " + beautifyString(mobInputOutputMaterials.get(entityType).toString()) + "\n";
        }
        return result;
    }

    public static String getMobExplosionDropScheme() {
        String result = "";
        for (String entityType : mobExplosionInputOutputMaterials.keySet()) {
            result += ChatColor.GRAY + beautifyString(entityType) + ChatColor.WHITE + " -> " + beautifyString(mobExplosionInputOutputMaterials.get(entityType).toString()) + "\n";
        }
        return result;
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
                DropRegistry.assignMobDrop(entityType, randomItem);

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
                DropRegistry.assignMobExplosionDrop(entityType, randomItem);

                allItems.remove(index);
            } else break;
        }
    }


}
