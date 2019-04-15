package by.bogdan.bsuir.bsuirgraduationbackend.service

import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.TestModel1
import by.bogdan.bsuir.bsuirgraduationbackend.utils.CustomReflectionUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

class ObjectCopyServiceTest {

    @Test
    fun testNoOverwriteCopy() {
        val objectCopyService = ObjectCopyService(CustomReflectionUtils())
        val birthday = Date()
        val source = TestModel1("kek", birthday, 100, true)
        val target = TestModel1("lol", null, null, false)
        objectCopyService.noOverwriteTargetCopy(source, target)
        assertEquals(target.name, "lol")
        assertEquals(target.birthday, birthday)
        assertEquals(target.score, 100)
        assertEquals(target.testBoolean, false)
    }

    @Test
    fun testPatchTarget() {
        val objectCopyService = ObjectCopyService(CustomReflectionUtils())
        val birthday = Date()
        val target = TestModel1("bogdan", birthday, 200, false)
        val patch = TestModel1(null, null, 100, null)
        objectCopyService.patchTarget(target, patch)
        assertEquals(target.name, "bogdan")
        assertEquals(target.birthday, birthday)
        assertEquals(target.score, 100) // got updated
        assertEquals(target.testBoolean, false)
    }
}