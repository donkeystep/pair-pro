package org.vors.pairbot.event.generator



import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.vors.pairbot.constant.BotConstants.MIN_DAYS_BETWEEN_SESSIONS
import org.vors.pairbot.model.Event
import org.vors.pairbot.model.Team
import org.vors.pairbot.model.UserInfo
import org.vors.pairbot.repository.EventRepository
import org.vors.pairbot.repository.ParticipantRepository
import org.vors.pairbot.repository.UserRepository
import org.vors.pairbot.service.TimeService
import java.util.*
import java.util.concurrent.ThreadLocalRandom
@Component
class PairGenerator(
        private val timeService: TimeService,
        private val userRepository: UserRepository
) {

    private val LOG = LoggerFactory.getLogger(javaClass)

    fun findPair(user: UserInfo, team: Team): Event? {
        val sessionDate = timeService.chooseSessionDate()
        return findPair(user, team, sessionDate)
    }

    fun findPair(user: UserInfo, team: Team, sessionDate: Date): Event? {
        val others = findAvailablePeers(user, team, sessionDate)

        if (others.isEmpty()) {
            LOG.debug("Pair not found, no available peers")
            return null
        }
        val event = pair(user, others, sessionDate)

        return null
    }

    private fun findAvailablePeers(user: UserInfo, team: Team, date: Date): List<UserInfo> {
        val dateThreshold = timeService.beginningOfDateMinusDaysFrom(date, MIN_DAYS_BETWEEN_SESSIONS)

        return userRepository.findByNoEventsAfter(dateThreshold, user, team)
    }

    private fun pair(first: UserInfo, others: List<UserInfo>, sessionDate: Date): Event {

        val random = ThreadLocalRandom.current()
        val pairIndex = random.nextInt(others.size)
        val second = others[pairIndex]

        val event = Event(
                first,
                second,
                ThreadLocalRandom.current().nextBoolean(),
                sessionDate
        )

        event.addParticipant(first)
        event.addParticipant(second)

        return event
    }

}
