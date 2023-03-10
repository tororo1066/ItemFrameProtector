package tororo1066.itemframeprotector

import org.bukkit.Bukkit
import org.bukkit.Location
import tororo1066.tororopluginapi.mysql.ultimate.USQLTable
import tororo1066.tororopluginapi.mysql.ultimate.USQLVariable
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap

class IFSQLTable: USQLTable(IFSQLTable::class.java,"protect_id",ItemFrameProtector.mysql){

    companion object{
        val id = USQLVariable(USQLVariable.Type.INT,true)
        val placePlayer = USQLVariable(USQLVariable.Type.VARCHAR,36)
        val placePlayerName = USQLVariable(USQLVariable.Type.VARCHAR,16)
        val frameId = USQLVariable(USQLVariable.Type.VARCHAR,36)
        val loc = USQLVariable(USQLVariable.Type.VARCHAR,100)
    }

    fun loadData(){
        val rs = select()
        rs.forEach { result ->
            val data = IFData()
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