package org.vors.pairbot.repository

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import org.vors.pairbot.PairbotApplication
import org.vors.pairbot.model.Event
import org.vors.pairbot.model.UserInfo
import org.vors.pairbot.service.TimeService

import java.util.Date

import org.junit.Assert.*
import javax.persistence.EntityManager
import javax.transaction.Transactional

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [PairbotApplication::class])
open class EventRepositoryTest {

    @Autowired
    lateinit var systemUnderTest: EventRepository
    @Autowired
    lateinit var userRepository: UserRepository
    @Autowired
    lateinit var entityManager: EntityManager

    private val timeService = TimeService()
    private val date = Date()
    private lateinit var event: Event
    private lateinit var user: UserInfo

    @Before
    fun setup() {
        user = UserInfo(0, "Vasya")
        val partner = UserInfo(1, "Petya")
        userRepository.saveAll(listOf(user, partner))

        event = Event(user, partner, true, Date())
    }

    @Test
    @Transactional
    open fun givenEventBeforeGivenDate_whenExistsByDate_thenFalse() {
        //given
        saveEventWithDateShift(-1)

        //then
        assertFalse(systemUnderTest.existsByDateAfterAndParticipants_User(date, user))
    }

    @Test
    @Transactional
    open fun givenEventAfterGivenDate_whenExistsByDate_thenTrue() {
        //given
        saveEventWithDateShift(1)

        //then
        assertTrue(systemUnderTest.existsByDateAfterAndParticipants_User(date, user))
    }

    private fun saveEventWithDateShift(hours: Int) {
        event.date = timeService.datePlusHours(date, hours)
        entityManager.merge(event.creator)
        entityManager.merge(event.partner)
        systemUnderTest.save(event)
    }

}