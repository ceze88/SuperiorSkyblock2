package com.bgsoftware.superiorskyblock.commands.command;

import com.bgsoftware.superiorskyblock.Locale;
import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.island.IslandPermission;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.commands.ICommand;
import com.bgsoftware.superiorskyblock.wrappers.SSuperiorPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class CmdVisit implements ICommand {

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("visit");
    }

    @Override
    public String getPermission() {
        return "superior.island.visit";
    }

    @Override
    public String getUsage() {
        return "island visit <player-name/island-name>";
    }

    @Override
    public String getDescription() {
        return Locale.COMMAND_DESCRIPTION_VISIT.getMessage();
    }

    @Override
    public int getMinArgs() {
        return 2;
    }

    @Override
    public int getMaxArgs() {
        return 2;
    }

    @Override
    public boolean canBeExecutedByConsole() {
        return false;
    }

    @Override
    public void execute(SuperiorSkyblockPlugin plugin, CommandSender sender, String[] args) {
        SuperiorPlayer superiorPlayer = SSuperiorPlayer.of(sender);
        SuperiorPlayer targetPlayer = SSuperiorPlayer.of(args[1]);

        Island targetIsland = targetPlayer == null ? plugin.getGrid().getIsland(args[1]) : targetPlayer.getIsland();

        if(targetIsland == null){
            if(targetPlayer == null)
                Locale.INVALID_ISLAND_OTHER_NAME.send(sender, args[1]);
            else
                Locale.INVALID_ISLAND_OTHER.send(sender, targetPlayer.getName());
            return;
        }

        Location visitLocation = targetIsland.getWarpLocation("visit");

        if(visitLocation == null){
            Locale.INVALID_VISIT_LOCATION.send(sender);

            if(!superiorPlayer.hasBypassModeEnabled())
                return;

            visitLocation = targetIsland.getTeleportLocation();
            Locale.INVALID_VISIT_LOCATION_BYPASS.send(sender);
        }

        if(targetIsland.isLocked() && !targetIsland.hasPermission(superiorPlayer, IslandPermission.CLOSE_BYPASS)){
            Locale.NO_CLOSE_BYPASS.send(sender);
            return;
        }

        superiorPlayer.asPlayer().teleport(visitLocation);
    }

    @Override
    public List<String> tabComplete(SuperiorSkyblockPlugin plugin, CommandSender sender, String[] args) {
        SuperiorPlayer superiorPlayer = SSuperiorPlayer.of(sender);
        List<String> list = new ArrayList<>();

        if(args.length == 2){
            for(Player player : Bukkit.getOnlinePlayers()){
                SuperiorPlayer onlinePlayer = SSuperiorPlayer.of(player);
                Island island = onlinePlayer.getIsland();
                if (island != null && (island.getWarpLocation("visit") != null || superiorPlayer.hasBypassModeEnabled()) &&
                        (!island.isLocked() || island.hasPermission(superiorPlayer, IslandPermission.CLOSE_BYPASS)) ) {
                    if (player.getName().toLowerCase().startsWith(args[1].toLowerCase()))
                        list.add(player.getName());
                    if (island.getName().toLowerCase().startsWith(args[1].toLowerCase()))
                        list.add(island.getName());
                }
            }
        }

        return list;
    }
}
