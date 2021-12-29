package com.icurety.temeredrops;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.inventory.ItemStack;
import sun.text.resources.ext.FormatData_ga;

import java.util.*;

public class DropEventListener implements Listener {
    private Random rng = new Random();

    //Map of coordinate/material type so that we know that the type of the destroyed block was, so we know what to drop
    private Map<String, Material> breakedBlocksCoordinateCache = new HashMap<String, Material>();

    private String getBlockLocationKey(Location location) {
        return location.getWorld().getName() + "," + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Location location = event.getBlock().getLocation();
        String key = this.getBlockLocationKey(location);
        breakedBlocksCoordinateCache.put(key, event.getBlock().getType());
    }

    @EventHandler
    public void onBlockDropItem(BlockDropItemEvent event) {
        if(event.getItems().size() > 0)
        {
            Item dropEntity = event.getItems().get(0);
            int originalAmount = dropEntity.getItemStack().getAmount();

            //will remove any overheading items in the list.
            //We only want a single drop entity
            for(int i = event.getItems().size() - 1; i > 0; i--) {
                event.getItems().remove(i);
            }

            String key = getBlockLocationKey(event.getBlock().getLocation());
            Material block = this.breakedBlocksCoordinateCache.get(key);
            this.breakedBlocksCoordinateCache.remove(key);
            Material assignedDrop = DropRegistry.getDropMaterialForBlock(block);
            dropEntity.getItemStack().setType(assignedDrop);
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Material assignedDrop = DropRegistry.getDropMaterialForEntity(event.getEntityType());
        if(assignedDrop != null) {
            int amount = 1;
            if (event.getDrops().size() > 0) {
                amount = event.getDrops().get(0).getAmount();
            }
            event.getDrops().clear();



            event.getDrops().add(new ItemStack(assignedDrop, amount));
        }
        //If the entity got exploded, it should drop something more as defined in the input ouput explosion list
        if (event.getEntity().getLastDamageCause() != null) {
            if (event.getEntity().getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
                int randomAmount = 1 + rng.nextInt(3);
                Material assignedExplosionDrop = DropRegistry.getDropMaterialForEntityExplosion(event.getEntityType());
                if (assignedExplosionDrop != null) {
                    event.getDrops().add(new ItemStack(assignedExplosionDrop, randomAmount));
                }
            }
        }
    }
}
