package com.fir3drag.tntrun.arena.commands.subcommands;

import com.fir3drag.tntrun.TntRun;
import com.fir3drag.tntrun.arena.VoidWorldGenerator;
import com.fir3drag.tntrun.arena.commands.interfaces.SubCommand;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class CreateCommand implements SubCommand {
    private final TntRun plugin;

    public CreateCommand(TntRun plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!this.plugin.permController.check(commandSender, "tntrun.create")){
            return;
        }

        List<String> arenas = this.plugin.data.getDataConfig().getStringList("arenas");
        List<String> disabledArenas = this.plugin.data.getDataConfig().getStringList("disabledArenas");

        if (args.length != 1){
            commandSender.sendMessage(ChatColor.RED + "/tntrun create [arena]");
            return;
        }
        String arenaName = args[0];

        if (args[0].equalsIgnoreCase("lobby")){
            commandSender.sendMessage(ChatColor.RED + "You cannot name an arena 'lobby'.");
            return;
        }

        if (!Bukkit.getWorlds().toString().contains(arenaName) && !arenas.contains(arenaName)){ // create world if it doesn't exist
            WorldCreator creator = new WorldCreator(arenaName);
            creator.generator(new VoidWorldGenerator()); // makes a void world
            creator.createWorld();

            World arena = Bukkit.getWorld(arenaName);

            arena.getBlockAt(0, 100, 0).setType(Material.BEDROCK);  // gives a block for player to stand on
            arena.setSpawnLocation(0, 100, 0);

            // set game rules
            arena.setGameRuleValue("doDaylightCycle", "false");  // disable dailyLight cycle
            arena.setGameRuleValue("doFireTick", "false");
            arena.setGameRuleValue("doMobSpawning", "false");
            arena.setGameRuleValue("mobGriefing", "false");
            arena.setTime(1000);
            arena.setDifficulty(Difficulty.PEACEFUL);

            // update arenas config
            arenas.add(arenaName);
            disabledArenas.add(arenaName);  // arenas start disabled
            this.plugin.data.getDataConfig().set("arenas", arenas);
            this.plugin.data.getDataConfig().set("disabledArenas", disabledArenas);
            this.plugin.data.saveConfig();

            // update map values
            this.plugin.loadDefaultArenaValues(arena);

            commandSender.sendMessage(ChatColor.YELLOW + "Successfully created arena: '" + arenaName + "'.");
            commandSender.sendMessage(ChatColor.YELLOW + "Arena is disabled for editing. use /tntrun enable " + arenaName +
                    " so that players can join.");

            if (commandSender instanceof Player){
                Player player = (Player) commandSender;

                this.plugin.playerMapsController.addToEditing(arena.getName(), player);
                player.teleport(arena.getSpawnLocation());
                player.setGameMode(GameMode.CREATIVE);
                commandSender.sendMessage(ChatColor.YELLOW + "Type /leave to return to main world.");
            }
        }
        else {
            commandSender.sendMessage(ChatColor.RED + "Arena '" + arenaName + "' already exists.");
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        return Collections.emptyList();
    }
}

