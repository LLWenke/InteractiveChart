package com.wk.view.ext

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * activity
 */
inline fun <reified VB : ViewBinding> Activity.binding() = lazy {
    inflateBinding<VB>(layoutInflater).apply { setContentView(root) }
}

inline fun <reified VB : ViewBinding> Dialog.binding() = lazy {
    inflateBinding<VB>(layoutInflater).apply { setContentView(root) }
}

inline fun <reified VB : ViewBinding> PopupWindow.binding(context: Context) = lazy {
    inflateBinding<VB>(LayoutInflater.from(context))
}

inline fun <reified VB : ViewBinding> ViewGroup.binding(attachToRoot: Boolean = false) = lazy {
    inflateBinding<VB>(LayoutInflater.from(context), this, attachToRoot)
}

inline fun <reified VB : ViewBinding> inflateBinding(
    layoutInflater: LayoutInflater,
    root: ViewGroup?,
    attachToRoot: Boolean
) = try {
    VB::class.java.getMethod(
        "inflate",
        LayoutInflater::class.java,
        ViewGroup::class.java
    ).invoke(
        null,
        layoutInflater,
        root
    ) as VB
} catch (e: Exception) {
    VB::class.java.getMethod(
        "inflate",
        LayoutInflater::class.java,
        ViewGroup::class.java,
        Boolean::class.java
    ).invoke(
        null,
        layoutInflater,
        root,
        attachToRoot
    ) as VB
}

inline fun <reified VB : ViewBinding> inflateBinding(
    layoutInflater: LayoutInflater
) =
    VB::class.java.getMethod(
        "inflate",
        LayoutInflater::class.java,
    ).invoke(
        null,
        layoutInflater,
    ) as VB


/**
 * fragment
 */
inline fun <reified VB : ViewBinding> Fragment.binding() =
    FragmentBindingDelegate(VB::class.java)

class FragmentBindingDelegate<VB : ViewBinding>(
    private val clazz: Class<VB>
) : ReadOnlyProperty<Fragment, VB> {

    private var isInitialized = false
    private var _binding: VB? = null
    private val binding: VB get() = _binding!!

    override fun getValue(thisRef: Fragment, property: KProperty<*>): VB {
        if (!isInitialized) {
            thisRef.viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
                override fun onDestroy(owner: LifecycleOwner) {
                    _binding = null
                }
            })
            _binding = clazz.getMethod("bind", View::class.java)
                .invoke(null, thisRef.requireView()) as VB
            isInitialized = true
        }
        return binding
    }
}