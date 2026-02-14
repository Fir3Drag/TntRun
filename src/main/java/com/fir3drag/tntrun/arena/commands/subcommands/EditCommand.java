package com.fir3drag.tntrun.arena.commands.subcommands;

import com.fir3drag.tntrun.TntRun;
import com.fir3drag.tntrun.arena.commands.interfaces.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EditCommand implements SubCommand {
    private final TntRun plugin;

    public EditCommand(TntRun plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender commandSender, Command command, String s, String[] args) {
        if (!this.plugin.checkPerms.check(commandSender, "tntrun.editArena")){
            return;
        }

        List<String> arenas = this.plugin.data.getDataConfig().getStringList("Arenas");

        if (commandSender instanceof Player){
            Player player = (Player) commandSender;

            if (args.length == 0){
                player.sendMessage(ChatColor.RED + "/tntrun editArena [arena]");
                return;
            }
            World currentWorld = player.getWorld();
            String currentWorldName = player.getWorld().getName();
            String arenaWorldName = args[0];
            World arena = Bukkit.getWorld(arenaWorldName);

            if (arena == null){  // checks the world exists
                player.sendMessage(ChatColor.RED + "Arena " + arenaWorldName + " does not exist");
                return;
            }

            if (arenas.contains(arenaWorldName)){  // if the arena exists and the player is not in an arena, allow the player to edit and tp them into the world
                if (arenas.contains(currentWorldName)) {  // checks if the player is already in an arena
                    this.plugin.changePlayerMaps.removePlayerAll(currentWorldName, player);
                    this.plugin.countdown.checkForCancel(currentWorld);
                    this.plugin.checkForWinner.check(currentWorld, player);
                }
                this.plugin.changePlayerMaps.addPlayerToEditing(arenaWorldName, player);  // add to the new editing list of the arena their going to

                // moves player into arena
                player.teleport(arena.getSpawnLocation());
                player.setGameMode(GameMode.CREATIVE);

                commandSender.sendMessage("Editing arena " + arenaWorldName);
                commandSender.sendMessage("Type /tntrun leave to return to main world");
            }
            else {
                commandSender.sendMessage(ChatColor.RED + "You are already editing an arena, /tntrun leave");
            }
        }
    }

    @Override
    public List<String> tabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length == 1){
            List<String> allCompletions = this.plugin.data.getDataConfig().getStringList("Arenas");
            List<String> completions = new ArrayList<>();

            for (String completion: allCompletions){ // dynamically updates the tab list depending on whats written
                if (completion.startsWith(args[0]))
                {
                    completions.add(completion);
                }
            }
            return completions;
        }

        return Collections.emptyList();
    }
}
