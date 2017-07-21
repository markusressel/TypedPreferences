package de.markusressel.typedpreferencesdemo;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.markusressel.typedpreferences.PreferenceItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static junit.framework.Assert.assertEquals;

/**
 * Created by Markus on 21.07.2017.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class PreferencesHandlerTest {

    private PreferenceHandler underTest;

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void before() {
        if (underTest == null) {
            underTest = new PreferenceHandler(mActivityRule.getActivity());
        }
        underTest.clear(new PreferenceItem<>(R.string.key_test, 0));
    }

    @Test
    public void testTypeString() {
        String defaultValue = "";
        PreferenceItem<String> preferenceItem = new PreferenceItem<>(R.string.key_test, defaultValue);

        String value = underTest.getValue(preferenceItem);
        assertEquals("Expected default value but got something different", defaultValue, value);

        String newValue = "newValue";
        underTest.setValue(preferenceItem, newValue);

        assertEquals("Invalid new value", newValue, underTest.getValue(preferenceItem));
    }

    @Test
    public void testTypeInteger() {
        int defaultValue = Integer.MIN_VALUE;
        PreferenceItem<Integer> preferenceItem = new PreferenceItem<>(R.string.key_test, defaultValue);

        int value = underTest.getValue(preferenceItem);
        assertEquals("Expected default value but got something different", defaultValue, value);

        Integer newValue = Integer.MAX_VALUE;
        underTest.setValue(preferenceItem, newValue);

        assertEquals("Invalid new value", newValue, underTest.getValue(preferenceItem));
    }

    @Test
    public void testTypeLong() {
        long defaultValue = Long.MIN_VALUE;
        PreferenceItem<Long> preferenceItem = new PreferenceItem<>(R.string.key_test, defaultValue);

        long value = underTest.getValue(preferenceItem);
        assertEquals("Expected default value but got something different", defaultValue, value);

        Long newValue = Long.MAX_VALUE;
        underTest.setValue(preferenceItem, newValue);

        assertEquals("Invalid new value", newValue, underTest.getValue(preferenceItem));
    }

    @Test
    public void testTypeFloat() {
        float defaultValue = Float.MIN_VALUE;
        PreferenceItem<Float> preferenceItem = new PreferenceItem<>(R.string.key_test, defaultValue);

        float value = underTest.getValue(preferenceItem);
        assertEquals("Expected default value but got something different", defaultValue, value);

        Float newValue = Float.MAX_VALUE;
        underTest.setValue(preferenceItem, newValue);

        assertEquals("Invalid new value", newValue, underTest.getValue(preferenceItem));
    }

    @Test
    public void testTypeBoolean() {
        boolean defaultValue = Boolean.FALSE;
        PreferenceItem<Boolean> preferenceItem = new PreferenceItem<>(R.string.key_test, defaultValue);

        boolean value = underTest.getValue(preferenceItem);
        assertEquals("Expected default value but got something different", defaultValue, value);

        Boolean newValue = Boolean.TRUE;
        underTest.setValue(preferenceItem, newValue);

        assertEquals("Invalid new value", newValue, underTest.getValue(preferenceItem));
    }

    @Test
    public void testTypeClass_Empty() {
        TestClass defaultValue = new TestClass();
        PreferenceItem<TestClass> preferenceItem = new PreferenceItem<>(R.string.key_test, defaultValue);

        TestClass value = underTest.getValue(preferenceItem);
        assertEquals("Expected default value but got something different", defaultValue, value);

        TestClass newValue = new TestClass();
        underTest.setValue(preferenceItem, newValue);

        assertEquals("Invalid new value", newValue, underTest.getValue(preferenceItem));
    }

    @Test
    public void testTypeClass_Full() {
        TestClass defaultValue = new TestClass(
                false, Boolean.TRUE,
                Integer.MIN_VALUE, Integer.MAX_VALUE,
                Long.MIN_VALUE, Long.MAX_VALUE,
                "TEST");
        PreferenceItem<TestClass> preferenceItem = new PreferenceItem<>(R.string.key_test, defaultValue);

        TestClass value = underTest.getValue(preferenceItem);
        assertEquals("Expected default value but got something different", defaultValue, value);

        TestClass newValue = new TestClass();
        underTest.setValue(preferenceItem, newValue);

        assertEquals("Invalid new value", newValue, underTest.getValue(preferenceItem));
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private class TestClass {
        private boolean boolBase;
        private Boolean boolClass;

        private int integerBase;
        private Integer integerClass;

        private long longBase;
        private Long longClass;

        private String text;
    }

}
