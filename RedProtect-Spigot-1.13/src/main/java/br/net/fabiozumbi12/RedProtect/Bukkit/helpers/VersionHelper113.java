/*
 * Copyright (c) 2019 - @FabioZumbi12
 * Last Modified: 25/04/19 07:02
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

package br.net.fabiozumbi12.RedProtect.Bukkit.helpers;

import br.net.fabiozumbi12.RedProtect.Bukkit.RedProtect;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.LingeringPotionSplashEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.material.Door;
import org.bukkit.projectiles.ProjectileSource;

import java.util.Set;
import java.util.stream.Collectors;

public class VersionHelper113 implements VersionHelper {

    public Set<Location> getPortalLocations(PortalCreateEvent e) {
        return e.getBlocks().stream().map(Block::getLocation).collect(Collectors.toSet());
    }

    public boolean denyEntLingPot(LingeringPotionSplashEvent e) {
        return RedProtect.get().getUtil().denyPotion(e.getEntity().getItem());
    }

    public Entity getEntLingPot(LingeringPotionSplashEvent e) {
        return e.getEntity();
    }

    public ProjectileSource getPlayerLingPot(LingeringPotionSplashEvent e) {
        return e.getEntity().getShooter();
    }

    public void toggleDoor(Block b) {
        BlockState state = b.getState();
        Door op = (Door) state.getData();
        if (!op.isOpen())
            op.setOpen(true);
        else
            op.setOpen(false);
        state.setData(op);
        state.update();
    }

    public boolean isOpenable(Block b) {
        return b.getState().getData() instanceof Door;
    }
}
