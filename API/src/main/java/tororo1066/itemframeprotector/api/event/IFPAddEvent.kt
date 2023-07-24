package tororo1066.itemframeprotector.api.event

import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import tororo1066.itemframeprotector.api.IFData

class IFPAddEvent(
    val data: IFData,
    private var isCancelled: Boolean = false
): Event(), Cancellable {

    companion object {
        private val handlers = HandlerList()
        @JvmStatic
        fun getHandlerList(): HandlerList {
            return handlers
        }
    }

    override fun getHandlers(): HandlerList {
        return getHandlerList()
    }

    override fun isCancelled(): Boolean {
        return isCancelled
    }

    override fun setCancelled(boolean: Boolean) {
        isCancelled = boolean
    }
}