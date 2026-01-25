package com.xclone.xclone
import com.xclone.xclone.domain.bookmark.infra.repository.BookmarkRepository
import com.xclone.xclone.domain.post.PostRepository
import com.xclone.xclone.domain.post.PostService
import com.xclone.xclone.domain.user.UserRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

@SpringBootTest
@ExtendWith(SpringExtension::class)
@ActiveProfiles("test")
open class AbstractServiceTest {

    @MockitoBean
    lateinit var bookmarkRepository: BookmarkRepository

    @MockitoBean
    lateinit var userRepository: UserRepository

    @MockitoBean
    lateinit var postRepository: PostRepository

    @MockitoBean
    lateinit var postService: PostService

    companion object {
        val pageable: PageRequest = PageRequest.of(0, 10)
        val ids: List<Long> = listOf(1L, 2L, 3L)
    }

    @BeforeEach
    fun setUp() {
        val mockRequest = MockHttpServletRequest()
        RequestContextHolder.setRequestAttributes(ServletRequestAttributes(mockRequest))
    }
}