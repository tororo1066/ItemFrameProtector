package tororo1066.itemframeprotector.api

import org.bukkit.Location
import java.util.*

interface IFData {
    var uuid: UUID
    var placePlayer: UUID
    var placePlayerName: String
    var loc: Location
}