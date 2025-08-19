// PPFSS_Magnet Plugin 
// Авторские права (c) 2025 PPFSS
// Лицензия: MIT

package com.ppfss.magnet.model;

import com.google.gson.annotations.SerializedName;
import com.ppfss.magnet.message.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;

@Getter
@Setter
@AllArgsConstructor
public class DefaultItemData {
    private Message name;
    private Message lore;
    private Material type;
    @SerializedName("head-value")
    private String headValue;
}
