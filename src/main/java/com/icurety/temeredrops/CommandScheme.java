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
            else {
                //try for specific material

                try {
                    String dropper = DropRegistry.findDropperFromDrop(argument);
                    sender.sendMessage(argument + " gets dropped by " + ChatColor.YELLOW +  dropper);
                }
                catch(MaterialNotFoundException e) {
                    sender.sendMessage("That item hasn't been assigned to anything");
                }
            }
            return true;
        }
        else {
            return false;
        }
    }
}
