package de.markusressel.typedpreferencesdemo

import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import de.markusressel.typedpreferences.PreferenceItem
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by Markus on 21.07.2017.
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class PreferencesHandlerTest {

    private lateinit var underTest: PreferenceHandler

    @Rule
    @JvmField
    var mActivityRule = ActivityTestRule(MainActivity::class.java)

    @Before
    fun before() {
        underTest = PreferenceHandler(mActivityRule.activity)

        underTest.clear(PreferenceItem(R.string.key_test, 0))
    }

    @Test
    fun testTypeString() {
        val defaultValue = ""
        val preferenceItem = PreferenceItem(R.string.key_test, defaultValue)

        val value = underTest.getValue(preferenceItem)
        assertEquals("Expected default value but got something different", defaultValue, value)

        val newValue = "newValue"
        underTest.setValue(preferenceItem, newValue)

        assertEquals("Invalid new value", newValue, underTest.getValue(preferenceItem))
    }

    @Test
    fun testTypeInteger() {
        val defaultValue = Integer.MIN_VALUE
        val preferenceItem = PreferenceItem(R.string.key_test, defaultValue)

        val value = underTest.getValue(preferenceItem)
        assertEquals("Expected default value but got something different", defaultValue, value)

        val newValue = Integer.MAX_VALUE
        underTest.setValue(preferenceItem, newValue)

        assertEquals("Invalid new value", newValue, underTest.getValue(preferenceItem))
    }

    @Test
    fun testTypeLong() {
        val defaultValue = java.lang.Long.MIN_VALUE
        val preferenceItem = PreferenceItem(R.string.key_test, defaultValue)

        val value = underTest.getValue(preferenceItem)
        assertEquals("Expected default value but got something different", defaultValue, value)

        val newValue = java.lang.Long.MAX_VALUE
        underTest.setValue(preferenceItem, newValue)

        assertEquals("Invalid new value", newValue, underTest.getValue(preferenceItem))
    }

    @Test
    fun testTypeFloat() {
        val defaultValue = java.lang.Float.MIN_VALUE
        val preferenceItem = PreferenceItem(R.string.key_test, defaultValue)

        val value = underTest.getValue(preferenceItem)
        assertEquals("Expected default value but got something different", defaultValue, value)

        val newValue = java.lang.Float.MAX_VALUE
        underTest.setValue(preferenceItem, newValue)

        assertEquals("Invalid new value", newValue, underTest.getValue(preferenceItem))
    }

    @Test
    fun testTypeBoolean() {
        val defaultValue = java.lang.Boolean.FALSE
        val preferenceItem = PreferenceItem(R.string.key_test, defaultValue)

        val value = underTest.getValue(preferenceItem)
        assertEquals("Expected default value but got something different", defaultValue, value)

        val newValue = java.lang.Boolean.TRUE
        underTest.setValue(preferenceItem, newValue!!)

        assertEquals("Invalid new value", newValue, underTest.getValue(preferenceItem))
    }

    @Test
    fun testTypeClass_Empty() {
        val defaultValue = TestClass()
        val preferenceItem = PreferenceItem(R.string.key_test, defaultValue)

        val value = underTest.getValue(preferenceItem)
        assertEquals("Expected default value but got something different", defaultValue, value)

        val newValue = TestClass()
        underTest.setValue(preferenceItem, newValue)

        assertEquals("Invalid new value", newValue, underTest.getValue(preferenceItem))
    }

    @Test
    fun testTypeClass_Full() {
        val defaultValue = TestClass(
                false, java.lang.Boolean.TRUE,
                Integer.MIN_VALUE, Integer.MAX_VALUE,
                java.lang.Long.MIN_VALUE, java.lang.Long.MAX_VALUE,
                "TEST")
        val preferenceItem = PreferenceItem(R.string.key_test, defaultValue)

        val value = underTest.getValue(preferenceItem)
        assertEquals("Expected default value but got something different", defaultValue, value)

        val newValue = TestClass()
        underTest.setValue(preferenceItem, newValue)

        assertEquals("Invalid new value", newValue, underTest.getValue(preferenceItem))
    }

    private data class TestClass(
            private val boolBase: Boolean = false,
            private val boolClass: Boolean? = null,

            private val integerBase: Int = 0,
            private val integerClass: Int? = null,

            private val longBase: Long = 0,
            private val longClass: Long? = null,

            private val text: String? = null
    )

}
