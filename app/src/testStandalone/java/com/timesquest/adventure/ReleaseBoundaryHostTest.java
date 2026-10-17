package com.timesquest.adventure;

import static org.junit.Assert.*;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import java.util.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.*;
import org.robolectric.annotation.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
@LooperMode(LooperMode.Mode.PAUSED)
public class ReleaseBoundaryHostTest {
  @Test
  public void standaloneCannotAcquireInternetPermissionAndBackupIsDisabled() throws Exception {
    var c = RuntimeEnvironment.getApplication();
    var info =
        c.getPackageManager().getPackageInfo(c.getPackageName(), PackageManager.GET_PERMISSIONS);
    assertFalse(Arrays.asList(info.requestedPermissions).contains("android.permission.INTERNET"));
    assertEquals(0, c.getApplicationInfo().flags & ApplicationInfo.FLAG_ALLOW_BACKUP);
  }

  @Test
  public void releaseCannotUseDebugPremiumOrStartMicrophoneOnLaunch() {
    RuntimeEnvironment.getApplication()
        .getSharedPreferences("timesquest-local-v1", 0)
        .edit()
        .clear()
        .commit();
    try (var controller = Robolectric.buildActivity(MainActivity.class).setup()) {
      MainActivity app = controller.get();
      assertNull(app.recognizer);
      assertFalse(app.listening);
      assertEquals(BuildConfig.DEBUG, app.premium());
      assertEquals(BuildConfig.DEBUG, app.canPlayMode(3));
      assertTrue(app.canPlayMode(0));
    }
  }
}
