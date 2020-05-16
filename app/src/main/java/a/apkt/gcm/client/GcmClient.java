package a.apkt.gcm.client;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import a.apkt.AutentUserNameActivity;
import a.apkt.LoginActivity;
import a.apkt.R;
import a.apkt.service.UserMsgService;

public class GcmClient {

	public static final String EXTRA_MESSAGE = "message";
	public static final String PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";
	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

	/**
	 * Substitute you own sender ID here. This is the PROJECT NUMBER you got
	 * from the API Console, as described in "Getting Started."
	 */
	String SENDER_ID = "602228333377";

	/**
	 * Tag used on log messages.
	 */
	static final String TAG = "GCM Demo";

	TextView mDisplay;
	GoogleCloudMessaging gcm;
	AtomicInteger msgId = new AtomicInteger();
	Context context;
	Activity activity;

	String regid;

	private RegisterInBackgroundAsyncTask registerInBackgroundAsyncTask;

	public GcmClient(Context context, Activity activity) {
		this.context = context;
		this.activity = activity;
	}

	public String checkOrRegisterId() {
		// Check device for Play Services APK. If check succeeds, proceed with
		// GCM registration.
		if (checkPlayServices()) {
			gcm = GoogleCloudMessaging.getInstance(context);
			regid = getRegistrationId(context);

			if (TextUtils.isEmpty(regid)) {
				registerInBackground();
				return null;
			} else {
				return regid;
			}
		} else {
			Log.i(TAG, "No valid Google Play Services APK found.");
			return null;
		}
	}

	/**
	 * Check the device to make sure it has the Google Play Services APK. If it
	 * doesn't, display a dialog that allows users to download the APK from the
	 * Google Play Store or enable it in the device's system settings.
	 */
	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(context);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, activity,
						PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Log.i(TAG, "This device is not supported.");
				UserMsgService.showDialogFinish(activity,
						R.string.alertdialog_google_play_services_title,
						R.string.alertdialog_google_play_services);
				// showDialog already has finish()
				// activity.finish();
			}
			return false;
		}
		return true;
	}

	/**
	 * Gets the current registration ID for application on GCM service, if there
	 * is one.
	 * <p>
	 * If result is empty, the app needs to register.
	 *
	 * @return registration ID, or empty string if there is no existing
	 *         registration ID.
	 */
	private String getRegistrationId(Context context) {
		final SharedPreferences prefs = getGcmPreferences(context);
		String registrationId = prefs.getString(PROPERTY_REG_ID, "");
		if (TextUtils.isEmpty(registrationId)) {
			Log.i(TAG, "Registration not found.");
			return "";
		}
		// Check if app was updated; if so, it must clear the registration ID
		// since the existing regID is not guaranteed to work with the new
		// app version.
		int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION,
				Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion) {
			Log.i(TAG, "App version changed.");
			return "";
		}
		return registrationId;
	}

	/**
	 * Stores the registration ID and the app versionCode in the application's
	 * {@code SharedPreferences}.
	 *
	 * @param context
	 *            application's context.
	 * @param regId
	 *            registration ID
	 */
	private void storeRegistrationId(Context context, String regId) {
		final SharedPreferences prefs = getGcmPreferences(context);
		int appVersion = getAppVersion(context);
		Log.i(TAG, "Saving regId on app version " + appVersion);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(PROPERTY_REG_ID, regId);
		editor.putInt(PROPERTY_APP_VERSION, appVersion);
		editor.commit();
	}

	/**
	 * @return Application's {@code SharedPreferences}.
	 */
	private SharedPreferences getGcmPreferences(Context context) {
		// This sample app persists the registration ID in shared preferences,
		// but
		// how you store the regID in your app is up to you.
		return context.getSharedPreferences(GcmClient.class.getSimpleName(),
				Context.MODE_PRIVATE);
	}

	/**
	 * @return Application's version code from the {@code PackageManager}.
	 */
	private static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	public String unregisterId(){

		try {
			gcm.unregister();
			return context.getString(R.string.ok);
		} catch (IOException e) {
			e.printStackTrace();
			return "failed";
		}

	}

	/**
	 * Sends the registration ID to your server over HTTP, so it can use
	 * GCM/HTTP or CCS to send messages to your app. Not needed for this demo
	 * since the device sends upstream messages to a server that echoes back the
	 * message using the 'from' address in the message.
	 */
	// already being send through RegisterInBackgroundAsyncTask's onPostExecute
	private void sendRegistrationIdToBackend() {
		// Your implementation here.
	}

	/**
	 * Registers the application with GCM servers asynchronously.
	 * <p>
	 * Stores the registration ID and the app versionCode in the application's
	 * shared preferences.
	 */
	private void registerInBackground() {
		registerInBackgroundAsyncTask = new RegisterInBackgroundAsyncTask();
		registerInBackgroundAsyncTask.execute(null, null, null);
	}

	public class RegisterInBackgroundAsyncTask extends
			AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			String msg = "";
			try {
				if (gcm == null) {
					gcm = GoogleCloudMessaging.getInstance(context);
				}
				regid = gcm.register(SENDER_ID);
				msg = "Device registered, registration ID=" + regid;

				// You should send the registration ID to your server over HTTP,
				// so it
				// can use GCM/HTTP or CCS to send messages to your app.
				// commented because it's being send through onPostExecute
				// sendRegistrationIdToBackend();

				// For this demo: we don't need to send it because the device
				// will send
				// upstream messages to a server that echo back the message
				// using the
				// 'from' address in the message.

				// Persist the regID - no need to register again.
				storeRegistrationId(context, regid);
			} catch (IOException ex) {
				msg = "Error :" + ex.getMessage();
				// If there is an error, don't just keep trying to register.
				// Require the user to click a button again, or perform
				// exponential back-off.
			}
			return msg;
		}

		@Override
		protected void onPostExecute(String msg) {
			// mDisplay.append(msg + "\n");

			String activityName = activity.getComponentName().getClassName();

			if (activityName.equals("a.apkt.AutentUserNameActivity")) {
				AutentUserNameActivity autentUserNameActivity = (AutentUserNameActivity) activity;
				autentUserNameActivity.getRegNewUserAsyncTask().execute(regid);
			} else if (activityName.equals("a.apkt.LoginActivity")) {
				LoginActivity loginActivity = (LoginActivity) activity;
				loginActivity.getLoginAsyncTask().execute(regid);
			}

		}
	}

	public RegisterInBackgroundAsyncTask getRegisterInBackgroundAsyncTask() {
		return registerInBackgroundAsyncTask;
	}

	// Send an upstream message.
	// public void onClick(final View view) {
	//
	// if (view == findViewById(R.id.send)) {
	// new AsyncTask<Void, Void, String>() {
	// @Override
	// protected String doInBackground(Void... params) {
	// String msg = "";
	// try {
	// Bundle data = new Bundle();
	// data.putString("my_message", "Hello World");
	// data.putString("my_action", "com.google.android.gcm.demo.app.ECHO_NOW");
	// String id = Integer.toString(msgId.incrementAndGet());
	// gcm.send(SENDER_ID + "@gcm.googleapis.com", id, data);
	// msg = "Sent message";
	// } catch (IOException ex) {
	// msg = "Error :" + ex.getMessage();
	// }
	// return msg;
	// }
	//
	// @Override
	// protected void onPostExecute(String msg) {
	// mDisplay.append(msg + "\n");
	// }
	// }.execute(null, null, null);
	// } else if (view == findViewById(R.id.clear)) {
	// mDisplay.setText("");
	// }
	// }

	// check for play services on resume of gcm client activity
	// @Override
	// protected void onResume() {
	// super.onResume();
	// // Check device for Play Services APK.
	// checkPlayServices();
	// }

	// @Override
	// protected void onDestroy() {
	// super.onDestroy();
	// }

}
