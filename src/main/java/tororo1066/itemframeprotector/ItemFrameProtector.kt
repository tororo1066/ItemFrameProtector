package tororo1066.itemframeprotector

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import tororo1066.tororopluginapi.SMySQL
import java.util.UUID

class ItemFrameProtector : JavaPlugin() {

    companion object{
        lateinit var plugin: ItemFrameProtector
        lateinit var mysql: SMySQL
        val itemFrameData = HashMap<UUID,IFData>()
        const val prefix = "§6[§d§lItem§b§lFrame§c§lProtect§6]§r"

        fun Player.sendPrefixMsg(s : String){
            this.sendMessage(prefix + s)
        }
    }

    override fun onEnable() {
        saveDefaultConfig()
        plugin = this
        mysql = SMySQL(this)
        IFEvent()
        IFCommand()

        mysql.execute("CREATE TABLE IF NOT EXISTS `protect_id` (\n" +
                "\t`id` INT(10) NOT NULL AUTO_INCREMENT,\n" +
                "\t`placePlayer` VARCHAR(36) NULL DEFAULT NULL COLLATE 'utf8mb4_0900_ai_ci',\n" +
                "\t`placePlayerName` VARCHAR(16) NULL DEFAULT NULL COLLATE 'utf8mb4_0900_ai_ci',\n" +
                "\t`frameId` VARCHAR(36) NULL DEFAULT NULL COLLATE 'utf8mb4_0900_ai_ci',\n" +
                "\t`loc` VARCHAR(100) NOT NULL DEFAULT '0' COLLATE 'utf8mb4_0900_ai_ci',\n" +
                "\tPRIMARY KEY (`id`) USING BTREE\n" +
                ")\n" +
                "COLLATE='utf8mb4_0900_ai_ci'\n" +
                "ENGINE=InnoDB")

        val rs = mysql.sQuery("select * from protect_id")

        for (result in rs) {
            val data = IFData()
            data.placePlayer = UUID.fromString(result.getString("placePlayer"))
            data.placePlayerName = result.getString("placePlayerName")
            data.uuid = UUID.fromString(result.getString("frameId"))
            val split = result.getString("loc").split(",")
            val loc = Location(Bukkit.getWorld(split[0]),split[1].toDouble(),split[2].toDouble(),split[3].toDouble())
            data.loc = loc

            itemFrameData[data.uuid] = data
        }


    }
}