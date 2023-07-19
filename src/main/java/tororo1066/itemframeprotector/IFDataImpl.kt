package tororo1066.itemframeprotector

import org.bukkit.Location
import tororo1066.itemframeprotector.api.IFData
import java.util.UUID

class IFDataImpl: IFData, Cloneable {

    override lateinit var uuid: UUID
    override lateinit var placePlayer: UUID
    override var placePlayerName = ""
    override lateinit var loc: Location

    public override fun clone(): IFDataImpl {
        val clone = super.clone() as IFDataImpl
        clone.loc = clone.loc.clone()
        return clone
    }

}