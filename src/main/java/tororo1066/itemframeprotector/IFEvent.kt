package tororo1066.itemframeprotector

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.block.BlockFace
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.block.BlockFadeEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.*
import org.bukkit.event.hanging.HangingBreakByEntityEvent
import org.bukkit.event.hanging.HangingBreakEvent
import org.bukkit.event.hanging.HangingPlaceEvent
import org.bukkit.event.player.PlayerBucketEmptyEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import tororo1066.itemframeprotector.ItemFrameProtector.Companion.sendPrefixMsg
import tororo1066.itemframeprotector.api.event.IFPRemoveEvent
import tororo1066.itemframeprotector.api.event.IFPCause
import tororo1066.itemframeprotector.api.event.IFPInteractEvent
import tororo1066.tororopluginapi.sEvent.SEvent
import tororo1066.tororopluginapi.utils.LocType
import tororo1066.tororopluginapi.utils.toLocString
import java.util.*

class IFEvent {
    init {
        SEvent(ItemFrameProtector.plugin).register(HangingPlaceEvent::class.java,EventPriority.HIGHEST) { e ->
            if (e.isCancelled)return@register
            if (!(e.player?:return@register).hasPermission("ifp.user"))return@register
            if (e.entity.type != EntityType.GLOW_ITEM_FRAME && e.entity.type != EntityType.ITEM_FRAME) return@register
            val placePlayer = e.player!!
            if (ItemFrameProtector.disableWorlds.contains(e.player!!.world.name)){
                return@register
            }
            val uuid = e.entity.uniqueId
            val loc = e.entity.location.toBlockLocation()
            loc.yaw = 0f
            loc.pitch = 0f
            val data = IFDataImpl()
            data.uuid = uuid
            data.placePlayer = placePlayer.uniqueId
            data.placePlayerName = placePlayer.name
            data.loc = loc

            ItemFrameProtector.itemFrameData[uuid] = data
            ItemFrameProtector.ifSQLTable.insert(placePlayer.uniqueId,placePlayer.name,uuid,loc.toLocString(LocType.WORLD_BLOCK_COMMA))



        }

        SEvent(ItemFrameProtector.plugin).register(HangingBreakByEntityEvent::class.java,EventPriority.HIGHEST) { e ->
            if (e.isCancelled)return@register
            if (!ItemFrameProtector.itemFrameData.containsKey(e.entity.uniqueId))return@register

            val remover = e.remover

            val data = ItemFrameProtector.itemFrameData[e.entity.uniqueId]!!

            if (remover == null){
                val event = IFPRemoveEvent(data, null, IFPCause.UNKNOWN, true)
                Bukkit.getPluginManager().callEvent(event)
                if (event.isCancelled){
                    e.isCancelled = true
                }
                return@register
            }

            if (remover is Player){
                val hand = remover.inventory.itemInMainHand
                if (isStaff(hand)){
                    val event = IFPRemoveEvent(data, remover, IFPCause.OP_STAFF)
                    Bukkit.getPluginManager().callEvent(event)
                    if (event.isCancelled){
                        e.isCancelled = true
                        return@register
                    }
                    remover.sendPrefixMsg("§b保護を強制的に破壊しました")
                    delete(e.entity.uniqueId)
                    return@register
                }
            }

            if (data.placePlayer != remover.uniqueId || e.cause == HangingBreakEvent.RemoveCause.EXPLOSION){
                val event = IFPRemoveEvent(data, remover, IFPCause.ENTITY, true)
                Bukkit.getPluginManager().callEvent(event)
                if (!event.isCancelled){
                    return@register
                }
                if (remover is Player){
                    remover.sendPrefixMsg("§4この額縁は保護されています")
                }

                e.isCancelled = true
                return@register
            }

            delete(e.entity.uniqueId)
        }

        SEvent(ItemFrameProtector.plugin).register(PlayerInteractEntityEvent::class.java){ e ->
            if (e.isCancelled)return@register
            if (!ItemFrameProtector.itemFrameData.containsKey(e.rightClicked.uniqueId))return@register

            val data = ItemFrameProtector.itemFrameData[e.rightClicked.uniqueId]!!

            val hand = e.player.inventory.itemInMainHand
            if (isStaff(hand)){
                val event = IFPInteractEvent(data, e.player, IFPCause.OP_STAFF)
                Bukkit.getPluginManager().callEvent(event)
                if (event.isCancelled){
                    e.isCancelled = true
                    return@register
                }
                e.player.sendPrefixMsg("§b保護を無視しました")
                return@register
            }

            if (data.placePlayer != e.player.uniqueId){
                val event = IFPInteractEvent(data, e.player, IFPCause.ENTITY, true)
                Bukkit.getPluginManager().callEvent(event)
                if (!event.isCancelled){
                    return@register
                }
                e.isCancelled = true
                e.player.sendPrefixMsg("§4この額縁は保護されています")
                return@register
            }

        }

        SEvent(ItemFrameProtector.plugin).register(EntityDamageEvent::class.java,EventPriority.HIGHEST){ e ->
            if (e.isCancelled || e is EntityDamageByEntityEvent)return@register
            val data = ItemFrameProtector.itemFrameData[e.entity.uniqueId]?:return@register
            val event = IFPInteractEvent(data, null, IFPCause.UNKNOWN, true)
            Bukkit.getPluginManager().callEvent(event)
            if (!event.isCancelled){
                return@register
            }
            e.isCancelled = true
        }

        SEvent(ItemFrameProtector.plugin).register(EntityDamageByEntityEvent::class.java,EventPriority.HIGHEST){ e ->
            if (e.isCancelled)return@register
            if (!ItemFrameProtector.itemFrameData.containsKey(e.entity.uniqueId))return@register
            val remover = e.damager

            val data = ItemFrameProtector.itemFrameData[e.entity.uniqueId]!!

            if (remover is Player){
                val hand = remover.inventory.itemInMainHand
                if (isStaff(hand)){
                    val event = IFPInteractEvent(data, remover, IFPCause.OP_STAFF)
                    Bukkit.getPluginManager().callEvent(event)
                    if (event.isCancelled){
                        e.isCancelled = true
                        return@register
                    }
                    remover.sendPrefixMsg("§b保護を無視しました")
                    return@register
                }
            }

            if (data.placePlayer != remover.uniqueId){
                val event = IFPInteractEvent(data, remover, IFPCause.ENTITY, true)
                Bukkit.getPluginManager().callEvent(event)
                if (!event.isCancelled){
                    return@register
                }

                if (remover is Player){
                    remover.sendPrefixMsg("§4この額縁は保護されています")
                }
                e.isCancelled = true
                return@register
            }
        }

        SEvent(ItemFrameProtector.plugin).register(BlockBreakEvent::class.java) { e ->
            if (e.isCancelled)return@register
            val entities = e.block.location.subtract(-0.5,-0.5,-0.5).getNearbyEntities(1.5,1.5,1.5).filter { it.type == EntityType.ITEM_FRAME || it.type == EntityType.GLOW_ITEM_FRAME }
            if (entities.isEmpty())return@register
            for (entity in entities){
                val data = ItemFrameProtector.itemFrameData[entity.uniqueId]?:continue

                val loc = data.loc.clone()

                when(entity.facing){
                    BlockFace.NORTH->{
                        loc.add(0.0,0.0,1.0)
                    }
                    BlockFace.EAST->{
                        loc.add(-1.0,0.0,0.0)
                    }
                    BlockFace.SOUTH->{
                        loc.add(0.0,0.0,-1.0)
                    }
                    BlockFace.WEST->{
                        loc.add(1.0,0.0,0.0)
                    }
                    BlockFace.DOWN->{
                        loc.add(0.0,1.0,0.0)
                    }
                    BlockFace.UP->{
                        loc.add(0.0,-1.0,0.0)
                    }
                    else ->{}
                }

                val breakLoc = e.block.location.toBlockLocation()

                if (breakLoc == loc){
                    val event = IFPRemoveEvent(data, e.player, IFPCause.BLOCK, true)
                    Bukkit.getPluginManager().callEvent(event)
                    if (!event.isCancelled){
                        return@register
                    }
                    e.isCancelled = true
                    e.player.sendPrefixMsg("§4この額縁は保護されています")
                    return@register
                }
            }
        }



        SEvent(ItemFrameProtector.plugin).register(EntityExplodeEvent::class.java,EventPriority.HIGHEST){ e ->
            if (e.isCancelled)return@register
            val list = e.blockList().filter { isProtectedBlock(it.location) }
            if (list.isEmpty())return@register
            list.forEach {
                e.blockList().remove(it)
            }
        }

        SEvent(ItemFrameProtector.plugin).register(BlockExplodeEvent::class.java,EventPriority.HIGHEST){ e ->
            if (e.isCancelled)return@register
            val list = e.blockList().filter { isProtectedBlock(it.location) }
            if (list.isEmpty())return@register
            list.forEach {
                e.blockList().remove(it)
            }
        }

        SEvent(ItemFrameProtector.plugin).register(BlockPlaceEvent::class.java){ e ->
            if (e.isCancelled)return@register

            if (isProtectedBlock(e.block.location.subtract(0.0,1.0,0.0))){
                e.player.sendPrefixMsg("§4額縁付近にはブロックは置けません")
                e.isCancelled = true
                return@register
            }
        }

        SEvent(ItemFrameProtector.plugin).register(PlayerBucketEmptyEvent::class.java){ e ->
            if (e.isCancelled)return@register
            if (e.bucket == Material.MILK_BUCKET)return@register
            if (isProtectedBlock(e.block.location.subtract(0.0,1.0,0.0))){
                e.player.sendPrefixMsg("§4額縁付近にはブロックは置けません")
                e.isCancelled = true
                return@register
            }
        }

        SEvent(ItemFrameProtector.plugin).register(BlockFadeEvent::class.java) { e ->
            if (isProtectedBlock(e.block.location))e.isCancelled = true
        }

//        SEvent(ItemFrameProtector.plugin).register(EntityChangeBlockEvent::class.java) { e ->
//            if (isProtectedBlock(e.block.location))e.isCancelled = true
//        }


    }

    private fun delete(uuid: UUID){
        ItemFrameProtector.itemFrameData.remove(uuid)
        ItemFrameProtector.ifSQLTable.delete(IFSQLTable.frameId.equal(uuid))
    }

    private fun isProtectedBlock(location: Location): Boolean {
        val entities = location.subtract(-0.5,-0.5,-0.5).getNearbyEntities(1.5,1.5,1.5).filter { it.type == EntityType.ITEM_FRAME || it.type == EntityType.GLOW_ITEM_FRAME }
        if (entities.isEmpty())return false
        for (entity in entities){
            if (!ItemFrameProtector.itemFrameData.containsKey(entity.uniqueId))continue
            val data = ItemFrameProtector.itemFrameData[entity.uniqueId]!!

            val loc = data.loc.clone()
            loc.add(0.0,-1.0,0.0)

            val breakLoc = location.toBlockLocation()

            if (breakLoc == loc){
                return true
            }
        }
        return false
    }

    private fun isStaff(item: ItemStack): Boolean {
        return !item.type.isAir && item.itemMeta.persistentDataContainer.has(NamespacedKey(ItemFrameProtector.plugin,"IFP"),
                PersistentDataType.INTEGER)
    }

}