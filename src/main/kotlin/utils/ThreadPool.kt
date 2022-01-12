package utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object ThreadPool {

    fun launch(runnable: Runnable){
        GlobalScope.launch(Dispatchers.IO) {
            runnable.run()
        }
    }

    fun runOnUI(runnable: Runnable){
        GlobalScope.launch(Dispatchers.Main) {
            runnable.run()
        }
    }
}