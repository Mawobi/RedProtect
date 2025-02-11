/*
 * Copyright (c) 2012-2023 - @FabioZumbi12
 * Last Modified: 10/05/2023 14:49
 *
 * This class is provided 'as-is', without any express or implied warranty. In no event will the authors be held liable for any
 *  damages arising from the use of this class.
 *
 * Permission is granted to anyone to use this class for any purpose, including commercial plugins, and to alter it and
 * redistribute it freely, subject to the following restrictions:
 * 1 - The origin of this class must not be misrepresented; you must not claim that you wrote the original software. If you
 * use this class in other plugins, an acknowledgment in the plugin documentation would be appreciated but is not required.
 * 2 - Altered source versions must be plainly marked as such, and must not be misrepresented as being the original class.
 * 3 - This notice may not be removed or altered from any source distribution.
 *
 * Esta classe é fornecida "como está", sem qualquer garantia expressa ou implícita. Em nenhum caso os autores serão
 * responsabilizados por quaisquer danos decorrentes do uso desta classe.
 *
 * É concedida permissão a qualquer pessoa para usar esta classe para qualquer finalidade, incluindo plugins pagos, e para
 * alterá-lo e redistribuí-lo livremente, sujeito às seguintes restrições:
 * 1 - A origem desta classe não deve ser deturpada; você não deve afirmar que escreveu a classe original. Se você usar esta
 *  classe em um plugin, uma confirmação de autoria na documentação do plugin será apreciada, mas não é necessária.
 * 2 - Versões de origem alteradas devem ser claramente marcadas como tal e não devem ser deturpadas como sendo a
 * classe original.
 * 3 - Este aviso não pode ser removido ou alterado de qualquer distribuição de origem.
 */

package br.net.fabiozumbi12.RedProtect.Bukkit.commands.SubCommands.RegionHandlers;

import br.net.fabiozumbi12.RedProtect.Bukkit.RedProtect;
import br.net.fabiozumbi12.RedProtect.Bukkit.Region;
import br.net.fabiozumbi12.RedProtect.Bukkit.actions.DefineRegionBuilder;
import br.net.fabiozumbi12.RedProtect.Bukkit.commands.SubCommand;
import br.net.fabiozumbi12.RedProtect.Bukkit.region.RegionBuilder;
import br.net.fabiozumbi12.RedProtect.Core.region.PlayerRegion;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static br.net.fabiozumbi12.RedProtect.Bukkit.commands.CommandHandlers.HandleHelpPage;

public class CreatePortalCommand implements SubCommand {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            HandleHelpPage(sender, 1);
            return true;
        }

        Player player = (Player) sender;

        //rp createportal <regionName> <regionTo> <world>
        if (args.length == 3) {
            World w = RedProtect.get().getServer().getWorld(args[2]);
            if (w == null) {
                sender.sendMessage(RedProtect.get().getLanguageManager().get("cmdmanager.region.invalidworld"));
                return true;
            }
            Region r = RedProtect.get().getRegionManager().getRegion(args[1], w.getName());
            if (r == null) {
                RedProtect.get().getLanguageManager().sendMessage(player, RedProtect.get().getLanguageManager().get("cmdmanager.createportal.warning").replace("{region}", args[1]));
            }

            PlayerRegion serverName = new PlayerRegion(RedProtect.get().getConfigManager().configRoot().region_settings.default_leader, RedProtect.get().getConfigManager().configRoot().region_settings.default_leader);
            String name = Normalizer.normalize(args[0].replace(" ", "_"), Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "").replaceAll("[^\\p{L}0-9 ]", "");

            final Region[] r2 = {RedProtect.get().getRegionManager().getRegion(name, w.getName())};
            if (r == r2[0]) {
                RedProtect.get().getLanguageManager().sendMessage(player, RedProtect.get().getLanguageManager().get("cmdmanager.createportal.equals"));
                return true;
            }

            if (r2[0] != null) {
                if ((!r2[0].isLeader(player) || !r2[0].isAdmin(player)) && !r2[0].canBuild(player)) {
                    RedProtect.get().getLanguageManager().sendMessage(player, "no.permission");
                    return true;
                }
                RedProtect.get().getLanguageManager().sendMessage(player, String.format(RedProtect.get().getLanguageManager().get("cmdmanager.region.portalcreated"), name, args[1], w.getName()));
                RedProtect.get().getLanguageManager().sendMessage(player, "cmdmanager.region.portalhint");
                r2[0].setFlag(sender, "set-portal", args[1] + " " + w.getName());

                RedProtect.get().logger.addLog("(World " + r2[0].getWorld() + ") Player " + player.getName() + " CREATED A PORTAL " + r2[0].getName() + " to " + args[1] + " database " + w.getName());
            } else {
                RedProtect.get().getLanguageManager().sendMessage(player, "regionbuilder.creating");

                // Run claim async
                Bukkit.getScheduler().runTaskAsynchronously(RedProtect.get(), () -> {
                    RegionBuilder rb2 = new DefineRegionBuilder(player, RedProtect.get().firstLocationSelections.get(player), RedProtect.get().secondLocationSelections.get(player), name, serverName, new HashSet<>(), true);
                    if (rb2.ready()) {
                        r2[0] = rb2.build();
                        RedProtect.get().getLanguageManager().sendMessage(player, String.format(RedProtect.get().getLanguageManager().get("cmdmanager.region.portalcreated"), name, args[1], w.getName()));
                        RedProtect.get().getLanguageManager().sendMessage(player, "cmdmanager.region.portalhint");

                        r2[0].setFlag(sender, "set-portal", args[1] + " " + w.getName());
                        RedProtect.get().getRegionManager().add(r2[0], player.getWorld().getName());

                        RedProtect.get().firstLocationSelections.remove(player);
                        RedProtect.get().secondLocationSelections.remove(player);

                        RedProtect.get().logger.addLog("(World " + r2[0].getWorld() + ") Player " + player.getName() + " CREATED A PORTAL " + r2[0].getName() + " to " + args[1] + " database " + w.getName());
                    }
                });
            }
            return true;
        }

        RedProtect.get().getLanguageManager().sendCommandHelp(sender, "createportal", true);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> tab = new ArrayList<>();
        if (args.length == 3) {
            if (args[2].isEmpty())
                tab.addAll(Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList()));
            else
                tab.addAll(Bukkit.getWorlds().stream().filter(e -> e.getName().startsWith(args[2])).map(World::getName).collect(Collectors.toList()));
        }
        return tab;
    }
}