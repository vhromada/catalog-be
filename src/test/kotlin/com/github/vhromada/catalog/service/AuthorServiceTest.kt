package com.github.vhromada.catalog.service

import com.github.vhromada.catalog.domain.Author
import com.github.vhromada.catalog.domain.filter.AuthorFilter
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.provider.UuidProvider
import com.github.vhromada.catalog.repository.AuthorRepository
import com.github.vhromada.catalog.service.impl.AuthorServiceImpl
import com.github.vhromada.catalog.utils.AuthorUtils
import com.github.vhromada.catalog.utils.TestConstants
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.http.HttpStatus
import java.util.Optional

/**
 * A class represents test for class [AuthorService].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(MockitoExtension::class)
class AuthorServiceTest {

    /**
     * Instance of [AuthorRepository]
     */
    @Mock
    private lateinit var repository: AuthorRepository

    /**
     * Instance of [UuidProvider]
     */
    @Mock
    private lateinit var uuidProvider: UuidProvider

    /**
     * Instance of [AuthorService]
     */
    private lateinit var service: AuthorService

    /**
     * Initializes service.
     */
    @BeforeEach
    fun setUp() {
        service = AuthorServiceImpl(repository = repository, uuidProvider = uuidProvider)
    }

    /**
     * Test method for [AuthorService.search].
     */
    @Test
    fun search() {
        val pageable = Pageable.ofSize(1)
        val page = PageImpl(listOf(AuthorUtils.getDomainAuthor(index = 1), AuthorUtils.getDomainAuthor(index = 2)))
        whenever(repository.findAll(any<Specification<Author>>(), any<Pageable>())).thenReturn(page)

        val result = service.search(filter = AuthorFilter(firstName = "first", middleName = "middle", lastName = "last"), pageable = pageable)

        assertThat(result).isEqualTo(page)
        verify(repository).findAll(any<Specification<Author>>(), eq(pageable))
        verifyNoMoreInteractions(repository)
        verifyNoInteractions(uuidProvider)
    }

    /**
     * Test method for [AuthorService.search] with empty filter.
     */
    @Test
    fun searchEmptyFilter() {
        val pageable = Pageable.ofSize(1)
        val page = PageImpl(listOf(AuthorUtils.getDomainAuthor(index = 1), AuthorUtils.getDomainAuthor(index = 2)))
        whenever(repository.findAll(any<Pageable>())).thenReturn(page)

        val result = service.search(filter = AuthorFilter(), pageable = pageable)

        assertThat(result).isEqualTo(page)
        verify(repository).findAll(pageable)
        verifyNoMoreInteractions(repository)
        verifyNoInteractions(uuidProvider)
    }

    /**
     * Test method for [AuthorService.get] with existing author.
     */
    @Test
    fun getExisting() {
        val author = AuthorUtils.getDomainAuthor(index = 1)
        whenever(repository.findByUuid(uuid = any())).thenReturn(Optional.of(author))

        val result = service.get(uuid = author.uuid)

        assertThat(result).isEqualTo(author)
        verify(repository).findByUuid(uuid = author.uuid)
        verifyNoMoreInteractions(repository)
        verifyNoInteractions(uuidProvider)
    }

    /**
     * Test method for [AuthorService.get] with not existing author.
     */
    @Test
    fun getNotExisting() {
        whenever(repository.findByUuid(uuid = any())).thenReturn(Optional.empty())

        assertThatThrownBy { service.get(uuid = TestConstants.UUID) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("AUTHOR_NOT_EXIST")
            .hasMessageContaining("Author doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        verify(repository).findByUuid(uuid = TestConstants.UUID)
        verifyNoMoreInteractions(repository)
        verifyNoInteractions(uuidProvider)
    }

    /**
     * Test method for [AuthorService.store].
     */
    @Test
    fun store() {
        val author = AuthorUtils.getDomainAuthor(index = 1)
        whenever(repository.save(anyDomain())).thenAnswer { it.arguments[0] }

        val result = service.store(author = author)

        assertThat(result).isSameAs(author)
        verify(repository).save(author)
        verifyNoMoreInteractions(repository)
        verifyNoInteractions(uuidProvider)
    }

    /**
     * Test method for [AuthorService.remove].
     */
    @Test
    fun remove() {
        val author = AuthorUtils.getDomainAuthor(index = 1)

        service.remove(author = author)

        verify(repository).delete(author)
        verifyNoMoreInteractions(repository)
        verifyNoInteractions(uuidProvider)
    }

    /**
     * Test method for [AuthorService.duplicate].
     */
    @Test
    fun duplicate() {
        val expectedAuthor = AuthorUtils.getDomainAuthor(index = 1)
            .copy(id = 0, uuid = TestConstants.UUID)
        val copyArgumentCaptor = argumentCaptor<Author>()
        whenever(repository.save(anyDomain())).thenAnswer {
            val argument = it.arguments[0] as Author
            argument.id = 0
            argument
        }
        whenever(uuidProvider.getUuid()).thenReturn(TestConstants.UUID)

        val result = service.duplicate(author = AuthorUtils.getDomainAuthor(index = 1))

        AuthorUtils.assertAuthorDeepEquals(expected = expectedAuthor, actual = result)
        verify(repository).save(copyArgumentCaptor.capture())
        verify(uuidProvider).getUuid()
        verifyNoMoreInteractions(repository, uuidProvider)
        assertThat(result).isSameAs(copyArgumentCaptor.lastValue)
    }

    /**
     * Test method for [AuthorService.getCount].
     */
    @Test
    fun getCount() {
        whenever(repository.count()).thenReturn(AuthorUtils.AUTHORS_COUNT.toLong())

        val result = service.getCount()

        assertThat(result).isEqualTo(AuthorUtils.AUTHORS_COUNT.toLong())
        verify(repository).count()
        verifyNoMoreInteractions(repository)
        verifyNoInteractions(uuidProvider)
    }

    /**
     * Returns any mock for domain author.
     *
     * @return any mock for domain author
     */
    private fun anyDomain(): Author {
        return any()
    }

}
