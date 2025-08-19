// PPFSS_Magnet Plugin 
// Авторские права (c) 2025 PPFSS
// Лицензия: MIT

package com.ppfss.magnet.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Particle;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ParticleData {
    private boolean enabled;
    private Particle type;
}
