package ua.senalll.immersionCore.nickname

import net.kyori.adventure.text.event.ClickEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.scheduler.BukkitRunnable
import ua.senalll.immersionCore.ImmersionCore
import ua.senalll.immersionCore.database.RelationTableHandler
import ua.senalll.immersionapi.component.gray
import ua.senalll.immersionapi.component.green
import ua.senalll.immersionapi.component.red
import ua.senalll.immersionapi.component.white

class NickNameListener(private val plugin: ImmersionCore) : Listener {

    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        object : BukkitRunnable() {
            override fun run() {
                for (viewer in Bukkit.getOnlinePlayers()) {
                    NameUpdater.updateFriendDisplayName(e.player, viewer)
                    NameUpdater.updateFriendDisplayName(viewer, e.player)
                    NameUpdater.updateNameForBoth(e.player, viewer)
                }
            }
        }.runTaskLater(plugin, 2L)
    }


    @EventHandler
    fun onInteract(e: PlayerInteractAtEntityEvent) {
        val rightClicked = e.rightClicked
        if (rightClicked !is Player) return

        val initiator = e.player
        val target = rightClicked

        if (!e.player.isSneaking) return

        e.isCancelled = true

        object : BukkitRunnable() {
            override fun run() {
                val relationExists = RelationTableHandler.isRelationExistAsync(initiator.uniqueId, target.uniqueId).get()
                if (relationExists) return

                val relationPDC = plugin.pdcRegistry.relationPDC()
                val pendingForInitiator = relationPDC.getMakingFriendList(initiator)

                if (pendingForInitiator.contains(target.uniqueId)) {
                    Bukkit.getScheduler().runTask(plugin, Runnable {
                        initiator.sendMessage("Вы уже отправили приглашение этому игроку!".gray())
                    })
                    return
                }

                val pendingForTarget = relationPDC.getMakingFriendList(target)
                if (pendingForTarget.contains(initiator.uniqueId)) {
                    Bukkit.getScheduler().runTask(plugin, Runnable {
                        initiator.sendMessage("Этот игрок уже отправил вам приглашение! Примите его в чате.".gray())
                    })
                    return
                }

                Bukkit.getScheduler().runTask(plugin, Runnable {
                    relationPDC.setMakingFriend(initiator, target)

                    object : BukkitRunnable() {
                        override fun run() {
                            relationPDC.removeMakingFriend(initiator, target)
                        }
                    }.runTaskLater(plugin, 20 * 10L)

                    val acceptComponent = "[Принять]".green()
                        .hoverEvent("Нажмите, чтобы принять приглашение".white())
                        .clickEvent(ClickEvent.runCommand("/relation accept ${initiator.uniqueId}"))

                    val denyComponent = "[Отклонить]".red()
                        .hoverEvent("Нажмите, чтобы отклонить приглашение".white())
                        .clickEvent(ClickEvent.runCommand("/relation deny ${initiator.uniqueId}"))

                    val mainMsg = "* Игрок ".gray()
                        .append(initiator.displayName().color(white))
                        .append(" хочет добавить вас в друзья. ".gray())
                        .append(acceptComponent)
                        .append(" ".gray())
                        .append(denyComponent)

                    target.sendMessage(mainMsg)
                    initiator.sendMessage("* Приглашение отправлено игроку *".gray().append(target.displayName()))
                })
            }
        }.runTaskAsynchronously(plugin)
    }


    @EventHandler
    fun onPlayerMove(e: PlayerMoveEvent) {
        val viewer = e.player

        val target = getLookedAtPlayer(viewer)
        if (target != null) {
            RelationTableHandler.isRelationExistAsync(viewer.uniqueId, target.uniqueId).thenAccept { result ->
                if (result) {
                    NameUpdater.getNameToShow(viewer, target).thenAccept { name ->
                        e.player.sendActionBar("\uD83D\uDC6C ".green().append(name.white()))
                    }
                } else {
                    e.player.sendActionBar("* Незнакомец *".gray())
                }
            }
        } else {
            e.player.sendActionBar("".white())
        }
    }

    fun getLookedAtPlayer(viewer: Player): Player? {
        val target = viewer.getTargetEntity(3, false)
        if (target is Player){
            if (plugin.pdcRegistry.vanishPDC().isVanished(target)){
                return null
            }
        }
        return target as? Player
    }
}