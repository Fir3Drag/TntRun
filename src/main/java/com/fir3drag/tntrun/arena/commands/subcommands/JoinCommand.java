package com.fir3drag.tntrun.arena.commands.subcommands;

import com.fir3drag.tntrun.TntRun;
import com.fir3drag.tntrun.arena.commands.interfaces.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class JoinCommand implements SubCommand {
    private final TntRun plugin;

    public JoinCommand(TntRun plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!this.plugin.permController.check(commandSender, "tntrun.join")){
            return;
        }
        if (commandSender instanceof Player){
            Player player = (Player) commandSender;
            this.plugin.lobbyController.openJoiningArenaInventory(player);
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        return Collections.emptyList();
    }
}
