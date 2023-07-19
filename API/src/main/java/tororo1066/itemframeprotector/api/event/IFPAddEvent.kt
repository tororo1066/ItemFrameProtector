package tororo1066.itemframeprotector.api.event

import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class IFPAddEvent(private var isCancelled: Boolean = false): Event(), Cancellable {

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