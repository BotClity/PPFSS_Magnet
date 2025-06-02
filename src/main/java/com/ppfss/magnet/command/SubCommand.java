// PPFS_Libs Plugin
// Авторские права (c) 2024 PPFSS
// Лицензия: MIT

package com.ppfss.magnet.command;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Абстрактный класс для подкоманд, поддерживающий вложенные команды.
 */
public abstract class SubCommand {


    /**
     * Имя текущей команды.
     */
    @Getter
    private final String name;

    @Getter
    private final List<String> aliases;

    @Getter
    @Setter
    private String description;

    /**
     * Флаг, указывающий, может ли команда быть выполнена только игроками.
     * По умолчанию: true.
     */
    @Setter
    private boolean onlyPlayers = true;


    public SubCommand(String name) {
        this.name = name;
        aliases = new ArrayList<>();
    }

    public SubCommand(String name, List<String> aliases) {
        this.name = name;
        this.aliases = aliases;
    }

    public SubCommand(String name, boolean onlyPlayers){
        this(name);
        this.onlyPlayers = onlyPlayers;
    }

    public SubCommand(String name, List<String> aliases, boolean onlyPlayers){
        this(name, aliases);
        this.onlyPlayers = onlyPlayers;
    }

    /**
     * Выполняет команду. Если есть вложенные подкоманды, передает управление им.
     *
     * @param sender  Отправитель команды (игрок или консоль).
     * @param command Объект команды.
     * @param label   Лейбл команды (основное имя команды).
     * @param args    Аргументы команды.
     */
    public void execute(CommandSender sender, Command command, String label, String... args) {
        if (onlyPlayers && !(sender instanceof Player)) {
            onlyPlayerExecute(sender, command, label, args);
            return;
        }
        handle(sender, command, label, args);
    }

    /**
     * Предоставляет список возможных завершений для автодополнения.
     *
     * @param sender Отправитель команды.
     * @param args   Аргументы команды.
     * @return Список возможных завершений.
     */
    public List<String> complete(CommandSender sender, String... args) {
        return Collections.emptyList();
    }

    /**
     * Основная логика команды, выполняется, если нет вложенных команд.
     *
     * @param sender  Отправитель команды.
     * @param command Объект команды.
     * @param label   Лейбл команды.
     * @param args    Аргументы команды.
     */
    public void handle(CommandSender sender, Command command, String label, String... args) {
        sender.sendMessage("This command has no specific action.");
    }

    /**
     * Сообщает отправителю, что у него нет разрешения на выполнение команды.
     *
     * @param sender  Отправитель команды.
     * @param command Объект команды.
     * @param label   Лейбл команды.
     * @param args    Аргументы команды.
     */
    public void noPermission(CommandSender sender, Command command, String label, String... args) {
        sender.sendMessage("No permissions");
    }

    /**
     * Сообщает отправителю, что команда может быть выполнена только игроками.
     *
     * @param sender  Отправитель команды.
     * @param command Объект команды.
     * @param label   Лейбл команды.
     * @param args    Аргументы команды.
     */
    public void onlyPlayerExecute(CommandSender sender, Command command, String label, String... args) {
        sender.sendMessage("This command can only be executed by players.");
    }


    /**
     * Формирует строку разрешения для подкоманды.
     * <p>
     * Если возвращает null, то разрешение не требуется.
     *
     * @return Строка разрешения для подкоманды (например, "command.subcommand").
     */
    public String getPermission(CommandSender sender, String... args) {
        return null;
    }
}