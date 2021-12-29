package com.icurety.temeredrops;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandScheme implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length > 0)
        {
            String argument = args[0];
            if(argument.toLowerCase().equals("blocks")) {
                sender.sendMessage(ChatColor.YELLOW + "---------- Block Drops: ----------", DropRegistry.getBlockDropScheme());
            }
            else if(argument.toLowerCase().equals("mobs")) {
                sender.sendMessage(ChatColor.YELLOW + "---------- Mob Drops: ----------", DropRegistry.getMobDropScheme());
            }
            else if(argument.toLowerCase().equals("explosion")) {
                sender.sendMessage(ChatColor.YELLOW + "---------- Mob Explosion Drops: ----------", DropRegistry.getMobExplosionDropScheme());
            }
            else if(argument.toLowerCase().equals("all")) {
                sender.sendMessage(ChatColor.YELLOW + "---------- Block Drops: ----------", DropRegistry.getBlockDropScheme(),
                        ChatColor.YELLOW + "---------- Mob Drops: ----------", DropRegistry.getMobDropScheme(),
                        ChatColor.YELLOW + "---------- Mob Explosion Drops: ----------", DropRegistry.getMobExplosionDropScheme());
            }
            else if(argument.toLowerCase().equals("unassigned")) {
                sender.sendMessage(ChatColor.YELLOW + "-------- Unassigned Drops --------\n" + DropRegistry.getUnassignedScheme());
            }
            else {
                //try for specific material

                try {
                    String dropper = DropRegistry.findDropperFromDrop(argument);
                    if(dropper == null)
                    {
                        sender.sendMessage("That item hasn't been assigned to anything");
                    }
                    else sender.sendMessage(argument + " gets dropped by " + ChatColor.YELLOW +  dropper);
                }
                catch(MaterialNotFoundException e) {
                    sender.sendMessage(ChatColor.RED + "That item doesn't exist!");
                }
            }
            return true;
        }
        else {
            return false;
        }
    }
}
