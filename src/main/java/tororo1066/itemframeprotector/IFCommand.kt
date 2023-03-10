package tororo1066.itemframeprotector

import org.bukkit.Material
import org.bukkit.persistence.PersistentDataType
import tororo1066.tororopluginapi.sCommand.*
import tororo1066.tororopluginapi.sItem.SItem
import java.util.function.Consumer

class IFCommand : SCommand("ifp",ItemFrameProtector.prefix) {

    init {
        addCommand(SCommandObject().addNeedPermission("ifp.op").addArg(SCommandArg().addAllowString("staff")).setExecutor(
            Consumer<SCommandOnlyPlayerData> { it.sender.inventory.addItem(SItem(Material.STICK).setCustomData(ItemFrameProtector.plugin,"IFP",
                PersistentDataType.INTEGER,1).setDisplayName("§a額縁保護破壊の杖§f(ｶｯｺｲｲ!)")) }
        ))

        addCommand(SCommandObject().addNeedPermission("ifp.op").addArg(SCommandArg().addAllowString("disableWorlds")).addArg(SCommandArg().addAllowString("add")).addArg(SCommandArg().addAllowType(SCommandArgType.WORLD)).setExecutor(
            Consumer<SCommandData> {
                val list = ItemFrameProtector.plugin.config.getStringList("disableWorlds")
                ItemFrameProtector.disableWorlds.add(it.args[2])
                list.add(it.args[2])
                ItemFrameProtector.plugin.config.set("disableWorlds",list)
                ItemFrameProtector.plugin.saveConfig()
                it.sender.sendMessage(ItemFrameProtector.prefix + "§a追加しました")
            }
        ))

        addCommand(SCommandObject().addNeedPermission("ifp.op").addArg(SCommandArg().addAllowString("disableWorlds")).addArg(SCommandArg().addAllowString("remove")).addArg(SCommandArg().addAllowType(SCommandArgType.WORLD)).setExecutor(
            Consumer<SCommandData> {
                val list = ItemFrameProtector.plugin.config.getStringList("disableWorlds")
                ItemFrameProtector.disableWorlds.remove(it.args[2])
                list.remove(it.args[2])
                ItemFrameProtector.plugin.config.set("disableWorlds",list)
                ItemFrameProtector.plugin.saveConfig()
                it.sender.sendMessage(ItemFrameProtector.prefix + "§a削除しました")
            }
        ))

        addCommand(SCommandObject().addNeedPermission("ifp.op").addArg(SCommandArg().addAllowString("convert")).setExecutor(
            Consumer<SCommandData> {
                IFConvert.convert()
            }
        ))
    }
}