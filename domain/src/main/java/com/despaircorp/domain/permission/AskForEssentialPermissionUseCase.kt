package com.despaircorp.domain.permission

import javax.inject.Inject

class AskForEssentialPermissionUseCase @Inject constructor(
    private val permissionDomainRepository: PermissionDomainRepository
) {
    suspend fun invoke(): Boolean = permissionDomainRepository.askForEssentialsPermissions()
}