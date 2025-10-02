package edu.sliit.operator.database


import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import  edu.sliit.operator.models.User
import java.security.MessageDigest

class EVHubDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val TAG = "EVHubDatabase"
        private const val DATABASE_NAME = "ev_hub.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_OPERATORS = "operators"
        const val TABLE_BOOKINGS_CACHE = "bookings_cache"
        const val TABLE_STATIONS_CACHE = "stations_cache"

        const val COL_ID = "id"
        const val COL_USERNAME = "username"
        const val COL_PASSWORD = "password"
        const val COL_ROLE = "role"
        const val COL_STATION_ASSIGNMENTS = "station_assignments"
        const val COL_CREATED_AT = "created_at"
        const val COL_LAST_SYNC = "last_sync"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        Log.d(TAG, "Creating database tables...")

        val createOperatorsTable = """
            CREATE TABLE $TABLE_OPERATORS (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_USERNAME TEXT UNIQUE NOT NULL,
                $COL_PASSWORD TEXT NOT NULL,
                $COL_ROLE TEXT DEFAULT 'OPERATOR',
                $COL_STATION_ASSIGNMENTS TEXT,
                $COL_CREATED_AT INTEGER,
                $COL_LAST_SYNC INTEGER
            )
        """.trimIndent()

        val createBookingsTable = """
            CREATE TABLE $TABLE_BOOKINGS_CACHE (
                booking_id TEXT PRIMARY KEY,
                owner_nic TEXT,
                station_id TEXT,
                reservation_datetime TEXT,
                status TEXT,
                qr_token TEXT,
                created_at INTEGER,
                updated_at INTEGER
            )
        """.trimIndent()

        val createStationsTable = """
            CREATE TABLE $TABLE_STATIONS_CACHE (
                station_id TEXT PRIMARY KEY,
                name TEXT,
                location TEXT,
                type TEXT,
                total_slots INTEGER,
                available_slots INTEGER,
                status TEXT,
                last_updated INTEGER
            )
        """.trimIndent()

        try {
            db?.execSQL(createOperatorsTable)
            Log.d(TAG, "✓ Operators table created")

            db?.execSQL(createBookingsTable)
            Log.d(TAG, "✓ Bookings cache table created")

            db?.execSQL(createStationsTable)
            Log.d(TAG, "✓ Stations cache table created")

            insertDemoData(db)

        } catch (e: Exception) {
            Log.e(TAG, "✗ Error creating tables: ${e.message}")
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVer: Int, newVer: Int) {
        Log.d(TAG, "Upgrading database from version $oldVer to $newVer")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_OPERATORS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_BOOKINGS_CACHE")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_STATIONS_CACHE")
        onCreate(db)
    }

    private fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(password.toByteArray())
        return hash.joinToString("") { "%02x".format(it) }
    }

    fun insertOperator(user: User): Long {
        Log.d(TAG, "Attempting to insert user: ${user.username}")

        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_USERNAME, user.username)
            put(COL_PASSWORD, hashPassword(user.password))
            put(COL_ROLE, user.role)
            put(COL_STATION_ASSIGNMENTS, user.stationAssignments)
            put(COL_CREATED_AT, user.createdAt)
            put(COL_LAST_SYNC, user.lastSync)
        }

        return try {
            val result = db.insert(TABLE_OPERATORS, null, values)
            if (result != -1L) {
                Log.d(TAG, "✓ User inserted successfully with ID: $result")
            } else {
                Log.e(TAG, "✗ Failed to insert user")
            }
            result
        } catch (e: Exception) {
            Log.e(TAG, "✗ Error inserting user: ${e.message}")
            -1L
        }
    }

    fun validateLogin(username: String, password: String): User? {
        Log.d(TAG, "Validating login for username: $username")

        val db = readableDatabase
        val hashedPassword = hashPassword(password)

        val cursor = db.query(
            TABLE_OPERATORS,
            null,
            "$COL_USERNAME = ? AND $COL_PASSWORD = ?",
            arrayOf(username, hashedPassword),
            null, null, null
        )

        return try {
            if (cursor.moveToFirst()) {
                val user = User(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                    username = cursor.getString(cursor.getColumnIndexOrThrow(COL_USERNAME)),
                    password = "",
                    role = cursor.getString(cursor.getColumnIndexOrThrow(COL_ROLE)),
                    stationAssignments = cursor.getString(cursor.getColumnIndexOrThrow(COL_STATION_ASSIGNMENTS)),
                    createdAt = cursor.getLong(cursor.getColumnIndexOrThrow(COL_CREATED_AT)),
                    lastSync = cursor.getLongOrNull(cursor.getColumnIndexOrThrow(COL_LAST_SYNC))
                )
                cursor.close()
                Log.d(TAG, "✓ Login successful for user: ${user.username} (ID: ${user.id})")
                user
            } else {
                cursor.close()
                Log.d(TAG, "✗ Login failed: Invalid credentials")
                null
            }
        } catch (e: Exception) {
            cursor.close()
            Log.e(TAG, "✗ Error during login: ${e.message}")
            null
        }
    }

    fun usernameExists(username: String): Boolean {
        Log.d(TAG, "Checking if username exists: $username")

        val db = readableDatabase
        val cursor = db.query(
            TABLE_OPERATORS,
            arrayOf(COL_ID),
            "$COL_USERNAME = ?",
            arrayOf(username),
            null, null, null
        )
        val exists = cursor.count > 0
        cursor.close()

        Log.d(TAG, if (exists) "✓ Username exists" else "✗ Username available")
        return exists
    }

    fun getAllOperators(): List<User> {
        Log.d(TAG, "Fetching all operators...")

        val operators = mutableListOf<User>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_OPERATORS,
            null,
            null, null, null, null,
            "$COL_CREATED_AT DESC"
        )

        try {
            while (cursor.moveToNext()) {
                val user = User(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                    username = cursor.getString(cursor.getColumnIndexOrThrow(COL_USERNAME)),
                    password = "",
                    role = cursor.getString(cursor.getColumnIndexOrThrow(COL_ROLE)),
                    stationAssignments = cursor.getString(cursor.getColumnIndexOrThrow(COL_STATION_ASSIGNMENTS)),
                    createdAt = cursor.getLong(cursor.getColumnIndexOrThrow(COL_CREATED_AT)),
                    lastSync = cursor.getLongOrNull(cursor.getColumnIndexOrThrow(COL_LAST_SYNC))
                )
                operators.add(user)
            }
            cursor.close()
            Log.d(TAG, "✓ Found ${operators.size} operators")
        } catch (e: Exception) {
            cursor.close()
            Log.e(TAG, "✗ Error fetching operators: ${e.message}")
        }

        return operators
    }

    private fun android.database.Cursor.getLongOrNull(columnIndex: Int): Long? {
        return if (isNull(columnIndex)) null else getLong(columnIndex)
    }

    private fun insertDemoData(db: SQLiteDatabase?) {
        Log.d(TAG, "Inserting demo data...")

        val demoUser = ContentValues().apply {
            put(COL_USERNAME, "demo")
            put(COL_PASSWORD, hashPassword("demo123"))
            put(COL_ROLE, "OPERATOR")
            put(COL_STATION_ASSIGNMENTS, "[\"Station-A\", \"Station-B\"]")
            put(COL_CREATED_AT, System.currentTimeMillis())
        }

        try {
            db?.insert(TABLE_OPERATORS, null, demoUser)
            Log.d(TAG, "✓ Demo user created (username: demo, password: demo123)")
        } catch (e: Exception) {
            Log.e(TAG, "✗ Failed to create demo user: ${e.message}")
        }
    }

    fun printAllUsers() {
        Log.d(TAG, "========== ALL USERS IN DATABASE ==========")
        val users = getAllOperators()
        if (users.isEmpty()) {
            Log.d(TAG, "No users found in database")
        } else {
            users.forEachIndexed { index, user ->
                Log.d(TAG, "User ${index + 1}:")
                Log.d(TAG, "  ID: ${user.id}")
                Log.d(TAG, "  Username: ${user.username}")
                Log.d(TAG, "  Role: ${user.role}")
                Log.d(TAG, "  Created: ${user.createdAt}")
            }
        }
        Log.d(TAG, "==========================================")
    }
}