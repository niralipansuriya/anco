package com.app.ancoturf.domain.account.usecases

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.domain.BaseUseCase
import com.app.ancoturf.domain.account.AccountRepository
import com.app.ancoturf.domain.common.UseCaseConstants
import io.reactivex.Single
import javax.inject.Inject

class LogoutUseCase @Inject constructor(private val accountRepository: AccountRepository) :
    BaseUseCase<BaseResponse<Any>>() {

    override fun buildSingle(data: Map<String, Any?>?): Single<BaseResponse<Any>> {
        return accountRepository.logout(data!![UseCaseConstants.LOGOUT_TOKEN] as String)
    }
}