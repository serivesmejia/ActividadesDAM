package org.deltacv.actividad2

import android.content.Context
import androidx.room.*

@Entity(tableName = "comments")
data class Comment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "article_id") val articleId: Int,
    val author: String,
    val content: String,
)

@Dao
interface CommentDao {
    @Query("SELECT * FROM comments WHERE article_id = :articleId ORDER BY id ASC")
    fun getCommentsForArticle(articleId: Int): List<Comment>

    @Insert
    fun insert(comment: Comment): Long
}

@Database(entities = [Comment::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun commentDao(): CommentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "news_comments_room.db"
                )
                .addCallback(DatabaseCallback())
                .allowMainThreadQueries()
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                super.onCreate(db)
                // Insertar comentarios por defecto
                db.execSQL("INSERT INTO comments (article_id, author, content) VALUES (1, 'Carlos López', '¡Excelente noticia! Muy informativa.')")
                db.execSQL("INSERT INTO comments (article_id, author, content) VALUES (1, 'María García', 'Gracias por compartir esta actualización.')")
                db.execSQL("INSERT INTO comments (article_id, author, content) VALUES (2, 'Juan Pérez', 'Gran iniciativa para el cuidado de nuestro medio ambiente.')")
                db.execSQL("INSERT INTO comments (article_id, author, content) VALUES (3, 'Ana Martínez', '¡Increíble la valentía de ese operador!')")
            }
        }
    }
}
