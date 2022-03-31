package tororo1066.itemframeprotector

import org.bukkit.Material
import org.bukkit.persistence.PersistentDataType
import tororo1066.tororopluginapi.sCommand.*
import tororo1066.tororopluginapi.sItem.SItem
import java.util.function.Consumer

class IFCommand : SCommand("ifp",ItemFrameProtector.prefix) {

    init {
        registerReportCommand(ItemFrameProtector.plugin,"ifp.user","ifp.op")

        addCommand(SCommandObject().addNeedPermission("ifp.op").addArg(SCommandArg().addAllowString("staff")).setExecutor(
            Consumer<SCommandOnlyPlayerData> { it.sender.inventory.addItem(SItem(Material.STICK).setCustomData(ItemFrameProtector.plugin,"IFP",
                PersistentDataType.INTEGER,1).setDisplayName("§a額縁保護破壊の杖§f(ｶｯｺｲｲ!)")) }
        ))

        addCommand(SCommandObject().addNeedPermission("ifp.op").addArg(SCommandArg().addAllowString("convert")).setExecutor(
            Consumer<SCommandData> {
                IFConvert.convert()
            }
        ))
    }
}