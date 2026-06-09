package com.ucb.mapexplorer.core.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import platform.Network.nw_path_get_status
import platform.Network.nw_path_monitor_create
import platform.Network.nw_path_monitor_set_update_handler
import platform.Network.nw_path_monitor_start
import platform.Network.nw_path_status_satisfied
import platform.darwin.dispatch_get_main_queue

class IosConnectivityObserver : ConnectivityObserver {

    // NWPathMonitor se crea via nw_path_monitor_create() en Kotlin/Native
    private val monitor = nw_path_monitor_create()
    private val _statusFlow = MutableStateFlow(ConnectivityObserver.Status.Available)

    init {
        nw_path_monitor_set_update_handler(monitor) { path ->
            val status = if (nw_path_get_status(path) == nw_path_status_satisfied) {
                ConnectivityObserver.Status.Available
            } else {
                ConnectivityObserver.Status.Unavailable
            }
            _statusFlow.value = status
        }
        nw_path_monitor_start(monitor, dispatch_get_main_queue())
    }

    override fun observe(): Flow<ConnectivityObserver.Status> = _statusFlow.asStateFlow()
}
