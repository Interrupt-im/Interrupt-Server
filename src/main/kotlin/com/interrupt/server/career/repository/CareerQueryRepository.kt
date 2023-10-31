package com.interrupt.server.career.repository

import com.interrupt.server.career.entity.Career
import com.interrupt.server.job.entity.Job
import com.interrupt.server.job.entity.JobGroup
import com.interrupt.server.job.repository.JobRepository
import com.interrupt.server.member.entity.Member
import com.interrupt.server.job.entity.MemberJob
import com.interrupt.server.skill.entity.MemberSkill
import com.interrupt.server.job.repository.MemberJobRepository
import com.interrupt.server.member.repository.MemberRepository
import com.interrupt.server.member.repository.MemberSkillRepository
import com.interrupt.server.skill.entity.Skill
import com.interrupt.server.skill.entity.SkillGroup
import com.interrupt.server.skill.repository.SkillRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class CareerQueryRepository(
    private val careerRepository: CareerRepository,
    private val memberRepository: MemberRepository,
    private val memberJobRepository: MemberJobRepository,
    private val memberSkillRepository: MemberSkillRepository,
    private val skillRepository: SkillRepository,
    private val jobRepository: JobRepository
) {

    data class TestDTO(
        val name: String,
        val year: Long
    )

    fun foo(): Any {

        /*
            select distinct m.name
            from member as m
            join member_job as mj on m.id = mj.member_id
            join job as j on mj.job_id = j.id
            join job_group as jg on j.job_group_id = jg.id
            join member_skill as ms on m.id = ms.member_id
            join skill as s on ms.skill_id = s.id
            join skill_group as sg on s.skill_group_id = sg.id
            left join career as c on c.member_id = m.id
            where jg.id = 1 and sg.id in (1, 2)
            group by m.id;
        */
        return careerRepository.findAll {
            select(
//                entity(Member::class)
                new(
                    TestDTO::class,
                    path(Career::member)(Member::name),
                    sum(
                        caseWhen(path(Career::careerEndDate).isNotNull()).then(
                            customExpression(
                                Int::class,
                                "DATEDIFF({0}, {1})",
                                (path(Career::careerEndDate)),
                                path(Career::careerStartDate)
                            )
                        ).`else`(
                            customExpression(
                                Int::class,
                                "DATEDIFF(CURRENT_DATE, {0})",
                                path(Career::careerStartDate)
                            )
                        )
                    )
                )

            ).from(
//                entity(Career::class),
//                fetchJoin(Member::class).on(path(Member::id).equal(path(Career::member)(Member::id)))

                entity(Member::class),
                fetchJoin(entity(MemberJob::class)).on(path(Member::id).equal(path(MemberJob::member)(Member::id))),
//                fetchJoin(Job::class).on(path(Job::id).equal(path(MemberJob::job)(Job::id))),
//                fetchJoin(JobGroup::class).on(path(JobGroup::id).equal(path(Job::jobGroup)(JobGroup::id))),
//                fetchJoin(entity(MemberSkill::class)).on(path(Member::id).equal(path(MemberSkill::member)(Member::id))),
//                fetchJoin(Skill::class).on(path(Skill::id).equal(path(MemberSkill::skill)(Skill::id))),
//                fetchJoin(SkillGroup::class).on(path(SkillGroup::id).equal(path(Skill::skillGroup)(SkillGroup::id))),
                join(Career::class).on(path(Member::id).equal(path(Career::member)(Member::id)))
            )
                .groupBy(
                path(Member::id)
            )
                .having(
                sum(
                    caseWhen(path(Career::careerEndDate).isNotNull()).then(
                        customExpression(
                            Int::class,
                            "DATEDIFF({0}, {1})",
                            (path(Career::careerEndDate)),
                            path(Career::careerStartDate)
                        )
                    ).`else`(
                        customExpression(
                            Int::class,
                            "DATEDIFF(CURRENT_DATE, {0})",
                            path(Career::careerStartDate)
                        )
                    )
                ).div(365).between(1L, 2L)
            )
                .orderBy(
                path(Career::member)(Member::id).asc()
            )
        }.let {
            println("success")
//            it.forEach { println("${it?.id}: ${it?.title}, ${it?.member?.name}") }

            return@let it
        }
    }

    fun save() {
        createJS()
        createSM()
        createTW()
    }

    /**
     * 경력(현재 경력 제외) - 364 / 1년차
     * 경력(현재 경력 포함) - 364 + 303 = 667 / 2년차
     */
    private fun createJS() {
        val job = jobRepository.findByIdOrNull(2)
        val skills = skillRepository.findAllById(listOf(1, 2, 9))

        memberRepository.findByLoginId("준서")?.let { memberRepository.delete(it) }
        val member = memberRepository.save(Member("준서", "test1234", "준서", "domain@email.com"))

        memberJobRepository.save(MemberJob(member, job!!))

        val memberSkills = skills.filterNotNull().map { MemberSkill(member, it) }
        memberSkillRepository.saveAll(memberSkills)

        val career1 = Career(member, "유앤에스네트웍스", careerStartDate = LocalDate.of(2021, 12, 1), careerEndDate = LocalDate.of(2022, 11, 30))
        val career2 = Career(member, "라인", careerStartDate = LocalDate.of(2023, 1, 1))

        careerRepository.saveAll(listOf(career1, career2))
    }

    /**
     * 경력(현재 경력 제외) - 0 / 0년차
     * 경력(현재 경력 포함) - 627 / 2년차
     */
    private fun createSM() {
        val jobs = jobRepository.findAllById(listOf(1, 4))
        val skills = skillRepository.findAllById(listOf(3, 4))

        memberRepository.findByLoginId("승민")?.let { memberRepository.delete(it) }
        val member = memberRepository.save(Member("승민", "test1234", "승민", "domain@email.com"))

        val memberJobs = jobs.filterNotNull().map { MemberJob(member, it) }
        memberJobRepository.saveAll(memberJobs)

        val memberSkills = skills.filterNotNull().map { MemberSkill(member, it) }
        memberSkillRepository.saveAll(memberSkills)

        val career1 = Career(member, "가이아", careerStartDate = LocalDate.of(2022, 2, 11))

        careerRepository.saveAll(listOf(career1))
    }

    /**
     * 경력(현재 경력 제외) - 2344 / 7년차
     * 경력(현재 경력 포함) - 2344 + 30 = 2374 / 7년차
     */
    private fun createTW() {
        val jobs = jobRepository.findAllById(listOf(3, 5))
        val skills = skillRepository.findAllById(listOf(3, 6))

        memberRepository.findByLoginId("태완")?.let { memberRepository.delete(it) }
        val member = memberRepository.save(Member("태완", "test1234", "태완", "domain@email.com"))

        val memberJobs = jobs.filterNotNull().map { MemberJob(member, it) }
        memberJobRepository.saveAll(memberJobs)

        val memberSkills = skills.filterNotNull().map { MemberSkill(member, it) }

        memberSkillRepository.saveAll(memberSkills)

        val career1 = Career(member, "510 포대 통신반", careerStartDate = LocalDate.of(2017, 5, 15), careerEndDate = LocalDate.of(2019, 4, 30))
        val career2 = Career(member, "동아대 컴공과", careerStartDate = LocalDate.of(2019, 5, 1), careerEndDate = LocalDate.of(2021, 1, 15))
        val career3 = Career(member, "방구석 개발자", careerStartDate = LocalDate.of(2021, 3, 1), careerEndDate = LocalDate.of(2023, 9, 1))
        val career4 = Career(member, "이마트", careerStartDate = LocalDate.of(2023, 10, 1))

        careerRepository.saveAll(listOf(career1, career2, career3, career4))
    }

}