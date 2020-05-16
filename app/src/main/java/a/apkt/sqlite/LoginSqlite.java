package a.apkt.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import a.apkt.backingbean.LoginBB;
import a.apkt.model.Login;

public class LoginSqlite extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "apekato";

    // Contacts table name
    private static final String TABLE_NAME = "user_login";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_MOBILE_NUM = "mobile_num";
    private static final String KEY_MOBILE_NUM_STATE = "mobile_num_state";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "pass_word";

    public LoginSqlite(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_USER_LOGIN_TABLE = "CREATE TABLE " + TABLE_NAME +
                "(" +
                KEY_ID + " INTEGER PRIMARY KEY," +
                KEY_USERNAME + " TEXT," +
                KEY_MOBILE_NUM + " TEXT," +
                KEY_MOBILE_NUM_STATE + " TEXT," +
                KEY_EMAIL + " TEXT," +
                KEY_PASSWORD + " TEXT" +
                ")";
        db.execSQL(CREATE_USER_LOGIN_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    /*
     * All CRUD(Create, Read, Update, Delete) Operations
     */
    public void addUserLogin(LoginBB loginBB) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, loginBB.getLoginAux().getId());
        values.put(KEY_USERNAME, loginBB.getLoginAux().getUsername());
//		values.put(KEY_MOBILE_NUM_STATE, loginBB.getMobileNumState());
//		values.put(KEY_MOBILE_NUM, loginBB.getMobileNum());
        values.put(KEY_EMAIL, loginBB.getLoginAux().getEmail());
		values.put(KEY_PASSWORD, loginBB.getLoginAux().getPassWord());

        // Inserting Row
        db.insert(TABLE_NAME, null, values);
        // Closing database connection
        db.close();
    }

    // Getting single contact
    public LoginBB getUserLogin() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                // String table
                TABLE_NAME,
                // String[] columns
                new String[]{
                        KEY_ID,
                        KEY_USERNAME,
//					KEY_MOBILE_NUM_STATE,
//					KEY_MOBILE_NUM,
                        KEY_EMAIL,
					KEY_PASSWORD
                },
                // String selection
                null,
                // String[] selectionArgs
                null,
                // String groupBy
                null,
                // String having
                null,
                // String orderBy
                null,
                // String limit
                null);

        if (cursor != null)
            cursor.moveToFirst();

        LoginBB loginBB = new LoginBB(
                // id
                cursor.getLong(0),
                // username
                cursor.getString(1),
                // email
                cursor.getString(2),
                // password
                cursor.getString(3)
        );

        return loginBB;
    }

    // This method will not be used, but serves as an example for future use
    // Getting All UserLogin
    public List<LoginBB> getAllUserLogin() {
        List<LoginBB> loginBBList = new ArrayList<LoginBB>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                LoginBB loginBB = new LoginBB(
                    cursor.getString(0), // MobileNumState
                    cursor.getString(1), // MobileNum
                    cursor.getString(2)); // PassWord
                // Adding contact to list
                loginBBList.add(loginBB);
            } while (cursor.moveToNext());
        }

        // return contact list
        return loginBBList;
    }

    // Updating single userLogin
    public int updateUserLogin(Login login) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_MOBILE_NUM_STATE, login.getMobileNumState());
        values.put(KEY_MOBILE_NUM, login.getMobileNum());
        values.put(KEY_MOBILE_NUM, login.getPassWord());

        // updating row
        return db.update(
                // String table
                TABLE_NAME,
                // ContentValues values
                values,
                // String whereClause
                null,
                // String[] whereArgs
                null);
    }

    // Deleting single contact
    public void deleteUserLogin() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(
                // String table
                TABLE_NAME,
                // String whereClause
                null,
                // String[] whereArgs
                null);
        db.close();
    }


    // Getting contacts Count
    public int getUserLoginCount(LoginSqlite loginSqlite) {
        Cursor cursor = null;

        try {
            String countQuery = "SELECT  * FROM " + TABLE_NAME;
            SQLiteDatabase db = loginSqlite.getReadableDatabase();
            cursor = db.rawQuery(countQuery, null);
            // return count
            return cursor.getCount();
        } finally {
            cursor.close();
        }
    }

}

