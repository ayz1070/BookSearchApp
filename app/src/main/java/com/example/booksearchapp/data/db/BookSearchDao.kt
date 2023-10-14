package com.example.booksearchapp.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.booksearchapp.data.model.Book
import kotlinx.coroutines.flow.Flow


// 체크 포인트 : @Dao, suspend fun
@Dao
interface BookSearchDao {
    // Insert와 Delete는 시간이 걸리는 연산이기 때문에 suspend 키워드를 활용하여 코루틴 활용
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: Book)

    @Delete
    suspend fun deleteBook(book: Book)


    @Query("SELECT * FROM books")
    fun getFavoriteBooks(): Flow<List<Book>>

}