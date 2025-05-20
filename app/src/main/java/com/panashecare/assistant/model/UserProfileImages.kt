package com.panashecare.assistant.model

import com.panashecare.assistant.R
import com.panashecare.assistant.model.objects.Gender

class UserProfileImages {
    private val maleImages = listOf(
        R.drawable.carer_man,
        R.drawable.carer_man_2,
        R.drawable.man_carer_3,
        R.drawable.man_carer_4
    )

    private val femaleImages = listOf(
        R.drawable.woman_patient,
        R.drawable.woman_2,
        R.drawable.woman_3,
        R.drawable.woman_4,
        R.drawable.woman_5
    )

    fun getRandomImageForGender(gender: Gender): Int {
        return when (gender) {
            Gender.MALE -> maleImages.random()
            Gender.FEMALE -> femaleImages.random()
            else -> (maleImages + femaleImages).random()
        }
    }
}

