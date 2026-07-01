package example.com.fielthyapps.Feature.History;

import java.util.HashMap;

/**
 * Model untuk unified history timeline.
 * Bisa berupa DATE_HEADER (pemisah tanggal) atau ITEM (entri riwayat).
 */
public class HistoryTimelineItem {

    /** View type: pemisah tanggal seperti "Hari ini", "Kemarin" */
    public static final int TYPE_DATE_HEADER = 0;

    /** View type: item riwayat (Med Check, Physical Activity, dll.) */
    public static final int TYPE_ITEM = 1;

    // ─── Category keys (dipakai untuk filter & navigasi) ──────────────────────
    public static final String CAT_MEDCHECK  = "medcheck";
    public static final String CAT_PHYSICAL  = "physical";
    public static final String CAT_REST      = "rest";
    public static final String CAT_NUTRITION = "nutrition";
    public static final String CAT_SMOKER    = "smoker";
    public static final String CAT_STRESS    = "stress";
    public static final String CAT_KALK_MEROKOK = "kalkulator_merokok";
    public static final String CAT_BMR = "bmr";
    public static final String CAT_FOOD_RECOG = "food_recog";

    // ─── Fields ───────────────────────────────────────────────────────────────
    private int    type;
    /** Untuk header: label tanggal ("Hari ini" / "Kemarin" / "Senin, 1 Mei…").
     *  Untuk item: dipakai sebagai rawDate untuk sorting. */
    private String label;
    /** Nama kategori yang ditampilkan di layar (e.g. "Med Check") */
    private String categoryDisplay;
    /** Kunci kategori internal (e.g. "medcheck") */
    private String categoryKey;
    /** Primary key record di SQLite */
    private String id;
    /** UID pengguna */
    private String uid;
    /** Tanggal mentah dari DB — dipakai untuk sorting & grouping */
    private String rawDate;
    /** Resource ID icon yang akan ditampilkan */
    private int    iconRes;
    /** Extra data untuk intent ke halaman hasil */
    private HashMap<String, String> extras;
    /** Date parsed value for sorting */
    private java.util.Date parsedDate;

    // ─── Constructors ─────────────────────────────────────────────────────────
    public HistoryTimelineItem(int type) {
        this.type = type;
    }

    // ─── Getters & Setters ────────────────────────────────────────────────────
    public int getType()                      { return type; }
    public String getLabel()                  { return label; }
    public void setLabel(String label)        { this.label = label; }

    public String getCategoryDisplay()                        { return categoryDisplay; }
    public void setCategoryDisplay(String categoryDisplay)    { this.categoryDisplay = categoryDisplay; }

    public String getCategoryKey()                    { return categoryKey; }
    public void setCategoryKey(String categoryKey)    { this.categoryKey = categoryKey; }

    public String getId()             { return id; }
    public void setId(String id)      { this.id = id; }

    public String getUid()            { return uid; }
    public void setUid(String uid)    { this.uid = uid; }

    public String getRawDate()                { return rawDate; }
    public void setRawDate(String rawDate)    { this.rawDate = rawDate; }

    public int getIconRes()                { return iconRes; }
    public void setIconRes(int iconRes)    { this.iconRes = iconRes; }

    public HashMap<String, String> getExtras()                { return extras; }
    public void setExtras(HashMap<String, String> extras)     { this.extras = extras; }

    public java.util.Date getParsedDate()                 { return parsedDate; }
    public void setParsedDate(java.util.Date parsedDate)  { this.parsedDate = parsedDate; }
}