package example.com.fielthyapps.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "fielthyapps.db";
    // VERSI 19 UNTUK MENAMBAHKAN KOLOM FTND 6 PERTANYAAN PADA TABEL SMOKER
    private static final int DATABASE_VERSION = 19;

    // Table Names
    public static final String TABLE_USER = "users";
    public static final String TABLE_MEDCHECK = "medcheck";
    public static final String TABLE_NUTRITION = "nutritiontest";
    public static final String TABLE_FOOD_RECOG = "foodrecognition";
    public static final String TABLE_PHYSICAL = "physicaltest";
    public static final String TABLE_REST = "restpattern";
    public static final String TABLE_SMOKER = "smokertest";
    public static final String TABLE_KALK_MEROKOK = "kalkulator_merokok";
    public static final String TABLE_STRESS = "stresstest";
    public static final String TABLE_BMR = "bmrtest";

    // Dataset Baru Pengganti TKPI
    public static final String TABLE_FOOD_DATA = "food_data";
    public static final String COL_FOOD_ID = "id";
    public static final String COL_FOOD_NAMA = "nama_makanan";
    public static final String COL_FOOD_KALORI = "kalori_kcal";
    public static final String COL_FOOD_PROTEIN = "protein_g";
    public static final String COL_FOOD_KARB = "karbohidrat_g";
    public static final String COL_FOOD_LEMAK = "lemak_g";
    public static final String COL_FOOD_SUMBER = "sumber";

    // User Table Columns
    private static final String COL_USER_UID = "uid";
    private static final String COL_USER_EMAIL = "email";
    private static final String COL_USER_PASSWORD = "password";
    private static final String COL_USER_NAMA = "nama";
    private static final String COL_USER_LOCATION = "location";
    private static final String COL_USER_BIRTHDAY = "birthday";
    private static final String COL_USER_GENDER = "gender";
    private static final String COL_USER_UMUR = "umur";
    private static final String COL_USER_PHOTOURI = "photoUri";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + COL_USER_UID + " TEXT PRIMARY KEY,"
                + COL_USER_EMAIL + " TEXT,"
                + COL_USER_PASSWORD + " TEXT,"
                + COL_USER_NAMA + " TEXT,"
                + COL_USER_LOCATION + " TEXT,"
                + COL_USER_BIRTHDAY + " TEXT,"
                + COL_USER_GENDER + " TEXT,"
                + COL_USER_UMUR + " INTEGER,"
                + COL_USER_PHOTOURI + " TEXT" + ")";
        db.execSQL(CREATE_USER_TABLE);

        String CREATE_MEDCHECK = "CREATE TABLE " + TABLE_MEDCHECK + "(id TEXT PRIMARY KEY, uid TEXT, date TEXT, gender TEXT, berat TEXT, diastolik TEXT, guladarah TEXT, hasilbmi TEXT, lemak TEXT, lingkarperut TEXT, sistolik TEXT, tinggi TEXT)";
        db.execSQL(CREATE_MEDCHECK);

        String CREATE_NUTRITION = "CREATE TABLE " + TABLE_NUTRITION + "(id TEXT PRIMARY KEY, uid TEXT, date TEXT, laukpauk TEXT, makanan TEXT, sayuran TEXT, buah TEXT, karbohidratsatu TEXT, porsikarbsatu TEXT, karbohidratdua TEXT, porsikarbdua TEXT, lauksatu TEXT, porsilauksatu TEXT, laukdua TEXT, porsilaukdua TEXT, sayursatu TEXT, porsisatu TEXT, sayurdua TEXT, porsidua TEXT, sayurtiga TEXT, porsitiga TEXT, buahsatu TEXT, porsibuahsatu TEXT, buahdua TEXT, porsibuahdua TEXT, buahtiga TEXT, porsibuahtiga TEXT)";
        db.execSQL(CREATE_NUTRITION);

        String CREATE_FOOD_RECOG = "CREATE TABLE IF NOT EXISTS " + TABLE_FOOD_RECOG + "(id TEXT PRIMARY KEY, uid TEXT, date TEXT, nama_makanan TEXT, porsi TEXT, kalori TEXT, protein TEXT, karbohidrat TEXT, lemak TEXT, serat TEXT, kalsium TEXT, besi TEXT, natrium TEXT, kalium TEXT, vitamin_a TEXT, vitamin_c TEXT, lemak_jenuh TEXT, lemak_ganda TEXT, lemak_tunggal TEXT, kolesterol TEXT, gula TEXT)";
        db.execSQL(CREATE_FOOD_RECOG);

        String CREATE_PHYSICAL = "CREATE TABLE " + TABLE_PHYSICAL + "(id TEXT PRIMARY KEY, uid TEXT, date TEXT, age TEXT, gender TEXT, beratbadan TEXT, tinggibadan TEXT, jaraktempuh TEXT, waktu TEXT, type TEXT, pathPoints TEXT)";
        db.execSQL(CREATE_PHYSICAL);

        String CREATE_REST = "CREATE TABLE " + TABLE_REST + "(id TEXT PRIMARY KEY, uid TEXT, date TEXT, day TEXT, timesleep TEXT, start_sleep TEXT, end_sleep TEXT, start_timestamp TEXT, end_timestamp TEXT)";
        db.execSQL(CREATE_REST);

        String CREATE_SMOKER = "CREATE TABLE " + TABLE_SMOKER + "(id TEXT PRIMARY KEY, uid TEXT, date TEXT, merokok TEXT, pertanyaan_1 TEXT, pertanyaan_2 TEXT, pertanyaan_3 TEXT, pertanyaan_4 TEXT, pertanyaan_5 TEXT, pertanyaan_6 TEXT, jawaban_pertanyaan_1 TEXT, jawaban_pertanyaan_2 TEXT, jawaban_pertanyaan_3 TEXT, jawaban_pertanyaan_4 TEXT, jawaban_pertanyaan_5 TEXT, jawaban_pertanyaan_6 TEXT, poin_pertanyaan_1 TEXT, poin_pertanyaan_2 TEXT, poin_pertanyaan_3 TEXT, poin_pertanyaan_4 TEXT, poin_pertanyaan_5 TEXT, poin_pertanyaan_6 TEXT, total_poin TEXT, status_perokok TEXT)";
        db.execSQL(CREATE_SMOKER);

        String CREATE_KALKULATOR = "CREATE TABLE " + TABLE_KALK_MEROKOK + "(id TEXT PRIMARY KEY, uid TEXT, date TEXT, batang_hari TEXT, batang_bulan TEXT, batang_tahun TEXT, total_batang TEXT, biaya_hari TEXT, biaya_bulan TEXT, biaya_tahun TEXT, total_biaya TEXT, lama_merokok TEXT)";
        db.execSQL(CREATE_KALKULATOR);

        String CREATE_STRESS = "CREATE TABLE " + TABLE_STRESS + "(id TEXT PRIMARY KEY, uid TEXT, date TEXT, stress TEXT, depresi TEXT, cemas TEXT, quest1 TEXT, quest2 TEXT, quest3 TEXT, quest4 TEXT, quest5 TEXT, quest6 TEXT, quest7 TEXT)";
        db.execSQL(CREATE_STRESS);

        String CREATE_BMR = "CREATE TABLE " + TABLE_BMR + "(id TEXT PRIMARY KEY, uid TEXT, date TEXT, gender TEXT, umur TEXT, berat TEXT, tinggi TEXT, aktivitas TEXT, faktor TEXT, bmr TEXT, tdee TEXT, turun TEXT, normal TEXT, naik TEXT)";
        db.execSQL(CREATE_BMR);

        // TABEL MAKANAN BARU DARI TENSORFLOW & JSON
        String CREATE_FOOD_TABLE = "CREATE TABLE " + TABLE_FOOD_DATA + "("
                + COL_FOOD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_FOOD_NAMA + " TEXT,"
                + COL_FOOD_KALORI + " REAL,"
                + COL_FOOD_PROTEIN + " REAL,"
                + COL_FOOD_KARB + " REAL,"
                + COL_FOOD_LEMAK + " REAL,"
                + COL_FOOD_SUMBER + " TEXT" + ")";
        db.execSQL(CREATE_FOOD_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 19) {
            addColumnIfNotExists(db, TABLE_SMOKER, "pertanyaan_1", "TEXT");
            addColumnIfNotExists(db, TABLE_SMOKER, "pertanyaan_2", "TEXT");
            addColumnIfNotExists(db, TABLE_SMOKER, "pertanyaan_3", "TEXT");
            addColumnIfNotExists(db, TABLE_SMOKER, "pertanyaan_4", "TEXT");
            addColumnIfNotExists(db, TABLE_SMOKER, "pertanyaan_5", "TEXT");
            addColumnIfNotExists(db, TABLE_SMOKER, "pertanyaan_6", "TEXT");
            addColumnIfNotExists(db, TABLE_SMOKER, "jawaban_pertanyaan_3", "TEXT");
            addColumnIfNotExists(db, TABLE_SMOKER, "jawaban_pertanyaan_4", "TEXT");
            addColumnIfNotExists(db, TABLE_SMOKER, "jawaban_pertanyaan_5", "TEXT");
            addColumnIfNotExists(db, TABLE_SMOKER, "jawaban_pertanyaan_6", "TEXT");
            addColumnIfNotExists(db, TABLE_SMOKER, "poin_pertanyaan_3", "TEXT");
            addColumnIfNotExists(db, TABLE_SMOKER, "poin_pertanyaan_4", "TEXT");
            addColumnIfNotExists(db, TABLE_SMOKER, "poin_pertanyaan_5", "TEXT");
            addColumnIfNotExists(db, TABLE_SMOKER, "poin_pertanyaan_6", "TEXT");
        }

        // Hapus sisa tabel yang tidak terpakai
        if (oldVersion < 18) {
            db.execSQL("DROP TABLE IF EXISTS pangan_tkpi"); // HANCURKAN TKPI
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOOD_DATA);

            String CREATE_FOOD_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_FOOD_DATA + "("
                    + COL_FOOD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COL_FOOD_NAMA + " TEXT,"
                    + COL_FOOD_KALORI + " REAL,"
                    + COL_FOOD_PROTEIN + " REAL,"
                    + COL_FOOD_KARB + " REAL,"
                    + COL_FOOD_LEMAK + " REAL,"
                    + COL_FOOD_SUMBER + " TEXT" + ")";
            db.execSQL(CREATE_FOOD_TABLE);
        }

        // --- MIGRATIONS LAMA ---
        if (oldVersion < 17) {
            try {
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOOD_RECOG);
                String CREATE_FOOD_RECOG = "CREATE TABLE IF NOT EXISTS " + TABLE_FOOD_RECOG + "(id TEXT PRIMARY KEY, uid TEXT, date TEXT, nama_makanan TEXT, porsi TEXT, kalori TEXT, protein TEXT, karbohidrat TEXT, lemak TEXT, serat TEXT, kalsium TEXT, besi TEXT, natrium TEXT, kalium TEXT, vitamin_a TEXT, vitamin_c TEXT, lemak_jenuh TEXT, lemak_ganda TEXT, lemak_tunggal TEXT, kolesterol TEXT, gula TEXT)";
                db.execSQL(CREATE_FOOD_RECOG);
            } catch (Exception e) {}
        }
        if (oldVersion < 15) {
            try { db.execSQL("ALTER TABLE " + TABLE_REST + " ADD COLUMN start_sleep TEXT"); } catch (Exception ignored) {}
            try { db.execSQL("ALTER TABLE " + TABLE_REST + " ADD COLUMN end_sleep TEXT"); } catch (Exception ignored) {}
            try { db.execSQL("ALTER TABLE " + TABLE_REST + " ADD COLUMN start_timestamp TEXT"); } catch (Exception ignored) {}
            try { db.execSQL("ALTER TABLE " + TABLE_REST + " ADD COLUMN end_timestamp TEXT"); } catch (Exception ignored) {}
        }
        if (oldVersion < 14) {
            try {
                String CREATE_BMR = "CREATE TABLE IF NOT EXISTS " + TABLE_BMR + "(id TEXT PRIMARY KEY, uid TEXT, date TEXT, gender TEXT, umur TEXT, berat TEXT, tinggi TEXT, aktivitas TEXT, faktor TEXT, bmr TEXT, tdee TEXT, turun TEXT, normal TEXT, naik TEXT)";
                db.execSQL(CREATE_BMR);
            } catch (Exception e) {}
        }
        if (oldVersion < 13) {
            try { db.execSQL("ALTER TABLE smokertest ADD COLUMN jawaban_pertanyaan_1 TEXT"); } catch (Exception ignored) {}
            try { db.execSQL("ALTER TABLE smokertest ADD COLUMN jawaban_pertanyaan_2 TEXT"); } catch (Exception ignored) {}
            try { db.execSQL("ALTER TABLE smokertest ADD COLUMN poin_pertanyaan_1 TEXT"); } catch (Exception ignored) {}
            try { db.execSQL("ALTER TABLE smokertest ADD COLUMN poin_pertanyaan_2 TEXT"); } catch (Exception ignored) {}
            try { db.execSQL("ALTER TABLE smokertest ADD COLUMN total_poin TEXT"); } catch (Exception ignored) {}
            try { db.execSQL("ALTER TABLE smokertest ADD COLUMN status_perokok TEXT"); } catch (Exception ignored) {}
        }
        if (oldVersion < 12) {
            try {
                String CREATE_KALKULATOR = "CREATE TABLE IF NOT EXISTS " + TABLE_KALK_MEROKOK + "(id TEXT PRIMARY KEY, uid TEXT, date TEXT, batang_hari TEXT, batang_bulan TEXT, batang_tahun TEXT, total_batang TEXT, biaya_hari TEXT, biaya_bulan TEXT, biaya_tahun TEXT, total_biaya TEXT, lama_merokok TEXT)";
                db.execSQL(CREATE_KALKULATOR);
            } catch (Exception e) {}
        }
        if (oldVersion < 10) {
            try {
                String CREATE_NUTRITION = "CREATE TABLE IF NOT EXISTS " + TABLE_NUTRITION + "(id TEXT PRIMARY KEY, uid TEXT, date TEXT, laukpauk TEXT, makanan TEXT, sayuran TEXT, buah TEXT, karbohidratsatu TEXT, porsikarbsatu TEXT, karbohidratdua TEXT, porsikarbdua TEXT, lauksatu TEXT, porsilauksatu TEXT, laukdua TEXT, porsilaukdua TEXT, sayursatu TEXT, porsisatu TEXT, sayurdua TEXT, porsidua TEXT, sayurtiga TEXT, porsitiga TEXT, buahsatu TEXT, porsibuahsatu TEXT, buahdua TEXT, porsibuahdua TEXT, buahtiga TEXT, porsibuahtiga TEXT)";
                db.execSQL(CREATE_NUTRITION);
                String CREATE_STRESS = "CREATE TABLE IF NOT EXISTS " + TABLE_STRESS + "(id TEXT PRIMARY KEY, uid TEXT, date TEXT, stress TEXT, depresi TEXT, cemas TEXT, quest1 TEXT, quest2 TEXT, quest3 TEXT, quest4 TEXT, quest5 TEXT, quest6 TEXT, quest7 TEXT)";
                db.execSQL(CREATE_STRESS);
            } catch (Exception e) {}
        }
        if (oldVersion < 9) {
            try {
                db.execSQL("ALTER TABLE " + TABLE_PHYSICAL + " ADD COLUMN type TEXT");
                db.execSQL("ALTER TABLE " + TABLE_PHYSICAL + " ADD COLUMN pathPoints TEXT");
            } catch (Exception e) {}
        }
        if (oldVersion < 8) {
            try {
                db.execSQL("ALTER TABLE " + TABLE_SMOKER + " ADD COLUMN jawaban_pertanyaan_1 TEXT");
                db.execSQL("ALTER TABLE " + TABLE_SMOKER + " ADD COLUMN jawaban_pertanyaan_2 TEXT");
                db.execSQL("ALTER TABLE " + TABLE_SMOKER + " ADD COLUMN poin_pertanyaan_1 TEXT");
                db.execSQL("ALTER TABLE " + TABLE_SMOKER + " ADD COLUMN poin_pertanyaan_2 TEXT");
                db.execSQL("ALTER TABLE " + TABLE_SMOKER + " ADD COLUMN total_poin TEXT");
                db.execSQL("ALTER TABLE " + TABLE_SMOKER + " ADD COLUMN status_perokok TEXT");
            } catch (Exception e) {}
        }
        if (oldVersion < 7) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDCHECK);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NUTRITION);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOOD_RECOG);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHYSICAL);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_REST);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SMOKER);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_STRESS);
            onCreate(db);
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    // =========================================================================
    // FUNGSI KHUSUS UNTUK MEMBACA NUTRITION_DATA.JSON TENSORFLOW
    // =========================================================================
    public boolean isFoodDataEmpty() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT count(*) FROM " + TABLE_FOOD_DATA, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        db.close();
        return count == 0;
    }

    public void loadNutritionFromAssets(Context context) {
        if (!isFoodDataEmpty()) {
            return;
        }
        try {
            InputStream is = context.getAssets().open("nutrition_data.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String jsonStr = new String(buffer, StandardCharsets.UTF_8);
            JSONObject jsonObject = new JSONObject(jsonStr); // Menggunakan JSONObject, bukan Array

            SQLiteDatabase db = this.getWritableDatabase();
            db.beginTransaction();

            try {
                Iterator<String> keys = jsonObject.keys();
                while(keys.hasNext()) {
                    String foodName = keys.next();
                    JSONObject obj = jsonObject.getJSONObject(foodName);

                    ContentValues values = new ContentValues();
                    values.put(COL_FOOD_NAMA, foodName);
                    values.put(COL_FOOD_KALORI, obj.optDouble("kalori_kcal", 0.0));
                    values.put(COL_FOOD_PROTEIN, obj.optDouble("protein_g", 0.0));
                    values.put(COL_FOOD_KARB, obj.optDouble("karbohidrat_g", 0.0));
                    values.put(COL_FOOD_LEMAK, obj.optDouble("lemak_g", 0.0));
                    values.put(COL_FOOD_SUMBER, obj.optString("sumber", ""));

                    db.insert(TABLE_FOOD_DATA, null, values);
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
            Log.d("DatabaseHelper", "Berhasil meload data makanan dari JSON");
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Gagal meload nutrition_data.json", e);
        }
    }

    public HashMap<String, String> getFoodByName(String namaMakanan) {
        SQLiteDatabase db = this.getReadableDatabase();
        HashMap<String, String> map = new HashMap<>();

        String cleanValue = "";
        if (namaMakanan != null) {
            cleanValue = namaMakanan.trim().toLowerCase();
        }

        String queryExact = "SELECT * FROM " + TABLE_FOOD_DATA + " WHERE LOWER(" + COL_FOOD_NAMA + ") = ?";
        Cursor cursor = db.rawQuery(queryExact, new String[]{cleanValue});

        if (!cursor.moveToFirst()) {
            cursor.close();
            String queryLike = "SELECT * FROM " + TABLE_FOOD_DATA + " WHERE LOWER(" + COL_FOOD_NAMA + ") LIKE ?";
            cursor = db.rawQuery(queryLike, new String[]{"%" + cleanValue + "%"});
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
            }
        }

        if (cursor.getCount() > 0) {
            for(int i = 0; i < cursor.getColumnCount(); i++) {
                map.put(cursor.getColumnName(i), cursor.getString(i));
            }
        }

        cursor.close();
        db.close();
        return map;
    }

    // =========================================================================
    // FUNGSI UMUM DATABASE
    // =========================================================================
    public void insertOrUpdateRecord(String table, String id, HashMap<String, Object> data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", id);
        List<String> columns = getTableColumns(db, table);
        for (String key : data.keySet()) {
            if(!key.equals("id") && columns.contains(key)) {
                try {
                    values.put(key, String.valueOf(data.get(key)));
                } catch(Exception e){}
            }
        }
        int rows = db.update(table, values, "id=?", new String[]{id});
        if (rows == 0) {
            try { db.insert(table, null, values); } catch (Exception e) { e.printStackTrace(); }
        }
        db.close();
    }

    private void addColumnIfNotExists(SQLiteDatabase db, String table, String column, String type) {
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("PRAGMA table_info(" + table + ")", null);
            while (cursor.moveToNext()) {
                String existingColumn = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                if (column.equals(existingColumn)) {
                    return;
                }
            }
            db.execSQL("ALTER TABLE " + table + " ADD COLUMN " + column + " " + type);
        } catch (Exception ignored) {
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private List<String> getTableColumns(SQLiteDatabase db, String table) {
        List<String> columns = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("PRAGMA table_info(" + table + ")", null);
            while (cursor.moveToNext()) {
                columns.add(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            }
        } catch (Exception ignored) {
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return columns;
    }

    public List<HashMap<String, String>> getAllRecords(String table, String uid) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<HashMap<String, String>> list = new ArrayList<>();
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM " + table + " WHERE uid = ?", new String[]{uid != null ? uid : ""});
            if (cursor.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<>();
                    for(int i=0; i<cursor.getColumnCount(); i++) {
                        map.put(cursor.getColumnName(i), cursor.getString(i));
                    }
                    list.add(map);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {}
        db.close();
        return list;
    }

    public HashMap<String, String> getRecordById(String table, String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        HashMap<String, String> map = new HashMap<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + table + " WHERE id = ?", new String[]{id});
        if (cursor.moveToFirst()) {
            for(int i=0; i<cursor.getColumnCount(); i++) {
                map.put(cursor.getColumnName(i), cursor.getString(i));
            }
        }
        cursor.close();
        db.close();
        return map;
    }

    public boolean insertUser(String uid, String email, String password, String nama, String location, String birthday, String gender, int umur) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_UID, uid);
        values.put(COL_USER_EMAIL, email);
        values.put(COL_USER_PASSWORD, password);
        values.put(COL_USER_NAMA, nama);
        values.put(COL_USER_LOCATION, location);
        values.put(COL_USER_BIRTHDAY, birthday);
        values.put(COL_USER_GENDER, gender);
        values.put(COL_USER_UMUR, umur);
        long result = db.insertWithOnConflict(TABLE_USER, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
        return result != -1;
    }

    public boolean checkLogin(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USER + " WHERE " + COL_USER_EMAIL + " = ? AND " + COL_USER_PASSWORD + " = ?", new String[]{email, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    public String getUidByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COL_USER_UID + " FROM " + TABLE_USER + " WHERE " + COL_USER_EMAIL + " = ?", new String[]{email});
        String foundUid = null;
        if (cursor.moveToFirst()) {
            foundUid = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return foundUid;
    }

    public HashMap<String, Object> getUserData(String uid) {
        SQLiteDatabase db = this.getReadableDatabase();
        HashMap<String, Object> userData = new HashMap<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USER + " WHERE " + COL_USER_UID + " = ?", new String[]{uid});
        if (cursor.moveToFirst()) {
            userData.put("uid", cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_UID)));
            userData.put("email", cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_EMAIL)));
            userData.put("password", cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_PASSWORD)));
            userData.put("nama", cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_NAMA)));
            userData.put("location", cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_LOCATION)));
            userData.put("birthday", cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_BIRTHDAY)));
            userData.put("gender", cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_GENDER)));
            userData.put("umur", cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER_UMUR)));
            userData.put("photoUrl", cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_PHOTOURI)));
        }
        cursor.close();
        db.close();
        return userData;
    }

    public boolean updateUserProfile(String uid, String nama, String location, String birthday, String gender, int umur) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_NAMA, nama);
        values.put(COL_USER_LOCATION, location);
        values.put(COL_USER_BIRTHDAY, birthday);
        values.put(COL_USER_GENDER, gender);
        values.put(COL_USER_UMUR, umur);
        int rows = db.update(TABLE_USER, values, COL_USER_UID + "=?", new String[]{uid});
        if (rows == 0) {
            values.put(COL_USER_UID, uid);
            long result = db.insert(TABLE_USER, null, values);
            db.close();
            return result != -1;
        }
        db.close();
        return true;
    }

    public boolean updateProfileImage(String uid, String photoUri) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_PHOTOURI, photoUri);
        int rows = db.update(TABLE_USER, values, COL_USER_UID + "=?", new String[]{uid});
        if (rows == 0) {
            values.put(COL_USER_UID, uid);
            db.insert(TABLE_USER, null, values);
        }
        db.close();
        return true;
    }

    /**
     * Menghapus satu record dari tabel berdasarkan ID-nya.
     *
     * @param table nama tabel SQLite
     * @param id    nilai kolom 'id' dari record yang ingin dihapus
     * @return true jika berhasil menghapus setidaknya 1 baris
     */
    public boolean deleteRecord(String table, String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(table, "id = ?", new String[]{id});
        db.close();
        return rows > 0;
    }

    /**
     * Menghapus semua record di TABLE_REST yang usianya melebihi batas hari yang ditentukan.
     * Menggunakan kolom 'start_timestamp' (milidetik) untuk menghitung usia data.
     *
     * @param maxAgeMillis batas usia data dalam milidetik (mis. 7 * 24 * 60 * 60 * 1000L untuk 7 hari)
     * @return list ID dari record yang berhasil dihapus (untuk dipakai menghapus dari Firestore)
     */
    public List<String> deleteOldRestPatterns(long maxAgeMillis) {
        List<String> deletedIds = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        long cutoffTimestamp = System.currentTimeMillis() - maxAgeMillis;
        Cursor cursor = null;
        try {
            // Cari semua record yang timestamp-nya lebih lama dari cutoff
            cursor = db.rawQuery(
                    "SELECT id FROM " + TABLE_REST + " WHERE CAST(start_timestamp AS INTEGER) > 0 AND CAST(start_timestamp AS INTEGER) < ?",
                    new String[]{String.valueOf(cutoffTimestamp)}
            );
            while (cursor.moveToNext()) {
                String id = cursor.getString(0);
                if (id != null && !id.isEmpty()) {
                    deletedIds.add(id);
                }
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "deleteOldRestPatterns error", e);
        } finally {
            if (cursor != null) cursor.close();
        }

        // Hapus dari SQLite setelah ID terkumpul
        for (String id : deletedIds) {
            try {
                db.delete(TABLE_REST, "id = ?", new String[]{id});
            } catch (Exception e) {
                Log.e("DatabaseHelper", "Failed to delete rest record: " + id, e);
            }
        }

        db.close();
        return deletedIds;
    }
}
