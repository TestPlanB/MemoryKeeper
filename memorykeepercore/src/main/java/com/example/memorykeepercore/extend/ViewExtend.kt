package com.example.memorykeepercore.extend
import android.view.View
import kotlin.properties.Delegates

internal var View.created: Boolean by Delegates.notNull()
