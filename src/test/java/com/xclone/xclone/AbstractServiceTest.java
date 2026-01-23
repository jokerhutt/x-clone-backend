package com.xclone.xclone;

import com.xclone.xclone.domain.post.PostDTOMapper;
import com.xclone.xclone.domain.post.PostRepository;
import com.xclone.xclone.domain.post.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class AbstractServiceTest {

    @MockitoBean
    public BookmarkRepository bookmarkRepository;

    @MockitoBean
    public UserRepository userRepository;

    @MockitoBean
    public PostRepository postRepository;

    @MockitoBean
    public PostDTOMapper postDTOMapper;

    @MockitoBean
    public PostService postService;

    public static final PageRequest pageable = PageRequest.of(0, 10);
    public static final List<Long> ids = List.of(1L, 2L, 3L);

    @BeforeEach
    public void setUp() {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockRequest));
    }


}
