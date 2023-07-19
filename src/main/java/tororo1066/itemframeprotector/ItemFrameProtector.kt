package tororo1066.itemframeprotector

import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import tororo1066.tororopluginapi.SJavaPlugin
import tororo1066.tororopluginapi.mysql.SMySQL
import java.util.UUID

class ItemFrameProtector : SJavaPlugin() {

    companion object{
        lateinit var plugin: ItemFrameProtector
        lateinit var mysql: SMySQL
        lateinit var ifSQLTable: IFSQLTable
        val itemFrameData = HashMap<UUID,IFDataImpl>()
        const val prefix = "§6[§d§lItem§b§lFrame§c§lProtect§6]§r"
        val disableWorlds = ArrayList<String>()

        fun Player.sendPrefixMsg(s : String){
            this.sendMessage(prefix + s)
        }
    }

    override fun onStart() {
        saveDefaultConfig()
        plugin = this
        mysql = SMySQL(this)
        ifSQLTable = IFSQLTable()
        ifSQLTable.loadData()
        IFEvent()
        IFCommand()
        disableWorlds.addAll(config.getStringList("disableWorlds"))
    }
}