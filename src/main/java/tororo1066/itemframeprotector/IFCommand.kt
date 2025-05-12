package tororo1066.itemframeprotector

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.ItemFrame
import org.bukkit.persistence.PersistentDataType
import tororo1066.tororopluginapi.sCommand.*
import tororo1066.tororopluginapi.sItem.SItem
import tororo1066.tororopluginapi.utils.LocType
import tororo1066.tororopluginapi.utils.toLocString
import java.util.function.Consumer

class IFCommand : SCommand(
    "ifp",
    prefix = ItemFrameProtector.PREFIX
) {

    init {
        addCommand(
            SCommandObject().addNeedPermission("ifp.op").addArg(SCommandArg().addAllowString("staff"))
                .setPlayerExecutor {
                    it.sender.inventory.addItem(
                        SItem(Material.STICK).setCustomData(
                            ItemFrameProtector.plugin, "IFP",
                            PersistentDataType.INTEGER, 1
                        ).setDisplayName("§a額縁保護破壊の杖§f(ｶｯｺｲｲ!)").build()
                    )
                })

        addCommand(
            SCommandObject().addNeedPermission("ifp.op").addArg(SCommandArg().addAllowString("disableWorlds")).addArg(SCommandArg().addAllowString("add")).addArg(SCommandArg().addAllowType(SCommandArgType.WORLD))
                .setNormalExecutor {
                    val list = ItemFrameProtector.plugin.config.getStringList("disableWorlds")
                    ItemFrameProtector.disableWorlds.add(it.args[2])
                    list.add(it.args[2])
                    ItemFrameProtector.plugin.config.set("disableWorlds", list)
                    ItemFrameProtector.plugin.saveConfig()
                    it.sender.sendMessage(ItemFrameProtector.PREFIX + "§a追加しました")
                })

        addCommand(
            SCommandObject()
                .addNeedPermission("ifp.op")
                .addArg(SCommandArg().addAllowString("disableWorlds"))
                .addArg(SCommandArg().addAllowString("remove"))
                .addArg(SCommandArg(ItemFrameProtector.disableWorlds))
                .setNormalExecutor {
                    val list = ItemFrameProtector.plugin.config.getStringList("disableWorlds")
                    ItemFrameProtector.disableWorlds.remove(it.args[2])
                    list.remove(it.args[2])
                    ItemFrameProtector.plugin.config.set("disableWorlds", list)
                    ItemFrameProtector.plugin.saveConfig()
                    it.sender.sendMessage(ItemFrameProtector.PREFIX + "§a削除しました")
                })

        addCommand(
            SCommandObject().addNeedPermission("ifp.op").addArg(SCommandArg().addAllowString("disableWorlds"))
                .setNormalExecutor {
                    it.sender.sendMessage(ItemFrameProtector.PREFIX + "§a現在の無効化ワールド: ${ItemFrameProtector.disableWorlds}")
                }
        )

        addCommand(
            SCommandObject()
                .addNeedPermission("ifp.op")
                .addArg(SCommandArg("saveAll"))
                .setPlayerExecutor {
                    Bukkit.getWorlds().forEach { world ->
                        if (world.name in ItemFrameProtector.disableWorlds) return@forEach
                        world.entities.forEach { entity ->
                            if (entity !is ItemFrame) return@forEach
                            if (ItemFrameProtector.itemFrameData.containsKey(entity.uniqueId)) return@forEach
                            val data = IFDataImpl()
                            data.uuid = entity.uniqueId
                            data.loc = entity.location.toBlockLocation().apply {
                                yaw = 0f
                                pitch = 0f
                            }
                            data.placePlayer = it.sender.uniqueId
                            data.placePlayerName = it.sender.name
                            ItemFrameProtector.itemFrameData[data.uuid] = data
                            ItemFrameProtector.ifSQLTable.callBackInsert(
                                data.placePlayer,
                                data.placePlayerName,
                                data.uuid,
                                data.loc.toLocString(LocType.WORLD_BLOCK_COMMA)
                            )
                        }
                    }
                    it.sender.sendMessage(ItemFrameProtector.PREFIX + "§a保存しました")
                }
        )

        addCommand(
            SCommandObject().addNeedPermission("ifp.op").addArg(SCommandArg().addAllowString("convert"))
                .setNormalExecutor {
                    IFConvert.convert()
                })
    }
}