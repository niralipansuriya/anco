package com.app.ancoturf.domain.account.usecases

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.account.remote.entity.response.UserInfo
import com.app.ancoturf.domain.BaseUseCase
import com.app.ancoturf.domain.account.AccountRepository
import com.app.ancoturf.domain.common.UseCaseConstants
import io.reactivex.Single
import javax.inject.Inject

class RegisterUseCase @Inject constructor(private val accountRepository: AccountRepository) : BaseUseCase<BaseResponse<UserInfo>>() {
    override fun buildSingle(data: Map<String, Any?>?): Single<BaseResponse<UserInfo>> {
        return accountRepository.postRegister(
            email = data!!.get(UseCaseConstants.EMAIL) as String,
            password = data[UseCaseConstants.PASSWORD] as String,
            firstName = data[UseCaseConstants.FIRST_NAME] as String,
            lastName = data[UseCaseConstants.LAST_NAME] as String,
            businessName = data[UseCaseConstants.BUSINESS_NAME] as String,
            abn = data[UseCaseConstants.ABN] as String,
            phoneNumber = data[UseCaseConstants.PHONE_NUMBER] as String,
            deviceToken = data[UseCaseConstants.DEVICE_TOKEN] as String
        )
    }
}