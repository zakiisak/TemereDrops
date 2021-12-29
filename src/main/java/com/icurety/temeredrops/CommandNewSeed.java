package com.icurety.temeredrops;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CommandNewSeed implements CommandExecutor {

    private void writeLongToFile(File file, long data) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(Long.toString(data));
        writer.close();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender.isOp())
        {
            try {
                long newSeed = System.currentTimeMillis();

                if(TemereDrops.instance.getDataFolder().exists() == false)
                    TemereDrops.instance.getDataFolder().mkdirs();

                writeLongToFile(new File(TemereDrops.instance.getDataFolder(), "seed.txt"), newSeed);
                DropRegistry.load(newSeed);
                sender.sendMessage("All drops have been reassigned!");
                return true;
            }
            catch(IOException e) {
                sender.sendMessage("Couldn't save the new seed! " + e.getLocalizedMessage());
            }
            return false;
        }
        sender.sendMessage("You do not have permissions to use this command.");
        return false;
    }
}
