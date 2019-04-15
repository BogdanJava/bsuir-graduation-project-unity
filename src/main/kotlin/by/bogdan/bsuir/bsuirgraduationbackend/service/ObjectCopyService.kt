package by.bogdan.bsuir.bsuirgraduationbackend.service

import by.bogdan.bsuir.bsuirgraduationbackend.utils.CustomReflectionUtils
import org.springframework.stereotype.Service
import org.springframework.util.ReflectionUtils

@Service
class ObjectCopyService(private val reflectionUtils: CustomReflectionUtils) {
    fun noOverwriteTargetCopy(source: Any, target: Any) {
        val sourceClass = source::class.java
        ReflectionUtils.doWithFields(sourceClass) { field ->
            val fieldName = field.name
            val targetField = ReflectionUtils.findField(target::class.java, fieldName)
            val sourceField = ReflectionUtils.findField(sourceClass, fieldName)
            if (targetField != null && sourceField != null) {
                val targetFieldValue = reflectionUtils.readField(targetField, target)
                if (targetFieldValue == null || targetFieldValue == "") {
                    val sourceFieldValue = reflectionUtils.readField(sourceField, source)
                    reflectionUtils.setField(targetField, target, sourceFieldValue)
                }
            }
        }
    }

    fun patchTarget(target: Any, patch: Any) {
        val patchClass = patch::class.java
        ReflectionUtils.doWithFields(patchClass) { field ->
            val patchValue = reflectionUtils.readField(field.name, patch)
            if (patchValue != null) {
                reflectionUtils.setField(field.name, target, patchValue)
            }
        }
    }
}