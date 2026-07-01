package example.com.fielthyapps.Database;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * FirestoreSyncManager — Sinkronisasi data history dari Firestore Cloud ke SQLite lokal
 * secara realtime menggunakan addSnapshotListener.
 *
 * Ini memungkinkan data yang disimpan dari Device A langsung muncul di Device B
 * tanpa harus re-login atau restart app.
 *
 * Penggunaan:
 *   syncManager = new FirestoreSyncManager();
 *   syncManager.attachRealtimeListeners(uid, dbHelper, () -> loadAllHistory());
 *   // ... di onDestroy():
 *   syncManager.detachListeners();
 */
public class FirestoreSyncManager {

    private static final String TAG = "FirestoreSyncManager";

    private final FirebaseFirestore fStore;
    private final List<ListenerRegistration> activeListeners = new ArrayList<>();

    /**
     * Mapping antara Firestore collection name dan SQLite table name.
     * Urutan: { firestoreCollection, sqliteTable }
     */
    private static final String[][] COLLECTION_TABLE_MAP = {
            {"medcheck",        DatabaseHelper.TABLE_MEDCHECK},
            {"nutritiontest",   DatabaseHelper.TABLE_NUTRITION},
            {"bmr",             DatabaseHelper.TABLE_BMR},
            {"6mwt",            DatabaseHelper.TABLE_PHYSICAL},
            {"balke",           DatabaseHelper.TABLE_PHYSICAL},
            {"restpattern",     DatabaseHelper.TABLE_REST},
            {"smoker",          DatabaseHelper.TABLE_SMOKER},
            {"kalk_merokok",    DatabaseHelper.TABLE_KALK_MEROKOK},
            {"stresstest",      DatabaseHelper.TABLE_STRESS},
            {"foodrecognition", DatabaseHelper.TABLE_FOOD_RECOG},
    };

    public FirestoreSyncManager() {
        this.fStore = FirebaseFirestore.getInstance();
    }

    /**
     * Melakukan one-time sync dari semua koleksi Firestore yang relevan ke SQLite lokal.
     * Dipanggil saat HistoryActivity pertama kali dibuka untuk memastikan data sudah ada.
     *
     * @param uid        UID user yang sedang login
     * @param dbHelper   Instance DatabaseHelper untuk menyimpan ke SQLite
     * @param onComplete Callback yang dipanggil setelah semua koleksi selesai di-sync
     */
    public void syncAllCollections(String uid, DatabaseHelper dbHelper, Runnable onComplete) {
        if (uid == null || uid.isEmpty()) {
            Log.w(TAG, "syncAllCollections: uid is null or empty");
            if (onComplete != null) onComplete.run();
            return;
        }

        final int totalCollections = COLLECTION_TABLE_MAP.length;
        final int[] completedCount = {0};

        for (String[] mapping : COLLECTION_TABLE_MAP) {
            String collection = mapping[0];
            String table = mapping[1];

            fStore.collection(collection)
                    .whereEqualTo("uid", uid)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                try {
                                    HashMap<String, Object> map = new HashMap<>(doc.getData());
                                    // Pastikan field 'id' ada — beberapa koleksi mungkin tidak menyimpannya
                                    if (!map.containsKey("id")) {
                                        map.put("id", doc.getId());
                                    }
                                    dbHelper.insertOrUpdateRecord(table, doc.getId(), map);
                                } catch (Exception e) {
                                    Log.e(TAG, "Error syncing " + collection + " doc " + doc.getId(), e);
                                }
                            }
                            Log.d(TAG, "Synced " + collection + " → " + table
                                    + " (" + task.getResult().size() + " docs)");
                        } else {
                            Log.w(TAG, "Failed to sync " + collection, task.getException());
                        }

                        completedCount[0]++;
                        if (completedCount[0] >= totalCollections && onComplete != null) {
                            onComplete.run();
                        }
                    });
        }
    }

    /**
     * Memasang realtime listener pada semua koleksi Firestore yang relevan.
     * Setiap kali ada perubahan data di cloud (misalnya dari device lain),
     * data baru langsung disimpan ke SQLite lokal dan callback onDataChanged dipanggil.
     *
     * @param uid           UID user yang sedang login
     * @param dbHelper      Instance DatabaseHelper untuk menyimpan ke SQLite
     * @param onDataChanged Callback yang dipanggil setiap kali ada perubahan data
     *                      (gunakan untuk reload tampilan history)
     */
    public void attachRealtimeListeners(String uid, DatabaseHelper dbHelper, Runnable onDataChanged) {
        if (uid == null || uid.isEmpty()) {
            Log.w(TAG, "attachRealtimeListeners: uid is null or empty");
            return;
        }

        // Bersihkan listener lama terlebih dahulu
        detachListeners();

        for (String[] mapping : COLLECTION_TABLE_MAP) {
            String collection = mapping[0];
            String table = mapping[1];

            ListenerRegistration registration = fStore.collection(collection)
                    .whereEqualTo("uid", uid)
                    .addSnapshotListener((snapshots, error) -> {
                        if (error != null) {
                            Log.w(TAG, "Realtime listener error on " + collection, error);
                            return;
                        }

                        if (snapshots == null || snapshots.isEmpty()) {
                            return;
                        }

                        boolean hasChanges = false;

                        for (QueryDocumentSnapshot doc : snapshots) {
                            try {
                                HashMap<String, Object> map = new HashMap<>(doc.getData());
                                // Pastikan field 'id' ada
                                if (!map.containsKey("id")) {
                                    map.put("id", doc.getId());
                                }
                                dbHelper.insertOrUpdateRecord(table, doc.getId(), map);
                                hasChanges = true;
                            } catch (Exception e) {
                                Log.e(TAG, "Error processing realtime update for "
                                        + collection + " doc " + doc.getId(), e);
                            }
                        }

                        if (hasChanges && onDataChanged != null) {
                            Log.d(TAG, "Realtime update received for " + collection
                                    + " (" + snapshots.size() + " docs) → refreshing");
                            onDataChanged.run();
                        }
                    });

            activeListeners.add(registration);
            Log.d(TAG, "Attached realtime listener for " + collection);
        }
    }

    /**
     * Melepaskan semua realtime listener yang aktif.
     * WAJIB dipanggil di onDestroy() Activity untuk menghindari memory leak.
     */
    public void detachListeners() {
        for (ListenerRegistration registration : activeListeners) {
            registration.remove();
        }
        if (!activeListeners.isEmpty()) {
            Log.d(TAG, "Detached " + activeListeners.size() + " realtime listeners");
        }
        activeListeners.clear();
    }

    /**
     * Mengecek apakah ada listener yang aktif.
     *
     * @return true jika ada realtime listener yang sedang berjalan
     */
    public boolean hasActiveListeners() {
        return !activeListeners.isEmpty();
    }
}
