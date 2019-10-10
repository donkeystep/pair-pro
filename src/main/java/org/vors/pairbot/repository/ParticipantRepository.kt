package org.vors.pairbot.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.vors.pairbot.model.Participant
import org.vors.pairbot.model.ParticipantId
import org.vors.pairbot.model.UserInfo
import java.util.*

@Repository
interface ParticipantRepository : JpaRepository<Participant, ParticipantId> {
    @Query(value =
    """
        SELECT p FROM Participant p 
        JOIN Event e ON e = p.event
        WHERE e.date > :date
            AND p.status != org.vors.pairbot.model.ParticipantStatus.DECLINED
            AND p.user = :user
        ORDER BY e.date
    """)
    fun getParticipantsAfter(date: Date, user: UserInfo): List<Participant>
}