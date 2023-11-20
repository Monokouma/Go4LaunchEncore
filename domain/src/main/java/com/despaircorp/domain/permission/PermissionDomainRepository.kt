package com.despaircorp.domain.permission

interface PermissionDomainRepository {
    suspend fun askForEssentialsPermissions(): Boolean
}