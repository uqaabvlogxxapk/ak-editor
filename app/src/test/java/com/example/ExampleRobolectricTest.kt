package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.AspectRatio
import com.example.model.TemplateLibrary
import com.example.model.VideoProject
import com.example.ui.editor.formatTimecode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("AK EDITOR", appName)
  }

  @Test
  fun `template library loads valid templates`() {
    val templates = TemplateLibrary.templates
    assertTrue("Template library should contain templates", templates.isNotEmpty())
    val first = templates.first()
    val project = first.createProject()
    assertNotNull("Created template project should not be null", project)
    assertTrue("Project should contain clips", project.clips.isNotEmpty())
  }

  @Test
  fun `timecode formatting produces accurate mm ss format`() {
    val formatted = formatTimecode(65430L)
    assertEquals("01:05.43", formatted)
  }
}
