/*
 * Copyright 2017 Google Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.ar.core.codelab.common.helpers

import android.app.Activity
import android.support.design.widget.BaseTransientBottomBar
import android.support.design.widget.Snackbar
import android.view.View
import android.widget.TextView

/**
 * Helper to manage the sample snackbar. Hides the Android boilerplate code, and exposes simpler
 * methods.
 */
class SnackbarHelper {
    companion object {
        const val BACKGROUND_COLOR = 0xbf323232
    }
  private var messageSnackbar: Snackbar? = null
  private enum class DismissBehavior { HIDE, SHOW, FINISH }
  private var maxLines = 2
  private var lastMessage = ""

  private fun isShowing(): Boolean {
    return messageSnackbar != null
  }

  /** Shows a snackbar with a given message. */
  fun showMessage(activity: Activity, message: String) {
    if (message != "" && (!isShowing() || lastMessage != message)) {
      lastMessage = message
      show(activity, message, DismissBehavior.HIDE);
    }
  }

  /** Shows a snackbar with a given message, and a dismiss button. */
  fun showMessageWithDismiss(activity: Activity, message: String) {
    show(activity, message, DismissBehavior.SHOW);
  }

  /**
   * Shows a snackbar with a given error message. When dismissed, will finish the activity. Useful
   * for notifying errors, where no further interaction with the activity is possible.
   */
  fun showError(activity: Activity, errorMessage: String) {
    show(activity, errorMessage, DismissBehavior.FINISH);
  }

  /**
   * Hides the currently showing snackbar, if there is one. Safe to call from any thread. Safe to
   * call even if snackbar is not shown.
   */
  fun hide(activity: Activity) {
    if (!isShowing()) {
      return;
    }
    lastMessage = "";
    val messageSnackbarToHide = messageSnackbar
    messageSnackbar = null
    activity.runOnUiThread(
        Runnable() {
            messageSnackbarToHide?.dismiss()
        })
  }

  fun setMaxLines(lines: Int) {
    maxLines = lines
  }

  private fun show(
      activity: Activity, message: String, dismissBehavior: DismissBehavior) {
    activity.runOnUiThread(
        Runnable() {
            messageSnackbar =
                Snackbar.make(
                    activity.findViewById(android.R.id.content),
                    message,
                    Snackbar.LENGTH_INDEFINITE);
            messageSnackbar?.view?.setBackgroundColor(BACKGROUND_COLOR.toInt())
            if (dismissBehavior != DismissBehavior.HIDE) {
                messageSnackbar?.setAction(
                    "Dismiss"
                ) { messageSnackbar?.dismiss() };
                if (dismissBehavior == DismissBehavior.FINISH) {
                    messageSnackbar?.addCallback(
                        object: BaseTransientBottomBar.BaseCallback<Snackbar>() {
                            override fun onDismissed(transientBottomBar: Snackbar, event: Int) {
                                super.onDismissed(transientBottomBar, event)
                                activity.finish()
                            }
                        });
                }
            }
            messageSnackbar?.view?.findViewById<TextView>(android.support.design.R.id.snackbar_text)?.maxLines =
                maxLines
            messageSnackbar?.show();
        })
  }
}
