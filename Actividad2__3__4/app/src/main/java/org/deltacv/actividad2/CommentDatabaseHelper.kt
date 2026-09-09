package org.deltacv.actividad2

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

data class Comment(
    val id: Long = 0,
    val articleId: Int,
    val author: String,
    val content: String,
)

class CommentDatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION,
) {

    companion object {
        private const val DATABASE_NAME = "news_comments.db"
        private const val DATABASE_VERSION = 2

        private const val TABLE_COMMENTS = "comments"
        private const val COLUMN_ID = "id"
        private const val COLUMN_ARTICLE_ID = "article_id"
        private const val COLUMN_AUTHOR = "author"
        private const val COLUMN_CONTENT = "content"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_COMMENTS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_ARTICLE_ID INTEGER NOT NULL,
                $COLUMN_AUTHOR TEXT NOT NULL,
                $COLUMN_CONTENT TEXT NOT NULL
            )
        """.trimIndent()
        db.execSQL(createTableQuery)

        insertDefaultComments(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_COMMENTS")
        onCreate(db)
    }

    private fun insertDefaultComments(db: SQLiteDatabase) {
        insertDefaultComment(db, 1, "Carlos López", "¡Excelente noticia! Muy informativa.")
        insertDefaultComment(db, 1, "María García", "Gracias por compartir esta actualización.")
        insertDefaultComment(db, 2, "Juan Pérez", "Gran iniciativa para el cuidado de nuestro medio ambiente.")
        insertDefaultComment(db, 3, "Ana Martínez", "¡Increíble la valentía de ese operador!")
    }

    private fun insertDefaultComment(
        db: SQLiteDatabase,
        articleId: Int,
        author: String,
        content: String,
    ) {
        val values = ContentValues().apply {
            put(COLUMN_ARTICLE_ID, articleId)
            put(COLUMN_AUTHOR, author)
            put(COLUMN_CONTENT, content)
        }
        db.insert(TABLE_COMMENTS, null, values)
    }

    fun addComment(articleId: Int, author: String, content: String): Comment {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ARTICLE_ID, articleId)
            put(COLUMN_AUTHOR, author)
            put(COLUMN_CONTENT, content)
        }
        val newId = db.insert(TABLE_COMMENTS, null, values)
        return Comment(id = newId, articleId = articleId, author = author, content = content)
    }

    fun getCommentsForArticle(articleId: Int): List<Comment> {
        val commentList = mutableListOf<Comment>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_COMMENTS,
            arrayOf(COLUMN_ID, COLUMN_ARTICLE_ID, COLUMN_AUTHOR, COLUMN_CONTENT),
            "$COLUMN_ARTICLE_ID = ?",
            arrayOf(articleId.toString()),
            null,
            null,
            "$COLUMN_ID ASC",
        )

        cursor.use { c ->
            val idIndex = c.getColumnIndexOrThrow(COLUMN_ID)
            val articleIdIndex = c.getColumnIndexOrThrow(COLUMN_ARTICLE_ID)
            val authorIndex = c.getColumnIndexOrThrow(COLUMN_AUTHOR)
            val contentIndex = c.getColumnIndexOrThrow(COLUMN_CONTENT)

            while (c.moveToNext()) {
                val id = c.getLong(idIndex)
                val artId = c.getInt(articleIdIndex)
                val author = c.getString(authorIndex)
                val content = c.getString(contentIndex)
                commentList.add(Comment(id, artId, author, content))
            }
        }

        return commentList
    }
}
