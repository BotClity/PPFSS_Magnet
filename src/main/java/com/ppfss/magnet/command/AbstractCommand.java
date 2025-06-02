// PPFS_Libs Plugin
// Авторские права (c) 2024 PPFSS
// Лицензия: MIT

package com.ppfss.magnet.command;

import com.ppfss.magnet.utils.ColorUtils;
import lombok.NonNull;
import org.bukkit.command.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.*;

public abstract class AbstractCommand implements CommandExecutor, TabCompleter {

    private final Map<String, SubCommand> subCommands = new HashMap<>();

    private final Map<String, String> subAliases = new HashMap<>();

    private final Plugin plugin;

    private final String name;

    private final FileConfiguration cfg;

    public AbstractCommand(String name, Plugin plugin) {
        this.plugin = plugin;
        this.cfg = plugin.getConfig();
        PluginCommand command = plugin.getServer().getPluginCommand(name);
        this.name = name;
        if (command != null) {
            command.setExecutor(this);
            command.setTabCompleter(this);
        }

        registerSubCommand(new HelpSubCommand());
    }

    public void registerSubCommand(SubCommand subCommand) {
        String subName = subCommand.getName().toLowerCase();

        subCommands.put(subName, subCommand);
        subAliases.put(subName, subName);
        subCommand.getAliases().forEach(alias -> {
            if (subAliases.containsKey(alias.toLowerCase())) {
                plugin.getLogger().warning("Alias already have used " + alias);
            }
            subAliases.put(alias.toLowerCase(), subCommand.getName().toLowerCase());
        });
    }

    public void execute(@NonNull CommandSender sender, @NonNull Command cmd, @NonNull String label, @NonNull String[] args) {
    }

    public void noPermission(CommandSender sender, Command command, String label, String... args) {
        sender.sendMessage("No permissions");
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String s, @NonNull String[] args) {
        if (args.length > 0 && !subCommands.isEmpty()) {
            String cmd = args[0].toLowerCase();
            String subCmd = subAliases.get(cmd);

            SubCommand subCommand = subCommands.get(subCmd);
            if (subCommand != null) {
                String permission = subCommand.getPermission(sender, args);

                if (permission != null && !sender.hasPermission(permission)) {
                    subCommand.noPermission(sender, command, s, args);
                    return true;
                }
                subCommand.execute(sender, command, s, Arrays.copyOfRange(args, 1, args.length));
                return true;
            }
        }

        String permission = getPermission(sender, args);
        if (permission != null && !sender.hasPermission(permission)) {
            noPermission(sender, command, s, args);
            return true;
        }

        execute(sender, command, s, args);
        return true;
    }

    public String getPermission(CommandSender sender, String... args) {
        return null;
    }

    private List<String> filter(List<String> strings, String... args) {
        if (strings == null || strings.isEmpty()) return new ArrayList<>();
        String lastArg = args[args.length - 1].toLowerCase().trim();
        List<String> filtered = new ArrayList<>();
        for (String string : strings) {
            if (string.toLowerCase().startsWith(lastArg)) filtered.add(string);
        }
        return filtered;
    }

    public List<String> complete(CommandSender sender, String... args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            for (String key : subCommands.keySet()) {

                SubCommand subCommand = subCommands.get(key);

                String permission = subCommand.getPermission(sender, args);

                if (permission != null && sender.hasPermission(permission)) {
                    completions.add(key);
                    completions.addAll(subCommand.getAliases());
                }
            }
            return completions;
        } else if (args.length > 1) {
            String cmd = args[0].toLowerCase();
            String cmdName = subAliases.get(cmd);

            SubCommand subCommand = subCommands.get(cmdName);
            if (subCommand != null) {
                return subCommand.complete(sender, Arrays.copyOfRange(args, 1, args.length));
            }
        }

        return List.of("help");
    }


    @Override
    public List<String> onTabComplete(@NonNull CommandSender commandSender, @NonNull Command command, @NonNull String s, @NonNull String[] strings) {
        return filter(complete(commandSender, strings), strings);
    }


    private class HelpSubCommand extends SubCommand {
        public HelpSubCommand() {
            super("help", List.of("помощь", "?", "h"));
        }

        @Override
        public String getName() {
            return "help";
        }

        @Override
        public String getPermission(CommandSender sender, String... args) {
            return name + ".help";
        }

        @Override
        public void noPermission(CommandSender sender, Command command, String label, String... args) {
            sender.sendMessage(
                    ColorUtils.color(
                            cfg.getString(
                                    "messages.no-permission-help",
                                    "§cУ вас нет прав!"
                            )));
        }

        @Override
        public void execute(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, @NonNull String[] args) {
            String header = cfg.getString(
                    "messages.help.header",
                    "§6Доступные команды для /%command%:"
            ).replace("%command%", label);

            header = ColorUtils.color(header);

            String format = cfg.getString(
                    "messages.help.format",
                    "§e/%command% %subcommand% §7- %description%"
            );

            format = ColorUtils.color(format);

            StringBuilder helpMessage = new StringBuilder(header + "\n");
            for (SubCommand subCommand : subCommands.values()) {
                String permission = subCommand.getPermission(sender, args);
                if (permission == null || sender.hasPermission(permission)) {
                    String line = format
                            .replace("%command%", label)
                            .replace("%subcommand%", subCommand.getName())
                            .replace("%description%", subCommand.getDescription());
                    helpMessage.append(line).append("\n");
                }
            }
            sender.sendMessage(helpMessage.toString().trim());
        }

        @Override
        public String getDescription() {
            return ColorUtils.color(cfg.getString(
                    "messages.help.description",
                    "Показывает список доступных команд"
            ));
        }
    }
}
