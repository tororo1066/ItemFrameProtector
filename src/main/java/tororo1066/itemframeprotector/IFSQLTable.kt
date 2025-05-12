package tororo1066.itemframeprotector

import org.bukkit.Bukkit
import org.bukkit.Location
import tororo1066.tororopluginapi.database.SDBVariable
import tororo1066.tororopluginapi.mysql.ultimate.USQLTable
import java.util.*

class IFSQLTable(table: String): USQLTable(table,ItemFrameProtector.mysql){

    @Suppress("unused")
    companion object{
        val id = SDBVariable(SDBVariable.Int,true)
        val placePlayer = SDBVariable(SDBVariable.VarChar,36)
        val placePlayerName = SDBVariable(SDBVariable.VarChar,16)
        val frameId = SDBVariable(SDBVariable.VarChar,36)
        val loc = SDBVariable(SDBVariable.VarChar,100)
    }

    fun loadData(){
        val rs = select()
        rs.forEach { result ->
            val data = IFDataImpl()
            data.placePlayer = UUID.fromString(result.getString("placePlayer"))
            data.placePlayerName = result.getString("placePlayerName")
            data.uuid = UUID.fromString(result.getString("frameId"))
            val split = result.getString("loc").split(",")
            if (ItemFrameProtector.disableWorlds.contains(split[0])){
                delete(frameId.equal(data.uuid))
                return@forEach
            }
            val loc = Location(Bukkit.getWorld(split[0])?:return@forEach,split[1].toDouble(),split[2].toDouble(),split[3].toDouble())
            data.loc = loc

            ItemFrameProtector.itemFrameData[data.uuid] = data
        }
    }
}