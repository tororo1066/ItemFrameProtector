package tororo1066.itemframeprotector

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Server
import java.sql.DriverManager
import java.util.UUID

object IFConvert {

    private fun hexDecode(hexValue: String): String {
        var textValue = ""
        if (hexValue.startsWith("#")) {
            var ix = 1
            while (ix < hexValue.length) {
                textValue = try {
                    textValue + hexValue.substring(ix, ix + 2).toInt(16).toChar()
                } catch (oops: Exception) {
                    return " "
                }
                ix += 2
            }
        }
        return textValue
    }

    fun convert(){
        try {
                Class.forName("org.sqlite.JDBC")
                val connection = DriverManager.getConnection("jdbc:sqlite:Fredashay.db")
                val stmt = connection.createStatement()
                val rs = stmt.executeQuery("select * from itemframe")
                while (rs.next()){
                    val frameId = UUID.fromString(hexDecode(rs.getString("frame")))
                    val playerId = UUID.fromString(hexDecode(rs.getString("player")))
                    if (ItemFrameProtector.itemFrameData.containsKey(frameId))continue
                    val frameEntity = Bukkit.getEntity(frameId)?:continue
                    val player = Bukkit.getOfflinePlayer(playerId)
                    player.name?:continue
                    val loc = frameEntity.location.toBlockLocation()
                    loc.yaw = 0f
                    loc.pitch = 0f
                    val data = IFDataImpl()
                    data.uuid = frameId
                    data.loc = loc
                    data.placePlayer = playerId
                    data.placePlayerName = player.name!!
                    ItemFrameProtector.mysql.execute("insert into protect_id (placePlayer, placePlayerName, frameId, loc) values ('${playerId}', '${player.name!!}', '${frameId}', '${loc.world.name},${loc.blockX},${loc.blockY},${loc.blockZ}')")
                }
                Bukkit.broadcast(Component.text(ItemFrameProtector.prefix + "§aコンバートが完了しました"), Server.BROADCAST_CHANNEL_ADMINISTRATIVE)

        }catch (e : Exception){
            Bukkit.getLogger().warning("IFP Error")
            e.printStackTrace()
        }
    }


}