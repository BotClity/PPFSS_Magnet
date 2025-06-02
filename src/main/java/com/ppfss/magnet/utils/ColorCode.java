package com.ppfss.magnet.utils;

public enum ColorCode {
    RESET("\u001B[0m", ""),
    RED("\u001B[31m", "&c"),
    GREEN("\u001B[32m", "&a"),
    YELLOW("\u001B[33m", "&e"),
    BLUE("\u001B[34m", "&9"),
    PURPLE("\u001B[35m", "&5"),
    CYAN("\u001B[36m", "&b"),
    WHITE("\u001B[37m", "&f");

    private final String ansiCode;
    private final String chatCode;

    ColorCode(String ansiCode, String chatCode) {
        this.ansiCode = ansiCode;
        this.chatCode = chatCode;
    }

    public String getAnsiCode() {
        return ansiCode;
    }

    public String getChatCode() {
        return chatCode;
    }
}