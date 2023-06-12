package org.techtown.ieat

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

class MyDataBaseHelper (private val context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        private const val DB_NAME = "mymyrecipe.db"
        private const val DB_VERSION = 1
        private const val DB_PATH = "/data/data/%s/databases/"

        // 복사할 데이터베이스 파일명
        private const val ASSETS_DB_NAME = "recipe.db"
    }

    private var database: SQLiteDatabase? = null

    init {
        // 데이터베이스 파일이 없으면 복사
        if (!checkDatabase()) {
            copyDatabase()
        }
    }

    // 데이터베이스가 이미 존재하는지 확인
    private fun checkDatabase(): Boolean {
        val dbFile = File(DB_PATH.format(context.packageName), DB_NAME)
        Log.d("db: ","파일확인")
        return dbFile.exists()
    }

    // assets 폴더의 데이터베이스 파일을 디바이스로 복사
    private fun copyDatabase() {
        Log.d("db: ", "copydatabase")
        try {
            val folder = File(DB_PATH.format(context.packageName))
            if (!folder.exists()) {
                folder.mkdirs()
            }

            val inputStream: InputStream = context.assets.open(ASSETS_DB_NAME)
            val outputStream = FileOutputStream(DB_PATH.format(context.packageName) + DB_NAME)

            val buffer = ByteArray(1024)
            var length: Int
            while (inputStream.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length)
            }

            outputStream.flush()
            outputStream.close()
            inputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    // 데이터베이스 오픈
    fun openDatabase() {
        val dbPath = DB_PATH.format(context.packageName) + DB_NAME
        database = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY)
    }

    // 데이터베이스 닫기
    override fun close() {
        database?.close()
        super.close()
    }

    // onCreate - 테이블 생성 등 초기 설정
    override fun onCreate(db: SQLiteDatabase?) {
        // 필요한 경우 테이블 생성 등 초기 설정 작업을 수행
        val create = "create table if not exists ingred (INGREDIENT text primary key)"
        Log.d("db: ", "onCreate")
        //실행시켜 줍니다.
        db?.execSQL(create)
    }

    // onUpgrade - 데이터베이스 버전 업그레이드 시 호출
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // 필요한 경우 테이블 구조 변경 등 업그레이드 작업을 수행
        Log.d("db: ", "upgrade")
    }

    private fun createTables(db: SQLiteDatabase?) {
        // 테이블 생성 등 초기 설정 작업을 수행
        val create = "CREATE TABLE IF NOT EXISTS ingred (INGREDIENT TEXT PRIMARY KEY)"
        db?.execSQL(create)
    }

    // 사용할 데이터베이스 객체 반환
    fun getDatabase(): SQLiteDatabase? {
        return database
    }
}