package tororo1066.itemframeprotector.api.event

import org.bukkit.entity.Entity
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import tororo1066.itemframeprotector.api.IFData

class IFPInteractEvent(
    val data: IFData,
    val entity: Entity?,
    val ifpCause: IFPCause,
    private var isCancelled: Boolean = false
): Event(), Cancellable {

    private val handlers = HandlerList()

    override fun getHandlers(): HandlerList {
        return handlers
    }

    override fun isCancelled(): Boolean {
        return isCancelled
    }

    override fun setCancelled(boolean: Boolean) {
        isCancelled = boolean
    }
}