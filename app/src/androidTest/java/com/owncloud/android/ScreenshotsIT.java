/*
 * Nextcloud - Android Client
 *
 * SPDX-FileCopyrightText: 2018 Tobias Kaminsky <tobias@kaminsky.me>
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */
package com.owncloud.android;

import android.content.Intent;

import com.owncloud.android.lib.common.operations.RemoteOperationResult;
import com.owncloud.android.operations.CreateFolderOperation;
import com.owncloud.android.operations.common.SyncOperation;
import com.owncloud.android.ui.activity.ConflictsResolveActivity;
import com.owncloud.android.ui.activity.FileDisplayActivity;
import com.owncloud.android.ui.activity.SettingsActivity;
import com.owncloud.android.ui.activity.SyncedFoldersActivity;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.PreferenceMatchers;
import androidx.test.filters.LargeTest;
import tools.fastlane.screengrab.Screengrab;
import tools.fastlane.screengrab.UiAutomatorScreenshotStrategy;
import tools.fastlane.screengrab.locale.LocaleTestRule;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.AnyOf.anyOf;
import static org.junit.Assert.assertTrue;

@LargeTest
@RunWith(JUnit4.class)
public class ScreenshotsIT extends AbstractOnServerIT {
    @ClassRule
    public static final LocaleTestRule localeTestRule = new LocaleTestRule();

    @BeforeClass
    public static void beforeScreenshot() {
        Screengrab.setDefaultScreenshotStrategy(new UiAutomatorScreenshotStrategy());
    }

    @Test
    public void gridViewScreenshot() {
        ActivityScenario<FileDisplayActivity> sutScenario = ActivityScenario.launch(new Intent(targetContext, FileDisplayActivity.class));
        sutScenario.onActivity(sut -> onIdleSync(() -> {
            onView(anyOf(withText(R.string.action_switch_grid_view), withId(R.id.switch_grid_view_button))).perform(click());
            shortSleep();
            Screengrab.screenshot("01_gridView");
            onView(anyOf(withText(R.string.action_switch_list_view), withId(R.id.switch_grid_view_button))).perform(click());
            Assert.assertTrue(true); // if we reach this, everything is ok
        }));
    }

    @Test
    public void listViewScreenshot() {
        String path = "/Camera/";

        // folder does not exist yet
        if (getStorageManager().getFileByEncryptedRemotePath(path) == null) {
            SyncOperation syncOp = new CreateFolderOperation(path, user, targetContext, getStorageManager());
            RemoteOperationResult result = syncOp.execute(client);

            assertTrue(result.isSuccess());
        }

        ActivityScenario<FileDisplayActivity> sutScenario = ActivityScenario.launch(new Intent(targetContext, FileDisplayActivity.class));
        sutScenario.onActivity(sut -> onIdleSync(() -> {
            // go into work folder
            onView(withId(R.id.list_root)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
            Screengrab.screenshot("02_listView");
            Assert.assertTrue(true); // if we reach this, everything is ok
        }));
    }

    @Test
    public void drawerScreenshot() {
        ActivityScenario<FileDisplayActivity> sutScenario = ActivityScenario.launch(new Intent(targetContext, FileDisplayActivity.class));
        sutScenario.onActivity(sut -> onIdleSync(() -> {
            onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
            Screengrab.screenshot("03_drawer");
            onView(withId(R.id.drawer_layout)).perform(DrawerActions.close());
            Assert.assertTrue(true); // if we reach this, everything is ok
        }));
    }

    @Test
    public void multipleAccountsScreenshot() {
        ActivityScenario<FileDisplayActivity> sutScenario = ActivityScenario.launch(new Intent(targetContext, FileDisplayActivity.class));
        sutScenario.onActivity(sut -> onIdleSync(() -> {
            onView(withId(R.id.switch_account_button)).perform(click());
            Screengrab.screenshot("04_accounts");
            pressBack();
            Assert.assertTrue(true); // if we reach this, everything is ok
        }));
    }

    @Test
    public void autoUploadScreenshot() {
        ActivityScenario<SyncedFoldersActivity> sutScenario = ActivityScenario.launch(new Intent(targetContext, SyncedFoldersActivity.class));
        sutScenario.onActivity(sut -> onIdleSync(() -> {
            Screengrab.screenshot("05_autoUpload");
            Assert.assertTrue(true); // if we reach this, everything is ok
        }));
    }

    @Test
    public void davdroidScreenshot() {
        ActivityScenario<SettingsActivity> sutScenario = ActivityScenario.launch(new Intent(targetContext, SettingsActivity.class));
        sutScenario.onActivity(sut -> onIdleSync(() -> {
            onData(PreferenceMatchers.withTitle(R.string.prefs_category_more)).perform(ViewActions.scrollTo());
            shortSleep();
            Screengrab.screenshot("06_davdroid");
            Assert.assertTrue(true); // if we reach this, everything is ok
        }));
    }
}
