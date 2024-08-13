package com.interrupt.server.member.infra

import com.interrupt.server.member.application.MemberQueryRepository
import com.interrupt.server.member.domain.Member
import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import com.linecorp.kotlinjdsl.render.RenderContext
import com.linecorp.kotlinjdsl.support.spring.data.jpa.extension.createQuery
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository

@Repository
class MemberQueryJdslRepository(
    private val memberJpaRepository : MemberJpaRepository,
    private val entityManager: EntityManager,
    private val jpqlRenderContext: RenderContext,
) : MemberQueryRepository {
    override fun existsByEmailAndNotDeleted(email: String?): Boolean {
        val query = jpql {
            select(
                entity(Member::class)
            ).from(
                entity(Member::class)
            ).where(
                path(Member::deletedAt).isNull()
                    .and(
                        path(Member::email).eq(email)
                    )
            )
        }

        return entityManager
            .createQuery(query, jpqlRenderContext)
            .apply { setMaxResults(1) }
            .resultList.isNotEmpty()
    }
}
