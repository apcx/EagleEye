@file:Suppress("UNCHECKED_CAST")

package apc.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.collection.ArrayMap
import androidx.databinding.ViewDataBinding
import androidx.databinding.library.baseAdapters.BR
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import kotlin.jvm.internal.CallableReference
import kotlin.reflect.KProperty0
import kotlin.reflect.jvm.internal.impl.descriptors.ValueDescriptor
import kotlin.reflect.jvm.internal.impl.name.Name
import kotlin.reflect.jvm.internal.impl.resolve.DescriptorUtils
import kotlin.reflect.jvm.jvmName

val <T> KProperty0<T>.jClass
    get(): Class<T> {
        val receiver = (this as CallableReference).boundReceiver::class
        return cache.getOrPut("${receiver.jvmName}.$name") {
            Class.forName(
                DescriptorUtils.getFqName(
                    (getProperties(receiver, Name.identifier(name))
                            as List<ValueDescriptor>)[0].type.constructor.declarationDescriptor!!
                ).asString()
            )
        } as Class<T>
    }

private val getProperties = Any::class::class.java.getMethod("getProperties", Name::class.java)
private val cache = mutableMapOf<String, Class<*>>()

abstract class BindFragment<Binding : ViewDataBinding, VM : ViewModel> : Fragment() {

    protected lateinit var binding: Binding
    protected lateinit var vm: VM

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = getOrPut(this::class.jvmName) {
            ::binding.jClass.getMethod(
                "inflate",
                LayoutInflater::class.java,
                ViewGroup::class.java,
                Boolean::class.javaPrimitiveType
            )
        }(null, inflater, container, false) as Binding
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val clazz = ::vm.jClass
        if (!Modifier.isAbstract(clazz.modifiers)) {
            vm = ViewModelProviders.of(this)[clazz]
            binding.setVariable(BR.vm, vm)
        }
    }

    companion object : ArrayMap<String, Method>()
}

fun <T> MutableLiveData<T>.update(newValue: T) {
    if (value != newValue) value = newValue
}