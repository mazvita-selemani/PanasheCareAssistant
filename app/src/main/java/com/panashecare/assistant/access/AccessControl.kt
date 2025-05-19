package com.panashecare.assistant.access

import androidx.compose.runtime.Composable
import com.panashecare.assistant.model.objects.User

enum class Permission {
    ViewAllShifts,
    CreateShifts,
    CancelShifts,
    UpdateShifts,
    CreatePrescriptionSchedule,
    UpdatePrescriptionSchedule,
    ViewInventory,
    UpdateInventory,
    EditPatientDetails
}


enum class UserType { ADMIN, CARER}

val rolePermissions = mapOf(
    UserType.ADMIN to setOf(
        Permission.ViewAllShifts,
        Permission.CreateShifts,
        Permission.CancelShifts,
        Permission.UpdateShifts,
        Permission.CreatePrescriptionSchedule,
        Permission.UpdatePrescriptionSchedule,
        Permission.ViewInventory,
        Permission.UpdateInventory,
        Permission.EditPatientDetails
    ),
    UserType.CARER to setOf(
        Permission.ViewAllShifts
    )
)

object AccessControl {
    fun isAuthorized(user: User?, permission: Permission): Boolean {
        return user?.userType?.let { rolePermissions[it]?.contains(permission) } ?: false
    }

    @Composable
    fun WithPermission(
        user: User?,
        permission: Permission,
        onAuthorized: @Composable () -> Unit,
        onDenied: @Composable () -> Unit = {}
    ) {
        if (isAuthorized(user, permission)) {
            onAuthorized()
        } else {
            onDenied()
        }
    }
}
