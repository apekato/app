package a.apkt.service;

import android.content.Context;
import android.content.SharedPreferences;

import a.apkt.backingbean.LoginBB;
import a.apkt.sqlite.LoginSqlite;

public class LoginOrSignupService {

    // Handle to SharedPreferences for Switch onToggleClicked
    private SharedPreferences mPrefs;
    // Handle to a SharedPreferences editor
    private SharedPreferences.Editor mEditor;

    // Name of shared preferences repository that stores persistent state
    public static final String SHARED_PREFERENCES =
            "android.loginOrSignupService.SHARED_PREFERENCES";

    // Key for storing the "updates requested" flag in shared preferences
    public static final String SMSPASSWORD_SIMSERIALNUMREG =
            ".android.loginOrSignupService.SMSPASSWORD_SIMSERIALNUMREG";

    public static final String USERID =
            ".android.loginOrSignupService.USERID";

    public static final String MOBILENUM_SIMSERIALNUMREG =
            ".android.loginOrSignupService.MOBILENUM_SIMSERIALNUMREG";

    public static final String USEREMAIL =
            ".android.loginOrSignupService.USEREMAIL";

    public static final String USERMOBILENUM =
            ".android.loginOrSignupService.USERMOBILENUM";

    public static final String USERSMSPASSWORD =
            ".android.loginOrSignupService.USERSMSPASSWORD";

    public static final String USERPASSWORD =
            ".android.loginOrSignupService.USERPASSWORD";

    public static final String ACTIVITYNAME =
            ".android.loginOrSignupService.ACTIVITYNAME";

    public static final String LOGINJSON =
            ".android.loginOrSignupService.LOGINJSON";

    public static final String USERRESETPASSWORD_EMAIL =
            ".android.loginOrSignupService.USERRESETPASSWORD_EMAIL";

    public static final String USERRESETPASSWORD_CODE =
            ".android.loginOrSignupService.USERRESETPASSWORD_CODE";

    public LoginOrSignupService(Context context) {
        // Open Shared Preferences
        mPrefs = context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        // Get an editor
        mEditor = mPrefs.edit();
    }

    public static boolean sqliteAddUserLogin(
            LoginSqlite loginSqlite,
            long id,
            String username,
            String email,
            String password) {
        loginSqlite.deleteUserLogin();
        // if sqlite is empty, it will add user login info
//        if (loginSqlite.getUserLoginCount(loginSqlite) == 0) {
            // adds user login info to sqlite
            loginSqlite.addUserLogin(new LoginBB(id, username, email, password));
            return true;
//        } else {
//            return false;
//        }
    }

    public SharedPreferences getmPrefs() {
        return mPrefs;
    }

    public void setmPrefs(SharedPreferences mPrefs) {
        this.mPrefs = mPrefs;
    }

    public SharedPreferences.Editor getmEditor() {
        return mEditor;
    }

    public void setmEditor(SharedPreferences.Editor mEditor) {
        this.mEditor = mEditor;
    }


}

