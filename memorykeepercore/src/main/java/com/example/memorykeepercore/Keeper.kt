package com.example.memorykeepercore

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.example.memorykeepercore.core.ViewKeeper
import kotlinx.coroutines.*
import java.lang.ref.ReferenceQueue
import java.lang.ref.WeakReference

object Keeper {
    // you should call it early
    fun init(application: Application) {
        application.registerActivityLifecycleCallbacks(object :
            Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

            }

            override fun onActivityStarted(activity: Activity) {

            }

            override fun onActivityResumed(activity: Activity) {

            }

            override fun onActivityPaused(activity: Activity) {

            }

            override fun onActivityStopped(activity: Activity) {

            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

            }

            override fun onActivityDestroyed(activity: Activity) {
                val queue = ReferenceQueue<Activity>()
                val weak = WeakReference<Activity>(activity, queue)
                GlobalScope.launch(Dispatchers.Default) {
                    startWatch(queue, weak)
                }


            }

        })
    }


    suspend fun startWatch(
        queue: ReferenceQueue<out Activity>,
        weakReference: WeakReference<out Activity>
    ) {
        delay(10000)
        Log.i("startWatch", "delay is over")
        queue.poll() ?: run {
            weakReference.get()?.let { activity ->
                withContext(Dispatchers.Main) {
                    dealActivity(activity)
                    dealActivityFragment(activity)
                }
            }
        }
    }

    private fun dealActivityFragment(activity: Activity) {
        if (activity is FragmentActivity) {
            val fragments = activity.supportFragmentManager.fragments
            for (fragment in fragments) {
                fragment.view?.let { ViewKeeper.clearMemory(it) }
            }
        }

    }

    private fun dealActivity(activity: Activity) {
        val decorView = activity.window.decorView
        ViewKeeper.clearMemory(decorView)
    }


}