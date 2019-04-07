package apc.eagle.tdr

import com.alibaba.excel.ExcelReader
import com.alibaba.excel.context.AnalysisContext
import com.alibaba.excel.event.AnalysisEventListener
import com.alibaba.excel.metadata.BaseRowModel
import com.alibaba.excel.metadata.Sheet
import kotlin.jvm.internal.CallableReference
import kotlin.reflect.KProperty0
import kotlin.reflect.jvm.internal.impl.descriptors.ValueDescriptor
import kotlin.reflect.jvm.internal.impl.name.Name
import kotlin.reflect.jvm.internal.impl.resolve.DescriptorUtils
import kotlin.reflect.jvm.jvmName

abstract class Table<T : BaseRowModel> : AnalysisEventListener<T>() {

    internal open val file = ""
    internal open val table = 1
    private val type get(): T? = null

    internal fun load() {
        @Suppress("SpellCheckingInspection")
        Tdr.root.resolve("$file.xlsx").toFile().inputStream()
            .use { ExcelReader(it, null, this).read(Sheet(table, 1, ::type.jClass)) }
    }

    override fun doAfterAllAnalysed(context: AnalysisContext) {}
}

@Suppress("UNCHECKED_CAST")
val <T> KProperty0<T>.jClass
    get(): Class<T> {
        val receiver = (this as CallableReference).boundReceiver::class
        return cache.getOrPut("${receiver.jvmName}.$name") {
            Class.forName(
                DescriptorUtils.getFqName(
                    (getProperties(
                        receiver,
                        Name.identifier(name)
                    ) as List<ValueDescriptor>)[0].type.constructor.declarationDescriptor!!
                ).asString()
            )
        } as Class<T>
    }

private val getProperties = Any::class::class.java.getMethod("getProperties", Name::class.java)
private val cache = mutableMapOf<String, Class<*>>()