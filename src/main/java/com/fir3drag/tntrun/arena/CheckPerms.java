package com.fir3drag.tntrun.arena;

import org.bukkit.command.CommandSender;

public class CheckPerms {
    public boolean check(CommandSender sender, String perm){
        if (!sender.hasPermission(perm) && !sender.hasPermission("tntrun.admin")){
            sender.sendMessage("You don't have permission to use this command.");
            return false;
        }
        return true;
    }
}
