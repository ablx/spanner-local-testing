package dev.verbosemode.spannerlocaltesting.persistence

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("noemulator")
internal class WithoutEmulatorFailingTest {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun `should fail without emulator`() {
        // given
        val entity = UserEntity("id", "name", "email")

        // when
        val actual = userRepository.save(entity)
        val read = userRepository.findById(entity.id)

        // then
        assert(actual == entity)
        assert(actual == read.get())
    }
}