package tororo1066.itemframeprotector

import org.bukkit.Bukkit
import org.bukkit.Location
import tororo1066.tororopluginapi.database.SDBCondition
import tororo1066.tororopluginapi.database.SDBVariable
import tororo1066.tororopluginapi.database.mysql.SMySQL
import tororo1066.tororopluginapi.utils.LocType
import tororo1066.tororopluginapi.utils.toLocString
import java.util.UUID

class IFDatabase {

    val database = SMySQL(
        ItemFrameProtector.plugin,
        configFile = null,
        configPath = "mysql"
    )

    val tableName = ItemFrameProtector.plugin.config.getString("mysql.tableName", "protect_id")!!

    init {
        createTable()
        loadData()
    }

    fun createTable() {
        database.createTable(tableName, mapOf(
            "id" to SDBVariable(SDBVariable.Int, true),
            "placePlayer" to SDBVariable(SDBVariable.VarChar, 36),
            "placePlayerName" to SDBVariable(SDBVariable.VarChar, 16),
            "frameId" to SDBVariable(SDBVariable.VarChar, 36),
            "loc" to SDBVariable(SDBVariable.VarChar, 100)
        ))
    }

    fun loadData() {
        val rs = database.select(tableName)
        rs.forEach { result ->
            val data = IFDataImpl()
            data.placePlayer = UUID.fromString(result.getString("placePlayer"))
            data.placePlayerName = result.getString("placePlayerName")
            data.uuid = UUID.fromString(result.getString("frameId"))
            val split = result.getString("loc").split(",")
            if (ItemFrameProtector.disableWorlds.contains(split[0])){
                delete(data.uuid)
                return@forEach
            }
            val loc = Location(
                Bukkit.getWorld(split[0]) ?: return@forEach,
                split[1].toDouble(),
                split[2].toDouble(),
                split[3].toDouble()
            )
            data.loc = loc

            ItemFrameProtector.itemFrameData[data.uuid] = data
        }
    }

    fun insert(data: IFDataImpl) {
        database.backGroundInsert(
            tableName,
            mapOf(
                "placePlayer" to data.placePlayer.toString(),
                "placePlayerName" to data.placePlayerName,
                "frameId" to data.uuid.toString(),
                "loc" to data.loc.toLocString(LocType.WORLD_BLOCK_COMMA)
            )
        )
    }

    fun delete(frameId: UUID) {
        database.backGroundDelete(
            tableName,
            SDBCondition().equal("frameId", frameId.toString())
        )
    }
}