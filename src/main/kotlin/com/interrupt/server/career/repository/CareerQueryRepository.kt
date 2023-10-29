package com.interrupt.server.career.repository

import org.springframework.stereotype.Repository

@Repository
class CareerQueryRepository(
    private val careerRepository: CareerRepository,
) {

}