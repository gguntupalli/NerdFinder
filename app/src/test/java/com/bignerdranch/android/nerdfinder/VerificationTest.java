package com.bignerdranch.android.nerdfinder;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Created by gguntupalli on 24/01/17.
 */

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 23, constants = BuildConfig.class)
public class VerificationTest {
    @Test
    public void testRoboelectricSetupWorks(){
        assertThat(1, equalTo(1));
    }
}
