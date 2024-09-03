package com.interrupt.server.member.fake

import com.interrupt.server.member.application.PasswordService

class FakePasswordService : PasswordService(FakePasswordEncoder.INSTANCE)
