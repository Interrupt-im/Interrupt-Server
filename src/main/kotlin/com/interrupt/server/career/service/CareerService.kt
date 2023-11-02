package com.interrupt.server.career.service

import com.interrupt.server.career.dto.CareerDto
import com.interrupt.server.career.entity.Career
import com.interrupt.server.career.repository.CareerQueryRepository
import com.interrupt.server.career.repository.CareerRepository
import com.interrupt.server.common.exception.ErrorCode
import com.interrupt.server.common.exception.InterruptServerException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class CareerService(
    private val careerRepository: CareerRepository,
    private val careerQueryRepository: CareerQueryRepository
) {

    /**
     * 회원 별 경력 등록
     */
    fun registerMemberCareers(careerDtos: List<CareerDto>) {
        val sortedCareerDtos = getSortedCareerList(careerDtos)

        validateCareers(sortedCareerDtos)

        val careers = sortedCareerDtos.map { it.toEntity() }

        careerRepository.saveAll(careers)
    }

    /**
     * 회원 별 경력 수정
     */
    fun updateMemberCareers(memberId: Long, careerDtos: List<CareerDto>) {
        val sortedCareerDtos = getSortedCareerList(careerDtos)

        validateCareers(sortedCareerDtos)

        val careers = careerQueryRepository.findAllByMemberId(memberId)

        val (toUpdateCareers, toDeleteCareers) = divideUpdateOrDeleteCareers(careers, careerDtos)

        updateCareers(toUpdateCareers, careerDtos)

        careerRepository.saveAll(toUpdateCareers)
        careerRepository.deleteAll(toDeleteCareers)
    }

    private fun getSortedCareerList(careerDtos: List<CareerDto>): List<CareerDto> = careerDtos.sortedBy { it.careerStartDate }

    private fun divideUpdateOrDeleteCareers(careers: List<Career>, careerDtos: List<CareerDto>) =
        careers.partition { career ->
            career.id in careerDtos.map { it.id }.toSet()
        }

    private fun updateCareers(toUpdateCareers: List<Career>, careerDtos: List<CareerDto>) {
        toUpdateCareers.forEach { career ->
            val toUpdateCareerDto = careerDtos.find { it.id == career.id }
            career.update(toUpdateCareerDto?.title, toUpdateCareerDto?.careerStartDate, toUpdateCareerDto?.careerEndDate, toUpdateCareerDto?.isPublic)
        }
    }

    /*
        정렬을 시켰을 때
        1. 경력 종료일이 없는 경력은 최대 한 개만 존재해야 한다
        2. 경력 종료일이 없는 경력은 마지막 경력이어야 한다
        3. 각 경력들은 startDate 가 endDate 보다 빨라야 한다
        4. 서로 겹치는 경력이 존재해선 안된다
     */
    private fun validateCareers(careerDtos: List<CareerDto>) {
        validateOngoingCareerNumberInCareers(careerDtos)
        validateOngoingCareerIndexInCareers(careerDtos)
        validateCareerPeriodInCareers(careerDtos)
        validateNotOverlappedCareerPeriodInCareers(careerDtos)
    }

    private fun validateOngoingCareerNumberInCareers(careers: List<CareerDto>) {
        val ongoingCareerNumber = careers.filter { it.careerEndDate == null }.size

        if (ongoingCareerNumber >= 2) throw InterruptServerException(errorCode = ErrorCode.INVALID_ONGOING_CAREER_NUMBER)
    }

    private fun validateOngoingCareerIndexInCareers(careers: List<CareerDto>) {
        careers.withIndex()
            .firstOrNull { it.value.careerEndDate == null }
            ?.index
            ?.let {
                if (it >= careers.size - 1) throw InterruptServerException(errorCode = ErrorCode.INVALID_ONGOING_CAREER_INDEX)
            }
    }

    private fun validateCareerPeriodInCareers(careers: List<CareerDto>) {
        careers.forEachIndexed { index, career ->
            val isValidCareerPeriod = career.careerEndDate?.isBefore(career.careerStartDate)
                ?: if (index == careers.size - 1) true else throw InterruptServerException(errorCode = ErrorCode.INVALID_ONGOING_CAREER_INDEX)

            if (!isValidCareerPeriod) throw InterruptServerException(errorCode = ErrorCode.INVALID_CAREER_PERIOD)
        }
    }

    private fun validateNotOverlappedCareerPeriodInCareers(careers: List<CareerDto>) {
        careers.windowed(2).forEach { window ->
            val currentCareer = window[0]
            val nextCareer = window[1]

            val isValidCareerSequence = currentCareer.careerEndDate?.isBefore(nextCareer.careerStartDate) ?: throw InterruptServerException(errorCode = ErrorCode.INVALID_ONGOING_CAREER_INDEX)

            if (!isValidCareerSequence) throw InterruptServerException(errorCode = ErrorCode.OVERLAPPED_CAREER_PERIOD)
        }
    }

}