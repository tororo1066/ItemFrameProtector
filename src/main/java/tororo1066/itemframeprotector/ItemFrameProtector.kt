package tororo1066.itemframeprotector

import org.bukkit.entity.Player
import tororo1066.tororopluginapi.SJavaPlugin
import tororo1066.tororopluginapi.mysql.SMySQL
import java.util.UUID

class ItemFrameProtector : SJavaPlugin() {

    companion object{
        lateinit var plugin: ItemFrameProtector
        lateinit var mysql: SMySQL
        lateinit var ifSQLTable: IFSQLTable
        val itemFrameData = HashMap<UUID,IFDataImpl>()
        const val PREFIX = "§6[§d§lItem§b§lFrame§c§lProtect§6]§r"
        val disableWorlds = ArrayList<String>()

        fun Player.sendPrefixMsg(s : String){
            this.sendMessage(PREFIX + s)
        }
    }

    override fun onStart() {
        saveDefaultConfig()
        plugin = this
        mysql = SMySQL(this)
        disableWorlds.addAll(config.getStringList("disableWorlds"))
        val tableName = config.getString("mysql.tableName", "protect_id")!!
        ifSQLTable = IFSQLTable(tableName)
        ifSQLTable.loadData()
        IFEvent()
        IFCommand()
    }

    override fun onEnd() {

    }
}