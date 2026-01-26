package com.xclone.xclone.domain.bookmark

import com.xclone.xclone.bookmark.infra.repository.BookmarkRepository
import com.xclone.xclone.helpers.JpaTestFactory
import com.xclone.xclone.utils.TestConstants
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageRequest
import java.sql.Timestamp

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JpaTestFactory::class)
class BookmarkRepositoryTest {

    @Autowired
    lateinit var bookmarkRepository: BookmarkRepository

    @Autowired
    lateinit var factory: JpaTestFactory

    @Test
    fun findPaginatedBookmarkedPostIdsByTime_ordersDescAndPaginates() {
        val author = factory.persistDefaultUser()

        val post1 = factory.persistDefaultPost(author)
        val post2 = factory.persistSecondPost(author)
        val post3 = factory.persistThirdPost(author)

        factory.persistBookmark(author.id!!, post1.id!!, TestConstants.TIME_1)
        factory.persistBookmark(author.id!!, post2.id!!, TestConstants.TIME_2)
        factory.persistBookmark(author.id!!, post3.id!!, TestConstants.TIME_3)

        val cursor = Timestamp.from(TestConstants.CURSOR_TIME)
        val page = PageRequest.of(0, 2)

        val result = bookmarkRepository.findPaginatedBookmarkedPostIdsByTime(
            author.id!!,
            cursor,
            page
        )

        assertThat(result).containsExactly(post3.id, post2.id)
    }
}