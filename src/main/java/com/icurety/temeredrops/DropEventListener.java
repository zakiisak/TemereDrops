package com.icurety.temeredrops;

import jdk.nashorn.internal.ir.Block;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Crops;
import org.bukkit.util.Vector;
import sun.text.resources.ext.FormatData_ga;

import java.util.*;

public class DropEventListener implements Listener {
    private Random rng = new Random();

    private List<Material> allItemsRegistry = DropRegistry.getAllItems();

    //Map of coordinate/material type so that we know that the type of the destroyed block was, so we know what to drop
    private Map<String, Material> breakedBlocksCoordinateCache = new HashMap<String, Material>();

    private String getBlockLocationKey(Location location) {
        return location.getWorld().getName() + "," + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        //If the user is using a silk touch pickaxe, the block should drop the actual block
        if(event.getPlayer().getGameMode() != GameMode.CREATIVE && event.getPlayer().getInventory().getItemInMainHand() != null && event.getPlayer().getInventory().getItemInMainHand().containsEnchantment(Enchantment.SILK_TOUCH)) {
            event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), new ItemStack(event.getBlock().getType()));
            event.setDropItems(false);
        }
        else {
            //If the block doesn't have any drops, we will just spawn the drop item through here
            if(event.getPlayer().getGameMode() != GameMode.CREATIVE && event.getBlock().getDrops().size() == 0) {
                Material assignedDrop = DropRegistry.getDropMaterialForBlock(event.getBlock().getType());
                if(assignedDrop != null)
                    event.getBlock().getWorld().dropItem(event.getBlock().getLocation().add(0.5, 0.5, 0.5), new ItemStack(assignedDrop));
            }
            Location location = event.getBlock().getLocation();
            String key = this.getBlockLocationKey(location);
            breakedBlocksCoordinateCache.put(key, event.getBlock().getType());

        }
    }

    @EventHandler
    public void onBlockDropItem(BlockDropItemEvent event) {
        if(event.getItems().size() > 0)
        {
            Item dropEntity = event.getItems().get(0);
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
        if (assignedDrop != null) {
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

    @EventHandler
    public void onEntityDropItem(EntityDropItemEvent event) {
        if(event.getEntity() instanceof Sheep) {
            Sheep sheep = (Sheep) event.getEntity();
            DyeColor color = sheep.getColor();
            Material assignedDrop = DropRegistry.getDropMaterialForSheep(color);
            if(assignedDrop != null) {
                event.getItemDrop().getItemStack().setType(assignedDrop);
            }
        }
        //Chickens drop completely random items instead of eggs
        else if(event.getEntity() instanceof Chicken && event.getItemDrop().getItemStack().getType().equals(Material.EGG))
        {
            event.getItemDrop().getItemStack().setType(allItemsRegistry.get(rng.nextInt(allItemsRegistry.size())));
        }
    }

    private void fireTNTTowardsPlayer(Location location, Player player) {
        TNTPrimed tnt =  (TNTPrimed) player.getWorld().spawnEntity(location, EntityType.PRIMED_TNT);
        tnt.setFuseTicks(10 + rng.nextInt(30));
        tnt.setGravity(true);
        tnt.setVelocity(player.getLocation().toVector().add(new Vector(0, 3, 0)).subtract(tnt.getLocation().toVector()).divide(new Vector(10, 10, 10)));
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        if(event.getCaught() != null) {

            //20% change tnt entity gets thrown at player
            if(rng.nextInt(5) == 2) {
                fireTNTTowardsPlayer(event.getHook().getLocation().add(0, 2, 0), event.getPlayer());
            }
            //otherwise loot unassigned items
            else if(event.getCaught() instanceof Item) {
                Item item = (Item) event.getCaught();
                if(DropRegistry.getUnassignedItems().size() > 0 )
                {
                    Material randomItem = DropRegistry.getUnassignedItems().get(rng.nextInt(DropRegistry.getUnassignedItems().size()));
                    item.getItemStack().setType(randomItem);
                }
            }
        }
    }

}
